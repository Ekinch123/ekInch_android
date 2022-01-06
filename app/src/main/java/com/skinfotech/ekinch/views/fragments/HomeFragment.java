package com.skinfotech.ekinch.views.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.Utility;
import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.models.responses.CategoryResponse;
import com.skinfotech.ekinch.models.responses.IntroVideo;
import com.skinfotech.ekinch.models.responses.ProfileResponse;
import com.skinfotech.ekinch.retrofit.RetrofitApi;
import com.skinfotech.ekinch.views.interfaces.IOnBackPressedListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class HomeFragment extends BaseFragment implements View.OnClickListener, IOnBackPressedListener {

    private boolean mIsDoubleBackPress = false;
    private CategoryAdapter mCategoryAdapter;
    private TextView userNameToolbarTextView;
    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_home_screen, container, false);
        mContentView.findViewById(R.id.profileImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.notificationImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.liveVideoIV).setOnClickListener(this);
        mContentView.findViewById(R.id.infoVideoIV).setOnClickListener(this);
        mContentView.findViewById(R.id.markSheetImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.markSheetTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.libraryImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.libraryTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.searchEditText).setOnClickListener(this);
        mContentView.findViewById(R.id.shareImageView).setOnClickListener(this);
        RecyclerView categoryRecyclerView = mContentView.findViewById(R.id.categoryRecyclerView);
        requestPermission();
        mCategoryAdapter = new CategoryAdapter();
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        categoryRecyclerView.setAdapter(mCategoryAdapter);
        userNameToolbarTextView = mContentView.findViewById(R.id.userNameTextView);
        fetchCategories();
        fetchProfile();
        return mContentView;
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
                            List<CategoryResponse.CategoryItem> categoryList = categoryResponse.getCategoryList();
                            if (!Utility.isEmpty(categoryList)) {
                                mCategoryAdapter.setCategoryList(categoryList);
                                mCategoryAdapter.notifyDataSetChanged();
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

    @Override
    void onProfileUpdateResponse(ProfileResponse response) {
        userNameToolbarTextView.setText(response.getUserName());
    }

    @Override
    public void onBackPressedToExit() {
        if (mIsDoubleBackPress) {
            mActivity.finish();
        }
        Snackbar.make(mContentView, getString(R.string.msg_back_press), Snackbar.LENGTH_SHORT).show();
        mIsDoubleBackPress = true;
        new Handler().postDelayed(() -> mIsDoubleBackPress = false, Constants.BACK_PRESS_INTERVAL);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notificationImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_homeFragment_to_notificationsFragment);
                break;
            case R.id.profileImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_homeFragment_to_profileFragment);
                break;
            case R.id.searchEditText:
                Navigation.findNavController(mContentView).navigate(R.id.action_homeFragment_to_searchFragment);
                break;
            case R.id.shareImageView:
                shareApplication();
                break;
            case R.id.markSheetImageView:
            case R.id.markSheetTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_homeFragment_to_markSheetFragment);
                break;
            case R.id.libraryImageView:
            case R.id.libraryTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_homeFragment_to_libraryFragment);
                break;
            case R.id.liveVideoIV:
                Navigation.findNavController(mContentView).navigate(R.id.action_homeFragment_to_liveVideoFragment);
                break;
            case R.id.infoVideoIV:

                fetchIntroVideo();
                break;
            default:
                break;
        }
    }

    private void fetchIntroVideo() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<IntroVideo> call = RetrofitApi.getAppServicesObject().getIntoVideo();
                    final Response<IntroVideo> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<IntroVideo> response) {
                if (response.isSuccessful()) {
                    IntroVideo questionResponse = response.body();
                    if (questionResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(questionResponse.getErrorCode())) {

                            String mVideoUrl = questionResponse.getVideo();

                            if (Utility.isEmpty(mVideoUrl)) {
                                return;
                            }
                            NavDirections directions = HomeFragmentDirections.actionHomeFragmentToVideoViewFragment(mVideoUrl, "","","");
                            Navigation.findNavController(mContentView).navigate(directions);
                        } else {
                            showToast(questionResponse.getErrorMessage());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        private List<CategoryResponse.CategoryItem> categoryList = new ArrayList<>();

        public void setCategoryList(List<CategoryResponse.CategoryItem> categoryList) {
            this.categoryList = categoryList;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.category_item, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            CategoryResponse.CategoryItem item = categoryList.get(position);
            if (!Utility.isEmpty(item.getCategoryImage())) {
                Picasso.get().load(item.getCategoryImage()).placeholder(R.drawable.logo).into(holder.categoryImageView);
            }
            holder.categoryTextView.setText(item.getCategoryTitle());
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        private class CategoryViewHolder extends RecyclerView.ViewHolder {

            private ImageView categoryImageView;
            private TextView categoryTextView;

            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                LinearLayout categoryContainer = itemView.findViewById(R.id.categoryContainer);
                categoryImageView = itemView.findViewById(R.id.categoryImageView);
                categoryTextView = itemView.findViewById(R.id.categoryTextView);
                categoryContainer.setOnClickListener(view -> {
                    CategoryResponse.CategoryItem selectedCategory = categoryList.get(getAdapterPosition());
                    NavDirections directions = HomeFragmentDirections.actionHomeFragmentToTopicFragment(selectedCategory.getCategoryTitle(), selectedCategory.getCategoryId());
                    Navigation.findNavController(mContentView).navigate(directions);
                });
            }
        }
    }
}
