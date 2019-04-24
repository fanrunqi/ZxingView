package cn.leo.produce.camera;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.util.List;

import cn.leo.produce.config.ZVParams;
import cn.leo.produce.decode.DecoderManager;
import cn.leo.produce.decode.DecoderNextCallBack;
import cn.leo.produce.decode.SourceData;

/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/17 12:00
 */
public class CameraManager implements Camera.PreviewCallback {

    private int degrees;
    private Camera mCamera;
    private int mCameraID;
    private ViewSize viewSize;
    private Camera.Size previewSize;
    private Activity activity;
    private Rect identifyRect;
    private TextureView textureView;
    private boolean isPause;

    public CameraManager(Activity activity,
                         ViewSize viewSize,
                         TextureView textureView) {
        this.activity = activity;
        this.viewSize = viewSize;
        this.textureView = textureView;
        openCamera();
    }

    public CameraManager openCamera() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCamera = Camera.open(i);
                mCameraID = info.facing;
                break;
            }
        }
        if (mCamera == null) {
            mCamera = Camera.open();
            mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        if (mCamera == null) {
            throw new RuntimeException("Unable to open camera");
        }
        mCamera.setDisplayOrientation(calculateCameraPreviewOrientation());
        setPreviewSize();
        return this;
    }


    public void startPreview() {
        Matrix transform = calculateTextureTransform();
        textureView.setTransform(transform);
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(textureView.getSurfaceTexture());
                mCamera.startPreview();
                mCamera.setOneShotPreviewCallback(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected Matrix calculateTextureTransform() {

        float videoWidth, videoHeight;
        if (degrees == 0 || degrees == 180) {
            videoWidth = previewSize.height;
            videoHeight = previewSize.width;
        } else {
            videoWidth = previewSize.width;
            videoHeight = previewSize.height;
        }

        float ratioTexture = viewSize.width / (float) viewSize.height;
        float ratioPreview = videoWidth / videoHeight;
        float scaleX;
        float scaleY;

        if (ratioTexture < ratioPreview) {
            scaleX = ratioPreview / ratioTexture;
            scaleY = 1;
        } else {
            scaleX = 1;
            scaleY = ratioTexture / ratioPreview;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        float scaledWidth = viewSize.width * scaleX;
        float scaledHeight = viewSize.height * scaleY;
        float dx = (viewSize.width - scaledWidth) / 2;
        float dy = (viewSize.height - scaledHeight) / 2;
        matrix.postTranslate(dx, dy);

        int left = (int) ((videoWidth - ZVParams.recognizeRectWidthInPx) / 2f);
        int top = (int) ((videoHeight - ZVParams.recognizeRectHeightInPx) / 2f);
        int right = left + ZVParams.recognizeRectWidthInPx;
        int bottom = top + ZVParams.recognizeRectHeightInPx;
        identifyRect = new Rect(left, top, right, bottom);

        return matrix;
    }


    public void setPreviewSize() {
        Camera.Parameters parameters = mCamera.getParameters();
        previewSize = calculatePerfectSize(parameters);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mCamera.setParameters(parameters);
    }


    private Camera.Size calculatePerfectSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size result = sizes.get(0);
        boolean isMatchHeight = true;
        if (viewSize.width > viewSize.height) {
            isMatchHeight = false;
        }
        for (Camera.Size size : sizes) {
            if (isMatchHeight) {
                if (Math.abs(result.height - viewSize.height)
                        > Math.abs(size.height - viewSize.height)) {
                    result = size;
                }
            } else {
                if (Math.abs(result.width - viewSize.width)
                        > Math.abs(size.width - viewSize.width)) {
                    result = size;
                }
            }
        }
        return result;
    }

    public int calculateCameraPreviewOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraID, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            result = (info.orientation - degrees + 360) % 360;
        } else {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        }
        return result;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mCamera == null || data.length < 1) {
            return;
        }

        int format = 0;
        try {
            format = camera.getParameters().getPreviewFormat();
        } catch (Exception e) {
            shotNextFrame();
        }

        SourceData source = new SourceData(data,
                previewSize.width,
                previewSize.height,
                format,
                degrees);

        DecoderManager.getInstance().decode(source,
                identifyRect,
                new DecoderNextCallBack() {
                    @Override
                    public void requestNextFrame() {
                        if (!isPause) {
                            shotNextFrame();
                        }
                    }
                });
    }

    private void shotNextFrame() {
        if (mCamera != null) {
            mCamera.addCallbackBuffer(null);
            mCamera.setOneShotPreviewCallback(CameraManager.this);
        }
    }

    public void onResume() {
        isPause = false;
        shotNextFrame();
    }

    public void onPause() {
        isPause = true;
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            activity = null;
        }
    }

    public void releaseDecoderThread() {
        DecoderManager.getInstance().stop();
    }
}
