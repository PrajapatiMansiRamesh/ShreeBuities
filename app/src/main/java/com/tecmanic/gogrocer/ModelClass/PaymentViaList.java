package com.tecmanic.gogrocer.ModelClass;

import com.squareup.moshi.Json;

public class PaymentViaList {

    @Json(name = "p_id")
    private String p_id;
    @Json(name = "paypal")
    private String paypal;
    @Json(name = "razorpay")
    private String razorpay;
    @Json(name = "paystack")
    private String paystack;

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getPaypal() {
        return paypal;
    }

    public void setPaypal(String paypal) {
        this.paypal = paypal;
    }

    public String getRazorpay() {
        return razorpay;
    }

    public void setRazorpay(String razorpay) {
        this.razorpay = razorpay;
    }

    public String getPaystack() {
        return paystack;
    }

    public void setPaystack(String paystack) {
        this.paystack = paystack;
    }

    @Override
    public String toString() {
        return "PaymentViaList{" +
                "p_id='" + p_id + '\'' +
                ", paypal='" + paypal + '\'' +
                ", razorpay='" + razorpay + '\'' +
                ", paystack='" + paystack + '\'' +
                '}';
    }
}
