package com.alexanderbiehl.apps.zephirmediaplayer.service;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.alexanderbiehl.apps.zephirmediaplayer.common.observers.MediaStoreContentObserver;
import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.MediaDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.datastore.LibrarySyncState;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MediaStoreSyncService extends LifecycleService {

    @Inject
    AppDatabase db;

    @Inject
    Executor executor;

    @Inject
    MediaDataSource mediaDataSource;

    @Inject
    LibrarySyncState librarySyncState;

    private MediaStoreContentObserver observer;

    @Override
    public void onCreate() {
        super.onCreate();
        this.observer = new MediaStoreContentObserver(
                new Handler(),
                executor,
                db,
                mediaDataSource
        );
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
        );
        // only execute first time sync if library is not synced yet, otherwise we would execute an unnecessary sync on every app start
        if (!librarySyncState.isLibrarySynced()) {
            this.observer.executeSync(result -> librarySyncState.setSynced(true));
        } else {
            librarySyncState.setSynced(true);
        }
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
