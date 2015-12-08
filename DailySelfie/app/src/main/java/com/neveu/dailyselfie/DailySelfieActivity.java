package com.neveu.dailyselfie;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DailySelfieActivity extends Activity implements PhotoItemListener {

    private static final String TAG_PHOTOLISTFRAG = "photo_list_fragment";
    private static final String TAG_PHOTOFRAG = "photo_fragment";
    public static final String PHOTO_ARG_TAG = "PHOTO_FILE_NAME";
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final long ALARM_INTERVAL = 3 * 60 * 1000; /* 3 minutes */

    private FragmentManager mFragMgr;
    private PhotoListFragment mPhotoListFragment;
    private PhotoFragment mPhotoFragment;

    public  static File mStorageDir;
    private File mCurrentPhotoFile;

    private AlarmManager mAlarmManager;
    private Intent mAlarmIntent;
    private PendingIntent mAlarmPendingIntent;

    public static CharSequence getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie);

        /* Get reference to device's external photo storage directory. */
        mStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        /* Get Activity's FragmentManager, which is used to swap between
            the two main Views: PhotoListFragment and PhotoFragment. */
        mFragMgr = getFragmentManager();

        /* If onCreate() is being called due to runtime reconfiguration,
            restore the existing (retained) Fragments. */
        if(savedInstanceState != null) {
            restoreState(savedInstanceState);
        }
        else {
            setupDisplay();
        }

        setAlarm();
    }

	/* Place new PhotoListFragment into main layout */
    private void setupDisplay()
    {
        mPhotoListFragment = new PhotoListFragment();
        mFragMgr.beginTransaction().replace(R.id.fragment_container, mPhotoListFragment, TAG_PHOTOLISTFRAG).commit();
    }

	/* 	Sets up a recurring Broadcast, which SelfieAlarmReceiver uses to create
		'reminder' notifications.*/
    private void setAlarm()
    {
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmIntent = new Intent(this, SelfieAlarmReceiver.class);
        mAlarmPendingIntent = PendingIntent.getBroadcast(this, 0, mAlarmIntent, 0);

        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + ALARM_INTERVAL,
                ALARM_INTERVAL, mAlarmPendingIntent);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_selfie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_camera) {
            dispatchTakePictureIntent();
            return true;
        }

        if(id == R.id.action_deleteAll)
        {
            mPhotoListFragment.deleteAllPhotos();
        }

        if(id == R.id.action_cancel_alarm)
        {
            mAlarmManager.cancel(mAlarmPendingIntent);
            Toast.makeText(this, "Reminders canceled", Toast.LENGTH_LONG).show();
        }

        if(id == R.id.action_help) { displayHelp(); }

        return super.onOptionsItemSelected(item);
    }

    private void displayHelp()
    {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage(getString(R.string.instructions_long))
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

    }

    /* Fragment saving/restoring during reconfigurations */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if(mPhotoListFragment != null)
        {
            savedInstanceState.putString(TAG_PHOTOLISTFRAG, mPhotoListFragment.getTag());
        }

        if(mPhotoFragment != null) {
            savedInstanceState.putString(TAG_PHOTOFRAG, mPhotoFragment.getTag());
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    private void restoreState(Bundle savedInstanceState)
    {
        mPhotoListFragment = (PhotoListFragment) mFragMgr.findFragmentByTag(
                savedInstanceState.getString(TAG_PHOTOLISTFRAG));

        mPhotoFragment = (PhotoFragment) mFragMgr.findFragmentByTag(
                savedInstanceState.getString(TAG_PHOTOFRAG));

    }


	/*	Initializes Intent with new file name and launches camera activity.	*/
    private void dispatchTakePictureIntent() {
        //TODO - verify that device's camera is available


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            mCurrentPhotoFile = createImageFile();

            // Continue only if the File was successfully created
            if (mCurrentPhotoFile != null) {
                //NOTE: Setting this disables the thumbnail image creation provided to onActivityResult
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(mCurrentPhotoFile));

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
            else
            {
                Toast.makeText(this, "Problem initializing camera", Toast.LENGTH_LONG).show();
            }
        }
    }

	/* Create a unique image file name */
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "selfie_" + timeStamp + ".jpg";

        try {
            return new File(
                    mStorageDir,
                    imageFileName
            );
        }catch (NullPointerException npe) { return null; }
    }


	/* Called upon return from camera activity. If all went well, mCurrentPhotoFile
		should reference the full-size photo file... which sent to PhotoListFragment
		for processing.	*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            if(mCurrentPhotoFile != null)
            {
                mPhotoListFragment.addPhotoItem(mCurrentPhotoFile);

            }
        }
    }


    //PhotoItemListener implementation
    @Override
    public void onPhotoSelected(String fileName) {

        mPhotoFragment = new PhotoFragment();

		/*  Photo cannot be assigned to new PhotoFragment until its view
			is created. So, we send it as an argument. */
        Bundle argBundle = new Bundle();
        argBundle.putString(PHOTO_ARG_TAG, fileName);
        mPhotoFragment.setArguments(argBundle);

        /* Swap PhotoListFragment with PhotoFragment */
        FragmentTransaction ft = mFragMgr.beginTransaction();
        ft.replace(R.id.fragment_container, mPhotoFragment, TAG_PHOTOFRAG);
        ft.addToBackStack(null);
        ft.commit();

    }

}
