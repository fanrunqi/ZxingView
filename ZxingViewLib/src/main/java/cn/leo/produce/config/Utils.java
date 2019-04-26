package cn.leo.produce.config;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.zxing.ResultPoint;
import java.util.List;
import cn.leo.produce.decode.BarcodeResult;
import static cn.leo.produce.config.ZvConstant.DECODE_FAILED;
import static cn.leo.produce.config.ZvConstant.DECODE_SUCCEEDED;
import static cn.leo.produce.config.ZvConstant.POSSIBLE_RESULT_POINTS;

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

    @SuppressWarnings("unchecked")
    public static Handler resultHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == DECODE_SUCCEEDED) {
                BarcodeResult result = (BarcodeResult) msg.obj;
                if (result != null) {
                    if (ZvParams.resultCallBack != null) {
                        ZvParams.resultCallBack.onResult(result.getText());
                    }
                }
                return true;
            } else if (msg.what == DECODE_FAILED) {
                return true;
            } else if (msg.what == POSSIBLE_RESULT_POINTS) {
                List<ResultPoint> resultPoints = (List<ResultPoint>) msg.obj;
                //do more
                return true;
            }
            return false;
        }
    });

}
