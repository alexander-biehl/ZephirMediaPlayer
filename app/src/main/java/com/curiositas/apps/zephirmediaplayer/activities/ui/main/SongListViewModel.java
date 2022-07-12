package com.curiositas.apps.zephirmediaplayer.activities.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.curiositas.apps.zephirmediaplayer.models.Song;
import com.curiositas.apps.zephirmediaplayer.repositories.SongRepository;

import java.util.List;

public class SongListViewModel extends ViewModel {

    /** class name for logging */
    private final String TAG = SongListViewModel.class.getSimpleName();

    /** Live data list that will hold our songs */
    private LiveData<List<Song>> songList;

    /** Repository used to retieve songs from the backend */
    private final SongRepository songRepository;

    public SongListViewModel(SongRepository songRepository) {
        this.songRepository = songRepository;
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