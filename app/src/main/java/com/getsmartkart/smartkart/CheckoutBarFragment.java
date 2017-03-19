package com.getsmartkart.smartkart;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CheckoutBarFragment extends Fragment {

    TextView currentPriceText;
    Button checkoutButton;

    double currentPrice = 0.00;


    public CheckoutBarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currentView =  inflater.inflate(R.layout.fragment_checkout_bar, container, false);

        currentPriceText = (TextView) currentView.findViewById(R.id.current_price);
        checkoutButton = (Button) currentView.findViewById(R.id.checkout_button);

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Open up confirm screen and then send request.
            }
        });

        return currentView;
    }

    public void updateTotalPrice(double newPrice){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        currentPrice = newPrice;
        currentPriceText.setText("Total: " + df.format(newPrice));
    }

}
