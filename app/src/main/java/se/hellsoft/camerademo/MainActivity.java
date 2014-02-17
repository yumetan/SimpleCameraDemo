package se.hellsoft.camerademo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final int CAPTURE_IMAGE_REQUEST = 1001;
    private static final int CAPTURE_VIDEO_REQUEST = 2002;
    private Uri mOutputFile;
    private MediaCaptureFragment mMediaCaptureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mMediaCaptureFragment = new MediaCaptureFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mMediaCaptureFragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream bitmapStream = getContentResolver()
                            .openInputStream(mOutputFile);
                    Bitmap bitmap = BitmapFactory.decodeStream(bitmapStream);
                    mMediaCaptureFragment.setBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Failed to read photo from "
                            + mOutputFile, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this,
                        "Failed to capture image!",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAPTURE_VIDEO_REQUEST) {
            if (resultCode == RESULT_OK) {
                mMediaCaptureFragment.setVideoUri(mOutputFile);
            } else {
                Toast.makeText(this,
                        "Failed to capture video!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onTakePictureClicked(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri outputFile = Uri.fromFile(Environment
                .getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_DCIM));
        outputFile = Uri.withAppendedPath(outputFile, getNextImageName());
        Log.d(TAG, "Storing photo in: " + outputFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFile);
        mOutputFile = outputFile;

        startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
    }

    private String getNextImageName() {
        return "SimpleCameraDemo-" + System.currentTimeMillis() + ".jpg";
    }

    private String getNextVideoName() {
        return "SimpleVideoDemo-" + System.currentTimeMillis() + ".mp4";
    }

    public void onTakeVideoClicked(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri outputFile = Uri.fromFile(Environment
                .getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_DCIM));
        outputFile = Uri.withAppendedPath(outputFile, getNextImageName());
        Log.d(TAG, "Storing video in: " + outputFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFile);
        mOutputFile = outputFile;

        startActivityForResult(cameraIntent, CAPTURE_VIDEO_REQUEST);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MediaCaptureFragment extends Fragment {
        private Bitmap mBitmap;
        private Uri mVideoUri;

        public MediaCaptureFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            ((ImageView) rootView.findViewById(R.id.last_photo)).setImageBitmap(mBitmap);
            VideoView videoView = ((VideoView) rootView.findViewById(R.id.last_video));
            MediaController mediaController = (MediaController) rootView.findViewById(R.id.media_controller);
            videoView.setMediaController(mediaController);
            if (mVideoUri != null) {
                videoView.setVideoURI(mVideoUri);
            }
            return rootView;
        }

        public void setBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
/*
            Activity activity = getActivity();
            if (activity != null) {
                ImageView imageView = ((ImageView) activity
                        .findViewById(R.id.last_photo));
                if (imageView != null) {
                    imageView.setImageBitmap(mBitmap);
                }
            }
*/
        }

        public void setVideoUri(Uri videoUri) {
            mVideoUri = videoUri;
            Activity activity = getActivity();
            if (activity != null) {
                VideoView videoView = ((VideoView) activity.findViewById(R.id.last_video));
                if (videoView != null) {
                    videoView.setVideoURI(mVideoUri);
                }
            }
        }

    }

}
