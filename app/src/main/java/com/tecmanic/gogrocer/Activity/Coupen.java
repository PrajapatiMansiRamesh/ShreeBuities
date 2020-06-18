package com.tecmanic.gogrocer.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tecmanic.gogrocer.Adapters.Coupun_Listing_Adapter;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.ModelClass.CoupunModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coupen extends AppCompatActivity {

    List<CoupunModel> coupunModelList = new ArrayList<>();
    RecyclerView coupen_recycler;
    String cart_id;
    private Session_management session_management;
    private Coupun_Listing_Adapter complainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupen);

        session_management = new Session_management(Coupen.this);
        cart_id = session_management.getCartId();
        coupen_recycler = findViewById(R.id.recycleer_coupen);
        coupen_recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        complainAdapter = new Coupun_Listing_Adapter(coupunModelList, couponCode -> {
            Intent backResult = new Intent();
            backResult.putExtra("code", couponCode);
            setResult(2, backResult);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            }else {
                finish();
            }
        });
        coupen_recycler.setAdapter(complainAdapter);
        Coupon_code();
    }

    private void Coupon_code() {
        coupunModelList.clear();
        String tag_json_obj = "json_order_detail_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cart_id", cart_id);
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.COUPON_CODE, params, response -> {
            Log.d("TAG", response.toString());

            try {

                String status = response.getString("status");
                String message = "";
                if (response.has("message")) {
                    message = response.getString("message");
                }

                if (status.equalsIgnoreCase("1")) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<CoupunModel>>() {
                    }.getType();

                    coupunModelList = gson.fromJson(response.getString("data"), listType);
                    complainAdapter.setList(coupunModelList);
                    complainAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(Coupen.this, "" + message, Toast.LENGTH_SHORT).show();
                }

//                        if (status.contains("1")) {
//
//                            JSONArray jsonArray = response.getJSONArray("data");
//
//                            for (int i = 0; i < jsonArray.length(); i++) {
//
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                                String name = jsonObject.getString("coupon_name");
//                                String discrip = jsonObject.getString("coupon_description");
//                                String code = jsonObject.getString("coupon_code");
//
//                                CoupunModel coupunModel12 = new CoupunModel();
//
//                                coupunModel12.setCoupon_name(name);
//                                coupunModel12.setCoupon_description(discrip);
//                                coupunModel12.setCoupon_code(code);
//                                coupunModelList.add(coupunModel12);
//                            }
//
//                        }
//
//                        else{
//
//                            Toast.makeText(Coupen.this, ""+message, Toast.LENGTH_SHORT).show();
//
//                        }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(Coupen.this, "check your internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }

    @Override
    public void onBackPressed() {
        Intent backResult = new Intent();
        backResult.putExtra("code", "");
        setResult(2, backResult);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }else {
            finish();
        }
//        super.onBackPressed();
    }
}
