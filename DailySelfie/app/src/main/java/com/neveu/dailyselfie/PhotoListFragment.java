package com.neveu.dailyselfie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.neveu.dailyselfie.utils.ImageHelper;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by neveu on 7/14/2015.
 * Fragment to display selectable list of photos.
 * Uses PhotoListAdapter for modification of actual PhotoItem's.
 */
public class PhotoListFragment extends ListFragment {

    private PhotoListAdapter mAdapter;
    private PhotoItemListener mListener;
    private File mStorageDirectory;
    private Context mContext;

    //Note: called on reconfig whether or not setRetainInstance(true).
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity;
        mListener = (PhotoItemListener) activity;
        setHasOptionsMenu(true);
    }

    //Note: NOT called on reconfig when setRetainInstance(true). Initialize adapter and list items here.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStorageDirectory = DailySelfieActivity.mStorageDir;
        setRetainInstance(true);
        mAdapter = new PhotoListAdapter(mContext);
        setListAdapter(mAdapter);

        loadPhotoList();

        Toast.makeText(mContext, getString(R.string.instructions_short), Toast.LENGTH_LONG).show();
    }

    //NOTE: For some reason, called when fragment is resumed. why?
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText("No photos to display...");

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getActivity())
                        .setCancelable(true)
                        .setMessage("Delete selected photo?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePhotoAtIndex(position);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();

                return true;
            }
        });
    }


	/* Inform hosting Activity (implementing PhotoItemListener) of user's selection. */
    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        if (null != mListener) {
            mListener.onPhotoSelected(mAdapter.getItem(position).getFullPath());
        }
    }


    public void addPhotoItem(File file)
    {
        mAdapter.add(new PhotoItem(file));
    }

    public void deleteAllPhotos()
    {
		/* Delete the photos from file system. */
        for(File f : mStorageDirectory.listFiles())
        {
            f.delete();
        }

		/* Remove photos from the PhotoItem collection. */
        mAdapter.removeAll();
    }

    private void deletePhotoAtIndex(int i)
    {
        mAdapter.getItem(i).getFile().delete();
        mAdapter.removeAtIndex(i);
    }

	/* 	Retrieves any thumbnail images from file system. 
		Populates / repopulates the PhotoListAdapter's PhotoItem collection. */
    private void loadPhotoList() {
        mAdapter.removeAll();

        File[] photoFiles = mStorageDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return !filename.contains("_thumb");
            }
        });

        for(File f : photoFiles)
        {
            Bitmap thumb = BitmapFactory.decodeFile(ImageHelper.createThumbFileName(f.getAbsolutePath()));
            mAdapter.add(new PhotoItem(thumb, f));
        }
    }

	/* 	Called immediately before options menu is displayed.
		Re-enable take-photo and delete-all-photos menu items (disabled in PhotoFragment). */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_deleteAll).setEnabled(true);
        menu.findItem(R.id.action_camera).setVisible(true);
    }

}