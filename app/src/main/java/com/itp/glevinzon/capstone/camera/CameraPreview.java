package com.itp.glevinzon.capstone.camera;

/**
 * Created by Glevinzon on 5/6/2017.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

private static final String TAG = "CameraPreview";

private Camera mCamera;
private Handler mAutoFocusHandler;
private boolean mPreviewing = true;
private boolean mAutoFocus = true;
private boolean mSurfaceCreated = false;

public CameraPreview(Context context, Camera camera) {
        super(context);
        init(camera);
        }

public void init(Camera camera) {
        setCamera(camera);
        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

public void setCamera(Camera camera) {
        mCamera = camera;
        }

@Override
public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceCreated = true;
        }

@Override
public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if(surfaceHolder.getSurface() == null) {
        return;
        }
        stopCameraPreview();
        showCameraPreview();
        }

@Override
public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceCreated = false;
        stopCameraPreview();
        }

public void showCameraPreview() {
        if(mCamera != null) {
        try {
        getHolder().addCallback(this);
        mPreviewing = true;
        setupCameraParameters();
        mCamera.setPreviewDisplay(getHolder());
        mCamera.setDisplayOrientation(getDisplayOrientation());

        Log.d("Flag","camera startPreview");
        mCamera.startPreview();

        } catch (Exception e) {
        Log.e(TAG, e.toString(), e);
        }
        }
        }

public void stopCameraPreview() {
        if(mCamera != null) {
        try {
        mPreviewing = false;
        getHolder().removeCallback(this);
//                mCamera.cancelAutoFocus();
        mCamera.setOneShotPreviewCallback(null);
        mCamera.stopPreview();
        } catch(Exception e) {
        Log.e(TAG, e.toString(), e);
        }
        }
        }

public void setupCameraParameters() {
        Camera.Size optimalSize = getOptimalPreviewSize();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        parameters.setFocusMode(parameters.FOCUS_MODE_CONTINUOUS_PICTURE);////FOCUS_MODE_AUTO
        mCamera.setParameters(parameters);
        adjustViewSize(optimalSize);
        }

private void adjustViewSize(Camera.Size cameraSize) {
        Point previewSize = convertSizeToLandscapeOrientation(new Point(getWidth(), getHeight()));
        float cameraRatio = ((float) cameraSize.width) / cameraSize.height;
        float screenRatio = ((float) previewSize.x) / previewSize.y;

        if (screenRatio > cameraRatio) {
        setViewSize((int) (previewSize.y * cameraRatio), previewSize.y);
        } else {
        setViewSize(previewSize.x, (int) (previewSize.x / cameraRatio));
        }
        }

private Point convertSizeToLandscapeOrientation(Point size) {
        if (getDisplayOrientation() % 180 == 0) {
        return size;
        } else {
        return new Point(size.y, size.x);
        }
        }

private void setViewSize(int width, int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (getDisplayOrientation() % 180 == 0) {
        layoutParams.width = width;
        layoutParams.height = height;
        } else {
        layoutParams.width = height;
        layoutParams.height = width;
        }
        setLayoutParams(layoutParams);
        }

public int getDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
        case Surface.ROTATION_0: degrees = 0; break;
        case Surface.ROTATION_90: degrees = 90; break;
        case Surface.ROTATION_180: degrees = 180; break;
        case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        result = (info.orientation + degrees) % 360;
        result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
        result = (info.orientation - degrees + 360) % 360;
        }
        return result;
        }

private Camera.Size getOptimalPreviewSize() {
        if(mCamera == null) {
        return null;
        }

        List<Camera.Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
        int w = getWidth();
        int h = getHeight();
        if (DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
        int portraitWidth = h;
        h = w;
        w = portraitWidth;
        }

final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
        double ratio = (double) size.width / size.height;
        if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
        if (Math.abs(size.height - targetHeight) < minDiff) {
        optimalSize = size;
        minDiff = Math.abs(size.height - targetHeight);
        }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
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
