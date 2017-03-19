package com.getsmartkart.smartkart;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ActiveShoppingCartActivity extends Activity
        implements ItemListFragment.OnListFragmentInteractionListener {

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    private ItemListFragment listOfItems;
    private CheckoutBarFragment checkoutBar;

    public List<ShoppingCartItem> listOfData = new ArrayList<>();

    private boolean isPopupActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_shopping_cart);
        PermsUtil.getPermissions(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar ab = getActionBar();
        ab.setTitle("My Smart Kart");

        listOfItems = new ItemListFragment();
        checkoutBar = new CheckoutBarFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();


        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.list_of_items, listOfItems);
        transaction.replace(R.id.checkout_bar, checkoutBar);


        transaction.commit();

        barcodeDetector =
                new BarcodeDetector.Builder(getApplicationContext()).build();

        cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true)
                .build();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.v("Perm Error", "Need Camera Perms");
        }
        try {
            cameraSource.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    Log.v("Incoming Barcode", barcodes.valueAt(0).displayValue);
                    if(!isPopupActive){
                        String nameOfItem = "";
                        final BigInteger id = new BigInteger(barcodes.valueAt(0).displayValue);
                        SyncHttpClient client = new SyncHttpClient ();
                        client.get("http://ec2-52-86-213-15.compute-1.amazonaws.com/api/v1/store/" + id, null, new AsyncHttpResponseHandler() {
                            @Override
                            public void onStart() {
                                // Initiated the request
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                // Successfully got a response
                                Bundle barcodeBundle = new Bundle();
                                Vibrator v = (Vibrator) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                                v.vibrate(300);
                                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.boop);
                                mp.start();
                                try {
                                    JSONArray jsonData = new JSONArray(new String(responseBody));
                                    JSONObject itemObj = jsonData.getJSONObject(0);
                                    barcodeBundle.putString("name", itemObj.getString("name"));
                                    barcodeBundle.putString("id", id+"");
                                    barcodeBundle.putString("price", itemObj.getDouble("price") + "");
                                    barcodeBundle.putString("percentOff", itemObj.getInt("percentOff") + "");
                                    barcodeBundle.putString("isOnSale", itemObj.getBoolean("isOnSale") + "");
                                    barcodeBundle.putString("type", itemObj.getString("type"));
                                    barcodeBundle.putString("image", itemObj.getString("image"));
                                } catch (JSONException e) {
                                    //e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Item not in database" , Toast.LENGTH_LONG).show();
                                    return;
                                }
                                FragmentManager fm = getFragmentManager();
                                VerifyItemAmountFragment verifyFragment = new VerifyItemAmountFragment();
                                verifyFragment.setArguments(barcodeBundle);
                                verifyFragment.show(fm, "Verify Amount");
                                isPopupActive = true;
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                                    error)
                            {
                                // Response failed :(
                            }
                        });
                    }
                    else{
                        Log.v("Popup Already Active", "Active");
                    }
                }
            }
        });

    }

    @Override
    public void onListFragmentInteraction(ShoppingCartItem item) {
            // something
    }

    public void closePopup(){
        isPopupActive = false;
    }
    public void openPopup(){
        isPopupActive = true;
    }
    public boolean getPopupStatus() {return isPopupActive; }

    public void buildNewListOfData(JSONArray newData){
        List<ShoppingCartItem> newListOfData = new ArrayList<>();
        double cost = 0.0;
        for(int i = 0; i < newData.length(); i++){
            Gson gson = new GsonBuilder().create();
            try {
                ShoppingCartItem item = gson.fromJson(newData.getJSONObject(i).toString(), ShoppingCartItem.class);
                newListOfData.add(item);
                if(item.isOnSale()){
                    cost += ((item.getPrice() * (1-(item.getPercentOff()/100.0))) * item.getQuantity());
                }
                else{
                    cost += (item.getPrice() * item.getQuantity());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        checkoutBar.updateTotalPrice(cost);

        listOfItems.adapter.swap(newListOfData);

    }

}
