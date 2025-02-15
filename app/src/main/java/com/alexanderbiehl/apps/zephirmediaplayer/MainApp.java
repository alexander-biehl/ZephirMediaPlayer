package com.alexanderbiehl.apps.zephirmediaplayer;

import android.app.Application;
import android.content.Intent;

import androidx.databinding.ObservableBoolean;

import com.alexanderbiehl.apps.zephirmediaplayer.service.MediaStoreSyncService;

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

    private ObservableBoolean storeIsSynced;

    @Override
    public void onCreate() {
        super.onCreate();
        storeIsSynced = new ObservableBoolean(false);
        Intent mediaObserverServiceIntent = new Intent(this, MediaStoreSyncService.class);
        startService(mediaObserverServiceIntent);
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

    public void setStoreIsSynced(boolean isSynced) {
        storeIsSynced.set(isSynced);
        storeIsSynced.notifyChange();
    }

    public ObservableBoolean getStoreIsSynced() {
        return storeIsSynced;
    }
}
