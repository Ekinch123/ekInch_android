package com.skinfotech.ekinch.views.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.skinfotech.ekinch.BuildConfig;
import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.models.requests.CommonRequest;
import com.skinfotech.ekinch.models.responses.ProfileResponse;
import com.skinfotech.ekinch.retrofit.RetrofitApi;
import com.skinfotech.ekinch.views.MainActivity;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.skinfotech.ekinch.constants.Constants.SHARED_PREF_NAME;
import static com.skinfotech.ekinch.constants.Constants.USER_ID;

public class BaseFragment extends Fragment {

    protected View mContentView;
    protected MainActivity mActivity;
    private static ProgressDialog sProgressDialog;
    private String selectedItemStr = "";
    private static final String TAG = "BaseFragment";

    public void showProgress() {
        mActivity.runOnUiThread(() -> {
            sProgressDialog = new ProgressDialog(mActivity);
            sProgressDialog.setMessage(getString(R.string.please_wait));
            sProgressDialog.setCancelable(false);
            sProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            sProgressDialog.show();
        });
    }

    void stopProgress() {
        mActivity.runOnUiThread(() -> {
            if (sProgressDialog != null && sProgressDialog.isShowing()) {
                sProgressDialog.dismiss();
            }
        });
    }

    void speakAudio(String text) {
        updateOnUiThread(() -> mActivity.speakAudio(text));
    }

    void requestPermission() {
        mActivity.requestPermission();
    }


    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    void updateOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public void storeStringDataInSharedPref(String keyName, String value) {
        if (getActivity() != null) {
            SharedPreferences.Editor editor = mActivity.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE).edit();
            editor.putString(keyName, value);
            editor.apply();
        }
    }



    String getStringDataFromSharedPref(String keyName) {
        SharedPreferences prefs = mActivity.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return prefs.getString(keyName, "");
    }

    void showToast(String msg) {
        mActivity.showToast(msg);
    }

    private void onAlertDialogItemClicked(String selectedItemStr, int id, int position) {
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = mActivity.getCurrentFocus();
        if (view == null) {
            view = new View(mActivity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    void onProfileUpdateResponse(ProfileResponse response) {
    }

    void shareApplication() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "Let me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            Log.d(TAG, "shareApplication: " + e.toString());
        }
    }
}
