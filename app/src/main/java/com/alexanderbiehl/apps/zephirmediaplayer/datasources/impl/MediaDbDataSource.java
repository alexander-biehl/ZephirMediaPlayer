package com.alexanderbiehl.apps.zephirmediaplayer.datasources.impl;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.data.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.MediaDataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class MediaDbDataSource implements MediaDataSource {

    private final AppDatabase db;
    private final Executor executor;

    public MediaDbDataSource(
            AppDatabase db,
            Executor executor
    ) {
        this.db = db;
        this.executor = executor;
    }

    @Override
    public void getMedia(RepositoryCallback<List<MediaItem>> callback) {
        executor.execute(() -> {
            List<MediaItem> items = loadMediaFromDb();
            Result<List<MediaItem>> result = new Result.Success<>(items);
            callback.onComplete(result);
        });
    }

    @Override
    public List<MediaItem> getMediaSynchronous() {
        return loadMediaFromDb();
    }

    private List<MediaItem> loadMediaFromDb() {
        SongEntity[] songEntities = db.songDao().getAllSongs();
        List<MediaItem> items = new ArrayList<>();
        for (SongEntity e : songEntities) {
            items.add(new MediaItem.Builder()
                    .setMediaId(e.mediaId)
                    .setUri(e.sourceUri)
                    .setMediaMetadata(
                            new MediaMetadata.Builder()
                                    .setTitle(e.title)
                                    .setArtist(db.artistDao().getById(e.songArtistId).title)
                                    .setAlbumTitle(db.albumDao().getById(e.songAlbumId).title)
                                    .setTrackNumber(Integer.valueOf(e.trackNumber))
                                    .build()
                    )
                    .build());
        }
        return items;
    }
}
