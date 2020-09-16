package com.tecmanic.gogrocer.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tecmanic.gogrocer.Adapters.Adapter_popup;
import com.tecmanic.gogrocer.Adapters.CategoryGridAdapter;
import com.tecmanic.gogrocer.Categorygridquantity;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.Fragments.Recent_Details_Fragment;
import com.tecmanic.gogrocer.ModelClass.NewCategoryDataModel;
import com.tecmanic.gogrocer.ModelClass.NewCategoryShowList;
import com.tecmanic.gogrocer.ModelClass.NewCategoryVarientList;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryPage extends AppCompatActivity {

    RecyclerView recycler_product;
    CategoryGridAdapter adapter;
    //    List<CategoryGrid> model = new ArrayList<>();
    List<NewCategoryShowList> newModelList = new ArrayList<>();
    List<NewCategoryDataModel> newCategoryDataModel = new ArrayList<>();
    String cat_id, image, title;
    BottomSheetBehavior behavior;
    private List<NewCategoryVarientList> varientProducts = new ArrayList<>();
    private LinearLayout bottom_sheet;
    private LinearLayout back;
    private LinearLayout bottom_lay_total;
    private TextView total_count;
    private TextView total_price;
    private TextView continue_tocart;
    private Session_management session_management;
    private DatabaseHandler dbcart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);
        session_management = new Session_management(CategoryPage.this);
        recycler_product = findViewById(R.id.recycler_product);
        bottom_lay_total = findViewById(R.id.bottom_lay_total);
        total_price = findViewById(R.id.total_price);
        total_count = findViewById(R.id.total_count);
        continue_tocart = findViewById(R.id.continue_tocart);
        image = Recent_Details_Fragment.product_image;
        bottom_sheet = findViewById(R.id.bottom_sheet);
        back = findViewById(R.id.back);
        behavior = BottomSheetBehavior.from(bottom_sheet);
        cat_id = getIntent().getStringExtra("cat_id");
        image = getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("title");
        dbcart = new DatabaseHandler(CategoryPage.this);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
        back.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("open", false);
            setResult(24, intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        });
        Categorygridquantity categorygridquantity = new Categorygridquantity() {
            @Override
            public void onClick(View view, int position, String ccId, String id) {
                varientProducts.clear();
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                TextView txt = findViewById(R.id.txt);
                txt.setText(id);
                LinearLayout cancl = findViewById(R.id.cancl);
                cancl.setOnClickListener(v -> behavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
                RecyclerView recyler_popup = findViewById(R.id.recyclerVarient);
                recyler_popup.setLayoutManager(new LinearLayoutManager(CategoryPage.this));
                varientProducts.addAll(newCategoryDataModel.get(position).getVarients());
                Adapter_popup selectCityAdapter = new Adapter_popup(CategoryPage.this, varientProducts, id, position1 -> {
                    if (varientProducts.get(position1).getVarient_id().equalsIgnoreCase(newCategoryDataModel.get(position).getVarient_id())) {
                        adapter.notifyItemChanged(position);
                    }
                });
                recyler_popup.setAdapter(selectCityAdapter);

//                Varient_product(ccId, recyler_popup, id);

            }

            @Override
            public void onCartItemAddOrMinus() {
                if (dbcart.getCartCount() > 0) {
                    bottom_lay_total.setVisibility(View.VISIBLE);
                    total_price.setText(session_management.getCurrency() + " " + dbcart.getTotalAmount());
                    total_count.setText("Total Items " + dbcart.getCartCount());
                } else {
                    bottom_lay_total.setVisibility(View.GONE);
                }
            }
        };


        recycler_product.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new CategoryGridAdapter(newCategoryDataModel, CategoryPage.this, categorygridquantity);
        recycler_product.setAdapter(adapter);

        continue_tocart.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("open", true);
            setResult(24, intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        });
        if (dbcart.getCartCount() > 0) {
            bottom_lay_total.setVisibility(View.VISIBLE);
            total_price.setText(session_management.getCurrency() + " " + dbcart.getTotalAmount());
            total_count.setText("Total Items (" + dbcart.getCartCount() + ")");
        } else {
            bottom_lay_total.setVisibility(View.GONE);
        }

        product(cat_id);


    }

//    private void Varient_product(String pId, RecyclerView recyler_popup, String id) {
//        varientProducts.clear();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, ProductVarient, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("Prod_detail", response);
//                try {
//                    varientProducts.clear();
//
//                    JSONObject jsonObject = new JSONObject(response);
//                    String status = jsonObject.getString("status");
//                    if (status.equals("1")) {
//                        JSONArray jsonArray = jsonObject.getJSONArray("data");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                            String product_id = jsonObject1.getString("product_id");
//                            String varient_id = jsonObject1.getString("varient_id");
//                            String price = jsonObject1.getString("price");
//                            String quantity = jsonObject1.getString("quantity");
//                            String varient_image = jsonObject1.getString("varient_image");
//                            String mrp = jsonObject1.getString("mrp");
//                            String unit = jsonObject1.getString("unit");
//                            String description = jsonObject1.getString("description");
//
//
//                            // Picasso.get().load(IMG_URL+varient_image).into(pImage);
//                            //prodMrp.setText(mrp);
//
//                            varient_product selectCityModel = new varient_product();
//                            selectCityModel.setVarient_imqge(varient_image);
//                            selectCityModel.setVariant_unit_value(unit);
//                            selectCityModel.setVariant_price(price);
//                            selectCityModel.setVariant_mrp(mrp);
//                            selectCityModel.setVariant_unit(quantity);
//                            selectCityModel.setVariant_id(varient_id);
//                            selectCityModel.setProductdescription(description);
//
//
////                            varientProducts.add(selectCityModel);
//
//                            Adapter_popup selectCityAdapter = new Adapter_popup(CategoryPage.this, varientProducts, id, new Communicator() {
//                                @Override
//                                public void onClick(int position) {
//
//
//                                }
//                            });
//                            recyler_popup.setAdapter(selectCityAdapter);
//
//
//                        }
//
//                    } else {
//                        varientProducts.clear();
//                        //JSONObject resultObj = jsonObject.getJSONObject("results");
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<>();
//                params.put("product_id", pId);
//                Log.d("kj", pId);
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.getCache().clear();
//        requestQueue.add(stringRequest);
//    }

    private void product(String cat_id) {
        newCategoryDataModel.clear();
        // Tag used to cancel the request
        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);
        params.put("lat", session_management.getLatPref());
        params.put("lng", session_management.getLangPref());
        params.put("city", session_management.getLocationCity());

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.cat_product, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("CheckApi", response.toString());


                try {
                    String status = response.getString("status");

//                    String message = response.getString("message");

                    if (status.contains("1")) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<NewCategoryDataModel>>() {
                        }.getType();
                        List<NewCategoryDataModel> listorl = gson.fromJson(response.getString("data"), listType);
                        newCategoryDataModel.addAll(listorl);

//                        for (int i = 0; i < listorl.size(); i++) {
//                            List<NewCategoryVarientList> listddd = listorl.get(i).getVarients();
//                            for (int j = 0; j < listddd.size(); j++) {
//                                NewCategoryShowList newCategoryShowList = new NewCategoryShowList(listorl.get(i).getProduct_id(), listorl.get(i).getProduct_name(), listorl.get(i).getProduct_image(), listddd.get(j));
//                                newModelList.add(newCategoryShowList);
//                            }
//                        }

                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                VolleyLog.d("", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                }
            }
        });

        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new RetryPolicy() {
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }


}
