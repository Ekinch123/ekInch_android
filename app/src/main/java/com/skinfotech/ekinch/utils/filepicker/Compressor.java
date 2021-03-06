package com.skinfotech.ekinch.utils.filepicker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;


import com.skinfotech.ekinch.utils.EkInchConstant;

import java.io.File;
import java.io.IOException;

/**
 * Created by gurmail on 11/01/19.
 *
 * @author gurmail
 */
public class Compressor implements EkInchConstant {
    //max width and height values of the compressed image is taken as 612x816
    private float maxWidth = 612.0f;
    private float maxHeight = 816.0f;
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    private int quality = 80;
    private String destinationDirectoryPath;
    private CompressorListener compressorListener;

    public Compressor() {
        destinationDirectoryPath = Util.getDirectoryPath(FOLDER_TYPE.get(IMAGE_FOLDER));
    }
    public Compressor setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public Compressor setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public Compressor setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
        return this;
    }

    public Compressor setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    public Compressor setListener(CompressorListener compressorListener) {
        this.compressorListener = compressorListener;
        return this;
    }

    public Compressor setDestinationDirectoryPath(String destinationDirectoryPath) {
        this.destinationDirectoryPath = destinationDirectoryPath;
        return this;
    }

//    public void compressToFile(File imageFile) throws IOException {
//        compressToFile(imageFile, imageFile.getName());
//    }


}
