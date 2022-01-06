package com.skinfotech.ekinch.views.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import com.google.android.material.snackbar.Snackbar;
import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.views.interfaces.IOnBackPressedListener;

public class SignUpFragment extends BaseFragment implements IOnBackPressedListener, View.OnClickListener {

    private boolean mIsDoubleBackPress = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mContentView.findViewById(R.id.signUpTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.loginHereTextView).setOnClickListener(this);
        mContentView.findViewById(R.id.alreadyHaveAccountTextView).setOnClickListener(this);
        return mContentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mActivity.getSupportActionBar()) {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
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
            case R.id.alreadyHaveAccountTextView:
            case R.id.loginHereTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_signUpFragment_to_loginFragment);
                break;
            case R.id.signUpTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_signUpFragment_to_homeFragment);
                break;
            default:
                break;
        }
    }


}
