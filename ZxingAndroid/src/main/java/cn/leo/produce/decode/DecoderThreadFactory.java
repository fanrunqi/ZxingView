package cn.leo.produce.decode;

import android.support.annotation.NonNull;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/25 14:30
 */
public class DecoderThreadFactory implements ThreadFactory {

    private final AtomicInteger mThreadNum = new AtomicInteger(1);

    @Override
    public Thread newThread(@NonNull Runnable r) {
        return new Thread(r,"DecoderThread-"+mThreadNum.getAndIncrement());
    }

}
