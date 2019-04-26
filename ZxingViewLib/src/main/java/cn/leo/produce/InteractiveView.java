package cn.leo.produce;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import cn.leo.produce.config.Utils;
import cn.leo.produce.config.ZvParams;
import cn.leo.produce.lifecycle.LifeCycleManager;
import cn.leo.produce.lifecycle.LifeCycleObserver;

/**
 * @description: Foreground animation view(Imitate Alipay scan )
 * @author: fanrunqi
 * @date: 2019/4/3 16:54
 */
public class InteractiveView extends View
        implements LifeCycleObserver {

    private Paint rectPaint, maskPaint, cornerPaint, gridPaint, bitmapPaint;
    private int rLeft, rTop, rRight, rBottom;
    private static int cornerPaintWidth = 12;
    private static int rectPaintWidth = 2;
    private Rect recognizeRect, maskRect;
    private PorterDuffXfermode xfermode;
    private Path cornerPath;
    private Bitmap gridBitmap;
    private ValueAnimator animator;
    private int bitmapH, gridBitmapLocY;

    public InteractiveView(Context context,
                           FrameLayout parent) {
        super(context);
        LifeCycleManager.getInstance().addLifeCycleObserver(this);
        Utils.addInParent(this, parent);
        initialize();
    }

    private void initialize() {
        rectPaint = new Paint();
        rectPaint.setColor(ZvParams.InteractiveViewColor);
        rectPaint.setDither(true);
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(rectPaintWidth);

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
        maskPaint = new Paint();
        maskPaint.setColor(ZvParams.InteractiveViewMaskColor);
        maskPaint.setAntiAlias(true);
        maskPaint.setStyle(Paint.Style.FILL);
        maskPaint.setDither(true);
        maskPaint.setFilterBitmap(true);

        cornerPaint = new Paint();
        cornerPaint.setColor(ZvParams.InteractiveViewColor);
        cornerPaint.setDither(true);
        cornerPaint.setAntiAlias(true);
        cornerPaint.setStyle(Paint.Style.STROKE);
        cornerPaint.setStrokeWidth(cornerPaintWidth);

        gridPaint = new Paint();
        gridPaint.setColor(ZvParams.InteractiveViewColor);
        gridPaint.setDither(true);
        gridPaint.setAntiAlias(true);
        gridPaint.setStyle(Paint.Style.STROKE);

        bitmapPaint = new Paint();
        bitmapPaint.setDither(true);
        bitmapPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setRect(w, h);
        setCornerPath();
        createGridBitMap(w);
    }

    private void createGridBitMap(int w) {
        int gridNum = 53;
        int gridLineWidth = 2;
        int bottomLineWidth = 6;
        int gridWidth = ZvParams.recognizeRectWidthInPx / gridNum;
        int bitmapW = ZvParams.recognizeRectWidthInPx;
        bitmapH = 7 * gridWidth + bottomLineWidth;
        gridBitmap = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888);
        Canvas gCanvas = new Canvas(gridBitmap);

        gridPaint.setStrokeWidth(gridLineWidth);
        Path gPath = new Path();
        for (int i = 1; i < 7; i++) {
            int locY = bitmapH - bottomLineWidth - i * gridWidth;
            gPath.moveTo(0, locY);
            gPath.lineTo(bitmapW, locY);
        }
        for (int j = 1; j < 53; j++) {
            int locX = j * gridWidth;
            gPath.moveTo(locX, 0);
            gPath.lineTo(locX, bitmapH - bottomLineWidth);
        }
        gCanvas.drawPath(gPath, gridPaint);

        gridPaint.setStrokeWidth(bottomLineWidth);
        int LineLocY = bitmapH - (bottomLineWidth >> 1);
        gCanvas.drawLine(0, LineLocY, w, LineLocY, gridPaint);

        //set alpha
        int[] argb = new int[gridBitmap.getWidth() * gridBitmap.getHeight()];
        gridBitmap.getPixels(argb, 0, gridBitmap.getWidth(), 0, 0,
                gridBitmap.getWidth(), gridBitmap.getHeight());

        for (int k = 0; k < argb.length; k++) {
            float alpha = k * 128 / (float) argb.length + 32;
            if ((argb[k] & 0xFF000000) != 0) {
                argb[k] = ((int) alpha << 24) | (argb[k] & 0x00FFFFFF);
            }
        }

        gridBitmap = Bitmap.createBitmap(argb, bitmapW, bitmapH, Bitmap.Config.ARGB_8888);
        gridBitmapLocY = rTop;//Set initial value
        startAnim();
    }

    private void setRect(int w, int h) {
        rLeft = (int) ((w - ZvParams.recognizeRectWidthInPx) / 2f);
        rTop = (int) ((h - ZvParams.recognizeRectHeightInPx) / 2f);
        rRight = rLeft + ZvParams.recognizeRectWidthInPx;
        rBottom = rTop + ZvParams.recognizeRectHeightInPx;
        recognizeRect = new Rect(rLeft, rTop, rRight, rBottom);
        maskRect = new Rect(0, 0, w, h);
    }


    private void setCornerPath() {
        int cLen = ZvParams.recognizeRectWidthInPx / 13;
        int hcpw = cornerPaintWidth / 2;
        cornerPath = new Path();
        cornerPath.moveTo(rLeft + hcpw, rTop + cLen);
        cornerPath.lineTo(rLeft + hcpw, rTop + hcpw);
        cornerPath.lineTo(rLeft + cLen, rTop + hcpw);

        cornerPath.moveTo(rRight - cLen, rTop + hcpw);
        cornerPath.lineTo(rRight - hcpw, rTop + hcpw);
        cornerPath.lineTo(rRight - hcpw, rTop + cLen);

        cornerPath.moveTo(rRight - hcpw, rBottom - cLen);
        cornerPath.lineTo(rRight - hcpw, rBottom - hcpw);
        cornerPath.lineTo(rRight - cLen, rBottom - hcpw);

        cornerPath.moveTo(rLeft + cLen, rBottom - hcpw);
        cornerPath.lineTo(rLeft + hcpw, rBottom - hcpw);
        cornerPath.lineTo(rLeft + hcpw, rBottom - cLen);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(gridBitmap, rLeft, gridBitmapLocY, bitmapPaint);
        drawBackground(canvas);
    }


    private void drawBackground(Canvas canvas) {
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(),
                maskPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(maskRect, maskPaint);
        maskPaint.setXfermode(xfermode);
        canvas.drawRect(recognizeRect, maskPaint);
        maskPaint.setXfermode(null);
        canvas.restoreToCount(sc);

        canvas.drawRect(recognizeRect, rectPaint);
        canvas.drawPath(cornerPath, cornerPaint);
    }

    private void startAnim() {
        animator = ValueAnimator.ofInt(rTop, rBottom - bitmapH);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                gridBitmapLocY = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    /**
     * add lifecycle
     */
    @Override
    public void onResume() {
        if (animator != null) {
            animator.resume();
        }
    }

    @Override
    public void onPause() {
        if (animator != null) {
            animator.pause();
        }
    }

    @Override
    public void onConfigurationChanged() {
    }

    @Override
    public void onDestroy() {
    }

}
