package com.alexanderbiehl.apps.zephirmediaplayer.datastore;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class ZephirDataStore {

    private static final String PREFERENCES_NAME = "zephir_preferences";

    private static final String KEY_LIBRARY_SYNC = "library_sync";

    private final SharedPreferences sharedPreferences;

    private final SharedPreferences.Editor editor;

    @Inject
    public ZephirDataStore(@ApplicationContext @NonNull Context context) {
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
        );
        this.editor = sharedPreferences.edit();
    }

    public boolean isLibrarySynced() {
        return sharedPreferences.getBoolean(KEY_LIBRARY_SYNC, false);
    }

    public void setLibrarySynced(boolean synced) {
        editor.putBoolean(KEY_LIBRARY_SYNC, synced);
        editor.apply();
    }
}
