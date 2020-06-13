package com.tecmanic.gogrocer.Adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.DatabaseHandler;

import java.util.ArrayList;
import java.util.HashMap;

import static com.tecmanic.gogrocer.Config.BaseURL.IMG_URL;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class ImageAdapterData extends RecyclerView.Adapter<ImageAdapterData.ProductHolder> {
    ArrayList<HashMap<String, String>> list;
    Activity activity;
    String price_tx;
    SharedPreferences preferences;
    String language;

    int lastpostion;
    DatabaseHandler dbHandler;

    public ImageAdapterData(Activity activity, ArrayList<HashMap<String, String>> list) {
        this.list = list;
        this.activity = activity;

        dbHandler = new DatabaseHandler(activity);
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageadapter, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position) {

        final HashMap<String, String> map = list.get(position);
        Glide.with(activity)
                .load(IMG_URL+ map.get("product_image"))
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.image_data);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        public ImageView image_data;


        public ProductHolder(View view) {
            super(view);


            image_data = (ImageView) view.findViewById(R.id.image_data);

           ;

            //  tv_add.setText(R.string.tv_pro_update);

        }
    }



}

