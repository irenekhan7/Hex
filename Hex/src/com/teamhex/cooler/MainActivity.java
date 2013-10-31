package com.teamhex.cooler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.teamhex.cooler.DrawImageActivity;
import com.teamhex.cooler.CameraPreview;
import com.teamhex.cooler.R;
import com.teamhex.cooler.Storage.Activities.PaletteLibraryActivity;
import com.teamhex.cooler.Storage.Activities.PaletteSaveActivity;
import com.teamhex.cooler.Storage.Classes.PaletteRecord;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

	private static final String TAG = "TeamHex";
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int ACTIVITY_SELECT_IMAGE = 3;
	
	private Camera mCamera;
	private CameraPreview mPreview;
	private Bitmap mBitmap = null;
	
	FrameLayout preview;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.i("TeamHex", "MainActivity now running onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Create preview and set as content of activity
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);

        // Event listener: Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                	Log.i("TeamHex", "Capture button clicked; storing the picture as a bitmap");
                    mCamera.takePicture(null, null, mPicture);
                }
            }
        );
        
       // Event listener: Analyze button
        Button analyzeButton = (Button) findViewById(R.id.button_analyze);
        analyzeButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Analyze image
                	if (mBitmap != null)
                	{
                		ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                		Intent d = new Intent(MainActivity.this, DrawImageActivity.class);
                        d.putExtra("byteArray", bs.toByteArray());
                    	startActivityForResult(d, 1);
                	}
                }
            }
        );
        

        // Event listener: Import button
        Button importButton = (Button) findViewById(R.id.button_import);
        importButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Import
                	//Potentially causes crash
                	Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                		startActivityForResult(i, ACTIVITY_SELECT_IMAGE); 

                }
            }
        );
        
        // Event listener: Library button
        Button libraryButton = (Button) findViewById(R.id.button_open_library);
        libraryButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Open library 
                	//System.out.println("OPEN LIBRARY");
                	Log.i("TeamHex", "Opening the library activity");
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
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    	if (getIntent().hasExtra("subBitmap")) {

            mBitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("subBitmap"), 0, getIntent()
                            .getByteArrayExtra("subBitmap").length); 
        }
    	
    	if(requestCode == ACTIVITY_SELECT_IMAGE)
	    {
	        if(resultCode == RESULT_OK){  
	            Uri selectedImage = data.getData();
	            try {
					mBitmap = getThumbnail(selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	        }
	    }
    	if(requestCode == 1)
    	{
	    	Log.i("TeamHex", "Analyze button clicked; running colorAlgorithm on mBitmap");
	    	int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
	    	mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());
			
	    	
	    	
	    	int[] colors = ColorPaletteGenerator.colorAlgorithm(pixels, 5);
	    	System.out.println("ANALYZE");
	    	
	    	// Store the output from colors[] into a new PaletteRecord
	    	PaletteRecord palette = new PaletteRecord();
	    	palette.setName("A really random color scheme");
	    	for (int i = 0; i < 5; i++)
	    		palette.addColor(colors[i]);
	    	
	    	System.out.println("ANALYZED");
	    	
	    	// Get auto-generated names for the palette
	    	Log.i("TeamHex", "Using the X11Helper to generate names for the palette");
	    	palette.setX11Names(mX11Helper);
	    	
	    	// Go to the PaletteSaveActivity to save the palette into the library
	    	Intent intent_save = new Intent(MainActivity.this, PaletteSaveActivity.class);
	    	intent_save.putExtra("palette", palette);
	    	startActivity(intent_save);
	    	System.out.println("SAVED");
    	}
    }
    	

    int sampleSize = 100;
    // Handle media storage once picture is captured
    private PictureCallback mPicture = new PictureCallback() 
    {
 
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
    		// Grab the pitcure file from MEDIA_TYPE_IMAGE
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            
            // If it's null, complain and stop
            if(pictureFile == null){
                Log.d(TAG, "For whatever reason, the pictureFile is null.");
                return;
            }

            // Otherwise attempt to stream the data to an output file
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            
         // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, sampleSize, sampleSize);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            mBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), options);
        }
        
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
    

    //http://stackoverflow.com/questions/3879992/get-bitmap-from-an-uri-android
    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = calculateInSampleSize(onlyBoundsOptions, sampleSize, sampleSize);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }
    
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
    }

    // Helps PaletteRecords generate names for their colors
    private X11Helper mX11Helper = new X11Helper();
}
