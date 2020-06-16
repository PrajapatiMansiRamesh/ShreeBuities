package com.tecmanic.gogrocer.util;

import android.content.Context;
import android.content.Intent;

import com.tecmanic.gogrocer.Activity.LoginActivity;
import com.tecmanic.gogrocer.Activity.MainActivity;

import java.util.HashMap;

import static com.tecmanic.gogrocer.Config.BaseURL.APP_OTP_STATUS;
import static com.tecmanic.gogrocer.Config.BaseURL.CART_ID_FINAL;
import static com.tecmanic.gogrocer.Config.BaseURL.IS_LOGIN;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_EMAIL;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_HOUSE;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_ID;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_IMAGE;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_MOBILE;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_NAME;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_PASSWORD;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_PAYMENT_METHOD;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_PINCODE;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_REWARDS_POINTS;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_SOCITY_ID;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_SOCITY_NAME;
import static com.tecmanic.gogrocer.Config.BaseURL.KEY_WALLET_Ammount;
import static com.tecmanic.gogrocer.Config.BaseURL.TOTAL_AMOUNT;
import static com.tecmanic.gogrocer.Config.BaseURL.USER_CURRENCY;
import static com.tecmanic.gogrocer.Config.BaseURL.USER_CURRENCY_CNTRY;
import static com.tecmanic.gogrocer.Config.BaseURL.USER_EMAIL_SERVICE;
import static com.tecmanic.gogrocer.Config.BaseURL.USER_INAPP_SERVICE;
import static com.tecmanic.gogrocer.Config.BaseURL.USER_OTP;
import static com.tecmanic.gogrocer.Config.BaseURL.USER_SKIP;
import static com.tecmanic.gogrocer.Config.BaseURL.USER_SMS_SERVICE;
import static com.tecmanic.gogrocer.Config.BaseURL.USER_STATUS;

/**
 * Created by Rajesh Dabhi on 28/6/2017.
 */

public class Session_management {


//    SharedPreferences prefs;
//    SharedPreferences prefs2;
//
//    SharedPreferences.Editor editor;
//    SharedPreferences.Editor editor2;
//    SharedPreferences.Editor editor3;


    SharedPreferenceUtil pref;


    Context context;

    int PRIVATE_MODE = 0;

    public Session_management(Context context) {

        this.context = context;
        pref = new SharedPreferenceUtil(context);
//        prefs = context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
//        editor = prefs.edit();
//
//        prefs2 = context.getSharedPreferences(PREFS_NAME2, PRIVATE_MODE);
//        editor2 = prefs2.edit();

    }

    public void createLoginSession(String id, String email, String name
            , String mobile, String password) {


        pref.setBoolean(IS_LOGIN, true);
        pref.setString(KEY_ID, id);
        pref.setString(KEY_EMAIL, email);
        pref.setString(KEY_NAME, name);
        pref.setString(KEY_MOBILE, mobile);
        pref.setString(KEY_PASSWORD, password);
//        pref.setString(USER_STATUS,"0");
        pref.setBoolean(USER_SKIP, false);
        pref.save();

//        editor.putBoolean(IS_LOGIN, true);
//        editor.putString(KEY_ID, id);
//        editor.putString(KEY_EMAIL, email);
//        editor.putString(KEY_NAME, name);
//        editor.putString(KEY_MOBILE, mobile);
//        editor.putString(KEY_PASSWORD,password);
//        editor.commit();
    }

    public void createLoginSession(String id, String email, String name
            , String mobile, String password, boolean isLogin, String any) {


        pref.setBoolean(IS_LOGIN, isLogin);
        pref.setString(KEY_ID, id);
        pref.setString(KEY_EMAIL, email);
        pref.setString(KEY_NAME, name);
        pref.setString(KEY_MOBILE, mobile);
        pref.setString(KEY_PASSWORD, password);
//        pref.setString(USER_STATUS,"0");
        pref.setBoolean(USER_SKIP, false);
        pref.save();

//        editor.putBoolean(IS_LOGIN, true);
//        editor.putString(KEY_ID, id);
//        editor.putString(KEY_EMAIL, email);
//        editor.putString(KEY_NAME, name);
//        editor.putString(KEY_MOBILE, mobile);
//        editor.putString(KEY_PASSWORD,password);
//        editor.commit();
    }

    public void createLoginSession(String id, String email, String name, String mobile, String password, boolean skip) {


        pref.setBoolean(IS_LOGIN, false);
        pref.setString(KEY_ID, id);
        pref.setString(KEY_EMAIL, email);
        pref.setString(KEY_NAME, name);
        pref.setString(KEY_MOBILE, mobile);
        pref.setString(KEY_PASSWORD, password);
//        pref.setString(USER_STATUS,"0");
        pref.setBoolean(USER_SKIP, skip);
        pref.save();

//        editor.putBoolean(IS_LOGIN, true);
//        editor.putString(KEY_ID, id);
//        editor.putString(KEY_EMAIL, email);
//        editor.putString(KEY_NAME, name);
//        editor.putString(KEY_MOBILE, mobile);
//        editor.putString(KEY_PASSWORD,password);
//        editor.commit();
    }


    public void createotpSession(String useremail, String username, String user_phone) {

//        editor.putBoolean(IS_LOGIN, true);
////        editor.putString(KEY_ID, id);
//        editor.putString(KEY_EMAIL, useremail);
//        editor.putString(KEY_NAME, username);
//        editor.putString(KEY_MOBILE, user_phone);
//
//        editor.commit();
    }

    public void createsignupSession(String id) {

//        editor.putBoolean(IS_LOGIN, true);
//        editor.putString(KEY_ID, id);
//
//        editor.commit();
    }

    public void checkLogin() {

//        if (!this.isLoggedIn()) {
//            Intent loginsucces = new Intent(context, LoginActivity.class);
//            // Closing all the Activities
//            loginsucces.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//            // Add new Flag to start new Activity
//            loginsucces.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            context.startActivity(loginsucces);
//        }
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_ID, pref.getString(KEY_ID, null));
        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));
        user.put(KEY_IMAGE, pref.getString(KEY_IMAGE, null));
        user.put(KEY_WALLET_Ammount, pref.getString(KEY_WALLET_Ammount, null));
        user.put(KEY_REWARDS_POINTS, pref.getString(KEY_REWARDS_POINTS, null));
        user.put(KEY_PAYMENT_METHOD, pref.getString(KEY_PAYMENT_METHOD, ""));
        user.put(TOTAL_AMOUNT, pref.getString(TOTAL_AMOUNT, null));
        user.put(KEY_PINCODE, pref.getString(KEY_PINCODE, null));
        user.put(KEY_SOCITY_ID, pref.getString(KEY_SOCITY_ID, null));
        user.put(KEY_SOCITY_NAME, pref.getString(KEY_SOCITY_NAME, null));
        user.put(KEY_HOUSE, pref.getString(KEY_HOUSE, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // return user
        return user;
    }

    public void updateData(String name, String mobile, String pincode
            , String socity_id, String image, String wallet, String rewards, String house) {

//        editor.putString(KEY_NAME, name);
//        editor.putString(KEY_MOBILE, mobile);
//        editor.putString(KEY_PINCODE, pincode);
//        editor.putString(KEY_SOCITY_ID, socity_id);
//        editor.putString(KEY_IMAGE, image);
//        editor.putString(KEY_WALLET_Ammount, wallet);
//        editor.putString(KEY_REWARDS_POINTS, rewards);
//        editor.putString(KEY_HOUSE, house);
//
//        editor.apply();
    }

    public void updateSocity(String socity_name, String socity_id) {
//        editor.putString(KEY_SOCITY_NAME, socity_name);
//        editor.putString(KEY_SOCITY_ID, socity_id);
//
//        editor.apply();
    }

    public void logoutSession() {
//        editor.clear();
//        editor.commit();
        pref.clearAll();
        cleardatetime();

        Intent logout = new Intent(context, MainActivity.class);
        // Closing all the Activities
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(logout);
    }

    public void logoutSessionwithchangepassword() {
//        editor.clear();
//        editor.commit();

        cleardatetime();

        Intent logout = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(logout);
    }

    public void creatdatetime(String date, String time) {
//        editor2.putString(KEY_DATE, date);
//        editor2.putString(KEY_TIME, time);
//
//        editor2.commit();
    }

    public void cleardatetime() {
//        editor2.clear();
//        editor2.commit();
    }

    public HashMap<String, String> getdatetime() {
        HashMap<String, String> user = new HashMap<String, String>();
//
//        user.put(KEY_DATE, prefs2.getString(KEY_DATE, null));
//        user.put(KEY_TIME, prefs2.getString(KEY_TIME, null));

        return user;
    }

    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void setLogin(boolean value) {
        pref.setBoolean(IS_LOGIN, value);
    }

    public boolean isSkip() {
        return pref.getBoolean(USER_SKIP, false);
    }


    public String userBlockStatus() {
        return pref.getString(USER_STATUS, "2");
    }

    public void setUserBlockStatus(String value) {
        pref.setString(USER_STATUS, value);
    }

    public void setEmailServer(String value) {
        pref.setString(USER_EMAIL_SERVICE, value);
    }

    public void setUserSMSService(String value) {
        pref.setString(USER_SMS_SERVICE, value);
    }

    public void setUserInAppService(String value) {
        pref.setString(USER_INAPP_SERVICE, value);
    }

    public String getEmailService() {
        return pref.getString(USER_EMAIL_SERVICE, "0");
    }

    public String getSMSService() {
        return pref.getString(USER_SMS_SERVICE, "0");
    }

    public String getINAPPService() {
        return pref.getString(USER_INAPP_SERVICE, "0");
    }

    public String userOtp() {
        return pref.getString(USER_OTP, "0");
    }

    public void setOtp(String value) {
        pref.setString(USER_OTP, value);
    }

    public String userId() {
        return pref.getString(KEY_ID, "");
    }

    public void setCartID(String value) {
        pref.setString(CART_ID_FINAL, value);
    }

    public String getCartId() {
        return pref.getString(CART_ID_FINAL, "");
    }

    public String getCurrency() {
        return pref.getString(USER_CURRENCY, "");
    }

    public String getCurrencyCountry() {
        return pref.getString(USER_CURRENCY_CNTRY, "");
    }

    public void setCurrency(String name, String currency) {
        pref.setString(USER_CURRENCY, currency);
        pref.setString(USER_CURRENCY_CNTRY, name);
    }

    public String getOtpSatus() {
        return pref.getString(APP_OTP_STATUS, "1");
    }

    public void setOtpStatus(String value) {
        pref.setString(APP_OTP_STATUS, value);
    }

}
