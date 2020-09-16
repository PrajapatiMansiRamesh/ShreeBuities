package com.tecmanic.gogrocer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tecmanic.gogrocer.ModelClass.MainScreenList;
import com.tecmanic.gogrocer.R;

import java.util.List;

public class MainScreenAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<MainScreenList> screenLists;

    public MainScreenAdapter(Context context, List<MainScreenList> screenLists) {
        this.context = context;
        this.screenLists = screenLists;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topsell, parent, false);
                return new TopSelling(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topsell, parent, false);
                return new RecentDeal(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topsell, parent, false);
                return new DealOfTheDay(view);
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topsell, parent, false);
                return new WhatsNew(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MainScreenList mainScreenList = screenLists.get(position);

        switch (mainScreenList.getViewType()) {
            case "TOP SELLING":
                TopSelling topSelling = (TopSelling) holder;
                LinearLayoutManager linearLayoutManagers = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                topSelling.topSelling.setLayoutManager(linearLayoutManagers);
                topSelling.topSelling.setAdapter(new CartAdapter(context, mainScreenList.getTopSelling()));
                break;
            case "RECENT SELLING":
                RecentDeal recentDeal = (RecentDeal) holder;
                LinearLayoutManager linearLayoutManagerss = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                recentDeal.topSelling.setLayoutManager(linearLayoutManagerss);
                recentDeal.topSelling.setAdapter(new CartAdapter(context, mainScreenList.getRecentSelling()));
                break;
            case "DEALS OF THE DAY":
                DealOfTheDay dealOfTheDay = (DealOfTheDay) holder;
                LinearLayoutManager dealofter = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                dealOfTheDay.topSelling.setLayoutManager(dealofter);
                dealOfTheDay.topSelling.setAdapter(new Deal_Adapter(context, mainScreenList.getDealoftheday()));
                break;
            case "WHAT'S NEW":
                WhatsNew whatsNew = (WhatsNew) holder;
                LinearLayoutManager linearLayoutManagersss = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                whatsNew.topSelling.setLayoutManager(linearLayoutManagersss);
                whatsNew.topSelling.setAdapter(new CartAdapter(context, mainScreenList.getWhatsNew()));
                break;
            default:
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        switch (screenLists.get(position).getViewType()) {
            case "TOP SELLING":
                return 0;
            case "RECENT SELLING":
                return 1;
            case "DEALS OF THE DAY":
                return 2;
            case "WHAT'S NEW":
                return 3;
            default:
                return super.getItemViewType(position);
        }

    }

    @Override
    public int getItemCount() {
        return screenLists.size();
    }

    public class TopSelling extends RecyclerView.ViewHolder {
        public RecyclerView topSelling;

        public TopSelling(@NonNull View itemView) {
            super(itemView);
            topSelling = itemView.findViewById(R.id.top_selling);
        }
    }

    public class WhatsNew extends RecyclerView.ViewHolder {
        public RecyclerView topSelling;

        public WhatsNew(@NonNull View itemView) {
            super(itemView);
            topSelling = itemView.findViewById(R.id.top_selling);
        }
    }

    public class RecentDeal extends RecyclerView.ViewHolder {
        public RecyclerView topSelling;

        public RecentDeal(@NonNull View itemView) {
            super(itemView);
            topSelling = itemView.findViewById(R.id.top_selling);
        }
    }

    public class DealOfTheDay extends RecyclerView.ViewHolder {
        public RecyclerView topSelling;

        public DealOfTheDay(@NonNull View itemView) {
            super(itemView);
            topSelling = itemView.findViewById(R.id.top_selling);
        }
    }
}
