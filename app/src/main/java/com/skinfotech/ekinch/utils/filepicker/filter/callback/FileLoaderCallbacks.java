package com.skinfotech.ekinch.utils.filepicker.filter.callback;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Files.FileColumns.MIME_TYPE;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.Images.ImageColumns.ORIENTATION;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.TITLE;
import static android.provider.MediaStore.Video.VideoColumns.DURATION;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;


import com.skinfotech.ekinch.utils.filepicker.Util;
import com.skinfotech.ekinch.utils.filepicker.filter.entity.Directory;
import com.skinfotech.ekinch.utils.filepicker.filter.entity.VideoFile;
import com.skinfotech.ekinch.utils.filepicker.filter.loader.VideoLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vincent Woo
 * Date: 2016/10/11
 * Time: 11:04
 */

public class FileLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_FILE = 3;

    private WeakReference<Context> context;
    private FilterResultCallback resultCallback;

    private int mType = TYPE_IMAGE;
    private String[] mSuffixArgs;
    private CursorLoader mLoader;
    private String mSuffixRegex;

    public FileLoaderCallbacks(Context context, FilterResultCallback resultCallback, int type) {
        this(context, resultCallback, type, null);
    }

    public FileLoaderCallbacks(Context context, FilterResultCallback resultCallback, int type, String[] suffixArgs) {
        this.context = new WeakReference<>(context);
        this.resultCallback = resultCallback;
        this.mType = type;
        this.mSuffixArgs = suffixArgs;
        if (suffixArgs != null && suffixArgs.length > 0) {
            mSuffixRegex = obtainSuffixRegex(suffixArgs);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (mType) {

            case TYPE_VIDEO:
                mLoader = new VideoLoader(context.get());
                break;

        }

        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;
        switch (mType) {

            case TYPE_VIDEO:
                onVideoResult(data);
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private void onVideoResult(final Cursor data) {
        List<Directory<VideoFile>> directories = new ArrayList<>();

        if (data.getPosition() != -1) {
            data.moveToPosition(-1);
        }

        while (data.moveToNext()) {
            //Create a File instance
            VideoFile video = new VideoFile();
            video.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
            video.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
            video.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
            video.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
            video.setBucketId(data.getString(data.getColumnIndexOrThrow(BUCKET_ID)));
            video.setBucketName(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
            video.setDate(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));

            video.setDuration(data.getLong(data.getColumnIndexOrThrow(DURATION)));

            //Create a Directory
            Directory<VideoFile> directory = new Directory<>();
            directory.setId(video.getBucketId());
            directory.setName(video.getBucketName());
            directory.setPath(Util.extractPathWithoutSeparator(video.getPath()));

            if (!directories.contains(directory)) {
                directory.addFile(video);
                directories.add(directory);
            } else {
                directories.get(directories.indexOf(directory)).addFile(video);
            }
        }

        if (resultCallback != null) {
            resultCallback.onResult(directories);
        }
    }





    private String obtainSuffixRegex(String[] suffixes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < suffixes.length ; i++) {
            if (i ==0) {
                builder.append(suffixes[i].replace(".", ""));
            } else {
                builder.append("|\\.");
                builder.append(suffixes[i].replace(".", ""));
            }
        }
        return ".+(\\." + builder.toString() + ")$";
    }
}
