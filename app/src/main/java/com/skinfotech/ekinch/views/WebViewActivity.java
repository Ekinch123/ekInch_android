package com.skinfotech.ekinch.views;

import static com.skinfotech.ekinch.constants.Constants.SHARED_PREF_NAME;
import static com.skinfotech.ekinch.constants.Constants.USER_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.adapter.CertificateVideoListAdapter;
import com.skinfotech.ekinch.models.requests.CertificateBody;
import com.skinfotech.ekinch.models.responses.CertificateResponce;
import com.skinfotech.ekinch.models.responses.Video;
import com.skinfotech.ekinch.retrofit.RetrofitApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class WebViewActivity extends AppCompatActivity {

    private static ProgressDialog sProgressDialog;
    RecyclerView videoRV;
    CertificateVideoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        videoRV = findViewById(R.id.videoRV);

        videoRV.setLayoutManager(new LinearLayoutManager(this));
        getCertificate();
    }

    private void getCertificate() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

//                    Call<List<CertificateResponce>> call = RetrofitApi.getAppServicesObject().getCertificate(getStringDataFromSharedPref(USER_ID));
                    Call<List<CertificateResponce>> call = RetrofitApi.getAppServicesObject().getCertificate("1");
                    final Response<List<CertificateResponce>> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                }
            }

            private void handleResponse(Response<List<CertificateResponce>> response) {
                if (response.isSuccessful()) {

                    List<Video> videos = new ArrayList<>();

                    for (int i = 0; i < response.body().size(); i++) {
                        for (int j = 0; j < response.body().get(i).getVideos().size(); j++) {
                            videos.add(response.body().get(i).getVideos().get(j));
                        }
                    }
                    adapter = new CertificateVideoListAdapter(WebViewActivity.this, videos);
                    videoRV.setAdapter(adapter);
                }
                stopProgress();
            }
        }).start();
    }

    public void showProgress() {
        runOnUiThread(() -> {
            sProgressDialog = new ProgressDialog(this);
            sProgressDialog.setMessage(getString(R.string.please_wait));
            sProgressDialog.setCancelable(false);
            sProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            sProgressDialog.show();
        });
    }

    String getStringDataFromSharedPref(String keyName) {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return prefs.getString(keyName, "");
    }

    void stopProgress() {
        runOnUiThread(() -> {
            if (sProgressDialog != null && sProgressDialog.isShowing()) {
                sProgressDialog.dismiss();
            }
        });
    }

    void updateOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

}