package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LocationResponse extends CommonResponse {

    @SerializedName("locationList")
    private List<LocationItem> mLocationList = new ArrayList<>();

    public List<LocationItem> getLocationList() {
        return mLocationList;
    }

    public static class LocationItem {

        @SerializedName("locationName")
        private String locationName = "";
        @SerializedName("locationId")
        private String locationId = "";

        public String getLocationName() {
            return locationName;
        }

        public String getLocationId() {
            return locationId;
        }
    }
}
