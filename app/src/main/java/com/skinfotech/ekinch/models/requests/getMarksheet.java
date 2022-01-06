package com.skinfotech.ekinch.models.requests;

import com.google.gson.annotations.SerializedName;

public class getMarksheet {

    @SerializedName("userId")
    private int mUserId = 0;

    public getMarksheet(String userID) {
        this.mUserId=Integer.parseInt(userID);
    }
}
