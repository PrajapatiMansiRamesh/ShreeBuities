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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tecmanic.gogrocer.Activity.ProductDetails;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.ModelClass.CartModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.DatabaseHandler;

import java.util.HashMap;
import java.util.List;

import static com.tecmanic.gogrocer.Config.BaseURL.IMG_URL;

public class ViewAll_Adapter extends RecyclerView.Adapter<ViewAll_Adapter.MyViewHolder> {
    Context context;
    private DatabaseHandler dbcart;
    private List<CartModel> cartList;

    public ViewAll_Adapter(List<CartModel> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
        dbcart = new DatabaseHandler(context);
    }

    @NonNull
    @Override
    public ViewAll_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_product_add, parent, false);
        dbcart = new DatabaseHandler(context);
        return new ViewAll_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewAll_Adapter.MyViewHolder holder, int position) {
        CartModel cc = cartList.get(position);
        holder.prodNAme.setText(cc.getpNAme());
        holder.pDescrptn.setText(cc.getpDes());
        holder.pQuan.setText(cc.getpQuan());
        holder.pPrice.setText(cc.getpPrice());
        holder.pdiscountOff.setText(cc.getDiscountOff());
        holder.pMrp.setText(cc.getpMrp());
        holder.pMrp.setPaintFlags(holder.pMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);



        int qtyd = Integer.parseInt(dbcart.getInCartItemQtys(cartList.get(position).getVarient_id()));
        if (qtyd > 0) {
            holder.btn_Add.setVisibility(View.GONE);
            holder.ll_addQuan.setVisibility(View.VISIBLE);
            holder.txtQuan.setText("" + qtyd);
            double priced = Double.parseDouble(cc.getpPrice());
            double mrpd = Double.parseDouble(cc.getpMrp());
            holder.pPrice.setText("" + (priced * qtyd));
            holder.pMrp.setText("" + (mrpd * qtyd));
        } else {
            holder.btn_Add.setVisibility(View.VISIBLE);
            holder.ll_addQuan.setVisibility(View.GONE);
            holder.pPrice.setText(cc.getpPrice());
            holder.pMrp.setText(cc.getpMrp());
            holder.txtQuan.setText("" + 0);
        }

        Glide.with(context)
                .load(IMG_URL + cc.getpImage())
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.image);
        holder.image.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetails.class);
            intent.putExtra("sId", cartList.get(position).getpId());
            intent.putExtra("sVariant_id", cartList.get(position).getVarient_id());
            intent.putExtra("sName", cartList.get(position).getpNAme());
            intent.putExtra("descrip", cartList.get(position).getpDes());
            intent.putExtra("price", cartList.get(position).getpPrice());
            intent.putExtra("mrp", cartList.get(position).getpMrp());
            intent.putExtra("unit", cartList.get(position).getUnit());
            intent.putExtra("qty", cartList.get(position).getpQuan());
            intent.putExtra("image", cartList.get(position).getpImage());

            v.getContext().startActivity(intent);

        });

//        Double items = Double.parseDouble(dbcart.getInCartItemQty(cartList.get(position).getVarient_id()));
        double price = Double.parseDouble(cartList.get(position).getpPrice());
        double mrp = Double.parseDouble(cartList.get(position).getpMrp());


        holder.plus.setOnClickListener(v -> {
            holder.btn_Add.setVisibility(View.GONE);
            holder.ll_addQuan.setVisibility(View.VISIBLE);
            int i = Integer.parseInt(dbcart.getInCartItemQtys(cartList.get(position).getVarient_id()));
//            cartList.get(position).setpQuan(String.valueOf(i + 1));
            holder.txtQuan.setText("" + (i + 1));
            holder.pPrice.setText("" + (price * (i + 1)));
            holder.pMrp.setText("" + (mrp * (i + 1)));
            updateMultiply(position);
//            notifyItemChanged(position);
        });
        holder.minus.setOnClickListener(v -> {
            int i = Integer.parseInt(dbcart.getInCartItemQtys(cartList.get(position).getVarient_id()));
//            cartList.get(position).setpQuan(String.valueOf(i - 1));
            holder.txtQuan.setText("" + (i - 1));
            holder.pPrice.setText("" + (price * (i - 1)));
            holder.pMrp.setText("" + (mrp * (i - 1)));
            if ((i - 1) < 0 || (i - 1) == 0) {
                holder.btn_Add.setVisibility(View.VISIBLE);
                holder.ll_addQuan.setVisibility(View.GONE);
            }
            updateMultiply(position);
        });
        holder.btn_Add.setOnClickListener(v -> {
            holder.btn_Add.setVisibility(View.GONE);
            holder.ll_addQuan.setVisibility(View.VISIBLE);
//            cartList.get(position).setpQuan("1");
            holder.txtQuan.setText("1");
            updateMultiply(position);
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    private void updateMultiply(int pos) {
        HashMap<String, String> map = new HashMap<>();
//            map.put("varient_id",cartList.get(position).getpId());
        map.put("varient_id", cartList.get(pos).getVarient_id());
        map.put("product_name", cartList.get(pos).getpNAme());
        map.put("category_id", cartList.get(pos).getpId());
        map.put("title", cartList.get(pos).getpNAme());
        map.put("price", cartList.get(pos).getpPrice());
        map.put("mrp", cartList.get(pos).getpMrp());
        Log.d("fd", cartList.get(pos).getpImage());
        map.put("product_image", cartList.get(pos).getpImage());
        map.put("status", cartList.get(pos).getStatus());
        map.put("in_stock", cartList.get(pos).getIn_stock());
        map.put("unit_value", cartList.get(pos).getpQuan());
        map.put("unit", cartList.get(pos).getUnit());
        map.put("increament", "0");
        map.put("rewards", "0");
        map.put("stock", "0");
        map.put("product_description", cartList.get(pos).getpDes());

//        Log.d("fgh",cartList.get(position).getUnit()+cartList.get(position).getpQuan());
//        Log.d("fghfgh",cartList.get(position).getpPrice());
        if (!cartList.get(pos).getpQuan().equalsIgnoreCase("0")) {
            if (dbcart.isInCart(map.get("varient_id"))) {
                dbcart.setCart(map, Integer.parseInt(cartList.get(pos).getpQuan()));
            } else {
                dbcart.setCart(map, Integer.parseInt(cartList.get(pos).getpQuan()));
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
                SharedPreferences preferences = context.getSharedPreferences("GOGrocer", Context.MODE_PRIVATE);
                preferences.edit().putInt("cardqnty", dbcart.getCartCount()).apply();
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d("qwer", e.toString());
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView prodNAme, pDescrptn, pQuan, pPrice, pdiscountOff, pMrp, minus, plus, txtQuan;
        ImageView image;
        LinearLayout btn_Add, ll_addQuan;
        int minteger = 0;
        RelativeLayout rlQuan;
        String catId, catName;

        public MyViewHolder(View view) {
            super(view);
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
        }
    }
}



