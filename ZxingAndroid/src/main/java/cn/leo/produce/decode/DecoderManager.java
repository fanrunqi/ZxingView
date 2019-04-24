package cn.leo.produce.decode;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.leo.produce.config.Utils;
import cn.leo.produce.config.ZVParams;

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

    private static final String TAG = "DecoderThread";
    private static final int ZXING_DECODE = 777;
    private static final int DECODE_SUCCEEDED = 778;
    private static final int DECODE_FAILED = 779;
    private static final int POSSIBLE_RESULT_POINTS = 780;

    private static volatile boolean running;
    private HandlerThread handlerThread;
    private Handler decoderHandler, resultHandler;
    private Rect cropRect;
    private Decoder decoder;
    private DecoderNextCallBack decoderNextCallBack;

    private DecoderManager() {
        start();
    }

    public void decode(SourceData source,
                       Rect cropRect,
                       DecoderNextCallBack decoderNextCallBack) {
        this.decoderNextCallBack = decoderNextCallBack;
        this.cropRect = cropRect;
        decoderHandler.obtainMessage(ZXING_DECODE, source).sendToTarget();
    }

    public void start() {
        running = true;
        Utils.validateMainThread();
        decoder = createDecoder();
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();

        decoderHandler = new Handler(handlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == ZXING_DECODE) {
                    actualDecode((SourceData) msg.obj);
                }
                return true;
            }
        });

        resultHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == DECODE_SUCCEEDED) {
                    BarcodeResult result = (BarcodeResult) msg.obj;
                    if (result != null) {
                        if (ZVParams.resultCallBack != null) {
                            ZVParams.resultCallBack.onResult(result.getText());
                        }
                    }
                    return true;
                } else if (msg.what == DECODE_FAILED) {
                    return true;
                } else if (msg.what == POSSIBLE_RESULT_POINTS) {
                    List<ResultPoint> resultPoints = (List<ResultPoint>) msg.obj;
                    return true;
                }
                return false;
            }
        });
    }

    public void stop() {
        Utils.validateMainThread();
        if (running && decoderHandler != null) {
            decoderHandler.removeCallbacksAndMessages(null);
            resultHandler.removeCallbacksAndMessages(null);
            handlerThread.quit();
            running = false;
        }
    }

    private Decoder createDecoder() {
        DecoderResultPointCallback callback = new DecoderResultPointCallback();
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, callback);

        Decoder decoder = new DecoderBuild().createDecoder(hints);
        callback.setDecoder(decoder);

        return decoder;
    }

    private void actualDecode(SourceData sourceData) {
        long start = System.currentTimeMillis();
        Result rawResult = null;

        sourceData.setCropRect(cropRect);
        LuminanceSource source = sourceData.createSource();

        if (source != null) {
            rawResult = decoder.decode(source);
        }
        Log.d(TAG, "rawResult=" + rawResult);
        if (rawResult != null) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode in " + (end - start) + " ms");
            if (resultHandler != null) {
                BarcodeResult barcodeResult = new BarcodeResult(rawResult, sourceData);
                Message message = Message.obtain(resultHandler, DECODE_SUCCEEDED, barcodeResult);
                Bundle bundle = new Bundle();
                message.setData(bundle);
                message.sendToTarget();
            }
        } else {
            if (resultHandler != null) {
                Message message = Message.obtain(resultHandler, DECODE_FAILED);
                message.sendToTarget();
            }
        }

        if (resultHandler != null) {
            List<ResultPoint> resultPoints = decoder.getPossibleResultPoints();
            Message message = Message.obtain(resultHandler, POSSIBLE_RESULT_POINTS);
            message.sendToTarget();
        }

        decoderNextCallBack.requestNextFrame();
    }


}
