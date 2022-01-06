package com.skinfotech.ekinch.utils.filepicker.filter;



import androidx.fragment.app.FragmentActivity;

import com.skinfotech.ekinch.utils.filepicker.filter.callback.FileLoaderCallbacks;
import com.skinfotech.ekinch.utils.filepicker.filter.callback.FilterResultCallback;
import com.skinfotech.ekinch.utils.filepicker.filter.entity.VideoFile;


/**
 * Created by Vincent Woo
 * Date: 2016/10/11
 * Time: 10:19
 */

public class FileFilter {


    public static void getVideos(FragmentActivity activity, FilterResultCallback<VideoFile> callback){
        activity.getSupportLoaderManager().initLoader(1, null,
                new FileLoaderCallbacks(activity, callback, 1));
    }


}
