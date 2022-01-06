package com.skinfotech.ekinch.models.requests;

import com.google.gson.annotations.SerializedName;

public class CertificateBody {
    @SerializedName("userId")
    private String userId ;
//    @SerializedName("courseid")
//    private int courseid ;

    public CertificateBody(int userId) {
        this.userId = userId+"";
    }
}
