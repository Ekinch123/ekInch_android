package com.skinfotech.ekinch.views.fragments;

import static android.app.Activity.RESULT_OK;
import static com.skinfotech.ekinch.constants.Constants.USER_ID;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.Utility;
import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.models.requests.CommonRequest;
import com.skinfotech.ekinch.models.responses.CategoryResponse;
import com.skinfotech.ekinch.models.responses.ProfileResponse;
import com.skinfotech.ekinch.retrofit.RetrofitApi;
import com.skinfotech.ekinch.utils.Constant;
import com.skinfotech.ekinch.utils.SpinAdapter;
import com.skinfotech.ekinch.utils.filepicker.Util;
import com.skinfotech.ekinch.utils.filepicker.activity.VideoPickActivity;
import com.skinfotech.ekinch.utils.filepicker.filter.entity.VideoFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UploadFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "UploadFragment";
    private FirebaseAnalytics mFirebaseAnalytics;

    Spinner mySpinner;
    private SpinAdapter adapter;
    private ArrayList<CategoryResponse.CategoryItem> categoryList = new ArrayList<>();
    private CategoryResponse.CategoryItem selectedItem;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());

        view.findViewById(R.id.notificationImageView).setOnClickListener(this);
        view.findViewById(R.id.homeImageView).setOnClickListener(this);
        view.findViewById(R.id.homeBottomTextView).setOnClickListener(this);
        view.findViewById(R.id.profileImageView).setOnClickListener(this);
        view.findViewById(R.id.libraryImageView).setOnClickListener(this);
        view.findViewById(R.id.libraryTextView).setOnClickListener(this);
        view.findViewById(R.id.shareImageView).setOnClickListener(this);
        view.findViewById(R.id.uploadTaskVideoBT).setOnClickListener(this);
        mySpinner = view.findViewById(R.id.categorySP);

        CategoryResponse.CategoryItem item = new CategoryResponse.CategoryItem();
        item.setCategoryTitle("कोर्स चुने");
        item.setCategoryId("0");

        categoryList.add(item);
        adapter = new SpinAdapter(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categoryList);

        mySpinner.setAdapter(adapter); // Set the custom adapter to the spinner

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                if (position > 0)
                    selectedItem = adapter.getItem(position);
                // Here you can do the action you want to...
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        fetchCategories();
        fetchProfile();
    }

    private void fetchCategories() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<CategoryResponse> call = RetrofitApi.getAppServicesObject().fetchCategories();
                    final Response<CategoryResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<CategoryResponse> response) {
                if (response.isSuccessful()) {
                    CategoryResponse categoryResponse = response.body();
                    if (categoryResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(categoryResponse.getErrorCode())) {
                            categoryList.addAll(categoryResponse.getCategoryList());

                            if (!Utility.isEmpty(categoryList)) {
                                adapter = new SpinAdapter(getContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        categoryList);
                                mySpinner.setAdapter(adapter); // Set the custom adapter to the spinner

                            }
                        } else {
                            showToast(categoryResponse.getErrorMessage());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    void fetchProfile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<ProfileResponse> call = RetrofitApi.getAppServicesObject().fetchProfile(new CommonRequest(getStringDataFromSharedPref(USER_ID)));
                    final Response<ProfileResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse profileResponse = response.body();
                    if (profileResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(profileResponse.getErrorCode())) {
                            onProfileUpdateResponse(profileResponse);
                        } else {
                            showToast(profileResponse.getErrorMessage());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notificationImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_markSheetFragment_to_notificationsFragment);
                break;
            case R.id.searchEditText:
                Navigation.findNavController(mContentView).navigate(R.id.action_markSheetFragment_to_searchFragment);
                break;
            case R.id.homeBottomTextView:
            case R.id.homeImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_markSheetFragment_to_homeFragment);
                break;
            case R.id.profileImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_markSheetFragment_to_profileFragment);
                break;
            case R.id.shareImageView:
                shareApplication();
                break;
            case R.id.libraryImageView:
            case R.id.libraryTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_markSheetFragment_to_libraryFragment);
                break;
            case R.id.uploadTaskVideoBT:
                Intent intent2 = new Intent(getActivity(), VideoPickActivity.class);
                intent2.putExtra("IsNeedCamera", false);
                intent2.putExtra(com.skinfotech.ekinch.utils.Constant.MAX_NUMBER, 1);
                intent2.putExtra("isNeedFolderList", true);
                startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case Constant.REQUEST_CODE_PICK_VIDEO:
                    ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                    VideoFile file = list.get(0);

                    String muid = UUID.randomUUID().toString() + "." + new Date().getTime();
                    String fileExt = Util.getExtension(file.getPath());
                    String fileName = muid;//file.getName();
                    if (!TextUtils.isEmpty(fileExt)) {
                        fileName = fileName + "." + fileExt;
                        //fileName = file.getName() + "." + fileExt;
                    }
                    if (selectedItem.getCategoryId().equalsIgnoreCase("0"))
                        Toast.makeText(getActivity(), "Please select Course", Toast.LENGTH_LONG).show();
                    else
                        uploadFile(new File(file.getPath()));

                    break;
            }
        }
    }


    private void uploadFile(File sProfilePhotoFile) {
        if (sProfilePhotoFile == null) {
            showToast("Something went wrong");
            return;
        }
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), sProfilePhotoFile);
                    MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", sProfilePhotoFile.getName(), requestBody);
                    RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), getStringDataFromSharedPref(USER_ID));
                    RequestBody userIdBodyCourseId = RequestBody.create(MediaType.parse("text/plain"), selectedItem.getCategoryId());
                    Call<ResponseBody> call = RetrofitApi.getAppServicesObject().uploadVideo(fileToUpload, userIdBody, userIdBodyCourseId);
                    final Response<ResponseBody> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                }
                stopProgress();
            }
        }).start();
    }

    private void sendEvent() {
        Bundle params = new Bundle();
        String userId = "";
        try {
            userId = getStringDataFromSharedPref(USER_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.putString("user_id", userId);
        params.putString("course_id", selectedItem.getCategoryId());
        params.putString("course_title", selectedItem.getCategoryTitle());
        mFirebaseAnalytics.logEvent("Practical_work_upload", params);
    }

}