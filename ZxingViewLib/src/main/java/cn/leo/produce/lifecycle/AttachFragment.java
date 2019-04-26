package cn.leo.produce.lifecycle;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

/**
 * @description: lifeCycle & permission
 * @author: fanrunqi
 * @date: 2019/4/16 14:33
 */
public class AttachFragment extends Fragment {

    private Activity activity;

    @Override
    public void onResume() {
        super.onResume();
        LifeCycleManager.getInstance().onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LifeCycleManager.getInstance().onPause();
    }

    @Override
    public void onDestroy() {
        LifeCycleManager.getInstance().onDestroy();
        super.onDestroy();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LifeCycleManager.getInstance().onConfigurationChanged();
        super.onConfigurationChanged(newConfig);
    }

    /**
     * add lifeFragment
     */
    public AttachFragment bind(Activity activity) {
        this.activity = activity;
        activity.getFragmentManager()
                .beginTransaction()
                .add(this, "AttachFragment")
                .commit();
        return this;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }
    }

    /**
     * request camera permission
     */

    private static int cameraPermissionReqCode = 777;
    private PermissionListener permissionListener;

    public void checkCameraPermission(PermissionListener listener) {
        if (Build.VERSION.SDK_INT >= 23) {
            this.permissionListener = listener;
        } else {
            listener.granted(AttachFragment.this);
        }
    }

    @TargetApi(23)
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            permissionListener.granted(AttachFragment.this);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    cameraPermissionReqCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == cameraPermissionReqCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionListener.granted(AttachFragment.this);
            }
        }
    }

}
