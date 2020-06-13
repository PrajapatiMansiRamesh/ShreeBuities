package com.tecmanic.gogrocer.Adapters;

import android.content.Context;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tecmanic.gogrocer.ModelClass.timing_model;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.ForClicktimings;

import java.util.List;


public class Timing_Adapter extends RecyclerView.Adapter<Timing_Adapter.MyViewHolder> {

    Context context;

    boolean showingfirst = true;
    int myPos = 0;

    String timeslot;
    private List<timing_model> OfferList;
    private int lastSelectedPosition = -1;
    ForClicktimings forClicktimings;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RadioButton time;

        public MyViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.time);
        }

    }


    public Timing_Adapter(Context context, List<timing_model> offerList, ForClicktimings forClicktimings) {
        this.OfferList = offerList;
        this.context = context;
        this.forClicktimings = forClicktimings;
    }

    @NonNull
    @Override
    public Timing_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lay_time, parent, false);
        return new Timing_Adapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull final Timing_Adapter.MyViewHolder holder, final int position) {
        final timing_model lists = OfferList.get(position);

        holder.time.setText(lists.getTiming());


        if (myPos == position) {
            timeslot = lists.getTiming();

            holder.time.setTextColor(Color.parseColor("#FE8100"));
//            holder.linear.setBackgroundResource(R.drawable.blue_dateday_rect);
        } else {

            holder.time.setTextColor(Color.parseColor("#8f909e"));
//            holder.linear.setBackgroundResource(R.drawable.gray_dateday_rect);
        }
        holder.time.setChecked(lastSelectedPosition==position);

        holder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                myPos = position;

                Log.e("TimingAdapter", "Timeslot: " + OfferList.get(position).getTiming());
                lastSelectedPosition = position;
                notifyDataSetChanged();

                forClicktimings.getTimeSlot(OfferList.get(position).getTiming());


            }
        });


    }

    public String gettimeslot() {
        return timeslot;
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}




