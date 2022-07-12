package com.curiositas.apps.zephirmediaplayer.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.curiositas.apps.zephirmediaplayer.MainApp;
import com.curiositas.apps.zephirmediaplayer.dataloaders.SongLoader;
import com.curiositas.apps.zephirmediaplayer.models.Song;

import java.util.List;
import java.util.concurrent.Executor;

public class SongRepository {

    private final String TAG = SongRepository.class.getSimpleName();

    private MutableLiveData<List<Song>> songList;

    private final Executor executor;
    private final Application app;

    SongRepository(Application app) {
        this.executor = ((MainApp) app).getExec();
        this.app = app;
    }

    /**
     * Getter method to retrieve our songList LiveData
     * @return LiveData<List<Song>> songList
     */
    public LiveData<List<Song>> getSongList() {
        return this.songList;
    }

    private void loadSongs() {
        this.executor.execute(() -> {
            List<Song> songs = SongLoader.getAllSongs(app.getApplicationContext());

            Log.d(TAG, "SongLoader returned " + songs.size()  + " items.");
            songList.setValue(songs);
        });
    }
}
