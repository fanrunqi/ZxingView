package cn.leo.produce;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import cn.leo.produce.config.Utils;
import cn.leo.produce.config.ZVParams;
import cn.leo.produce.decode.ResultCallBack;
import cn.leo.produce.lifecycle.AttachFragment;
import cn.leo.produce.lifecycle.PermissionListener;

/**
 * @description: QRCode or barcode recognition view
 * @author: fanrunqi
 * @date: 2019/4/3 16:26
 */
public class ZxingView extends FrameLayout {

    private AppCompatActivity activity;
    private boolean useDefaultViewfinder;

    public ZxingView(@NonNull Context context) {
        this(context, null);
    }

    public ZxingView(@NonNull Context context,
                     @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZxingView(@NonNull Context context,
                     @Nullable AttributeSet attrs,
                     @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ZxingView(@NonNull Context context,
                     @Nullable AttributeSet attrs,
                     @AttrRes int defStyleAttr,
                     @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ZxingView);
        String ViewFinderColor = ta.getString(R.styleable.ZxingView_defaultViewFinderColor);
        if (!"".equals(ViewFinderColor) && ViewFinderColor != null) {
            ZVParams.ViewFinderColor = Color.parseColor(ViewFinderColor);
        }
        useDefaultViewfinder = ZVParams.useDefaultViewfinder = ta.getBoolean(
                R.styleable.ZxingView_useDefaultViewfinder,
                ZVParams.useDefaultViewfinder);
        int recognizeWidthInDp = ta.getInt(R.styleable.ZxingView_recognizeRectWidthInDp,
                ZVParams.defaultRecognizeRectWidthInDp);
        int recognizeHeightInDp = ta.getInt(R.styleable.ZxingView_recognizeRectHeightInDp,
                ZVParams.defaultRecognizeRectHeightInDp);
        ta.recycle();

        ZVParams.recognizeRectWidthInPx = Utils.dip2px(context, recognizeWidthInDp);
        ZVParams.recognizeRectHeightInPx = Utils.dip2px(context, recognizeHeightInDp);
    }

    /**
     * Setting configuration
     */

    public ZxingView bind(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }

    public void subscribe(ResultCallBack callBack) {
        Utils.validateMainThread();
        ZVParams.resultCallBack = callBack;
        addLifeCycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (ZVParams.recognizeRectWidthInPx > w || ZVParams.recognizeRectWidthInPx <= 0) {
            ZVParams.recognizeRectWidthInPx = w;
        }
        if (ZVParams.recognizeRectHeightInPx > h || ZVParams.recognizeRectHeightInPx <= 0) {
            ZVParams.recognizeRectHeightInPx = h;
        }
    }

    /**
     * create CameraView & ForegroundView
     */
    private void addLifeCycle() {
        new AttachFragment()
                .bind(activity)
                .checkCameraPermission(new PermissionListener() {
                    @Override
                    public void granted(AttachFragment fragment) {
                        new CameraPreview(activity, ZxingView.this);
                        if (useDefaultViewfinder) {
                            new Viewfinder(activity, ZxingView.this);
                        }
                    }
                });
    }

}
