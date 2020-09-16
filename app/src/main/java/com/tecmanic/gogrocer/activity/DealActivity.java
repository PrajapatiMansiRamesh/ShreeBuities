package com.tecmanic.gogrocer.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tecmanic.gogrocer.Adapters.Deal_Adapter;
import com.tecmanic.gogrocer.Adapters.ViewAll_Adapter;
import com.tecmanic.gogrocer.ModelClass.NewCartModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tecmanic.gogrocer.Config.BaseURL.HomeDeal;

public class DealActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ViewAll_Adapter topSellingAdapter;
    String catId, catName;
    private ShimmerRecyclerView rv_top_selling;
    private List<NewCartModel> dealList = new ArrayList<>();
    private String action_name;
    private Deal_Adapter dealAdapter;
    private Session_management session_management;
    private DatabaseHandler dbcart;
    private LinearLayout bottom_lay_total;
    private TextView total_count;
    private TextView total_price;
    private TextView continue_tocart;
    private ImageView back_btn;
    private boolean invalue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
//        action_name = (String) Objects.requireNonNull(getIntent().getExtras()).get("action_name");
        session_management = new Session_management(DealActivity.this);
        rv_top_selling = findViewById(R.id.recyclerTopSelling);
        progressDialog = new ProgressDialog(DealActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        dbcart = new DatabaseHandler(DealActivity.this);
        bottom_lay_total = findViewById(R.id.bottom_lay_total);
        total_price = findViewById(R.id.total_price);
        back_btn = findViewById(R.id.back_btn);
        total_count = findViewById(R.id.total_count);
        continue_tocart = findViewById(R.id.continue_tocart);

        if (dbcart.getCartCount() > 0) {
            bottom_lay_total.setVisibility(View.VISIBLE);
            total_price.setText(session_management.getCurrency() + " " + dbcart.getTotalAmount());
            total_count.setText("Total Items (" + dbcart.getCartCount() + ")");
        } else {
            bottom_lay_total.setVisibility(View.GONE);
        }
        back_btn.setOnClickListener(v -> {
            invalue = false;
            onBackPressed();
        });

        if (isOnline()) {
            progressDialog.show();
            dealUrl();
        }

        continue_tocart.setOnClickListener(v -> {
            invalue = true;
            onBackPressed();
        });


    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void dealUrl() {
        dealList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HomeDeal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("HomeDeal", response);
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equals("1")) {
//                        JSONArray jsonArray = jsonObject.getJSONArray("data");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                            String product_id = jsonObject1.getString("product_id");
//                            String varient_id = jsonObject1.getString("varient_id");
//                            String product_name = jsonObject1.getString("product_name");
//                            String description= jsonObject1.getString("description");
//                            String pprice= jsonObject1.getString("price");
//                            String quantity = jsonObject1.getString("quantity");
//                            String product_image = jsonObject1.getString("varient_image");
//                            String mmrp = jsonObject1.getString("mrp");
//                            String unit = jsonObject1.getString("unit");
//                            Long count = jsonObject1.getLong("timediff");
//                            String storeId = jsonObject1.getString("store_id");
//                            String totalOff= String.valueOf(Integer.parseInt(String.valueOf(mmrp))-Integer.parseInt(String.valueOf(pprice)));
//
//                            CartModel recentData= new CartModel(product_id,product_name,description,pprice,quantity+" "+unit,product_image,session_management.getCurrency()+totalOff+" "+"Off",mmrp," ",unit,storeId);
//                            recentData.setVarient_id(varient_id);
//                            recentData.setHoursmin(count);
//                            dealList.add(recentData);
//
//                        }

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<NewCartModel>>() {
                        }.getType();
                        List<NewCartModel> listorl = gson.fromJson(jsonObject.getString("data"), listType);
                        dealList.addAll(listorl);

                        dealAdapter = new Deal_Adapter(DealActivity.this, dealList, () -> {
                            if (dbcart.getCartCount() > 0) {
                                bottom_lay_total.setVisibility(View.VISIBLE);
                                total_price.setText(session_management.getCurrency() + " " + dbcart.getTotalAmount());
                                total_count.setText("Total Items (" + dbcart.getCartCount() + ")");
                            } else {
                                bottom_lay_total.setVisibility(View.GONE);
                            }
                        });
                        rv_top_selling.setLayoutManager(new LinearLayoutManager(DealActivity.this, RecyclerView.VERTICAL, false));
                        rv_top_selling.setAdapter(dealAdapter);
                        dealAdapter.notifyDataSetChanged();

                    } else {
//                        shimmerRecyclerView.setVisibility(View.GONE);
//                        viewall.setVisibility(View.GONE);
//                        no_data.setVisibility(View.VISIBLE);
//                        JSONObject resultObj = jsonObject.getJSONObject("results");
//                        String msg = resultObj.getString("message");
                        Toast.makeText(DealActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("lat", session_management.getLatPref());
                params.put("lng", session_management.getLangPref());
                params.put("city", session_management.getLocationCity());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(DealActivity.this);
        requestQueue.getCache().clear();
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("carttogo",invalue);
        setResult(56,intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
//        super.onBackPressed();
    }
}
