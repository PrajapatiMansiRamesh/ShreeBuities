package com.tecmanic.gogrocer.network;

import com.tecmanic.gogrocer.ModelClass.CountryCodeModel;
import com.tecmanic.gogrocer.ModelClass.FirebaseStatusModel;
import com.tecmanic.gogrocer.ModelClass.ForgotEmailModel;
import com.tecmanic.gogrocer.ModelClass.MapSelectionModel;
import com.tecmanic.gogrocer.ModelClass.NotificationBannerStatus;
import com.tecmanic.gogrocer.ModelClass.NotifyModelUser;
import com.tecmanic.gogrocer.ModelClass.PaymentVia;
import com.tecmanic.gogrocer.ModelClass.VerifyOtp;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("forgot_password")
    @FormUrlEncoded
    Call<ForgotEmailModel> getEmailOtp(@Field("user_email") String userEmail,@Field("user_phone") String userPhone);

    @POST("verify_via_firebase")
    @FormUrlEncoded
    Call<VerifyOtp> getVerifyOtpStatus(@Field("status ") String status , @Field("user_phone") String userPhone);

    @GET("checkotponoff")
    Call<ForgotEmailModel> getOtpOnOffStatus();

    @GET("pymnt_via")
    Call<PaymentVia> getPaymentVia();

    @POST("notifyby")
    @FormUrlEncoded
    Call<NotifyModelUser> getNotifyUser(@Field("user_id") String userId);

    @GET("mapby")
    Call<MapSelectionModel> getMapSelectionStatus();

    @GET("firebase")
    Call<FirebaseStatusModel> getFirebaseOtpStatus();

    @GET("countrycode")
    Call<CountryCodeModel> getCountryCode();

    @GET("app_notice")
    Call<NotificationBannerStatus> getNotificationBannerStatus();

}
