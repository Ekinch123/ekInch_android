package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MarkSheetResponse extends CommonResponse {

    @SerializedName("markSheetList")
    private List<MarkSheetItem> mMarkSheetList = new ArrayList<>();


    @SerializedName("seen_videos")
    private List<seenVideos> seen_videos = new ArrayList<>();

    public List<MarkSheetItem> getMarkSheetList() {
        return mMarkSheetList;
    }

    public List<seenVideos> getSeen_videos() {
        return seen_videos;
    }

    public static class MarkSheetItem {

        @SerializedName("courseTitle")
        private String markSheetTitle = "";
        @SerializedName("markSheetId")
        private String markSheetId = "";
        @SerializedName("courseTotalMarks")
        private String courseTotalMarks = "";
        @SerializedName("courseMarksObtained")
        private String courseMarksObtained = "";
        @SerializedName("courseStatus")
        private String courseStatus = "";

        public String getMarkSheetTitle() {
            return markSheetTitle;
        }

        public String getMarkSheetId() {
            return markSheetId;
        }

        public String getCourseTotalMarks() {
            return courseTotalMarks;
        }

        public String getCourseMarksObtained() {
            return courseMarksObtained;
        }

        public String getCourseStatus() {
            return courseStatus;
        }
    }

    public static class seenVideos {


        @SerializedName("video_id")
        private String videoId = "";
        @SerializedName("id")
        private String id = "";
        @SerializedName("title")
        private String videoName = "";
        @SerializedName("videoFileName")
        private String videoFileName = "";
        @SerializedName("seen_length")
        private String viewDuration = "";
        @SerializedName("count")
        private String videoViewCount = "";

        public String getVideoId() {
            return videoId;
        }

        public String getVideoName() {
            return videoName;
        }

        public String getVideoFileName() {
            return videoFileName;
        }

        public String getViewDuration() {
            return viewDuration;
        }

        public String getVideoViewCount() {
            return videoViewCount;
        }
    }
}
