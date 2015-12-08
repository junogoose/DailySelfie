package com.neveu.dailyselfie.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by neveu on 7/16/2015.
 */
public class ImageHelper {

    /* Credit: derived from http://developer.android.com/training/camera/photobasics.html */
    public static Bitmap getScaledBitmap(String path, int targetWidth, int targetHeight) {
        // Get the dimensions of the View
//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetWidth, photoH/targetHeight);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(path, bmOptions);
//        imageView.setImageBitmap(bitmap);
    }

        public static void generateThumbName() {

        }

    public static void saveBitmap(Bitmap bm, String path)
    {
        try {
            FileOutputStream fos = new FileOutputStream(new File(path), false);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();

            fos.write(b, 0, b.length);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String createThumbFileName(String fName)
    {
        return fName.substring(0, fName.indexOf(".jpg"))
                + "_thumb" + fName.substring(fName.indexOf(".jpg"));
    }

}
