package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CertificateResponce implements Serializable {

    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("videos")
    @Expose
    private ArrayList<Video> videos = null;
    private final static long serialVersionUID = -5354332141046731431L;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
    }

}