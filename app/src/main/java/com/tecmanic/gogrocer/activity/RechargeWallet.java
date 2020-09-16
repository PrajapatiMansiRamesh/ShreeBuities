package com.tecmanic.gogrocer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.ModelClass.PaymentVia;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.network.ApiInterface;
import com.tecmanic.gogrocer.util.LocaleHelper;
import com.tecmanic.gogrocer.util.NetworkConnection;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static com.android.volley.VolleyLog.TAG;

/*import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;*/

public class RechargeWallet extends AppCompatActivity implements PaymentResultListener {
    private static final int PAYPAL_REQUEST_CODE = 123;
    EditText Wallet_Ammount;
    RelativeLayout Recharge_Button;
    String ammount;
    private Session_management sessionManagement;
    private ProgressDialog progressDialog;
    private ImageView dropdown;
    private LinearLayout pay_lay;
    private LinearLayout payment_opt;
    private LinearLayout razor_pay;
    private LinearLayout paypal_lay;
    private LinearLayout paystack_lay;
    private CheckBox use_paystack;
    private TextView paypaystack_txt;
    private CheckBox use_paypal;
    private CheckBox use_razorpay;
    private TextView raz_txt;
    private TextView paypal_txt;
    private boolean paypal = false;
    private boolean razorpay = false;
    private boolean paystack = false;

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
        dropdown = findViewById(R.id.dropdown);
        pay_lay = findViewById(R.id.pay_lay);
        paystack_lay = findViewById(R.id.paystack_lay);
        use_paystack = findViewById(R.id.use_paystack);
        paypaystack_txt = findViewById(R.id.paypaystack_txt);
        payment_opt = findViewById(R.id.payment_opt);
        razor_pay = findViewById(R.id.razor_pay);
        paypal_lay = findViewById(R.id.paypal_lay);
        use_razorpay = findViewById(R.id.use_razorpay);
        use_paypal = findViewById(R.id.use_paypal);
        raz_txt = findViewById(R.id.raz_txt);
        paypal_txt = findViewById(R.id.paypal_txt);
        Wallet_Ammount = findViewById(R.id.et_wallet_ammount);
        Recharge_Button = findViewById(R.id.recharge_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Recharge Wallet");
        progressDialog = new ProgressDialog(RechargeWallet.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        sessionManagement = new Session_management(RechargeWallet.this);
        final String email = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
        final String mobile = sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE);
        final String name = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
        checkUserPayNotify();

        if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("1") && sessionManagement.getPayStack().equalsIgnoreCase("1")) {
            paypal = true;
            razorpay = true;
            paystack = true;
//            dropdown.setVisibility(View.VISIBLE);
//            payment_opt.setVisibility(View.VISIBLE);
            pay_lay.setVisibility(View.VISIBLE);
        } else if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("1") && sessionManagement.getPayStack().equalsIgnoreCase("0")) {
            paypal = true;
            razorpay = true;
            paystack = false;
            paystack_lay.setVisibility(View.GONE);
            dropdown.setVisibility(View.VISIBLE);
        } else if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("0") && sessionManagement.getPayStack().equalsIgnoreCase("1")) {
            paypal = true;
            paystack = true;
            razorpay = false;
            razor_pay.setVisibility(View.GONE);
            dropdown.setVisibility(View.VISIBLE);
        } else if (sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getRazorPay().equalsIgnoreCase("1") && sessionManagement.getPayStack().equalsIgnoreCase("1")) {
            paypal = false;
            razorpay = true;
            paystack = true;
            paypal_lay.setVisibility(View.GONE);
            dropdown.setVisibility(View.VISIBLE);
        } else {
            pay_lay.setVisibility(View.GONE);
            if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("0") && sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                paypal = true;
                paystack = false;
                razorpay = false;
            } else if (sessionManagement.getRazorPay().equalsIgnoreCase("1") && sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                paypal = false;
                paystack = false;
                razorpay = true;
            } else if (sessionManagement.getPayStack().equalsIgnoreCase("1") && sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getRazorPay().equalsIgnoreCase("0")) {
                paypal = false;
                razorpay = false;
                paystack = true;
            }
        }

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

        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (payment_opt.getVisibility() == View.VISIBLE) {
                    payment_opt.setVisibility(View.GONE);
                    dropdown.setBackground(getResources().getDrawable(R.drawable.ic_arrow_righ_new));
                } else {
                    payment_opt.setVisibility(View.VISIBLE);
                    dropdown.setBackground(getResources().getDrawable(R.drawable.ic_down_new));
                }
            }
        });

        razor_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (use_razorpay.isChecked()) {
                    use_razorpay.setChecked(false);
                    use_paypal.setChecked(false);
                    use_paystack.setChecked(false);
                    razor_pay.setBackgroundResource(R.drawable.border_rounded1);
                    raz_txt.setTextColor(getResources().getColor(R.color.black));
                    paypal_lay.setBackgroundResource(R.drawable.border_rounded1);
                    paypal_txt.setTextColor(getResources().getColor(R.color.black));
                    paystack_lay.setBackgroundResource(R.drawable.border_rounded1);
                    paypaystack_txt.setTextColor(getResources().getColor(R.color.black));
                } else {
                    use_razorpay.setChecked(true);
                    use_paystack.setChecked(false);
                    use_paypal.setChecked(false);
                    razor_pay.setBackgroundResource(R.drawable.gradientbg);
                    raz_txt.setTextColor(getResources().getColor(R.color.white));
                    paystack_lay.setBackgroundResource(R.drawable.border_rounded1);
                    paypaystack_txt.setTextColor(getResources().getColor(R.color.black));
                    paypal_lay.setBackgroundResource(R.drawable.border_rounded1);
                    paypal_txt.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        paypal_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (use_paypal.isChecked()) {
                    use_razorpay.setChecked(false);
                    use_paypal.setChecked(false);
                    use_paystack.setChecked(false);
                    razor_pay.setBackgroundResource(R.drawable.border_rounded1);
                    raz_txt.setTextColor(getResources().getColor(R.color.black));
                    paypal_lay.setBackgroundResource(R.drawable.border_rounded1);
                    paypal_txt.setTextColor(getResources().getColor(R.color.black));
                    paystack_lay.setBackgroundResource(R.drawable.border_rounded1);
                    paypaystack_txt.setTextColor(getResources().getColor(R.color.black));
                } else {
                    use_razorpay.setChecked(false);
                    use_paystack.setChecked(false);
                    use_paypal.setChecked(true);
                    razor_pay.setBackgroundResource(R.drawable.border_rounded1);
                    raz_txt.setTextColor(getResources().getColor(R.color.black));
                    paystack_lay.setBackgroundResource(R.drawable.border_rounded1);
                    paypaystack_txt.setTextColor(getResources().getColor(R.color.black));
                    paypal_lay.setBackgroundResource(R.drawable.gradientbg);
                    paypal_txt.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        paystack_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (use_paystack.isChecked()) {
                    use_razorpay.setChecked(false);
                    use_paypal.setChecked(false);
                    use_paystack.setChecked(false);
                    razor_pay.setBackgroundResource(R.drawable.border_rounded1);
                    raz_txt.setTextColor(getResources().getColor(R.color.black));
                    paypal_lay.setBackgroundResource(R.drawable.border_rounded1);
                    paypal_txt.setTextColor(getResources().getColor(R.color.black));
                    paystack_lay.setBackgroundResource(R.drawable.border_rounded1);
                    paypaystack_txt.setTextColor(getResources().getColor(R.color.black));
                } else {
                    use_razorpay.setChecked(false);
                    use_paypal.setChecked(false);
                    use_paystack.setChecked(true);
                    razor_pay.setBackgroundResource(R.drawable.border_rounded1);
                    raz_txt.setTextColor(getResources().getColor(R.color.black));
                    paypal_lay.setBackgroundResource(R.drawable.border_rounded1);
                    paypal_txt.setTextColor(getResources().getColor(R.color.black));
                    paystack_lay.setBackgroundResource(R.drawable.gradientbg);
                    paypaystack_txt.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });


        Recharge_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ammount = Wallet_Ammount.getText().toString();
                if (paypal && razorpay && paystack) {
                    if (use_razorpay.isChecked()) {
                        progressDialog.show();
                        startPayment(name, ammount, email, mobile);
                    } else if (use_paypal.isChecked()) {
                        progressDialog.show();
                        startPaypal(name, ammount, email, mobile);
                    } else if (use_paystack.isChecked()) {
                        Intent intent = new Intent(RechargeWallet.this, PaystackPayment.class);
                        intent.putExtra("activity", "wallet");
                        intent.putExtra("total_amount", ammount);
                        startActivityForResult(intent, 25);
                    } else {
                        Toast.makeText(RechargeWallet.this, "Please select payment mode!", Toast.LENGTH_SHORT).show();
                        if (payment_opt.getVisibility() != View.VISIBLE) {
                            payment_opt.setVisibility(View.VISIBLE);
                            dropdown.setBackground(getResources().getDrawable(R.drawable.ic_down_new));
                        }
                    }
                } else {
                    if (paypal) {
                        progressDialog.show();
                        startPaypal(name, ammount, email, mobile);
                    } else if (razorpay) {
                        progressDialog.show();
                        startPayment(name, ammount, email, mobile);
                    } else if (paystack) {
                        Intent intent = new Intent(RechargeWallet.this, PaystackPayment.class);
                        intent.putExtra("activity", "wallet");
                        intent.putExtra("total_amount", ammount);
                        startActivityForResult(intent, 25);
                    }
                }
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

            preFill.put("contact", phone);

            options.put("prefill", preFill);

            co.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(RechargeWallet.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

    private void checkUserPayNotify() {
        progressDialog.show();
        Retrofit emailOtp = new Retrofit.Builder()
                .baseUrl(BaseURL.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        ApiInterface apiInterface = emailOtp.create(ApiInterface.class);

        Call<PaymentVia> checkOtpStatus = apiInterface.getPaymentVia();

        checkOtpStatus.enqueue(new Callback<PaymentVia>() {
            @Override
            public void onResponse(@NonNull Call<PaymentVia> call, @NonNull retrofit2.Response<PaymentVia> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        PaymentVia modelUser = response.body();
                        Log.i("TAG", modelUser.toString());
                        if (modelUser.getStatus().equalsIgnoreCase("1")) {
                            sessionManagement.setPaymentMethodOpt(modelUser.getData().getRazorpay(), modelUser.getData().getPaypal(), modelUser.getData().getPaystack());
                            if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("1") && sessionManagement.getPayStack().equalsIgnoreCase("1")) {
                                paypal = true;
                                razorpay = true;
                                paystack = true;
//            dropdown.setVisibility(View.VISIBLE);
//            payment_opt.setVisibility(View.VISIBLE);
                                pay_lay.setVisibility(View.VISIBLE);
                            } else if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("1") && sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                                paypal = true;
                                razorpay = true;
                                paystack = false;
                                paystack_lay.setVisibility(View.GONE);
                                dropdown.setVisibility(View.VISIBLE);
                                pay_lay.setVisibility(View.VISIBLE);
                            } else if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("0") && sessionManagement.getPayStack().equalsIgnoreCase("1")) {
                                paypal = true;
                                paystack = true;
                                razorpay = false;
                                pay_lay.setVisibility(View.VISIBLE);
                                razor_pay.setVisibility(View.GONE);
                                dropdown.setVisibility(View.VISIBLE);
                            } else if (sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getRazorPay().equalsIgnoreCase("1") && sessionManagement.getPayStack().equalsIgnoreCase("1")) {
                                paypal = false;
                                razorpay = true;
                                paystack = true;
                                pay_lay.setVisibility(View.VISIBLE);
                                paypal_lay.setVisibility(View.GONE);
                                dropdown.setVisibility(View.VISIBLE);
                            } else {
                                pay_lay.setVisibility(View.GONE);
                                if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("0") && sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                                    paypal = true;
                                    paystack = false;
                                    razorpay = false;
                                } else if (sessionManagement.getRazorPay().equalsIgnoreCase("1") && sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                                    paypal = false;
                                    paystack = false;
                                    razorpay = true;
                                } else if (sessionManagement.getPayStack().equalsIgnoreCase("1") && sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getRazorPay().equalsIgnoreCase("0")) {
                                    paypal = false;
                                    razorpay = false;
                                    paystack = true;
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<PaymentVia> call, @NonNull Throwable t) {
                progressDialog.dismiss();
            }
        });

    }

    private void startPaypal(String name, String total_amount, String email, String mobile) {
        PayPalConfiguration config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .acceptCreditCards(true)
                .defaultUserPhone(mobile)
                .defaultUserEmail(email)
                .clientId(PayPalConfig.PAYPAL_CLIENT_ID);
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        getPayment(total_amount, config);
    }

    private void getPayment(String total_rs, PayPalConfiguration config) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(total_rs), "USD", "Shopping Fee", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
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
//            overridePendingTransition(0, 0);

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
            StringRequest postReq = new StringRequest(Request.Method.POST, BaseURL.RecharegeWallet,
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
            postReq.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 5;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            rq.add(postReq);
        } else {
            Intent intent = new Intent(RechargeWallet.this, NetworkError.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
//                        String paymentDetails = confirm.toJSONObject().toString(5);
                        JSONObject jsonObject = confirm.toJSONObject();
                        JSONObject responseJs = jsonObject.getJSONObject("response");
                        if (responseJs.getString("state").equalsIgnoreCase("approved")) {
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
                        Log.i("paymentExample", jsonObject.toString(5));

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                progressDialog.dismiss();
                Log.i("paymentExample", "The user canceled.");
                Toast.makeText(this, "Wallet Recharge Failed!", Toast.LENGTH_SHORT).show();
                Recharge_wallet("failed");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                progressDialog.dismiss();
                Toast.makeText(this, "Wallet Recharge Failed!", Toast.LENGTH_SHORT).show();
                Recharge_wallet("failed");
                Log.i("paymentExample", PaymentActivity.RESULT_EXTRAS_INVALID + "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == 25) {
            if (Objects.requireNonNull(Objects.requireNonNull(data).getStringExtra("result")).equalsIgnoreCase("success")) {
                Recharge_wallet("success");
                Toast.makeText(this, "Wallet Recharge Successful", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
