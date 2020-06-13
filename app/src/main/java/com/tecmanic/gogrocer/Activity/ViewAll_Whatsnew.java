package com.tecmanic.gogrocer.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
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

import static com.tecmanic.gogrocer.Config.BaseURL.HomeRecent;

public class ViewAll_Whatsnew extends AppCompatActivity {
    ShimmerRecyclerView recyclerWhatNew;
    ViewAll_Adapter recentAdapter;

    ProgressDialog progressDialog;
    TextView viewall;
    private List<CartModel> recentList = new ArrayList<>();
    private Session_management session_management;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all__whatsnew);

        recyclerWhatNew = findViewById(R.id.recyclerWhatNew);

        progressDialog = new ProgressDialog(ViewAll_Whatsnew.this);
        session_management = new Session_management(ViewAll_Whatsnew.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        recentUrl();

    }

    private void recentUrl() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, HomeRecent, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("homeRecent", response);
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("1")) {
                        recentList.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String product_id = jsonObject1.getString("product_id");
                            String product_name = jsonObject1.getString("product_name");
                            String varient_id = jsonObject1.getString("varient_id");
                            String description = jsonObject1.getString("description");
                            String pprice = jsonObject1.getString("price");
                            String quantity = jsonObject1.getString("quantity");
                            String product_image = jsonObject1.getString("varient_image");
                            String mmrp = jsonObject1.getString("mrp");
                            String unit = jsonObject1.getString("unit");
                            String count = jsonObject1.getString("count");
                            String totalOff = String.valueOf(Integer.parseInt(mmrp) - Integer.parseInt(pprice));

                            CartModel recentData = new CartModel(product_id, product_name, description, pprice, quantity + " " + unit, product_image,session_management.getCurrency() + totalOff + " " + "Off", mmrp, count, unit);
                            recentData.setVarient_id(varient_id);
                            recentList.add(recentData);

                        }
                        recentAdapter = new ViewAll_Adapter(recentList, getApplicationContext());
                        recyclerWhatNew.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerWhatNew.setAdapter(recentAdapter);
                        recentAdapter.notifyDataSetChanged();

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

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);


    }
}
