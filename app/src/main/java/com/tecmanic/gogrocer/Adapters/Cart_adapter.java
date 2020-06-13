package com.tecmanic.gogrocer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;
import com.tecmanic.gogrocer.util.ViewNotifier;

import java.util.ArrayList;
import java.util.HashMap;

import static com.tecmanic.gogrocer.Config.BaseURL.IMG_URL;
import static com.tecmanic.gogrocer.Fragments.CartFragment.tv_total;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class Cart_adapter extends RecyclerView.Adapter<Cart_adapter.ProductHolder> {
    ArrayList<HashMap<String, String>> list;
    Activity activity;
    String price_tx;
    SharedPreferences preferences;
    String language;
    int lastpostion;
    DatabaseHandler dbHandler;
    private ViewNotifier notifier;
    private Session_management session_management;

    public Cart_adapter(Activity activity, ArrayList<HashMap<String, String>> list, ViewNotifier viewNotifier) {
        this.list = list;
        this.activity = activity;
        notifier = viewNotifier;

        dbHandler = new DatabaseHandler(activity);
        session_management = new Session_management(activity);
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position) {

        final HashMap<String, String> map = list.get(position);
        holder.currency_indicator.setText(session_management.getCurrency());
        Glide.with(activity)
                .load(IMG_URL + map.get("product_image"))
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_logo);

        holder.tv_title.setText(map.get("product_name"));
        holder.pDescrptn.setText(map.get("product_description"));
        int items = (int) Double.parseDouble(dbHandler.getInCartItemQty(map.get("varient_id")));
        int sprice = (int) Double.parseDouble(map.get("price"));
        Log.d("kjs", String.valueOf(sprice));

        holder.price = sprice;
//        int items = (int) Double.parseDouble(dbcart.getInCartItemQty(map.get("varient_id")));

        holder.pPrice.setText("" + sprice * items);
        //  Log.d("fgh",map.get("title"));
        Log.d("fghfgh", map.get("price"));
        tv_total.setText(session_management.getCurrency() + " " + dbHandler.getTotalAmount());


        //  Double reward = Double.parseDouble(map.get("rewards"));

        //  holder.pPrice.setText(map.get("deal_price"));
        holder.tv_contetiy.setText("" + items);
        holder.minteger = items;
        holder.pQuan.setText("" + map.get("unit_value"));
        holder.pMrp.setText("" + map.get("price"));
        holder.pMrp.setPaintFlags(holder.pMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        //  holder.tv_reward.setText("" + reward * items);
       /* holder.iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = 0;
                if (!holder.tv_contetiy.getText().toString().equalsIgnoreCase(""))
                    qty = Integer.valueOf(holder.tv_contetiy.getText().toString());

                if (qty > 0) {
                    qty = qty - 1;
                    holder.tv_contetiy.setText(String.valueOf(qty));
                }

                if (holder.tv_contetiy.getText().toString().equalsIgnoreCase("0")) {
                    dbHandler.removeItemFromCart(map.get("product_id"));
                    list.remove(position);
                    notifyDataSetChanged();

                    updateintent();
                }
            }
        });*/

      /*  holder.iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int qty = Integer.valueOf(holder.tv_contetiy.getText().toString());
                qty = qty + 1;

                holder.tv_contetiy.setText(String.valueOf(qty));
            }
        });*/

        holder.tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.tv_add.setVisibility(View.GONE);
                holder.ll_addQuan.setVisibility(View.VISIBLE);

                dbHandler.setCart(map, Integer.valueOf(holder.tv_contetiy.getText().toString()));
                Double items = Double.parseDouble(dbHandler.getInCartItemQty(map.get("varient_id")));
                Double price = Double.parseDouble(map.get("price"));
                Double reward = Double.parseDouble(map.get("rewards"));
                holder.pPrice.setText("" + price * items);
                //  holder.tv_reward.setText("" + reward * items);
                //   holder.tv_total.setText(activity.getResources().getString(R.string.tv_cart_total) + price * items + " " + activity.getResources().getString(R.string.currency));
                updateintent(dbHandler, view.getContext());
            }
        });

        holder.txt_close.setOnClickListener(view -> {
            dbHandler.removeItemFromCart(map.get("varient_id"));
            list.remove(position);
            notifyDataSetChanged();
            updateintent(dbHandler, view.getContext());
        });

        holder.iv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseInteger(holder);
                updateMultiply(holder, map,position);
            }
        });

        holder.iv_plus.setOnClickListener(v -> {
            increaseInteger(holder);
            updateMultiply(holder, map, position);
        });
    }

    private void updateMultiply(ProductHolder holder, HashMap<String, String> map, int position) {

        dbHandler.setCart(map, Integer.valueOf(holder.tv_contetiy.getText().toString()));
        Log.d("asfd", holder.tv_contetiy.getText().toString());


        int items = (int) Double.parseDouble(dbHandler.getInCartItemQty(map.get("varient_id")));
        //  Double price = Double.parseDouble(map.get("price"));
//        Double reward = Double.parseDouble(dbHandler.getInCartItemQty(map.get("price")));
        if (items == 1) {
            holder.tv_contetiy.setText("" + Integer.valueOf(String.valueOf(items)));
            holder.pPrice.setText("" + holder.price);
            dbHandler.removeItemFromCart(map.get("varient_id"));
            list.remove(position);
            notifyDataSetChanged();
        } else {
            holder.tv_contetiy.setText("" + Integer.valueOf(String.valueOf(items)));
            holder.pPrice.setText("" + holder.price * items);
        }
        tv_total.setText(session_management.getCurrency() + " " + dbHandler.getTotalAmount());

        //   holder.tv_total.setText(activity.getResources().getString(R.string.tv_cart_total) + price * items + " " + activity.getResources().getString(R.string.currency));
        updateintent(dbHandler, activity.getApplicationContext());
    }

    public void increaseInteger(ProductHolder holder) {
        holder.minteger = holder.minteger + 1;
        display(holder.minteger, holder);
    }

    public void decreaseInteger(ProductHolder holder) {
        if (holder.minteger == 1) {
            holder.minteger = 1;
            display(holder.minteger, holder);
            holder.ll_addQuan.setVisibility(View.GONE);
            holder.tv_add.setVisibility(View.VISIBLE);
        } else {
            holder.minteger = holder.minteger - 1;
            display(holder.minteger, holder);

        }
    }

    private void display(Integer number, ProductHolder holder) {

        holder.tv_contetiy.setText("" + number);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void updateintent(DatabaseHandler dbHandler, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SharedPreferences preferences = context.getSharedPreferences("GOGrocer", Context.MODE_PRIVATE);
            preferences.edit().putInt("cardqnty", dbHandler.getCartCount()).apply();

            if (dbHandler.getCartCount() == 0) {
                notifier.onViewNotify();
            }
        }
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "update");
        activity.sendBroadcast(updates);
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        public TextView tv_title, txt_close, tv_contetiy, iv_plus, iv_minus, pDescrptn, pQuan, pPrice, pdiscountOff, pMrp, tv_unit, tv_unit_value, currency_indicator;
        public ImageView iv_logo;
        LinearLayout tv_add, ll_addQuan;
        int minteger = 0;
        int price = 0;

        public ProductHolder(View view) {
            super(view);

            tv_title = view.findViewById(R.id.txt_pName);
            currency_indicator = view.findViewById(R.id.currency_indicator);
            iv_logo = view.findViewById(R.id.prodImage);

            tv_contetiy = view.findViewById(R.id.txtQuan);
            tv_add = view.findViewById(R.id.btn_Add);
            ll_addQuan = view.findViewById(R.id.ll_addQuan);
            iv_plus = view.findViewById(R.id.plus);
            iv_minus = view.findViewById(R.id.minus);

            pDescrptn = view.findViewById(R.id.txt_pInfo);
            pQuan = view.findViewById(R.id.txt_unit);
            pPrice = view.findViewById(R.id.txt_Pprice);
            pdiscountOff = view.findViewById(R.id.txt_discountOff);
            pMrp = view.findViewById(R.id.txt_Mrp);

            txt_close = view.findViewById(R.id.txt_close);

            //  tv_add.setText(R.string.tv_pro_update);

        }
    }

}

