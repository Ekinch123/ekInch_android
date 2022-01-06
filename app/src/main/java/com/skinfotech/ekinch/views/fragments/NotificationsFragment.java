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
import com.skinfotech.ekinch.models.responses.NotificationsResponse;
import com.skinfotech.ekinch.models.responses.ProfileResponse;
import com.skinfotech.ekinch.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.skinfotech.ekinch.constants.Constants.USER_ID;

public class NotificationsFragment extends BaseFragment implements View.OnClickListener {

    private NotificationAdapter mNotificationAdapter;
    private static final String TAG = "NotificationsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_notifications, container, false);
        RecyclerView notificationRecyclerView = mContentView.findViewById(R.id.notificationRecyclerView);
        mNotificationAdapter = new NotificationAdapter();
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        notificationRecyclerView.setAdapter(mNotificationAdapter);
        mContentView.findViewById(R.id.profileImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.shareImageView).setOnClickListener(this);
        fetchProfile();
        fetchNotifications();
        return mContentView;
    }

    @Override
    void onProfileUpdateResponse(ProfileResponse response) {
        TextView userNameToolbarTextView = mContentView.findViewById(R.id.userNameTextView);
        userNameToolbarTextView.setText(response.getUserName());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profileImageView:
                Navigation.findNavController(mContentView).navigate(R.id.action_notificationsFragment_to_profileFragment);
                break;
            case R.id.searchEditText:
                Navigation.findNavController(mContentView).navigate(R.id.action_notificationsFragment_to_searchFragment);
                break;
            case R.id.shareImageView:
                shareApplication();
                break;
            default:
                break;
        }
    }

    private void fetchNotifications() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<NotificationsResponse> call = RetrofitApi.getAppServicesObject().fetchNotifications(new CommonRequest(getStringDataFromSharedPref(USER_ID)));
                    final Response<NotificationsResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<NotificationsResponse> response) {
                if (response.isSuccessful()) {
                    NotificationsResponse notificationsResponse = response.body();
                    if (notificationsResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(notificationsResponse.getErrorCode())) {
                            List<NotificationsResponse.NotificationItem> notificationList = notificationsResponse.getNotificationList();
                            if (!Utility.isEmpty(notificationList)) {
                                mNotificationAdapter.setNotificationList(notificationList);
                                mNotificationAdapter.notifyDataSetChanged();
                            }
                        } else {
                            showToast(notificationsResponse.getErrorMessage());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

        private List<NotificationsResponse.NotificationItem> notificationList = new ArrayList<>();

        public void setNotificationList(List<NotificationsResponse.NotificationItem> notificationList) {
            this.notificationList = notificationList;
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View notificationView = layoutInflater.inflate(R.layout.notification_item, parent, false);
            return new NotificationViewHolder(notificationView);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            NotificationsResponse.NotificationItem item = notificationList.get(position);
            holder.notificationTitle.setText(item.getNotificationTitle());
            holder.notificationDesc.setText(item.getNotificationDesc());
            holder.notificationTime.setText(item.getNotificationTime());
            if (!Utility.isEmpty(item.getNotificationImage())) {
                Picasso.get().load(item.getNotificationImage()).placeholder(R.drawable.logo).into(holder.notificationImageView);
            }
        }

        @Override
        public int getItemCount() {
            return notificationList.size();
        }

        private class NotificationViewHolder extends RecyclerView.ViewHolder {

            private TextView notificationTitle;
            private TextView notificationDesc;
            private TextView notificationTime;
            private ImageView notificationImageView;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                notificationTitle = itemView.findViewById(R.id.notificationTitle);
                notificationDesc = itemView.findViewById(R.id.notificationDesc);
                notificationTime = itemView.findViewById(R.id.notificationTime);
                notificationImageView = itemView.findViewById(R.id.notificationImageView);
            }
        }
    }
}
