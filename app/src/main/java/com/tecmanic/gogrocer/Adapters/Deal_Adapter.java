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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tecmanic.gogrocer.Activity.ProductDetails;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.ModelClass.CartModel;
import com.tecmanic.gogrocer.ModelClass.varient_product;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tecmanic.gogrocer.Config.BaseURL.IMG_URL;

public class Deal_Adapter extends RecyclerView.Adapter<Deal_Adapter.MyViewHolder> {
    private final int limit = 4;
    SharedPreferences preferences;
    Context context;
    RecyclerView recyler_popup;
    LinearLayout cancl;
    String varient_id, product_id;
    private DatabaseHandler dbcart;
    private List<CartModel> cartList = new ArrayList<>();
    private ArrayList al_data;
    private List<varient_product> varientProducts = new ArrayList<>();
    private Session_management session_management;


    public Deal_Adapter(List<CartModel> topSellList, FragmentActivity activity) {
        this.cartList = topSellList;
        dbcart = new DatabaseHandler(activity);
        session_management = new Session_management(activity);
    }

    public Deal_Adapter(ArrayList al_data) {
        this.cartList = al_data;
    }

    @Override
    public Deal_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_topsell, parent, false);
        context = parent.getContext();
        dbcart = new DatabaseHandler(context);
        return new Deal_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Deal_Adapter.MyViewHolder holder, int position) {
        CartModel cc = cartList.get(position);

        holder.currency_indicator.setText(session_management.getCurrency());
        holder.currency_indicator_2.setText(session_management.getCurrency());

        holder.prodNAme.setText(cc.getpNAme());
        holder.pDescrptn.setText(cc.getpDes());
        holder.pQuan.setText("" + cc.getpQuan());
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
        holder.pdiscountOff.setText(cc.getDiscountOff());
        holder.pMrp.setPaintFlags(holder.pMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        if (holder.timer != null) {
            holder.timer.cancel();
        }
        Long timer = ((cartList.get(position).getHoursmin()));

        timer = timer * 60 * 1000;


        holder.timer = new CountDownTimer(timer, 1000) {
            public void onTick(long millisUntilFinished) {
                int sec = (int) (millisUntilFinished / 1000);

                int min = sec / 60;
                int our = min / 60;

                sec = sec % 60;

                min = min % 60;
                Log.d("adfs", String.valueOf(sec));
//                our = our24 ;
                holder.time.setText(String.format(" %02d", our) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec));


            }

            public void onFinish() {
                holder.time.setText("00:00:00");
            }
        }.start();


        Glide.with(context)
                .load(IMG_URL + cc.getpImage())
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.image);

        holder.image.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetails.class);
            intent.putExtra("sId", cartList.get(position).getpId());
            intent.putExtra("sName", cartList.get(position).getpNAme());
            intent.putExtra("descrip", cartList.get(position).getpDes());
            intent.putExtra("price", cartList.get(position).getpPrice());
            intent.putExtra("mrp", cartList.get(position).getpMrp());
            intent.putExtra("unit", cartList.get(position).getUnit());
            intent.putExtra("qty", cartList.get(position).getpQuan());
            intent.putExtra("image", cartList.get(position).getpImage());
            intent.putExtra("sVariant_id", cartList.get(position).getVarient_id());
            context.startActivity(intent);

        });

        holder.plus.setOnClickListener(v -> {
            holder.btn_Add.setVisibility(View.GONE);
            holder.ll_addQuan.setVisibility(View.VISIBLE);
            int i = Integer.parseInt(dbcart.getInCartItemQtys(cartList.get(position).getVarient_id()));
//            cartList.get(position).setpQuan(String.valueOf(i + 1));
            holder.txtQuan.setText("" + (i + 1));
            double priced = Double.parseDouble(cc.getpPrice());
            double mrpd = Double.parseDouble(cc.getpMrp());
            holder.pPrice.setText("" + (priced * (i + 1)));
            holder.pMrp.setText("" + (mrpd * (i + 1)));
            updateMultiply(position, (i + 1));

//            notifyItemChanged(position);
        });
        holder.minus.setOnClickListener(v -> {
            int i = Integer.parseInt(dbcart.getInCartItemQtys(cartList.get(position).getVarient_id()));
            i = i - 1;
            if (i < 1) {
                holder.btn_Add.setVisibility(View.VISIBLE);
                holder.ll_addQuan.setVisibility(View.GONE);
//                cartList.get(position).setpQuan("0");
                holder.txtQuan.setText("0");
                double priced = Double.parseDouble(cc.getpPrice());
                double mrpd = Double.parseDouble(cc.getpMrp());
                holder.pPrice.setText("" + priced);
                holder.pMrp.setText("" + mrpd);
            } else {
                holder.btn_Add.setVisibility(View.GONE);
                holder.ll_addQuan.setVisibility(View.VISIBLE);
//                cartList.get(position).setpQuan(String.valueOf(i - 1));
                holder.txtQuan.setText("" + (i));
                double priced = Double.parseDouble(cc.getpPrice());
                double mrpd = Double.parseDouble(cc.getpMrp());
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
        Double items = Double.parseDouble(dbcart.getInCartItemQty(cartList.get(position).getpId()));
        Double price = Double.parseDouble(cartList.get(position).getpPrice());
        // Double reward = Double.parseDouble(cartList.get(position).getRewards());
//        holder.pPrice.setText("" + price * items);
        // holder.tv_reward.setText("" + reward * items);


    }

    private void updateMultiply(int pos, int i) {
        HashMap<String, String> map = new HashMap<>();
//            map.put("varient_id",cartList.get(position).getpId());
        map.put("varient_id", cartList.get(pos).getVarient_id());
        map.put("product_name", cartList.get(pos).getpNAme());
        map.put("category_id", cartList.get(pos).getpId());
        map.put("title", cartList.get(pos).getpNAme());
        map.put("price", cartList.get(pos).getpPrice());
        map.put("mrp", cartList.get(pos).getpMrp());
//        Log.d("fd", cartList.get(pos).getpImage());
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
                SharedPreferences preferences = context.getSharedPreferences("GOGrocer", Context.MODE_PRIVATE);
                preferences.edit().putInt("cardqnty", dbcart.getCartCount()).apply();
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d("qwer", e.toString());
        }
    }

    @Override
    public int getItemCount() {

        if (cartList.size() > limit) {
            return limit;
        } else {
            return cartList.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
//                    updateMultiply();
//
//                    if (Integer.parseInt(txtQuan.getText().toString()) == 1) {
//                       /* minus.setClickable(false);
//                        minus.setFocusable(false);*/
//                    } else if (Integer.parseInt(txtQuan.getText().toString()) > 1) {
//                        minus.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                decreaseInteger();
//                                updateMultiply();
//                            }
//                        });
//                    }
//
//                }
//
//            });
            //  minus.setOnClickListener(this);
            //   plus.setOnClickListener(this);


        }


//        private void updateMultiply() {
//            int position = getAdapterPosition();
//            HashMap<String, String> map = new HashMap<>();
//            map.put("product_id",cartList.get(position).getpId());
//            map.put("varient_id",cartList.get(position).getVarient_id());
//            map.put("product_name",cartList.get(position).getpNAme());
//            map.put("category_id",cartList.get(position).getpId());
//
//            map.put("title",cartList.get(position).getpDes());
//            map.put("price",cartList.get(position).getpPrice());
//            map.put("deal_price",cartList.get(position).getpMrp());
//            map.put("product_image",cartList.get(position).getpImage());
//            map.put("status",cartList.get(position).getStatus());
//            map.put("in_stock",cartList.get(position).getIn_stock());
//            map.put("unit_value",cartList.get(position).getpQuan());
//            map.put("unit",cartList.get(position).getUnit());
//            map.put("increament","0");
//            map.put("rewards","0");
//            map.put("stock","0");
//            map.put("product_description","0");
//
//            Log.d("fgh",cartList.get(position).getpDes());
//            Log.d("fghfgh",cartList.get(position).getpPrice());
//             /*   map.put("start_date", cartList.get(position).getStart_date());
//                map.put("start_time", cartList.get(position).getStart_time());
//                map.put("end_date", cartList.get(position).getEnd_date());
//                map.put("end_time", cartList.get(position).getEnd_time());*/
//            if (!txtQuan.getText().toString().equalsIgnoreCase("0")) {
//                if (dbcart.isInCart(map.get("varient_id"))) {
//                    dbcart.setCart(map, Integer.valueOf(txtQuan.getText().toString()));
//                    Log.d("sdf", "update");
//                    //  Toast.makeText(context, "Product quantity is updated in your cart", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    dbcart.setCart(map, Integer.valueOf(txtQuan.getText().toString()));
//                    //   Toast.makeText(context, "Product quantity is added in your cart", Toast.LENGTH_SHORT).show();
//
//                }
//            } else {
//                dbcart.removeItemFromCart(map.get("varient_id"));
//            }
//            try {
//                int items = (int) Double.parseDouble(dbcart.getInCartItemQty(map.get("varient_id")));
//                Double price = Double.parseDouble(map.get("price").trim());
//                Double mrp = Double.parseDouble(map.get("deal_price").trim());
//
//                //  Double reward = Double.parseDouble(map.get("rewards"));
//                // tv_reward.setText("" + reward * items);
//                pDescrptn.setText(""+cartList.get(position).getpDes());
//                pPrice.setText("" +price* items);
//                txtQuan.setText("" + items);
//                pMrp.setText("" + mrp* items );
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    //  ((MainActivity) context).setCartCounter("" + dbcart.getCartCount());
//                }
//            }catch (IndexOutOfBoundsException e){
//                e.toString();
//                Log.d("qwer",e.toString());
//            }
//        }
//
//        public void increaseInteger() {
//            minteger = minteger + 1;
//            display(minteger);
//        }
//
//        public void decreaseInteger() {
//            if (minteger == 1) {
//                minteger = 1;
//                display(minteger);
//                ll_addQuan.setVisibility(View.GONE);
//                btn_Add.setVisibility(View.VISIBLE);
//            } else {
//                minteger = minteger - 1;
//                display(minteger);
//
//            }
//        }
//
//        private void display(Integer number) {
//
//            txtQuan.setText("" + number);
//        }


        @Override
        public void onClick(View view) {

        }
    }


}

