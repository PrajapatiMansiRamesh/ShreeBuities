package com.tecmanic.gogrocer.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.google.android.material.snackbar.Snackbar;
import com.tecmanic.gogrocer.Adapters.ImageAdapterData;
import com.tecmanic.gogrocer.Adapters.MyCalnderAdapter;
import com.tecmanic.gogrocer.Adapters.Timing_Adapter;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.ModelClass.CartModel;
import com.tecmanic.gogrocer.ModelClass.DeliveryModel;
import com.tecmanic.gogrocer.ModelClass.MyCalendarData;
import com.tecmanic.gogrocer.ModelClass.MyCalendarModel;
import com.tecmanic.gogrocer.ModelClass.timing_model;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.CalendarClick;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.ForClicktimings;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.model.CalendarItemStyle;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarPredicate;

import static com.tecmanic.gogrocer.Config.BaseURL.CalenderUrl;
import static com.tecmanic.gogrocer.Config.BaseURL.MinMaxOrder;
import static com.tecmanic.gogrocer.Config.BaseURL.OrderContinue;
import static com.tecmanic.gogrocer.Config.BaseURL.ShowAddress;

public class OrderSummary extends AppCompatActivity implements ForClicktimings {
    private static final String TAG = OrderSummary.class.getName();
    public static String cart_id;
    Session_management session_management;
    String total_atm;
    Button btn_AddAddress;
    LinearLayout back;
    TextView btn_Contine, txt_deliver, txtTotalItems, pPrice, pMrp, totalItms, price, DeliveryCharge, Amounttotal, txt_totalPrice;
    String dname;
    JSONArray array;
    RecyclerView recycler_itemsList, recyclerTimeSlot;
    RecyclerView calendarview2;
    HorizontalCalendar horizontalCalendar;
    Timing_Adapter bAdapter1;
    String timeslot;
    String addressid, totalprice;
    String user_id;
    ProgressDialog progressDialog;
    String minVAlue, maxValue;
    private ArrayList<timing_model> dateDayModelClasses1 = new ArrayList<>();
    private List<CartModel> cartList = new ArrayList<>();
    private DatabaseHandler db;
    private Session_management sessionManagement;
    private String todaydatee;
    private TextView textview_name_ofdata, textview_mobile_delivery, add_check_notice, currency_indicator, ruppy, currency_indicator_2;
    private String pable_amont;
    private String todaydate;
    private List<MyCalendarModel> calendarModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        sessionManagement = new Session_management(getApplicationContext());
        user_id = sessionManagement.userId();
        array = new JSONArray();
        prepareData();
        init();
    }

    private void prepareData() {
        for (int i = 0; i < 30; i++) {
            MyCalendarData m_calendar = new MyCalendarData(i);
            MyCalendarModel calendar = new MyCalendarModel(m_calendar.getWeekDay(), String.valueOf(m_calendar.getDay()), String.valueOf(m_calendar.getMonth()), String.valueOf(m_calendar.getYear()), 0,String.valueOf(m_calendar.getMonth()));
            m_calendar.getNextWeekDay(1);
            Log.i(TAG, calendar.toString());
            calendarModelList.add(calendar);

        }
    }

    private void init() {
        session_management = new Session_management(OrderSummary.this);
//        Log.d("hj",session_management.getUserDetails().get(BaseURL.KEY_ID));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        sessionManagement = new Session_management(this);
        sessionManagement.cleardatetime();
        db = new DatabaseHandler(this);

        dname = getIntent().getStringExtra("dName");
        addressid = getIntent().getStringExtra("dId");

        btn_AddAddress = findViewById(R.id.btn_AddAddress);

        txtTotalItems = findViewById(R.id.txtTotalItems);
        calendarview2 = findViewById(R.id.calendarview2);
        currency_indicator = findViewById(R.id.currency_indicator);
        currency_indicator_2 = findViewById(R.id.currency_indicator_2);
        ruppy = findViewById(R.id.rupyy);
        btn_Contine = findViewById(R.id.btn_Contine);
        add_check_notice = findViewById(R.id.add_check_notice);
        txt_deliver = findViewById(R.id.txt_deliver);
        textview_mobile_delivery = findViewById(R.id.textview_mobile_delivery);
        recycler_itemsList = findViewById(R.id.recycler_itemsList);
        textview_name_ofdata = findViewById(R.id.textview_name_ofdata);
        recyclerTimeSlot = findViewById(R.id.recyclerTimeSlot);
        pPrice = findViewById(R.id.pPrice);
        pMrp = findViewById(R.id.pMrp);
        totalItms = findViewById(R.id.totalItms);
        price = findViewById(R.id.price);
        DeliveryCharge = findViewById(R.id.DeliveryCharge);
        Amounttotal = findViewById(R.id.Amounttotal);
        txt_totalPrice = findViewById(R.id.txt_totalPrice);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        btn_AddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent In = new Intent(getApplicationContext(), SelectAddress.class);
                startActivityForResult(In, 2);
            }
        });
        showAdreesUrl();

        calendarview2.setLayoutManager(new LinearLayoutManager(OrderSummary.this, RecyclerView.HORIZONTAL, false));
        calendarview2.setHasFixedSize(true);
        calendarview2.setAdapter(new MyCalnderAdapter(OrderSummary.this, calendarModelList, new CalendarClick() {
            @Override
            public void onCalenderClick(int position) {
                todaydatee = calendarModelList.get(position).getYear()+"-"+calendarModelList.get(position).getMonthValue()+"-"+calendarModelList.get(position).getDate();
                Log.i(TAG,todaydatee);
                makeGetAddressRequests(todaydatee);
            }
        }));

        todaydatee = calendarModelList.get(0).getYear()+"-"+calendarModelList.get(0).getMonthValue()+"-"+calendarModelList.get(0).getDate();
        makeGetAddressRequests(todaydatee);


//        Calendar startDate = Calendar.getInstance();
//        todaydate = String.valueOf(DateFormat.format("dd-MM-yyyy", startDate));
//        startDate.add(Calendar.DATE, 1);
//        Calendar endDate = Calendar.getInstance();
//        endDate.add(Calendar.MONTH, 1);
//        final Calendar defaultSelectedDate = Calendar.getInstance();

        currency_indicator.setText(session_management.getCurrency());
        currency_indicator_2.setText(session_management.getCurrency());
        ruppy.setText(session_management.getCurrency());

//        horizontalCalendar = new HorizontalCalendar.Builder(OrderSummary.this, R.id.calendarView)
//                .range(startDate, endDate)
//                .datesNumberOnScreen(4)
//                .configure()
//                .formatTopText("MMM")
//                .formatMiddleText("dd")
//                .formatBottomText("EEE")
//                .textSize(11f, 20f, 12f)
//                .showTopText(true)
//                .showBottomText(true)
//                .textColor(Color.GRAY, Color.BLACK)
//                .end().defaultSelectedDate(Calendar.getInstance())
//                .build();
//
//
//        HorizontalCalendarPredicate horizontalCalendarPredicate = new HorizontalCalendarPredicate() {
//            @SuppressLint("WrongConstant")
//            @Override
//            public boolean test(Calendar date) {
//
//                date.add(Calendar.SATURDAY, 1);
//                return true;
//            }
//
//            @Override
//            public CalendarItemStyle style() {
//                return null;
//            }
//        };
//
//        horizontalCalendarPredicate.style();
//        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
//            @Override
//            public void onDateSelected(Calendar date, int position) {
//
//                dateDayModelClasses1.clear();
//                todaydatee = String.valueOf(DateFormat.format("yyyy-MM-dd", date));
//                makeGetAddressRequests(todaydatee);
//            }
//
//        });

        //   makeGetAddressRequest(todaydate);

        ArrayList<HashMap<String, String>> map = db.getCartAll();

        try {
            JSONArray object = new JSONArray(map);
            for (int i = 0; i < object.length(); i++) {
                Log.d("sadf", object.toString());
                JSONObject object1 = object.getJSONObject(i);

                JSONObject product_array = new JSONObject();

                product_array.put("qty", object1.getString("qty"));
                product_array.put("varient_id", object1.getString("varient_id"));
                product_array.put("product_image", object1.getString("product_image"));

                Log.d("sdf", product_array.toString());
                array.put(product_array);


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("sdfa", array.toString());
        ImageAdapterData adapters = new ImageAdapterData(OrderSummary.this, map);

        recycler_itemsList.setLayoutManager(new LinearLayoutManager(OrderSummary.this, LinearLayoutManager.HORIZONTAL, false));
        recycler_itemsList.setAdapter(adapters);
//        adapter.notifyDataSetChanged();
        updateData();
        deliverychrge();
        Toast.makeText(getApplicationContext(), "Please select a time slot on available date!", Toast.LENGTH_LONG).show();
        minimaxAmount();
        btn_Contine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                timeslot = bAdapter1.gettimeslot();
                progressDialog.show();
                if (Integer.parseInt(txt_totalPrice.getText().toString()) <= Integer.parseInt(minVAlue)) {
                    Snackbar.make(v, "Please order more than" + " " + minVAlue, Snackbar.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else if (Integer.parseInt(txt_totalPrice.getText().toString()) >= Integer.parseInt(maxValue)) {
                    Snackbar.make(v, "Please order less than" + " " + maxValue, Snackbar.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else {
                    continueUrl(todaydatee, timeslot, map);
                }
                /*if(txt_totalPrice.getText().toString()!=minVAlue){
                    Toast.makeText(getApplicationContext(),"Cart amount must be more than "+minVAlue,Toast.LENGTH_SHORT).show();
                }else {
                    timeslot = bAdapter1.gettimeslot();
                    continueUrl(todaydate, timeslot, map);
                }*/
            }
        });

    }

    private void showAdreesUrl() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowAddress, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("adresssHoww", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String msg = jsonObject.getString("message");
                    if (status.equals("1")) {
                        add_check_notice.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String address_id = jsonObject1.getString("address_id");
                            String user_id = jsonObject1.getString("user_id");
                            String receiver_name = jsonObject1.getString("receiver_name");
                            String receiver_phone = jsonObject1.getString("receiver_phone");
                            String cityyyy = jsonObject1.getString("city");
                            String society = jsonObject1.getString("society");
                            String house_no = jsonObject1.getString("house_no");
                            String pincode = jsonObject1.getString("pincode");
                            String stateeee = jsonObject1.getString("state");
                            String landmark2 = jsonObject1.getString("landmark");
                            int select_status = jsonObject1.getInt("select_status");
                            DeliveryModel ss = new DeliveryModel(address_id, receiver_name, receiver_phone, house_no + ", " + society
                                    + "," + cityyyy + ", " + stateeee + ", " + pincode);
                            if (select_status == 1) {
                                txt_deliver.setText(house_no + ", " + society + "," + cityyyy + ", " + stateeee + ", " + pincode);
                                textview_name_ofdata.setText(receiver_name);
                                textview_mobile_delivery.setText(receiver_phone);
                            }
                            ss.setCity_name(cityyyy);
                            ss.setHouse_no(house_no);
                            ss.setLandmark(landmark2);
                            ss.setPincode(pincode);
                            ss.setState(stateeee);
                            ss.setReceiver_phone(receiver_phone);
                            ss.setReceiver_name(receiver_name);
                            ss.setId(address_id);
                            ss.setSelect_status(select_status);

                            ss.setSociety(society);
//                            dlist.add(ss);
                        }
//                        deliveryAdapter = new SelectAddress.DeliveryAdapter(dlist);
//                        recycleraddressList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                        recycleraddressList.setAdapter(deliveryAdapter);
//                        deliveryAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        /*Save.setVisibility(View.VISIBLE);
                        EditBtn.setVisibility(View.GONE);
                        saveAddress(cityId,socetyId,landmaarkkkk);*/
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("user_id", user_id);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(OrderSummary.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void minimaxAmount() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, MinMaxOrder, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("MinMaxOrder value", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String msg = jsonObject.getString("message");
                    if (status.equals("1")) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        String id = jsonObject1.getString("min_max_id");
                        String min = jsonObject1.getString("min_value");
                        String max = jsonObject1.getString("max_value");

                        minVAlue = min;
                        maxValue = max;

                        Log.d("minmax", minVAlue + " " + maxValue);
                    }
//                    else {
//                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
//                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(OrderSummary.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    private void continueUrl(final String todaydate, final String timeslot, final ArrayList<HashMap<String, String>> map) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, OrderContinue, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ordermake", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String msg = jsonObject.getString("message");
                    if (status.equals("1")) {

                        JSONObject object = jsonObject.getJSONObject("data");

                        cart_id = object.getString("cart_id");
                        sessionManagement.setCartID(cart_id);
                        Intent intent = new Intent(getApplicationContext(), PaymentDetails.class);

                        intent.putExtra("order_amt", txt_totalPrice.getText().toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e(TAG, "onErrorResponse: " + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("time_slot", timeslot);
                param.put("user_id", user_id);
                param.put("delivery_date", todaydatee);
                param.put("order_array", array.toString());
//                Log.e(TAG, "getParams: "+param.toString() );
//                Log.d("hj",session_management.userId());
//                Log.d("timeslot",timeslot);
//                Log.d("todaydate",todaydate);
//                Log.d("order_array",array.toString());
                return param;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(OrderSummary.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    private void updateData() {
        pPrice.setText("" + db.getTotalAmount());
        price.setText("" + db.getTotalAmount());
        total_atm = db.getTotalAmount();

        txtTotalItems.setText("" + db.getCartCount());
        totalItms.setText("" + db.getCartCount() + " " + " Items");

    }

    private void makeGetAddressRequests(String date) {
        dateDayModelClasses1.clear();
        // Tag used to cancel the request
        String tag_json_obj = "json_get_address_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("selected_date", date);
        Log.d("dd", date);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                CalenderUrl, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("fdfdfddf", response.toString());

                try {
                    String status = response.getString("status");
                    String msg = response.getString("message");
                    if (status.contains("1")) {
                        dateDayModelClasses1.clear();
                        JSONArray jsonArray = response.getJSONArray("data");

                        if (jsonArray.length() > 0) {


                            recyclerTimeSlot.setVisibility(View.VISIBLE);
                            for (int i = 0; i < jsonArray.length(); i++) {

                                String data = jsonArray.getString(i);
                                timing_model mycreditList = new timing_model();
                                mycreditList.setTiming(data);
                                dateDayModelClasses1.add(mycreditList);

                            }

                            timeslot = jsonArray.getString(0);
                            bAdapter1 = new Timing_Adapter(OrderSummary.this, dateDayModelClasses1, OrderSummary.this);
                            recyclerTimeSlot.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerTimeSlot.setAdapter(bAdapter1);
                            bAdapter1.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("selected_date", date);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        requestQueue.add(jsonObjReq);

    }

    private void deliverychrge() {


        String tag_json_obj = "json_category_req";


        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.GET,
                BaseURL.delivery_info, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", response.toString());

                try {

                    String status = response.getString("status");
                    String message = response.getString("message");

                    if (status.contains("1")) {


                        JSONObject jsonObject = response.getJSONObject("data");

                        String min_cart_value = jsonObject.getString("min_cart_value");

                        String del_charge = jsonObject.getString("del_charge");

                        if (Integer.parseInt(pPrice.getText().toString()) >= Integer.parseInt(min_cart_value)) {
//                            Toast.makeText(OrderSummary.this, "Minimum Order Amount Should be Greater then ₹500", Toast.LENGTH_SHORT).show();

                            DeliveryCharge.setText("0");
                            txt_totalPrice.setText(String.valueOf(total_atm));
                        } else {
//                            Toast.makeText(OrderSummary.this, "sdjfwhuf", Toast.LENGTH_SHORT).show();

                            DeliveryCharge.setText(del_charge);
                            txt_totalPrice.setText(String.valueOf(Integer.parseInt(String.valueOf(total_atm)) + Integer.parseInt(del_charge)));

                        }
                    } else {

//                        Toast.makeText(OrderSummary.this, ""+message, Toast.LENGTH_SHORT).show();

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

    @Override
    public void getTimeSlot(String timeslot) {
        this.timeslot = timeslot;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {

        }

    }
}