package com.neveu.dailyselfie;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/* Fragment which displays the full-size photo from the
    selected thumbnail in PhotoListFragment. */

public class PhotoFragment extends Fragment {

    ImageView mFullPhotoView;
    Context mContext;

    public PhotoFragment() {
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mContext = activity;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

	/* Initialize new view for the image. Set its bitmap, using the filename supplied
		by the argument Bundle. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFullPhotoView = new ImageView(mContext);
        mFullPhotoView.setImageBitmap(BitmapFactory.decodeFile(getArguments().getString(DailySelfieActivity.PHOTO_ARG_TAG)));
        return mFullPhotoView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        /* Disable take-photo and delete-all-photos menu items */
        menu.findItem(R.id.action_deleteAll).setEnabled(false);
        menu.findItem(R.id.action_camera).setVisible(false);
    }

}
