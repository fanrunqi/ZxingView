package cn.leo.produce;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.view.TextureView;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

import cn.leo.produce.camera.CameraManager;
import cn.leo.produce.camera.ViewSize;
import cn.leo.produce.config.Utils;
import cn.leo.produce.lifecycle.LifeCycleManager;
import cn.leo.produce.lifecycle.LifeCycleObserver;

import static cn.leo.produce.config.ZvConstant.SYSTEM_SLEEP_IN_MILLIS;

/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/16 17:54
 */

public class CameraPreview extends TextureView implements
        TextureView.SurfaceTextureListener,
        LifeCycleObserver {

    private Activity activity;
    private CameraManager cameraManager;
    private ViewSize viewSize;
    private sleepHandle sleepHandle;

    CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Activity activity,
                         FrameLayout parent) {
        super(activity);
        this.activity = activity;
        LifeCycleManager.getInstance().addLifeCycleObserver(this);
        Utils.addInParent(this, parent);

        setLayerType(LAYER_TYPE_HARDWARE, null);
        setSurfaceTextureListener(this);

        sleepHandle = new sleepHandle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (viewSize == null) {
            viewSize = new ViewSize(w, h);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                          int width,
                                          int height) {
        buildCameraManager();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                            int width,
                                            int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }


    private void buildCameraManager() {
        cameraManager = new CameraManager(activity, viewSize,CameraPreview.this);
        cameraManager.startPreview();
    }

    /**
     * add lifecycle
     */
    @Override
    public void onResume() {
        if (cameraManager != null) {
            cameraManager.onResume();
            sleepHandle.removeCallbacksAndMessages(null);
        }
        else if (viewSize != null) {
            buildCameraManager();
        }
    }

    @Override
    public void onPause() {
        if (cameraManager != null) {
            cameraManager.onPause();
        }
        Message msg = new Message();
        sleepHandle.sendMessageAtTime(msg, SYSTEM_SLEEP_IN_MILLIS);
    }

    @Override
    public void onConfigurationChanged() {
        onDestroy();
        if (viewSize != null) {
            viewSize = viewSize.Rotate();
        }
        buildCameraManager();
    }

    @Override
    public void onDestroy() {
        if (cameraManager != null) {
            cameraManager.releaseCamera();
            cameraManager = null;
        }
    }

    @SuppressLint("HandlerLeak")
    private class sleepHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            onDestroy();
        }
    }

}
