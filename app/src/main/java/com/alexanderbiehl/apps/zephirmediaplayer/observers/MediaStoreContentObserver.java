package com.alexanderbiehl.apps.zephirmediaplayer.observers;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;

import com.alexanderbiehl.apps.zephirmediaplayer.data.database.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaStoreContentObserver extends ContentObserver {

    private final ContentResolver contentResolver;
    private final AppDatabase db;
    private final ExecutorService executorService;

    public MediaStoreContentObserver(Handler handler, ContentResolver contentResolver, AppDatabase db) {
        super(handler);
        this.contentResolver = contentResolver;
        this.db = db;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        executeSync();
    }

    private void executeSync() {
        this.executorService.execute(this::syncMediaStore);
    }

    private void syncMediaStore() {
        // TODO
    }


}
