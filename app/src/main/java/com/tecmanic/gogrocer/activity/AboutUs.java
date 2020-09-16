package com.tecmanic.gogrocer.activity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tecmanic.gogrocer.Config.BaseURL;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.ConnectivityReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class AboutUs extends AppCompatActivity {
    private static String tag = AboutUs.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        setTitle("About us");
        TextView tvInfo = (TextView) findViewById(R.id.tv_info);
        if (ConnectivityReceiver.isConnected()) {
            makeGetInfoRequest(tvInfo);
        }
    }

    private void makeGetInfoRequest(TextView tvInfo) {

        // Tag used to cancel the request

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, BaseURL.AboutUrl, null, response -> {
            Log.d(tag, response.toString());
            Log.d("fsdfsd", response.toString());

            try {
                // Parsing json array response
                // loop through each json object

                String status = response.getString("status");
                if (status.contains("1")) {

                    JSONObject jsonObject = response.getJSONObject("data");

                    String description = jsonObject.getString("description");

                    tvInfo.setText(Html.fromHtml(description).toString());

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }, Throwable::printStackTrace);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
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
error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjReq);
    }
}

