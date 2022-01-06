package com.skinfotech.ekinch.retrofit;

import com.skinfotech.ekinch.models.requests.CertificateBody;
import com.skinfotech.ekinch.models.requests.CommonRequest;
import com.skinfotech.ekinch.models.requests.CoursesRequest;
import com.skinfotech.ekinch.models.requests.LikeUnlikeRequest;
import com.skinfotech.ekinch.models.requests.OtpRequest;
import com.skinfotech.ekinch.models.requests.ProfileRequest;
import com.skinfotech.ekinch.models.requests.QuestionRequest;
import com.skinfotech.ekinch.models.requests.SearchRequest;
import com.skinfotech.ekinch.models.requests.VerifyMobileRequest;
import com.skinfotech.ekinch.models.requests.VideoLength;
import com.skinfotech.ekinch.models.requests.getMarksheet;
import com.skinfotech.ekinch.models.responses.CategoryResponse;
import com.skinfotech.ekinch.models.responses.CertificateResponce;
import com.skinfotech.ekinch.models.responses.CommonResponse;
import com.skinfotech.ekinch.models.responses.CoursesResponse;
import com.skinfotech.ekinch.models.responses.IntroVideo;
import com.skinfotech.ekinch.models.responses.LikeDislike;
import com.skinfotech.ekinch.models.responses.LocationResponse;
import com.skinfotech.ekinch.models.responses.MarkSheetResponse;
import com.skinfotech.ekinch.models.responses.NewCoursesResponce;
import com.skinfotech.ekinch.models.responses.NotificationsResponse;
import com.skinfotech.ekinch.models.responses.ProfessionDatum;
import com.skinfotech.ekinch.models.responses.ProfileResponse;
import com.skinfotech.ekinch.models.responses.QuestionResponse;
import com.skinfotech.ekinch.models.responses.RewardsResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface AppServices {

    @POST("verify_mobile_number.php")
    Call<CommonResponse> verifyMobileNumber(@Body VerifyMobileRequest request);

    @POST("verify_otp.php")
    Call<CommonResponse> verifyOTP(@Body OtpRequest request);

    @POST("fetch_profile.php")
    Call<ProfileResponse> fetchProfile(@Body CommonRequest request);

    @POST("save_profile.php")
    Call<CommonResponse> saveProfile(@Body ProfileRequest request);

    @GET("fetch_profession.php")
    Call<List<ProfessionDatum>> getProfession();

    @POST("fetch_notifications.php")
    Call<NotificationsResponse> fetchNotifications(@Body CommonRequest request);

    @POST("fetch_rewards.php")
    Call<RewardsResponse> fetchRewards(@Body CommonRequest request);

    @POST("fetch_categories.php")
    Call<CategoryResponse> fetchCategories();

    @POST("fetch_mark_sheets.php")
    Call<MarkSheetResponse> fetchMarkSheets(@Body getMarksheet request);

    @POST("fetch_courses.php")
    Call<NewCoursesResponce> fetchCoursesServerCall(@Body CoursesRequest request);

    @POST("search_courses.php")
    Call<CoursesResponse> searchServerCall(@Body SearchRequest request);

    @POST("fetch_questions.php")
    Call<QuestionResponse> fetchQuestionsServerCall(@Body QuestionRequest request);

    @GET("fetch_intro_video.php")
    Call<IntroVideo> getIntoVideo();

    @POST("seen_video_len.php")
    Call<QuestionResponse> seenVideoLengthCall(@Body VideoLength request);

    @POST("get_certificate.php")
    Call<List<CertificateResponce>> getCertificate(@Query("userId") String request);

    @POST("like_unlike.php")
    Call<LikeDislike> LikeUnlike(@Body LikeUnlikeRequest request);

    @POST("fetch_locations.php")
    Call<LocationResponse> fetchLocationsServerCall();

    @Multipart
    @POST("save_answer.php")
    Call<QuestionResponse> uploadAudioFile(@Part MultipartBody.Part file, @Part("userId") RequestBody userId, @Part("courseId") RequestBody courseId
            , @Part("questionId") RequestBody questionId);

    @Multipart
    @POST("upload_profile_photo.php")
    Call<ProfileResponse> uploadProfilePhoto(@Part MultipartBody.Part file, @Part("userId") RequestBody userId);

//    :10
//    :12.4
//    :himmatcandidate-photo.php


    @Multipart
    @POST("candidate-photo.php")
    Call<JSONObject> uploadCandidatePhoto(@Part MultipartBody.Part file,
                                          @Part("user_id") RequestBody userId,
                                          @Part("course_id") RequestBody courseId,
                                          @Part("videoId") RequestBody videoId,
                                          @Part("progressTime") RequestBody progressTime,
                                          @Part("videoName") RequestBody videoName
    );

    @Multipart
    @POST("api/practical-work.php")
    Call<ResponseBody> uploadVideo(@Part MultipartBody.Part file, @Part("userId") RequestBody userId, @Part("course_id") RequestBody courseId);

}
