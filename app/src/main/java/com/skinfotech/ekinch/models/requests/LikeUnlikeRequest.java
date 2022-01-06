package com.skinfotech.ekinch.models.requests;

import com.google.gson.annotations.SerializedName;

public class LikeUnlikeRequest {
    @SerializedName("userId")
    private String userId = "";
    @SerializedName("video_id")
    private String video_id = "";

    public LikeUnlikeRequest(String userId, String video_id) {
        this.userId = userId;
        this.video_id = video_id;
    }
}
