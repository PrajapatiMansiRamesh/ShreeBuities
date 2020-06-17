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
        TextView slotname;

        public MyViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.time);
            slotname = view.findViewById(R.id.slotname);
        }

    }


    public Timing_Adapter(Context context, List<timing_model> offerList, ForClicktimings forClicktimings) {
        this.OfferList = offerList;
        this.context = context;
        this.forClicktimings = forClicktimings;
        lastSelectedPosition =0;
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


        if (lastSelectedPosition == position) {
            timeslot = lists.getTiming();
            holder.time.setTextColor(Color.parseColor("#FE8100"));
            holder.slotname.setTextColor(Color.parseColor("#FE8100"));
        } else {
            holder.time.setTextColor(Color.parseColor("#8f909e"));
            holder.slotname.setTextColor(Color.parseColor("#8f909e"));
        }
        holder.time.setChecked(lastSelectedPosition==position);

        holder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    lastSelectedPosition = position;
                    notifyDataSetChanged();
                    forClicktimings.getTimeSlot(OfferList.get(lastSelectedPosition).getTiming());
                }catch (IndexOutOfBoundsException ex){
                    ex.printStackTrace();
                }



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




