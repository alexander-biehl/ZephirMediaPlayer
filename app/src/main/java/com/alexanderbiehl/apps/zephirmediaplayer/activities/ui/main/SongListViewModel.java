package com.alexanderbiehl.apps.zephirmediaplayer.activities.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alexanderbiehl.apps.zephirmediaplayer.models.Song;
import com.alexanderbiehl.apps.zephirmediaplayer.repositories.SongRepository;

import java.util.List;

public class SongListViewModel extends AndroidViewModel {

    /** class name for logging */
    private final String TAG = SongListViewModel.class.getSimpleName();

    /** Live data list that will hold our songs */
    private LiveData<List<Song>> songList;

    /** Repository used to retieve songs from the backend */
    private SongRepository songRepository;

    public SongListViewModel(Application app) {
        super(app);
        this.songRepository = new SongRepository(app);
        this.songList = this.songRepository.getSongList();
    }

    /**
     * Accessor to allow our UI to access the data
     * @return
     */
    public LiveData<List<Song>> getSongs() {
        return this.songList;
    }
}