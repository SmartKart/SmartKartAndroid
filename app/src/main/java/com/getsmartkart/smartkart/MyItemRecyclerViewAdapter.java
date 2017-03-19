package com.getsmartkart.smartkart;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.getsmartkart.smartkart.ItemListFragment.OnListFragmentInteractionListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MyItemRecyclerViewAdapter extends RecyclerSwipeAdapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<ShoppingCartItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Activity activity;
    private final FragmentManager fragmentManager;

    public MyItemRecyclerViewAdapter(List<ShoppingCartItem> items, OnListFragmentInteractionListener listener, Activity mainActivity, FragmentManager fm) {
        mValues = items;
        mListener = listener;
        activity = mainActivity;
        fragmentManager = fm;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.CEILING);
        holder.mItem = mValues.get(position);
        final ShoppingCartItem item = mValues.get(position);
        if(item.getName().length() > 8){
            holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, 35);
        }
        holder.itemName.setText(item.getName());
        if(item.isOnSale()){
            holder.unitPrice.setText("$" + df.format(item.getPrice()));
            SpannableString text = new SpannableString("$" + df.format(item.getPrice()) + "   $" + df.format(item.getPrice() * (1-(item.getPercentOff()/100.0))));
            text.setSpan(new StrikethroughSpan(), 0, df.format(item.getPrice()).length()+1, 0);
            holder.unitPrice.setText(text, TextView.BufferType.SPANNABLE);
            holder.totalPriceOfItem.setText("$" + df.format((item.getPrice() * (1-(item.getPercentOff()/100.0))) * item.getQuantity()));
        }
        else{
            holder.unitPrice.setText("$" + df.format(item.getPrice()));
            holder.totalPriceOfItem.setText("$" + df.format(item.getPrice() * item.getQuantity()));
        }
        holder.numberOfItems.setText("Quantity: " + item.getQuantity());


        new DownloadImageTask(holder.itemImage)
                .execute(item.getImage());


        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.removeItemFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Send the remove info to the database

                final ActiveShoppingCartActivity ref = (ActiveShoppingCartActivity) activity;

                AsyncHttpClient client = new AsyncHttpClient();
                client.delete("http://ec2-52-86-213-15.compute-1.amazonaws.com/api/v1/cartItems/" + item.getId(), new AsyncHttpResponseHandler() {
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

                mValues.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mValues.size());
            }
        });


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveShoppingCartActivity ref = (ActiveShoppingCartActivity) activity;
                ref.closePopup();
                if(!(ref.getPopupStatus())){
                    Bundle editItemBundle = new Bundle();
                    editItemBundle.putString("barcodeID", item.getId());
                    editItemBundle.putString("name", item.getName());
                    editItemBundle.putDouble("price", item.getPrice());
                    editItemBundle.putInt("percentOff", item.getPercentOff());
                    editItemBundle.putBoolean("isOnSale", item.isOnSale());
                    editItemBundle.putString("type", item.getType());
                    editItemBundle.putString("image", item.getImage());
                    editItemBundle.putInt("number", item.getQuantity());
                    EditItemAmountFragment editAmountFragment = new EditItemAmountFragment();
                    editAmountFragment.setArguments(editItemBundle);
                    editAmountFragment.show(fragmentManager, "Edit Amount");
                    ref.openPopup();
                }
                else{
                    Log.v("Popup Already Active", "Active - Edit");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.CartSwipeLayout;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView itemName;
        public final TextView numberOfItems;
        public final TextView unitPrice;
        public final TextView totalPriceOfItem;
        public final ImageView itemImage;
        public final ImageButton removeItemFromCart;
        public final SwipeLayout swipeLayout;
        public final Button editButton;
        public ShoppingCartItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            itemName = (TextView) view.findViewById(R.id.item_name);
            unitPrice = (TextView) view.findViewById(R.id.unit_price);
            numberOfItems = (TextView) view.findViewById(R.id.total_amount_of_item);
            totalPriceOfItem = (TextView) view.findViewById(R.id.total_price);
            itemImage = (ImageView) view.findViewById(R.id.item_image);
            swipeLayout = (SwipeLayout) view.findViewById(R.id.CartSwipeLayout);
            removeItemFromCart = (ImageButton) view.findViewById(R.id.remove_item);
            editButton = (Button) view.findViewById(R.id.list_item_edit_button);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + itemName.getText() + "'";
        }
    }

    public void swap(List<ShoppingCartItem> datas){
        mValues.clear();
        mValues.addAll(datas);
        notifyDataSetChanged();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
