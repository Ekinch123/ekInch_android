package com.skinfotech.ekinch.models.requests;

import com.google.gson.annotations.SerializedName;

public class OtpRequest {

    @SerializedName("otp")
    private String mOtp = "";
    @SerializedName("mobileNumber")
    private String mMobileNumber = "";

    public OtpRequest(String otp, String mobileNumber) {
        mOtp = otp;
        mMobileNumber = mobileNumber;
    }
}
