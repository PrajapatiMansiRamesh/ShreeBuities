package com.tecmanic.gogrocer.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tecmanic.gogrocer.activity.DealActivity;
import com.tecmanic.gogrocer.Adapters.Deal_Adapter;
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

import static com.tecmanic.gogrocer.Config.BaseURL.HomeDeal;

public class Deals_Fragment extends Fragment {

    ProgressDialog progressDialog;
    Deal_Adapter DealAdapter;
    ShimmerRecyclerView shimmerRecyclerView;
    String catId, catName;
    TextView viewall;
    private List<CartModel> dealList = new ArrayList<>();
    private RelativeLayout no_data;
    private Session_management session_management;

    public Deals_Fragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deals, container, false);
        progressDialog = new ProgressDialog(container.getContext());
        session_management = new Session_management(container.getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        viewall = view.findViewById(R.id.viewall);
        no_data = view.findViewById(R.id.no_data);

        viewall.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DealActivity.class);
            intent.putExtra("action_name", "Deals_Fragment");
            startActivity(intent);
        });

        shimmerRecyclerView = view.findViewById(R.id.recyclerDealsDay);
        if (isOnline()) {
            progressDialog.show();
            DealUrl();
        }
    /*    shimmerRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), shimmerRecyclerView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                catId = dealList.get(position).getpId();
                catName = dealList.get(position).getpNAme();
                Intent intent=new Intent(getActivity(), ProductDetails.class);
                intent.putExtra("sId",catId);
                intent.putExtra("sName",catName);
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));*/
        return view;
    }

    private void DealUrl() {
        shimmerRecyclerView.setVisibility(View.VISIBLE);
        viewall.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HomeDeal, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("HomeDeal", response);
//                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equals("1")) {
                        dealList.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String product_id = jsonObject1.getString("product_id");
                            String varient_id = jsonObject1.getString("varient_id");
                            String product_name = jsonObject1.getString("product_name");
                            String description = jsonObject1.getString("description");
                            String pprice = jsonObject1.getString("price");
                            String quantity = jsonObject1.getString("quantity");
                            String product_image = jsonObject1.getString("varient_image");
                            String mmrp = jsonObject1.getString("mrp");
                            String unit = jsonObject1.getString("unit");
                            Long count = jsonObject1.getLong("timediff");
                            String storeId = jsonObject1.getString("store_id");
                            String totalOff = String.valueOf(Integer.parseInt(mmrp) - Integer.parseInt(pprice));

                            CartModel recentData = new CartModel(product_id, product_name, description, pprice, quantity + " " + unit, product_image, session_management.getCurrency() + totalOff + " " + "Off", mmrp, " ", unit,storeId);
                            recentData.setVarient_id(varient_id);
                            recentData.setHoursmin(count);
                            dealList.add(recentData);

                        }
                        DealAdapter = new Deal_Adapter(dealList, requireActivity());
                        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                        shimmerRecyclerView.setAdapter(DealAdapter);
                        DealAdapter.notifyDataSetChanged();

                    } else {
                        shimmerRecyclerView.setVisibility(View.GONE);
                        viewall.setVisibility(View.GONE);
                        no_data.setVisibility(View.VISIBLE);
//                        JSONObject resultObj = jsonObject.getJSONObject("results");
//                        String msg = resultObj.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
//                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    progressDialog.dismiss();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                shimmerRecyclerView.setVisibility(View.GONE);
                viewall.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
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

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
