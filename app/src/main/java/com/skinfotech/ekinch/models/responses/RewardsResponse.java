package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RewardsResponse extends CommonResponse {

    @SerializedName("rewardsList")
    private List<RewardItem> mRewardList = new ArrayList<>();

    public List<RewardItem> getRewardList() {
        return mRewardList;
    }

    public static class RewardItem {

        @SerializedName("rewardImage")
        private String rewardImage = "";
        @SerializedName("rewardTitle")
        private String rewardTitle = "";
        @SerializedName("rewardTime")
        private String rewardTime = "";

        public String getRewardImage() {
            return rewardImage;
        }

        public String getRewardTitle() {
            return rewardTitle;
        }

        public String getRewardTime() {
            return rewardTime;
        }
    }
}
