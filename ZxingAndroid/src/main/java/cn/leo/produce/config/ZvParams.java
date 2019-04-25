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
    public static int ViewFinderColor;
    public static int ViewFinderMaskColor;
    public static int defaultRecognizeRectWidthInDp;
    public static int defaultRecognizeRectHeightInDp;
    public static int recognizeRectWidthInPx;
    public static int recognizeRectHeightInPx;

    static {
        useDefaultInteractiveView = true;
        defaultRecognizeRectWidthInDp = 300;
        defaultRecognizeRectHeightInDp = 300;
        ViewFinderColor = Color.parseColor("#118eea");
        ViewFinderMaskColor = Color.parseColor("#a0000000");
    }

}
