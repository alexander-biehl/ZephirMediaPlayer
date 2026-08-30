package com.alexanderbiehl.apps.zephirmediaplayer.datastore;

import androidx.databinding.ObservableBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * App-wide flag tracking whether the on-device MediaStore library has been synced into Room
 * at least once. MediaStoreSyncService writes it after the initial sync completes; MainActivity
 * waits on it before connecting to the playback service so browsing doesn't race an empty
 * database on first launch.
 */
@Singleton
public class LibrarySyncState {

    private final ZephirDataStore dataStore;
    private final ObservableBoolean isSynced = new ObservableBoolean(false);

    @Inject
    public LibrarySyncState(ZephirDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public ObservableBoolean getIsSynced() {
        return isSynced;
    }

    public boolean isLibrarySynced() {
        return dataStore.isLibrarySynced();
    }

    public synchronized void setSynced(boolean synced) {
        dataStore.setLibrarySynced(synced);
        isSynced.set(synced);
        isSynced.notifyChange();
    }
}
