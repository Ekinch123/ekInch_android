package com.skinfotech.ekinch.models.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Video implements Serializable
{

@SerializedName("videoName")
@Expose
private String videoName;
@SerializedName("views")
@Expose
private String views="0";
private final static long serialVersionUID = 2582766071881932574L;

public String getVideoName() {
return videoName;
}

public void setVideoName(String videoName) {
this.videoName = videoName;
}

public int getViews() {
return Integer.parseInt(views);
}

public void setViews(String views) {
this.views = views;
}

}