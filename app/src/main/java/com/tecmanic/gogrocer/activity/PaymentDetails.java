package com.tecmanic.gogrocer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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
import com.tecmanic.gogrocer.Config.SharedPref;
import com.tecmanic.gogrocer.ModelClass.CoupunModel;
import com.tecmanic.gogrocer.ModelClass.PaymentVia;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.network.ApiInterface;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static com.android.volley.VolleyLog.TAG;

public class PaymentDetails extends AppCompatActivity implements PaymentResultListener {

    LinearLayout llwallet, llpromocode, llcards, llcod;


    String Prefrence_TotalAmmount;
    String lat, lng;
    String getwallet;
    LinearLayout Promo_code_layout, Coupon_and_wallet;
    RelativeLayout Apply_Coupon_Code, Relative_used_wallet, Relative_used_coupon;
    List<CoupunModel> coupunModelList = new ArrayList<>();
    RecyclerView coupen_recycler;
    SharedPreferences sharedPreferences12;
    SharedPreferences.Editor editor12;
    String code, cart_id;
    TextView twallet, tcod, tcards, tpromocode;
    LinearLayout backcart;
    int status = 0;
    String payment_method;
    Button confirm;
    String wallet_amount = "00";
    TextView reset_text;
    String wallet_status = "no";
    String total_amt;
    TextView payble_ammount, my_wallet_ammount, used_wallet_ammount, used_coupon_ammount, order_ammount;
    CheckBox rb_Store, rb_Cod, rb_card;
    CheckBox checkBox_Wallet;
    CheckBox checkBox_coupon;
    CheckBox use_razorpay;
    CheckBox use_paypal;
    EditText et_Coupon;
    String getvalue;
    String text;
    String cp;
    String Used_Wallet_amount;
    String total_amount;
    String order_total_amount;
    TextView linea, lineb;
    RadioGroup radioGroup;
    private TextView coupuntxt;
    private DatabaseHandler db_cart;
    private Session_management sessionManagement;
    private String getlocation_id = "";
    private String getstore_id = "";
    private String gettime = "";
    private String getdate = "";
    private String getuser_id = "";
    private Double rewards;
    private String remaingprice = "";
    private boolean coupounApplied = false;
    private String payable_amt = "";
    private double walletbalnce = 0;
    private ProgressDialog progressDialog;
    private String coupon_amount = "";
    private ImageView dropdown;

    private TextView coupon_apply_t;
    private TextView paypal_txt;
    private TextView raz_txt;
    private LinearLayout payment_opt;
    private boolean paypal = false;
    private boolean razorpay = false;
    private boolean paystack = false;
    private LinearLayout razor_pay;
    private LinearLayout paypal_lay;
    private LinearLayout paystack_lay;
    private CheckBox use_paystack;
    private TextView paypaystack_txt;

    public static final int PAYPAL_REQUEST_CODE = 123;


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
    private void checkUserPayNotify() {
        progressDialog.setMessage("Loading please wait!...");
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
                progressDialog.setMessage("Payment is in processing...");
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        PaymentVia modelUser = response.body();
                        Log.i("TAG",modelUser.toString());
                        if (modelUser.getStatus().equalsIgnoreCase("1")) {
                            sessionManagement.setPaymentMethodOpt(modelUser.getData().getRazorpay(), modelUser.getData().getPaypal(), modelUser.getData().getPaystack());
                            if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("1")&& sessionManagement.getPayStack().equalsIgnoreCase("1")) {
                                paypal = true;
                                razorpay = true;
                                paystack = true;
                                dropdown.setVisibility(View.VISIBLE);
//            payment_opt.setVisibility(View.VISIBLE);
                                rb_card.setVisibility(View.GONE);
                            }
                            else if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("1")&& sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                                paypal = true;
                                razorpay = true;
                                paystack = false;
                                paystack_lay.setVisibility(View.GONE);
                                dropdown.setVisibility(View.VISIBLE);
                                rb_card.setVisibility(View.GONE);
                            }
                            else if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("0")&& sessionManagement.getPayStack().equalsIgnoreCase("1")) {
                                paypal = true;
                                paystack = true;
                                razorpay = false;
                                razor_pay.setVisibility(View.GONE);
                                dropdown.setVisibility(View.VISIBLE);
                                rb_card.setVisibility(View.GONE);
                            }
                            else if (sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getRazorPay().equalsIgnoreCase("1")&& sessionManagement.getPayStack().equalsIgnoreCase("1")) {
                                paypal = false;
                                razorpay = true;
                                paystack = true;
                                paypal_lay.setVisibility(View.GONE);
                                dropdown.setVisibility(View.VISIBLE);
                                rb_card.setVisibility(View.GONE);
                            }
                            else {
                                if (sessionManagement.getPayPal().equalsIgnoreCase("1")&& sessionManagement.getRazorPay().equalsIgnoreCase("0")&& sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                                    paypal = true;
                                    paystack = false;
                                    razorpay = false;
                                }
                                if (sessionManagement.getRazorPay().equalsIgnoreCase("1") && sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                                    paypal = false;
                                    paystack = false;
                                    razorpay = true;
                                }
                                if (sessionManagement.getPayStack().equalsIgnoreCase("1")&&sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getRazorPay().equalsIgnoreCase("0")) {
                                    paypal = false;
                                    razorpay = false;
                                    paystack = true;
                                }
                                dropdown.setVisibility(View.GONE);
                                rb_card.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<PaymentVia> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                progressDialog.setMessage("Payment is in processing...");
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_payment);

        sessionManagement = new Session_management(this);
        getwallet = SharedPref.getString(getApplicationContext(), BaseURL.KEY_WALLET_Ammount);
        db_cart = new DatabaseHandler(this);
        getuser_id = sessionManagement.userId();
        progressDialog = new ProgressDialog(PaymentDetails.this);
        progressDialog.setMessage("Payment is in processing...");
        progressDialog.setCancelable(false);
        llwallet = findViewById(R.id.llwallet);
        coupon_apply_t = findViewById(R.id.coupon_apply_t);
        llpromocode = findViewById(R.id.llpromocode);
        llcod = findViewById(R.id.llcod);
        llcards = findViewById(R.id.llcards);

        paystack_lay = findViewById(R.id.paystack_lay);
        use_paystack = findViewById(R.id.use_paystack);
        paypaystack_txt = findViewById(R.id.paypaystack_txt);

        dropdown = findViewById(R.id.dropdown);
        payment_opt = findViewById(R.id.payment_opt);
        use_paypal = findViewById(R.id.use_paypal);
        razor_pay = findViewById(R.id.razor_pay);
        paypal_lay = findViewById(R.id.paypal_lay);
        use_razorpay = findViewById(R.id.use_razorpay);
        paypal_txt = findViewById(R.id.paypal_txt);
        raz_txt = findViewById(R.id.raz_txt);

        backcart = findViewById(R.id.backcart);
        confirm = findViewById(R.id.confirm_order);
//        cart_id = OrderSummary.cart_id;
        cart_id = sessionManagement.getCartId();
        linea = findViewById(R.id.line1);
        lineb = findViewById(R.id.line2);
        reset_text = findViewById(R.id.reset_text);
        twallet = findViewById(R.id.walletext);
        tcod = findViewById(R.id.txtcod);
        tcards = findViewById(R.id.txtcards);
        tpromocode = findViewById(R.id.txtpromo);
        checkBox_Wallet = findViewById(R.id.use_wallet);
        rb_Store = findViewById(R.id.use_store_pickup);
        rb_Cod = findViewById(R.id.use_COD);
        rb_card = findViewById(R.id.use_card);
        checkBox_coupon = findViewById(R.id.use_coupon);
        et_Coupon = findViewById(R.id.et_coupon_code);
        Promo_code_layout = findViewById(R.id.prommocode_layout);
        Apply_Coupon_Code = findViewById(R.id.apply_coupoun_code);
        radioGroup = findViewById(R.id.radio_group);
        my_wallet_ammount = findViewById(R.id.user_wallet);
        order_ammount = findViewById(R.id.order_ammount);
        used_wallet_ammount = findViewById(R.id.used_wallet_ammount);
        used_coupon_ammount = findViewById(R.id.used_coupon_ammount);
        coupuntxt = findViewById(R.id.coupuntxt);
        coupuntxt.setText("Apply");
        checkBox_Wallet.setClickable(false);
        rb_card.setClickable(false);
        rb_Cod.setClickable(false);
        checkBox_coupon.setClickable(false);
        use_paypal.setClickable(false);
        use_razorpay.setClickable(false);

        checkUserPayNotify();

        reset_text.setOnClickListener(v -> {
            checkBox_Wallet.setClickable(false);
            rb_card.setClickable(false);
            rb_Cod.setClickable(false);
            checkBox_coupon.setClickable(false);
            use_razorpay.setClickable(false);
            use_paypal.setClickable(false);
            use_paystack.setClickable(false);
            use_razorpay.setChecked(false);
            use_paypal.setChecked(false);
            use_paystack.setChecked(false);
            total_amount = getIntent().getStringExtra("order_amt");
            payable_amt = getIntent().getStringExtra("order_amt");
            code = "";
            payment_method = "";
            wallet_status = "no";
            getwallet = SharedPref.getString(getApplicationContext(), BaseURL.KEY_WALLET_Ammount);
            wallet_amount = getwallet;
            walletbalnce = 0;
            my_wallet_ammount.setText(sessionManagement.getCurrency() + "" + wallet_amount);
            order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
            my_wallet_ammount.setTextColor(getResources().getColor(R.color.black));
            checkBox_Wallet.setChecked(false);
            llwallet.setBackgroundResource(R.drawable.border_rounded1);
            twallet.setTextColor(getResources().getColor(R.color.black));
            checkBox_coupon.setChecked(false);
            llpromocode.setBackgroundResource(R.drawable.border_rounded1);
            tpromocode.setTextColor(getResources().getColor(R.color.black));
            rb_Cod.setChecked(false);
            llcod.setBackgroundResource(R.drawable.border_rounded1);
            tcod.setTextColor(getResources().getColor(R.color.black));
            rb_card.setChecked(false);
            llcards.setBackgroundResource(R.drawable.border_rounded1);
            tcards.setTextColor(getResources().getColor(R.color.black));
            et_Coupon.setText("");
            Promo_code_layout.setVisibility(View.GONE);
            coupon_apply_t.setVisibility(View.VISIBLE);
            Promo_code_layout.setClickable(true);
            llcards.setClickable(true);
            llcod.setClickable(true);
            llpromocode.setClickable(true);
            razor_pay.setClickable(true);
            paypal_lay.setClickable(true);
            paystack_lay.setClickable(true);
//            if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("1")) {
//                paypal = true;
//                razorpay = true;
//                dropdown.setVisibility(View.VISIBLE);
////                    payment_opt.setVisibility(View.VISIBLE);
//                rb_card.setVisibility(View.GONE);
//            } else {
//                if (sessionManagement.getPayPal().equalsIgnoreCase("1")) {
//                    paypal = true;
//                    razorpay = false;
//                }
//                if (sessionManagement.getRazorPay().equalsIgnoreCase("1")) {
//                    paypal = false;
//                    razorpay = true;
//                }
//                dropdown.setVisibility(View.GONE);
////                    payment_opt.setVisibility(View.GONE);
//                rb_card.setVisibility(View.VISIBLE);
//            }
            if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("1")&& sessionManagement.getPayStack().equalsIgnoreCase("1")) {
                paypal = true;
                razorpay = true;
                paystack = true;
                dropdown.setVisibility(View.VISIBLE);
//            payment_opt.setVisibility(View.VISIBLE);
                rb_card.setVisibility(View.GONE);
            }
            else if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("1")&& sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                paypal = true;
                razorpay = true;
                paystack = false;
                paystack_lay.setVisibility(View.GONE);
                dropdown.setVisibility(View.VISIBLE);
                rb_card.setVisibility(View.GONE);
            }else if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("0")&& sessionManagement.getPayStack().equalsIgnoreCase("1")) {
                paypal = true;
                paystack = true;
                razorpay = false;
                razor_pay.setVisibility(View.GONE);
                dropdown.setVisibility(View.VISIBLE);
                rb_card.setVisibility(View.GONE);
            }else if (sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getRazorPay().equalsIgnoreCase("1")&& sessionManagement.getPayStack().equalsIgnoreCase("1")) {
                paypal = false;
                razorpay = true;
                paystack = true;
                paypal_lay.setVisibility(View.GONE);
                dropdown.setVisibility(View.VISIBLE);
                rb_card.setVisibility(View.GONE);
            }else {
                if (sessionManagement.getPayPal().equalsIgnoreCase("1")&& sessionManagement.getRazorPay().equalsIgnoreCase("0")&& sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                    paypal = true;
                    paystack = false;
                    razorpay = false;
                }
                else if (sessionManagement.getRazorPay().equalsIgnoreCase("1") && sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                    paypal = false;
                    paystack = false;
                    razorpay = true;
                }
                else if (sessionManagement.getPayStack().equalsIgnoreCase("1")&&sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getRazorPay().equalsIgnoreCase("0")) {
                    paypal = false;
                    razorpay = false;
                    paystack = true;
                }
                dropdown.setVisibility(View.GONE);
                rb_card.setVisibility(View.VISIBLE);
            }
            payment_opt.setVisibility(View.GONE);
            razor_pay.setBackgroundResource(R.drawable.border_rounded1);
            raz_txt.setTextColor(getResources().getColor(R.color.black));
            paypal_lay.setBackgroundResource(R.drawable.border_rounded1);
            paypal_txt.setTextColor(getResources().getColor(R.color.black));
            paystack_lay.setBackgroundResource(R.drawable.border_rounded1);
            paypaystack_txt.setTextColor(getResources().getColor(R.color.black));
        });
        total_amount = getIntent().getStringExtra("order_amt");
        payable_amt = getIntent().getStringExtra("order_amt");

        order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());

        if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("1")&& sessionManagement.getPayStack().equalsIgnoreCase("1")) {
            paypal = true;
            razorpay = true;
            paystack = true;
            dropdown.setVisibility(View.VISIBLE);
//            payment_opt.setVisibility(View.VISIBLE);
            rb_card.setVisibility(View.GONE);
        } if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("1")&& sessionManagement.getPayStack().equalsIgnoreCase("0")) {
            paypal = true;
            razorpay = true;
            paystack = false;
            paystack_lay.setVisibility(View.GONE);
            dropdown.setVisibility(View.VISIBLE);
            rb_card.setVisibility(View.GONE);
        }if (sessionManagement.getPayPal().equalsIgnoreCase("1") && sessionManagement.getRazorPay().equalsIgnoreCase("0")&& sessionManagement.getPayStack().equalsIgnoreCase("1")) {
            paypal = true;
            paystack = true;
            razorpay = false;
            razor_pay.setVisibility(View.GONE);
            dropdown.setVisibility(View.VISIBLE);
            rb_card.setVisibility(View.GONE);
        }if (sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getRazorPay().equalsIgnoreCase("1")&& sessionManagement.getPayStack().equalsIgnoreCase("1")) {
            paypal = false;
            razorpay = true;
            paystack = true;
            paypal_lay.setVisibility(View.GONE);
            dropdown.setVisibility(View.VISIBLE);
            rb_card.setVisibility(View.GONE);
        }else {
            if (sessionManagement.getPayPal().equalsIgnoreCase("1")&& sessionManagement.getRazorPay().equalsIgnoreCase("0")&& sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                paypal = true;
                paystack = false;
                razorpay = false;
            }
            if (sessionManagement.getRazorPay().equalsIgnoreCase("1") && sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getPayStack().equalsIgnoreCase("0")) {
                paypal = false;
                paystack = false;
                razorpay = true;
            }
            if (sessionManagement.getPayStack().equalsIgnoreCase("1")&&sessionManagement.getPayPal().equalsIgnoreCase("0") && sessionManagement.getRazorPay().equalsIgnoreCase("0")) {
                paypal = false;
                razorpay = false;
                paystack = true;
            }
            dropdown.setVisibility(View.GONE);
            rb_card.setVisibility(View.VISIBLE);
        }

        dropdown.setOnClickListener(v -> {
            if (payment_opt.getVisibility() == View.VISIBLE) {
                payment_opt.setVisibility(View.GONE);
                dropdown.setBackground(getResources().getDrawable(R.drawable.ic_arrow_righ_new));
            } else {
                payment_opt.setVisibility(View.VISIBLE);
                dropdown.setBackground(getResources().getDrawable(R.drawable.ic_down_new));
            }
        });

        backcart.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        });

        sharedPreferences12 = getSharedPreferences("loction", MODE_PRIVATE);
        editor12 = sharedPreferences12.edit();
        lat = sharedPreferences12.getString("lat", "77.1111");
        lng = sharedPreferences12.getString("lng", "22.02002");

//        code = getIntent().getStringExtra("code");

//        Log.d("dff", code);
//        checkBox_coupon.setTypeface(font);

        Apply_Coupon_Code.setOnClickListener(v -> {
            if (!coupuntxt.getText().toString().trim().equalsIgnoreCase("Applied")) {
//                startActivity(new Intent(PaymentDetails.this, Coupen.class));
                code = et_Coupon.getText().toString().trim();
                apply();
            }
//            apply();
        });


        getRefresrh();
        rewardliness();

        final String email = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
        final String mobile = sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE);
        final String name = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (rb_Cod.isChecked() && checkBox_Wallet.isChecked()) {
                    wallet_status = "yes";
                    payment_method = "COD";
                    makeAddOrderRequest(getuser_id, cart_id, payment_method, wallet_status, "success");
                } else if (rb_card.isChecked() && checkBox_Wallet.isChecked()) {
                    wallet_status = "yes";
                    payment_method = "card";
                    if (razorpay){
                        startPayment(name, total_amount, email, mobile);
                    }else if (paypal){
                        startPaypal(name,total_amount,email,mobile);
                    }else if (paystack){
                        Intent intent = new Intent(PaymentDetails.this,PaystackPayment.class);
                        intent.putExtra("getuser_id",getuser_id);
                        intent.putExtra("cart_id",cart_id);
                        intent.putExtra("payment_method",payment_method);
                        intent.putExtra("wallet_status",wallet_status);
                        intent.putExtra("total_amount",total_amount);
                        intent.putExtra("activity","non_wallet");
                        startActivity(intent);
                    }

                } else if (rb_card.isChecked()) {
                    wallet_status = "no";
                    payment_method = "card";
                    if (razorpay){
                        startPayment(name, total_amount, email, mobile);
                    }else if (paypal){
                        startPaypal(name,total_amount,email,mobile);
                    }else if (paystack){
                        Intent intent = new Intent(PaymentDetails.this,PaystackPayment.class);
                        intent.putExtra("getuser_id",getuser_id);
                        intent.putExtra("cart_id",cart_id);
                        intent.putExtra("payment_method",payment_method);
                        intent.putExtra("wallet_status",wallet_status);
                        intent.putExtra("total_amount",total_amount);
                        intent.putExtra("activity","non_wallet");
                        startActivity(intent);
                    }
                }else if (use_razorpay.isChecked() && checkBox_Wallet.isChecked()) {
                    wallet_status = "yes";
                    payment_method = "card";
                    startPayment(name, total_amount, email, mobile);
                } else if (use_razorpay.isChecked()) {
                    wallet_status = "no";
                    payment_method = "card";
                    startPayment(name, total_amount, email, mobile);
                }else if (use_paypal.isChecked() && checkBox_Wallet.isChecked()) {
                    wallet_status = "yes";
                    payment_method = "card";
                    startPaypal(name,total_amount,email,mobile);
//                    startPayment(name, total_amount, email, mobile);
                } else if (use_paypal.isChecked()) {
                    wallet_status = "no";
                    payment_method = "card";
                    startPaypal(name,total_amount,email,mobile);
//                    startPayment(name, total_amount, email, mobile);
                }else if (use_paystack.isChecked() && checkBox_Wallet.isChecked()) {
                    wallet_status = "yes";
                    payment_method = "card";
                    Intent intent = new Intent(PaymentDetails.this,PaystackPayment.class);
                    intent.putExtra("getuser_id",getuser_id);
                    intent.putExtra("cart_id",cart_id);
                    intent.putExtra("payment_method",payment_method);
                    intent.putExtra("wallet_status",wallet_status);
                    intent.putExtra("total_amount",total_amount);
                    intent.putExtra("activity","non_wallet");
                    startActivity(intent);
                } else if (use_paystack.isChecked()) {
                    wallet_status = "no";
                    payment_method = "card";
                    Intent intent = new Intent(PaymentDetails.this,PaystackPayment.class);
                    intent.putExtra("getuser_id",getuser_id);
                    intent.putExtra("cart_id",cart_id);
                    intent.putExtra("payment_method",payment_method);
                    intent.putExtra("wallet_status",wallet_status);
                    intent.putExtra("total_amount",total_amount);
                    intent.putExtra("activity","non_wallet");
                    startActivity(intent);
                }
                else if (rb_Cod.isChecked()) {
                    wallet_status = "no";
                    payment_method = "COD";
                    makeAddOrderRequest(getuser_id, cart_id, payment_method, wallet_status, "success");
                } else if (checkBox_Wallet.isChecked()) {
                    wallet_status = "yes";
                    payment_method = "wallet";
                    if (walletbalnce == 0.0 && Double.parseDouble(total_amount) > 0.0) {
                        progressDialog.dismiss();
                        Toast.makeText(PaymentDetails.this, "Select Card Or COD", Toast.LENGTH_SHORT).show();
                    } else {
                        makeAddOrderRequest(getuser_id, cart_id, payment_method, wallet_status, "success");
                    }

                } else {
                    if (checkBox_coupon.isChecked()) {
                        if (Double.parseDouble(total_amount) > 0.0) {
                            progressDialog.dismiss();
                            Toast.makeText(PaymentDetails.this, "Select One plz", Toast.LENGTH_SHORT).show();
                        } else {
                            wallet_status = "no";
                            payment_method = "promocode";
                            makeAddOrderRequest(getuser_id, cart_id, payment_method, wallet_status, "success");
                        }
                    }
                }
            }
        });

        llcards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dropdown.getVisibility() == View.GONE) {
                    if (total_amount.equalsIgnoreCase("0") || total_amount.equalsIgnoreCase("0.0") || total_amount.equalsIgnoreCase("")) {

                    } else {
                        if (!rb_card.isChecked()) {
                            rb_card.setChecked(true);
                            rb_Cod.setChecked(false);
                            llcards.setBackgroundResource(R.drawable.gradientbg);
                            tcards.setTextColor(getResources().getColor(R.color.white));
                            llcod.setBackgroundResource(R.drawable.border_rounded1);
                            tcod.setTextColor(getResources().getColor(R.color.black));
                            if (checkBox_Wallet.isChecked()) {
                                wallet_status = "yes";
                            } else {
                                wallet_status = "no";
                            }
                            payment_method = "cards";
                        } else {
                            rb_card.setChecked(false);
                            llcards.setBackgroundResource(R.drawable.border_rounded1);
                            tcards.setTextColor(getResources().getColor(R.color.black));
                            if (checkBox_Wallet.isChecked()) {
                                wallet_status = "yes";
                                payment_method = "wallet";
                            } else {
                                payment_method = "";
                                wallet_status = "no";
                            }
                        }
                    }

                }
                checkBox_Wallet.setClickable(false);
                rb_card.setClickable(false);
                rb_Cod.setClickable(false);
                checkBox_coupon.setClickable(false);
                use_razorpay.setClickable(false);
                use_paypal.setClickable(false);
            }
        });

        razor_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total_amount.equalsIgnoreCase("0") || total_amount.equalsIgnoreCase("0.0") || total_amount.equalsIgnoreCase("")) {

                } else {
                    if (!use_razorpay.isChecked()) {
                        use_razorpay.setChecked(true);
                        use_paystack.setChecked(false);
                        use_paypal.setChecked(false);
                        rb_Cod.setChecked(false);
                        razor_pay.setBackgroundResource(R.drawable.gradientbg);
                        raz_txt.setTextColor(getResources().getColor(R.color.white));
                        paypal_lay.setBackgroundResource(R.drawable.border_rounded1);
                        paypal_txt.setTextColor(getResources().getColor(R.color.black));
                        llcod.setBackgroundResource(R.drawable.border_rounded1);
                        tcod.setTextColor(getResources().getColor(R.color.black));
                        paystack_lay.setBackgroundResource(R.drawable.border_rounded1);
                        paypaystack_txt.setTextColor(getResources().getColor(R.color.black));
                        if (checkBox_Wallet.isChecked()) {
                            wallet_status = "yes";
                        } else {
                            wallet_status = "no";
                        }
                        payment_method = "cards";
                    } else {
                        use_razorpay.setChecked(false);
//                        use_paypal.setChecked(false);
//                        rb_Cod.setChecked(false);
                        razor_pay.setBackgroundResource(R.drawable.border_rounded1);
                        raz_txt.setTextColor(getResources().getColor(R.color.black));
                        if (checkBox_Wallet.isChecked()) {
                            wallet_status = "yes";
                            payment_method = "wallet";
                        } else {
                            payment_method = "";
                            wallet_status = "no";
                        }
                    }
                }


                checkBox_Wallet.setClickable(false);
                rb_card.setClickable(false);
                rb_Cod.setClickable(false);
                checkBox_coupon.setClickable(false);
                use_razorpay.setClickable(false);
                use_paypal.setClickable(false);
                use_paystack.setClickable(false);
            }
        });

        paypal_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total_amount.equalsIgnoreCase("0") || total_amount.equalsIgnoreCase("0.0") || total_amount.equalsIgnoreCase("")) {

                } else {
                    if (!use_paypal.isChecked()) {
                        use_paypal.setChecked(true);
                        use_razorpay.setChecked(false);
                        use_paystack.setChecked(false);
                        rb_Cod.setChecked(false);
                        paypal_lay.setBackgroundResource(R.drawable.gradientbg);
                        paypal_txt.setTextColor(getResources().getColor(R.color.white));
                        llcod.setBackgroundResource(R.drawable.border_rounded1);
                        tcod.setTextColor(getResources().getColor(R.color.black));
                        razor_pay.setBackgroundResource(R.drawable.border_rounded1);
                        raz_txt.setTextColor(getResources().getColor(R.color.black));
                        paystack_lay.setBackgroundResource(R.drawable.border_rounded1);
                        paypaystack_txt.setTextColor(getResources().getColor(R.color.black));
                        if (checkBox_Wallet.isChecked()) {
                            wallet_status = "yes";
                        } else {
                            wallet_status = "no";
                        }
                        payment_method = "cards";
                    } else {
                        use_paypal.setChecked(false);
                        paypal_lay.setBackgroundResource(R.drawable.border_rounded1);
                        paypal_txt.setTextColor(getResources().getColor(R.color.black));
                        if (checkBox_Wallet.isChecked()) {
                            wallet_status = "yes";
                            payment_method = "wallet";
                        } else {
                            payment_method = "";
                            wallet_status = "no";
                        }
                    }
                }


                checkBox_Wallet.setClickable(false);
                rb_card.setClickable(false);
                rb_Cod.setClickable(false);
                checkBox_coupon.setClickable(false);
                use_razorpay.setClickable(false);
                use_paypal.setClickable(false);
                use_paystack.setClickable(false);
            }
        });

        paystack_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total_amount.equalsIgnoreCase("0") || total_amount.equalsIgnoreCase("0.0") || total_amount.equalsIgnoreCase("")) {

                } else {
                    if (!use_paystack.isChecked()) {
                        use_paystack.setChecked(true);
                        use_paypal.setChecked(false);
                        use_razorpay.setChecked(false);
                        rb_Cod.setChecked(false);
                        paystack_lay.setBackgroundResource(R.drawable.gradientbg);
                        paypaystack_txt.setTextColor(getResources().getColor(R.color.white));
                        paypal_lay.setBackgroundResource(R.drawable.border_rounded1);
                        paypal_txt.setTextColor(getResources().getColor(R.color.black));
                        llcod.setBackgroundResource(R.drawable.border_rounded1);
                        tcod.setTextColor(getResources().getColor(R.color.black));
                        razor_pay.setBackgroundResource(R.drawable.border_rounded1);
                        raz_txt.setTextColor(getResources().getColor(R.color.black));
                        if (checkBox_Wallet.isChecked()) {
                            wallet_status = "yes";
                        } else {
                            wallet_status = "no";
                        }
                        payment_method = "cards";
                    } else {
                        use_paypal.setChecked(false);
                        use_paystack.setChecked(false);
                        paystack_lay.setBackgroundResource(R.drawable.border_rounded1);
                        paypaystack_txt.setTextColor(getResources().getColor(R.color.black));
                        paypal_lay.setBackgroundResource(R.drawable.border_rounded1);
                        paypal_txt.setTextColor(getResources().getColor(R.color.black));
                        if (checkBox_Wallet.isChecked()) {
                            wallet_status = "yes";
                            payment_method = "wallet";
                        } else {
                            payment_method = "";
                            wallet_status = "no";
                        }
                    }
                }


                checkBox_Wallet.setClickable(false);
                rb_card.setClickable(false);
                rb_Cod.setClickable(false);
                checkBox_coupon.setClickable(false);
                use_razorpay.setClickable(false);
                use_paypal.setClickable(false);
                use_paystack.setClickable(false);
            }
        });

        llcod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total_amount.equalsIgnoreCase("0") || total_amount.equalsIgnoreCase("0.0") || total_amount.equalsIgnoreCase("")) {

                } else {
                    if (!rb_Cod.isChecked()) {
                        rb_Cod.setChecked(true);
                        rb_card.setChecked(false);
                        llcod.setBackgroundResource(R.drawable.gradientbg);
                        tcod.setTextColor(getResources().getColor(R.color.white));
                        llcards.setBackgroundResource(R.drawable.border_rounded1);
                        tcards.setTextColor(getResources().getColor(R.color.black));
                        if (checkBox_Wallet.isChecked()) {
                            wallet_status = "yes";
                        } else {
                            wallet_status = "no";
                        }
                        payment_method = "COD";
                    } else {
                        rb_Cod.setChecked(false);
                        rb_card.setChecked(false);
                        llcod.setBackgroundResource(R.drawable.border_rounded1);
                        tcod.setTextColor(getResources().getColor(R.color.black));
                        if (checkBox_Wallet.isChecked()) {
                            wallet_status = "yes";
                            payment_method = "wallet";
                        } else {
                            wallet_status = "no";
                            payment_method = "";
                        }
                    }
                }

                checkBox_Wallet.setClickable(false);
                rb_card.setClickable(false);
                rb_Cod.setClickable(false);
                checkBox_coupon.setClickable(false);
                use_razorpay.setClickable(false);
                use_paypal.setClickable(false);
            }
        });

        llwallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox_Wallet.isChecked()) {
                    checkBox_Wallet.setChecked(true);
                    llwallet.setBackgroundResource(R.drawable.gradientbg);
                    twallet.setTextColor(getResources().getColor(R.color.white));
                    my_wallet_ammount.setTextColor(getResources().getColor(R.color.white));
                    double amt = Double.parseDouble(total_amount);
                    double wallet = Double.parseDouble(wallet_amount);
                    if (wallet > 0) {
                        if (amt <= wallet) {
                            walletbalnce = wallet - amt;
                            total_amount = "0";
                            llcards.setClickable(false);
                            llcod.setClickable(false);
                            llpromocode.setClickable(false);
                            my_wallet_ammount.setText(sessionManagement.getCurrency() + "" + walletbalnce);
                            order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
                        } else {
                            walletbalnce = 0;
                            total_amount = String.valueOf((amt - wallet));
                            my_wallet_ammount.setText(sessionManagement.getCurrency() + "" + walletbalnce);
                            order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
//                        startActivity(new Intent(getApplicationContext(), RechargeWallet.class));
                        }
                        wallet_status = "yes";
                        payment_method = "wallet";
                    } else {
                        wallet_status = "no";
                        payment_method = "";
                        checkBox_Wallet.setChecked(false);
                        llwallet.setBackgroundResource(R.drawable.border_rounded1);
                        twallet.setTextColor(getResources().getColor(R.color.black));
                        my_wallet_ammount.setTextColor(getResources().getColor(R.color.black));
                        double wallett = Double.parseDouble(wallet_amount);
                        my_wallet_ammount.setText(sessionManagement.getCurrency() + wallett);
                        total_amount = payable_amt;
                        walletbalnce = wallet;
                        order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
                        wallet_status = "no";
                        payment_method = "";
                        llcod.setClickable(true);
                        llcards.setClickable(true);
                        llpromocode.setClickable(true);
                        startActivityForResult(new Intent(PaymentDetails.this, RechargeWallet.class), 5);
                    }

                } else {
                    checkBox_Wallet.setChecked(false);
                    llwallet.setBackgroundResource(R.drawable.border_rounded1);
                    twallet.setTextColor(getResources().getColor(R.color.black));
                    my_wallet_ammount.setTextColor(getResources().getColor(R.color.black));
                    double wallet = Double.parseDouble(wallet_amount);
                    my_wallet_ammount.setText(sessionManagement.getCurrency() + "" + wallet);

                    if (checkBox_coupon.isChecked()) {
                        if (coupon_amount != null && !coupon_amount.equalsIgnoreCase("")) {
                            double amountd = Double.parseDouble(payable_amt) - Double.parseDouble(coupon_amount);
                            total_amount = String.valueOf(amountd);
                            walletbalnce = wallet;
                            order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
                            wallet_status = "no";
                            payment_method = "promocode";
                        } else {
                            total_amount = payable_amt;
                            walletbalnce = wallet;
                            order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
                            wallet_status = "no";
                            payment_method = "";
                        }

                    } else {
                        total_amount = payable_amt;
                        walletbalnce = wallet;
                        order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
                        wallet_status = "no";
                        payment_method = "";
                    }
                    llcod.setClickable(true);
                    llcards.setClickable(true);
                    llpromocode.setClickable(true);
                }

                checkBox_Wallet.setClickable(false);
                rb_card.setClickable(false);
                rb_Cod.setClickable(false);
                checkBox_coupon.setClickable(false);
                use_razorpay.setClickable(false);
                use_paypal.setClickable(false);
            }
        });

        llpromocode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox_coupon.isChecked()) {
                    coupon_apply_t.setVisibility(View.GONE);
                    checkBox_coupon.setChecked(true);
                    llpromocode.setBackgroundResource(R.drawable.gradientbg);
                    tpromocode.setTextColor(getResources().getColor(R.color.white));
                    Intent coupounIntent = new Intent(PaymentDetails.this, Coupen.class);
                    startActivityForResult(coupounIntent, 2);
                } else {
                    coupon_apply_t.setVisibility(View.VISIBLE);
                    checkBox_coupon.setChecked(false);
                    llpromocode.setBackgroundResource(R.drawable.border_rounded1);
                    tpromocode.setTextColor(getResources().getColor(R.color.black));
                    et_Coupon.setText("");
                    Promo_code_layout.setVisibility(View.GONE);
                    Promo_code_layout.setClickable(true);
                    if (code != null && !code.equalsIgnoreCase("")) {
                        apply();
                    }

                }

                checkBox_Wallet.setClickable(false);
                rb_card.setClickable(false);
                rb_Cod.setClickable(false);
                checkBox_coupon.setClickable(false);
                use_razorpay.setClickable(false);
                use_paypal.setClickable(false);
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
        getPayment(total_amount,config);
    }

    private void getPayment(String total_rs, PayPalConfiguration config) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(total_rs), "USD", "Shopping Fee", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }


    private void rewardliness() {

        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("cart_id", cart_id);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.rewardlines, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", response.toString());

                try {

                    String status = response.getString("status");
                    String message = response.getString("message");

                    if (status.contains("1")) {


                        String line1 = response.getString("line1");
                        String line2 = response.getString("line2");
                        linea.setVisibility(View.VISIBLE);
                        lineb.setVisibility(View.VISIBLE);
                        if (line1.equalsIgnoreCase("")) {
                            linea.setVisibility(View.GONE);
                            lineb.setText(line2);
                        }
                        if (line2.equalsIgnoreCase("")) {
                            lineb.setVisibility(View.GONE);
                            linea.setText(line1);

                        }
                        linea.setText(line1);
                        lineb.setText(line2);


                    } else {
                        linea.setVisibility(View.GONE);
                        lineb.setVisibility(View.GONE);

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
        jsonObjReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().getRequestQueue().add(jsonObjReq);


    }


    private void apply() {
        progressDialog.show();
        String tag_json_obj = "json_order_detail_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("cart_id", cart_id);
        params.put("coupon_code", code);
//        Log.d("ssd", cart_id);
//        Log.d("dd", code);


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.apply_coupon, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", response.toString());
                try {

                    String statuss = response.getString("status");
                    String message = response.getString("message");
                    if (statuss.contains("1")) {
                        coupounApplied = true;
                        JSONObject jsonObject = response.getJSONObject("data");
                        remaingprice = jsonObject.getString("rem_price");
                        String deliverycharge = jsonObject.getString("delivery_charge");
                        coupuntxt.setText("Applied");
                        coupon_amount = jsonObject.getString("coupon_discount");
                        if (checkBox_Wallet.isChecked()) {
                            double remInt = Double.parseDouble(remaingprice);
                            double wallet = Double.parseDouble(wallet_amount);

                            if (wallet >= remInt) {
                                walletbalnce = (int) (wallet - remInt);
                                total_amount = "0";
                                rb_card.setClickable(false);
                                rb_Cod.setClickable(false);
                                checkBox_coupon.setClickable(true);
                                my_wallet_ammount.setText(sessionManagement.getCurrency() + walletbalnce);
                                order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
                            } else {
                                if (wallet == 0.0) {
                                    total_amount = remaingprice;
                                    order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
                                } else {
                                    walletbalnce = 0;
                                    total_amount = String.valueOf((remInt - wallet));
                                    order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
                                }
                            }

                        } else {
                            total_amount = remaingprice;
                            order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
                        }
                        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                    } else if (statuss.contains("2")) {
                        coupounApplied = false;
                        code = "";
                        status = 1;
                        if (checkBox_Wallet.isChecked()) {
                            double amt = Double.parseDouble(payable_amt);
                            double wallet = Double.parseDouble(wallet_amount);
                            if (wallet > 0) {
                                if (amt <= wallet) {
                                    walletbalnce = wallet - amt;
                                    total_amount = "0";
                                    rb_card.setClickable(false);
                                    rb_Cod.setClickable(false);
                                    checkBox_coupon.setClickable(false);
                                    my_wallet_ammount.setText(sessionManagement.getCurrency() + "" + walletbalnce);
                                    order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
                                } else {
                                    walletbalnce = 0;
                                    total_amount = String.valueOf((amt - wallet));
                                    order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
//                        startActivity(new Intent(getApplicationContext(), RechargeWallet.class));
                                }
                            }
                        } else {
                            total_amount = payable_amt;
                            order_ammount.setText(total_amount + " " + sessionManagement.getCurrency());
                        }
                    } else {
                        coupounApplied = false;
                        code = "";
                        status = 1;
                        Promo_code_layout.setVisibility(View.GONE);
                        coupon_apply_t.setVisibility(View.VISIBLE);
                        checkBox_coupon.setChecked(false);
                        llpromocode.setBackgroundResource(R.drawable.border_rounded1);
                        tpromocode.setTextColor(getResources().getColor(R.color.black));
                        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                }
            }
        });

        // Adding request to request queue
        jsonObjReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
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

//    private void attemptOrder() {
//        ArrayList<HashMap<String, String>> items = db_cart.getCartAll();
//        rewards = Double.parseDouble(db_cart.getColumnRewards());
//        if (items.size() > 0) {
//            JSONArray passArray = new JSONArray();
//            for (int i = 0; i < items.size(); i++) {
//                HashMap<String, String> map = items.get(i);
//                JSONObject jObjP = new JSONObject();
//                try {
//                    jObjP.put("product_id", map.get("product_id"));
//                    jObjP.put("qty", map.get("qty"));
//                    jObjP.put("unit_value", map.get("unit_value"));
//                    jObjP.put("unit", map.get("unit"));
//                    jObjP.put("price", map.get("price"));
//                    jObjP.put("rewards", map.get("rewards"));
//                    passArray.put(jObjP);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            getuser_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
//
//            if (ConnectivityReceiver.isConnected()) {
//
//                Log.e(TAG, "from:" + gettime + "\ndate:" + getdate +
//                        "\n" + "\nuser_id:" + getuser_id + "\n" + getlocation_id + getstore_id + "\ndata:" + passArray.toString());
//
//
////                makeAddOrderRequest(cart_id,getuser_id);
//
//
//            }
//        }
//    }

    private void makeAddOrderRequest(String userid, String cart_id, String payment_method, String wallet_status, String payment_status) {
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
                        Toast.makeText(PaymentDetails.this, "" + message, Toast.LENGTH_SHORT).show();
                    } else if (status.equalsIgnoreCase("2")) {
                        sessionManagement.setCartID("");
                        JSONObject jsonObject = response.getJSONObject("data");
                        db_cart.clearCart();
                        Intent intent = new Intent(getApplicationContext(), OrderSuccessful.class);
                        intent.putExtra("msg", message);
                        startActivity(intent);
                        Toast.makeText(PaymentDetails.this, "" + message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PaymentDetails.this, "" + message, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
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

//    private void Usewalletfororder(String userid, String Wallet) {
//        String tag_json_obj = "json_add_order_req";
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("user_id", userid);
//        params.put("wallet_amount", Wallet);
//
//        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST, Wallet_CHECKOUT, params, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, response.toString());
//
//                try {
//                    String status = response.getString("responce");
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//
//            }
//        });
//        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//    }
//
//    private void Use_Wallet_Ammont() {
//        final String Wallet_Ammount = SharedPref.getString(getApplicationContext(), BaseURL.KEY_WALLET_Ammount);
//        final String Coupon_Ammount = SharedPref.getString(getApplicationContext(), BaseURL.COUPON_TOTAL_AMOUNT);
//        final String Ammount = SharedPref.getString(getApplicationContext(), BaseURL.TOTAL_AMOUNT);
//        if (NetworkConnection.connectionChecking(getApplicationContext())) {
//            RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
//            StringRequest postReq = new StringRequest(Request.Method.POST, BaseURL.BASE_URL + Wallet_CHECKOUT,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Log.i("eclipse", "Response=" + response);
//                            try {
//                                JSONObject object = new JSONObject(response);
//                                JSONArray Jarray = object.getJSONArray("final_amount");
//                                for (int i = 0; i < Jarray.length(); i++) {
//                                    JSONObject json_data = Jarray.getJSONObject(i);
//                                    String Wallet_amount = json_data.getString("wallet");
//                                    Used_Wallet_amount = json_data.getString("used_wallet");
//                                    total_amount = json_data.getString("total");
//                                    if (total_amount.equals("0")) {
//                                        rb_Cod.setText("Home Delivery");
//                                        getvalue = rb_Cod.getText().toString();
//                                        rb_card.setClickable(false);
//                                        rb_card.setTextColor(getResources().getColor(R.color.hintColor));
////                                        rb_Netbanking.setClickable(false);
////
////                                        rb_Netbanking.setTextColor(getResources().getColor(R.color.hintColor));
////                                        rb_paytm.setClickable(false);
////                                        rb_paytm.setTextColor(getResources().getColor(R.color.hintColor));
//                                        checkBox_coupon.setClickable(false);
//                                        checkBox_coupon.setTextColor(getResources().getColor(R.color.hintColor));
//                                    } else {
//                                        if (total_amount != null) {
//                                            rb_Cod.setText("Cash On Delivery");
//                                            rb_card.setClickable(true);
//                                            rb_card.setTextColor(getResources().getColor(R.color.black));
////                                            rb_Netbanking.setClickable(true);
////                                            rb_Netbanking.setTextColor(getResources().getColor(R.color.black));
////                                            rb_paytm.setClickable(true);
////                                            rb_paytm.setTextColor(getResources().getColor(R.color.black));
//                                            checkBox_coupon.setClickable(true);
//                                            checkBox_coupon.setTextColor(getResources().getColor(R.color.black));
//                                        }
//                                    }
////                                    payble_ammount.setText(total_amount + getResources().getString(R.string.currency));
//                                    used_wallet_ammount.setText("(" + getResources().getString(R.string.currency) + Used_Wallet_amount + ")");
//                                    SharedPref.putString(getApplicationContext(), BaseURL.WALLET_TOTAL_AMOUNT, total_amount);
//                                    my_wallet_ammount.setText(Wallet_amount + getResources().getString(R.string.currency));
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    System.out.println("Error [" + error + "]");
//                }
//            }) {
//
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<String, String>();
//                    if (checkBox_Wallet.isChecked()) {
//                        params.put("wallet_amount", Wallet_Ammount);
//                    } else {
//                        params.put("total_amount", Ammount);
//
//                    }
//
//                    if (checkBox_coupon.isChecked()) {
//                        params.put("total_amount", Coupon_Ammount);
//                    } else {
//                        params.put("total_amount", Ammount);
//
//                    }
//                    return params;
//                }
//            };
//            rq.add(postReq);
//        } else {
//            Intent intent = new Intent(getApplicationContext(), NetworkError.class);
//            startActivity(intent);
//        }
//    }


    public void getRefresrh() {
        String user_id = sessionManagement.userId();
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.POST, BaseURL.WALLET_REFRESH + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);

                            String statusss = jObj.getString("status");
                            if (statusss.equalsIgnoreCase("1")) {
                                String wallet_amountss = jObj.getString("data");
                                wallet_amount = wallet_amountss;
                                my_wallet_ammount.setText(sessionManagement.getCurrency() + "" + wallet_amount);
                                SharedPref.putString(PaymentDetails.this, BaseURL.KEY_WALLET_Ammount, wallet_amount);
                            } else {
                                // Toast.makeText(DashboardPage.this, "" + jObj.optString("msg"), Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

        };
        strReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        rq.add(strReq);
    }

    public void startPayment(String name, String amount, String email, String phone) {

        final Activity activity = this;
        final Checkout co = new Checkout();

        try {

            JSONObject options = new JSONObject();

            options.put("name", name);
            options.put("description", "Shopping Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");

            options.put("amount", Double.parseDouble(amount) * 100);

            JSONObject preFill = new JSONObject();

            preFill.put("email", email);

            preFill.put("contact", phone);

            options.put("prefill", preFill);

            co.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(PaymentDetails.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onPaymentSuccess(String s) {
        makeAddOrderRequest(getuser_id, cart_id, payment_method, wallet_status, "success");
    }

    @Override
    public void onPaymentError(int i, String s) {
        progressDialog.dismiss();
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (data != null && data.getExtras() != null) {
                code = data.getExtras().getString("code");
                Log.i(TAG, code);
                if (code == null) {
                    code = "";
                    status = 1;
                    Promo_code_layout.setVisibility(View.GONE);
                    coupon_apply_t.setVisibility(View.VISIBLE);
                    checkBox_coupon.setChecked(false);
                    llpromocode.setBackgroundResource(R.drawable.border_rounded1);
                    tpromocode.setTextColor(getResources().getColor(R.color.black));
                } else if (code.equalsIgnoreCase("")) {
                    code = "";
                    status = 1;
                    Promo_code_layout.setVisibility(View.GONE);
                    coupon_apply_t.setVisibility(View.VISIBLE);
                    checkBox_coupon.setChecked(false);
                    llpromocode.setBackgroundResource(R.drawable.border_rounded1);
                    tpromocode.setTextColor(getResources().getColor(R.color.black));
                } else {
                    checkBox_coupon.setChecked(true);
                    llpromocode.setBackgroundResource(R.drawable.gradientbg);
                    tpromocode.setTextColor(getResources().getColor(R.color.white));
//                    tcod.setTextColor(getResources().getColor(R.color.black));
//                    tcards.setTextColor(getResources().getColor(R.color.black));
//                    twallet.setTextColor(getResources().getColor(R.color.black));
//                    my_wallet_ammount.setTextColor(getResources().getColor(R.color.black));
                    et_Coupon.setText(code);
                    Promo_code_layout.setVisibility(View.VISIBLE);
                    Promo_code_layout.setClickable(false);
                    apply();
                }
            }
        }
        else if (requestCode == 5) {
            if (data != null && data.getExtras() != null) {
                if (Objects.requireNonNull(data.getStringExtra("recharge")).equalsIgnoreCase("success")) {
                    getRefresrh();
                }
            }
        }
        else if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
//                        String paymentDetails = confirm.toJSONObject().toString(5);
                        JSONObject jsonObject = confirm.toJSONObject();
                        JSONObject responseJs = jsonObject.getJSONObject("response");
                        if (responseJs.getString("state").equalsIgnoreCase("approved")){
                            makeAddOrderRequest(getuser_id, cart_id, payment_method, wallet_status, "success");
                        }
                        Log.i("paymentExample", jsonObject.toString(5));

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                progressDialog.dismiss();
                Log.i("paymentExample", "The user canceled.");
            }
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                progressDialog.dismiss();
                Log.i("paymentExample", PaymentActivity.RESULT_EXTRAS_INVALID+"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }
}
