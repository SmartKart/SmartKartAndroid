package com.getsmartkart.smartkart;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by Nathan on 2017-03-18.
 */

public class VerifyItemAmountFragment extends DialogFragment {

    private NumberPicker amountPicker;
    private Button cancelButton;
    private Button addToCartButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.verify_item_amount, container, false);

        TextView barcode = (TextView) rootView.findViewById(R.id.barcodeValue);
        amountPicker = (NumberPicker) rootView.findViewById(R.id.amountPicker);
        barcode.setText("Add " + getArguments().getString("barcode") + " to cart");
        amountPicker.setMinValue(1);
        amountPicker.setMaxValue(100);
        amountPicker.setWrapSelectorWheel(false);

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
                //TODO: All the call backs
                ActiveShoppingCartActivity ref = (ActiveShoppingCartActivity) getActivity();
                ref.closePopup();
                amountPicker.getValue();
                dismiss();
            }
        });



        return rootView;
    }

}
