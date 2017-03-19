package com.getsmartkart.smartkart;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Nathan on 2017-03-18.
 */

public class VerifyItemAmountFragment extends DialogFragment {

    private NumberPicker amountPicker;
    private Button cancelButton;
    private Button addToCartButton;
    private String barcodeID;
    private String name;
    private String price;
    private String percentOff;
    private String isOnSale;
    private String type;
    private String image;
    protected ActiveShoppingCartActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.verify_item_amount, container, false);

        TextView barcode = (TextView) rootView.findViewById(R.id.barcodeValue);
        amountPicker = (NumberPicker) rootView.findViewById(R.id.amountPicker);
        name = getArguments().getString("name");
        barcode.setText("Add " + name + " to cart");
        amountPicker.setMinValue(1);
        amountPicker.setMaxValue(100);
        amountPicker.setWrapSelectorWheel(false);
        barcodeID = getArguments().getString("id");
        price = getArguments().getString("price");
        percentOff = getArguments().getString("percentOff");
        isOnSale = getArguments().getString("isOnSale");
        type = getArguments().getString("type");
        image = getArguments().getString("image");


        cancelButton = (Button) rootView.findViewById(R.id.cancel_add_item);
        addToCartButton = (Button) rootView.findViewById(R.id.add_item);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveShoppingCartActivity ref = (ActiveShoppingCartActivity) getActivity();
                ref.closePopup();
                dismiss();
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = amountPicker.getValue();
                //cart=1&barcode=1&name=beggs&price=5.99&quantity=6&percentOff=4&isOnSale=true&type=essentials&image=lol

                //String addon = "id=1&" + "barcode=" + barcodeID + "&name=" + name + "&price=" + price + "&quantity=" + amount + "&percentOff=" + percentOff + "&isOnSale=" + isOnSale + "&type=" + type + "&image=" + image;

                RequestParams params = new RequestParams();
                params.put("id", barcodeID);
                params.put("cart", "1");
                params.put("name", name);
                params.put("price", price);
                params.put("quantity", amount);
                params.put("percentOff", percentOff);
                params.put("isOnSale", isOnSale);
                params.put("type", type);
                params.put("image", image);


                AsyncHttpClient client = new AsyncHttpClient();
                client.post("http://ec2-52-86-213-15.compute-1.amazonaws.com/api/v1/cartItems/", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        // Initiated the request

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // Successfully got a response
                        try {
                            JSONArray jsonData = new JSONArray(new String(responseBody));
                            mActivity.buildNewListOfData(jsonData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                            error)
                    {
                        // Response failed :(
                        Log.v("Request failed", error.getLocalizedMessage());
                    }
                });

                ActiveShoppingCartActivity ref = (ActiveShoppingCartActivity) getActivity();
                ref.closePopup();
                dismiss();
            }
        });



        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActiveShoppingCartActivity) activity;
    }

}
