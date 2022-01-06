package com.skinfotech.ekinch.models.requests;

import com.google.gson.annotations.SerializedName;

public class CoursesRequest {

    @SerializedName("categoryId")
    private String categoryId = "";

    public CoursesRequest(String categoryId) {
        this.categoryId = categoryId;
    }
}
