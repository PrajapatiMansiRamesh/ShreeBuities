package com.tecmanic.gogrocer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.LocaleHelper;
import com.tecmanic.gogrocer.util.NetworkConnection;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;
import static com.tecmanic.gogrocer.Config.BaseURL.RecharegeWallet;

/*import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;*/

public class RechargeWallet extends AppCompatActivity implements PaymentResultListener {
    EditText Wallet_Ammount;
    RelativeLayout Recharge_Button;
    String ammount;
    private Session_management sessionManagement;
    private ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_wallet);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Recharge Wallet");
        progressDialog = new ProgressDialog(RechargeWallet.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(RechargeWallet.this, MainActivity.class);
//                startActivity(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            }
        });
        sessionManagement = new Session_management(RechargeWallet.this);
        final String email = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
        final String mobile = sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE);
        final String name = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
        Wallet_Ammount = findViewById(R.id.et_wallet_ammount);
        Recharge_Button = findViewById(R.id.recharge_button);

        Recharge_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                ammount = Wallet_Ammount.getText().toString();
                startPayment(name, ammount, email, mobile);

            }
        });
    }

    public void startPayment(String name, String amount, String email, String phone) {
/*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {

            JSONObject options = new JSONObject();

            options.put("name", name);
            options.put("description", "Wallet Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");

            options.put("amount", Integer.parseInt(amount) * 100);

            JSONObject preFill = new JSONObject();

            preFill.put("email", email);

            preFill.put("contact",phone);

            options.put("prefill", preFill);

            co.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(RechargeWallet.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }


    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")

    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Recharge_wallet("success");
            Toast.makeText(this, "Wallet Recharge Successful", Toast.LENGTH_SHORT).show();
            overridePendingTransition(0, 0);

//            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
            progressDialog.dismiss();
        }
    }

    public void onPaymentError(int i, String s) {
//        Intent intent = new Intent(RechargeWallet.this, OrderFail.class);
//        startActivity(intent);
//        overridePendingTransition(0, 0);
//

        Recharge_wallet("failed");

    }

    private void Recharge_wallet(String success) {
        final String user_id = sessionManagement.userId();
        if (NetworkConnection.connectionChecking(this)) {
            RequestQueue rq = Volley.newRequestQueue(this);
            StringRequest postReq = new StringRequest(Request.Method.POST, RecharegeWallet,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("eclipse", "Response=" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                String message = object.getString("message");
                                Toast.makeText(RechargeWallet.this, "" + message, Toast.LENGTH_LONG).show();
                                if (object.getString("status").equalsIgnoreCase("1")) {
                                    Intent intent = new Intent();
                                    intent.putExtra("recharge", "success");
                                    setResult(5, intent);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        finishAndRemoveTask();
                                    } else {
                                        finish();
                                    }
                                }
//                                if (object.optString("success").equalsIgnoreCase("success")) {
//                                    String wallet_amount = object.getString("wallet_amount");
//                                    SharedPref.putString(RechargeWallet.this, BaseURL.KEY_WALLET_Ammount, wallet_amount);
//
//                                } else {
//                                    //
//                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error [" + error + "]");
                    progressDialog.dismiss();
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", user_id);
                    params.put("amount", ammount);
                    params.put("recharge_status", success);
                    return params;
                }
            };
            rq.add(postReq);
        } else {
            Intent intent = new Intent(RechargeWallet.this, NetworkError.class);
            startActivity(intent);
        }

    }

}
