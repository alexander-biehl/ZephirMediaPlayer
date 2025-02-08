package com.alexanderbiehl.apps.zephirmediaplayer.datasources.song;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.model.Song;

import java.util.List;
import java.util.Optional;

public interface SongDataSource {

    void getSong(Long id, Long mediaId, RepositoryCallback<Song> callback);

    void getSongs(RepositoryCallback<List<Song>> callback);

    void createSong(SongEntity song, RepositoryCallback<Long> callback);

    void updateSong(SongEntity song, RepositoryCallback<Void> callback);

    void deleteSong(SongEntity song, RepositoryCallback<Void> callback);

    void deleteSong(Long id, RepositoryCallback<Void> callback);
}
