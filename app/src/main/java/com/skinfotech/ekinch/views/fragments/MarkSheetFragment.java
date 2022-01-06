package com.skinfotech.ekinch.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.skinfotech.ekinch.models.dto.MarkSheetDto;
import com.skinfotech.ekinch.models.requests.CommonRequest;
import com.skinfotech.ekinch.models.requests.getMarksheet;
import com.skinfotech.ekinch.models.responses.MarkSheetResponse;
import com.skinfotech.ekinch.models.responses.ProfileResponse;
import com.skinfotech.ekinch.retrofit.RetrofitApi;

import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.skinfotech.ekinch.constants.Constants.USER_ID;

public class MarkSheetFragment extends BaseFragment implements View.OnClickListener {

    private MarkSheetAdapter mMarkSheetAdapter;
    private static final String TAG = "MarkSheetFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_mark_sheet, container, false);
        mContentView.findViewById(R.id.notificationImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.homeImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.homeBottomTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.profileImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.libraryImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.libraryTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.shareImageView).setOnClickListener(this);
        mContentView.findViewById(R.id.uploadWorkVideoTV).setOnClickListener(this);
        mMarkSheetAdapter = new MarkSheetAdapter();
        RecyclerView markSheetRecyclerView = mContentView.findViewById(R.id.markSheetRecyclerView);
        markSheetRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        markSheetRecyclerView.setAdapter(mMarkSheetAdapter);
        fetchProfile();
        fetchProfilee();
        return mContentView;
    }

    private void fetchMarkSheets() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    Call<MarkSheetResponse> call = RetrofitApi.getAppServicesObject().fetchMarkSheets(new CommonRequest(getStringDataFromSharedPref(USER_ID)));
                    Call<MarkSheetResponse> call = RetrofitApi.getAppServicesObject().fetchMarkSheets(new getMarksheet(getStringDataFromSharedPref(USER_ID)));
                    final Response<MarkSheetResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<MarkSheetResponse> response) {
                if (response.isSuccessful()) {
                    MarkSheetResponse markSheetResponse = response.body();
                    if (markSheetResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(markSheetResponse.getErrorCode())) {
                            List<MarkSheetResponse.seenVideos> markSheetList = markSheetResponse.getSeen_videos();
                            if (!Utility.isEmpty(markSheetList)) {
                                mMarkSheetAdapter.setMarkSheetList(markSheetList);
                                mMarkSheetAdapter.notifyDataSetChanged();
                            }
                        } else {
                            showToast(markSheetResponse.getErrorMessage());
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private void fetchProfilee() {
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
                        List<MarkSheetResponse.seenVideos> markSheetList = profileResponse.getSeen_videos();
                        if (!Utility.isEmpty(markSheetList)) {
                            mMarkSheetAdapter.setMarkSheetList(markSheetList);
                            mMarkSheetAdapter.notifyDataSetChanged();
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
            case R.id.uploadWorkVideoTV:
                Navigation.findNavController(mContentView).navigate(R.id.action_markSheetFragment_to_uploadFragment);

                break;
            default:
                break;
        }
    }

    private String secToHourConversion(int seconds) {
        int sec = seconds % 60;
        int hour = seconds / 60;
        int min = hour % 60;
        hour = hour / 60;

        if (hour > 0)
            return hour + "." + min + "." + sec + " hour";
        else if (min > 0)
            return min + "." + sec + " mins";
        else
            return sec + " sec";


    }

    private class MarkSheetAdapter extends RecyclerView.Adapter<MarkSheetAdapter.MarkSheetViewHolder> {

        private List<MarkSheetResponse.seenVideos> mMarkSheetList = new ArrayList<>();

        public void setMarkSheetList(List<MarkSheetResponse.seenVideos> markSheetList) {
            mMarkSheetList = markSheetList;
        }

        @NonNull
        @Override
        public MarkSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.mark_sheet_item, parent, false);
            return new MarkSheetViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MarkSheetViewHolder holder, int position) {
            MarkSheetResponse.seenVideos item = mMarkSheetList.get(position);
            holder.markSheetTextView.setText(position + 1 + ". " + item.getVideoName());
            if (item.getViewDuration() != null && !item.getViewDuration().isEmpty()) {
                holder.generateCertificateTextView.setText(secToHourConversion(Integer.parseInt(item.getViewDuration())));
            } else
                holder.generateCertificateTextView.setText("0 sec");
        }

        @Override
        public int getItemCount() {
            return mMarkSheetList.size();
        }

        private class MarkSheetViewHolder extends RecyclerView.ViewHolder {

            private TextView markSheetTextView, generateCertificateTextView;

            MarkSheetViewHolder(@NonNull View itemView) {
                super(itemView);
                markSheetTextView = itemView.findViewById(R.id.textView2);
                generateCertificateTextView = itemView.findViewById(R.id.generateCertificate);
//                generateCertificateTextView.setOnClickListener(view -> {
//                    MarkSheetResponse.MarkSheetItem selectedMarkSheet = mMarkSheetList.get(getAdapterPosition());
//                    MarkSheetDto markSheetDto = new MarkSheetDto(selectedMarkSheet.getMarkSheetTitle(),
//                                                                 selectedMarkSheet.getMarkSheetId(),
//                                                                 selectedMarkSheet.getCourseTotalMarks(),
//                                                                 selectedMarkSheet.getCourseMarksObtained(),
//                                                                 selectedMarkSheet.getCourseStatus());
//                    NavDirections directions = MarkSheetFragmentDirections.actionMarkSheetFragmentToGenerateMarkSheetFragment(markSheetDto);
//                    Navigation.findNavController(mContentView).navigate(directions);
//                });
            }
        }
    }
}
