package com.skinfotech.ekinch.models.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class MarkSheetDto implements Parcelable {

    private String markSheetTitle = "";
    private String markSheetId = "";
    private String courseTotalMarks = "";
    private String courseMarksObtained = "";
    private String courseStatus = "";

    public MarkSheetDto(String markSheetTitle, String markSheetId, String courseTotalMarks, String courseMarksObtained, String courseStatus) {
        this.markSheetTitle = markSheetTitle;
        this.markSheetId = markSheetId;
        this.courseTotalMarks = courseTotalMarks;
        this.courseMarksObtained = courseMarksObtained;
        this.courseStatus = courseStatus;
    }

    protected MarkSheetDto(Parcel in) {
        markSheetTitle = in.readString();
        markSheetId = in.readString();
        courseTotalMarks = in.readString();
        courseMarksObtained = in.readString();
        courseStatus = in.readString();
    }

    public static final Creator<MarkSheetDto> CREATOR = new Creator<MarkSheetDto>() {
        @Override
        public MarkSheetDto createFromParcel(Parcel in) {
            return new MarkSheetDto(in);
        }

        @Override
        public MarkSheetDto[] newArray(int size) {
            return new MarkSheetDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(markSheetTitle);
        parcel.writeString(markSheetId);
        parcel.writeString(courseTotalMarks);
        parcel.writeString(courseMarksObtained);
        parcel.writeString(courseStatus);
    }

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
