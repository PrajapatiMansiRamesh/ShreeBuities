package com.tecmanic.gogrocer.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tecmanic.gogrocer.Activity.LoginActivity;
import com.tecmanic.gogrocer.Activity.RechargeWallet;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.Config.SharedPref;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.ConnectivityReceiver;
import com.tecmanic.gogrocer.util.Session_management;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


/**
 * Created by Rajesh Dabhi on 29/6/2017.
 */

public class Wallet_fragment extends Fragment {

    private static String TAG = Wallet_fragment.class.getSimpleName();
    private TextView Wallet_Ammount;
    private TextView Recharge_Wallet;
    private Session_management sessionManagement;
    private Context context;
    private ProgressDialog progressDialog;

    public Wallet_fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_wallet_ammount, container, false);
        getActivity().setTitle("Wallet");
        sessionManagement = new Session_management(container.getContext());
        context = container.getContext();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        //   String getwallet = sessionManagement.getUserDetails().get(BaseURL.KEY_WALLET_Ammount);
        Wallet_Ammount = view.findViewById(R.id.wallet_ammount);
        //     Wallet_Ammount.setText(getwallet);
        Recharge_Wallet = view.findViewById(R.id.recharge_wallet);
        Recharge_Wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManagement.isLoggedIn()) {
                    if (sessionManagement.userBlockStatus().equalsIgnoreCase("2")) {
                        Intent intent = new Intent(v.getContext(), RechargeWallet.class);
                        startActivityForResult(intent, 5);
                    } else {
                        showBloackDialog();
                    }

                } else {
                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
                    startActivity(intent);
                }

            }
        });

        if (ConnectivityReceiver.isConnected()) {
            getRefresrh();
        } else {
            Wallet_Ammount.setText(sessionManagement.getCurrency() + "0");
        }


        return view;

    }

    private void showBloackDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
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

    private void getRefresrh() {
        progressDialog.show();
        String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        RequestQueue rq = Volley.newRequestQueue(requireActivity());
        StringRequest strReq = new StringRequest(Request.Method.POST, BaseURL.WALLET_REFRESH + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String status = jObj.getString("status");
                            if (status.equals("1")) {
                                String wallet_amount = jObj.getString("data");
                                Wallet_Ammount.setText(sessionManagement.getCurrency() + "" + wallet_amount);
                                SharedPref.putString(context, BaseURL.KEY_WALLET_Ammount, wallet_amount);
                            } else {
                                // Toast.makeText(DashboardPage.this, "" + jObj.optString("msg"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }) {

        };
        rq.add(strReq);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            if (data != null && data.getExtras() != null) {
                if (Objects.requireNonNull(data.getStringExtra("recharge")).equalsIgnoreCase("success")) {
                    getRefresrh();
                }
            }
        }
    }
}