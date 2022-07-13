package com.curiositas.apps.zephirmediaplayer;

import android.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApp extends Application {

    /**
     * Get number of available cores
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // Create a thread pool manager
    ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CORES);

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO potentially initialize ExecutorService
        //  https://developer.android.com/guide/background/threading#creating-multiple-threads
        //  or WorkManager
        //  https://developer.android.com/reference/androidx/work/WorkManager
        //  here
//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
//                + Environment.getExternalStorageDirectory())));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // TODO do we need to clean up references to ExecutorService, WorkManager, etc?
        // tell executor to stop accepting new threads
        executorService.shutdown();
    }

    public Executor getExec() {
        return executorService;
    }
}