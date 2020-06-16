package com.tecmanic.gogrocer.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.LocaleHelper;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.provider.Telephony.Carriers.PASSWORD;
import static com.tecmanic.gogrocer.Config.BaseURL.PREFS_NAME;
import static com.tecmanic.gogrocer.Config.BaseURL.SignUp;

public class SignUpActivity extends AppCompatActivity {

    EditText etName,etPhone,etEmail,etPAss;
    Button btnSignUP;
    TextView btnLogin;
    ProgressDialog progressDialog;
    String emailPattern,token;
    LinearLayout skip;
    private Session_management session_management;

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        session_management = new Session_management(SignUpActivity.this);
        init();

    }

    private void init() {

        emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

//        token = FirebaseInstanceId.getInstance().getToken();
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        token = "";
                        Log.i("Login", "getInstanceId failed", task.getException());
                        return;
                    }
                    // Get new Instance ID token
                    token = task.getResult().getToken();
                });
        etName=findViewById(R.id.etName);
        etPhone=findViewById(R.id.etPhone);
        etEmail=findViewById(R.id.etEmail);
        etPAss=findViewById(R.id.etPass);
        skip=findViewById(R.id.skip);

        btnSignUP=findViewById(R.id.btnSignUP);
        btnLogin=findViewById(R.id.btn_Login);

        skip.setOnClickListener(v -> {
            session_management.createLoginSession("","","","","",true);
            finish();
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        });

        btnSignUP.setOnClickListener(v -> {
            if(etName.getText().toString().trim().equalsIgnoreCase("")){
                Toast.makeText(getApplicationContext(),"Full name required!",Toast.LENGTH_SHORT).show(); }
            else if(etEmail.getText().toString().trim().equalsIgnoreCase("")){
                Toast.makeText(getApplicationContext(),"Email id required!",Toast.LENGTH_SHORT).show(); }
            else if(!etEmail.getText().toString().trim().matches(emailPattern)){
                Toast.makeText(getApplicationContext(),"Valid Email id required!",Toast.LENGTH_SHORT).show(); }
            else if(etPhone.getText().toString().trim().equalsIgnoreCase("")){
                Toast.makeText(getApplicationContext(),"Mobile Number required!",Toast.LENGTH_SHORT).show(); }
            else if (etPhone.getText().toString().trim().length()<9) {
                Toast.makeText(getApplicationContext(),"Valid Mobile Number required!",Toast.LENGTH_SHORT).show(); }
            else if(etPAss.getText().toString().trim().equalsIgnoreCase("")){
                Toast.makeText(getApplicationContext(),"Password required!",Toast.LENGTH_SHORT).show();
            }
            else if(!isOnline()){
                Toast.makeText(getApplicationContext(),"Please check your Internet Connection!",Toast.LENGTH_SHORT).show();
            }else {
                progressDialog.show();
                signUpUrl();
            }
        });

    }

    private void signUpUrl() {

        if (token!=null && !token.equalsIgnoreCase("")){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, SignUp, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("SignUP",response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("message");
                        if (status.equalsIgnoreCase("1")){
                            JSONObject resultObj = jsonObject.getJSONObject("data");

                            String user_name = resultObj.getString("user_name");
                            String id = resultObj.getString("user_id");
                            String user_email= resultObj.getString("user_email");
                            String user_phone= resultObj.getString("user_phone");
                            String password = resultObj.getString("user_password");
                            String otp_value = resultObj.getString("otp_value");
                            String block = resultObj.getString("block");
                            String is_verified = resultObj.getString("is_verified");

//                            if (is_verified.equalsIgnoreCase("1")){
//
//                            }
                            session_management.createLoginSession(id,user_email,user_name,user_phone,password,false,"");
                            session_management.setOtp(otp_value);
                            session_management.setOtpStatus("1");
                            session_management.setUserBlockStatus(block);

                            //  Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(getApplicationContext(),OtpVerification.class);
                            intent.putExtra("MobNo",etPhone.getText().toString());
                            startActivity(intent);
                            finish();


                        }else if (status.equalsIgnoreCase("2")){
                            JSONObject resultObj = jsonObject.getJSONObject("data");

                            String user_name = resultObj.getString("user_name");
                            String id = resultObj.getString("user_id");
                            String user_email= resultObj.getString("user_email");
                            String user_phone= resultObj.getString("user_phone");
                            String password = resultObj.getString("user_password");
                            String block = resultObj.getString("block");
                            String is_verified = resultObj.getString("is_verified");

                            session_management.createLoginSession(id,user_email,user_name,user_phone,password);
                            session_management.setUserBlockStatus(block);
                            session_management.setOtpStatus("0");
                            Intent intent= new Intent(SignUpActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        }

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
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> param = new HashMap<>();

                    param.put("user_name",etName.getText().toString());
                    param.put("user_phone",etPhone.getText().toString());
                    param.put("user_email",etEmail.getText().toString());
                    param.put("user_password",etPAss.getText().toString());
                    param.put("device_id",token);

              /*  param.put("user_name","Sonal Jain");
                param.put("user_phone","9179985780");
                param.put("user_email","sonal@gmail.com");
                param.put("user_password","12345");
                param.put("device_id","f1bca86f19636b42");*/
                    return param;

                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);
        }else {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            token = "";
                            Log.i("Login", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        signUpUrl();
                    });
        }


    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
