package cn.leo.produce.lifecycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * @author: fanrunqi
 * @date: 2019/4/18 09:40
 */
public class LifeCycleManager {

    private static class SingletonHandler{
        private static LifeCycleManager lifeCycleManager = new LifeCycleManager();
    }

    public static LifeCycleManager getInstance(){
        return SingletonHandler.lifeCycleManager;
    }


    private List<LifeCycleObserver> lifeCycleObservers = new ArrayList<>();

    public  void addLifeCycleObservers(LifeCycleObserver... observers) {
        lifeCycleObservers.addAll(Arrays.asList(observers));
    }

    public  void addLifeCycleObserver(LifeCycleObserver observer) {
        lifeCycleObservers.add(observer);
    }

    public void onResume(){
        for (LifeCycleObserver observers : lifeCycleObservers) {
            observers.onResume();
        }
    }

    public void onPause(){
        for (LifeCycleObserver observers : lifeCycleObservers) {
            observers.onPause();
        }
    }

    public void onConfigurationChanged(){
        for (LifeCycleObserver observers : lifeCycleObservers) {
            observers.onConfigurationChanged();
        }
    }

    public void onDestroy(){
        for (LifeCycleObserver observers : lifeCycleObservers) {
            observers.onDestroy();
        }
        lifeCycleObservers.clear();
    }

}
