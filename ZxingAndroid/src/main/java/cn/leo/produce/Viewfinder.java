package cn.leo.produce;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import cn.leo.produce.config.Utils;
import cn.leo.produce.config.ZVParams;
import cn.leo.produce.lifecycle.LifeCycleManager;
import cn.leo.produce.lifecycle.LifeCycleObserver;

/**
 * @description: Foreground animation view
 * @author: fanrunqi
 * @date: 2019/4/3 16:54
 */
public class Viewfinder extends View
        implements LifeCycleObserver {

    private Paint rectPaint;
    private Rect recognizeRect;

    public Viewfinder(Context context,
                      FrameLayout parent) {
        super(context);
        LifeCycleManager.getInstance().addLifeCycleObserver(this);
        Utils.addInParent(this, parent);
        initialize();
    }

    private void initialize() {
        rectPaint = new Paint();
        rectPaint.setColor(ZVParams.ViewFinderColor);
        rectPaint.setDither(true);
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(3);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int rLeft = (int) ((w - ZVParams.recognizeRectWidthInPx) / 2f);
        int rTop = (int) ((h - ZVParams.recognizeRectHeightInPx) / 2f);
        int rRight = rLeft + ZVParams.recognizeRectWidthInPx;
        int rBottom = rTop + ZVParams.recognizeRectHeightInPx;
        recognizeRect = new Rect(rLeft, rTop, rRight, rBottom);
        Log.i("FAN","onSizeChanged");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("FAN","onDraw");
        canvas.drawRect(recognizeRect, rectPaint);
    }

    /**
     * add lifecycle
     */
    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onConfigurationChanged() {
    }

    @Override
    public void onDestroy() {
    }

}
