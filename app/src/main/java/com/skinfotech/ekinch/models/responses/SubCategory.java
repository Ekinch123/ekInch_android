package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SubCategory implements Serializable {

    @SerializedName("sub_category_name")
    @Expose
    private String subCategoryName;
    @SerializedName("sub_category_data")
    @Expose
    private ArrayList<SubCategoryDatum> subCategoryData = new ArrayList<>();

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public ArrayList<SubCategoryDatum> getSubCategoryData() {
        return subCategoryData;
    }

    public void setSubCategoryData(ArrayList<SubCategoryDatum> subCategoryData) {
        this.subCategoryData = subCategoryData;
    }

}