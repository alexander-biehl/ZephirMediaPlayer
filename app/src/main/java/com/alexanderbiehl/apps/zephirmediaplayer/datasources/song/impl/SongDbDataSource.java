package com.alexanderbiehl.apps.zephirmediaplayer.datasources.song.impl;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.model.Song;
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.song.SongDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class SongDbDataSource implements SongDataSource {

    private final SongDao songDao;
    private final Executor executor;

    public SongDbDataSource(
            final SongDao songDao,
            final Executor executor
    ) {
        this.songDao = songDao;
        this.executor = executor;
    }

    @Override
    public void getSong(Long id, Long mediaId, RepositoryCallback<Song> callback) {
        this.executor.execute(() -> {
            SongEntity e = null;
            if (id != null) {
                e = songDao.getById(id);
            } else if (mediaId != null) {
                e = songDao.getByMediaId(String.valueOf(mediaId));
            } else {
                callback.onComplete(new Result.Error<>(new Exception("id and mediaId missing")));
            }
            Result<Song> result;
            if (e != null) {
                result = new Result.Success<>(Song.from(e));
            } else {
                result = new Result.Error<>(new Exception("No result for given id"));
            }
            callback.onComplete(result);
        });
    }

    @Override
    public void getSongs(RepositoryCallback<List<Song>> callback) {
        this.executor.execute(() -> {
            List<Song> songs = new ArrayList<>();
            SongEntity[] entities = songDao.getAllSongs();
            for (SongEntity entity : entities) {
                songs.add(Song.from(entity));
            }
            Result<List<Song>> result;
            if (songs.isEmpty()) {
                result = new Result.Error<>(new Exception("No Songs Found"));
            } else {
                result = new Result.Success<>(songs);
            }
            callback.onComplete(result);
        });
    }

    @Override
    public void createSong(SongEntity song, RepositoryCallback<Long> callback) {
        this.executor.execute(() -> {
            long id = songDao.insert(song);
            Result<Long> result = new Result.Success<>(id);
            callback.onComplete(result);
        });
    }

    @Override
    public void updateSong(SongEntity song, RepositoryCallback<Void> callback) {
        this.executor.execute(() -> {
            songDao.update(song);
            callback.onComplete(new Result.Success<>());
        });
    }

    @Override
    public void deleteSong(SongEntity song, RepositoryCallback<Void> callback) {
        this.executor.execute(() -> {
            songDao.delete(song);
            callback.onComplete(new Result.Success<>());
        });
    }

    @Override
    public void deleteSong(Long id, RepositoryCallback<Void> callback) {
        this.executor.execute(() -> {
            SongEntity song = songDao.getById(id);
            Result<Void> result;
            if (song != null) {
                songDao.delete(song);
                result = new Result.Success<>();
            } else {
                result = new Result.Error<>(new Exception("song not found for id: " + id.toString()));
            }
            callback.onComplete(result);
        });
    }
}
