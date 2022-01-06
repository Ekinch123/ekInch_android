package com.skinfotech.ekinch.views.fragments;

import static io.agora.rtc.video.VideoCanvas.RENDER_MODE_HIDDEN;
import static io.agora.rtc.video.VideoEncoderConfiguration.STANDARD_BITRATE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.skinfotech.ekinch.App;
import com.skinfotech.ekinch.R;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.ChannelMediaOptions;
import io.agora.rtc.models.ClientRoleOptions;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;


public class LiveVideoFragment extends Fragment {

    private static final int PERMISSION_REQ_ID = 22;

    // Ask for Android device permissions at runtime.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };
    FrameLayout mLocalContainer, fl_remote_video;
    RelativeLayout mRemoteContainer;
    Handler handler;
    ImageView btn_call;


    private Map<Integer, ViewGroup> remoteViews = new ConcurrentHashMap<Integer, ViewGroup>();


    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the onJoinChannelSuccess callback.
        // This callback occurs when the local user successfully joins the channel.
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("agora", "Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                    setupRemoteVideo(uid);

                }
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            setupRemoteVideo(uid);

        }

        @Override
        // Listen for the onUserOffline callback.
        // This callback occurs when the host leaves the channel or drops offline.
        public void onUserOffline(final int uid, int reason) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i("agora", "User offline, uid: " + (uid & 0xFFFFFFFFL));
//                    removeRemoteVideo();
//                }
//            });
            onBackPressed();

        }
    };

    public void onBackPressed() {
        getActivity().onBackPressed();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLocalContainer = view.findViewById(R.id.local_video_view_container);
        mRemoteContainer = view.findViewById(R.id.remote_video_view_container);
        fl_remote_video = view.findViewById(R.id.fl_remote_video);
        handler = new Handler(Looper.getMainLooper());

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            initializeEngine();
            setupVideoConfig();
            setChannelProfile();
        }

        btn_call = view.findViewById(R.id.btn_call);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    SurfaceView mRemoteView;

    private void setupRemoteVideo(int uid) {

        int count = mRemoteContainer.getChildCount();
        View view = null;
//        for (int i = 0; i < count; i++) {
//            View v = mRemoteContainer.getChildAt(i);
//            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
//                view = v;
//            }
//        }

        if (view != null) {
            return;
        }

        // Create a SurfaceView object.
        mRtcEngine.muteLocalAudioStream(true);

//        mRemoteView = RtcEngine.CreateRendererView(getActivity().getBaseContext());
//        mRemoteContainer.addView(mRemoteView);
        // Initializes the video view of a remote user.
//        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
//        mRemoteView.setTag(uid);


        handler.post(() ->
        {
            /**Display remote video stream*/
            SurfaceView surfaceView = null;
            // Create render view by RtcEngine
            surfaceView = RtcEngine.CreateRendererView(getContext());
            surfaceView.setZOrderMediaOverlay(true);
            remoteViews.put(uid, fl_remote_video);
            // Add to the remote container
            fl_remote_video.addView(surfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            // Setup remote video to render
            mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, RENDER_MODE_HIDDEN, uid));
        });


    }

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            mRemoteContainer.removeView(mRemoteView);
        }
        // Destroys remote view
        mRemoteView = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mRtcEngine.leaveChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RtcEngine.destroy();


    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getActivity().getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e("TAG", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        mRtcEngine.enableVideo();

        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        ));
    }

    private void setChannelProfile() {
        mRtcEngine.setChannelProfile(Constants.CLIENT_ROLE_AUDIENCE);

        ClientRoleOptions clientRoleOptions = new ClientRoleOptions();
        boolean isLowLatency = false;
        clientRoleOptions.audienceLatencyLevel = isLowLatency ? Constants.AUDIENCE_LATENCY_LEVEL_ULTRA_LOW_LATENCY : Constants.AUDIENCE_LATENCY_LEVEL_LOW_LATENCY;
        mRtcEngine.setClientRole(IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_AUDIENCE, clientRoleOptions);
        setupLocalVideo();
    }

    private void setupLocalVideo() {

        // Enable the video module.
        mRtcEngine.enableVideo();

        // Create a SurfaceView object.
        SurfaceView mLocalView;

        mLocalView = RtcEngine.CreateRendererView(getActivity().getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        // Set the local video view.
        VideoCanvas localVideoCanvas = new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(localVideoCanvas);
        joinChannel();
    }


    private void joinChannel() {
        // Join a channel with a token.
        String mRoomName;
//        mRoomName = getIntent().getStringExt  ra("CName");
//        mRoomName = "ekinch_live";

        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                ((App) getActivity().getApplication()).getGlobalSettings().getVideoEncodingDimensionObject(),
                VideoEncoderConfiguration.FRAME_RATE.valueOf(((App) getActivity().getApplication()).getGlobalSettings().getVideoEncodingFrameRate()),
                STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.valueOf(((App) getActivity().getApplication()).getGlobalSettings().getVideoEncodingOrientation())
        ));


        mRoomName = "EkInch";
        ChannelMediaOptions option = new ChannelMediaOptions();
        option.autoSubscribeAudio = true;
        option.autoSubscribeVideo = true;
        mRtcEngine.joinChannel(null,
                mRoomName, "Extra Optional Data", 0, option);
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getContext(), permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }
}