package com.curiositas.apps.zephirmediaplayer.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.curiositas.apps.zephirmediaplayer.MainApp;
import com.curiositas.apps.zephirmediaplayer.SongManager;
import com.curiositas.apps.zephirmediaplayer.dataloaders.SongLoader;
import com.curiositas.apps.zephirmediaplayer.models.Song;

import java.util.List;
import java.util.concurrent.Executor;

public class SongRepository {

    private final String TAG = SongRepository.class.getSimpleName();
    private final Executor executor;
    private final Application app;
    private final SongManager songManager;
    private final MutableLiveData<List<Song>> songList;

    public SongRepository(Application app) {
        this.executor = ((MainApp) app).getExec();
        this.app = app;
        this.songList = new MutableLiveData<>();
        this.songManager = new SongManager();
        loadSongs();
    }

    /**
     * Getter method to retrieve our songList LiveData
     *
     * @return LiveData<List < Song>> songList
     */
    public LiveData<List<Song>> getSongList() {
        if (this.songList == null) {
            loadSongs();
        }
        return this.songList;
    }

        private void loadSongs() {
        this.executor.execute(() -> {
            List<Song> songs = SongLoader.getAllSongs(app.getApplicationContext());

            Log.d(TAG, "SongLoader returned " + songs.size()  + " items.");
            songList.postValue(songs);
        });
//        this.executor.execute(() -> {
//            List<HashMap<String,String>> songs = songManager.getPlayList();
//        });
    }
//    private void loadSongs() {
//        executor.execute(() -> {
//            List <Song> songs = new ArrayList<>();
//            for (int i = 1; i <= 5; i++) {
//                String title = "Title " + i;
//                String artist = "Artist " + i;
//                String album = "Album " + i;
//
//                songs.add(new Song(Uri.EMPTY, title, artist, album));
//            }
//            songList.postValue(songs);
//            //songList.setValue(songs);
//        });
//    }
}
