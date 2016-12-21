package com.teamhex.colorbird;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
//import android.util.Log;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context c;
    private Activity mainAct;
    private Camera.PreviewCallback mCallback;
    @SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera, Camera.PreviewCallback callback) {
        super(context);
        mCamera = camera;
        c = context;
        mainAct = (Activity)context;
        mCallback = callback;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // Deprecated setting, but required on Android versions prior to 3.0
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        	mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera)
    {
    	mCamera = camera;
    }

    public Camera getCamera()
    {
    	return mCamera;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // This case can actually happen if the user opens and closes the camera too frequently.
            // The problem is that we cannot really prevent this from happening as the user can easily
            // get into a chain of activites and tries to escape using the back button.
            // The most sensible solution would be to quit the entire EPostcard flow once the picture is sent.
            mCamera = Camera.open();
        } catch(Exception e) {
            Log.v("SNAIL", "surfaceCreated ERROR", e);
            //finish();
            return;
        }

        Log.v("SNAIL", "creating surface");
        //Surface.setOrientation(Display.DEFAULT_DISPLAY,Surface.ROTATION_90);
        Parameters p = mCamera.getParameters();
        setCameraDisplayOrientation(mainAct, 0, mCamera);

        Camera.Size bestPreviewSize = determineBestPreviewSize(p);
        Camera.Size bestPictureSize = determineBestPictureSize(p);

        Log.v("CAMPREV_WIDTH", Integer.toString(determineBestPreviewSize(p).width));
        Log.v("CAMPREV_HEIGHT", Integer.toString(determineBestPreviewSize(p).height));
        Log.v("CAMPIC_WIDTH", Integer.toString(determineBestPictureSize(p).width));
        Log.v("CAMPIC_HEIGHT", Integer.toString(determineBestPictureSize(p).height));

        p.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        p.setPictureSize(bestPictureSize.width, bestPictureSize.height);
        //Log.v("CAMPREV_WIDTH", Integer.toString(p.getPreviewSize().width));
        //Log.v("CAMPREV_HEIGHT", Integer.toString(p.getPreviewSize().height));


        //Camera.Size s = p.getSupportedPreviewSizes().get(1);
        //p.setPreviewSize( s.width, s.height );

        //p.setPictureSize(p.getSupportedPictureSizes().get(0).width, p.getSupportedPictureSizes().get(0).height);
        //Log.v("CAMPIC_WIDTH", Integer.toString(p.getSupportedPictureSizes().get(0).width));
        //Log.v("CAMPIC_HEIGHT", Integer.toString(p.getSupportedPictureSizes().get(0).height));
        //p.setPictureSize(4160, 3120);
        //p.setPictureSize(512, 384);
        //p.setPictureSize(1920, 1080);
        //p.set("orientation", "portrait");

        // set other parameters ..
        //mCamera.getParameters().setRotation(90);
        p.set("orientation", "landscape");
        p.setRotation(0);

        //p.setPictureFormat(PixelFormat.JPEG);
        //p.set("flash-mode", "auto");
        try {
            mCamera.setParameters(p);
        } catch (Throwable whysnailswhy) {
            Log.v("SNAIL", "set parameters error", whysnailswhy);
        }

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Throwable ignored) {
            Log.e("TeamHex", "set preview error.", ignored);
        }
        //Log.v("SNAIL", "no set preview error");
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.v("SNAIL", "surface is CHANGING");
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

    	// If the preview surface doesn't exist, we're done here
        if (mHolder.getSurface() == null)
          return;

        // Make sure to stop the preview before making changes
        try { mCamera.stopPreview(); } catch (Exception e){ Log.v("SNAIL", "surface change preview stop failed");}

        // Update the orientation (landscape / horizontal)

        	Camera.Parameters p = mCamera.getParameters();
        	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                p.set("orientation", "landscape");
                p.set("rotation",0);
                Log.v("SNAIL", "portrait rotation 90");
               // mCamera.setDisplayOrientation(90);
            }
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                p.set("orientation", "landscape");
                p.set("rotation", 180);
                Log.v("SNAIL", "landscape rotation 90");
               // mCamera.setDisplayOrientation(90);
            }


        // Set the preview display with the new orientation settings
        try {
            mCamera.setParameters(p);
        	//mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(mCallback);
            mCamera.setPreviewDisplay(mHolder);
            //mCamera.setPreviewCallback(null);
            //mCamera.setPreviewCallback((Camera.PreviewCallback) mainAct);
            mCamera.startPreview();
        } catch (Exception e){
            Log.i("TeamHex", "Error starting camera preview: " + e.getMessage());
        }
    }


    //Modified from Android documentation: http://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation(),
        	degrees = 0,
        	result;
        switch (rotation) {
            case Surface.ROTATION_0:   degrees = 0;   break;
            case Surface.ROTATION_90:  degrees = 90;  break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        // The front-facing camera compensates for the mirror
        if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        Camera.Parameters params = camera.getParameters();
        params.setRotation(result);
        camera.setParameters(params);
        camera.setDisplayOrientation(result);
    }

    public static Camera.Size determineBestPreviewSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        return determineBestSize(sizes);
    }

    public static Camera.Size determineBestPictureSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        return determineBestSize(sizes);
    }

    protected static Camera.Size determineBestSize(List<Camera.Size> sizes) {
        Camera.Size bestSize = null;
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long availableMemory = Runtime.getRuntime().maxMemory() - used;
        for (Camera.Size currentSize : sizes) {
            int newArea = currentSize.width * currentSize.height;
            long neededMemory = newArea * 4 * 4; // newArea * 4 Bytes/pixel * 4 needed copies of the bitmap (for safety :) )
            boolean isDesiredRatio = (currentSize.width / 4) == (currentSize.height / 3);
            boolean isBetterSize = (bestSize == null || currentSize.width > bestSize.width);
            boolean isSafe = neededMemory < availableMemory;
            if (isDesiredRatio && isBetterSize && isSafe) {
                bestSize = currentSize;
            }
        }
        if (bestSize == null) {
            return sizes.get(0);
        }
        return bestSize;
    }
}
