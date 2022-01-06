package com.skinfotech.ekinch.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.Utility;
import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.models.requests.CommonRequest;
import com.skinfotech.ekinch.models.responses.ProfileResponse;
import com.skinfotech.ekinch.models.responses.RewardsResponse;
import com.skinfotech.ekinch.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.skinfotech.ekinch.constants.Constants.USER_ID;

public class RewardsFragment extends BaseFragment implements View.OnClickListener {

    private RewardsAdapter mRewardsAdapter;
    private static final String TAG = "RewardsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_notifications, container, false);
        RecyclerView notificationRecyclerView = mContentView.findViewById(R.id.notificationRecyclerView);
        mRewardsAdapter = new RewardsAdapter();
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        notificationRecyclerView.setAdapter(mRewardsAdapter);
        mContentView.findViewById(R.id.markSheetImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.markSheetTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.profileImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.notificationImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.homeImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.homeBottomTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.libraryImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.libraryTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.shareImageView).setOnClickListener(this);
        TextView titleTextView = mContentView.findViewById(R.id.notificationTitle);
        titleTextView.setText(getString(R.string.rewards));
        fetchProfile();
        fetchRewards();
        return mContentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.markSheetImageView:
            case R.id.markSheetTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_changePasswordFragment_to_markSheetFragment);
                break;
            case R.id.notificationImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_changePasswordFragment_to_notificationsFragment);
                break;
            case R.id.homeImageView:
            case R.id.homeBottomTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_changePasswordFragment_to_homeFragment);
                break;
            case R.id.profileImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_changePasswordFragment_to_profileFragment);
                break;
            case R.id.searchEditText:
                Navigation.findNavController(mContentView).navigate(R.id.action_changePasswordFragment_to_searchFragment);
                break;
            case R.id.shareImageView:
                shareApplication();
                break;
            case R.id.libraryImageView:
            case R.id.libraryTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_changePasswordFragment_to_libraryFragment);
                break;
            default:
                break;
        }
    }

    private void fetchRewards() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<RewardsResponse> call = RetrofitApi.getAppServicesObject().fetchRewards(new CommonRequest(getStringDataFromSharedPref(USER_ID)));
                    final Response<RewardsResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<RewardsResponse> response) {
                if (response.isSuccessful()) {
                    RewardsResponse rewardsResponse = response.body();
                    if (rewardsResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(rewardsResponse.getErrorCode())) {
                            List<RewardsResponse.RewardItem> notificationList = rewardsResponse.getRewardList();
                            if (!Utility.isEmpty(notificationList)) {
                                mRewardsAdapter.setRewardsList(notificationList);
                                mRewardsAdapter.notifyDataSetChanged();
                            }
                        } else {
                            showToast(rewardsResponse.getErrorMessage());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    @Override
    void onProfileUpdateResponse(ProfileResponse response) {
        TextView userNameToolbarTextView = mContentView.findViewById(R.id.userNameTextView);
        userNameToolbarTextView.setText(response.getUserName());
    }

    private static class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.RewardsViewHolder> {

        private List<RewardsResponse.RewardItem> rewardsList = new ArrayList<>();

        public void setRewardsList(List<RewardsResponse.RewardItem> rewardItems) {
            this.rewardsList = rewardItems;
        }

        @NonNull
        @Override
        public RewardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.rewards_item, parent, false);
            return new RewardsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RewardsViewHolder holder, int position) {
            RewardsResponse.RewardItem item = rewardsList.get(position);
            holder.title.setText(item.getRewardTitle());
            holder.timeStamp.setText(item.getRewardTime());
            if (!Utility.isEmpty(item.getRewardImage())) {
                Picasso.get().load(item.getRewardImage()).placeholder(R.drawable.logo).into(holder.imageView);
            }
        }

        @Override
        public int getItemCount() {
            return rewardsList.size();
        }

        private static class RewardsViewHolder extends RecyclerView.ViewHolder {

            private TextView title;
            private TextView timeStamp;
            private ImageView imageView;

            public RewardsViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.notificationTitle);
                timeStamp = itemView.findViewById(R.id.notificationTime);
                imageView = itemView.findViewById(R.id.notificationImageView);
            }
        }
    }
}