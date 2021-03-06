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
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class EditItemAmountFragment extends DialogFragment {

    private NumberPicker amountPicker;
    private Button cancelButton;
    private Button editCartButton;

    public EditItemAmountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_item_amount, container, false);

        TextView itemNameAmount = (TextView) rootView.findViewById(R.id.Item_Name_Edit_Amount);
        amountPicker = (NumberPicker) rootView.findViewById(R.id.edit_amount_picker);
        itemNameAmount.setText("Amount of " + getArguments().getString("name"));

        amountPicker.setMinValue(1);
        amountPicker.setMaxValue(100);
        amountPicker.setWrapSelectorWheel(false);
        amountPicker.setValue(getArguments().getInt("number"));

        cancelButton = (Button) rootView.findViewById(R.id.cancel_edit_item);
        editCartButton = (Button) rootView.findViewById(R.id.edit_item);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveShoppingCartActivity ref = (ActiveShoppingCartActivity) getActivity();
                ref.closePopup();
                dismiss();
            }
        });

        editCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ActiveShoppingCartActivity ref = (ActiveShoppingCartActivity) getActivity();

                RequestParams params = new RequestParams();
                params.put("cart", "1");
                params.put("name", getArguments().getString("name"));
                params.put("price", getArguments().getDouble("price"));
                params.put("quantity", amountPicker.getValue());
                params.put("percentOff", getArguments().getInt("percentOff"));
                params.put("isOnSale", getArguments().getBoolean("isOnSale"));
                params.put("type", getArguments().getString("type"));
                params.put("image", getArguments().getString("image"));


                AsyncHttpClient client = new AsyncHttpClient();
                client.put("http://ec2-52-86-213-15.compute-1.amazonaws.com/api/v1/cartItems/" + getArguments().getString("barcodeID"), params, new AsyncHttpResponseHandler() {
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
