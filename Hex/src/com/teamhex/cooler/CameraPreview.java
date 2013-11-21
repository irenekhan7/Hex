package com.teamhex.cooler;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
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
            //finish();
            return;
        }

        //Surface.setOrientation(Display.DEFAULT_DISPLAY,Surface.ROTATION_90);
        Parameters p = mCamera.getParameters();
        p.setPictureSize(512, 384);

        p.set("orientation", "portrait");
        
        // set other parameters ..
        mCamera.getParameters().setRotation(90);
        
        Camera.Size s = p.getSupportedPreviewSizes().get(0);
        p.setPreviewSize( s.width, s.height );

        p.setPictureFormat(PixelFormat.JPEG);
        p.set("flash-mode", "auto");
        mCamera.setParameters(p);

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Throwable ignored) {
            Log.e("TeamHex", "set preview error.", ignored);
        }
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
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

    	// If the preview surface doesn't exist, we're done here
        if (mHolder.getSurface() == null)
          return;

        // Make sure to stop the preview before making changes
        try { mCamera.stopPreview(); } catch (Exception e){}
        
        // Update the orientation (landscape / horizontal)
        setCameraDisplayOrientation((Activity) this.getContext(), 0, mCamera);
        
        // Set the preview display with the new orientation settings
        try { 
        	mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewDisplay(mHolder);
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
}
