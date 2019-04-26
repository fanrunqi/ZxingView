package cn.leo.produce.decode;

import android.graphics.Rect;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import cn.leo.produce.config.Utils;

/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/17 17:56
 */
public class DecoderManager {

    private ThreadPoolExecutor executor;

    public DecoderManager() {
        Utils.validateMainThread();
        executor = new ThreadPoolExecutor(6,
                Integer.MAX_VALUE,
                3L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new DecoderThreadFactory());
        executor.prestartAllCoreThreads();
    }

    public void decode(SourceData source,Rect cropRect) {
        executor.execute(new DecoderThreadRunnable(source, cropRect));
    }

    public void stop() {
        Utils.validateMainThread();
        executor.shutdownNow();
    }

}
