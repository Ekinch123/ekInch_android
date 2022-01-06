package com.skinfotech.ekinch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.skinfotech.ekinch.views.interfaces.IAutoReadOtpListener;

public class OtpBroadCastReceiver extends BroadcastReceiver {

    private static IAutoReadOtpListener sOtpListener;
    private static final String TAG = "SMS_HASH";

    public void setOtpListener(IAutoReadOtpListener otpListener) {
        sOtpListener = otpListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: OTP is received");
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Log.e(TAG, "onReceive: OTP is received2");
            Bundle extras = intent.getExtras();
            if (extras == null) {
                return;
            }
            Log.e(TAG, "onReceive: OTP is received3");
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            if (status == null) {
                return;
            }
            Log.e(TAG, "onReceive: OTP is received4");
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    Log.e(TAG, "onReceive: OTP is received5");
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    Log.e(TAG, "onReceive: " + message);
                    if (Utility.isNotEmpty(message) && message.toLowerCase().contains("ek inch")) {
                        if (sOtpListener != null) {
                            // <#> Dear User, 975917 is the one time password to login into Smilyhome App. Ni+9ZcbRtzL
                            try {
                                String otpString = message.split(",")[1].trim().substring(0,4);
                                if (Utility.isNotEmpty(otpString)) {
                                    sOtpListener.onOtpReceived(otpString);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Unable to fetch OTP", e);
                            }
                        }
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    break;
            }
        }
    }
}
