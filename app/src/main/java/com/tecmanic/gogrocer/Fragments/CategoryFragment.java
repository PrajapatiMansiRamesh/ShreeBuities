package com.tecmanic.gogrocer.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tecmanic.gogrocer.activity.CategoryPage;
import com.tecmanic.gogrocer.Adapters.HomeCategoryAdapter;
import com.tecmanic.gogrocer.ModelClass.HomeCate;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.CustomVolleyJsonRequest;
import com.tecmanic.gogrocer.util.FragmentClickListner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tecmanic.gogrocer.Config.BaseURL.Categories;

public class CategoryFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView recyclerSubCate;
    HomeCategoryAdapter cateAdapter, subCateAdapter;
    ProgressDialog progressDialog;
    String catId;
    Gson gson;
    private List<HomeCate> cateList = new ArrayList<>();
    private List<HomeCate> subcateList = new ArrayList<>();
    private boolean isSubcat = false;
    private FragmentClickListner fragmentClickListner;

    public CategoryFragment(FragmentClickListner fragmentClickListner) {
        this.fragmentClickListner = fragmentClickListner;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        recyclerView = view.findViewById(R.id.recyclerCAte);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getActivity().setTitle(getResources().getString(R.string.Category));
        progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
        if (isOnline()) {
            categoryUrl();
        }
        return view;
    }

    private void categoryUrl() {
        cateList.clear();
        // Tag used to cancel the request
        String tag_json_obj = "json_get_address_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.GET,
                Categories, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("categdrytyguioj", response.toString());

                try {
                    if (response != null && response.length() > 0) {
                        String status = response.getString("status");
                        if (status.equals("1")) {
                            JSONArray array = response.getJSONArray("data");


                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.getJSONObject(i);
                                HomeCate model = new HomeCate();


                                model.setDetail(object.getString("description"));
                                model.setId(object.getString("cat_id"));
                                model.setImages(object.getString("image"));
                                model.setName(object.getString("title"));

                                model.setSub_array(object.getJSONArray("subcategory"));
                                cateList.add(model);
                            }
                            cateAdapter = new HomeCategoryAdapter(cateList, getContext(), cat_id -> {
                                Intent intent = new Intent(requireActivity(), CategoryPage.class);
                                intent.putExtra("cat_id", cat_id);
                                startActivityForResult(intent, 24);
                            });
                            recyclerView.setAdapter(cateAdapter);
                            cateAdapter.notifyDataSetChanged();
//
//                            Gson gson = new Gson();
//                            Type listType = new TypeToken<List<HomeCate>>() {
//                            }.getType();
//                            cateList = gson.fromJson(response.getString("data"), listType);
//
                        }
                    } else {
                        // Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
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
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.getCache().clear();
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
        requestQueue.add(jsonObjReq);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 24) {
            if (data != null && data.getBooleanExtra("open", false)){
                if (fragmentClickListner != null) {
                    fragmentClickListner.onFragmentClick(data.getBooleanExtra("open",false));
                }
            }
//            fragmentClickListner.onFragmentClick(data.getBooleanExtra("open", false));
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


}
