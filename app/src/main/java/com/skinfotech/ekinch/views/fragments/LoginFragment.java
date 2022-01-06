package com.skinfotech.ekinch.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.skinfotech.ekinch.AppSignatureHelper;
import com.skinfotech.ekinch.OtpBroadCastReceiver;
import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.SmsBroadcastReceiver;
import com.skinfotech.ekinch.Utility;
import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.models.requests.OtpRequest;
import com.skinfotech.ekinch.models.requests.VerifyMobileRequest;
import com.skinfotech.ekinch.models.responses.CommonResponse;
import com.skinfotech.ekinch.retrofit.RetrofitApi;
import com.skinfotech.ekinch.views.interfaces.IAutoReadOtpListener;
import com.skinfotech.ekinch.views.interfaces.IOnBackPressedListener;

import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.skinfotech.ekinch.constants.Constants.USER_ID;

public class LoginFragment extends BaseFragment implements View.OnClickListener, IOnBackPressedListener, IAutoReadOtpListener {

    private EditText otpField1;
    private EditText otpField2;
    private EditText otpField3;
    private EditText otpField4;
    private View otpContainer;
    private EditText mobileEditText;
    Activity activity;
    private boolean mIsDoubleBackPress = false;
    private static final String TAG = "LoginFragment";

    SmsBroadcastReceiver smsBroadcastReceiver;
    private static final int REQ_USER_CONSENT = 200;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_login, container, false);
        mContentView.findViewById(R.id.textView4).setOnClickListener(this);
        activity = getActivity();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());

        mobileEditText = mContentView.findViewById(R.id.mobileEditText);
        otpField1 = mContentView.findViewById(R.id.otp_field1);
        otpField2 = mContentView.findViewById(R.id.otp_field2);
        otpField3 = mContentView.findViewById(R.id.otp_field3);
        otpField4 = mContentView.findViewById(R.id.otp_field4);
        otpField1.addTextChangedListener(new GenericOTPAdapter(otpField1));
        otpField2.addTextChangedListener(new GenericOTPAdapter(otpField2));
        otpField3.addTextChangedListener(new GenericOTPAdapter(otpField3));
        otpContainer = mContentView.findViewById(R.id.otpContainer);
        otpField4.addTextChangedListener(new GenericOTPAdapter(otpField4));
        mobileEditText.addTextChangedListener(new GenericOTPAdapter(mobileEditText));
        new OtpBroadCastReceiver().setOtpListener(this);
        otpField1.setOnTouchListener(mTouchListener);
        otpField2.setOnTouchListener(mTouchListener);
        otpField3.setOnTouchListener(mTouchListener);
        otpField4.setOnTouchListener(mTouchListener);
        return mContentView;
    }

    private View.OnTouchListener mTouchListener = (view, motionEvent) -> {
//        hideKeyboard();
        return false;
    };

    @Override
    public void onResume() {
        super.onResume();
        registerBroadcastReceiver();

        if (null != mActivity.getSupportActionBar()) {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alreadyHaveAccountTextView:
            case R.id.loginHereTextView:
                Navigation.findNavController(mContentView).navigate(R.id.action_loginFragment_to_signUpFragment);
                break;
            case R.id.textView4:
                otpContainer.setVisibility(View.VISIBLE);
                break;
            default:
                break;
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
    public void onOtpReceived(String otp) {
        updateOnUiThread(() -> {
            if (otpContainer != null && otpContainer.getVisibility() == View.VISIBLE) {
                otpField1 = mContentView.findViewById(R.id.otp_field1);
                otpField2 = mContentView.findViewById(R.id.otp_field2);
                otpField3 = mContentView.findViewById(R.id.otp_field3);
                otpField4 = mContentView.findViewById(R.id.otp_field4);
                otpField1.setText(otp.substring(0, 1));
                otpField2.setText(otp.substring(1, 2));
                otpField3.setText(otp.substring(2, 3));
                otpField4.setText(otp.substring(3, 4));
            }
        });
    }

    public class GenericOTPAdapter implements TextWatcher {

        private View view;

        GenericOTPAdapter(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            /*
             *
             * */
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /*
             *
             * */
        }

        @Override
        public void afterTextChanged(Editable s) {
            String textEntered = s.toString();
            switch (view.getId()) {
                case R.id.otp_field1:
                    if (textEntered.length() == 1) {
                        otpField2.requestFocus();
                    }
                    break;
                case R.id.otp_field2:
                    if (textEntered.length() == 1) {
                        otpField3.requestFocus();
                    } else if (textEntered.length() == 0) {
                        otpField1.requestFocus();
                    }
                    break;
                case R.id.otp_field3:
                    if (textEntered.length() == 1) {
                        otpField4.requestFocus();
                    } else if (textEntered.length() == 0) {
                        otpField2.requestFocus();
                    }
                    break;
                case R.id.otp_field4:
                    if (textEntered.length() == 0) {
                        otpField3.requestFocus();
                        return;
                    }
                    String otpText1 = otpField1.getText().toString();
                    String otpText2 = otpField2.getText().toString();
                    String otpText3 = otpField3.getText().toString();
                    String otpText4 = otpField4.getText().toString();
                    String otpEntered = otpText1 + otpText2 + otpText3 + otpText4;
                    verifyOtpServerCall(otpEntered);
                    hideKeyboard();
                    break;
                case R.id.mobileEditText:
                    if (otpContainer.getVisibility() == View.VISIBLE) {
                        otpField1.setText("");
                        otpField2.setText("");
                        otpField3.setText("");
                        otpField4.setText("");
                        otpContainer.setVisibility(View.GONE);
                    }
                    if (textEntered.length() == mActivity.getResources().getInteger(R.integer.mobile_length)) {
                        verifyMobileNumberServerCall(textEntered);
                        startSmsUserConsent(textEntered);
                        hideKeyboard();
                    }
                    break;
                default:
                    break;
            }
        }

        private void startSmsUserConsent(String number) {
            SmsRetrieverClient client = SmsRetriever.getClient(activity);
            //We can add sender phone number or leave it blank
            // I'm adding null here
            client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(activity, "On Success", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, "On OnFailure", Toast.LENGTH_LONG).show();
                }
            });
        }

        private void verifyMobileNumberServerCall(String mobileNumber) {
            showProgress();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(mActivity);
                        String smsHashString = appSignatureHelper.getAppSignatures().get(0);
                        Call<CommonResponse> call = RetrofitApi.getAppServicesObject().verifyMobileNumber(new VerifyMobileRequest(mobileNumber, smsHashString));
                        final Response<CommonResponse> response = call.execute();
                        updateOnUiThread(() -> handleResponse(response));
                    } catch (Exception e) {
                        stopProgress();
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                private void handleResponse(Response<CommonResponse> response) {
                    if (response.isSuccessful()) {
                        CommonResponse vehiclesResponse = response.body();
                        if (vehiclesResponse != null) {
                            if (Constants.SUCCESS.equalsIgnoreCase(vehiclesResponse.getErrorCode())) {
                                otpContainer.setVisibility(View.VISIBLE);
                                hideKeyboard();
                            }
                            showToast(vehiclesResponse.getErrorMessage());
                        }
                    }
                    stopProgress();
                }
            }).start();
        }

        private void verifyOtpServerCall(String otp) {
            showProgress();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String mobileNumber = mobileEditText.getText().toString();
                        Call<CommonResponse> call = RetrofitApi.getAppServicesObject().verifyOTP(new OtpRequest(otp, mobileNumber));
                        final Response<CommonResponse> response = call.execute();
                        updateOnUiThread(() -> handleResponse(response));
                    } catch (Exception e) {
                        stopProgress();
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                private void handleResponse(Response<CommonResponse> response) {
                    if (response.isSuccessful()) {
                        CommonResponse commonResponse = response.body();
                        if (commonResponse != null) {
                            if (Constants.SUCCESS.equalsIgnoreCase(commonResponse.getErrorCode())) {
                                storeStringDataInSharedPref(Constants.USER_LOGIN_DONE, Constants.YES);
                                storeStringDataInSharedPref(Constants.USER_ID, commonResponse.getUserId());
                                Navigation.findNavController(mContentView).navigate(R.id.action_loginFragment_to_homeFragment);
                            }
                            sendEvent();
                            showToast(commonResponse.getErrorMessage());
                        }
                    }
                    stopProgress();
                }
            }).start();
        }
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
        mFirebaseAnalytics.logEvent("Login", params);
    }

    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, REQ_USER_CONSENT);
                    }

//                    @Override
//                    public void onSuccess(Intent intent) {
//
//                    }

                    @Override
                    public void onFailure() {
                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        activity.registerReceiver(smsBroadcastReceiver, intentFilter);
    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        registerBroadcastReceiver();
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                textViewMessage.setText(
//                        String.format("%s - %s", getString(R.string.received_message), message));
                getOtpFromMessage(message);
            }
        }
    }

    private void getOtpFromMessage(String otp) {
        // This will match any 6 digit number in the message
        updateOnUiThread(() -> {

            if (Utility.isNotEmpty(otp) && otp.toLowerCase().contains("एक इंच")) {
                //नमस्कार एक इंच मे आप का स्वागत है। आपका OTP है  8408 धन्यवाद
                try {
                    String otpString = otp.split("OTP है")[1].trim().substring(0, 4);
                    if (Utility.isNotEmpty(otpString)) {
                        if (otpContainer != null && otpContainer.getVisibility() == View.VISIBLE) {
                            otpField1 = mContentView.findViewById(R.id.otp_field1);
                            otpField2 = mContentView.findViewById(R.id.otp_field2);
                            otpField3 = mContentView.findViewById(R.id.otp_field3);
                            otpField4 = mContentView.findViewById(R.id.otp_field4);
                            otpField1.setText(otpString.substring(0, 1));
                            otpField2.setText(otpString.substring(1, 2));
                            otpField3.setText(otpString.substring(2, 3));
                            otpField4.setText(otpString.substring(3, 4));
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Unable to fetch OTP", e);
                }
            }


        });
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.unregisterReceiver(smsBroadcastReceiver);

    }


}
