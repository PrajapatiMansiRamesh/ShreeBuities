package com.tecmanic.gogrocer.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tecmanic.gogrocer.Adapters.ComplainAdapter;
import com.tecmanic.gogrocer.Adapters.My_order_detail_adapter;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.Constans.RecyclerTouchListener;
import com.tecmanic.gogrocer.ModelClass.ComplainModel;
import com.tecmanic.gogrocer.ModelClass.My_order_detail_model;
import com.tecmanic.gogrocer.ModelClass.NewPendingDataModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.ConnectivityReceiver;
import com.tecmanic.gogrocer.util.CustomVolleyJsonArrayRequest;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;
import com.tecmanic.gogrocer.util.LocaleHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Myorderdetails extends AppCompatActivity {
    private static String TAG = Myorderdetails.class.getSimpleName();
    ImageView back_button;
    SharedPreferences preferences;
    List<ComplainModel> complainModels = new ArrayList<>();
    //    private TextView tv_date, tv_time, tv_total, tv_delivery_charge;
    private RelativeLayout btn_cancle;
    private RecyclerView rv_detail_order;
    private RecyclerView rv_cancel_order;
    private String sale_id;
    private ImageView back_tool;
    private ProgressDialog progressDialog;
    //    private List<My_order_detail_model> my_order_detail_modelList = new ArrayList<>();
    private List<NewPendingDataModel> my_order_detail_modelList = new ArrayList<>();
    //    private List<NewPastOrderSubModel> my_order_detail_modelList1 = new ArrayList<>();
    private LinearLayout cancel_lay;

    private List<NewPendingDataModel> modelsList = new ArrayList<>();
    private String pastorder = "";
    private ComplainAdapter complainAdapter;
    private String reason;
    private boolean isCancel = false;
    private String status = "";


    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorderdetails);

        btn_cancle = (RelativeLayout) findViewById(R.id.btn_order_detail_cancle);
        rv_detail_order = (RecyclerView) findViewById(R.id.rv_order_detail);
        rv_cancel_order = (RecyclerView) findViewById(R.id.rv_cancel_order);
        back_tool = findViewById(R.id.back_tool);
        cancel_lay = findViewById(R.id.cancel_lay);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);

        rv_detail_order.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        rv_cancel_order.setLayoutManager(new LinearLayoutManager(Myorderdetails.this, RecyclerView.VERTICAL, false));
        complainAdapter = new ComplainAdapter(complainModels);
        rv_cancel_order.setAdapter(complainAdapter);
        back_tool.setOnClickListener(v -> onBackPressed());
        cancel_lay.setVisibility(View.GONE);
//        pastorder = getIntent().getStringExtra("pastorder");
        sale_id = getIntent().getStringExtra("sale_id");
        String total_rs = getIntent().getStringExtra("total");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        status = getIntent().getStringExtra("status");
        String deli_charge = getIntent().getStringExtra("deli_charge");
        my_order_detail_modelList.clear();
//        my_order_detail_modelList1.clear();
//        if (pastorder.equalsIgnoreCase("true")){
//            my_order_detail_modelList1 = (List<NewPastOrderSubModel>) getIntent().getSerializableExtra("data");
//        }else {
//
//        }

        my_order_detail_modelList = (List<NewPendingDataModel>) getIntent().getSerializableExtra("data");


        if (status.equals("Completed") || status.equals("Out_For_Delivery") || status.equals("Cancelled")) {
            btn_cancle.setVisibility(View.GONE);
        } else {
            btn_cancle.setVisibility(View.VISIBLE);
        }
//        btn_cancle.setVisibility(View.VISIBLE);

//        tv_total.setText(total_rs);
//        tv_date.setText(getResources().getString(R.string.date) + date);
        preferences = getSharedPreferences("lan", MODE_PRIVATE);
        String language = preferences.getString("language", "");
        if (language.contains("spanish")) {
            time = time.replace("pm", "م");
            time = time.replace("am", "ص");
//            tv_time.setText(getResources().getString(R.string.time) + time);

        } else {
//            tv_time.setText(getResources().getString(R.string.time) + time);
        }

        rv_cancel_order.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), rv_cancel_order, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    progressDialog.show();
                    reason = complainModels.get(position).getReason();
                    deleteorder();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

//        if (pastorder.equalsIgnoreCase("true")){
//            my_order_detail_modelList1 = (List<NewPastOrderSubModel>) getIntent().getSerializableExtra("data");
//            MyPendingOrderDetails adapter = new MyPendingOrderDetails(this,my_order_detail_modelList1);
//            rv_detail_order.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
//        }else {
//
//        }

        my_order_detail_modelList = (List<NewPendingDataModel>) getIntent().getSerializableExtra("data");
        My_order_detail_adapter adapter = new My_order_detail_adapter(my_order_detail_modelList);
        rv_detail_order.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        // check internet connection
//        if (ConnectivityReceiver.isConnected()) {
////            makeGetOrderDetailRequest();
//        } else {
//            Toast.makeText(Myorderdetails.this, "Error Network Issues", Toast.LENGTH_SHORT).show();
//            // ((MainActivity) getApplication()).onNetworkConnectionChanged(false);
//        }

        btn_cancle.setOnClickListener(view -> showDeleteDialog());

    }

    // alertdialog for cancle order
    private void showDeleteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Myorderdetails.this);
        alertDialog.setMessage(getResources().getString(R.string.cancle_order_note));
        alertDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

//                Session_management sessionManagement = new Session_management(Myorderdetails.this);
//                String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

                // check internet connection
                if (ConnectivityReceiver.isConnected()) {
                    progressDialog.show();
                    complain_ques();
                }

                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    /**
     * Method to make json array request where json response starts wtih
     */
    private void makeGetOrderDetailRequest() {

        // Tag used to cancel the request
        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", "3");

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.POST,
                BaseURL.PendingOrderUrl, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                Gson gson = new Gson();
                Type listType = new TypeToken<List<My_order_detail_model>>() {
                }.getType();

                my_order_detail_modelList = gson.fromJson(response.toString(), listType);

                My_order_detail_adapter adapter = new My_order_detail_adapter(my_order_detail_modelList);
                rv_detail_order.setAdapter(adapter);
                adapter.notifyDataSetChanged();


                if (my_order_detail_modelList.isEmpty()) {
                    Toast.makeText(Myorderdetails.this, getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    /**
     * Method to make json object request where json response starts wtih
     */
//    private void makeDeleteOrderRequest(String sale_id, String user_id) {
//        progressDialog.show();
//        // Tag used to cancel the request
//        String tag_json_obj = "json_delete_order_req";
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("sale_id", sale_id);
//        params.put("user_id", user_id);
//
//        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
//                BaseURL.DELETE_ORDER_URL, params, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, response.toString());
//
//                try {
//                    boolean status = response.getBoolean("responce");
//                    if (status) {
//                        progressDialog.dismiss();
//                        String msg = response.getString("message");
//                        Toast.makeText(Myorderdetails.this, "" + msg, Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent();
//                        setResult(13,intent);
//                        onBackPressed();
//                        // ((MainActivity) getActivity()).onBackPressed();
//
//                    } else {
//                        progressDialog.dismiss();
//                        String error = response.getString("error");
//                        Toast.makeText(Myorderdetails.this, "" + error, Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }finally {
//                    if (progressDialog!=null && progressDialog.isShowing()){
//                        progressDialog.dismiss();
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                }
//            }
//        });
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//
//
//    }
    private void complain_ques() {

        complainModels.clear();

        String tag_json_obj = "json_category_req";


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.GET,
                BaseURL.DELETE_ORDER_URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", response.toString());

                try {

                    String status = response.getString("status");
                    String message = response.getString("message");

                    if (status.contains("1")) {

                        JSONArray jsonArray = response.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            complainModels.clear(); //Clear your array list before adding new data in it

                            reason = jsonObject.getString("reason");

                            ComplainModel complainModel = new ComplainModel();
                            complainModel.setReason(reason);
                            complainModels.add(complainModel);


                        }
                        complainAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(Myorderdetails.this, "" + message, Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                    if (complainModels.isEmpty()) {
                        cancel_lay.setVisibility(View.GONE);
                        rv_detail_order.setVisibility(View.VISIBLE);
                        btn_cancle.setVisibility(View.VISIBLE);
                    } else {
                        cancel_lay.setVisibility(View.VISIBLE);
                        rv_detail_order.setVisibility(View.GONE);
                        btn_cancle.setVisibility(View.GONE);
                    }
                }
            }
        }, error -> {
            progressDialog.dismiss();
            if (complainModels.isEmpty()) {
                cancel_lay.setVisibility(View.GONE);
                rv_detail_order.setVisibility(View.VISIBLE);
                btn_cancle.setVisibility(View.VISIBLE);
            } else {
                cancel_lay.setVisibility(View.VISIBLE);
                rv_detail_order.setVisibility(View.GONE);
                btn_cancle.setVisibility(View.GONE);
            }
            VolleyLog.d("TAG", "Error: " + error.getMessage());
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().getRequestQueue().add(jsonObjReq);


    }

    private void deleteorder() {

        // Tag used to cancel the request
        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("cart_id", sale_id);
        params.put("reason", reason);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.delete_order, params, response -> {
            Log.d("CheckApi", response.toString());


            try {
                String status = response.getString("status");

                String message = response.getString("message");
                if (status.contains("1")) {
                    showCancelDialog();
                    Toast.makeText(Myorderdetails.this, "" + message, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
                VolleyLog.d("", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    private void showCancelDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Myorderdetails.this);
        alertDialog.setCancelable(true);
        alertDialog.setMessage("You product has been cancel!");
        alertDialog.setPositiveButton("Ok", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            Intent intent = new Intent();
            setResult(13, intent);
            isCancel = true;
            onBackPressed();
        });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

        if (isCancel) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        } else {
            if (cancel_lay.getVisibility() == View.VISIBLE) {
                cancel_lay.setVisibility(View.GONE);
                rv_detail_order.setVisibility(View.VISIBLE);
                if (status.equals("Completed") || status.equals("Out_For_Delivery") || status.equals("Cancelled")) {
                    btn_cancle.setVisibility(View.GONE);
                } else {
                    btn_cancle.setVisibility(View.VISIBLE);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            }
        }


    }
}
