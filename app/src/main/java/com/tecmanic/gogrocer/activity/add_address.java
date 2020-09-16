package com.tecmanic.gogrocer.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.tecmanic.gogrocer.Adapters.CustomAdapter;
import com.tecmanic.gogrocer.Adapters.SearchAdapter;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.ModelClass.SearchModel;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.tecmanic.gogrocer.Config.BaseURL.Add_address;
import static com.tecmanic.gogrocer.Config.BaseURL.CityListUrl;
import static com.tecmanic.gogrocer.Config.BaseURL.SocietyListUrl;

public class add_address extends AppCompatActivity {
    private static final String TAG = add_address.class.getName();
    Session_management session_management;
    LinearLayout back;
    Button Save, EditBtn;
    EditText pinCode, houseNo, Area, city, state, landmaark, name, mobNo, alterMob;
    TextInputLayout tpinCode, thouseNo, tArea, tcity, tstate, tlandmaark, tname, tmobNo, talterMob;

    RadioGroup radioGroup;
    RadioButton rHome, rWork;
    CardView currentLoc;
    String user_id;
    String city_id;
    RecyclerView recyclerViewCity, recyclerViewSociety;
    private AppCompatSpinner register_vehicletype_brand, area_spinner;
    String cityId="", cityName="", socetyId, SocetyName="", landmaarkkkk, updtae, addressId;
    ProgressDialog progressDialog;
    SearchAdapter cityAdapter, societyAdapter;
    List<SearchModel> citylist = new ArrayList<>();
    List<SearchModel> societylist = new ArrayList<>();
    ArrayList<String> vehicletypemessagebrandList = new ArrayList<>();
    TextView city_txt;
    private boolean inSelectVal = false;
    private String statused = "status";
    private String cityNameRef = "city_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address2);
        init();
    }


    private void init() {
        session_management = new Session_management(add_address.this);
        Area = (EditText) findViewById(R.id.input_area);
        back = findViewById(R.id.back);
        Save = findViewById(R.id.SaveBtn);
        EditBtn = findViewById(R.id.EditBtn);
        progressDialog = new ProgressDialog(add_address.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        if (updtae != "") {
            updtae = getIntent().getStringExtra("update");
            addressId = getIntent().getStringExtra("addId");
            //     Log.d("fgh",addressId);

        }
        back.setOnClickListener(v -> {
            inSelectVal = false;
            onBackPressed();
        });
//        Session_management sessionManagement = new Session_management(getApplicationContext());
        user_id = session_management.getUserDetails().get(BaseURL.KEY_ID);
        currentLoc = findViewById(R.id.currentLoc);
        recyclerViewCity = findViewById(R.id.recyclerCity);
        city_txt = findViewById(R.id.city_txt);
        recyclerViewSociety = findViewById(R.id.recyclerSociety);
        register_vehicletype_brand = findViewById(R.id.register_vehicletype_brand);
        area_spinner = findViewById(R.id.area_spinner);
        tpinCode = findViewById(R.id.input_layout_pinCode);
        thouseNo = findViewById(R.id.input_layout_HOuseNo);
//        tArea = findViewById(R.id.input_layout_area);
//        tcity = findViewById(R.id.input_layout_CIty);
        tstate = findViewById(R.id.input_layout_state);
        tlandmaark = findViewById(R.id.input_layout_landmark);
        tname = findViewById(R.id.input_layout_NAme);
        tmobNo = findViewById(R.id.input_layout_mobNo);
        talterMob = findViewById(R.id.input_layout_AltermobileNO);
        pinCode = findViewById(R.id.input_pinCode);
        houseNo = (EditText) findViewById(R.id.input_HouseNO);
//        area =  findViewById(R.id.input_area);
//        city = (EditText) findViewById(R.id.input_city);
        state = (EditText) findViewById(R.id.input_state);
        landmaark = (EditText) findViewById(R.id.input_landmark_2);
        name = (EditText) findViewById(R.id.input_NAme);
        mobNo = (EditText) findViewById(R.id.input_mobNO);
        alterMob = (EditText) findViewById(R.id.input_AltermobileNO);
        getAddress();
        Save.setOnClickListener(v -> {

            if (pinCode.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Enter Pincode", Toast.LENGTH_SHORT).show();
            } else if (houseNo.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Enter House No., Building Name", Toast.LENGTH_SHORT).show();
            }

//            else if (area.getText().toString().trim().equals("")) {
//                Toast.makeText(getApplicationContext(), "Enter Area, Colony", Toast.LENGTH_SHORT).show();
//            }
            else if (city_txt.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Enter City", Toast.LENGTH_SHORT).show();
            }
            else if (state.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Enter State", Toast.LENGTH_SHORT).show();
            }
            else if (name.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Enter your Name", Toast.LENGTH_SHORT).show();
            } else if (mobNo.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Enter Mobile No.", Toast.LENGTH_SHORT).show();
            }
//                else if (!isOnline()) {
//                    Toast.makeText(getApplicationContext(), "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
//
//                }
            else {
//                if (landmaarkkkk.equals("")) {
//                    landmaarkkkk = "NA";
//                } else {
//
//
//                }

                landmaarkkkk = landmaark.getText().toString();
                if (landmaarkkkk!=null && !landmaarkkkk.equalsIgnoreCase("")){
                    saveAddress(cityName, SocetyName, landmaarkkkk);
                }else {
                    saveAddress(cityName, SocetyName, "NA");
                }


            }

        });

    }

    private void saveAddress(String cityName, String soctyName, String landmaarkkkkk) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Add_address, response -> {
            Log.d("addadrss", response);

            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString(statused);
                String msg = jsonObject.getString("message");
                if (status.equalsIgnoreCase("1")) {
                    Toast.makeText(getApplicationContext(), msg + "", Toast.LENGTH_SHORT).show();
                    inSelectVal = true;
                    onBackPressed();
                } else {
                    Toast.makeText(getApplicationContext(), msg + "", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }finally {
                progressDialog.dismiss();
            }
        }, error -> {
            progressDialog.dismiss();
            Log.e(TAG, "onErrorResponse: ");
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("user_id", session_management.getUserDetails().get(BaseURL.KEY_ID));
                param.put("receiver_name", name.getText().toString());
                param.put("receiver_phone", mobNo.getText().toString());
                param.put(cityNameRef, cityName);
                param.put("society_name", (soctyName!=null && !soctyName.equalsIgnoreCase(""))?soctyName:cityName);
                param.put("house_no", houseNo.getText().toString());
                param.put("landmark", landmaarkkkkk);
                param.put("state", state.getText().toString());
                param.put("pin", pinCode.getText().toString());
                param.put("lat", session_management.getLatPref());
                param.put("lng", session_management.getLangPref());

                return param;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
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
                error.printStackTrace();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(add_address.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    private void societyURl(String cityId) {
        vehicletypemessagebrandList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SocietyListUrl, response -> {
            Log.d(TAG, response);

            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String msg = jsonObject.getString("message");
                if (status.equals("1")) {
                    societylist.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String society_id = jsonObject1.getString("society_id");
                        String society_name = jsonObject1.getString("society_name");
                        recyclerViewSociety.setVisibility(View.VISIBLE);
                        SearchModel ss = new SearchModel(society_id, society_name);
                        societylist.add(ss);
                        vehicletypemessagebrandList.add(society_name);
                    }

                    SocetyName = vehicletypemessagebrandList.get(0);

                    runOnUiThread(() -> {
                        ArrayAdapter adapter = new ArrayAdapter(add_address.this, android.R.layout.simple_spinner_item, vehicletypemessagebrandList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        area_spinner.setAdapter(adapter);
                        area_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                SocetyName=area_spinner.getSelectedItem().toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                Log.i(TAG,"d");
                            }
                        });
                    });


                } else {
                    SocetyName = "";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }, Throwable::printStackTrace) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put(cityNameRef, cityId);
                Log.d(TAG, cityId);
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(add_address.this);
        requestQueue.getCache().clear();
        stringRequest.setRetryPolicy(new RetryPolicy() {
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
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);

    }

    private void getAddress() {
        progressDialog.show();
        new Thread(() -> {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(add_address.this, Locale.getDefault());
            DecimalFormat dFormat = new DecimalFormat("#.######");
            double lat = Double.parseDouble(session_management.getLatPref());
            double lang = Double.parseDouble(session_management.getLangPref());
            double latitude = Double.parseDouble(dFormat.format(lat));
            double longitude = Double.parseDouble(dFormat.format(lang));

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                String city = addresses.get(0).getLocality();
//
                String states = addresses.get(0).getAdminArea();
                String socity = addresses.get(0).getSubLocality();
              //  societyURl(city);
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//
//            String address = addresses.get(0).getAddressLine(0);
//            String knownName = addresses.get(0).getSubAdminArea();

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    cityName = city;
                    city_txt.setText(city);
                    if (states!=null && !states.equalsIgnoreCase("")){
                        state.setText(states);
                    }else {
                        state.setText(city);
                    }
                    if (socity!=null && !socity.equalsIgnoreCase("")){
                        Area.setText(socity);
                    }else {
                        Area.setText(city);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                runOnUiThread(() -> progressDialog.dismiss());
            }
        }).start();

    }

    private void cityUrl() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, CityListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("cityyyyyyyy", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString(statused);
                    String msg = jsonObject.getString("message");
                    if (status.equals("1")) {
                        citylist.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            city_id = jsonObject1.getString("city_id");
                            String city_name = jsonObject1.getString(cityNameRef);

                            recyclerViewCity.setVisibility(View.VISIBLE);
                            SearchModel cs = new SearchModel(city_id, city_name);

                            citylist.add(cs);
                        }

//                        vehicletypemessagebrandList.add(0,"-- Please Select Vehicle Brand --");
//                        for (int i=0;i<citylist.size();i++)
//                        {
//
//                            vehicletypemessagebrandList.add(citylist.get(i).getpNAme());
//
//                        }
//                        cityAdapter = new SearchAdapter(citylist);
//                        recyclerViewCity.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                        recyclerViewCity.setAdapter(cityAdapter);
//                        cityAdapter.notifyDataSetChanged();
//                        ArrayAdapter adapter = new ArrayAdapter(add_address.this, android.R.layout.simple_spinner_item, vehicletypemessagebrandList);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        register_vehicletype_brand.setAdapter(adapter);
//                        citylist.add(0,"-- Please Select Vehicle Brand --")
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), citylist);
                        register_vehicletype_brand.setAdapter(customAdapter);
                        register_vehicletype_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                if (register_vehicletype_brand.getSelectedItem().)
//                {
////                    selectedbrand="";
//                }
//                else
//                {


                                cityName=citylist.get(i).getpNAme();
////                    ongetvehiclemodal(selectedbrand);
//                }
//                cityId= register_vehicletype_brand.get(po)
//                id_click = spinner.getSelectedItemPosition();
                                String cityId = citylist.get(i).getId();
                              //  societyURl(cityId);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                Log.d(TAG, "d");
                            }
                        });


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
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(add_address.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("select_address",inSelectVal);
        setResult(21,intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }else {
            finish();
        }
    }
}
