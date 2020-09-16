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
import com.tecmanic.gogrocer.ModelClass.NewCartModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;
import com.tecmanic.gogrocer.util.ViewNotifier;

import java.util.HashMap;
import java.util.List;

import static com.tecmanic.gogrocer.Config.BaseURL.IMG_URL;

public class ViewAll_Adapter extends RecyclerView.Adapter<ViewAll_Adapter.MyViewHolder> {
    Context context;
    private DatabaseHandler dbcart;
    private List<NewCartModel> cartList;
    private Session_management session_management;
    private ViewNotifier viewNotifier;

    public ViewAll_Adapter(List<NewCartModel> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
        dbcart = new DatabaseHandler(context);
        session_management = new Session_management(context);
    }

    public ViewAll_Adapter(List<NewCartModel> cartList, Context context, ViewNotifier viewNotifier) {
        this.cartList = cartList;
        this.context = context;
        this.viewNotifier = viewNotifier;
        dbcart = new DatabaseHandler(context);
        session_management = new Session_management(context);
    }

    @NonNull
    @Override
    public ViewAll_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_product_add, parent, false);
        context = parent.getContext();
     /*   dbcart = new DatabaseHandler(context);
        session_management = new Session_management(context);
*/
        dbcart = new DatabaseHandler(context);
        if (session_management == null){
            session_management = new Session_management(context);
        }
        return new ViewAll_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewAll_Adapter.MyViewHolder holder, int position) {
        NewCartModel cc = cartList.get(position);

        holder.currency_indicator.setText(session_management.getCurrency());
        holder.currency_indicator_2.setText(session_management.getCurrency());
        holder.prodNAme.setText(cc.getProduct_name());
        holder.pDescrptn.setText(cc.getDescription());
        holder.pQuan.setText(cc.getQuantity());
        holder.pPrice.setText(cc.getPrice());
        String totalOff = String.valueOf(Integer.parseInt(cc.getMrp()) - Integer.parseInt(cc.getPrice()));
        holder.pdiscountOff.setText(session_management.getCurrency() + totalOff + " " + "Off");
//        holder.pdiscountOff.setText(cc.());
        holder.pMrp.setText(cc.getMrp());
        holder.pMrp.setPaintFlags(holder.pMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        session_management.setStoreId(cc.getStore_id());

        if (Integer.parseInt(cc.getStock()) > 0) {
            holder.outofs.setVisibility(View.GONE);
            holder.outofs_in.setVisibility(View.VISIBLE);
        } else {
            holder.outofs_in.setVisibility(View.GONE);
            holder.outofs.setVisibility(View.VISIBLE);
        }

        int qtyd = Integer.parseInt(dbcart.getInCartItemQtys(cartList.get(position).getVarient_id()));
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
//        holder.image.setOnClickListener(v -> {
//
//        });

//        Double items = Double.parseDouble(dbcart.getInCartItemQty(cartList.get(position).getVarient_id()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetails.class);
            intent.putExtra("sId", cartList.get(position).getProduct_id());
            intent.putExtra("sVariant_id", cartList.get(position).getVarient_id());
            intent.putExtra("sName", cartList.get(position).getProduct_name());
            intent.putExtra("descrip", cartList.get(position).getDescription());
            intent.putExtra("price", cartList.get(position).getPrice());
            intent.putExtra("mrp", cartList.get(position).getMrp());
            intent.putExtra("unit", cartList.get(position).getUnit());
            intent.putExtra("qty", cartList.get(position).getQuantity());
            intent.putExtra("stock", cartList.get(position).getStock());
            intent.putExtra("image", cartList.get(position).getProduct_image());

            v.getContext().startActivity(intent);
        });

        double price = Double.parseDouble(cartList.get(position).getPrice());
        double mrp = Double.parseDouble(cartList.get(position).getMrp());


        holder.plus.setOnClickListener(v -> {
            holder.btn_Add.setVisibility(View.GONE);
            holder.ll_addQuan.setVisibility(View.VISIBLE);
            int i = Integer.parseInt(dbcart.getInCartItemQtys(cartList.get(position).getVarient_id()));
            if (i < Integer.parseInt(cc.getStock())) {
                //            cartList.get(position).setpQuan(String.valueOf(i + 1));
                holder.txtQuan.setText("" + (i + 1));
                holder.pPrice.setText("" + (price * (i + 1)));
                holder.pMrp.setText("" + (mrp * (i + 1)));
                updateMultiply(position, (i + 1));
//            notifyItemChanged(position);
            }
        });
        holder.minus.setOnClickListener(v -> {
            int i = Integer.parseInt(dbcart.getInCartItemQtys(cartList.get(position).getVarient_id()));
//            cartList.get(position).setpQuan(String.valueOf(i - 1));

            if ((i - 1) < 0 || (i - 1) == 0) {
                holder.btn_Add.setVisibility(View.VISIBLE);
                holder.ll_addQuan.setVisibility(View.GONE);
                holder.txtQuan.setText("" + (i - 1));
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
//            cartList.get(position).setpQuan("1");
            holder.txtQuan.setText("1");
            updateMultiply(position, 1);
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    private void updateMultiply(int pos, int i) {
        HashMap<String, String> map = new HashMap<>();
//            map.put("varient_id",cartList.get(position).getpId());
        map.put("varient_id", cartList.get(pos).getVarient_id());
        map.put("product_name", cartList.get(pos).getProduct_name());
        map.put("category_id", cartList.get(pos).getProduct_id());
        map.put("title", cartList.get(pos).getProduct_name());
        map.put("price", cartList.get(pos).getPrice());
        map.put("mrp", cartList.get(pos).getMrp());
//        Log.d("fd", cartList.get(pos).getProduct_image());
        map.put("product_image", cartList.get(pos).getProduct_image());
        map.put("status", "0");
        map.put("in_stock", "0");
        map.put("unit_value", "0");
        map.put("unit", cartList.get(pos).getUnit());
        map.put("increament", "0");
        map.put("rewards", "0");
        map.put("stock", cartList.get(pos).getStock());
        map.put("product_description", cartList.get(pos).getDescription());

//        Log.d("fgh",cartList.get(position).getUnit()+cartList.get(position).getpQuan());
//        Log.d("fghfgh",cartList.get(position).getpPrice());
        if (i > 0) {
            if (dbcart.isInCart(map.get("varient_id"))) {
                dbcart.setCart(map, i);
            } else {
                dbcart.setCart(map, i);
            }
        } else {
            dbcart.removeItemFromCart(map.get("varient_id"));
        }
        try {
//            int items = (int) Double.parseDouble(dbcart.getInCartItemQty(map.get("varient_id")));
//            double price = Double.parseDouble(Objects.requireNonNull(map.get("price")).trim());
//            double mrp = Double.parseDouble(Objects.requireNonNull(map.get("mrp")).trim());
            //  Double reward = Double.parseDouble(map.get("rewards"));
            // tv_reward.setText("" + reward * items);
//            pDescrptn.setText(""+cartList.get(position).getpDes());
//            pPrice.setText("" +price* items);
//            txtQuan.setText("" + items);
//            pMrp.setText("" + mrp* items );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (viewNotifier != null) {
                    viewNotifier.onViewNotify();
                }
                SharedPreferences preferences = context.getSharedPreferences("GOGrocer", Context.MODE_PRIVATE);
                preferences.edit().putInt("cardqnty", dbcart.getCartCount()).apply();
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d("qwer", e.toString());
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView prodNAme, pDescrptn, pQuan, pPrice, pdiscountOff, pMrp, minus, plus, txtQuan, currency_indicator, currency_indicator_2;
        ImageView image;
        LinearLayout btn_Add, ll_addQuan, outofs, outofs_in;
        int minteger = 0;
        RelativeLayout rlQuan;
        String catId, catName;

        public MyViewHolder(View view) {
            super(view);
            currency_indicator = view.findViewById(R.id.currency_indicator);
            currency_indicator_2 = view.findViewById(R.id.currency_indicator_2);

            prodNAme = view.findViewById(R.id.txt_pName);
            pDescrptn = view.findViewById(R.id.txt_pInfo);
            pQuan = view.findViewById(R.id.txt_unit);
            pPrice = view.findViewById(R.id.txt_Pprice);
            image = view.findViewById(R.id.prodImage);
            pdiscountOff = view.findViewById(R.id.txt_discountOff);
            pMrp = view.findViewById(R.id.txt_Mrp);
            rlQuan = view.findViewById(R.id.rlQuan);
            btn_Add = view.findViewById(R.id.btn_Add);
            ll_addQuan = view.findViewById(R.id.ll_addQuan);
            txtQuan = view.findViewById(R.id.txtQuan);
            minus = view.findViewById(R.id.minus);
            plus = view.findViewById(R.id.plus);
            outofs = view.findViewById(R.id.outofs);
            outofs_in = view.findViewById(R.id.outofs_in);
        }
    }
}



