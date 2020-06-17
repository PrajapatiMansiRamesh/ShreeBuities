package com.tecmanic.gogrocer.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tecmanic.gogrocer.Adapters.My_Pending_Order_adapter;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.ModelClass.NewPendingOrderModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.ConnectivityReceiver;
import com.tecmanic.gogrocer.util.CustomVolleyJsonArrayRequest;
import com.tecmanic.gogrocer.util.MyPendingReorderClick;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tecmanic.gogrocer.Config.BaseURL.PendingOrderUrl;

public class My_Pending_Order extends Fragment {

    public static String cart_id;
    private static String TAG = My_Pending_Order.class.getSimpleName();
    String user_id;
    Session_management sessionManagement;
    TabHost tHost;
    private RecyclerView rv_myorder;
    private List<NewPendingOrderModel> listModelNew = new ArrayList<>();
    private MyPendingReorderClick myPendingReorderClick;

    public My_Pending_Order(MyPendingReorderClick myPendingReorderClick) {
        this.myPendingReorderClick = myPendingReorderClick;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_pending_order, container, false);


        // handle the touch event if true
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // check user can press back button or not
                //                    Fragment fm = new Home_fragment();
                //                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                //                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                //                            .addToBackStack(null).commit();
                return event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
//
//        SharedPreferences.Editor editor = getActivity().getSharedPreferences("logindata", MODE_PRIVATE).edit();
//        editor.putString("id", cart_id);
//        Log.d("ee",cart_id);
//        editor.commit();
        sessionManagement = new Session_management(getActivity());
        user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

        rv_myorder = view.findViewById(R.id.rv_myorder);
        rv_myorder.setLayoutManager(new LinearLayoutManager(getActivity()));


        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetOrderRequest(user_id);
        } else {
//            ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
        }

        // recyclerview item click listener
//        rv_myorder.addOnItemTouchListener(new
//                RecyclerTouchListener(getActivity(), rv_myorder, new RecyclerTouchListener.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(View view, int position) {
//                Bundle args = new Bundle();
//                String sale_id = my_order_modelList.get(position).getCart_id();
//                String date = my_order_modelList.get(position).getDelivery_date();
//                String time = my_order_modelList.get(position).getTime_slot();
//                String total = my_order_modelList.get(position).getPrice();
//                String status = my_order_modelList.get(position).getOrder_status();
//                String deli_charge = my_order_modelList.get(position).getDelivery_charge();
//                Intent intent=new Intent(getContext(), Myorderdetails.class);
//                intent.putExtra("sale_id", sale_id);
//                intent.putExtra("date", date);
//                intent.putExtra("time", time);
//                intent.putExtra("total", total);
//                intent.putExtra("status", status);
//                intent.putExtra("deli_charge", deli_charge);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));

        return view;
    }

    /**
     * Method to make json array request where json response starts wtih
     */
    private void makeGetOrderRequest(String userid) {
        String tag_json_obj = "json_socity_req";
        listModelNew.clear();
//my_order_modelList.clear();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userid);

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                PendingOrderUrl, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                if (response != null && response.length() > 0) {

//                    for (int i=0;i<response.length();i++){
//
//                        try {
//                            JSONObject resp = response.getJSONObject(i);
//                            String order_status = resp.getString("order_status");
//                            String delivery_date = resp.getString("delivery_date");
//                            String time_slot = resp.getString("time_slot");
//                            String payment_method = resp.getString("payment_method");
//                            String payment_status = resp.getString("payment_status");
//                            String paid_by_wallet = resp.getString("paid_by_wallet");
//                            String cart_id = resp.getString("cart_id");
//                            String price = resp.getString("price");
//                            String del_charge = resp.getString("del_charge");
//                            JSONArray dataArray = resp.getJSONArray("data");
//                            for (int j=0;j<dataArray.length();j++){
//
//                            }
//
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }


                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<NewPendingOrderModel>>() {
                    }.getType();

                    listModelNew = gson.fromJson(response.toString(), listType);

                    My_Pending_Order_adapter myPendingOrderAdapter = new My_Pending_Order_adapter(listModelNew, myPendingReorderClick);
                    rv_myorder.setAdapter(myPendingOrderAdapter);
                    myPendingOrderAdapter.notifyDataSetChanged();

                }

//                try {
//                    JSONObject object = response.getJSONObject(0);
//                    if (object.getString("data").equals("no orders found")){
//
//                    }
//                    else {
//                        Gson gson = new Gson();
//                        Type listType = new TypeToken<List<My_Pending_order_model>>() {
//                        }.getType();
//
//
//
//                        my_order_modelList = gson.fromJson(response.toString(), listType);
//                        cart_id = gson.toJson("cart_id");
//                        My_Pending_Order_adapter myPendingOrderAdapter = new My_Pending_Order_adapter(my_order_modelList);
//                        rv_myorder.setAdapter(myPendingOrderAdapter);
//                        myPendingOrderAdapter.notifyDataSetChanged();
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Gson gson = new Gson();
//                Type listType = new TypeToken<List<My_Pending_order_model>>() {
//                }.getType();
//
//
//
//                    my_order_modelList = gson.fromJson(response.toString(), listType);
//                    cart_id = gson.toJson("cart_id");
//                    My_Pending_Order_adapter myPendingOrderAdapter = new My_Pending_Order_adapter(my_order_modelList);
//                    rv_myorder.setAdapter(myPendingOrderAdapter);
//                    myPendingOrderAdapter.notifyDataSetChanged();

                if (listModelNew.isEmpty()) {
                    Toast.makeText(getActivity(), "No Data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
               /* if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

}
