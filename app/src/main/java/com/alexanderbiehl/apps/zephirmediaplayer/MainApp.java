package com.alexanderbiehl.apps.zephirmediaplayer;

import android.app.Application;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApp extends Application {

    @Inject
    ExecutorService executorService;

    @Override
    public void onTerminate() {
        super.onTerminate();
        executorService.shutdown();
    }
}
