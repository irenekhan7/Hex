package com.teamhex.colorbird;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
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
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;

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
        Camera.Parameters p = mCamera.getParameters();
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        for(Camera.Size str: mSupportedPreviewSizes)
            Log.e("SNAIL", str.width + "/" + str.height);
        setCameraDisplayOrientation(mainAct, 0, mCamera);

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
            p.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(p);
            mCamera.setPreviewCallback(mCallback);
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

    //override onMeasure and use getOptimalPreviewSize to address SurfaceView stretch issue
    //Reference: http://stackoverflow.com/questions/19577299/android-camera-preview-stretched?rq=1
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }

        float ratio;
        if(mPreviewSize.height >= mPreviewSize.width)
            ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
        else
            ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

        // One of these methods should be used, second method squishes preview slightly
        setMeasuredDimension(width, (int) (width * ratio));
//        setMeasuredDimension((int) (width * ratio), height);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }
}
