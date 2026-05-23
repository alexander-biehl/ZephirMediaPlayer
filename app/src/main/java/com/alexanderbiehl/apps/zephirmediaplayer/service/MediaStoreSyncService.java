package com.alexanderbiehl.apps.zephirmediaplayer.service;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.alexanderbiehl.apps.zephirmediaplayer.MainApp;
import com.alexanderbiehl.apps.zephirmediaplayer.common.observers.MediaStoreContentObserver;
import com.alexanderbiehl.apps.zephirmediaplayer.database.AppDatabase;

public class MediaStoreSyncService extends LifecycleService {

    private MediaStoreContentObserver observer;

    @Override
    public void onCreate() {
        super.onCreate();
        MainApp app = (MainApp) getApplication();
        AppDatabase db = app.getAppContainer().getDatabase();
        this.observer = new MediaStoreContentObserver(
                new Handler(),
                app.getExec(),
                db,
                this
        );
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
        );
        // execute first time sync
        this.observer.executeSync(result -> app.setStoreIsSynced(true));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (observer != null) {
            getContentResolver().unregisterContentObserver(observer);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return super.onBind(intent);
    }
}
