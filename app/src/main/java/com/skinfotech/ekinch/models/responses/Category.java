package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable
{

    @SerializedName("sub_categories")
    @Expose
    private ArrayList<SubCategory> subCategories = null;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    private final static long serialVersionUID = -5466039065297914393L;

    public ArrayList<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

}