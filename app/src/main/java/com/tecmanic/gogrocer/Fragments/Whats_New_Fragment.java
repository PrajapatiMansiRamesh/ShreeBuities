package com.tecmanic.gogrocer.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.tecmanic.gogrocer.Activity.ViewAll_TopDeals;
import com.tecmanic.gogrocer.Adapters.CartAdapter;
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

import static com.tecmanic.gogrocer.Config.BaseURL.whatsnew;

/**
 * A simple {@link Fragment} subclass.
 */
public class Whats_New_Fragment extends Fragment {

    ShimmerRecyclerView recyclerWhatNew;
    CartAdapter recentAdapter;

    ProgressDialog progressDialog;
    TextView viewall;
    private List<CartModel> recentList = new ArrayList<>();
    private LinearLayout mm;
    private RelativeLayout no_data;
    private Session_management session_management;
    private Context context;

    public Whats_New_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_whats__new_, container, false);
        session_management = new Session_management(container.getContext());
        context = container.getContext();
        viewall = view.findViewById(R.id.viewall);
        mm = view.findViewById(R.id.mm);
        no_data = view.findViewById(R.id.no_data);

        viewall.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewAll_TopDeals.class);
            intent.putExtra("action_name", "Whats_New_Fragment");
            startActivity(intent);
        });
        recyclerWhatNew = view.findViewById(R.id.recyclerWhatNew);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        recentUrl();
        return view;
    }

    private void recentUrl() {
        mm.setVisibility(View.VISIBLE);
        viewall.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, whatsnew, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("homeRecent", response);
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
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
//                            String count = jsonObject1.getString("count");
                            String totalOff = String.valueOf(Integer.parseInt(mmrp) - Integer.parseInt(pprice));

                            CartModel recentData = new CartModel(product_id, product_name, description, pprice, quantity + " " + unit, product_image, session_management.getCurrency() + totalOff + " " + "Off", mmrp, "", unit);
                            recentData.setVarient_id(varient_id);
                            recentList.add(recentData);

                        }
                        recentAdapter = new CartAdapter(recentList, context);
                        recyclerWhatNew.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
                        recyclerWhatNew.setAdapter(recentAdapter);
                        recentAdapter.notifyDataSetChanged();

                    } else {
                        mm.setVisibility(View.GONE);
                        viewall.setVisibility(View.GONE);
                        no_data.setVisibility(View.VISIBLE);

//                        JSONObject resultObj = jsonObject.getJSONObject("results");
//                        String msg = resultObj.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
                mm.setVisibility(View.GONE);
                viewall.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

}
