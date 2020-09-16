package com.tecmanic.gogrocer.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tecmanic.gogrocer.ModelClass.NewPendingOrderModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.ForReorderListner;
import com.tecmanic.gogrocer.util.Session_management;
import com.tecmanic.gogrocer.util.TodayOrderClickListner;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class My_Past_Order_adapter extends RecyclerView.Adapter<My_Past_Order_adapter.MyViewHolder> {

    SharedPreferences preferences, valuepref;
    SharedPreferences.Editor editor;
    String Used_Wallet_amount;
    ForReorderListner reorderListner;
    private List<NewPendingOrderModel> modelList;
    private LayoutInflater inflater;
    private Fragment currentFragment;
    private Context context;
    private String getuser_id = "";
    private Session_management session_management;
    private TodayOrderClickListner todayOrderClickListner;


//    public My_Past_Order_adapter(Context context, List<My_Past_order_model> modemodelList, final Fragment currentFragment) {
//
//        this.context = context;
//        this.modelList = modelList;
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.currentFragment = currentFragment;
//    }

    public My_Past_Order_adapter(List<NewPendingOrderModel> modelList, TodayOrderClickListner todayOrderClickListner) {
        this.modelList = modelList;
        this.todayOrderClickListner = todayOrderClickListner;
    }


//    public My_Past_Order_adapter(List<NewPendingOrderModel> modelList, ForReorderListner reorderListner) {
//        this.modelList = modelList;
//        this.reorderListner = reorderListner;
//    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_my_past_order_rv, parent, false);
        context = parent.getContext();
        session_management = new Session_management(context);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final My_Past_Order_adapter.MyViewHolder holder, int position) {
        final NewPendingOrderModel mList = modelList.get(position);

        holder.tv_orderno.setText(mList.getCart_id());
        holder.canclebtn.setVisibility(View.GONE);
        holder.reorder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todayOrderClickListner.onReorderClick(position, "past");
            }
        });

        if (mList.getOrder_status().equalsIgnoreCase("Completed")) {
            holder.relative_background.setCardBackgroundColor(getColor(0, 128, 0));
            holder.relativetextstatus.setText("Completed");
            holder.l1.setVisibility(View.VISIBLE);
            holder.reorder_btn.setVisibility(View.VISIBLE);
            holder.Confirm.setVisibility(View.GONE);
            holder.Out_For_Deliverde.setVisibility(View.GONE);
            holder.Delivered.setVisibility(View.GONE);
            holder.Confirm1.setVisibility(View.VISIBLE);
            holder.Out_For_Deliverde1.setVisibility(View.VISIBLE);
            holder.Delivered1.setVisibility(View.VISIBLE);

        } else if (mList.getOrder_status().equalsIgnoreCase("Pending")) {
            holder.relativetextstatus.setText("Pending");
            holder.l1.setVisibility(View.VISIBLE);
            holder.reorder_btn.setVisibility(View.VISIBLE);
            holder.Confirm.setVisibility(View.VISIBLE);
            holder.Out_For_Deliverde.setVisibility(View.VISIBLE);
            holder.Delivered.setVisibility(View.VISIBLE);
            holder.Confirm1.setVisibility(View.GONE);
            holder.Out_For_Deliverde1.setVisibility(View.GONE);
            holder.Delivered1.setVisibility(View.GONE);

        } else if (mList.getOrder_status().equalsIgnoreCase("Confirmed")) {
            holder.relativetextstatus.setText("Confirmed");
            holder.l1.setVisibility(View.VISIBLE);
            holder.reorder_btn.setVisibility(View.VISIBLE);
            holder.Confirm.setVisibility(View.GONE);
            holder.Out_For_Deliverde.setVisibility(View.VISIBLE);
            holder.Delivered.setVisibility(View.VISIBLE);
            holder.Confirm1.setVisibility(View.VISIBLE);
            holder.Out_For_Deliverde1.setVisibility(View.GONE);
            holder.Delivered1.setVisibility(View.GONE);
        } else if (mList.getOrder_status().equalsIgnoreCase("Out_For_Delivery")) {
            holder.relativetextstatus.setText("Out For Delivery");
            holder.reorder_btn.setVisibility(View.VISIBLE);
            holder.l1.setVisibility(View.VISIBLE);
            holder.Confirm.setVisibility(View.GONE);
            holder.Out_For_Deliverde.setVisibility(View.GONE);
            holder.Delivered.setVisibility(View.VISIBLE);
            holder.Confirm1.setVisibility(View.VISIBLE);
            holder.Out_For_Deliverde1.setVisibility(View.VISIBLE);
            holder.Delivered1.setVisibility(View.GONE);
        } else if (mList.getOrder_status().equalsIgnoreCase("Cancelled")) {
            holder.relative_background.setCardBackgroundColor(getColor(255, 0, 0));
            holder.relativetextstatus.setText("Cancelled");
            holder.reorder_btn.setVisibility(View.GONE);
            holder.l1.setVisibility(View.GONE);
        }

        if (mList.getPayment_status() == null) {
            holder.tv_status.setText("Payment:-" + " " + "Pending");
        } else {
            if (mList.getPayment_status().equalsIgnoreCase("success") || mList.getPayment_status().equalsIgnoreCase("failed") || mList.getPayment_status().equalsIgnoreCase("COD")) {
                holder.tv_status.setText("Payment:-" + " " + mList.getPayment_status());
            }
        }

        if (mList.getPaid_by_wallet() != null && !mList.getPaid_by_wallet().equalsIgnoreCase("") && !mList.getPaid_by_wallet().equalsIgnoreCase("0")) {
            holder.wallet_layout.setVisibility(View.VISIBLE);
            holder.tv_wallet_amount.setText("- " + session_management.getCurrency() + "" + mList.getPaid_by_wallet());
        } else {
            holder.wallet_layout.setVisibility(View.GONE);
        }

        if (mList.getCoupon_discount() != null && !mList.getCoupon_discount().equalsIgnoreCase("") && !mList.getCoupon_discount().equalsIgnoreCase("0")) {
            holder.coupon_layout.setVisibility(View.VISIBLE);
            holder.tv_coupon_amount.setText("- " + session_management.getCurrency() + "" + mList.getCoupon_discount());
        } else {
            holder.coupon_layout.setVisibility(View.GONE);
        }

        if (mList.getDel_charge() != null && !mList.getDel_charge().equalsIgnoreCase("")) {
            holder.tv_delivery_amount.setText(session_management.getCurrency() + "" + mList.getDel_charge());
            holder.tv_order_price_2.setText(session_management.getCurrency() + "" + ((int) (Double.parseDouble(mList.getPrice()) - Double.parseDouble(mList.getDel_charge()))));
        } else {
            holder.tv_order_price_2.setText(session_management.getCurrency() + "" + mList.getPrice());
            holder.tv_delivery_amount.setText(session_management.getCurrency() + " 0");
        }

        holder.info_price.setOnClickListener(v -> {
            if (holder.price_deatils.getVisibility() == View.VISIBLE) {
                holder.price_deatils.setVisibility(View.GONE);
            } else {
                holder.price_deatils.setVisibility(View.VISIBLE);
            }
        });

        holder.tv_pending_date.setText(mList.getDelivery_date());
        holder.tv_confirm_date.setText(mList.getDelivery_date());
        holder.tv_delevered_date.setText(mList.getDelivery_date());
        holder.tv_cancel_date.setText(mList.getDelivery_date());
        holder.tv_methid1.setText(mList.getPayment_method());
        holder.tv_date.setText(mList.getDelivery_date());
        holder.tv_tracking_date.setText(mList.getDelivery_date());

        preferences = context.getSharedPreferences("lan", MODE_PRIVATE);
        String language = preferences.getString("language", "");
        if (language.contains("spanish")) {
            String timefrom = mList.getTime_slot();
            timefrom = timefrom.replace("pm", "م");
            timefrom = timefrom.replace("am", "ص");
            holder.tv_time.setText(timefrom);
        } else {
            holder.tv_time.setText(mList.getTime_slot());
        }

        holder.tv_price.setText(session_management.getCurrency() + "" + mList.getPrice());
        if (mList.getRemaining_amount() != null && !mList.getRemaining_amount().equalsIgnoreCase("")) {
            holder.tv_pay_ableamount.setText(session_management.getCurrency() + "" + mList.getRemaining_amount());
            holder.tv_total_pay.setText(session_management.getCurrency() + "" + mList.getRemaining_amount());
        } else {
            holder.tv_pay_ableamount.setText(session_management.getCurrency() + "" + mList.getPrice());
        }
        holder.tv_item.setText(context.getResources().getString(R.string.tv_cart_item) + mList.getData().size());
        holder.tv_pending_date.setText(mList.getDelivery_date());
        holder.tv_confirm_date.setText(mList.getDelivery_date());
        holder.tv_delevered_date.setText(mList.getDelivery_date());
        holder.tv_cancel_date.setText(mList.getDelivery_date());

        holder.order_details.setOnClickListener(view -> {
            todayOrderClickListner.onClickForOrderDetails(position, "past");
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public int getColor(int r, int g, int b) {
        return Color.rgb(r, g, b);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_orderno, tv_status, tv_date, tv_time, tv_price, tv_item, relativetextstatus, tv_tracking_date;
        public TextView tv_pending_date, tv_pending_time, tv_confirm_date, tv_confirm_time, tv_delevered_date, tv_delevered_time, tv_cancel_date, tv_cancel_time;
        public TextView tv_methid1;
        public TextView tv_pay_ableamount, tv_order_price_2, tv_wallet_amount, tv_coupon_amount, tv_delivery_amount, tv_total_pay;
        public View view1, view2, view3, view4, view5, view6;
        public CardView relative_background;
        public LinearLayout rr, price_deatils;
        public CircleImageView Confirm, Out_For_Deliverde, Delivered;
        public CircleImageView Confirm1, Out_For_Deliverde1, Delivered1;
        public ImageView info_price;
        CardView cardView;
        TextView canclebtn, reorder_btn, order_details;
        LinearLayout wallet_layout, coupon_layout, delivery_layout;
        LinearLayout btn_lay;
        private LinearLayout l1;


        public MyViewHolder(View view) {
            super(view);
            tv_orderno = view.findViewById(R.id.tv_order_no);

            tv_pay_ableamount = view.findViewById(R.id.tv_pay_ableamount);
            tv_order_price_2 = view.findViewById(R.id.tv_order_price_2);
            tv_coupon_amount = view.findViewById(R.id.tv_coupon_amount);
            tv_total_pay = view.findViewById(R.id.tv_total_pay);
            tv_delivery_amount = view.findViewById(R.id.tv_delivery_amount);
            tv_wallet_amount = view.findViewById(R.id.tv_wallet_amount);
            delivery_layout = view.findViewById(R.id.delivery_layout);
            wallet_layout = view.findViewById(R.id.wallet_layout);
            coupon_layout = view.findViewById(R.id.coupon_layout);
            price_deatils = view.findViewById(R.id.price_deatils);
            info_price = view.findViewById(R.id.info_price);
            canclebtn = view.findViewById(R.id.canclebtn);
            order_details = view.findViewById(R.id.order_details);
            tv_status = view.findViewById(R.id.tv_order_status);
            relativetextstatus = view.findViewById(R.id.status);
            tv_tracking_date = view.findViewById(R.id.tracking_date);
            tv_date = view.findViewById(R.id.tv_order_date);
            tv_time = view.findViewById(R.id.tv_order_time);
            tv_price = view.findViewById(R.id.tv_order_price);
            tv_item = view.findViewById(R.id.tv_order_item);
            cardView = view.findViewById(R.id.card_view);
            l1 = view.findViewById(R.id.l1);
            reorder_btn = view.findViewById(R.id.reorder_btn);


//            //Payment Method
            tv_methid1 = view.findViewById(R.id.method1);
            //Date And Time
            tv_pending_date = view.findViewById(R.id.pending_date);
//            tv_pending_time = (TextView) view.findViewById(R.id.pending_time);
            tv_confirm_date = view.findViewById(R.id.confirm_date);
//            tv_confirm_time = (TextView) view.findViewById(R.id.confirm_time);
            tv_delevered_date = view.findViewById(R.id.delevered_date);
//            tv_delevered_time = (TextView) view.findViewById(R.id.delevered_time);
            tv_cancel_date = view.findViewById(R.id.cancel_date);
//            tv_cancel_time = (TextView) view.findViewById(R.id.cancel_time);
            //Oredre Tracking
            view1 = view.findViewById(R.id.view1);
            view2 = view.findViewById(R.id.view2);
            view3 = view.findViewById(R.id.view3);
            view4 = view.findViewById(R.id.view4);
            view5 = view.findViewById(R.id.view5);
            view6 = view.findViewById(R.id.view6);
            relative_background = view.findViewById(R.id.relative_background);

            Confirm = view.findViewById(R.id.confirm_image);
            Out_For_Deliverde = view.findViewById(R.id.delivered_image);
            Delivered = view.findViewById(R.id.cancal_image);
            Confirm1 = view.findViewById(R.id.confirm_image1);
            Out_For_Deliverde1 = view.findViewById(R.id.delivered_image1);
            Delivered1 = view.findViewById(R.id.cancal_image1);

        }
    }
}