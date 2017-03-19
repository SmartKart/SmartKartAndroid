package com.getsmartkart.smartkart;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
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

import com.getsmartkart.smartkart.dummy.DummyContent;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.List;

public class ActiveShoppingCartActivity extends Activity
        implements ItemListFragment.OnListFragmentInteractionListener {

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    private List<ShoppingCartItem> listOfData;

    private boolean isPopupActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_shopping_cart);
        PermsUtil.getPermissions(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar ab = getActionBar();
        ab.setTitle("My Smart Kart");

        Fragment listOfItems = new ItemListFragment();
        Fragment checkoutBar = new CheckoutBarFragment();
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
                        Bundle barcodeBundle = new Bundle();
                        Vibrator v = (Vibrator) getApplicationContext().getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                        v.vibrate(300);
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.boop);
                        mp.start();
                        barcodeBundle.putString("barcode", barcodes.valueAt(0).displayValue);
                        FragmentManager fm = getFragmentManager();
                        VerifyItemAmountFragment verifyFragment = new VerifyItemAmountFragment();
                        verifyFragment.setArguments(barcodeBundle);
                        verifyFragment.show(fm, "Verify Amount");
                        isPopupActive = true;
                    }
                    else{
                        Log.v("Popup Already Active", "Active");
                    }
                }
            }
        });

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
            // something
    }

    public void closePopup(){
        isPopupActive = false;
    }
    public void openPopup(){
        isPopupActive = true;
    }
    public boolean getPopupStatus() {return isPopupActive; }

}
