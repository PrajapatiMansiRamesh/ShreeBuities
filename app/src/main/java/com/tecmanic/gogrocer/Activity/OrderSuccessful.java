package com.tecmanic.gogrocer.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.DatabaseHandler;
import com.tecmanic.gogrocer.util.Session_management;

public class OrderSuccessful extends AppCompatActivity {

    Button btn_ShopMore;
    private SharedPreferences pref;
    private Session_management sessionManagement;
    DatabaseHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);
        btn_ShopMore=findViewById(R.id.btn_ShopMore);
        pref = getSharedPreferences("GOGrocer", Context.MODE_PRIVATE);
//        pref.registerOnSharedPreferenceChangeListener(this);
        sessionManagement = new Session_management(OrderSuccessful.this);
        dbHandler = new DatabaseHandler(OrderSuccessful.this);
        dbHandler.clearCart();
        SharedPreferences preferences = getSharedPreferences("GOGrocer", Context.MODE_PRIVATE);
        preferences.edit().putInt("cardqnty", 0).apply();

        btn_ShopMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dbHandler.setCart()
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
