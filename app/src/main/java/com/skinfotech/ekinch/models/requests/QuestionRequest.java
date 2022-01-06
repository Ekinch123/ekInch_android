package com.skinfotech.ekinch.models.requests;

import com.google.gson.annotations.SerializedName;

public class QuestionRequest {

    @SerializedName("userId")
    private String userId = "";
    @SerializedName("courseId")
    private String courseId = "";

    public QuestionRequest(String userId, String courseId) {
        this.userId = userId;
        this.courseId = courseId;
    }
}
