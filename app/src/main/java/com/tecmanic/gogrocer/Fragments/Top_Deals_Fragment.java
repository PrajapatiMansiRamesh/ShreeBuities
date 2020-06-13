package com.tecmanic.gogrocer.Fragments;


import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import static com.tecmanic.gogrocer.Config.BaseURL.HomeTopSelling;

/**
 * A simple {@link Fragment} subclass.
 */
public class Top_Deals_Fragment extends Fragment {

    TextView viewall;
    ProgressDialog progressDialog;
    CartAdapter topSellingAdapter;
    String catId, catName;
    private ShimmerRecyclerView rv_top_selling;
    private List<CartModel> topSellList = new ArrayList<>();
    private Context contexts;
    private RelativeLayout no_data;
    private Session_management session_management;

    public Top_Deals_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top__deals_, container, false);

        contexts = container.getContext();
        session_management = new Session_management(contexts);
        rv_top_selling = view.findViewById(R.id.recyclerTopSelling);
        no_data = view.findViewById(R.id.no_data);
        progressDialog = new ProgressDialog(contexts != null ? contexts : getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        viewall = view.findViewById(R.id.viewall_topdeals);

        viewall.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewAll_TopDeals.class);
            intent.putExtra("action_name","Top_Deals_Fragment");
            startActivity(intent);
        });
        if (isOnline()){
            topSellingUrl();
        }
     /*   rv_top_selling.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rv_top_selling, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                catId = topSellList.get(position).getpId();
                catName = topSellList.get(position).getpNAme();
                Intent intent=new Intent(getActivity(), ProductDetails.class);
                intent.putExtra("sId",catId);
                intent.putExtra("sName",catName);
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
*/
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        contexts = context;
        super.onAttach(context);
    }

    private boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) (contexts != null ? contexts : getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();

    }

    private void topSellingUrl() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, HomeTopSelling, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("HomeTopSelling", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equalsIgnoreCase("1")) {
                        topSellList.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String product_id = jsonObject1.getString("product_id");
                            String varient_id = jsonObject1.getString("varient_id");
                            String product_name = jsonObject1.getString("product_name");
                            String product_image = jsonObject1.getString("product_image");
                            String description = jsonObject1.getString("description");
                            String pprice = jsonObject1.getString("price");
                            String quantity = jsonObject1.getString("quantity");
                            String varient_image = jsonObject1.getString("varient_image");
                            String mmrp = jsonObject1.getString("mrp");
                            String unit = jsonObject1.getString("unit");
                            String count = jsonObject1.getString("count");

                            String totalOff = String.valueOf(Integer.parseInt(mmrp) - Integer.parseInt(pprice));
//                            Activity activity = getActivity();
//                            if (activity != null) {
//
//                            }
                            CartModel recentData = new CartModel(product_id, product_name, description, pprice, quantity + " " + unit, product_image, session_management.getCurrency() + totalOff + " " + "Off", mmrp, count, unit);
                            recentData.setVarient_id(varient_id);
                            topSellList.add(recentData);
                        }
                        rv_top_selling.setVisibility(View.VISIBLE);
                        viewall.setVisibility(View.VISIBLE);
                        no_data.setVisibility(View.GONE);
                        topSellingAdapter = new CartAdapter(topSellList, contexts);
                        rv_top_selling.setLayoutManager(new LinearLayoutManager(contexts,LinearLayoutManager.VERTICAL,false));
                        rv_top_selling.setAdapter(topSellingAdapter);
                        topSellingAdapter.notifyDataSetChanged();
                      /*  topSellingAdapter = new CartAdapter(topSellList,getActivity());
                        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getActivity(), 1);
                        rv_top_selling.setLayoutManager(gridLayoutManager2);
                        rv_top_selling.setAdapter(topSellingAdapter);
                        rv_top_selling.setItemAnimator(new DefaultItemAnimator());
                        rv_top_selling.setNestedScrollingEnabled(false);
                      //  rv_top_selling.showShimmerAdapter();
                        topSellingAdapter.notifyDataSetChanged();
*/
                    } else {
                        rv_top_selling.setVisibility(View.GONE);
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
                rv_top_selling.setVisibility(View.GONE);
                viewall.setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return new HashMap<>();
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(contexts != null ? contexts : getActivity());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }
}
