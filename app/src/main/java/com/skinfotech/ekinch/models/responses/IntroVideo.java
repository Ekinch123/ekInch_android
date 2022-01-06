package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.SerializedName;

public class IntroVideo extends CommonResponse{
    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    @SerializedName("video")
    private String video = "";
}
