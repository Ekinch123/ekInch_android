package com.skinfotech.ekinch.models.requests;

import com.google.gson.annotations.SerializedName;

public class ProfileRequest {

    @SerializedName("name")
    private String mUserName = "";
    @SerializedName("aadharNumber")
    private String mAadharNumber = "";
    @SerializedName("mobileNumber")
    private String mMobileNumber = "";
    @SerializedName("userId")
    private String mUserId = "";
    @SerializedName("locationId")
    private String mLocationId = "";
    @SerializedName("location_name")
    private String locationName = "";

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    @SerializedName("profession")
    private String profession = "";

    @SerializedName("organisationName")
    private String organisationName = "";

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public void setLocationId(String locationId) {
        mLocationId = locationId;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public void setAadharNumber(String aadharNumber) {
        mAadharNumber = aadharNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        mMobileNumber = mobileNumber;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }
}
