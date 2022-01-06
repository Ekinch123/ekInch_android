package com.skinfotech.ekinch.views.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.skinfotech.ekinch.R;
import com.skinfotech.ekinch.Utility;
import com.skinfotech.ekinch.models.dto.MarkSheetDto;

import java.io.ByteArrayOutputStream;

public class GenerateMarkSheetFragment extends BaseFragment {

    private static final String TAG = "GenerateMarkSheet";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_generate_certificate, container, false);
        if (null != getArguments()) {
            MarkSheetDto markSheet = GenerateMarkSheetFragmentArgs.fromBundle(getArguments()).getMarkSheetItem();
            TextView courseNameTextView = mContentView.findViewById(R.id.courseNameTextView);
            TextView totalMarksTextView = mContentView.findViewById(R.id.marksObtainedTextView);
            TextView marksObtainedTextView = mContentView.findViewById(R.id.totalMarksTextView);
            TextView statusTextView = mContentView.findViewById(R.id.statusTextView);
            TextView shareResultTextView = mContentView.findViewById(R.id.shareResultTextView);
            if (markSheet != null) {
                courseNameTextView.setText(markSheet.getMarkSheetTitle());
                totalMarksTextView.setText(markSheet.getCourseTotalMarks());
                marksObtainedTextView.setText(markSheet.getCourseMarksObtained());
                statusTextView.setText(markSheet.getCourseStatus());
                if (markSheet.getCourseStatus().equalsIgnoreCase("pass")) {
                    statusTextView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.green_curve_stroke));
                    statusTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.success_green));
                } else {
                    statusTextView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.red_curve_stroke));
                    statusTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.red));
                }
            }
            shareResultTextView.setOnClickListener(view -> shareScreenShot());
        }
        return mContentView;
    }

    private void shareScreenShot() {
        showProgress();
        View view = mContentView.getRootView();
        view.setDrawingCacheEnabled(true);
        Bitmap screenBitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ByteArrayOutputStream screenBytes = new ByteArrayOutputStream();
        screenBitmap.compress(Bitmap.CompressFormat.PNG, 100, screenBytes);
        String screen;
        Intent intent;
        long uniqueId = Utility.generateRandomNumber();
        screen = MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), screenBitmap, "my_certificate_" + uniqueId, null);
        intent = createIntent(screen);
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing MarkSheet result from " + getString(R.string.app_name));
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "my_certificate_" + uniqueId);
        stopProgress();
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private Intent createIntent(String screen) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (!Utility.isEmpty(screen)) {
            intent.setType("image/jpeg");
            try {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(screen));
            } catch (Exception e) {
                Log.e(TAG, "createIntent: ", e);
            }
        }
        return intent;
    }
}
