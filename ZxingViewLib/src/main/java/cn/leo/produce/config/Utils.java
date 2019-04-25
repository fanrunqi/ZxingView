package cn.leo.produce.config;

import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/16 17:48
 */
public class Utils {

    public static void addInParent(View view,
                                   ViewGroup parent) {
        parent.addView(view, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
    }


    public static void validateMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Must be called from the main thread.");
        }
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        if (dpValue <= 0) {
            return 0;
        }
        return (int) (dpValue * scale + 0.5f);
    }
}
