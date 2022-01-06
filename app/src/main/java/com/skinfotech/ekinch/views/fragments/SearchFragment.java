package com.skinfotech.ekinch.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.models.requests.LikeUnlikeRequest;
import com.skinfotech.ekinch.models.requests.QuestionRequest;
import com.skinfotech.ekinch.models.requests.SearchRequest;
import com.skinfotech.ekinch.models.responses.CommonResponse;
import com.skinfotech.ekinch.models.responses.CoursesResponse;
import com.skinfotech.ekinch.models.responses.LikeDislike;
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

public class SearchFragment extends BaseFragment {

    private static final String TAG = "SearchFragment";
//    private SearchAdapter mSearchAdapter;
    private EditText searchEditText;
    private View noItemFoundContainer;
    private RecyclerView mSearchRecyclerView;
//    List<CoursesResponse.CoursesItem> coursesList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_search, container, false);
        mSearchRecyclerView = mContentView.findViewById(R.id.categoryRecyclerView);
//        mSearchAdapter = new SearchAdapter();
        noItemFoundContainer = mContentView.findViewById(R.id.noItemFoundContainer);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
//        mSearchRecyclerView.setAdapter(mSearchAdapter);
        noItemFoundContainer.setVisibility(View.GONE);
        mSearchRecyclerView.setVisibility(View.GONE);
        searchEditText = mContentView.findViewById(R.id.searchEditText);
//        mContentView.findViewById(R.id.searchImageView).setOnClickListener(view -> startSearching());
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                startSearching();
                return true;
            }
            return false;
        });
        return mContentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchEditText != null && !Utility.isEmpty(searchEditText.getText().toString())) {
            mContentView.findViewById(R.id.searchImageView).callOnClick();
        }
    }
    private void fetchQuestionsServerCall(String coursesId, String coursesTitle) {
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
                                NavDirections directions = SearchFragmentDirections.actionSearchFragmentToQuestionsFragment(mVideoUrl,coursesId,questionResponse.getVideo_id(),coursesTitle);
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

//    private void likeApi(String coursesId, int adapterPosition) {
//        showProgress();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    Call<LikeDislike> call = RetrofitApi.getAppServicesObject().LikeUnlike(new LikeUnlikeRequest(getStringDataFromSharedPref(USER_ID), coursesId));
//                    final Response<LikeDislike> response = call.execute();
//                    updateOnUiThread(() -> handleResponse(response));
//                } catch (Exception e) {
//                    stopProgress();
//                    Log.e(TAG, e.getMessage(), e);
//                }
//            }
//
//            private void handleResponse(Response<LikeDislike> response) {
//                if (response.isSuccessful()) {
//                    LikeDislike questionResponse = response.body();
//                    if (coursesList != null) {
//                        coursesList.get(adapterPosition).setTotalLikes(questionResponse.getTotalCount());
//                        mSearchAdapter.notifyItemChanged(adapterPosition);
//                    }
////                    if (questionResponse != null) {
////                        if (Constants.SUCCESS.equalsIgnoreCase(questionResponse.getErrorCode())) {
////
////                        } else {
////                            showToast(questionResponse.getErrorMessage());
////                        }
////                    }
//                }
//                stopProgress();
//            }
//        }).start();
//    }


//    private void startSearching() {
//        String text = searchEditText.getText().toString();
//        if (Utility.isEmpty(text)) {
//            return;
//        }
//        hideKeyboard();
//        showProgress();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String userId = getStringDataFromSharedPref(Constants.USER_ID);
//                    Call<CoursesResponse> call = RetrofitApi.getAppServicesObject().searchServerCall(new SearchRequest(userId, text));
//                    final Response<CoursesResponse> response = call.execute();
//                    updateOnUiThread(() -> handleResponse(response));
//                } catch (Exception e) {
//                    stopProgress();
//                    Log.e(TAG, e.getMessage(), e);
//                }
//            }
//
//            private void handleResponse(Response<CoursesResponse> response) {
//                if (response.isSuccessful()) {
//                    CoursesResponse coursesResponse = response.body();
//                    if (coursesResponse != null) {
//                        if (Constants.SUCCESS.equalsIgnoreCase(coursesResponse.getErrorCode())) {
//                             coursesList = coursesResponse.getCoursesList();
//                            if (!Utility.isEmpty(coursesList)) {
//                                mSearchRecyclerView.setVisibility(View.VISIBLE);
//                                noItemFoundContainer.setVisibility(View.GONE);
//                                Collections.sort(coursesList, new Comparator<CoursesResponse.CoursesItem>() {
//                                    @Override
//                                    public int compare(CoursesResponse.CoursesItem lhs, CoursesResponse.CoursesItem rhs) {
//                                        return lhs.getCoursesId().compareTo(rhs.getCoursesId());
//                                    }
//                                });
//                                mSearchAdapter.setCoursesList(coursesList);
//                                mSearchAdapter.notifyDataSetChanged();
//                            }
//                        } else {
//                            mSearchRecyclerView.setVisibility(View.GONE);
//                            noItemFoundContainer.setVisibility(View.VISIBLE);
//                            showToast(coursesResponse.getErrorMessage());
//                        }
//                    }
//                }
//                stopProgress();
//            }
//        }).start();
//    }

//    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
//
//        private List<CoursesResponse.CoursesItem> coursesList = new ArrayList<>();
//
//        public void setCoursesList(List<CoursesResponse.CoursesItem> coursesList) {
//            this.coursesList = coursesList;
//        }
//
//        @NonNull
//        @Override
//        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//            View view = layoutInflater.inflate(R.layout.topic_item, parent, false);
//            return new SearchViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
//            CoursesResponse.CoursesItem item = coursesList.get(position);
//            if (!Utility.isEmpty(item.getCoursesImage())) {
//                Picasso.get().load(item.getCoursesImage().replace("video","course thumbnail")).placeholder(R.drawable.video_bg).into(holder.topicImageView);
//            }
//            holder.topicTextView.setText(item.getCoursesTitle());
//            holder.likeTV.setText(item.getTotalLikes() + "");
//            holder.topicImageView.setClipToOutline(true);
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return coursesList.size();
//        }
//
//        private class SearchViewHolder extends RecyclerView.ViewHolder {
//
//            private ImageView topicImageView;
//            private TextView topicTextView, likeTV;
//            private LinearLayout likeLL;
//
//            public SearchViewHolder(@NonNull View itemView) {
//                super(itemView);
//                likeLL = itemView.findViewById(R.id.likeLL);
//                likeTV = itemView.findViewById(R.id.likeTV);
//
//                topicImageView = itemView.findViewById(R.id.imageView2);
//                topicTextView = itemView.findViewById(R.id.textView);
//                View topicContainer = itemView.findViewById(R.id.topicContainer);
//                topicContainer.setOnClickListener(v -> {
//                    CoursesResponse.CoursesItem selectedCourse = coursesList.get(getAdapterPosition());
//
//                    fetchQuestionsServerCall(selectedCourse.getCoursesId(), selectedCourse.getCoursesTitle());
//
////                    NavDirections directions = SearchFragmentDirections.actionSearchFragmentToQuestionsFragment(selectedCourse.getCoursesId(), selectedCourse.getCoursesTitle());
////                    Navigation.findNavController(mContentView).navigate(directions);
//                });
//
//                likeLL.setOnClickListener(view -> {
//                    CoursesResponse.CoursesItem selectedCourse = coursesList.get(getAdapterPosition());
//
//                    likeApi(selectedCourse.getCoursesId(),getAdapterPosition());
//                });
//            }
//        }
//    }
}
