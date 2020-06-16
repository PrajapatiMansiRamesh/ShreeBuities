package com.tecmanic.gogrocer.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.tecmanic.gogrocer.Adapters.ComplainAdapter;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.Constans.RecyclerTouchListener;
import com.tecmanic.gogrocer.ModelClass.ComplainModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Cancel_Order extends AppCompatActivity {
    RecyclerView rc_complain;
    List<ComplainModel> complainModels = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    String reason;
    String cart_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel__order);

        cart_id = Objects.requireNonNull(getIntent().getExtras()).getString("cart_id");
//        cart_id = My_Pending_Order.cart_id;
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading....");
        rc_complain = findViewById(R.id.rc_complain);
        LinearLayoutManager gridLayoutManagercat1 = new LinearLayoutManager(Cancel_Order.this, LinearLayoutManager.VERTICAL, false);
        rc_complain.setLayoutManager(gridLayoutManagercat1);

        rc_complain.addOnItemTouchListener(new
                RecyclerTouchListener(getApplicationContext(), rc_complain, new RecyclerTouchListener.OnItemClickListener() {
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

        complain_ques();
    }

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

                        ComplainAdapter complainAdapter = new ComplainAdapter(complainModels);
                        rc_complain.setAdapter(complainAdapter);
                        complainAdapter.notifyDataSetChanged();

                    } else {

                        Toast.makeText(Cancel_Order.this, "" + message, Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                }
            }
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
        params.put("cart_id", cart_id);
        params.put("reason", reason);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.delete_order, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("CheckApi", response.toString());


                try {
                    String status = response.getString("status");

                    String message = response.getString("message");
                    if (status.contains("1")) {
                        showCancelDialog();
                        Toast.makeText(Cancel_Order.this, "" + message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    progressDialog.dismiss();
                }

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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cancel_Order.this);
        alertDialog.setCancelable(true);
        alertDialog.setMessage("You product has been cancel!");
//        alertDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }
}
