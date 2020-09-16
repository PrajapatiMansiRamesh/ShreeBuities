package com.tecmanic.gogrocer.Adapters;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tecmanic.gogrocer.activity.ProductDetails;
import com.tecmanic.gogrocer.ModelClass.CartModel;
import com.tecmanic.gogrocer.ModelClass.NewCartModel;
import com.tecmanic.gogrocer.ModelClass.varient_product;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tecmanic.gogrocer.Config.BaseURL.IMG_URL;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    private final int limit = 6;
    SharedPreferences preferences;
    Context context;
    RecyclerView recyler_popup;
    LinearLayout cancl;
    String varient_id, product_id;
    private List<NewCartModel> topSelling;
    private DatabaseHandler dbcart;
    private List<CartModel> cartList;
    private Session_management session_management;
    private List<varient_product> varientProducts = new ArrayList<>();

//    public CartAdapter(Context context, List<CartModel> cartList) {
//        this.cartList = cartList;
//        dbcart = new DatabaseHandler(context);
//    }


//    public CartAdapter(List<CartModel> cartList, Context context) {
//        this.cartList = cartList;
//        dbcart = new DatabaseHandler(context);
//        session_management = new Session_management(context);
//    }

    public CartAdapter(Context context, List<NewCartModel> topSelling) {
        this.context = context;
        this.topSelling = topSelling;
        dbcart = new DatabaseHandler(context);
        session_management = new Session_management(context);
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_product_add, parent, false);
        context = parent.getContext();
        dbcart = new DatabaseHandler(context);
        return new CartAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartAdapter.MyViewHolder holder, int position) {

        NewCartModel cc = topSelling.get(position);
        holder.currency_indicator.setText(session_management.getCurrency());
        holder.currency_indicator_2.setText(session_management.getCurrency());
        holder.prodNAme.setText(cc.getProduct_name());
        holder.pDescrptn.setText(cc.getDescription());
        holder.pQuan.setText(cc.getQuantity() + "" + cc.getUnit());
        holder.pPrice.setText(cc.getPrice());
        if(!holder.pMrp.getText().toString().equalsIgnoreCase(null)){
           holder.pdiscountOff.setVisibility(View.VISIBLE);
            holder.pMrp.setVisibility(View.VISIBLE);
            holder.currency_indicator_2.setVisibility(View.VISIBLE);
            String totalOff = String.valueOf(Float.parseFloat(cc.getMrp()) - Integer.parseInt(cc.getPrice()));
            holder.pdiscountOff.setText(session_management.getCurrency() + totalOff + " " + "Off");
            holder.pMrp.setText(cc.getMrp());
            holder.pMrp.setPaintFlags(holder.pMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else {
            holder.pdiscountOff.setVisibility(View.GONE);
            holder.pMrp.setVisibility(View.GONE);
            holder.currency_indicator_2.setVisibility(View.GONE);

        }
         session_management.setStoreId(cc.getStore_id());
        if (Integer.parseInt(cc.getStock())>0){
            holder.outofs.setVisibility(View.GONE);
            holder.outofs_in.setVisibility(View.VISIBLE);
        }else {
            holder.outofs_in.setVisibility(View.GONE);
            holder.outofs.setVisibility(View.VISIBLE);
        }


        int qtyd = Integer.parseInt(dbcart.getInCartItemQtys(topSelling.get(position).getVarient_id()));
        if (qtyd > 0) {
            holder.btn_Add.setVisibility(View.GONE);
            holder.ll_addQuan.setVisibility(View.VISIBLE);
            holder.txtQuan.setText("" + qtyd);
            double priced = Double.parseDouble(cc.getPrice());
            double mrpd = Double.parseDouble(cc.getMrp());
            holder.pPrice.setText("" + (priced * qtyd));
            holder.pMrp.setText("" + (mrpd * qtyd));
        } else {
            holder.btn_Add.setVisibility(View.VISIBLE);
            holder.ll_addQuan.setVisibility(View.GONE);
            holder.pPrice.setText(cc.getPrice());
            holder.pMrp.setText(cc.getMrp());
            holder.txtQuan.setText("" + 0);
        }

        Picasso.with(context)
                .load(IMG_URL + cc.getProduct_image())
                .into(holder.image);


//        Double items = Double.parseDouble(dbcart.getInCartItemQty(cartList.get(position).getpId()));
        double price = Double.parseDouble(topSelling.get(position).getPrice());
        double mrp = Double.parseDouble(topSelling.get(position).getMrp());


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetails.class);
            intent.putExtra("sId", topSelling.get(position).getProduct_id());
            intent.putExtra("sVariant_id", topSelling.get(position).getVarient_id());
            intent.putExtra("sName", topSelling.get(position).getProduct_name());
            intent.putExtra("descrip", topSelling.get(position).getDescription());
            intent.putExtra("price", topSelling.get(position).getPrice());
            intent.putExtra("mrp", topSelling.get(position).getMrp());
            intent.putExtra("unit", topSelling.get(position).getUnit());
            intent.putExtra("stock", topSelling.get(position).getStock());
            intent.putExtra("qty", topSelling.get(position).getQuantity());
            intent.putExtra("image", topSelling.get(position).getProduct_image());

            v.getContext().startActivity(intent);

        });


        holder.plus.setOnClickListener(v -> {
            try {
                if (dbcart == null) {
                    dbcart = new DatabaseHandler(v.getContext());
                }
                int i = Integer.parseInt(dbcart.getInCartItemQtys(topSelling.get(position).getVarient_id()));
                if (i<Integer.parseInt(cc.getStock())){
                    holder.btn_Add.setVisibility(View.GONE);
                    holder.ll_addQuan.setVisibility(View.VISIBLE);
//                    Log.i("Product _id", topSelling.get(position).getVarient_id());
                    holder.txtQuan.setText("" + (i + 1));
                    holder.pPrice.setText("" + (price * (i + 1)));
                    holder.pMrp.setText("" + (mrp * (i + 1)));
                    updateMultiply(position, (i + 1));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        holder.minus.setOnClickListener(v -> {
            Log.i("Product _id", topSelling.get(position).getVarient_id());
            int i = Integer.parseInt(dbcart.getInCartItemQtys(topSelling.get(position).getVarient_id()));
//            int i = Integer.parseInt(cc.getpQuan());
//            cartList.get(position).setpQuan(String.valueOf(i-1));

            if ((i - 1) < 0 || (i - 1) == 0) {
                holder.btn_Add.setVisibility(View.VISIBLE);
                holder.ll_addQuan.setVisibility(View.GONE);
                holder.txtQuan.setText("" + 0);
                holder.pPrice.setText("" + price);
                holder.pMrp.setText("" + mrp);
            } else {
                holder.txtQuan.setText("" + (i - 1));
                holder.pPrice.setText("" + (price * (i - 1)));
                holder.pMrp.setText("" + (mrp * (i - 1)));
            }
            updateMultiply(position, (i - 1));
        });
        holder.btn_Add.setOnClickListener(v -> {
            holder.btn_Add.setVisibility(View.GONE);
            holder.ll_addQuan.setVisibility(View.VISIBLE);
            holder.txtQuan.setText("1");
            updateMultiply(position, 1);
        });

    }

    @Override
    public int getItemCount() {

        if (topSelling.size() > limit) {
            return limit;
        } else {
            return topSelling.size();
        }
    }

    private void updateMultiply(int pos, int i) {
        try {
            Log.i("Product _id", topSelling.get(pos).getVarient_id());
            HashMap<String, String> map = new HashMap<>();
//            map.put("varient_id",cartList.get(position).getpId());
            map.put("varient_id", topSelling.get(pos).getVarient_id());
            map.put("product_name", topSelling.get(pos).getProduct_name());
            map.put("category_id", topSelling.get(pos).getProduct_id());
            map.put("title", topSelling.get(pos).getProduct_name());
            map.put("price", topSelling.get(pos).getPrice());
            map.put("mrp", topSelling.get(pos).getMrp());
//        Log.d("fd",cartList.get(pos).getpImage());
            map.put("product_image", topSelling.get(pos).getProduct_image());
            map.put("status", "");
            map.put("in_stock", "");
            map.put("unit_value", topSelling.get(pos).getQuantity() + "" + topSelling.get(pos).getUnit());
            map.put("unit", topSelling.get(pos).getUnit());
            map.put("increament", "0");
            map.put("rewards", "0");
            map.put("stock", topSelling.get(pos).getStock());
            map.put("product_description", topSelling.get(pos).getDescription());

            if (i > 0) {
                if (dbcart.isInCart(map.get("varient_id"))) {
                    dbcart.setCart(map, i);
                } else {
                    dbcart.setCart(map, i);
                }
            } else {
                dbcart.removeItemFromCart(map.get("varient_id"));
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                SharedPreferences preferences = context.getSharedPreferences("GOGrocer", Context.MODE_PRIVATE);
                preferences.edit().putInt("cardqnty", dbcart.getCartCount()).apply();
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView prodNAme, pDescrptn, pQuan, pPrice, pdiscountOff, pMrp, minus, plus, txtQuan, currency_indicator, currency_indicator_2;
        ImageView image;
        LinearLayout btn_Add, ll_addQuan,outofs,outofs_in;
        int minteger = 0;
        RelativeLayout rlQuan;
        String catId, catName;

        public MyViewHolder(View view) {
            super(view);
            prodNAme = view.findViewById(R.id.txt_pName);
            currency_indicator = view.findViewById(R.id.currency_indicator);
            currency_indicator_2 = view.findViewById(R.id.currency_indicator_2);
            pDescrptn = view.findViewById(R.id.txt_pInfo);
            pQuan = view.findViewById(R.id.txt_unit);
            pPrice = view.findViewById(R.id.txt_Pprice);
            image = view.findViewById(R.id.prodImage);
            pdiscountOff = view.findViewById(R.id.txt_discountOff);
            pMrp = view.findViewById(R.id.txt_Mrp);
            rlQuan = view.findViewById(R.id.rlQuan);
            btn_Add = view.findViewById(R.id.btn_Add);
            ll_addQuan = view.findViewById(R.id.ll_addQuan);
            outofs = view.findViewById(R.id.outofs);
            outofs_in = view.findViewById(R.id.outofs_in);
            txtQuan = view.findViewById(R.id.txtQuan);
            minus = view.findViewById(R.id.minus);
            plus = view.findViewById(R.id.plus);
//            btn_Add.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    btn_Add.setVisibility(View.GONE);
//                    ll_addQuan.setVisibility(View.VISIBLE);
//                    txtQuan.setText("1");
//                    updateMultiply();
//                }
//            });
//            plus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    increaseInteger();
////                    updateMultiply();
//
//                    if (Float.parseFloat(txtQuan.getText().toString()) == 1) {
//                       /* minus.setClickable(false);
//                        minus.setFocusable(false);*/
//                    } else if (Float.parseFloat(txtQuan.getText().toString()) > 1) {
//
//                    }
//                }
//            });
//            minus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    decreaseInteger();
//                    updateMultiply();
//                }
//            });
            //  minus.setOnClickListener(this);
            //   plus.setOnClickListener(this);


        }

        private void display(Integer number) {

            txtQuan.setText("" + number);
        }


        @Override
        public void onClick(View view) {

        }
    }
//
//    public void increaseInteger() {
//        minteger = minteger + 1;
//        display(minteger);
//    }
//
//    public void decreaseInteger() {
//        if (minteger == 1) {
//            minteger = 1;
//            display(minteger);
//            ll_addQuan.setVisibility(View.GONE);
//            btn_Add.setVisibility(View.VISIBLE);
//        } else {
//            minteger = minteger - 1;
//            display(minteger);
//
//        }
//    }


}
