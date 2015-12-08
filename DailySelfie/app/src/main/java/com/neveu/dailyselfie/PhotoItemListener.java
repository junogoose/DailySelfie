package com.neveu.dailyselfie;

/**
 * Created by neveu on 7/14/2015.
   Propagates user's item touch event to an implementing class.
 */
public interface PhotoItemListener {
    public void onPhotoSelected(String fileName);
}
