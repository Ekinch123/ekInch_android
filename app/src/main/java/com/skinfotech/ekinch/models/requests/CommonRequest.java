package com.skinfotech.ekinch.models.requests;

import com.google.gson.annotations.SerializedName;

public class CommonRequest {

    @SerializedName("userId")
    private String mUserId = "";

    public CommonRequest(String userId) {
        mUserId = userId;
    }
}
