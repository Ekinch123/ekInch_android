package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.SerializedName;

public class LikeDislike {
    @SerializedName("errorCode")
    private String mErrorCode = "";
    @SerializedName("errorMessage")
    private String mErrorMessage = "";
    @SerializedName("totalCount")
    private int totalCount = 0;

    public int getTotalCount() {
        return totalCount;
    }


    public String getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
