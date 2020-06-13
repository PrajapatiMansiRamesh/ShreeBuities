package com.tecmanic.gogrocer.Fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.tecmanic.gogrocer.Activity.ViewAll_TopDeals;
import com.tecmanic.gogrocer.Adapters.CartAdapter;
import com.tecmanic.gogrocer.ModelClass.CartModel;
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

import static com.tecmanic.gogrocer.Config.BaseURL.HomeRecent;


public class Recent_Details_Fragment extends Fragment {

    public static String product_image;
    ProgressDialog progressDialog;
    CartAdapter recentAdapter;
    String catId, catName;
    TextView viewall;
    private ShimmerRecyclerView recycler_recent;
    private DatabaseHandler db;
    private Session_management sessionManagement;
    private List<CartModel> recentList = new ArrayList<>();
    private RelativeLayout no_data;
    private Session_management session_management;
    private Context context;
    // broadcast reciver for receive data
    private BroadcastReceiver mCart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            if (type.contentEquals("update")) {

            }
        }
    };


    public Recent_Details_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent__details_, container, false);
        viewall = view.findViewById(R.id.viewall_topdeals);
        session_management = new Session_management(container.getContext());
        context = container.getContext();
        viewall.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewAll_TopDeals.class);
            intent.putExtra("action_name", "Recent_Details_Fragment");
            startActivity(intent);
        });

        sessionManagement = new Session_management(context);
        sessionManagement.cleardatetime();
        db = new DatabaseHandler(context);
        recycler_recent = view.findViewById(R.id.recyclerRecent);
        no_data = view.findViewById(R.id.no_data);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        if (isOnline()) {
            recentUrl();
        }
      /*  recycler_recent.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recycler_recent, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                catId = recentList.get(position).getpId();
                catName = recentList.get(position).getpNAme();
                Intent intent=new Intent(getActivity(), ProductDetails.class);
                intent.putExtra("sId",catId);
                intent.putExtra("sName",catName);
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));*/

//        ArrayList<HashMap<String, String>> map = db.getrecentAll();

//        Log.d("dhsf",map.toString());
//        Recent_Adapter adapter = new Recent_Adapter(getActivity(), map);
//        recent_list.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

        return view;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
//    private void updateData() {
//        tv_total.setText("" + db.getTotalAmount());
//        tv_item.setText("" + db.getCartCount());
//        ((MainActivity) getActivity()).setCartCounter("" + db.getCartCount());
//    }

    private void recentUrl() {
        recycler_recent.setVisibility(View.VISIBLE);
        viewall.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, HomeRecent, new Response.Listener<String>() {
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
                            String varient_id = jsonObject1.getString("varient_id");
                            String product_name = jsonObject1.getString("product_name");
                            String description = jsonObject1.getString("description");
                            String pprice = jsonObject1.getString("price");
                            String quantity = jsonObject1.getString("quantity");
                            String varient_image = jsonObject1.getString("varient_image");
                            String product_image = jsonObject1.getString("product_image");
                            String mmrp = jsonObject1.getString("mrp");
                            String unit = jsonObject1.getString("unit");
                            String count = jsonObject1.getString("count");
                            String totalOff = String.valueOf(Integer.parseInt(mmrp) - Integer.parseInt(pprice));
                            CartModel recentData = new CartModel(product_id, product_name, description, pprice, quantity + " " + unit, product_image, session_management.getCurrency() + totalOff + " " + "Off", mmrp, count, unit);
                            recentData.setVarient_id(varient_id);
                            recentList.add(recentData);
                        }
                        recentAdapter = new CartAdapter(recentList, context);
                        recycler_recent.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                        recycler_recent.setAdapter(recentAdapter);
                        recentAdapter.notifyDataSetChanged();

                    } else {
                        recycler_recent.setVisibility(View.GONE);
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
                recycler_recent.setVisibility(View.GONE);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister reciver
        getActivity().unregisterReceiver(mCart);
    }

    @Override
    public void onResume() {
        super.onResume();
        // register reciver
        getActivity().registerReceiver(mCart, new IntentFilter("Grocery_cart"));
    }

}
