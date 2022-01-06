package com.skinfotech.ekinch.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.views.fragments.VideoViewFragment;
import com.skinfotech.ekinch.views.interfaces.IOnBackPressedListener;

import java.util.Locale;

import static com.skinfotech.ekinch.constants.Constants.MY_DATA_CHECK_CODE;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private NavController mNavController;
    private TextToSpeech myTTS;
    private String[] mPermissionArray = new String[]{
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(aVoid -> {
            // Successfully started retriever, expect broadcast intent
        });
        task.addOnFailureListener(e -> {
            // Failed to start retriever, inspect Exception for more details
        });
    }

    public void changeOrientationForVideo(boolean isVideoFragment) {
        if (isVideoFragment) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            if(getSupportActionBar() != null){
                getSupportActionBar().hide();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        return mNavController.navigateUp();
    }

    @Override
    public void onBackPressed() {
        final Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment).getChildFragmentManager().getFragments().get(0);
        final NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment);
        if (currentFragment instanceof IOnBackPressedListener) {
            ((IOnBackPressedListener) currentFragment).onBackPressedToExit();
        }
        else if (!controller.popBackStack()) {
            finish();
        }
    }

    private String textToSpeech = "";

    public void speakAudio(String text) {
        if (myTTS != null && myTTS.isSpeaking()) {
            myTTS.stop();
        }
        Intent checkTTSIntent = new Intent();
        this.textToSpeech = text;
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            } else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myTTS != null) {
            if (myTTS.isSpeaking()) {
                myTTS.stop();
            }
            myTTS.shutdown();
        }
    }

    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE) {
                myTTS.setLanguage(Locale.US);
            }
            myTTS.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(mPermissionArray, Constants.PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.PERMISSION_REQUEST_CODE) {
            boolean isAllPermissionAllowed = false;
            for (int grantResult : grantResults) {
                isAllPermissionAllowed = (grantResult == PackageManager.PERMISSION_GRANTED);
            }
        }
    }
}