package com.skinfotech.ekinch.views.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.Utility;
import com.skinfotech.ekinch.constants.Constants;
import com.skinfotech.ekinch.models.requests.QuestionRequest;
import com.skinfotech.ekinch.models.responses.ProfileResponse;
import com.skinfotech.ekinch.models.responses.QuestionResponse;
import com.skinfotech.ekinch.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.skinfotech.ekinch.constants.Constants.USER_ID;

public class QuestionsFragment extends BaseFragment implements View.OnClickListener {

    private QuestionsAdapter mQuestionsAdapter;
    private static final String TAG = "QuestionsFragment";
    private String mCourseId = "";
    private String mVideoUrl = "";
    private ImageView questionVideoImageView;
    private String recordFile = "";
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private Chronometer timer;
    private File mFileToPlay = null;
    private File mVideoFile = null;
    private int mSelectedVideoPosition = 0;
    private static final int VIDEO_PERMISSION_CODE = 9005;
    private QuestionResponse.QuestionItem mVideoUploadSelectedItem = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_questions, container, false);
        RecyclerView questionRecyclerView = mContentView.findViewById(R.id.questionRecyclerView);
        questionVideoImageView = mContentView.findViewById(R.id.questionVideoImageView);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mQuestionsAdapter = new QuestionsAdapter();
        questionRecyclerView.setAdapter(mQuestionsAdapter);
        mContentView.findViewById(R.id.questionVideoContainer).setOnClickListener(this);
        questionRecyclerView.addItemDecoration(new DividerItemDecoration(questionRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        fetchProfile();
        if (null != getArguments()) {
            mCourseId = QuestionsFragmentArgs.fromBundle(getArguments()).getCourseId();
            String courseName = QuestionsFragmentArgs.fromBundle(getArguments()).getCourseName();
            TextView questionTitleTextView = mContentView.findViewById(R.id.questionTitleTextView);
            questionTitleTextView.setText(courseName);
            fetchQuestionsServerCall();
        }
        return mContentView;
    }

    private void fetchQuestionsServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<QuestionResponse> call = RetrofitApi.getAppServicesObject().fetchQuestionsServerCall(new QuestionRequest(getStringDataFromSharedPref(USER_ID), mCourseId));
                    final Response<QuestionResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<QuestionResponse> response) {
                if (response.isSuccessful()) {
                    QuestionResponse questionResponse = response.body();
                    if (questionResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(questionResponse.getErrorCode())) {
                            List<QuestionResponse.QuestionItem> questionItemList = questionResponse.getQuestionList();
                            if (!Utility.isEmpty(questionItemList)) {
                                mQuestionsAdapter.setQuestionItemList(questionItemList);
                                mQuestionsAdapter.notifyDataSetChanged();
                                if (!Utility.isEmpty(questionResponse.getQuestionImage())) {
                                    Picasso.get().load(questionResponse.getQuestionImage()).placeholder(R.drawable.video_bg).into(questionVideoImageView);
                                }
                                mVideoUrl = questionResponse.getQuestionVideo();
                            }
                        } else {
                            showToast(questionResponse.getErrorMessage());
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
        if (view.getId() == R.id.questionVideoContainer) {
            if (Utility.isEmpty(mVideoUrl)) {
                return;
            }
//            NavDirections directions = QuestionsFragmentDirections.actionQuestionsFragmentToVideoViewFragment(mVideoUrl);
//            Navigation.findNavController(mContentView).navigate(directions);
        }
    }

    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        String recordPath = "";
        if (null != mActivity.getExternalFilesDir("/")) {
            recordPath = mActivity.getExternalFilesDir("/").getAbsolutePath();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();
        recordFile = "Recording_" + formatter.format(now) + ".3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private void startVideoRecording() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        String recordPath;
        recordPath = mActivity.getExternalFilesDir("/").getAbsolutePath();
        File folder = new File(recordPath);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();
        mVideoFile = new File(folder, "video_file_" + formatter.format(now) + ".mp4");
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri videoUri = Uri.fromFile(mVideoFile);
        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(videoIntent, VIDEO_PERMISSION_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == VIDEO_PERMISSION_CODE && resultCode == RESULT_OK) {
            if (mVideoFile != null) {
                showVideoRecordDialog(mSelectedVideoPosition);
            }
        } else {
            showToast("No video");
        }
    }

    private void stopRecording() {
        timer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
    }

    private class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionsViewHolder> {

        private List<QuestionResponse.QuestionItem> questionItemList = new ArrayList<>();

        public void setQuestionItemList(List<QuestionResponse.QuestionItem> questionItemList) {
            this.questionItemList = questionItemList;
        }

        @NonNull
        @Override
        public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.question_item, parent, false);
            return new QuestionsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionsViewHolder holder, int position) {
            QuestionResponse.QuestionItem item = questionItemList.get(position);
            holder.questionNumberTextView.setText(String.valueOf(position + 1).concat("."));
            holder.questionTextView.setText(item.getQuestion());
            if (item.isSubmit()) {
                holder.submitTextView.setVisibility(View.VISIBLE);
                holder.audioTextView.setText(getString(R.string.txt_hear_recording));
            }
            if (item.getQuestionType().equalsIgnoreCase(Constants.IFileType.AUDIO)) {
                if (item.isSubmit()) {
                    holder.audioTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.ic_hear_audio), null, null, null);
                } else {
                    holder.audioTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.ic_mic), null, null, null);
                }
            } else {
                if (item.isSubmit()) {
                    holder.audioTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.ic_view_video), null, null, null);
                    holder.audioTextView.setText(getString(R.string.txtVideoRecorded));
                } else {
                    holder.audioTextView.setText(getString(R.string.txt_record_video));
                    holder.audioTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mActivity, R.drawable.ic_video), null, null, null);
                }
            }
        }

        @Override
        public int getItemCount() {
            return questionItemList.size();
        }

        private class QuestionsViewHolder extends RecyclerView.ViewHolder {

            private TextView questionNumberTextView;
            private TextView questionTextView;
            private TextView audioTextView;
            private TextView submitTextView;
            private MediaPlayer mMediaPlayer = new MediaPlayer();

            public QuestionsViewHolder(@NonNull View itemView) {
                super(itemView);
                submitTextView = itemView.findViewById(R.id.submitTextView);
                audioTextView = itemView.findViewById(R.id.audioTextView);
                questionNumberTextView = itemView.findViewById(R.id.questionNumberTextView);
                questionTextView = itemView.findViewById(R.id.questionTextView);
                View audioImageView = itemView.findViewById(R.id.audioImageView);
                audioImageView.setOnClickListener(view -> {
                    String textToSpeak = questionTextView.getText().toString();
                    new Handler().postDelayed(QuestionsFragment.this::stopProgress, 1000);
                    showProgress();
                    speakAudio(textToSpeak);
                });
                audioTextView.setOnClickListener(view -> {
                    QuestionResponse.QuestionItem selectedQuestion = questionItemList.get(getAdapterPosition());
                    if (selectedQuestion.getQuestionType().equalsIgnoreCase(Constants.IFileType.AUDIO)) {
                        if (audioTextView.getText().toString().equalsIgnoreCase(getString(R.string.txt_hear_recording))) {
                            mMediaPlayer.reset();
                            if (mMediaPlayer.isPlaying()) {
                                return;
                            }
                            try {
                                mMediaPlayer.setDataSource(selectedQuestion.getQuestionAnswer());
                                mMediaPlayer.prepare();
                                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                audioTextView.setText(getString(R.string.txt_playing));
                                mMediaPlayer.start();
                                mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                                    audioTextView.setText(getString(R.string.txt_hear_recording));
                                    mMediaPlayer.reset();
                                });
                            } catch (IOException e) {
                                Log.e(TAG, "Audio play failed :: " + e.toString());
                            }
                        } else {
                            if (!selectedQuestion.isSubmit()) {
                                showAudioRecordDialog(getAdapterPosition());
                            }
                        }
                    } else {
                        if (selectedQuestion.isSubmit()) {
//                            NavDirections directions = QuestionsFragmentDirections.actionQuestionsFragmentToVideoViewFragment(selectedQuestion.getQuestionAnswer());
                            //Navigation.findNavController(mContentView).navigate(directions);
                        } else {
                            mSelectedVideoPosition = getAdapterPosition();
                            mVideoUploadSelectedItem = questionItemList.get(mSelectedVideoPosition);
                            showVideoRecordDialog(mSelectedVideoPosition);
                        }
                    }
                });
            }

            private void showAudioRecordDialog(final int position) {
                final Dialog dialog = new Dialog(mActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.recording_dialog);
                timer = dialog.findViewById(R.id.record_timer);
                TextView audioTextView = dialog.findViewById(R.id.audioTextView);
                TextView questionNumberTextView = dialog.findViewById(R.id.questionNumberTextView);
                TextView questionTextView = dialog.findViewById(R.id.questionTextView);
                questionTextView.setText(questionItemList.get(position).getQuestion());
                questionNumberTextView.setText(String.valueOf(position + 1).concat("."));
                audioTextView.setOnClickListener(v -> {
                    if (isRecording) {
                        cancelRecordingDialog(audioTextView);
                    } else {
                        if (audioTextView.getText().toString().equalsIgnoreCase(getString(R.string.txt_hear_recording))) {
                            try {
                                mMediaPlayer.setDataSource(questionItemList.get(getAdapterPosition()).getRecordedFile().getAbsolutePath());
                                mMediaPlayer.prepare();
                                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                audioTextView.setText(getString(R.string.txt_playing));
                                mMediaPlayer.start();
                                mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                                    audioTextView.setText(getString(R.string.txt_hear_recording));
                                    mMediaPlayer.reset();
                                });
                            } catch (IOException e) {
                                Log.e(TAG, "Audio play failed :: " + e.toString());
                            }
                        } else {
                            audioTextView.setText(getString(R.string.txt_stop));
                            startRecording();
                            isRecording = true;
                        }
                    }
                });
                dialog.findViewById(R.id.closeDialog).setOnClickListener(v -> dialog.dismiss());
                dialog.findViewById(R.id.submitTextView).setOnClickListener(v -> {
                    if (mFileToPlay != null) {
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                            mMediaPlayer.reset();
                            mMediaPlayer.release();
                        }
                        dialog.dismiss();
                        uploadFile(questionItemList.get(position).getQuestionId(), mFileToPlay);
                    }
                });
                dialog.show();
            }

            private void cancelRecordingDialog(TextView audioTextView) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setPositiveButton("OKAY", (dialog, which) -> {
                    isRecording = false;
                    mFileToPlay = new File(mActivity.getExternalFilesDir("/".concat(recordFile)).getAbsolutePath());
                    stopRecording();
                    questionItemList.get(getAdapterPosition()).setRecordedFile(mFileToPlay);
                    audioTextView.setText(getString(R.string.txt_hear_recording));
                });
                alertDialog.setNegativeButton(R.string.cancel, null);
                alertDialog.setTitle("Still recording");
                alertDialog.setMessage("Are you sure, you want to stop the recording?");
                alertDialog.create().show();
            }
        }
    }

    private void showVideoRecordDialog(final int position) {
        final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.recording_dialog);
        timer = dialog.findViewById(R.id.record_timer);
        dialog.findViewById(R.id.logoImageView).setVisibility(View.VISIBLE);
        timer.setVisibility(View.INVISIBLE);
        TextView audioTextView = dialog.findViewById(R.id.audioTextView);
        audioTextView.setText(getString(R.string.txt_record_video));
        TextView questionNumberTextView = dialog.findViewById(R.id.questionNumberTextView);
        TextView questionTextView = dialog.findViewById(R.id.questionTextView);
        questionTextView.setText(mVideoUploadSelectedItem.getQuestion());
        questionNumberTextView.setText(String.valueOf(position + 1).concat("."));
        audioTextView.setOnClickListener(v -> {
            if (audioTextView.getText().toString().equalsIgnoreCase(getString(R.string.txt_record_video))) {
                dialog.dismiss();
                startVideoRecording();
            }
        });
        dialog.findViewById(R.id.closeDialog).setOnClickListener(v -> {
            mFileToPlay = null;
            mVideoFile = null;
            dialog.dismiss();
        });
        if (mVideoFile != null) {
            audioTextView.setText(getString(R.string.txtVideoRecorded));
        }
        dialog.findViewById(R.id.submitTextView).setOnClickListener(v -> {
            dialog.dismiss();
            if (mVideoFile != null) {
                uploadFile(mVideoUploadSelectedItem.getQuestionId(), mVideoFile);
            } else {
                showToast(getString(R.string.msgNoFileToUpload));
            }
        });
        dialog.show();
    }

    private void uploadFile(String questionId, final File fileToUploadServer) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), fileToUploadServer);
                    MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("inputAnswer", fileToUploadServer.getName(), requestBody);
                    RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), getStringDataFromSharedPref(USER_ID));
                    RequestBody courseIdBody = RequestBody.create(MediaType.parse("text/plain"), mCourseId);
                    RequestBody questionIdBody = RequestBody.create(MediaType.parse("text/plain"), questionId);
                    Call<QuestionResponse> call = RetrofitApi.getAppServicesObject().uploadAudioFile(fileToUpload, userIdBody, courseIdBody, questionIdBody);
                    final Response<QuestionResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<QuestionResponse> response) {
                if (response.isSuccessful()) {
                    QuestionResponse questionResponse = response.body();
                    if (questionResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(questionResponse.getErrorCode())) {
                            List<QuestionResponse.QuestionItem> questionItemList = questionResponse.getQuestionList();
                            if (!Utility.isEmpty(questionItemList)) {
                                mQuestionsAdapter.setQuestionItemList(questionItemList);
                                mQuestionsAdapter.notifyDataSetChanged();
                                if (!Utility.isEmpty(questionResponse.getQuestionImage())) {
                                    Picasso.get().load(questionResponse.getQuestionImage()).placeholder(R.drawable.video_bg).into(questionVideoImageView);
                                }
                                mVideoUrl = questionResponse.getQuestionVideo();
                            }
                        }
                        showToast(questionResponse.getErrorMessage());
                    }
                }
                stopProgress();
            }
        }).start();
    }
}
