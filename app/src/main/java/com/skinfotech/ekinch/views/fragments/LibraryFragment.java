package com.skinfotech.ekinch.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.Utility;
import com.skinfotech.ekinch.adapter.CustomExpandableListAdapter;
import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.models.requests.CoursesRequest;
import com.skinfotech.ekinch.models.requests.LikeUnlikeRequest;
import com.skinfotech.ekinch.models.requests.QuestionRequest;
import com.skinfotech.ekinch.models.responses.Category;
import com.skinfotech.ekinch.models.responses.CommonResponse;
import com.skinfotech.ekinch.models.responses.CoursesResponse;
import com.skinfotech.ekinch.models.responses.LikeDislike;
import com.skinfotech.ekinch.models.responses.NewCoursesResponce;
import com.skinfotech.ekinch.models.responses.ProfileResponse;
import com.skinfotech.ekinch.models.responses.QuestionResponse;
import com.skinfotech.ekinch.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.skinfotech.ekinch.constants.Constants.USER_ID;

public class LibraryFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "LibraryFragment";
    private ArrayList<Category> coursesList;
    private ExpandableListView categoryRecyclerView;
    private CustomExpandableListAdapter expandableListAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_liberary, container, false);
        mContentView.findViewById(R.id.profileImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.notificationImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.markSheetImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.markSheetTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.homeImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.homeBottomTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.searchEditText).setVisibility(View.VISIBLE);
        mContentView.findViewById(R.id.searchEditText).setOnClickListener(this);
        mContentView.findViewById(R.id.shareImageView).setOnClickListener(this);

        mContentView.findViewById(R.id.liveVideoIV).setVisibility(View.GONE);
        mContentView.findViewById(R.id.infoVideoIV).setVisibility(View.GONE);

        categoryRecyclerView = mContentView.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true; // This way the expander cannot be collapsed
            }
        });
//        mLibraryAdapter = new LibraryAdapter();
//        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));


//        categoryRecyclerView.setAdapter(mLibraryAdapter);
        fetchProfile();
        fetchCoursesServerCall();
        return mContentView;
    }

    private void fetchCoursesServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<NewCoursesResponce> call = RetrofitApi.getAppServicesObject().fetchCoursesServerCall(new CoursesRequest(""));
                    final Response<NewCoursesResponce> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    showToast(e.getMessage());
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<NewCoursesResponce> response) {
                if (response.isSuccessful()) {
                    NewCoursesResponce coursesResponse = response.body();
                    if (coursesResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(coursesResponse.getErrorCode())) {
                            coursesList = coursesResponse.getCategory();
                            if (!Utility.isEmpty(coursesList)) {

                                setAdapter(coursesList);

                            }
                        } else {
                            showToast(coursesResponse.getErrorMessage());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private void setAdapter(ArrayList<Category> coursesList) {
        expandableListAdapter = new CustomExpandableListAdapter(getContext(), coursesList, getActivity(), LibraryFragment.this);
        categoryRecyclerView.setAdapter(expandableListAdapter);
        for (int i = 0; i < coursesList.size(); i++) {
            categoryRecyclerView.expandGroup(i);
        }

    }

    @Override
    void onProfileUpdateResponse(ProfileResponse response) {
        TextView userNameToolbarTextView = mContentView.findViewById(R.id.userNameTextView);
        userNameToolbarTextView.setText(response.getUserName());
    }

    public void fetchQuestionsServerCall(String coursesId, String coursesTitle) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<QuestionResponse> call = RetrofitApi.getAppServicesObject().fetchQuestionsServerCall(new QuestionRequest(getStringDataFromSharedPref(USER_ID), coursesId));
                    final Response<QuestionResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<QuestionResponse> response) {
                if (response.isSuccessful()) {
                    QuestionResponse questionResponse = response.body();
                    if (questionResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(questionResponse.getErrorCode())) {
                            List<QuestionResponse.QuestionItem> questionItemList = questionResponse.getQuestionList();
//                            if (!Utility.isEmpty(questionItemList)) {
//                                mQuestionsAdapter.setQuestionItemList(questionItemList);
//                                mQuestionsAdapter.notifyDataSetChanged();
//                                if (!Utility.isEmpty(questionResponse.getQuestionImage())) {
//                                    Picasso.get().load(questionResponse.getQuestionImage()).placeholder(R.drawable.video_bg).into(questionVideoImageView);
//                                }
                            String mVideoUrl = questionResponse.getQuestionVideo();

                            if (Utility.isEmpty(mVideoUrl)) {
                                return;
                            }
                            NavDirections directions = LibraryFragmentDirections.actionLibraryFragmentToVideoFragment(mVideoUrl, coursesId, questionResponse.getVideo_id(), coursesTitle);
                            Navigation.findNavController(mContentView).navigate(directions);
//                            }
                        } else {
                            showToast(questionResponse.getErrorMessage());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    public void likeApi(String coursesId, int position, int groupPosition, int childPosition) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Call<LikeDislike> call = RetrofitApi.getAppServicesObject().LikeUnlike(new LikeUnlikeRequest(getStringDataFromSharedPref(USER_ID), coursesId));
                    final Response<LikeDislike> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<LikeDislike> response) {
                if (response.isSuccessful()) {
                    LikeDislike questionResponse = response.body();

                    if (coursesList != null) {
                        coursesList.get(groupPosition).getSubCategories().get(childPosition).getSubCategoryData().get(position).setTotalLikes(questionResponse.getTotalCount());
                        setAdapter(coursesList);
                    }
                    if (questionResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(questionResponse.getErrorCode())) {

                        } else {
                            showToast(questionResponse.getErrorMessage());
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
                Navigation.findNavController(mContentView).navigate(R.id.action_libraryFragment_to_notificationsFragment);
                break;
            case R.id.profileImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_libraryFragment_to_profileFragment);
                break;
            case R.id.searchEditText:
                Navigation.findNavController(mContentView).navigate(R.id.action_libraryFragment_to_searchFragment);
                break;
            case R.id.shareImageView:
                shareApplication();
                break;
            case R.id.homeBottomTextView:
            case R.id.homeImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_libraryFragment_to_homeFragment);
                break;
            case R.id.markSheetImageView:
            case R.id.markSheetTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_libraryFragment_to_markSheetFragment);
                break;
            default:
                break;
        }


    }

}




