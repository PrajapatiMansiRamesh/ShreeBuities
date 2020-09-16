package com.tecmanic.gogrocer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tecmanic.gogrocer.activity.ProductDetails;
import com.tecmanic.gogrocer.ModelClass.CartModel;
import com.tecmanic.gogrocer.ModelClass.NewCartModel;
import com.tecmanic.gogrocer.ModelClass.varient_product;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;
import com.tecmanic.gogrocer.util.ViewNotifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tecmanic.gogrocer.Config.BaseURL.IMG_URL;

public class Deal_Adapter extends RecyclerView.Adapter<Deal_Adapter.MyViewHolder> {
    private final int limit = 6;
    SharedPreferences preferences;
    Context context;
    RecyclerView recyler_popup;
    LinearLayout cancl;
    String varient_id, product_id;
    private List<NewCartModel> dealoftheday;
    private DatabaseHandler dbcart;
    private List<CartModel> cartList = new ArrayList<>();
    private ArrayList al_data;
    private List<varient_product> varientProducts = new ArrayList<>();
    private Session_management session_management;
    private ViewNotifier viewNotifier;


    public Deal_Adapter(List<CartModel> topSellList, FragmentActivity activity) {
        this.cartList = topSellList;
        dbcart = new DatabaseHandler(activity);
        session_management = new Session_management(activity);
    }

    public Deal_Adapter(Context context, List<NewCartModel> dealoftheday) {
        this.context = context;
        this.dealoftheday = dealoftheday;
        dbcart = new DatabaseHandler(context);
        session_management = new Session_management(context);
    }

    public Deal_Adapter(Context context, List<NewCartModel> dealoftheday, ViewNotifier viewNotifier) {
        this.context = context;
        this.dealoftheday = dealoftheday;
        this.viewNotifier = viewNotifier;
        dbcart = new DatabaseHandler(context);
        session_management = new Session_management(context);
    }

    @Override
    public Deal_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_topsell, parent, false);
        context = parent.getContext();
        dbcart = new DatabaseHandler(context);
        if (session_management == null) {
            session_management = new Session_management(parent.getContext());
        }
        return new Deal_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Deal_Adapter.MyViewHolder holder, int position) {
        NewCartModel cc = dealoftheday.get(position);

        holder.currency_indicator.setText(session_management.getCurrency());
        holder.currency_indicator_2.setText(session_management.getCurrency());
        session_management.setStoreId(cc.getStore_id());
        holder.prodNAme.setText(cc.getProduct_name());
        holder.pDescrptn.setText(cc.getDescription());
        holder.pQuan.setText("" + cc.getQuantity() + " " + cc.getUnit());
        session_management.setStoreId(cc.getStore_id());
        int qtyd = Integer.parseInt(dbcart.getInCartItemQtys(dealoftheday.get(position).getVarient_id()));
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


       if(holder.pMrp.getText().toString().equalsIgnoreCase(null)){
           holder.pdiscountOff.setVisibility(View.GONE);
           holder.pMrp.setVisibility(View.GONE);
           holder.currency_indicator_2.setVisibility(View.GONE);
       }
       else {
           holder.pdiscountOff.setVisibility(View.VISIBLE);
           holder.pMrp.setVisibility(View.VISIBLE);
           holder.currency_indicator_2.setVisibility(View.VISIBLE);
           String totalOff = String.valueOf(Integer.parseInt(cc.getMrp()) - Integer.parseInt(cc.getPrice()));
           holder.pdiscountOff.setText(session_management.getCurrency() + totalOff + " " + "Off");
           holder.pMrp.setPaintFlags(holder.pMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

       }
        if (holder.timer != null) {
            holder.timer.cancel();
        }
        Long timer = Long.parseLong(dealoftheday.get(position).getTimediff());

        timer = timer * 60 * 1000;


        holder.timer = new CountDownTimer(timer, 1000) {
            public void onTick(long millisUntilFinished) {
                int sec = (int) (millisUntilFinished / 1000);

                int min = sec / 60;
                int our = min / 60;

                sec = sec % 60;

                min = min % 60;
//                Log.d("adfs", String.valueOf(sec));
//                our = our24 ;
                holder.time.setText(String.format(" %02d", our) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec));


            }

            public void onFinish() {
                holder.time.setText("00:00:00");
            }
        }.start();


        Picasso.with(context)
                .load(IMG_URL + cc.getProduct_image())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetails.class);
            intent.putExtra("sId", dealoftheday.get(position).getProduct_id());
            intent.putExtra("sName", dealoftheday.get(position).getProduct_name());
            intent.putExtra("descrip", dealoftheday.get(position).getDescription());
            intent.putExtra("price", dealoftheday.get(position).getPrice());
            intent.putExtra("mrp", dealoftheday.get(position).getMrp());
            intent.putExtra("unit", dealoftheday.get(position).getUnit());
            intent.putExtra("stock", dealoftheday.get(position).getStock());
            intent.putExtra("qty", dealoftheday.get(position).getQuantity());
            intent.putExtra("image", dealoftheday.get(position).getProduct_image());
            intent.putExtra("sVariant_id", dealoftheday.get(position).getVarient_id());
            context.startActivity(intent);

        });

        holder.plus.setOnClickListener(v -> {
            holder.btn_Add.setVisibility(View.GONE);
            holder.ll_addQuan.setVisibility(View.VISIBLE);
            int i = Integer.parseInt(dbcart.getInCartItemQtys(dealoftheday.get(position).getVarient_id()));
//            cartList.get(position).setpQuan(String.valueOf(i + 1));
            holder.txtQuan.setText("" + (i + 1));
            double priced = Double.parseDouble(cc.getPrice());
            double mrpd = Double.parseDouble(cc.getMrp());
            holder.pPrice.setText("" + (priced * (i + 1)));
            holder.pMrp.setText("" + (mrpd * (i + 1)));
            updateMultiply(position, (i + 1));

//            notifyItemChanged(position);
        });
        holder.minus.setOnClickListener(v -> {
            int i = Integer.parseInt(dbcart.getInCartItemQtys(dealoftheday.get(position).getVarient_id()));
            i = i - 1;
            if (i < 1) {
                holder.btn_Add.setVisibility(View.VISIBLE);
                holder.ll_addQuan.setVisibility(View.GONE);
//                cartList.get(position).setpQuan("0");
                holder.txtQuan.setText("0");
                double priced = Double.parseDouble(cc.getPrice());
                double mrpd = Double.parseDouble(cc.getMrp());
                holder.pPrice.setText("" + priced);
                holder.pMrp.setText("" + mrpd);
            } else {
                holder.btn_Add.setVisibility(View.GONE);
                holder.ll_addQuan.setVisibility(View.VISIBLE);
//                cartList.get(position).setpQuan(String.valueOf(i - 1));
                holder.txtQuan.setText("" + (i));
                double priced = Double.parseDouble(cc.getPrice());
                double mrpd = Double.parseDouble(cc.getMrp());
                holder.pPrice.setText("" + (priced * i));
                holder.pMrp.setText("" + (mrpd * i));
            }
            updateMultiply(position, i);
        });
        holder.btn_Add.setOnClickListener(v -> {
            holder.btn_Add.setVisibility(View.GONE);
            holder.ll_addQuan.setVisibility(View.VISIBLE);
//            cartList.get(position).setpQuan("1");
            holder.txtQuan.setText("1");
            updateMultiply(position, 1);
//            notifyItemChanged(position);
        });

       /* holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(context, ProductDetails.class);
                intent.putExtra("sId",cc.getpId());
                intent.putExtra("sName",cc.getpNAme());
                intent.putExtra("sImge",cc.getpImage());
                context.startActivity(intent);
            }
        });
*/


      /* if (!dbcart.isInCart(cartList.get(position).getpId())) {

        } else {
           holder.txtQuan.setText(dbcart.getCartItemQty(cartList.get(position).getpId()));
       }*/
//        Double items = Double.parseDouble(dbcart.getInCartItemQty(dealoftheday.get(position).getProduct_id()));
//        Double price = Double.parseDouble(dealoftheday.get(position).getPrice());
        // Double reward = Double.parseDouble(cartList.get(position).getRewards());
//        holder.pPrice.setText("" + price * items);
        // holder.tv_reward.setText("" + reward * items);


    }

    private void updateMultiply(int pos, int i) {
        HashMap<String, String> map = new HashMap<>();
//            map.put("varient_id",cartList.get(position).getpId());
        map.put("varient_id", dealoftheday.get(pos).getVarient_id());
        map.put("product_name", dealoftheday.get(pos).getProduct_name());
        map.put("category_id", dealoftheday.get(pos).getProduct_id());
        map.put("title", dealoftheday.get(pos).getProduct_name());
        map.put("price", dealoftheday.get(pos).getPrice());
        map.put("mrp", dealoftheday.get(pos).getMrp());
//        Log.d("fd", cartList.get(pos).getpImage());
        map.put("product_image", dealoftheday.get(pos).getProduct_image());
        map.put("status", "");
        map.put("in_stock", "");
        map.put("unit_value", dealoftheday.get(pos).getQuantity() + "" + dealoftheday.get(pos).getUnit());
        map.put("unit", dealoftheday.get(pos).getUnit());
        map.put("increament", "0");
        map.put("rewards", "0");
        map.put("stock", "0");
        map.put("product_description", dealoftheday.get(pos).getDescription());

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
                if (viewNotifier!=null){
                    viewNotifier.onViewNotify();
                }
                SharedPreferences preferences = context.getSharedPreferences("GOGrocer", Context.MODE_PRIVATE);
                preferences.edit().putInt("cardqnty", dbcart.getCartCount()).apply();
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d("qwer", e.toString());
        }
    }

    @Override
    public int getItemCount() {

        if (dealoftheday.size() > limit) {
            return limit;
        } else {
            return dealoftheday.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView prodNAme, pDescrptn, pQuan, pPrice, pdiscountOff, pMrp, minus, plus, txtQuan, time, currency_indicator, currency_indicator_2;
        ImageView image;
        LinearLayout btn_Add, ll_addQuan;
        CountDownTimer timer;
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
            txtQuan = view.findViewById(R.id.txtQuan);
            minus = view.findViewById(R.id.minus);
            plus = view.findViewById(R.id.plus);
            time = view.findViewById(R.id.time);

        }
    }

}

