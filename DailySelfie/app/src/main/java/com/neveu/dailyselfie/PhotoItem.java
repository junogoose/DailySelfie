package com.neveu.dailyselfie;

import android.graphics.Bitmap;
import com.neveu.dailyselfie.utils.ImageHelper;

import java.io.File;

/**
 * Created by neveu on 7/14/2015.
 */
public class PhotoItem {
    private Bitmap mThumbnail;
    private File mFile;
    private static final int thumbWidth = 80, thumbHeight = 60;

    public PhotoItem(File file)
    {
        mFile = file;

        //create thumbnail from full-size photo
        mThumbnail = ImageHelper.getScaledBitmap(mFile.getAbsolutePath(), thumbWidth, thumbHeight);
        ImageHelper.saveBitmap(mThumbnail, ImageHelper.createThumbFileName(mFile.getAbsolutePath()));
    }

    public PhotoItem(Bitmap bitmap, File file)
    {
        mThumbnail = bitmap;
        mFile = file;
    }

    public Bitmap getThumbnail()
    {
        return mThumbnail;
    }
    public File getFile() { return mFile; }

    public String getFileName()
    {
        return mFile.getName().substring(0, mFile.getName().indexOf(".jpg"));
    }
    public String getFullPath() { return mFile.getAbsolutePath(); }




}
