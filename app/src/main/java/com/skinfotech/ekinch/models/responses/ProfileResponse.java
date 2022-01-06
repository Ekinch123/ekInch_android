package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProfileResponse extends CommonResponse {

    @SerializedName("name")
    private String mUserName = "";
    @SerializedName("aadharNumber")
    private String mAadharNumber = "";
    @SerializedName("mobileNumber")
    private String mMobileNumber = "";
    @SerializedName("location")
    private String mLocationName = "";
    @SerializedName("photo")
    private String mPhotoUrl = "";

    @SerializedName("profession")
    private String profession = "";

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    @SerializedName("location_name")
    private String location_name = "";

    public String getOrganisatonName() {
        return organisatonName;
    }

    public void setOrganisatonName(String organisatonName) {
        this.organisatonName = organisatonName;
    }

    @SerializedName("organisatonName")
    private String organisatonName = "";

    public List<MarkSheetResponse.seenVideos> getSeen_videos() {
        return seen_videos;
    }

    public void setSeen_videos(List<MarkSheetResponse.seenVideos> seen_videos) {
        this.seen_videos = seen_videos;
    }

    @SerializedName("seen_videos")
    private List<MarkSheetResponse.seenVideos> seen_videos = new ArrayList<>();

    public String getPhotoUrl() {
        if (mPhotoUrl.equalsIgnoreCase("https://ekinch.in/api/profile_pics/"))
            return "";
        return mPhotoUrl;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getAadharNumber() {
        return mAadharNumber;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }
}
