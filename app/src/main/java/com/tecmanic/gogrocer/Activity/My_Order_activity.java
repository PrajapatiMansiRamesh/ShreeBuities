package com.tecmanic.gogrocer.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.tecmanic.gogrocer.Adapters.PagerOrderAdapter;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.ModelClass.NewPastOrderSubModel;
import com.tecmanic.gogrocer.ModelClass.NewPendingDataModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.ForReorderListner;
import com.tecmanic.gogrocer.util.LocaleHelper;
import com.tecmanic.gogrocer.util.MyPendingReorderClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tecmanic.gogrocer.Config.BaseURL.KEY_MOBILE;


public class My_Order_activity extends AppCompatActivity {

    Toolbar toolbar;
    int padding = 0;
    ImageView back_button;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String moblie;
    ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__oreder_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Order");
        progressDialog=new ProgressDialog(this);

        toolbar.setNavigationOnClickListener(v -> {
//            Intent intent = new Intent(My_Order_activity.this, MainActivity.class);
//            startActivity(intent);
//            overridePendingTransition(0,0);
            finish();

        });

        sharedPreferences = getSharedPreferences(BaseURL.MyPrefreance,MODE_PRIVATE);
        moblie=sharedPreferences.getString(KEY_MOBILE ,null);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.addTab(tabLayout.newTab().setText("Past Order"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerOrderAdapter adapter = new PagerOrderAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), pastOrderSubModelArrayList -> {
            Intent backresult = new Intent();
            backresult.putExtra("actIdentfy","past");
            backresult.putExtra("datalist", pastOrderSubModelArrayList);
            setResult(4,backresult);
            finish();
        }, models -> {
            Intent backresult = new Intent();
            backresult.putExtra("actIdentfy","pending");
            backresult.putExtra("datalist", models);
            setResult(4,backresult);
            finish();
        });
        viewPager.setAdapter(adapter);
        wrapTabIndicatorToTitle(tabLayout,80,80);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        overridePendingTransition(0,0);
        super.onDestroy();
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent=new Intent(My_Order_activity.this,MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    public void wrapTabIndicatorToTitle(TabLayout tabLayout, int externalMargin, int internalMargin) {
        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            int childCount = ((ViewGroup) tabStrip).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View tabView = tabStripGroup.getChildAt(i);
                tabView.setMinimumWidth(0);
                tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    if (i == 0) {
                        setMargin(layoutParams, externalMargin, internalMargin);
                    } else if (i == childCount - 1) {
                        setMargin(layoutParams, internalMargin, externalMargin);
                    } else {
                        setMargin(layoutParams, internalMargin, internalMargin);
                    }
                }
            }

            tabLayout.requestLayout();
        }
    }

    private void setMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(start);
            layoutParams.setMarginEnd(end);
        } else {
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        }
    }

}

