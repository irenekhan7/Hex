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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity implements PreviewCallback{

	private static final String TAG = "TeamHex";
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int ACTIVITY_SELECT_IMAGE = 3;
	public static final int ACTIVITY_SELECTED_REGION = 1;
    	int sampleSize = 100;
	
	private Camera mCamera;
	private Parameters mParameters;
	private CameraPreview mPreview;
	private Bitmap mBitmap = null;
	int[] pixels = null;
	
	FrameLayout preview;
	
	//Used to prevent analysis from being started twice
	Boolean takingPhoto = false;
	
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
                	if(!takingPhoto)
                	{
	                    // Get an image from the camera
	                	Log.i("TeamHex", "Capture button clicked; storing the picture as a bitmap");
	                	mCamera.setOneShotPreviewCallback(MainActivity.this);
	                    //mCamera.takePicture(null, null, mPicture);
	                    //takingPhoto = true;
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
                	Log.i("TeamHex", "Opening the library activity");
                	Intent i = new Intent(MainActivity.this, PaletteLibraryActivity.class);
                    startActivity(i);
                }
            }
        );
    }
    
    
    //Pulls data from the camera preview and uses that instead of taking an image
    @Override  
    public void onPreviewFrame(byte[] data, Camera camera) {  
    	mParameters = mCamera.getParameters();
    	pixels = new int[mParameters.getPreviewSize().width * mParameters.getPreviewSize().height];
    	
    	int width = mParameters.getPreviewSize().width;
    	int height = mParameters.getPreviewSize().height;
    
        YUVtoRGBUtility.decodeYUV420SP(pixels, data, width,  height); 
        
        int[] newPixels; 
        
        //If rotated 90 ccw, change pixels.
        //TO-DO: Add support for 90 degrees clockwise
        if(getResources().getConfiguration().orientation == 1)
        {
        	newPixels = new int[mParameters.getPreviewSize().width * mParameters.getPreviewSize().height];
	        for (int y = 0; y < height; y++) {
	            for (int x = 0; x < width; x++)
	                newPixels[x * height + height - y - 1] = pixels[x + y * width];
	        }
	        
	        //Fancy swap
	        width = width + height;
	        height = width - height;
	        width = width - height;
        }
        else
        {
        	newPixels = pixels;
        }
        
        mBitmap = Bitmap.createBitmap(newPixels, width,  height, Bitmap.Config.ARGB_8888);
        analyze();
        
    }
    
    private void analyze()
    {
    	Log.i("TeamHex", "Launching analyze activity");
    	ByteArrayOutputStream bs = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
		Intent d = new Intent(MainActivity.this, DrawImageActivity.class);
        d.putExtra("byteArray", bs.toByteArray()); // Could potentially be saved to the phone and passed as uri instead.
        
        startActivity(d);
    	
    }
    
    @Override
    protected void onPause() {
        super.onPause(); 
        preview.removeView(mPreview); // Remove the preview from the view to prevent crash
        releaseCamera();              // release the camera immediately on pause event
    }
    
    @Override
    protected void onResume() {
        super.onResume();   
        takingPhoto = false;
        mCamera = getCameraInstance();
        mPreview.setCamera(mCamera);
        // Only add the view to mPreview if the camera exists
        if(mCamera != null)
        {
        	preview.addView(mPreview); // Add the preview back into the view
        }
        mParameters = mCamera.getParameters();
    }

    // Release the camera for other applications
    private void releaseCamera(){
    	if(mCamera == null) 
    		return;
        mCamera.release();        
        mCamera = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	
    	// Potentially adds an item to more modern Android menu interfaces. Commented as it prevents compilation.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public static Camera getCameraInstance(){
        // Attempt to get a Camera instance
        try {
            return Camera.open(); 
        }
        // Camera is not available (in use or does not exist)
        catch (Exception e){
        	return null;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	
    	// If the data is null, there is nothing to do so just return.
    	if(data == null)
    	{
    		return;
    	}
    	
    	if(data.hasExtra("subBitmap")) {
    		Log.i("TeamHex", "Found a subBitmap from activity result. Apparently that makes me a squirrel.");
            mBitmap = BitmapFactory.decodeByteArray(
                    data.getByteArrayExtra("subBitmap"), 0, data
                            .getByteArrayExtra("subBitmap").length); 
        }
    	else { 
    		Log.i("TeamHex", "Did not found a subBitmap from activity result. Apparently that makes me not a squirrel.");
    	}
    	
    	if(requestCode == ACTIVITY_SELECT_IMAGE)
	    {
	        if(resultCode == RESULT_OK){  
	            Uri selectedImage = data.getData();
	            try {
					mBitmap = getThumbnail(selectedImage);
					analyze();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	            
	        }
	    }
    	if(requestCode == ACTIVITY_SELECTED_REGION)
    	{
	    	Log.i("TeamHex", "Analyze button clicked; running colorAlgorithm on mBitmap");
	    	
	    	pixels = data.getIntArrayExtra("com.teamhex.cooler.polygonPixels");
	    	
	    	if(resultCode == 1000)
	    	{
	    		Log.i("TeamHex", "Successful result (1000)");
	    	 }
	    	else 
	    	{ 
				 Log.i("TeamHex", "Unsuccessful result (" + Integer.toString(resultCode) + ")");
				 System.exit(1);
	    	}  
		    
	    }
	    else if(resultCode == RESULT_OK)
		{
		    pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
		    mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());
		    Log.i("TeamHex", "Pixels received with RESULT_OK");
		}    	

	    
    }
    	
    // Handle media storage once picture is captured
    private PictureCallback mPicture = new PictureCallback() 
    {
 
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
    		// Grab the picture file from MEDIA_TYPE_IMAGE
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
            
            //Analyze the picture
            analyze();
        }
        
        // Create a File for saving an image or video
        private File getOutputMediaFile(int type) {
        	// Check if an SD card is mounted
        	if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        	{
	        	// File location : DDMS -> /mnt/sdcard/Pictures/TeamHex
	            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	                      Environment.DIRECTORY_PICTURES), "TeamHex");
	
	            // Create the storage directory if it does not exist
	            if(!mediaStorageDir.exists()) {
	                if(!mediaStorageDir.mkdirs()){
	                    Log.d("TeamHex", "Failed to create storage directory directory");
	                    return null;
	                }
	            }

	            // Create a media file name
	            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new Date());
	            
	            // Depending on the media type, create a new file
	            String filename = mediaStorageDir.getPath() + File.separator;
	            if(type == MEDIA_TYPE_IMAGE)
	            	filename += "IMG_" + timeStamp + ".jpg";
	            else if(type == MEDIA_TYPE_VIDEO)
	            	filename += "VID_" + timeStamp + ".mp4";
	            else return null; // Unknown files get nothing
	            
	            // Return an actual file under that name
	            return new File(filename);
        	}
            return null;
        } 
        
    };
    
    // Gets a thumbnail in Bitmap form, given a URI
    // http://stackoverflow.com/questions/3879992/get-bitmap-from-an-uri-android
    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
    	// Open a stream to the URI
        InputStream input = this.getContentResolver().openInputStream(uri);

        // Get a new Options factory for just the bounds, setting some variables
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither           = true; 					// (optional)
        onlyBoundsOptions.inPreferredConfig  = Bitmap.Config.ARGB_8888; // (optional)
        
        // Decode the URI's input stream into the options factory
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        
        // If the sizes are invalid, it failed - return null
        if(onlyBoundsOptions.outWidth < 0 || onlyBoundsOptions.outHeight < 0)
            return null;
        
        // Now that sizing is known, create a factory for the full bitmap
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize      = calculateInSampleSize(onlyBoundsOptions, sampleSize, sampleSize);
        bitmapOptions.inDither          = true;               	   // (optional)
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888; // (optional)
        
        // Decode the URI's input stream into the options factory
        input = this.getContentResolver().openInputStream(uri);
        // Retrieve the actual bitmap from the factory using the input stream
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        
        return bitmap;
    }
    
    // Returns the sample size given a BitmapFactory options, width, and height
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    // If the given height or given width are smaller than the options'... 
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
}
