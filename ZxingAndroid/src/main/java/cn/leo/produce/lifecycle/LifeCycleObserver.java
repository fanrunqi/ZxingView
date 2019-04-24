package cn.leo.produce.lifecycle;


/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/16 14:35
 */
public interface LifeCycleObserver {
    void onResume();
    void onPause();
    void onDestroy();
    void onConfigurationChanged();
}
