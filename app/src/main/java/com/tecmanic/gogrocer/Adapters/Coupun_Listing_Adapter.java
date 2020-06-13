package com.tecmanic.gogrocer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tecmanic.gogrocer.Activity.PaymentDetails;
import com.tecmanic.gogrocer.ModelClass.CoupunModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.CoupounClickListner;

import java.util.List;
import java.util.Map;

public class Coupun_Listing_Adapter extends RecyclerView.Adapter<Coupun_Listing_Adapter.MyViewHolder> {

    private List<CoupunModel> modelList;

    private Context context;
    private CoupounClickListner coupounClickListner;

    public Coupun_Listing_Adapter(List<CoupunModel> coupunModelList, CoupounClickListner coupounClickListner) {
        this.modelList = coupunModelList;
        this.coupounClickListner = coupounClickListner;
    }

    public void setList(List<CoupunModel> coupunModelList) {
        this.modelList = coupunModelList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView coupun_heading, discrip_coupon, copen_text, applybtn;
        LinearLayout linearLayout ;

        public MyViewHolder(View view) {
            super(view);

            coupun_heading = (TextView) view.findViewById(R.id.coupun_heading);
            discrip_coupon = (TextView) view.findViewById(R.id.discrip_coupon);
            linearLayout = view.findViewById(R.id.layout);

            copen_text = (TextView) view.findViewById(R.id.copen_text);

            applybtn = (TextView) view.findViewById(R.id.applybtn);


        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_couponlist, parent, false);
        context = parent.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        CoupunModel mList = modelList.get(position);

        holder.applybtn.setOnClickListener(v -> {
//            Intent intent = new Intent(context, PaymentDetails.class);
//            intent.putExtra("code",mList.getCoupon_code());
//            context.startActivity(intent);
            coupounClickListner.onClickApply(modelList.get(position).getCoupon_code());
        });
        holder.coupun_heading.setText(mList.getCoupon_name());
        holder.discrip_coupon.setText(mList.getCoupon_description());
        holder.copen_text.setText(mList.getCoupon_code());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}