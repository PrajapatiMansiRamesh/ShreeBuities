package com.tecmanic.gogrocer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.ModelClass.NewPendingDataModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.Session_management;

import java.util.List;


/**
 * Created by Rajesh Dabhi on 30/6/2017.
 */

public class My_order_detail_adapter extends RecyclerView.Adapter<My_order_detail_adapter.MyViewHolder> {

    private List<NewPendingDataModel> modelList;
    private Context context;
    private Session_management session_management;

    public My_order_detail_adapter(List<NewPendingDataModel> modelList) {
        this.modelList = modelList;
    }

    @Override
    public My_order_detail_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_order_detail_rv, parent, false);

        context = parent.getContext();
        session_management = new Session_management(context);
        return new My_order_detail_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(My_order_detail_adapter.MyViewHolder holder, int position) {
        NewPendingDataModel mList = modelList.get(position);

        Glide.with(context)
                .load(BaseURL.IMG_URL + mList.getVarient_image())
                .centerCrop()
                .placeholder(R.drawable.splashicon)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.iv_img);

        if (mList.getDescription() != null && !mList.getDescription().equalsIgnoreCase("")) {
            holder.tv_order_descrp.setVisibility(View.VISIBLE);
            holder.tv_order_descrp.setText(mList.getDescription());
        } else {
            holder.tv_order_descrp.setVisibility(View.GONE);
        }

        holder.tv_title.setText(mList.getProduct_name());
        holder.tv_price.setText(session_management.getCurrency() + " " + mList.getPrice());
        holder.txtQty.setText("Qty - " + "" + mList.getQuantity() + "" + mList.getUnit());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title, tv_price, tv_qty, txtQty, tv_order_descrp;
        public ImageView iv_img;

        public MyViewHolder(View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_order_Detail_title);
            tv_price = view.findViewById(R.id.tv_order_Detail_price);
//            tv_qty = (TextView) view.findViewById(R.id.tv_order_Detail_qty);
            tv_order_descrp = view.findViewById(R.id.tv_order_descrp);
            txtQty = view.findViewById(R.id.txtQty);
            iv_img = view.findViewById(R.id.iv_order_detail_img);

        }
    }

}