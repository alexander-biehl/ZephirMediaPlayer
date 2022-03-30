package com.curiositas.apps.zephirmediaplayer;

import android.app.Application;

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // TODO potentially initialize ExecutorService
        //  https://developer.android.com/guide/background/threading#creating-multiple-threads
        //  or WorkManager
        //  https://developer.android.com/reference/androidx/work/WorkManager
        //  here
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // TODO do we need to clean up references to ExecutorService, WorkManager, etc?
    }
}
