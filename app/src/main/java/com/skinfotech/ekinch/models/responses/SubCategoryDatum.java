package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SubCategoryDatum implements Serializable {

    @SerializedName("coursesId")
    @Expose
    private String coursesId;
    @SerializedName("videoId")
    @Expose
    private String videoId;
    @SerializedName("coursesImage")
    @Expose
    private String coursesImage;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("totalLikes")
    @Expose
    private Integer totalLikes;
    @SerializedName("comments")
    @Expose
    private ArrayList<Object> comments = null;
    private final static long serialVersionUID = 4380143801266246701L;

    public String getCoursesId() {
        return coursesId;
    }

    public void setCoursesId(String coursesId) {
        this.coursesId = coursesId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getCoursesImage() {
        return coursesImage;
    }

    public void setCoursesImage(String coursesImage) {
        this.coursesImage = coursesImage;
    }

    public Integer getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Integer totalLikes) {
        this.totalLikes = totalLikes;
    }

    public ArrayList<Object> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Object> comments) {
        this.comments = comments;
    }

}