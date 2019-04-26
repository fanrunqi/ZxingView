package cn.leo.produce.config;

import android.graphics.Color;
import cn.leo.produce.decode.ResultCallBack;

/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/23 15:03
 */
public class ZvParams {

    public static ResultCallBack resultCallBack;
    public static boolean useDefaultInteractiveView;
    public static int InteractiveViewColor;
    public static int InteractiveViewMaskColor;
    public static int RecognizeRectWidthInDp;
    public static int RecognizeRectHeightInDp;

    public static int recognizeRectWidthInPx;
    public static int recognizeRectHeightInPx;

    static {
        useDefaultInteractiveView = true;
        RecognizeRectWidthInDp = 300;
        RecognizeRectHeightInDp = 300;
        InteractiveViewColor = Color.parseColor("#118eea");
        InteractiveViewMaskColor = Color.parseColor("#a0000000");
    }

}
