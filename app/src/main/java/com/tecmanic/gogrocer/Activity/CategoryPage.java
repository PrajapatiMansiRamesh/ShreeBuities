package com.tecmanic.gogrocer.Activity;

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

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.tecmanic.gogrocer.ModelClass.varient_product;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tecmanic.gogrocer.Config.BaseURL.ProductVarient;

public class CategoryPage extends AppCompatActivity {

    RecyclerView recycler_product;
    CategoryGridAdapter adapter;
    //    List<CategoryGrid> model = new ArrayList<>();
    List<NewCategoryShowList> newModelList = new ArrayList<>();
    String cat_id, image, title;
    BottomSheetBehavior behavior;
    private List<varient_product> varientProducts = new ArrayList<>();
    private LinearLayout bottom_sheet;
    private LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);
        recycler_product = findViewById(R.id.recycler_product);
        image = Recent_Details_Fragment.product_image;
        bottom_sheet = findViewById(R.id.bottom_sheet);
        back = findViewById(R.id.back);
        behavior = BottomSheetBehavior.from(bottom_sheet);
        cat_id = getIntent().getStringExtra("cat_id");
        image = getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("title");

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
        back.setOnClickListener(v -> finish());
        Categorygridquantity categorygridquantity = new Categorygridquantity() {
            @Override
            public void onClick(View view, int position, String ccId, String id) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                Toast.makeText(CategoryPage.this,position+ id + ccId,Toast.LENGTH_LONG).show();

                TextView txt = findViewById(R.id.txt);
                txt.setText(id);
                LinearLayout cancl = findViewById(R.id.cancl);
                cancl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });
                RecyclerView recyler_popup = findViewById(R.id.recyclerVarient);
                recyler_popup.setLayoutManager(new LinearLayoutManager(CategoryPage.this));

                Varient_product(ccId, recyler_popup, id);

            }
        };


        recycler_product.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new CategoryGridAdapter(newModelList, CategoryPage.this, categorygridquantity);
        recycler_product.setAdapter(adapter);


        product(cat_id);


    }

    private void Varient_product(String pId, RecyclerView recyler_popup, String id) {
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

                            Adapter_popup selectCityAdapter = new Adapter_popup(CategoryPage.this, varientProducts, id);
                            recyler_popup.setAdapter(selectCityAdapter);


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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void product(String cat_id) {
        newModelList.clear();
        // Tag used to cancel the request
        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", cat_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.cat_product, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("CheckApi", response.toString());


                try {
                    String status = response.getString("status");

                    String message = response.getString("message");

                    if (status.contains("1")) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<NewCategoryDataModel>>() {
                        }.getType();
                        List<NewCategoryDataModel> listorl = gson.fromJson(response.getString("data"), listType);

                        for (int i = 0; i < listorl.size(); i++) {
                            for (int j = 0; j < listorl.get(i).getVarients().size(); j++) {
                                NewCategoryShowList newCategoryShowList = new NewCategoryShowList(listorl.get(i).getProduct_id(), listorl.get(i).getProduct_name(), listorl.get(i).getProduct_image(), listorl.get(i).getVarients().get(j));
                                newModelList.add(newCategoryShowList);
                            }
                        }

                        adapter.notifyDataSetChanged();

//                        JSONArray data = response.getJSONArray("data");


//                        for (int i = 0; i < data.length(); i++) {
//
//                            JSONObject object = data.getJSONObject(i);
//
//                            CategoryGrid models = new CategoryGrid();
//
//                            models.setId(object.getString("product_id"));
//
//                            models.setImage(object.getString("product_image"));
//                            models.setName(object.getString("product_name"));
//
//
//                            JSONArray dataobj2 = object.getJSONArray("varients");
//
//                            String qty = "";
//                            String unit = "";
//                            String description = "";
//                            String mrp = "";
//                            String price = "";
//                            String varient_id = "";
//                            String varient_image = "";
//                            if (dataobj2.length() > 0) {
//                                JSONObject jsonObject = dataobj2.getJSONObject(0);
//
//                                qty = jsonObject.getString("quantity");
//                                unit = jsonObject.getString("unit");
//                                mrp = jsonObject.getString("mrp");
//                                description = jsonObject.getString("description");
//
//                                varient_id = jsonObject.getString("varient_id");
//                                price = jsonObject.getString("price");
//                                varient_image = jsonObject.getString("varient_image");
//                                models.setVarient_id(varient_id);
//                                models.setVarient_image(varient_image);
//                                models.setPrice(price);
//                                models.setMrp(mrp);
//                                models.setUnit(unit);
//                                models.setQuantity(qty);
//                                models.setDescription(description);
//                                model.add(models);
//
//                                adapter.notifyDataSetChanged();
//                            }
//
////                        adapter.notify();
//                        }


                    } else {

//                        Toast.makeText(CategoryPage.this, "", Toast.LENGTH_SHORT).show();
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }


}
