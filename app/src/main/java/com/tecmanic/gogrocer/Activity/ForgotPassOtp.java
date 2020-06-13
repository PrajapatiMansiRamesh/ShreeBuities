package com.tecmanic.gogrocer.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ForgotPassOtp extends AppCompatActivity {
    EditText et_req_mobile;
    CardView cvverify;
    ProgressDialog progressDialog;
    public static String TAG="Login";

    Button verify;
    TextView edit,txt_mobile;
    LinearLayout ll_edit;
    EditText et_otp;
    String MobileNO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_otp);
        et_req_mobile=findViewById(R.id.et_req_mobile);
        cvverify=findViewById(R.id.cvverify);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        cvverify.setEnabled(true);
        cvverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvverify.setEnabled(true);
                if (et_req_mobile.getText().toString().length()!=10 || et_req_mobile.getText().toString().contains("+")){
                    et_req_mobile.setError("Enter valid mobile number");
                }else {

                    progressDialog.show();

                    makeRegisterRequest();

                }
            }
        });
    }
    private void makeRegisterRequest() {

        // Tag used to cancel the request
        String tag_json_obj = "json_register_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_phone", et_req_mobile.getText().toString());


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.forget_password, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    String status = response.getString("status");
                    String message=response.getString("message");

                    if (status.contains("1")) {

                        cvverify.setEnabled(false);
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPassOtp.this, message, Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent(ForgotPassOtp.this,Forget_otp_verify.class);
                        intent.putExtra("user_phone",et_req_mobile.getText().toString());
                        startActivity(intent);

                    } else {
                        cvverify.setEnabled(true);
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPassOtp.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    progressDialog.dismiss();
                    cvverify.setEnabled(true);
//                    Toast.makeText(ForgotPassOtp.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
}