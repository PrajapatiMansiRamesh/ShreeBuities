package com.tecmanic.gogrocer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
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
import com.tecmanic.gogrocer.ModelClass.CartModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;

import java.util.HashMap;
import java.util.List;

import static com.tecmanic.gogrocer.Config.BaseURL.IMG_URL;

public class NewCartAdapter extends RecyclerView.Adapter<NewCartAdapter.MyNewCartSubView> {

    SharedPreferences preferences;
    Context context;
    private DatabaseHandler dbcart;
    private List<CartModel> cartList;
    private Session_management session_management;

    public NewCartAdapter(List<CartModel> cartList, Context context) {
        this.cartList = cartList;
        dbcart = new DatabaseHandler(context);
        session_management = new Session_management(context);
    }

    @NonNull
    @Override
    public MyNewCartSubView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_product_add, parent, false);
        context = parent.getContext();
        dbcart = new DatabaseHandler(context);
        return new MyNewCartSubView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNewCartSubView holder, int position) {
        CartModel cc = cartList.get(position);
        holder.currency_indicator.setText(session_management.getCurrency());
        holder.currency_indicator_2.setText(session_management.getCurrency());
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

        double price = Double.parseDouble(cartList.get(position).getpPrice());
        double mrp = Double.parseDouble(cartList.get(position).getpMrp());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        holder.plus.setOnClickListener(v -> {
            try {
                holder.btn_Add.setVisibility(View.GONE);
                holder.ll_addQuan.setVisibility(View.VISIBLE);
                if (dbcart == null) {
                    dbcart = new DatabaseHandler(v.getContext());
                }
                int i = Integer.parseInt(dbcart.getInCartItemQtys(cartList.get(position).getVarient_id()));
                holder.txtQuan.setText("" + (i + 1));
                holder.pPrice.setText("" + (price * (i + 1)));
                holder.pMrp.setText("" + (mrp * (i + 1)));
                updateMultiply(position, (i + 1));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        holder.minus.setOnClickListener(v -> {
            int i = Integer.parseInt(dbcart.getInCartItemQtys(cartList.get(position).getVarient_id()));
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
        return cartList.size();
    }

    private void updateMultiply(int pos, int i) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("varient_id", cartList.get(pos).getVarient_id());
            map.put("product_name", cartList.get(pos).getpNAme());
            map.put("category_id", cartList.get(pos).getpId());
            map.put("title", cartList.get(pos).getpNAme());
            map.put("price", cartList.get(pos).getpPrice());
            map.put("mrp", cartList.get(pos).getpMrp());
            map.put("product_image", cartList.get(pos).getpImage());
            map.put("status", cartList.get(pos).getStatus());
            map.put("in_stock", cartList.get(pos).getIn_stock());
            map.put("unit_value", cartList.get(pos).getUnit_value());
            map.put("unit", cartList.get(pos).getUnit());
            map.put("increament", "0");
            map.put("rewards", "0");
            map.put("stock", cartList.get(pos).getStock());
            map.put("product_description", cartList.get(pos).getpDes());

            if (i > 0) {
                if (dbcart.isInCart(map.get("varient_id"))) {
                    dbcart.setCart(map, i);
                } else {
                    dbcart.setCart(map, i);
                }
            } else {
                dbcart.removeItemFromCart(map.get("varient_id"));
                cartList.remove(pos);
                notifyDataSetChanged();
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                SharedPreferences preferences = context.getSharedPreferences("GOGrocer", Context.MODE_PRIVATE);
                preferences.edit().putInt("cardqnty", dbcart.getCartCount()).apply();
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public class MyNewCartSubView extends RecyclerView.ViewHolder {

        public TextView prodNAme, pDescrptn, pQuan, pPrice, pdiscountOff, pMrp, minus, plus, txtQuan, currency_indicator, currency_indicator_2;
        ImageView image;
        LinearLayout btn_Add, ll_addQuan;
        RelativeLayout rlQuan;

        public MyNewCartSubView(@NonNull View view) {
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
            txtQuan = view.findViewById(R.id.txtQuan);
            minus = view.findViewById(R.id.minus);
            plus = view.findViewById(R.id.plus);
        }
    }
}
