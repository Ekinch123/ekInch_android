package com.skinfotech.ekinch.views.fragments;

import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.models.requests.VideoLength;
import com.skinfotech.ekinch.models.responses.QuestionResponse;
import com.skinfotech.ekinch.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.skinfotech.ekinch.constants.Constants.USER_ID;


public class VideoViewFragment extends BaseFragment {

    private static final String TAG = "VideoViewFragment";
    private static String timeStamp;
    PlayerView videoView;
    TextView textView;
    private Handler mHandler1;
    private Runnable mRunnable;
    private boolean running;
    private ProgressBar progressBar;
    SimpleExoPlayer player;
    private String CoursesId = "";
    private boolean inPreview;
    private Camera camera;
    private Camera.Parameters params;
    private SurfaceView preview;
    private SurfaceHolder previewHolder;
    private boolean safeToTakePicture = false;
    ImageView image, backIV;
    private String timeduration = "0";
    private String video_Name, video_Id;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_video_view, container, false);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());

        if (getArguments() != null) {
            mActivity.changeOrientationForVideo(true);
            videoView = mContentView.findViewById(R.id.videoView);
            textView = mContentView.findViewById(R.id.textView);
            progressBar = mContentView.findViewById(R.id.progressBar);
            image = mContentView.findViewById(R.id.image);
            backIV = mContentView.findViewById(R.id.backIV);


            preview = (SurfaceView) mContentView.findViewById(R.id.surface);
            previewHolder = preview.getHolder();
            previewHolder.addCallback(surfaceCallback);
            previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


            progressBar.setVisibility(View.VISIBLE);
            String videoUrl = VideoViewFragmentArgs.fromBundle(getArguments()).getVideoUrl();
            CoursesId = VideoViewFragmentArgs.fromBundle(getArguments()).getCoursesId();
            video_Id = VideoViewFragmentArgs.fromBundle(getArguments()).getVideoId();
            video_Name = VideoViewFragmentArgs.fromBundle(getArguments()).getVideoName();
            Log.d(TAG, "onCreateView: videoUrl :: " + videoUrl);
            Uri uri = Uri.parse(videoUrl);


            player = new SimpleExoPlayer.Builder(getContext()).build();
            videoView.setPlayer(player);
            player.addListener(new Player.EventListener() {


                @Override
                public void onIsLoadingChanged(boolean isLoading) {
                    if (isLoading)
                        progressBar.setVisibility(View.VISIBLE);
                    else
                        progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying) {
                        progressBar.setVisibility(View.GONE);
                        if (!CoursesId.isEmpty())
                            if (!CoursesId.isEmpty())
                                stratVIdeoTime();
                    }
                }

            });

//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) player.getLayoutParams();
//            params.width = params.MATCH_PARENT;
//            params.height = params.MATCH_PARENT;
//            player.setLayoutParams(params);

            MediaItem mediaItem = MediaItem.fromUri(uri);
            // Set the media item to be played.
            player.setMediaItem(mediaItem);
            // Prepare the player.
            player.prepare();
            // Start the playback.
            player.play();


        }

        return mContentView;
    }

    private void stratVIdeoTime() {

        final long duration = player.getDuration();
        running = true;

        new Thread(() -> {
            do {
                textView.post(() -> {
                    long time = (player.getCurrentPosition());
//                                        textView.setText(time + "");
                    hitAPI(formattime(time), formattime(duration));
                });
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!running) break;
            }
            while (player.getCurrentPosition() < duration);
        }).start();

        backIV.setOnClickListener(v -> getActivity().onBackPressed());
    }


    private String formattime(long time) {

        DateFormat formatter = new SimpleDateFormat("mm:ss", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String text = formatter.format(new Date(time));
        return text;
    }

    private void hitAPI(String time, String duration) {
        timeduration = duration;
        try {
            if (safeToTakePicture) {
                camera.takePicture(null, null, mPicture);
                safeToTakePicture = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Call<QuestionResponse> call = RetrofitApi.getAppServicesObject().seenVideoLengthCall(new VideoLength(CoursesId, time, getStringDataFromSharedPref(USER_ID), duration));
                    final Response<QuestionResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    showToast(e.getMessage());
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<QuestionResponse> response) {
                if (response.isSuccessful()) {

                }
            }
        }).start();
    }

    PowerManager.WakeLock wakeLock;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendEvent();


    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null)
            player.release();

        if (inPreview) {
            camera.stopPreview();
        }
        camera.release();
        camera = null;
        inPreview = false;
        mActivity.changeOrientationForVideo(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }


    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
//            if (size.width <= width && size.height <= height) {
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea > resultArea) {
                    result = size;
                }
            }
//            }
        }

        return (result);
    }

    private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea < resultArea) {
                    result = size;
                }
            }
        }

        return (result);
    }


    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
            } catch (Throwable t) {
                Log.e("PreviewD",
                        "Exception in setPreviewDisplay()", t);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            params.setRotation(180);
            Camera.Size size = getBestPreviewSize(width, height, params);
            Camera.Size pictureSize = getSmallestPictureSize(params);
            if (size != null && pictureSize != null) {
                params.setPreviewSize(size.width, size.height);
                params.setPictureSize(pictureSize.width,
                        pictureSize.height);
                camera.setParameters(params);
                camera.startPreview();
                inPreview = true;
                safeToTakePicture = true;


            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            camera.startPreview();

            if (pictureFile == null) {
                //no path to picture, return
                safeToTakePicture = true;
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();              //<-------- show exception
            } catch (IOException e) {
                e.printStackTrace();              //<-------- show exception
            }

            //finished saving picture
            uploadFile(pictureFile);
            Picasso.get().load(pictureFile).into(image);


        }
    };

    static File getOutputMediaFile() {

        /* yyyy-MM-dd'T'HH:mm:ss.SSSZ */
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());

        // file name
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File mediaFile = new File(path, File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;

    }

    private void uploadFile(File file) {
        if (file == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
                    MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                    RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), getStringDataFromSharedPref(USER_ID));
                    RequestBody userIdBodyCourseId = RequestBody.create(MediaType.parse("text/plain"), CoursesId);

                    RequestBody videoId = RequestBody.create(MediaType.parse("text/plain"), video_Id);
                    RequestBody progressTime = RequestBody.create(MediaType.parse("text/plain"), timeduration);
                    RequestBody videoName = RequestBody.create(MediaType.parse("text/plain"), video_Name);


                    Call<JSONObject> call = RetrofitApi.getAppServicesObject().uploadCandidatePhoto(fileToUpload, userIdBody, userIdBodyCourseId, videoId, progressTime, videoName);
                    final Response<JSONObject> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<JSONObject> response) {
                if (response.isSuccessful()) {

                }
                safeToTakePicture = true;

            }
        }).start();
    }

    private void sendEvent() {
        Bundle params = new Bundle();
        String userId = "";
        try {
            userId = getStringDataFromSharedPref(USER_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.putString("user_id", getStringDataFromSharedPref(USER_ID));
        params.putString("video_Name", video_Name);
        params.putString("video_Id", video_Id);
        mFirebaseAnalytics.logEvent("video_view", params);
    }


}
