package com.tecmanic.gogrocer.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
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
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tecmanic.gogrocer.Adapters.BannerAdapter;
import com.tecmanic.gogrocer.Adapters.CategoryGridAdapter;
import com.tecmanic.gogrocer.Adapters.DealsAdapter;
import com.tecmanic.gogrocer.Adapters.HomeCategoryAdapter;
import com.tecmanic.gogrocer.Adapters.Home_adapter;
import com.tecmanic.gogrocer.Adapters.MainScreenAdapter;
import com.tecmanic.gogrocer.Adapters.PageAdapter;
import com.tecmanic.gogrocer.Adapters.Popular_adapter;
import com.tecmanic.gogrocer.Categorygridquantity;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.Constans.RecyclerTouchListener;
import com.tecmanic.gogrocer.ModelClass.CategoryGrid;
import com.tecmanic.gogrocer.ModelClass.Category_model;
import com.tecmanic.gogrocer.ModelClass.HomeCate;
import com.tecmanic.gogrocer.ModelClass.MainScreenList;
import com.tecmanic.gogrocer.ModelClass.MapboxModel;
import com.tecmanic.gogrocer.ModelClass.NewCartModel;
import com.tecmanic.gogrocer.ModelClass.NewCategoryDataModel;
import com.tecmanic.gogrocer.ModelClass.NotificationBannerStatus;
import com.tecmanic.gogrocer.ModelClass.Popular_model;
import com.tecmanic.gogrocer.ModelClass.varient_product;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.activity.AddressLocationActivity;
import com.tecmanic.gogrocer.activity.CategoryPage;
import com.tecmanic.gogrocer.activity.DealActivity;
import com.tecmanic.gogrocer.activity.MainActivity;
import com.tecmanic.gogrocer.activity.MapboxActivity;
import com.tecmanic.gogrocer.activity.ViewAll_TopDeals;
import com.tecmanic.gogrocer.network.ApiInterface;
import com.tecmanic.gogrocer.util.AppController;
import com.tecmanic.gogrocer.util.CustomSlider;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;
import com.tecmanic.gogrocer.util.FragmentClickListner;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.VolleyLog.TAG;
import static com.tecmanic.gogrocer.Config.BaseURL.ADDRESS;
import static com.tecmanic.gogrocer.Config.BaseURL.BANNER;
import static com.tecmanic.gogrocer.Config.BaseURL.BANNER_IMG_URL;
import static com.tecmanic.gogrocer.Config.BaseURL.BANN_IMG_URL;
import static com.tecmanic.gogrocer.Config.BaseURL.CITY;
import static com.tecmanic.gogrocer.Config.BaseURL.HomeDeal;
import static com.tecmanic.gogrocer.Config.BaseURL.HomeRecent;
import static com.tecmanic.gogrocer.Config.BaseURL.HomeTopSelling;
import static com.tecmanic.gogrocer.Config.BaseURL.LAT;
import static com.tecmanic.gogrocer.Config.BaseURL.LONG;
import static com.tecmanic.gogrocer.Config.BaseURL.MyPrefreance;
import static com.tecmanic.gogrocer.Config.BaseURL.popularcateHome;
import static com.tecmanic.gogrocer.Config.BaseURL.secondary_banner;
import static com.tecmanic.gogrocer.Config.BaseURL.topsix;
import static com.tecmanic.gogrocer.Config.BaseURL.whatsnew;

public class HomeeeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG1 = "MainActivity";
    ViewPager viewPager;
    TabLayout tabLayout;
    PageAdapter pageAdapter;
    TabItem tab1, tab2, tab3, tab4;
    Float translationY = 100f;
    FloatingActionButton fabMain, fabOne, fabTwo, fabThree, fabfour;
    LinearLayout parent_lay;
    LinearLayout progressBar;
    CardView Search_layout;
    //    ScrollView scrollView;
    NestedScrollView scrollView;
    RecyclerView rv_items;
    SliderLayout banner_slider, featuredslider;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    Boolean isMenuOpen = false;
    RecyclerView recyclerViewCate, recyclerViewDEal;
    HomeCategoryAdapter cateListAdapter;
    DealsAdapter dealAdapter;
    String productId;
    TextView viewall_topdeals;
    List<Address> addresses = new ArrayList<>();
    String latitude, longitude, address, city, state, country, postalCode;
    LocationManager locationManager;
    SharedPreferences sharedPreferences;
    ArrayList<String> imageString = new ArrayList<>();
    private RecyclerView recyclerImages;
    private List<HomeCate> cateList = new ArrayList<>();
    //    private List<CategoryGrid> dealList = new ArrayList<>();
   private List<Category_model> category_modelList = new ArrayList<>();
    private Home_adapter adapter;
    private boolean isSubcat = false;
    private BannerAdapter bannerAdapter, bannerAdapter1;
    private LinearLayout viewpager_layout;
    private LinearLayout change_loc_lay;
    private TextView not_product;
    private TextView change_loc;
    private ArrayList<String> imageString1 = new ArrayList<>();
    private RecyclerView recyclerImages1;
    private Session_management session_management;
    private FragmentClickListner fragmentClickListner;
    private ViewPager2 viewPager2;
    private ProgressDialog progressDialog;

    private List<MainScreenList> screenLists = new ArrayList<>();
    private List<NewCartModel> topSelling = new ArrayList<>();
    private List<NewCartModel> whatsNew = new ArrayList<>();
    private List<NewCartModel> recentSelling = new ArrayList<>();
    private List<NewCartModel> dealOftheday = new ArrayList<>();
    private MainScreenAdapter screenAdapter;
    private Context contexts;
    private int tabCounter = 0;
    String getid,cat_id;
    LinearLayout llPop,lltop;
    RecyclerView rv_populrcate,recycler_product;
    private List<Popular_model> popularList = new ArrayList<>();
    Popular_adapter popularAdapter;
    List<NewCategoryDataModel> newCategoryDataModel = new ArrayList<>();
    CategoryGridAdapter productadapter;
    private List<varient_product> varientProducts = new ArrayList<>();
    BottomSheetBehavior behavior;
    private LinearLayout bottom_sheet;
    public static String totalOff;
    public static RecyclerView recyler_popup;
    public static TextView txt;
    public static LinearLayout cancl;
    private RelativeLayout notificationLay;
    private TextView notificaitonStatus;
    private boolean bannerStatus = false;
    private String bannerText = "";
    public HomeeeFragment(FragmentClickListner fragmentClickListner) {
        this.fragmentClickListner = fragmentClickListner;
        // Required empty public constructor
    }
    private void getNotificationStatus() {
        Retrofit emailOtp = new Retrofit.Builder()
                .baseUrl(BaseURL.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        ApiInterface apiInterface = emailOtp.create(ApiInterface.class);

        Call<NotificationBannerStatus> checkOtpStatus = apiInterface.getNotificationBannerStatus();
        checkOtpStatus.enqueue(new Callback<NotificationBannerStatus>() {
            @Override
            public void onResponse(@NonNull Call<NotificationBannerStatus> call, @NonNull retrofit2.Response<NotificationBannerStatus> response) {
                if (response.isSuccessful()) {
                    NotificationBannerStatus model = response.body();
                    if (model != null) {
                        bannerStatus = model.getData().getStatus().equalsIgnoreCase("1");
                        bannerText = model.getData().getNotice();
                        requireActivity().runOnUiThread(() -> {
                            if (bannerStatus) {
                                notificationLay.setVisibility(View.VISIBLE);
                                notificaitonStatus.setText(bannerText);
                                notificaitonStatus.setSelected(true);
                            } else {
                                notificationLay.setVisibility(View.GONE);
                                notificaitonStatus.setText("");
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationBannerStatus> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home, container, false);
        requireActivity().setTitle(getResources().getString(R.string.app_name));
        contexts = container.getContext();
        sharedPreferences = requireContext().getSharedPreferences(MyPrefreance, MODE_PRIVATE);
        session_management = new Session_management(container.getContext());
        progressDialog = new ProgressDialog(container.getContext());
        progressDialog.setMessage("Please wait while loading..");
        progressDialog.setCancelable(false);
        latitude = sharedPreferences.getString(LAT, null);
        longitude = sharedPreferences.getString(LONG, null);
        address = sharedPreferences.getString(ADDRESS, null);
        city = sharedPreferences.getString(CITY, null);
        bannerAdapter = new BannerAdapter(getActivity(), imageString);

        // loc.setText(address+", "+city+", "+postalCode);
        rv_items = view.findViewById(R.id.rv_home);
        progressBar = view.findViewById(R.id.progress_bar);

        change_loc_lay = view.findViewById(R.id.change_loc_lay);
        viewpager_layout = view.findViewById(R.id.viewpager_layout);
        not_product = view.findViewById(R.id.not_product);
        change_loc = view.findViewById(R.id.change_loc);
//        tab1 = view.findViewById(R.id.top_selling_item);
//        tab2 = view.findViewById(R.id.recent_item);
//        tab3 = view.findViewById(R.id.deals_item);
//        tab4 = view.findViewById(R.id.whtsnewitem);
        notificationLay = view.findViewById(R.id.notification_lay);
        notificaitonStatus = view.findViewById(R.id.notificaiton_status);
        tabLayout = view.findViewById(R.id.tablayout);
        viewall_topdeals = view.findViewById(R.id.viewall_topdeals);
        viewPager = view.findViewById(R.id.pager_product);
        viewPager2 = view.findViewById(R.id.viewpa_2);
        recyclerImages1 = view.findViewById(R.id.recycler_image_slider1);
        recyclerImages = view.findViewById(R.id.recycler_image_slider);
        banner_slider = view.findViewById(R.id.relative_banner);
        featuredslider = view.findViewById(R.id.featured_img_slider);
        rv_items = view.findViewById(R.id.rv_home);
        fabMain = view.findViewById(R.id.fabMain);
        fabOne = view.findViewById(R.id.fabOne);
        fabTwo = view.findViewById(R.id.fabTwo);
        fabThree = view.findViewById(R.id.fabThree);
        fabfour = view.findViewById(R.id.fabfour);
        parent_lay = view.findViewById(R.id.parent_lay);
        screenAdapter = new MainScreenAdapter(container.getContext(), screenLists);
        viewPager2.setAdapter(screenAdapter);
        recyclerImages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerImages.setAdapter(bannerAdapter);
        // home_list.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false));


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rv_items.setLayoutManager(gridLayoutManager);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setNestedScrollingEnabled(false);
        rv_items.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_items, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String getid = category_modelList.get(position).getCat_id();
                Intent intent = new Intent(getActivity(), CategoryPage.class);
                intent.putExtra("cat_id", getid);
                intent.putExtra("title", category_modelList.get(position).getTitle());
                intent.putExtra("image", category_modelList.get(position).getImage());
                startActivityForResult(intent, 24);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        change_loc.setOnClickListener(v -> {
            if (session_management.getMapSelection().equalsIgnoreCase("mapbox")) {
//                startActivityForResult(new Intent(v.getContext(), MapboxActivity.class), 22);
                getMapKey();
            } else {
                startActivityForResult(new Intent(v.getContext(), AddressLocationActivity.class), 22);
            }
//            startActivityForResult(new Intent(requireContext(), AddressLocationActivity.class), 22);
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
//            alertDialog.setCancelable(true);
//            alertDialog.setMessage("This feature is not available in Demo!");
////        alertDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialogInterface, int i) {
////                dialogInterface.dismiss();
////            }
////        });
//            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//
//            alertDialog.show();
        });
        lltop= view.findViewById(R.id.lltop);
        llPop= view.findViewById(R.id.llPop);
        rv_populrcate= view.findViewById(R.id.rv_populrcate);

        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 3);
        rv_populrcate.setLayoutManager(gridLayoutManager1);
        rv_populrcate.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_populrcate, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                 getid = popularList.get(position).getCat_id();
                product(getid);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        txt = view.findViewById(R.id.txt);
        cancl = view.findViewById(R.id.cancl);

        new Thread(this::getNotificationStatus).start();
        bottom_sheet = view.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottom_sheet);
//        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                // React to state change
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                // React to dragging events
//            }
//        });

        Categorygridquantity categorygridquantity = new Categorygridquantity() {
            @Override
            public void onClick(View vieww, int position, String ccId, String iddd) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                // Toast.makeText(getActivity(),position+ iddd + ccId,Toast.LENGTH_LONG).show();


                txt.setText(iddd);

                cancl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });
             //   Varient_product(ccId, recyler_popup, iddd);
            }

            @Override
            public void onCartItemAddOrMinus() {

            }
        };

        recycler_product= view.findViewById(R.id.recycler_product);
        recycler_product.setLayoutManager(new GridLayoutManager(getContext(), 1));
        productadapter = new CategoryGridAdapter(newCategoryDataModel, getContext(), categorygridquantity);
        recycler_product.setAdapter(productadapter);


        bannerAdapter1 = new BannerAdapter(getActivity(), imageString1);
        recyclerImages1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerImages1.setAdapter(bannerAdapter1);
//        loc=view.findViewById(R.id.loc);
        Search_layout = view.findViewById(R.id.ll3);
        scrollView = view.findViewById(R.id.scroll_view);
        scrollView.setSmoothScrollingEnabled(true);


        if (isOnline()) {
            makeGetSliderRequest();
            second_banner();
            PopularCategoryRequest();
            topSelling();
        }


        Search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SearchFragment trending_fragment = new SearchFragment();
                FragmentManager manager = getParentFragmentManager();
//                FragmentManager m = getSu();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.replace(R.id.contentPanel, trending_fragment);
                fragmentTransaction.commit();

            }
        });


        fabOne.setAlpha(0f);
        fabTwo.setAlpha(0f);
        fabThree.setAlpha(0f);
        fabfour.setAlpha(0f);

        fabOne.setTranslationY(translationY);
        fabTwo.setTranslationY(translationY);
        fabThree.setTranslationY(translationY);
        fabfour.setTranslationY(translationY);

        fabMain.setOnClickListener(this);
        fabOne.setOnClickListener(this);
        fabTwo.setOnClickListener(this);
        fabThree.setOnClickListener(this);
        fabfour.setOnClickListener(this);

        closeMenu(false);

        setTabs();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.i("TAG Demo", String.valueOf(position));
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                if (position == 0) {
//                    if (topSelling.size() > 0) {
//                        viewall_topdeals.setVisibility(View.VISIBLE);
//                    } else {
//                        viewall_topdeals.setVisibility(View.GONE);
//                    }
//                } else if (position == 1) {
//                    if (recentSelling.size() > 0) {
//                        viewall_topdeals.setVisibility(View.VISIBLE);
//                    } else {
//                        viewall_topdeals.setVisibility(View.GONE);
//                    }
//                } else if (position == 2) {
//                    if (dealOftheday.size() > 0) {
//                        viewall_topdeals.setVisibility(View.VISIBLE);
//                    } else {
//                        viewall_topdeals.setVisibility(View.GONE);
//                    }
//                } else if (position == 3) {
//                    if (whatsNew.size() > 0) {
//                        viewall_topdeals.setVisibility(View.VISIBLE);
//                    } else {
//                        viewall_topdeals.setVisibility(View.GONE);
//                    }
//                }
                viewPager2.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                try {
                    if (ViewPager2.SCROLL_STATE_IDLE == state) {
                        screenAdapter.notifyItemChanged(viewPager2.getCurrentItem());
                    } else {
                        super.onPageScrollStateChanged(state);
                    }
                } catch (IllegalStateException e) {
                    super.onPageScrollStateChanged(state);
                }
            }
        });

        viewall_topdeals.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewAll_TopDeals.class);
            if (tabLayout.getTabAt(viewPager2.getCurrentItem()).getText().toString().equalsIgnoreCase("TOP SELLING")) {
                if (topSelling.size() > 0) {
                    intent.putExtra("action_name", "Top_Deals_Fragment");
                    startActivityForResult(intent, 56);
                } else {
                    Toast.makeText(contexts, "No Order found in your location!", Toast.LENGTH_SHORT).show();
                }
            } else if (tabLayout.getTabAt(viewPager2.getCurrentItem()).getText().toString().equalsIgnoreCase("RECENT SELLING")) {
                if (recentSelling.size() > 0) {
                    intent.putExtra("action_name", "Recent_Details_Fragment");
                    startActivityForResult(intent, 56);
                } else {
                    Toast.makeText(contexts, "No Order found in your location!", Toast.LENGTH_SHORT).show();
                }

            } else if (tabLayout.getTabAt(viewPager2.getCurrentItem()).getText().toString().equalsIgnoreCase("DEALS OF THE DAY")) {
                if (dealOftheday.size() > 0) {
                    Intent intent1 = new Intent(v.getContext(), DealActivity.class);
                    intent1.putExtra("action_name", "Deals_Fragment");
                    startActivityForResult(intent1, 56);
                } else {
                    Toast.makeText(contexts, "No Order found in your location!", Toast.LENGTH_SHORT).show();
                }

            } else if (tabLayout.getTabAt(viewPager2.getCurrentItem()).getText().toString().equalsIgnoreCase("WHAT'S NEW")) {
                if (whatsNew.size() > 0) {
                    intent.putExtra("action_name", "Whats_New_Fragment");
                    startActivityForResult(intent, 56);
                } else {
                    Toast.makeText(contexts, "No Order found in your location!", Toast.LENGTH_SHORT).show();
                }
            }

        });

//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                setUpTabClick();
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
        return view;
    }


    private void show() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void getMapKey() {
        show();
        StringRequest request = new StringRequest(StringRequest.Method.GET, BaseURL.MAPBOX_KEY, response -> {
            Log.i("mapkey", response);
            Gson mapGson = new Gson();
            MapboxModel mapModel = mapGson.fromJson(response, MapboxModel.class);
            String map_access_token = mapModel.getData().getMap_api_key();
            show();
            startActivityForResult(new Intent(requireActivity(), MapboxActivity.class).putExtra("map_key", map_access_token), 22);
//            Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        }, error -> {
            show();
            error.printStackTrace();
        });

        RequestQueue rq = Volley.newRequestQueue(requireActivity());
        request.setRetryPolicy(new RetryPolicy() {
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
        rq.add(request);

    }

    private void setTabs() {
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {


            if (position == 0) {
//                Log.i("TAG NEW",""+position);
                if (screenLists.size() > 0) {
                    tab.setText(screenLists.get(0).getViewType());
                }

//                if (screenLists.size() == 4){
//                    tab.setText("TOP SELLING");
//                }else {
//                    if (topSelling.size()>0){
//                        tab.setText("TOP SELLING");
//                    }else if (recentSelling.size()>0){
//                        tab.setText("RECENT SELLING");
//                    }else if (dealOftheday.size()>0){
//                        tab.setText("DEALS OF THE DAY");
//                    }else if (whatsNew.size()>0){
//                        tab.setText("WHAT'S NEW");
//                    }
//                }

            } else if (position == 1) {
//                Log.i("TAG NEW",""+position);
//                if (screenLists.size() == 4){
//                    tab.setText("RECENT SELLING");
//                }else {
//                    if (topSelling.size()>0){
//                        tab.setText("TOP SELLING");
//                    }else if (recentSelling.size()>0){
//                        tab.setText("RECENT SELLING");
//                    }else if (dealOftheday.size()>0){
//                        tab.setText("DEALS OF THE DAY");
//                    }else if (whatsNew.size()>0){
//                        tab.setText("WHAT'S NEW");
//                    }
//                }

                if (screenLists.size() > 0) {
                    tab.setText(screenLists.get(1).getViewType());
                }

            } else if (position == 2) {
//                Log.i("TAG NEW",""+position);
//                if (screenLists.size() == 4){
//                    tab.setText("DEALS OF THE DAY");
//                }else {
//                    if (topSelling.size()>0){
//                        tab.setText("TOP SELLING");
//                    }else if (recentSelling.size()>0){
//                        tab.setText("RECENT SELLING");
//                    }else if (dealOftheday.size()>0){
//                        tab.setText("DEALS OF THE DAY");
//                    }else if (whatsNew.size()>0){
//                        tab.setText("WHAT'S NEW");
//                    }
//                }
                if (screenLists.size() > 0) {
                    tab.setText(screenLists.get(2).getViewType());
                }
            } else if (position == 3) {
//                Log.i("TAG NEW",""+position);
                tab.setText("WHAT'S NEW");
            }

        });
        tabLayoutMediator.attach();
    }

    private void topSelling() {
        progressDialog.show();
        screenLists.clear();
        topSelling.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HomeTopSelling, response -> {
            Log.d("HomeTopSelling", response);

            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("1")) {
//                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    screenLists.add(new MainScreenList("TOP SELLING", topSelling, recentSelling, dealOftheday, whatsNew));
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<NewCartModel>>() {
                    }.getType();
                    List<NewCartModel> listorl = gson.fromJson(jsonObject.getString("data"), listType);
                    topSelling.addAll(listorl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                recentDeal();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recentDeal();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("lat", session_management.getLatPref());
                params.put("lng", session_management.getLangPref());
                params.put("city", session_management.getLocationCity());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(contexts);
        requestQueue.getCache().clear();
        stringRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(stringRequest);
    }

    private void whatsNew() {
        whatsNew.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, whatsnew, response -> {
            Log.d("HomeTopSelling", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("1")) {
//                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    screenLists.add(new MainScreenList("WHAT'S NEW", topSelling, recentSelling, dealOftheday, whatsNew));
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<NewCartModel>>() {
                    }.getType();
                    List<NewCartModel> listorl = gson.fromJson(jsonObject.getString("data"), listType);
                    whatsNew.addAll(listorl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

                if (screenLists.size() > 0) {
                    change_loc_lay.setVisibility(View.GONE);
//                    viewpager_layout.setVisibility(View.VISIBLE);
                    viewPager2.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    viewall_topdeals.setVisibility(View.VISIBLE);
                } else {
                    change_loc_lay.setVisibility(View.VISIBLE);
//                    viewpager_layout.setVisibility(View.GONE);
                    viewPager2.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    viewall_topdeals.setVisibility(View.GONE);
                }
                setTabs();
                progressDialog.dismiss();
                screenAdapter.notifyDataSetChanged();
                makeGetCategoryRequest();
//                tabCounter = screenLists.size()-1;
//                setUpTabClick();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (screenLists.size() > 0) {
                    change_loc_lay.setVisibility(View.GONE);
//                    viewpager_layout.setVisibility(View.VISIBLE);
                    viewPager2.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    viewall_topdeals.setVisibility(View.VISIBLE);
                } else {
                    change_loc_lay.setVisibility(View.VISIBLE);
//                    viewpager_layout.setVisibility(View.GONE);
                    viewPager2.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    viewall_topdeals.setVisibility(View.GONE);
                }
                setTabs();
                progressDialog.dismiss();
//                setUpTabClick();
                screenAdapter.notifyDataSetChanged();
                makeGetCategoryRequest();
//                tabCounter = screenLists.size()-1;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("lat", session_management.getLatPref());
                params.put("lng", session_management.getLangPref());
                params.put("city", session_management.getLocationCity());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(contexts);
        requestQueue.getCache().clear();
        stringRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(stringRequest);
    }

    private void DealOfTheDay() {
        dealOftheday.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HomeDeal, response -> {
            Log.d("HomeTopSelling", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("1")) {
//                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    screenLists.add(new MainScreenList("DEALS OF THE DAY", topSelling, recentSelling, dealOftheday, whatsNew));
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<NewCartModel>>() {
                    }.getType();
                    List<NewCartModel> listorl = gson.fromJson(jsonObject.getString("data"), listType);
                    dealOftheday.addAll(listorl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                whatsNew();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                whatsNew();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("lat", session_management.getLatPref());
                params.put("lng", session_management.getLangPref());
                params.put("city", session_management.getLocationCity());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(contexts);
        requestQueue.getCache().clear();
        stringRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(stringRequest);
    }

    private void recentDeal() {
        recentSelling.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HomeRecent, response -> {
            Log.d("HomeTopSelling", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("1")) {
//                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    screenLists.add(new MainScreenList("RECENT SELLING", topSelling, recentSelling, dealOftheday, whatsNew));
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<NewCartModel>>() {
                    }.getType();
                    List<NewCartModel> listorl = gson.fromJson(jsonObject.getString("data"), listType);
                    recentSelling.addAll(listorl);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                DealOfTheDay();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DealOfTheDay();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("lat", session_management.getLatPref());
                params.put("lng", session_management.getLangPref());
                params.put("city", session_management.getLocationCity());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(contexts);
        requestQueue.getCache().clear();
        stringRequest.setRetryPolicy(new RetryPolicy() {
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
        requestQueue.add(stringRequest);
    }

    private void openMenu() {
        isMenuOpen = !isMenuOpen;
        fabMain.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();

        fabOne.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabOne.setVisibility(View.VISIBLE);
        fabTwo.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.setVisibility(View.VISIBLE);
        fabThree.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabThree.setVisibility(View.VISIBLE);
        fabfour.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabfour.setVisibility(View.VISIBLE);
    }

    private void closeMenu() {
        isMenuOpen = !isMenuOpen;

        fabMain.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();

        fabOne.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabOne.setVisibility(View.GONE);
        fabTwo.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.setVisibility(View.GONE);
        fabThree.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabThree.setVisibility(View.GONE);
        fabfour.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabfour.setVisibility(View.GONE);


    }

    private void closeMenu(boolean value) {
        isMenuOpen = value;

        fabMain.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();

        fabOne.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabOne.setVisibility(View.GONE);
        fabTwo.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.setVisibility(View.GONE);
        fabThree.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabThree.setVisibility(View.GONE);
        fabfour.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabfour.setVisibility(View.GONE);


    }

    private void handleFabOne() {
        Log.i(TAG, "handleFabOne: ");
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabMain:

                Log.i(TAG, "onClick: fab main");
                if (isMenuOpen) {
                    fabOne.setVisibility(View.GONE);
                    fabTwo.setVisibility(View.GONE);
                    fabThree.setVisibility(View.GONE);
                    fabfour.setVisibility(View.GONE);
                    closeMenu();
                } else {
                    fabOne.setVisibility(View.VISIBLE);
                    fabTwo.setVisibility(View.VISIBLE);
                    fabThree.setVisibility(View.VISIBLE);
                    fabfour.setVisibility(View.VISIBLE);
                    openMenu();
                }
                break;
            case R.id.fabOne:
                Intent sendIntent1 = new Intent();
                sendIntent1.setAction(Intent.ACTION_SEND);
                sendIntent1.putExtra(Intent.EXTRA_TEXT, "Hi friends i am using ." + " http://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + " APP");
                sendIntent1.setType("text/plain");
                startActivity(sendIntent1);

                Log.i(TAG, "onClick: fab one");
                handleFabOne();
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;
            case R.id.fabTwo:
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
                break;
            case R.id.fabThree:
                String smsNumber = "919889887711";
                openWhatsApp(smsNumber);
                break;


            case R.id.fabfour:

                if (isPermissionGranted()) {
                    call_action();
                }

                Log.i(TAG, "onClick: fab four");
                break;
        }
    }

    private void openWhatsApp(String numberwhats) {
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(numberwhats) + "@s.whatsapp.net");//phone number without "+" prefix

            startActivity(sendIntent);
        } else {
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            Toast.makeText(requireContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
            startActivity(goToMarket);
        }
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = requireContext().getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                    call_action();
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void call_action() {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + "919889887711"));
        startActivity(callIntent);

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void makeGetSliderRequest() {
        imageString.clear();
        String tag_json_obj = "json_category_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.GET, BANNER, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("fghgh", response.toString());
                        try {
                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                            JSONArray jsonArray = response.getJSONArray("data");
                            if (jsonArray.length() <= 0) {
                                recyclerImages.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    HashMap<String, String> url_maps = new HashMap<String, String>();
                                    url_maps.put("banner_name", jsonObject.getString("banner_name"));
                                    url_maps.put("banner_id", jsonObject.getString("banner_id"));
                                    url_maps.put("banner_image", BANN_IMG_URL + jsonObject.getString("banner_image"));
                                    imageString.add(BANN_IMG_URL + jsonObject.getString("banner_image"));
                                    listarray.add(url_maps);
                                }


                                bannerAdapter.notifyDataSetChanged();


                                for (HashMap<String, String> name : listarray) {
                                    CustomSlider textSliderView = new CustomSlider(getActivity());
                                    textSliderView.description(name.get("")).image(name.get("banner_image")).setScaleType(BaseSliderView.ScaleType.Fit);
                                    textSliderView.bundle(new Bundle());
                                    textSliderView.getBundle().putString("extra", name.get("banner_name"));
                                    textSliderView.getBundle().putString("extra", name.get("banner_id"));
//                                home_list_banner.addSlider(textSliderView);
                                    //   banner_slider.addSlider(textSliderView);
                                    final String sub_cat = (String) textSliderView.getBundle().get("extra");
                                    textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                        @Override
                                        public void onSliderClick(BaseSliderView slider) {
                                            //   Toast.makeText(getActivity(), "" + sub_cat, Toast.LENGTH_SHORT).show();
//                                        Bundle args = new Bundle();
//                                        android.app.Fragment fm = new Product_fragment();
//                                        args.putString("id", sub_cat);
//                                        fm.setArguments(args);
//                                        FragmentManager fragmentManager = getFragmentManager();
//                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                                                .addToBackStack(null).commit();
                                        }
                                    });
                                }
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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.getCache().clear();
        requestQueue.add(jsonObjReq);


    }

    private void second_banner() {
        imageString1.clear();
        String tag_json_obj = "json_category_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.GET, secondary_banner, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("fghgh", response.toString());
                        try {
                            ArrayList<HashMap<String, String>> listarray = new ArrayList<>();
                            JSONArray jsonArray = response.getJSONArray("data");
                            if (jsonArray.length() <= 0) {
                                recyclerImages1.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    HashMap<String, String> url_maps = new HashMap<String, String>();
                                    url_maps.put("banner_name", jsonObject.getString("banner_name"));
                                    url_maps.put("banner_id", jsonObject.getString("sec_banner_id"));
                                    url_maps.put("banner_image", BANNER_IMG_URL + jsonObject.getString("banner_image"));
                                    imageString1.add(BANNER_IMG_URL + jsonObject.getString("banner_image"));
                                    listarray.add(url_maps);
                                }

                                bannerAdapter1.notifyDataSetChanged();

                               /* for (HashMap<String, String> name : listarray) {
                                    CustomSlider textSliderView = new CustomSlider(getActivity());
                                    textSliderView.description(name.get("")).image(name.get("banner_image")).setScaleType(BaseSliderView.ScaleType.Fit);
                                    textSliderView.bundle(new Bundle());
                                    textSliderView.getBundle().putString("extra", name.get("banner_name"));
                                    textSliderView.getBundle().putString("extra", name.get("banner_id"));
                                   // banner_slider.addSlider(textSliderView);

                                }*/
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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.getCache().clear();
        requestQueue.add(jsonObjReq);


    }

    private void makeGetCategoryRequest() {
        progressDialog.show();
        String tag_json_obj = "json_category_req";
        isSubcat = false;
        Map<String, String> params = new HashMap<>();
        params.put("lat", session_management.getLatPref());
        params.put("lng", session_management.getLangPref());
        params.put("city", session_management.getLocationCity());
        isSubcat = true;
        category_modelList.clear();
       /* if (parent_id != null && parent_id != "") {
        }*/

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                topsix, params, response -> {
                    Log.d(TAG, response.toString());
                    progressDialog.dismiss();
                    try {
                        if (response.length() > 0) {
                            String status = response.getString("status");
                            if (status.contains("1")) {
                                lltop.setVisibility(View.VISIBLE);
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Category_model>>() {
                                }.getType();
                                List<Category_model> category_modelLists = gson.fromJson(response.getString("data"), listType);
                                category_modelList.addAll(category_modelLists);
                                adapter = new Home_adapter(category_modelList);
                                rv_items.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            else {
                                lltop.setVisibility(View.GONE);
                            }
                            progressDialog.dismiss();
                        }
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
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

    private void PopularCategoryRequest() {
        progressDialog.show();
        popularList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, popularcateHome, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("popularcateHome",response);

                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String msg = jsonObject.getString("message");
                    if(status.equalsIgnoreCase("1")) {
                        llPop.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject resultObj = jsonArray.getJSONObject(i);

                            String title = resultObj.getString("title");
                            String image = resultObj.getString("image");
                            cat_id = resultObj.getString("cat_id");
                      /*  String description = resultObj.getString("description");
                        String count = resultObj.getString("count");
*/
                            Popular_model recentData = new Popular_model(cat_id, title, image);
                            popularList.add(recentData);
                            popularAdapter = new Popular_adapter(popularList);
                            rv_populrcate.setAdapter(popularAdapter);
                            popularAdapter.notifyDataSetChanged();
                            int position = 0;
                            product(popularList.get(position).getCat_id());
                        }
                    }else {
                        llPop.setVisibility(View.GONE);
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                param.put("lat", session_management.getLatPref());
                param.put("lng", session_management.getLangPref());
                return param;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    private void product(String getid) {
        newCategoryDataModel.clear();
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        // Tag used to cancel the request
        String tag_json_obj = "json_order_detail_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("cat_id", getid);
        params.put("lat", session_management.getLatPref());
        params.put("lng", session_management.getLangPref());
        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.cat_product, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("CheckApi", response.toString());

                newCategoryDataModel.clear();
                progressDialog.dismiss();


                try {
                    String status = response.getString("status");

//                    String message = response.getString("message");

                    if (status.contains("1")) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<NewCategoryDataModel>>() {
                        }.getType();
                        List<NewCategoryDataModel> listorl = gson.fromJson(response.getString("data"), listType);
                        newCategoryDataModel.addAll(listorl);

//                        for (int i = 0; i < listorl.size(); i++) {
//                            List<NewCategoryVarientList> listddd = listorl.get(i).getVarients();
//                            for (int j = 0; j < listddd.size(); j++) {
//                                NewCategoryShowList newCategoryShowList = new NewCategoryShowList(listorl.get(i).getProduct_id(), listorl.get(i).getProduct_name(), listorl.get(i).getProduct_image(), listddd.get(j));
//                                newModelList.add(newCategoryShowList);
//                            }
//                        }
                        progressDialog.dismiss();
                        productadapter.notifyDataSetChanged();
                    }
                   // progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }finally{
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                VolleyLog.d("", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 24) {
            if (data != null && data.getBooleanExtra("open", false)) {
                if (fragmentClickListner != null) {
                    fragmentClickListner.onFragmentClick(data.getBooleanExtra("open", false));
                }
            }
        } else if (requestCode == 56) {
            if (data != null && data.getBooleanExtra("carttogo", false)) {
                if (fragmentClickListner != null) {
                    fragmentClickListner.onFragmentClick(data.getBooleanExtra("carttogo", false));
                }
            } else {
                topSelling();
            }
        } else if (requestCode == 22) {
            if (fragmentClickListner != null) {
                fragmentClickListner.onChangeHome(true);
            }
        }
    }
}