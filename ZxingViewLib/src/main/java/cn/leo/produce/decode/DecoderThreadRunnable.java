package cn.leo.produce.decode;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static cn.leo.produce.config.Utils.resultHandler;
import static cn.leo.produce.config.ZvConstant.DECODE_FAILED;
import static cn.leo.produce.config.ZvConstant.DECODE_SUCCEEDED;
import static cn.leo.produce.config.ZvConstant.POSSIBLE_RESULT_POINTS;

/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/25 15:00
 */
public class DecoderThreadRunnable implements Runnable {

    private SourceData source;
    private Decoder decoder;
    private Rect cropRect;

    public DecoderThreadRunnable(SourceData source, Rect cropRect) {
        this.source = source;
        this.cropRect = cropRect;
        decoder = createDecoder();
    }

    @Override
    public void run() {
        doDecode(source);
    }

    private void doDecode(SourceData sourceData) {
        long start = System.currentTimeMillis();

        Result rawResult = null;
        sourceData.setCropRect(cropRect);
        LuminanceSource source = sourceData.createSource();

        if (source != null) {
            rawResult = decoder.decode(source);
        }

        if (rawResult != null) {
            long end = System.currentTimeMillis();
            BarcodeResult barcodeResult = new BarcodeResult(rawResult, sourceData);
            Message message = Message.obtain(resultHandler, DECODE_SUCCEEDED, barcodeResult);
            Bundle bundle = new Bundle();
            message.setData(bundle);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(resultHandler, DECODE_FAILED);
            message.sendToTarget();
        }

        List<ResultPoint> resultPoints = decoder.getPossibleResultPoints();
        Message message = Message.obtain(resultHandler, POSSIBLE_RESULT_POINTS);
        message.sendToTarget();

    }

    private Decoder createDecoder() {
        DecoderResultPointCallback callback = new DecoderResultPointCallback();
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, callback);
        Decoder decoder = new DecoderBuild().createDecoder(hints);
        callback.setDecoder(decoder);
        return decoder;
    }

}
