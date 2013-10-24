package com.teamhex.cooler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.teamhex.cooler.CameraPreview;
import com.teamhex.cooler.R;
import com.teamhex.cooler.Storage.Activities.PaletteLibraryActivity;
import com.teamhex.cooler.Storage.Activities.PaletteSaveActivity;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	private Bitmap mBitmap = null;
	private static final String TAG = "ACTIVITY";
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	FrameLayout preview;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

        //Camera is now acquired in onResume
        
        //Create preview and set as content of activity
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);

        //Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                	System.out.println("CAPTURE CLICKED\n");
                    mCamera.takePicture(null, null, mPicture);
                }
            }
        );
        
       
        
      //Analyze button
        Button analyzeButton = (Button) findViewById(R.id.button_analyze);
        analyzeButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Analyze image
                	if (mBitmap != null)
                	{
                		int[] colors = ColorPaletteGenerator.colorAlgorithm(mBitmap, 5);
                    	System.out.println("ANALYZE");
                    	PaletteRecord palette = new PaletteRecord();
                    	palette.setName("A really random color scheme");
                    	for (int i = 0; i < 5; i++)
                    	{
                    		palette.addColor(colors[i]);
                    	}
                    	Intent i = new Intent(MainActivity.this, PaletteSaveActivity.class);
                    	i.putExtra("palette", palette);
                    	startActivity(i);
                	}
                }
            }
        );
        
    // Import button listener
        Button importButton = (Button) findViewById(R.id.button_import);
        importButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Import
                	System.out.println("IMPORT AN IMAGE");
                }
            }
        );
        
    //Open library button listener
        Button libraryButton = (Button) findViewById(R.id.button_open_library);
        libraryButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Open library 
                	//System.out.println("OPEN LIBRARY");
                	Intent i = new Intent(MainActivity.this, PaletteLibraryActivity.class);
                    startActivity(i);
                }
            }
        );
    }
    
    @Override
    protected void onPause() {
        super.onPause(); 
        preview.removeView(mPreview); //Remove the preview from the view to prevent crash
        releaseCamera();              // release the camera immediately on pause event
    }
    
    @Override
    protected void onResume() {
        super.onResume();   
        mCamera = getCameraInstance();
        mPreview.setCamera(mCamera);
        if(mCamera != null)
        {
        	preview.addView(mPreview); //Add the preview back into the view
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	
    	//Potentially adds an item to more modern Android menu interfaces. Uncommented as it prevents compilation.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    
    //Handle media storage once picture is captured
    private PictureCallback mPicture = new PictureCallback() 
    {
 
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: " +
                    "ERROR");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            
    		bmOptions.inSampleSize = 4;
    		bmOptions.inPurgeable = true;
    		
            mBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), bmOptions);
        }
        
        
        public static final int MEDIA_TYPE_IMAGE = 1;
        public static final int MEDIA_TYPE_VIDEO = 2;

        /** Create a File for saving an image or video */
        private File getOutputMediaFile(int type){
        	File mediaFile = null;

        	//Check if SD card is mounted
        	if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        	{
        	//File location : DDMS -> /mnt/sdcard/Pictures/MyCameraApp
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                      Environment.DIRECTORY_PICTURES), "MyCameraApp");

            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Log.d("MyCameraApp", "failed to create directory");
                    return null;
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new Date());
            //File mediaFile;
            if (type == MEDIA_TYPE_IMAGE){
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
            } else if(type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "VID_"+ timeStamp + ".mp4");
            } else {
                return null;
            }
        	}
            return mediaFile;
        } 
        
    };
}
