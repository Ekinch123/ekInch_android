package com.skinfotech.ekinch.models.requests;

import com.google.gson.annotations.SerializedName;

public class VerifyMobileRequest {

    @SerializedName("mobileNumber")
    private String mMobileNumber = "";
    @SerializedName("messageId")
    private String mMessageId = "";

    public VerifyMobileRequest(String mobileNumber, String messageId) {
        mMobileNumber = mobileNumber;
        mMessageId = messageId;
    }
}
