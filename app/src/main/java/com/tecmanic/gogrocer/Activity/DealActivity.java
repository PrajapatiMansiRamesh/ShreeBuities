package com.tecmanic.gogrocer.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.tecmanic.gogrocer.Adapters.CartAdapter;
import com.tecmanic.gogrocer.Adapters.Deal_Adapter;
import com.tecmanic.gogrocer.Adapters.ViewAll_Adapter;
import com.tecmanic.gogrocer.ModelClass.CartModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.tecmanic.gogrocer.Config.BaseURL.HomeDeal;
import static com.tecmanic.gogrocer.Config.BaseURL.HomeRecent;
import static com.tecmanic.gogrocer.Config.BaseURL.HomeTopSelling;
import static com.tecmanic.gogrocer.Config.BaseURL.whatsnew;

public class DealActivity extends AppCompatActivity {

    private ShimmerRecyclerView rv_top_selling;
    ProgressDialog progressDialog;
    ViewAll_Adapter topSellingAdapter;
    private List<CartModel> dealList = new ArrayList<>();
    String catId,catName;
    private String action_name;
    private Deal_Adapter dealAdapter;
    private Session_management session_management;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
//        action_name = (String) Objects.requireNonNull(getIntent().getExtras()).get("action_name");
        session_management = new Session_management(this);
        rv_top_selling =  findViewById(R.id.recyclerTopSelling);
        progressDialog=new ProgressDialog(DealActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        if(isOnline()){
            progressDialog.show();
            dealUrl();
        }


    }
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private void dealUrl() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, HomeDeal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("HomeDeal",response);
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equals("1")){
                        dealList.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String product_id = jsonObject1.getString("product_id");
                            String varient_id = jsonObject1.getString("varient_id");
                            String product_name = jsonObject1.getString("product_name");
                            String description= jsonObject1.getString("description");
                            String pprice= jsonObject1.getString("price");
                            String quantity = jsonObject1.getString("quantity");
                            String product_image = jsonObject1.getString("varient_image");
                            String mmrp = jsonObject1.getString("mrp");
                            String unit = jsonObject1.getString("unit");
                            Long count = jsonObject1.getLong("timediff");
                            String totalOff= String.valueOf(Integer.parseInt(String.valueOf(mmrp))-Integer.parseInt(String.valueOf(pprice)));

                            CartModel recentData= new CartModel(product_id,product_name,description,pprice,quantity+" "+unit,product_image,session_management.getCurrency()+totalOff+" "+"Off",mmrp," ",unit);
                            recentData.setVarient_id(varient_id);
                            recentData.setHoursmin(count);
                            dealList.add(recentData);

                        }
                        dealAdapter = new Deal_Adapter(dealList,DealActivity.this);
                        rv_top_selling.setLayoutManager(new LinearLayoutManager(DealActivity.this));
                        rv_top_selling.setAdapter(dealAdapter);
                        dealAdapter.notifyDataSetChanged();

                    }else {
//                        shimmerRecyclerView.setVisibility(View.GONE);
//                        viewall.setVisibility(View.GONE);
//                        no_data.setVisibility(View.VISIBLE);
//                        JSONObject resultObj = jsonObject.getJSONObject("results");
//                        String msg = resultObj.getString("message");
                        Toast.makeText(DealActivity.this,message,Toast.LENGTH_SHORT).show();
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(DealActivity.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
