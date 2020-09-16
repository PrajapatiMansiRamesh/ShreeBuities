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
import java.util.Objects;

import static com.tecmanic.gogrocer.Config.BaseURL.HomeDeal;
import static com.tecmanic.gogrocer.Config.BaseURL.HomeRecent;
import static com.tecmanic.gogrocer.Config.BaseURL.HomeTopSelling;
import static com.tecmanic.gogrocer.Config.BaseURL.whatsnew;

public class ViewAll_TopDeals extends AppCompatActivity {
    ProgressDialog progressDialog;
    ViewAll_Adapter topSellingAdapter;
    String catId, catName;
    private ShimmerRecyclerView rv_top_selling;
    private List<NewCartModel> topSellList = new ArrayList<>();
    private String action_name;
    private Session_management session_management;
    private ImageView back_btn;
    private DatabaseHandler dbcart;
    private LinearLayout bottom_lay_total;
    private TextView total_count;
    private TextView total_price;
    private TextView continue_tocart;
    private boolean invalue = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all__top_deals);

        action_name = (String) Objects.requireNonNull(getIntent().getExtras()).get("action_name");
        rv_top_selling = findViewById(R.id.recyclerTopSelling);
        back_btn = findViewById(R.id.back_btn);
        progressDialog = new ProgressDialog(ViewAll_TopDeals.this);
        session_management = new Session_management(ViewAll_TopDeals.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        dbcart = new DatabaseHandler(ViewAll_TopDeals.this);
        bottom_lay_total = findViewById(R.id.bottom_lay_total);
        total_price = findViewById(R.id.total_price);
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
            if (action_name.equalsIgnoreCase("Recent_Details_Fragment")) {
                topSellingUrl(HomeRecent);
            } else if (action_name.equalsIgnoreCase("Whats_New_Fragment")) {
                topSellingUrl(whatsnew);
            } else if (action_name.equalsIgnoreCase("Deals_Fragment")) {
                topSellingUrl(HomeDeal);
            } else if (action_name.equalsIgnoreCase("Top_Deals_Fragment")) {
                topSellingUrl(HomeTopSelling);
            }
        }

        continue_tocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invalue = true;
                onBackPressed();
            }
        });

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void topSellingUrl(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("HomeTopSelling", response);
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("1")) {
                    topSellList.clear();


                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<NewCartModel>>() {
                    }.getType();
                    List<NewCartModel> listorl = gson.fromJson(jsonObject.getString("data"), listType);
                    topSellList.addAll(listorl);
//                    JSONArray jsonArray = jsonObject.getJSONArray("data");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//
//                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                        String product_id = jsonObject1.getString("product_id");
//                        String varient_id = jsonObject1.getString("varient_id");
//                        String product_name = jsonObject1.getString("product_name");
//                        String description = jsonObject1.getString("description");
//                        String pprice = jsonObject1.getString("price");
//                        String quantity = jsonObject1.getString("quantity");
//                        String product_image = jsonObject1.getString("varient_image");
//                        String mmrp = jsonObject1.getString("mrp");
//                        String unit = jsonObject1.getString("unit");
//                        String storeId = jsonObject1.getString("store_id");
////                            String count = jsonObject1.getString("count");
//
//                        String totalOff = String.valueOf(Integer.parseInt(mmrp) - Integer.parseInt(pprice));
//
//                        CartModel recentData = new CartModel(product_id, product_name, description, pprice, quantity + " " + unit, product_image, session_management.getCurrency() + totalOff + " " + "Off", mmrp, " ", unit,storeId);
//                        recentData.setVarient_id(varient_id);
//                        topSellList.add(recentData);
//                    }
                    topSellingAdapter = new ViewAll_Adapter(topSellList, getApplicationContext(), () -> {
                        if (dbcart.getCartCount() > 0) {
                            bottom_lay_total.setVisibility(View.VISIBLE);
                            total_price.setText(session_management.getCurrency() + " " + dbcart.getTotalAmount());
                            total_count.setText("Total Items (" + dbcart.getCartCount() + ")");
                        } else {
                            bottom_lay_total.setVisibility(View.GONE);
                        }
                    });
                    rv_top_selling.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rv_top_selling.setAdapter(topSellingAdapter);
                    topSellingAdapter.notifyDataSetChanged();
                } else {
                    JSONObject resultObj = jsonObject.getJSONObject("results");
                    String msg = resultObj.getString("message");
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("lat",session_management.getLatPref());
                params.put("lng",session_management.getLangPref());
                params.put("city",session_management.getLocationCity());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ViewAll_TopDeals.this);
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
        }else {
            finish();
        }
//        super.onBackPressed();
    }
}
