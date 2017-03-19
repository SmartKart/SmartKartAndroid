package com.getsmartkart.smartkart;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

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
        amountPicker.setValue(getArguments().getInt("num"));

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
