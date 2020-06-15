package com.tecmanic.gogrocer.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tecmanic.gogrocer.Adapters.Adapter_popup;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.ModelClass.varient_product;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tecmanic.gogrocer.Config.BaseURL.BASE_URL;
import static com.tecmanic.gogrocer.Config.BaseURL.ProductVarient;

public class ProductDetails extends AppCompatActivity {
    int minteger = 0;
    LinearLayout back, btn_Add, ll_addQuan, ll_details;
    ImageView pImage;
    TextView prodName, prodDesc, ProdPrice, prodMrp, nodata;
    TextView txtQuan, minus, plus, txt_unit, txt_qty;
    String varientName, discription12, price12, mrp12, unit12, qty, varientimage;
    ProgressDialog progressDialog;
    String product_id, varient_id;
    private DatabaseHandler dbcart;
    private SharedPreferences preferences;
    private List<varient_product> varientProducts = new ArrayList<>();
    private Session_management session_management;

    private RecyclerView recyclerUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        session_management = new Session_management(ProductDetails.this);
        dbcart = new DatabaseHandler(ProductDetails.this);
        preferences = getSharedPreferences("GOGrocer", Context.MODE_PRIVATE);
        init();

    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        product_id = getIntent().getStringExtra("sId");
        varientName = getIntent().getStringExtra("sName");

        discription12 = getIntent().getStringExtra("descrip");
        price12 = getIntent().getStringExtra("price");
        mrp12 = getIntent().getStringExtra("mrp");
        unit12 = getIntent().getStringExtra("unit");
        qty = getIntent().getStringExtra("qty");
        varientimage = getIntent().getStringExtra("image");
        varient_id = getIntent().getStringExtra("sVariant_id");

        ll_details = findViewById(R.id.ll3);
        back = findViewById(R.id.back);
        pImage = findViewById(R.id.pImage);
        nodata = findViewById(R.id.nodata);
        prodName = findViewById(R.id.txt_pName);
        prodDesc = findViewById(R.id.txt_pInfo);
        ProdPrice = findViewById(R.id.txt_pPrice);
        prodMrp = findViewById(R.id.txt_pMrp);
        btn_Add = findViewById(R.id.btn_Add);
        ll_addQuan = findViewById(R.id.ll_addQuan);
        txtQuan = findViewById(R.id.txtQuan);
        minus = findViewById(R.id.minus);
        plus = findViewById(R.id.plus);
        txt_unit = findViewById(R.id.txt_unit);
        txt_qty = findViewById(R.id.txt_qty);
        recyclerUnit = findViewById(R.id.recyclerUnit);

        recyclerUnit.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerUnit.setHasFixedSize(true);


        Varient_product(product_id);
        back.setOnClickListener(v -> finish());
        prodDesc.setText(discription12);

        int qtyd = Integer.parseInt(dbcart.getInCartItemQtys(varient_id));
        if (qtyd > 0) {
            btn_Add.setVisibility(View.GONE);
            ll_addQuan.setVisibility(View.VISIBLE);
            txtQuan.setText("" + qtyd);
            double priced = Double.parseDouble(price12);
            double mrpd = Double.parseDouble(mrp12);
            ProdPrice.setText("" + (priced * qtyd));
            prodMrp.setText("" + (mrpd * qtyd));
        } else {
            btn_Add.setVisibility(View.VISIBLE);
            ll_addQuan.setVisibility(View.GONE);
            ProdPrice.setText(price12);
            prodMrp.setText(mrp12);
            txtQuan.setText("" + 0);
        }


        btn_Add.setOnClickListener(v -> {
            btn_Add.setVisibility(View.GONE);
            ll_addQuan.setVisibility(View.VISIBLE);
            txtQuan.setText("1");
            updateMultiply();

        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseInteger();
                updateMultiply();
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseInteger();
                updateMultiply();
            }
        });

        if (isOnline()) {
            prodName.setText(varientName);
            ProdPrice.setText(session_management.getCurrency() + "" + price12);
            prodMrp.setPaintFlags(prodMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            prodMrp.setText(mrp12);
            txt_unit.setText(unit12);
            txt_qty.setText(qty);
//            varientUrl(varientId);

            Glide.with(this)
                    .load(BaseURL.IMG_URL+varientimage)
                    .centerCrop()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(pImage);
        }
    }

//    private void varientUrl(String varientId) {
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, ProductVarient, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("Prod detail", response);
//                progressDialog.dismiss();
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    String status = jsonObject.getString("status");
//                    if (status.equals("1")) {
//                        JSONArray jsonArray = jsonObject.getJSONArray("data");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                             product_id = jsonObject1.getString("product_id");
//                             varient_id = jsonObject1.getString("varient_id");
//                             price = jsonObject1.getString("price");
//                             quantity = jsonObject1.getString("quantity");
//                             varient_image = jsonObject1.getString("varient_image");
//
//                            mrp = jsonObject1.getString("mrp");
//                             unit = jsonObject1.getString("unit");
//                             description = jsonObject1.getString("description");
//                            nodata.setVisibility(View.GONE);
//                            ll_details.setVisibility(View.VISIBLE);
//                            prodMrp.setPaintFlags(prodMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//                            prodMrp.setText(mrp);
//                            ProdPrice.setText(getApplication().getResources().getString(R.string.currency) + price);
//                            prodDesc.setText(description);
//                            Picasso.with(getApplicationContext()).load(IMG_URL+varient_image).into(pImage);
//                            //prodMrp.setText(mrp);
//
//                        }
//
//                    } else {
//                        //JSONObject resultObj = jsonObject.getJSONObject("results");
//                        nodata.setVisibility(View.VISIBLE);
//                        ll_details.setVisibility(View.GONE);
//                    }
//                    progressDialog.dismiss();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                progressDialog.dismiss();
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<>();
//                params.put("product_id",varientId);
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.getCache().clear();
//        requestQueue.add(stringRequest);
//    }


    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    public void increaseInteger() {
        minteger = minteger + 1;
        display(minteger);
    }

    public void decreaseInteger() {

        if (minteger > 0) {
            minteger = minteger - 1;
            if (minteger == 0) {
                ll_addQuan.setVisibility(View.GONE);
                btn_Add.setVisibility(View.VISIBLE);
            }
            display(minteger);
        } else {
            display(minteger);
            ll_addQuan.setVisibility(View.GONE);
            btn_Add.setVisibility(View.VISIBLE);
        }

//        if (minteger == 1) {
//            minteger = 1;
//
//        } else {
//
//
//        }
    }

    private void display(Integer number) {

        txtQuan.setText("" + number);
    }


    private void updateMultiply() {
        HashMap<String, String> map = new HashMap<>();
        map.put("product_id", product_id);
        map.put("product_name", varientName);
        map.put("varient_id", varient_id);
        map.put("title", varientName);
        map.put("price", price12);
        map.put("deal_price", "");
        map.put("product_image", varientimage);
        map.put("unit_value", txtQuan.getText().toString());
        map.put("increament", "0");
        map.put("rewards", "0");
        map.put("stock", "0");
        map.put("product_description", discription12);
        if (!txtQuan.getText().toString().equalsIgnoreCase("0")) {
            if (dbcart.isInCart(map.get("varient_id"))) {
                dbcart.setCart(map, Integer.parseInt(txtQuan.getText().toString()));
            } else {
                dbcart.setCart(map, Integer.parseInt(txtQuan.getText().toString()));
            }
        } else {
            dbcart.removeItemFromCart(map.get("varient_id"));
        }
        try {
            int items = Integer.parseInt(dbcart.getInCartItemQtys(map.get("varient_id")));
            double price = Double.parseDouble(price12);
            double mrp = Double.parseDouble(mrp12);
            if (items == 0) {
                ProdPrice.setText(session_management.getCurrency() + "" + price);
                prodMrp.setText(session_management.getCurrency() + "" + mrp);
            } else {
                ProdPrice.setText(session_management.getCurrency() + "" + price * items);
                prodMrp.setText(session_management.getCurrency() + "" + mrp * items);
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                preferences.edit().putInt("cardqnty", dbcart.getCartCount()).apply();
            }
        } catch (IndexOutOfBoundsException e) {

            Log.d("qwer", e.toString());
        }
    }

    private void Varient_product(String pId) {
        varientProducts.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ProductVarient, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Prod_detail", response);
                try {
                    varientProducts.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String product_id = jsonObject1.getString("product_id");
                            String varient_id = jsonObject1.getString("varient_id");
                            String price = jsonObject1.getString("price");
                            String quantity = jsonObject1.getString("quantity");
                            String varient_image = jsonObject1.getString("varient_image");
                            String mrp = jsonObject1.getString("mrp");
                            String unit = jsonObject1.getString("unit");
                            String description = jsonObject1.getString("description");


                            // Picasso.get().load(IMG_URL+varient_image).into(pImage);
                            //prodMrp.setText(mrp);

                            varient_product selectCityModel = new varient_product();
                            selectCityModel.setVarient_imqge(varient_image);
                            selectCityModel.setVariant_unit_value(unit);
                            selectCityModel.setVariant_price(price);
                            selectCityModel.setVariant_mrp(mrp);
                            selectCityModel.setVariant_unit(quantity);
                            selectCityModel.setVariant_id(varient_id);
                            selectCityModel.setProductdescription(description);


                            varientProducts.add(selectCityModel);

                            Adapter_popup selectCityAdapter = new Adapter_popup(ProductDetails.this, varientProducts, varientName);
                            recyclerUnit.setAdapter(selectCityAdapter);


                        }

                    } else {
                        varientProducts.clear();
                        //JSONObject resultObj = jsonObject.getJSONObject("results");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("product_id", pId);
                Log.d("kj", pId);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

}
