package com.getsmartkart.smartkart;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;

public class ConfirmCheckoutFragment extends DialogFragment {

    private Button cancelButton;
    private Button checkoutButton;


    public ConfirmCheckoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_confirm_checkout, container, false);

        TextView itemNameAmount = (TextView) rootView.findViewById(R.id.final_cost);
        double finalCost = getArguments().getDouble("cost", 25.00);
        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.CEILING);
        itemNameAmount.setText("$" + df.format(finalCost));


        cancelButton = (Button) rootView.findViewById(R.id.cancel_checkout);
        checkoutButton = (Button) rootView.findViewById(R.id.confirm_checkout);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveShoppingCartActivity ref = (ActiveShoppingCartActivity) getActivity();
                ref.closePopup();
                dismiss();
            }
        });

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: All the call backs

                final ActiveShoppingCartActivity ref = (ActiveShoppingCartActivity) getActivity();

                AsyncHttpClient client = new AsyncHttpClient();
                client.delete("http://ec2-52-86-213-15.compute-1.amazonaws.com/api/v1/cartItems/", new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        // Initiated the request

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // Successfully got a response
                        try {
                            JSONArray jsonData = new JSONArray(new String(responseBody));
                            ref.buildNewListOfData(jsonData);
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

                ref.closePopup();
                dismiss();
            }
        });

        return rootView;
    }

}
