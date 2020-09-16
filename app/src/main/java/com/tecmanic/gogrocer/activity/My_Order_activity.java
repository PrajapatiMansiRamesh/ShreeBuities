package com.tecmanic.gogrocer.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tecmanic.gogrocer.Adapters.OrderAdapter;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.ModelClass.ListAssignAndUnassigned;
import com.tecmanic.gogrocer.ModelClass.NewPendingOrderModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.CustomVolleyJsonArrayRequest;
import com.tecmanic.gogrocer.util.LocaleHelper;
import com.tecmanic.gogrocer.util.Session_management;
import com.tecmanic.gogrocer.util.TodayOrderClickListner;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tecmanic.gogrocer.Config.BaseURL.KEY_MOBILE;
import static com.tecmanic.gogrocer.Config.BaseURL.OrderDoneUrl;
import static com.tecmanic.gogrocer.Config.BaseURL.PendingOrderUrl;


public class My_Order_activity extends AppCompatActivity {

    Toolbar toolbar;
    int padding = 0;
    ImageView back_button;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String moblie;
    ProgressDialog progressDialog;
    private String numberPhone = "";

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipe_to;
    private List<ListAssignAndUnassigned> listAssignAndUnassigneds = new ArrayList<>();
    private List<NewPendingOrderModel> movieList = new ArrayList<>();
    private List<NewPendingOrderModel> nextdayOrderModels = new ArrayList<>();
    private OrderAdapter adapter;
    private Session_management sessionManagement;
    private String user_id = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__oreder_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("My Order");
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while loading!");
        progressDialog.setCancelable(false);

        toolbar.setNavigationOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        });

        sharedPreferences = getSharedPreferences(BaseURL.MyPrefreance, MODE_PRIVATE);
        moblie = sharedPreferences.getString(KEY_MOBILE, null);

        sessionManagement = new Session_management(My_Order_activity.this);
        user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.viewpager);

        tabLayout.addTab(tabLayout.newTab().setText("Pending Order"));
        tabLayout.addTab(tabLayout.newTab().setText("Past Order"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        swipe_to = findViewById(R.id.swipe_to);
        tabLayout.addTab(tabLayout.newTab().setText("Pending Orders"));
        tabLayout.addTab(tabLayout.newTab().setText("Past Order"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        listAssignAndUnassigneds.add(new ListAssignAndUnassigned("assigned", movieList, nextdayOrderModels));
        listAssignAndUnassigneds.add(new ListAssignAndUnassigned("unassigned", movieList, nextdayOrderModels));
        adapter = new OrderAdapter(My_Order_activity.this, listAssignAndUnassigneds, new TodayOrderClickListner() {

            @Override
            public void onCallToDeliveryBoy(String number) {
                if (isPermissionGranted()) {
                    callAction(number);
                }
            }

            @Override
            public void onClickForOrderDetails(int position, String viewType) {
                if (viewType.equalsIgnoreCase("pending")) {
                    String sale_id = movieList.get(position).getCart_id();
                    String date = movieList.get(position).getDelivery_date();
                    String time = movieList.get(position).getTime_slot();
                    String total = movieList.get(position).getPrice();
                    String status = movieList.get(position).getOrder_status();
                    String deli_charge = movieList.get(position).getDel_charge();
                    Intent intent = new Intent(My_Order_activity.this, Myorderdetails.class);
                    intent.putExtra("sale_id", sale_id);
                    intent.putExtra("pastorder", "false");
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    intent.putExtra("total", total);
                    intent.putExtra("status", status);
                    intent.putExtra("deli_charge", deli_charge);
                    intent.putExtra("data", movieList.get(position).getData());
                    startActivityForResult(intent, 13);
                } else if (viewType.equalsIgnoreCase("past")) {
                    String sale_id = nextdayOrderModels.get(position).getCart_id();
                    String date = nextdayOrderModels.get(position).getDelivery_date();
                    String time = nextdayOrderModels.get(position).getTime_slot();
                    String total = nextdayOrderModels.get(position).getPrice();
                    String status = nextdayOrderModels.get(position).getOrder_status();
                    String deli_charge = nextdayOrderModels.get(position).getDel_charge();
                    Intent intent = new Intent(My_Order_activity.this, Myorderdetails.class);
                    intent.putExtra("sale_id", sale_id);
                    intent.putExtra("pastorder", "true");
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    intent.putExtra("total", total);
                    intent.putExtra("status", status);
                    intent.putExtra("deli_charge", deli_charge);
                    intent.putExtra("data", nextdayOrderModels.get(position).getData());
                    startActivityForResult(intent, 13);
                }
            }

            @Override
            public void onReorderClick(int position, String viewType) {
                if (viewType.equalsIgnoreCase("pending")) {
                    Intent backresult = new Intent();
                    backresult.putExtra("actIdentfy", "pending");
                    backresult.putExtra("datalist", movieList.get(position).getData());
                    setResult(4, backresult);
                    onBackPressed();
                } else if (viewType.equalsIgnoreCase("past")) {
                    Intent backresult = new Intent();
                    backresult.putExtra("actIdentfy", "past");
                    backresult.putExtra("datalist", nextdayOrderModels.get(position).getData());
                    setResult(4, backresult);
                    onBackPressed();
                }
            }

            @Override
            public void onCancelClick(int position, String viewType) {
                showDeleteDialog(position);
            }
        });
        viewPager2.setAdapter(adapter);
        wrapTabIndicatorToTitle(tabLayout, 80, 80);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Pending Order");
            } else if (position == 1) {
                tab.setText("Past Order");
            }

        });
        tabLayoutMediator.attach();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
//                super.onPageSelected(position);
                viewPager2.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
//        viewPager.addon(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        swipe_to.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makePendingorderRequest(user_id);
            }
        });

        makePendingorderRequest(user_id);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(My_Order_activity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                    callAction(numberPhone);
                } else {
                    Toast.makeText(My_Order_activity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void callAction(String number) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            startActivity(callIntent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(My_Order_activity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        overridePendingTransition(0, 0);
        super.onDestroy();
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(My_Order_activity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
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

    private void makePendingorderRequest(String userid) {
        progressDialog.show();
        String tag_json_obj = "json_socity_req";
        movieList.clear();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userid);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                PendingOrderUrl, params, response -> {
            Log.d("TAG", response.toString());

            if (response != null && response.length() > 0) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<NewPendingOrderModel>>() {
                }.getType();

                List<NewPendingOrderModel> movieListt = gson.fromJson(response.toString(), listType);
                movieList.addAll(movieListt);
            }
            makePastOrder(userid);
        }, error -> {
            makePastOrder(userid);
            VolleyLog.d("TAG", "Error: " + error.getMessage());
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void makePastOrder(String userid) {
        nextdayOrderModels.clear();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userid);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                OrderDoneUrl, params, response -> {
            Log.d("asdf", response.toString());

            try {
                JSONObject object = response.getJSONObject(0);
                String data = object.getString("data");
                if (data.equals("no orders yet")) {

                } else {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<NewPendingOrderModel>>() {
                    }.getType();
                    List<NewPendingOrderModel> nextdayOrderModelss = gson.fromJson(response.toString(), listType);
                    nextdayOrderModels.addAll(nextdayOrderModelss);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                swipe_to.setRefreshing(false);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

        }, error -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            swipe_to.setRefreshing(false);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(My_Order_activity.this);
        requestQueue.getCache().clear();
        requestQueue.add(jsonObjReq);
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(My_Order_activity.this);
        alertDialog.setMessage(getResources().getString(R.string.cancle_order_note));
        alertDialog.setNegativeButton(getResources().getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {

            Intent intent = new Intent(My_Order_activity.this, Cancel_Order.class);
            intent.putExtra("cart_id", movieList.get(position).getCart_id());
            startActivityForResult(intent, 12);
            dialogInterface.dismiss();
        });

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12) {
            makePendingorderRequest(user_id);
        } else if (requestCode == 13) {
            makePendingorderRequest(user_id);
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }
}

