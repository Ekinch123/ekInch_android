package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CoursesResponse extends CommonResponse {

    @SerializedName("coursesList")
    private List<CoursesItem> mCoursesList = new ArrayList<>();

    public List<CoursesItem> getCoursesList() {
        return mCoursesList;
    }

    public static class CoursesItem {

        @SerializedName("coursesImage")
        private String coursesImage = "";
        @SerializedName("coursesTitle")
        private String coursesTitle = "";
        @SerializedName("coursesId")
        private String coursesId = "";

        public void setTotalLikes(int totalLikes) {
            this.totalLikes = totalLikes;
        }

        @SerializedName("totalLikes")
        private int totalLikes = 0;

        public int getTotalLikes() {
            return totalLikes;
        }

        public String getCoursesImage() {
            return coursesImage;
        }

        public String getCoursesTitle() {
            return coursesTitle;
        }

        public String getCoursesId() {
            return coursesId;
        }
    }
}
