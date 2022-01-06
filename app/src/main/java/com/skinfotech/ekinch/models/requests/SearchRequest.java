package com.skinfotech.ekinch.models.requests;

import com.google.gson.annotations.SerializedName;

public class SearchRequest {

    @SerializedName("userId")
    private String userId = "";
    @SerializedName("searchStr")
    private String searchStr = "";

    public SearchRequest(String userId, String searchStr) {
        this.userId = userId;
        this.searchStr = searchStr;
    }
}
