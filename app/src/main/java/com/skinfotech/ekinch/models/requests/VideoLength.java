package com.skinfotech.ekinch.models.requests;

import com.google.gson.annotations.SerializedName;

public class VideoLength {
    @SerializedName("video_id")
    private String video_id = "";
    @SerializedName("seen_length")
    private String seen_length = "";
    @SerializedName("user_id")
    private String user_id = "";
    @SerializedName("total_duration")
    private String total_duration = "";

    public VideoLength(String video_id, String seen_length, String user_id, String total_duration) {
        this.video_id=video_id;
        this.seen_length=seen_length;
        this.user_id=user_id;
        this.total_duration=total_duration;

    }
}
