package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QuestionResponse extends CommonResponse {

    @SerializedName("questionImage")
    private String questionImage = "";
    @SerializedName("questionVideo")
    private String questionVideo = "";

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    @SerializedName("video_id")
    private String video_id = "";
    @SerializedName("questionList")
    private List<QuestionItem> mQuestionList = new ArrayList<>();

    public String getQuestionImage() {
        return questionImage;
    }

    public String getQuestionVideo() {
        return questionVideo;
    }

    public List<QuestionItem> getQuestionList() {
        return mQuestionList;
    }

    public static class QuestionItem {

        @SerializedName("questionId")
        private String questionId = "";
        @SerializedName("questionType")
        private String questionType = "";
        @SerializedName("questionAnswer")
        private String questionAnswer = "";
        @SerializedName("question")
        private String question = "";
        @SerializedName("isSubmit")
        private boolean isSubmit;
        transient private File mRecordedFile;

        public File getRecordedFile() {
            return mRecordedFile;
        }

        public void setRecordedFile(File recordedFile) {
            mRecordedFile = recordedFile;
        }

        public String getQuestionId() {
            return questionId;
        }

        public String getQuestionType() {
            return questionType;
        }

        public String getQuestion() {
            return question;
        }

        public String getQuestionAnswer() {
            return questionAnswer;
        }

        public boolean isSubmit() {
            return isSubmit;
        }
    }
}
