package com.alexanderbiehl.apps.zephirmediaplayer;

import android.app.Application;

import androidx.databinding.ObservableBoolean;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApp extends Application {

    /**
     * Get number of available cores
     */
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // Create a thread pool manager
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CORES);

    private final ObservableBoolean storeIsSynced = new ObservableBoolean(false);

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // tell executor to stop accepting new threads
        executorService.shutdown();
    }

    // Note: don't need to make this synchronized since executorService is Final
    public Executor getExec() {
        return executorService;
    }

    public synchronized ObservableBoolean getStoreIsSynced() {
        return storeIsSynced;
    }

    public void setStoreIsSynced(boolean isSynced) {
        synchronized (storeIsSynced) {
            storeIsSynced.set(isSynced);
            storeIsSynced.notifyChange();
        }
    }
}
