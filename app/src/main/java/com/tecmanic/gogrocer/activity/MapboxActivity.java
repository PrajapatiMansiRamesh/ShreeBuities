package com.tecmanic.gogrocer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.Session_management;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MapboxActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnCameraIdleListener, PermissionsListener {

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final int REQUEST_CODE = 5678;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Location location;
    private ImageView back_btn;
    private RecyclerView search_view_recy;
    private TextView search_text;
    private LinearLayout search_lay;
    private LinearLayout address_lay;
    private LinearLayout current_Loc;
    private TextView address_text;
    private TextView save_loc;
    private Session_management session_management;
    private ProgressBar progressBar;
    private String address;
    private View search_view;
    private LinearLayout progress_bar;
    private boolean inPlacePredection = false;
    private CarmenFeature home;
    private CarmenFeature work;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private LocationEngine locationEngine;
    private String map_access_token;
    private PermissionsManager permissionsManager;


    private void intialLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(MapboxActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                if (result != null && result.getLastLocation() != null) {
                    Location locationd = result.getLastLocation();
                    location = locationd;
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(14).build()), 4000);
                    getAddress();
                }
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });


    }

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
        if (getIntent() != null) {
            map_access_token = getIntent().getStringExtra("map_key");
        }
//        getMapKey();
        Mapbox.getInstance(this, map_access_token);
        setContentView(R.layout.activity_mapbox);
        session_management = new Session_management(MapboxActivity.this);
        mapView = findViewById(R.id.mapView);
        back_btn = findViewById(R.id.back_btn);
        search_view_recy = findViewById(R.id.search_view_recy);
        search_text = findViewById(R.id.search_txt);
        search_lay = findViewById(R.id.search_lay);
        progressBar = findViewById(R.id.progressbar);
        address_text = findViewById(R.id.address_text);
        address_lay = findViewById(R.id.address_lay);
        save_loc = findViewById(R.id.save_loc);
        search_view = findViewById(R.id.search_view);
        progress_bar = findViewById(R.id.progress_bar);
        current_Loc = findViewById(R.id.current_Loc);
        search_lay.setVisibility(View.GONE);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        location = new Location("A");
        back_btn.setOnClickListener(v -> onBackPressed());
        search_view.setOnClickListener(view -> setPlaceDescription());
        save_loc.setOnClickListener(v -> onBackPressed());

        current_Loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                enableLocationComponent();
                intialLocationEngine();
            }
        });

    }

//    private void getMapKey() {
//        StringRequest request = new StringRequest(StringRequest.Method.GET, BaseURL.MAPBOX_KEY, response -> {
//            Log.i("mapkey", response);
//            Gson mapGson = new Gson();
//            MapboxModel mapModel = mapGson.fromJson(response, MapboxModel.class);
//            map_access_token = mapModel.getData().getMap_api_key();
////            Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
//            show();
//        }, error -> {
//            show();
//            error.printStackTrace();
//        });
//
//        RequestQueue rq = Volley.newRequestQueue(MapboxActivity.this);
//        request.setRetryPolicy(new RetryPolicy() {
//            @Override
//            public int getCurrentTimeout() {
//                return 90000;
//            }
//
//            @Override
//            public int getCurrentRetryCount() {
//                return 0;
//            }
//
//            @Override
//            public void retry(VolleyError error) throws VolleyError {
//
//            }
//        });
//        rq.add(request);
//
//    }

    private void setPlaceDescription() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS))
                .build(MapboxActivity.this);
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.getUiSettings().setCompassEnabled(false);
        mapboxMap.getUiSettings().setLogoEnabled(false);
//        LatLngBounds latLngBounds = LatLngBounds.from(36.5, 93.8, 7.2, 67.8);
//        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 11));
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            addUserLocations();
            style.addImage(symbolIconId, BitmapFactory.decodeResource(
                    MapboxActivity.this.getResources(), R.drawable.map_default_map_marker));
            setUpSource(style);
            setupLayer(style);
//            enableLocationComponent(style);
        });
        intialLocationEngine();
        mapboxMap.addOnCameraIdleListener(MapboxActivity.this);
    }

    @Override
    public void onCameraIdle() {
        LatLng latLng = mapboxMap.getCameraPosition().target;
        if (latLng != null) {
            inPlacePredection = true;
            location.setLatitude(latLng.getLatitude());
            location.setLongitude(latLng.getLongitude());
            getAddress();
        }
    }

    private void addUserLocations() {
        home = CarmenFeature.builder().text("SF Office")
                .geometry(Point.fromLngLat(29.013462, 77.013403))
                .placeName("Jatwara Rd, Jamalpura, Sonipat, Haryana 131001")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("DC Office")
                .placeName("Hanuman Nagar, Sonipat, Haryana 131001")
                .geometry(Point.fromLngLat(29.017333, 77.012922))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})
        ));
    }

    private void getAddress() {
        new Thread(() -> {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(MapboxActivity.this, Locale.getDefault());
            DecimalFormat dFormat = new DecimalFormat("#.######");
            double latitude = Double.parseDouble(dFormat.format(location.getLatitude()));
            double longitude = Double.parseDouble(dFormat.format(location.getLongitude()));

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                String city = addresses.get(0).getLocality();
                session_management.setLocationCity(city);
                session_management.setLocationPref(String.valueOf(latitude), String.valueOf(longitude));
//                Log.i("TAG", "" + strReturnedAddress.toString());
//                Log.i("TAG", "" + returnedAddress.toString());
                address = returnedAddress.getAddressLine(0);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (inPlacePredection) {
                        if (!address.equalsIgnoreCase("")) {
                            address_lay.setVisibility(View.VISIBLE);
                            address_text.setText(address);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                }
            }
        }
//        else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
//            CarmenFeature carmenFeature = PlacePicker.getPlace(data);
//            if (carmenFeature != null) {
//                address_lay.setVisibility(View.VISIBLE);
//                address_text.setText(String.format(
//                        getString(R.string.selected_place_info), carmenFeature.toJson()));
//            }
//        }
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationComponent.setLocationComponentEnabled(true);
//            locationComponent.setCameraMode(CameraMode.TRACKING);
//            locationComponent.setRenderMode(RenderMode.COMPASS);
            onCameraIdle();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setResult(22);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Permission Needed For this!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(style -> {
//                enableLocationComponent(style);
                intialLocationEngine();
            });
        } else {
            Toast.makeText(this, "Permission Not Granted!", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}