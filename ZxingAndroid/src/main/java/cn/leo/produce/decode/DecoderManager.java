package cn.leo.produce.decode;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import com.google.zxing.ResultPoint;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import cn.leo.produce.config.Utils;
import cn.leo.produce.config.ZvParams;
import static cn.leo.produce.config.ZvConstant.DECODE_FAILED;
import static cn.leo.produce.config.ZvConstant.DECODE_SUCCEEDED;
import static cn.leo.produce.config.ZvConstant.POSSIBLE_RESULT_POINTS;

/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/17 17:56
 */
public class DecoderManager {

    private static class SingletonHandler {
        private static DecoderManager decoderManager = new DecoderManager();
    }

    public static DecoderManager getInstance() {
        return DecoderManager.SingletonHandler.decoderManager;
    }


    public static Handler resultHandler;
    private ThreadPoolExecutor executor;

    private DecoderManager() {
        Utils.validateMainThread();
        BuildResultHandler();
        executor = new ThreadPoolExecutor(6,
                Integer.MAX_VALUE,
                3L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new DecoderThreadFactory());
        executor.prestartAllCoreThreads();
    }

    @SuppressWarnings("unchecked")
    private void BuildResultHandler() {
        resultHandler = new Handler(new Handler.Callback() {
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

    public void decode(SourceData source,Rect cropRect) {
        executor.execute(new DecoderThreadRunnable(source, cropRect));
    }


    public void stop() {
        Utils.validateMainThread();
        executor.shutdownNow();
    }


}
