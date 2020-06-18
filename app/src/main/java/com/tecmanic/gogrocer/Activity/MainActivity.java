package com.tecmanic.gogrocer.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.Fragments.CartFragment;
import com.tecmanic.gogrocer.Fragments.CategoryFragment;
import com.tecmanic.gogrocer.Fragments.Contact_Us_fragment;
import com.tecmanic.gogrocer.Fragments.Edit_profile_fragment;
import com.tecmanic.gogrocer.Fragments.HomeeeFragment;
import com.tecmanic.gogrocer.Fragments.NotificationFragment;
import com.tecmanic.gogrocer.Fragments.Reward_fragment;
import com.tecmanic.gogrocer.Fragments.SearchFragment;
import com.tecmanic.gogrocer.Fragments.Terms_and_Condition_fragment;
import com.tecmanic.gogrocer.Fragments.Wallet_fragment;
import com.tecmanic.gogrocer.ModelClass.ForgotEmailModel;
import com.tecmanic.gogrocer.ModelClass.NewPastOrderSubModel;
import com.tecmanic.gogrocer.ModelClass.NewPendingDataModel;
import com.tecmanic.gogrocer.ModelClass.NotifyModelUser;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.network.ApiInterface;
import com.tecmanic.gogrocer.util.ConnectivityReceiver;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.FetchAddressTask;
import com.tecmanic.gogrocer.util.GPSTracker;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static com.tecmanic.gogrocer.Config.BaseURL.ADDRESS;
import static com.tecmanic.gogrocer.Config.BaseURL.CITY;
import static com.tecmanic.gogrocer.Config.BaseURL.COUNTRY;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_PINCODE;
import static com.tecmanic.gogrocer.Config.BaseURL.LAT;
import static com.tecmanic.gogrocer.Config.BaseURL.LONG;
import static com.tecmanic.gogrocer.Config.BaseURL.MyPrefreance;
import static com.tecmanic.gogrocer.Config.BaseURL.STATE;
import static com.tecmanic.gogrocer.Config.BaseURL.SupportUrl;
import static com.tecmanic.gogrocer.Config.BaseURL.TermsUrl;
import static com.tecmanic.gogrocer.Config.BaseURL.USERBLOCKAPI;
import static com.tecmanic.gogrocer.Config.BaseURL.currencyApi;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, FetchAddressTask.OnTaskCompleted, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    public static BottomNavigationView navigation;
    int padding = 0;
    LinearLayout My_Order, My_Reward, My_Walllet, My_Cart;
    RelativeLayout loginSignUp, aboutUs, TermsPolicy, ContactUs, share, logout;
    NavigationView navigationView;
    LinearLayout viewpa;
    TextView mTitle, username;
    Button login, signup;
    TextView totalBudgetCount;
    Toolbar toolbar;
    ImageView sliderr;
    ImageView bell;
    List<Address> addresses = new ArrayList<>();
    String latitude, longitude, address, city, state, country, postalCode;
    LocationManager locationManager;
    SharedPreferences sharedPreferences;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.e(TAG, "onLocationResult: called");
        }
    };
    private DatabaseHandler dbcart;
    private Session_management sessionManagement;
    private AppBarConfiguration mAppBarConfiguration;
    private ImageView profile;
    private Menu nav_menu;
    private ImageView iv_profile;
    private FusedLocationProviderClient mFusedLocationClient;
    private SharedPreferences pref;
    private BottomNavigationMenuView mbottomNavigationMenuView;
    private DrawerLayout drawer;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.BLACK);
        }


        sessionManagement = new Session_management(MainActivity.this);
        dbcart = new DatabaseHandler(this);
        pref = getSharedPreferences("GOGrocer", Context.MODE_PRIVATE);
        pref.registerOnSharedPreferenceChangeListener(this);
        navigation = findViewById(R.id.nav_view12);
        bell = findViewById(R.id.bell);
        profile = findViewById(R.id.profile);

//        mbottomNavigationMenuView = (BottomNavigationMenuView) navigation.getChildAt(0);
//        View view = mbottomNavigationMenuView.getChildAt(4);
//
//        BottomNavigationItemView itemView = (BottomNavigationItemView) view;
//
//        View cart_badge = LayoutInflater.from(this)
//                .inflate(R.layout.bottom_badge_cart, mbottomNavigationMenuView, false);
//        itemView.addView(cart_badge);
//        totalBudgetCount = (TextView) cart_badge.findViewById(R.id.totalBudgetCount);

        int badgeCount = pref.getInt("cardqnty", 0);
        if (badgeCount > 0) {
            navigation.getOrCreateBadge(R.id.navigation_notifications123).setNumber(badgeCount);
//            totalBudgetCount.setVisibility(View.VISIBLE);
//            totalBudgetCount.setText(""+badgeCount);
        } else {
            navigation.removeBadge(R.id.navigation_notifications123);
//            totalBudgetCount.setVisibility(View.GONE);
        }

        profile.setOnClickListener(v -> {
            if (sessionManagement.isLoggedIn()) {
                Edit_profile_fragment fm = new Edit_profile_fragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            } else {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

//        bell.setOnClickListener(v -> {
//            navigation.setSelectedItemId(R.id.navigation_notifications123);
//            loadFragment(new CartFragment());
//        });

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(R.string.app_name);

        /*toolbar.setPadding(padding, toolbar.getPaddingTop(), padding, toolbar.getPaddingBottom());

        setSupportActionBar(toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {

            View view = toolbar.getChildAt(i);


            if (view instanceof TextView) {
                TextView textView = (TextView) view;

            }
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);*/
        drawer = findViewById(R.id.drawer_layout);
        ImageView menuSlider = findViewById(R.id.sliderr);

        menuSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
//                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
//            applyFontToMenuItem(mi);
        }

        View headerView = navigationView.getHeaderView(0);
        navigationView.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);
        navigationView.setNavigationItemSelectedListener(this);
        nav_menu = navigationView.getMenu();
        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        viewpa = header.findViewById(R.id.viewpa);
        if (sessionManagement.isLoggedIn()) {
            viewpa.setVisibility(View.VISIBLE);
        }


        My_Order = header.findViewById(R.id.my_orders);
        My_Reward = header.findViewById(R.id.my_reward);
        My_Walllet = header.findViewById(R.id.my_wallet);
        My_Cart = header.findViewById(R.id.my_cart);
        iv_profile = header.findViewById(R.id.iv_header_img);
        username = header.findViewById(R.id.tv_header_name);


       /* login = (Button) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.sign);
        username=findViewById(R.id.tv_header_name);*/

        My_Order.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
            if (sessionManagement.isLoggedIn()) {
                Intent intent = new Intent(MainActivity.this, My_Order_activity.class);
                startActivityForResult(intent, 4);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        My_Reward.setOnClickListener(v -> {
            if (sessionManagement.isLoggedIn()) {

                drawer.closeDrawer(GravityCompat.START);

                Reward_fragment fm = new Reward_fragment();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.contentPanel, fm);
                transaction.commit();

//                    android.app.FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                            .addToBackStack(null).commit();
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        });
        My_Walllet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManagement.isLoggedIn()) {
                    drawer.closeDrawer(GravityCompat.START);
                    if (sessionManagement.userBlockStatus().equalsIgnoreCase("2")){
                        Wallet_fragment fm = new Wallet_fragment();
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.contentPanel, fm);
                        transaction.commit();
                    }else {
                        showBloackDialog();
                    }






//                    Wallet_fragment fm = new Wallet_fragment();
//                    android.app.FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                            .addToBackStack(null).commit();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        My_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dbcart.getCartCount() > 0) {
                    CartFragment favourite_fragment = new CartFragment();
                    FragmentManager manager1 = getSupportFragmentManager();
                    FragmentTransaction transaction1 = manager1.beginTransaction();
                    transaction1.replace(R.id.contentPanel, favourite_fragment);
                    transaction1.addToBackStack(null);
                    transaction1.commit();
                } else {
                    Toast.makeText(MainActivity.this, "No Item in Cart", Toast.LENGTH_SHORT).show();
                }
            }
        });
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManagement.isLoggedIn()) {
                    Edit_profile_fragment fm = new Edit_profile_fragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                } else {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }
        });

        sideMenu();

        new Thread(this::getCurrency).start();
        new Thread(this::checkOtpStatus).start();
        new Thread(this::checkUserNotify).start();

        // checkConnection();

        if (savedInstanceState == null) {
            HomeeeFragment fm = new HomeeeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentPanel, fm, "Home_fragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    Fragment fr = getSupportFragmentManager().findFragmentById(R.id.contentPanel);

                    final String fm_name = fr.getClass().getSimpleName();
                    Log.e("backstack: ", ": " + fm_name);
                    if (fm_name.contentEquals("Home_fragment")) {
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        toggle.setDrawerIndicatorEnabled(true);
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        }
                        toggle.syncState();

                    } else if (fm_name.contentEquals("My_order_fragment") ||
                            fm_name.contentEquals("Thanks_fragment")) {
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                        toggle.setDrawerIndicatorEnabled(false);
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        }
                        toggle.syncState();

                        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HomeeeFragment fm = new HomeeeFragment();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                        .addToBackStack(null).commit();
                            }
                        });
                    } else {

                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                        toggle.setDrawerIndicatorEnabled(false);
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        }
                        toggle.syncState();

                        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                onBackPressed();
                            }
                        });
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        GPSTracker mGPS = new GPSTracker(getApplicationContext());
        if (mGPS.canGetLocation) {
            mGPS.getLocation();

            latitude = String.valueOf(mGPS.getLatitude());
            longitude = String.valueOf(mGPS.getLongitude());

            Log.d("lat", latitude);
            Log.d("long", longitude);

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(mGPS.getLatitude(), mGPS.getLongitude(), 1); //1 num of possible location returned
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (address != null) {
                address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();                // commented on 27 sept 19 bcoz of screen crash
                // create your custom title
                String title = address + "-" + city + "-" + state;
                Log.d("addresss", title + country + "-" + postalCode);

                SharedPreferences.Editor editor = getSharedPreferences(MyPrefreance, MODE_PRIVATE).edit();
                editor.putString(ADDRESS, address);
                editor.putString(CITY, city);
                editor.putString(STATE, state);
                editor.putString(KEY_PINCODE, postalCode);
                editor.putString(COUNTRY, country);
                editor.putString(LAT, latitude);
                editor.putString(LONG, longitude);
                editor.apply();
                editor.commit();
            }
        }

        initComponent();
        loadFragment(new HomeeeFragment());
//        updateHeader();
//sideMenu();


        getLocation();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

// start
//        mFusedLocationClient.requestLocationUpdates
//                (getLocationRequest(), mLocationCallback,
//                        null /* Looper */);


        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
//                Log.e(TAG, "location create"+location.getLatitude()+" , "+location.getLongitude() );
                if (location != null) {
                    new FetchAddressTask(MainActivity.this, MainActivity.this).execute(location);
                }
            }
        });


    }

    private void showBloackDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setCancelable(true);
        alertDialog.setMessage("You are blocked from backend.\n Please Contact with customer care!");
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

    @Override
    protected void onStart() {
        fetchBlockStatus();
        super.onStart();
    }

    private void fetchBlockStatus() {

        if (!sessionManagement.userId().equalsIgnoreCase("")) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, USERBLOCKAPI, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("adresssHoww", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String msg = jsonObject.getString("message");
                        if (status.equals("2")) {
//                            JSONObject jsonArray = jsonObject.getJSONObject("data");
//                            String userStatusValue = jsonArray.getString("block");
                            sessionManagement.setUserBlockStatus("2");
                        } else {
//                            JSONObject jsonArray = jsonObject.getJSONObject("data");
//                            String userStatusValue = jsonArray.getString("block");
                            sessionManagement.setUserBlockStatus("1");
//                            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                        }
//                        Toast.makeText(MainActivity.this,""+msg,Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("user_id", sessionManagement.userId());
                    return param;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {// If the permission is granted, get the location,
            // otherwise, show a Toast
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getLocation();
                Log.e(TAG, "Granted");
                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
//                                Log.e(TAG, "location create" + location.getLatitude() + " , " + location.getLongitude());
                            new FetchAddressTask(MainActivity.this, MainActivity.this)
                                    .execute(location);
                        }
                    }
                });


            } else {
//                    Log.e(TAG, "permission denied" );

                Toast.makeText(MainActivity.this, "Location permission is necessary", Toast.LENGTH_SHORT).show();
                finish();

            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "getLocation: permissions granted");
        }
    }

    private void checkUserNotify() {
        Retrofit emailOtp = new Retrofit.Builder()
                .baseUrl(BaseURL.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        ApiInterface apiInterface = emailOtp.create(ApiInterface.class);

        Call<NotifyModelUser> checkOtpStatus = apiInterface.getNotifyUser(sessionManagement.userId());

        checkOtpStatus.enqueue(new Callback<NotifyModelUser>() {
            @Override
            public void onResponse(@NonNull Call<NotifyModelUser> call, @NonNull retrofit2.Response<NotifyModelUser> response) {

                if (response.isSuccessful()){
                    if (response.body()!=null){
                        NotifyModelUser modelUser = response.body();
                        if (modelUser.getStatus().equalsIgnoreCase("1")){
                            sessionManagement.setEmailServer(modelUser.getData().getEmail());
                            sessionManagement.setUserSMSService(modelUser.getData().getSms());
                            sessionManagement.setUserInAppService(modelUser.getData().getApp());
                        }else {
                            sessionManagement.setEmailServer("0");
                            sessionManagement.setUserSMSService("0");
                            sessionManagement.setUserInAppService("0");
                        }
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<NotifyModelUser> call, @NonNull Throwable t) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop
//        mFusedLocationClient.removeLocationUpdates(mLocationCallback);

    }

    @Override
    public boolean onSupportNavigateUp() {
       /* NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();*/
        return false;
    }

    private void loadFragment(Fragment fragment) {
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentPanel, fragment)
                .commitAllowingStateLoss();
    }

    private void initComponent() {
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(new HomeeeFragment());

//                        HomeeeFragment appNewsHome1Fragment = new HomeeeFragment();
//                        FragmentManager manager = getSupportFragmentManager();
//                        FragmentTransaction transaction = manager.beginTransaction();
//                        transaction.replace(R.id.contentPanel, appNewsHome1Fragment);
//                        transaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    loadFragment(new CategoryFragment());
//                        CategoryFragment category_fragment = new CategoryFragment();
//                        FragmentManager manager2 = getSupportFragmentManager();
//                        FragmentTransaction transaction2 = manager2.beginTransaction();
//                        transaction2.replace(R.id.contentPanel, category_fragment);
//                        transaction2.commit();
                    return true;

                case R.id.navigation_notifications1:
                    loadFragment(new SearchFragment());
//                        SearchFragment trending_fragment = new SearchFragment();
//                        FragmentManager m = getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = m.beginTransaction();
//                        fragmentTransaction.replace(R.id.contentPanel, trending_fragment);
//                        fragmentTransaction.commit();
                    return true;


                case R.id.navigation_notifications12:
                    loadFragment(new NotificationFragment());
//                        NotificationFragment nf = new NotificationFragment();
//                        FragmentManager m4 = getSupportFragmentManager();
//                        FragmentTransaction fragmentTransactionnn = m4.beginTransaction();
//                        fragmentTransactionnn.replace(R.id.contentPanel, nf);
//                        fragmentTransactionnn.commit();
                    return true;
//                    case R.id.navigation_newsstand:
////                        mTextMessage.setText(item.getTitle());
//                        navigation.setBackgroundColor(getResources().getColor(R.color.teal_800));
//                        window.setStatusBarColor(getResources().getColor(R.color.teal_800));
//                        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal_800));
//
//                        EditorPicked_Fragment editorPicked_fragment = new EditorPicked_Fragment();
//                        FragmentManager manager3 = getSupportFragmentManager();
//                        FragmentTransaction transaction3 = manager3.beginTransaction();
//                        transaction3.replace(R.id.contentPanel, editorPicked_fragment);
//                        transaction3.commit();
//                        return true;
                case R.id.navigation_notifications123:
                    loadFragment(new CartFragment());
//                        CartFragment favourite_fragment = new CartFragment();
//                        FragmentManager manager1 = getSupportFragmentManager();
//                        FragmentTransaction transaction1 = manager1.beginTransaction();
//                        transaction1.replace(R.id.contentPanel, favourite_fragment);
//                        transaction1.commit();
                    return true;
            }
            return false;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fm = null;
        Bundle args = new Bundle();
        if (id == R.id.sign) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        } else if (id == R.id.sign) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }

       /* else if (id == R.id.nav_shop_now) {
            fm = new Shop_Now_fragment();
        } */
        else if (id == R.id.nav_my_profile) {
            fm = new Edit_profile_fragment();
//        } else if (id == R.id.nav_support) {
//            String smsNumber = "9889887711";
//            Intent sendIntent = new Intent("android.intent.action.MAIN");
//            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
//            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");//phone number without "+" prefix
//            startActivity(sendIntent);

        } else if (id == R.id.nav_aboutus) {
//            toolbar.setTitle("About");
            startActivity(new Intent(getApplicationContext(), About_us.class));
        } else if (id == R.id.nav_policy) {
            fm = new Terms_and_Condition_fragment();
            args.putString("url", TermsUrl);
            args.putString("title", getResources().getString(R.string.nav_terms));
            fm.setArguments(args);
        }
//        else if (id == R.id.nav_review) {
//            //reviewOnApp();
//        }
        else if (id == R.id.nav_contact) {
            fm = new Contact_Us_fragment();
            args.putString("url", SupportUrl);
            args.putString("title", getResources().getString(R.string.nav_terms));
            fm.setArguments(args);

        }
//        else if (id == R.id.nav_review) {
//            reviewOnApp();
//        }
        else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_logout) {
            sessionManagement.logoutSession();
//            login.setVisibility(View.VISIBLE);
            finish();

        }
//        else if (id == R.id.nav_powerd) {
//            // stripUnderlines(textView);
//            String url = "http://sameciti.com";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);
//            finish();
//        }

        if (fm != null) {


            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi friends i am using ." + " http://play.google.com/store/apps/details?id=" + getPackageName() + " APP"); //getPackageName()
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void sideMenu() {

        if (sessionManagement.isLoggedIn()) {
            //  tv_number.setVisibility(View.VISIBLE);
            nav_menu.findItem(R.id.nav_logout).setVisible(true);
            nav_menu.findItem(R.id.nav_my_profile).setVisible(true);
            //   nav_menu.findItem(R.id.login).setVisible(true);
            nav_menu.findItem(R.id.sign).setVisible(false);
            nav_menu.findItem(R.id.nav_powerd).setVisible(true);

            username.setText("Welcome! " +
                    "" + sessionManagement.getUserDetails().get(BaseURL.KEY_NAME));

//            nav_menu.findItem(R.id.signup).setVisible(false);

//            nav_menu.findItem(R.id.nav_user).setVisible(true);
        } else {

            //tv_number.setVisibility(View.GONE);
//            tv_name.setText(getResources().getString(R.string.btn_login));
//            tv_name.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
//                    startActivity(i);
//                }
//            });
            nav_menu.findItem(R.id.login).setVisible(false);
            nav_menu.findItem(R.id.nav_my_profile).setVisible(false);
            nav_menu.findItem(R.id.nav_logout).setVisible(false);
            nav_menu.findItem(R.id.sign).setVisible(true);


            //            nav_menu.findItem(R.id.nav_user).setVisible(false);
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(20000);
        locationRequest.setFastestInterval(20000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "onConnected: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged: ");
    }

    @Override
    public void onTaskCompleted(String result) {
        Log.e(TAG, "onTaskCompleted: " + result);

        ((TextView) findViewById(R.id.address)).setText(result);
    }

//    public void setCartCounter(String totalitem) {
//        try {
//            totalBudgetCount.setText(totalitem);
//        }catch (Exception e){}
//    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("cardqnty")) {
//            totalBudgetCount.setText(pref.getInt("cardqnty",0));
            int badgeCount = pref.getInt("cardqnty", 0);
            if (badgeCount > 0) {
//                totalBudgetCount.setVisibility(View.VISIBLE);
//                totalBudgetCount.setText(""+badgeCount);
                navigation.getOrCreateBadge(R.id.navigation_notifications123).setNumber(badgeCount);
            } else {
//                totalBudgetCount.setVisibility(View.GONE);
                navigation.removeBadge(R.id.navigation_notifications123);
            }
        }
    }

    @Override
    protected void onDestroy() {
        pref.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    //    public void updateHeader() {
//        if (sessionManagement.isLoggedIn()) {
//            String getname = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
//            String getimage = sessionManagement.getUserDetails().get(BaseURL.KEY_IMAGE);
//            String getemail = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
//            SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
//            String previouslyEncodedImage = shre.getString("image_data", "");
//            if (!previouslyEncodedImage.equalsIgnoreCase("")) {
//                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
//                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                iv_profile.setImageBitmap(bitmap);
//            }
//            Glide.with(this)
//                    .load(BaseURL.IMG_PROFILE_URL + getimage)
//                    .placeholder(R.drawable.icon)
//                    .crossFade()
//                    .into(iv_profile);
    //  tv_name.setText(getname);

//        }
//    }


   /*

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem item = menu.findItem(R.id.action_cart);
        item.setVisible(true);

       View count = item.getActionView();
        totalBudgetCount = (TextView) count.findViewById(R.id.actionbar_notifcation_textview);
        count.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                menu.performIdentifierAction(item.getItemId(), 0);
            }
        });


        totalBudgetCount.setText("" + dbcart.getCartCount());
        return true;
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (resultCode == RESULT_OK){
//
//        }
        if (requestCode == 4) {
            if (data != null && data.getExtras() != null) {
//                String activityIdentify = data.getExtras().getString("actIdentfy");
//                if (activityIdentify != null && activityIdentify.equalsIgnoreCase("past")) {
//
//                }
                ArrayList<NewPendingDataModel> orderSubModels = (ArrayList<NewPendingDataModel>) data.getSerializableExtra("datalist");
                if (orderSubModels != null) {
                    dbcart.clearCart();
                    for (int i = 0; i < orderSubModels.size(); i++) {
                        NewPendingDataModel odModel = orderSubModels.get(i);
                        if (odModel.getDescription()!=null && !odModel.getDescription().equalsIgnoreCase("")){
                            HashMap<String, String> map = new HashMap<>();
                            map.put("varient_id", odModel.getVarient_id());
                            map.put("product_name", odModel.getProduct_name());
                            map.put("category_id", odModel.getVarient_id());
                            map.put("title", odModel.getProduct_name());
                            map.put("price", odModel.getPrice());
                            map.put("mrp", odModel.getTotal_mrp());
                            map.put("product_image", odModel.getVarient_image());
                            map.put("status", "1");
                            map.put("in_stock", "");
                            map.put("unit_value", odModel.getQuantity()+""+odModel.getUnit());
                            map.put("unit", "");
                            map.put("increament", "0");
                            map.put("rewards", "0");
                            map.put("stock", "0");
                            map.put("product_description", odModel.getDescription());

                            if (!odModel.getQty().equalsIgnoreCase("0")) {
                                dbcart.setCart(map, Integer.parseInt(odModel.getQty()));
                            } else {
                                dbcart.removeItemFromCart(map.get("varient_id"));
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                pref.edit().putInt("cardqnty", dbcart.getCartCount()).apply();
                            }
                        }
                    }

                    loadFragment(new CartFragment());

                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void getCurrency(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, currencyApi, response -> {
            Log.d("currency api", response);

            try {

                JSONObject currencyObject = new JSONObject(response);
                if (currencyObject.getString("status").equalsIgnoreCase("1") && currencyObject.getString("message").equalsIgnoreCase("currency")){

                    JSONObject dataObject = currencyObject.getJSONObject("data");

                    sessionManagement.setCurrency(dataObject.getString("currency_name"),dataObject.getString("currency_sign"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void checkOtpStatus() {

        Retrofit emailOtp = new Retrofit.Builder()
                .baseUrl(BaseURL.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        ApiInterface apiInterface = emailOtp.create(ApiInterface.class);

        Call<ForgotEmailModel> checkOtpStatus = apiInterface.getOtpOnOffStatus();
        checkOtpStatus.enqueue(new Callback<ForgotEmailModel>() {
            @Override
            public void onResponse(@NonNull Call<ForgotEmailModel> call, @NonNull retrofit2.Response<ForgotEmailModel> response) {
                if (response.isSuccessful()) {
                    ForgotEmailModel model = response.body();
                    if (model != null) {
                        if (model.getStatus().equalsIgnoreCase("0")) {
                            sessionManagement.setOtpStatus("0");
                        } else {
                            sessionManagement.setOtpStatus("1");
                        }
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<ForgotEmailModel> call, @NonNull Throwable t) {

            }
        });

    }

}
