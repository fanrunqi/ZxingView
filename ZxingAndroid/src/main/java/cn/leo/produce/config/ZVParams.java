package cn.leo.produce.config;

import android.graphics.Color;
import cn.leo.produce.decode.ResultCallBack;

/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/23 15:03
 */
public class ZVParams {

    public static boolean useDefaultViewfinder;
    public static int ViewFinderColor;
    public static ResultCallBack resultCallBack;
    public static int defaultRecognizeRectWidthInDp;
    public static int defaultRecognizeRectHeightInDp;
    public static int recognizeRectWidthInPx;
    public static int recognizeRectHeightInPx;

    static {
        useDefaultViewfinder = true;
        defaultRecognizeRectWidthInDp = 300;
        defaultRecognizeRectHeightInDp = 300;
        ViewFinderColor = Color.parseColor("#0693ff");
    }

}
