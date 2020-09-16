package com.tecmanic.gogrocer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.material.textfield.TextInputLayout;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

import static com.android.volley.VolleyLog.TAG;

public class PaystackPayment extends AppCompatActivity {
    SharedPreferences delivercharge;
    private Session_management sessionManagement;
    private String get_wallet_ammount = "";
    private String getuser_id = "";
    private LinearLayout progress_bar;
    private TextInputLayout mCardNumber;
    private TextInputLayout mCardExpiry;
    private TextInputLayout mCardCVV;
    private Button makePayment;
    private String email = "";
    private String mobile = "";
    private String name = "";
    private String activity = "";
    private DatabaseHandler db_cart;
    private String cart_id = "";
    private String payment_method = "";
    private String wallet_status = "";

    private void show() {
        if (progress_bar.getVisibility() == View.VISIBLE) {
            progress_bar.setVisibility(View.GONE);
        } else {
            progress_bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paystack_payment);
        progress_bar = findViewById(R.id.progress_bar);
        mCardNumber = findViewById(R.id.til_card_number);
        mCardExpiry = findViewById(R.id.til_card_expiry);
        mCardCVV = findViewById(R.id.til_card_cvv);
        makePayment = findViewById(R.id.btn_make_payment);
        db_cart = new DatabaseHandler(this);
        sessionManagement = new Session_management(PaystackPayment.this);
        delivercharge = getSharedPreferences("charges", 0);

        email = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
        mobile = sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE);
        name = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);

//        String total_rs = getIntent().getStringExtra("total_amount");
        activity = getIntent().getStringExtra("activity");
        get_wallet_ammount = getIntent().getStringExtra("total_amount");
        getuser_id = getIntent().getStringExtra("getuser_id");
        cart_id = getIntent().getStringExtra("cart_id");
        payment_method = getIntent().getStringExtra("payment_method");
        wallet_status = getIntent().getStringExtra("wallet_status");


        Objects.requireNonNull(mCardExpiry.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 2 && !s.toString().contains("/")) {
                    s.append("/");
                }
            }
        });

        initializePaystack();
        makePayment.setOnClickListener(view -> performCharge());
    }

    private void initializePaystack() {
        PaystackSdk.initialize(PaystackPayment.this);
        PaystackSdk.setPublicKey("pk_test_f0269be01832feda8b9cce63a261770ecd249f77");
    }

    public void performCharge() {
        int price_rs = Integer.parseInt(get_wallet_ammount);

        String cardNumber = mCardNumber.getEditText().getText().toString();
        String cardExpiry = mCardExpiry.getEditText().getText().toString();
        String cvv = mCardCVV.getEditText().getText().toString();

        String[] cardExpiryArray = cardExpiry.split("/");
        int expiryMonth = Integer.parseInt(cardExpiryArray[0]);
        int expiryYear = Integer.parseInt(cardExpiryArray[1]);


        Card card = new Card(cardNumber, expiryMonth, expiryYear, cvv);

        Charge charge = new Charge();
        charge.setAmount(price_rs);
//        try {
//            charge.putCustomField(name,"");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        charge.setEmail(email);
        charge.setCurrency(sessionManagement.getCurrency());
        charge.setCard(card);

        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                Log.d("PaystackPayment", "onSuccess: " + transaction.getReference());
                parseResponse(transaction.getReference());
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                Log.d("PaystackPayment", "beforeValidate: " + transaction.getReference());
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                Log.d("PaystackPayment", "onError: " + error.getLocalizedMessage());
            }

        });
    }

    private void parseResponse(String transactionReference) {
//        String message = "Payment Successful - " + transactionReference;
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (activity.equalsIgnoreCase("wallet")) {
            Intent intent = new Intent();
            intent.putExtra("result", "success");
            setResult(25, intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        } else if (activity.equalsIgnoreCase("non_wallet")) {
            makeAddOrderRequest(getuser_id, cart_id, payment_method, wallet_status, "success");
        }

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (activity.equalsIgnoreCase("wallet")) {
            Intent intent = new Intent();
            intent.putExtra("result", "false");
            setResult(25, intent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }

    }

    private void makeAddOrderRequest(String userid, String cart_id, String payment_method, String wallet_status, String payment_status) {
        show();
        String tag_json_obj = "json_add_order_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userid);
        params.put("payment_status", payment_status);
        params.put("cart_id", cart_id);
        params.put("payment_method", payment_method);
        params.put("wallet", wallet_status);
//        params.put("lat",lat);
//        params.put("lng",lng);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.ADD_ORDER_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    String status = response.getString("status");
                    String message = response.getString("message");
                    if (status.equalsIgnoreCase("1")) {
                        sessionManagement.setCartID("");
                        JSONObject jsonObject = response.getJSONObject("data");
                        db_cart.clearCart();
                        Intent intent = new Intent(getApplicationContext(), OrderSuccessful.class);
                        intent.putExtra("msg", message);
                        startActivity(intent);
                        Toast.makeText(PaystackPayment.this, "" + message, Toast.LENGTH_SHORT).show();
                    } else if (status.equalsIgnoreCase("2")) {
                        sessionManagement.setCartID("");
                        JSONObject jsonObject = response.getJSONObject("data");
                        db_cart.clearCart();
                        Intent intent = new Intent(getApplicationContext(), OrderSuccessful.class);
                        intent.putExtra("msg", message);
                        startActivity(intent);
                        Toast.makeText(PaystackPayment.this, "" + message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PaystackPayment.this, "" + message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                show();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
               /* if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        jsonObjReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 90000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}