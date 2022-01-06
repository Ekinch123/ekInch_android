package com.skinfotech.ekinch.views.fragments;

import static com.skinfotech.ekinch.constants.Constants.USER_ID;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.skinfotech.ekinch.BuildConfig;
import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.constants.Constants;

import java.util.Objects;

public class SplashFragment extends BaseFragment {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_splash, container, false);
        TextView appVersion = mContentView.findViewById(R.id.appVersion);
        String appVersionStr = "v".concat(BuildConfig.VERSION_NAME);
        appVersion.setText(appVersionStr);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());

        sendEvent();
        new Handler().postDelayed(() -> {
            if (getStringDataFromSharedPref(Constants.USER_LOGIN_DONE).equalsIgnoreCase(Constants.YES)) {
                Navigation.findNavController(mContentView).navigate(R.id.action_splashFragment_to_homeFragment);
            } else {
                Navigation.findNavController(mContentView).navigate(R.id.action_splashFragment_to_loginFragment2);
            }
        }, Constants.SPLASH_TIME_INTERVAL);
        return mContentView;
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
        mFirebaseAnalytics.logEvent("app_Launch", params);
    }
}
