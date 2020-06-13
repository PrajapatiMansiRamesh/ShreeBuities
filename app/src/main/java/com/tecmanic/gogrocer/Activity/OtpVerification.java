package com.tecmanic.gogrocer.Activity;

import androidx.appcompat.app.AppCompatActivity;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.tecmanic.gogrocer.Config.BaseURL.SignUpOtp;

public class OtpVerification extends AppCompatActivity {

    Button verify;
    TextView edit,txt_mobile;
    LinearLayout ll_edit;
    EditText et_otp;
    String MobileNO;
    ProgressDialog progressDialog;
    private Session_management session_management;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        session_management = new Session_management(OtpVerification.this);
        init();
    }

    private void init() {

        progressDialog = new ProgressDialog(OtpVerification.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        txt_mobile=findViewById(R.id.txt_mobile);
        et_otp=findViewById(R.id.et_otp);

        verify=findViewById(R.id.btnVerify);
        ll_edit=findViewById(R.id.ll_edit);

            MobileNO = getIntent().getStringExtra("MobNo");
            txt_mobile.setText(MobileNO);

        verify.setOnClickListener(v -> {
            if(et_otp.getText().toString().trim().equalsIgnoreCase("")){
                Toast.makeText(getApplicationContext(),"OTP required!",Toast.LENGTH_SHORT).show(); }

            else if(!isOnline()){
                Toast.makeText(getApplicationContext(),"Please check your Internet Connection!",Toast.LENGTH_SHORT).show();
            }else {
                progressDialog.show();
                otpUrl();
            }
        });
        ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void otpUrl() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SignUpOtp, response -> {
            Log.d("OTP",response);

            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("1")){
                    String msg = jsonObject.getString("message");
                   // JSONObject resultObj = jsonObject.getJSONObject("data");
                    session_management.setLogin(true);


                    Toast.makeText(getApplicationContext(), msg+"", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);finish();

                }else {
                    session_management.setLogin(false);
                    String msg = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                }
                progressDialog.dismiss();
            } catch (JSONException e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                param.put("user_phone",MobileNO);
                param.put("otp",et_otp.getText().toString());
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
