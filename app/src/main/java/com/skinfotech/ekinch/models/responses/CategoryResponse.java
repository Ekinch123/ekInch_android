package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CategoryResponse extends CommonResponse {

    @SerializedName("categoryList")
    private ArrayList<CategoryItem> mCategoryList = new ArrayList<>();

    public ArrayList<CategoryItem> getCategoryList() {
        return mCategoryList;
    }

    public static class CategoryItem {

        @SerializedName("categoryImage")
        private String categoryImage = "";
        @SerializedName("categoryTitle")
        private String categoryTitle = "";

        public void setCategoryImage(String categoryImage) {
            this.categoryImage = categoryImage;
        }

        public void setCategoryTitle(String categoryTitle) {
            this.categoryTitle = categoryTitle;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        @SerializedName("categoryId")
        private String categoryId = "";

        public String getCategoryImage() {
            return categoryImage;
        }

        public String getCategoryTitle() {
            return categoryTitle;
        }

        public String getCategoryId() {
            return categoryId;
        }
    }
}
