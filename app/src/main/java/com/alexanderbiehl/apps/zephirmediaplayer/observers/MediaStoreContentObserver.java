package com.alexanderbiehl.apps.zephirmediaplayer.observers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import androidx.media3.common.MediaItem;
import androidx.media3.extractor.ExtractorUtil;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.base.DoaBase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.util.EntityExtractor;
import com.alexanderbiehl.apps.zephirmediaplayer.dataloaders.MediaLoader;
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.impl.MediaLocalDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.repositories.MediaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaStoreContentObserver extends ContentObserver {

    private final AppDatabase db;
    private final Executor executorService;
    private final Context context;

    public MediaStoreContentObserver(
            Handler handler,
            Executor executorService,
            AppDatabase db,
            Context context) {
        super(handler);
        this.db = db;
        this.executorService = executorService;
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        executeSync();
    }

    public void executeSync() {
        this.executorService.execute(this::syncMediaStore);
    }

    private void syncMediaStore() {
        MediaRepository mediaRepository = new MediaRepository(
                new MediaLocalDataSource(
                        new MediaLoader(),
                        executorService,
                        context
                )
        );
        // want to make a synchronous call here because we are already running on a background thread
        List<MediaItem> items = mediaRepository.getMediaSynchronous();

        List<ArtistEntity> artistEntities = EntityExtractor.extractArtistEntities(items);
        Map<String, Long> artistIdMap = new HashMap<>();
        for (ArtistEntity entity : artistEntities) {
            ArtistEntity _entity = db.artistDao().getByMediaId(entity.mediaId);

            Long id = _entity != null ? _entity.id : db.artistDao().insert(entity);
            artistIdMap.put(entity.title, id);
        }

        List<AlbumEntity> albumEntities = EntityExtractor.extractAlbumEntities(items, artistIdMap);
        Map<String,Long> albumIdMap = new HashMap<>();
        for (AlbumEntity entity : albumEntities) {
            AlbumEntity _entity = db.albumDao().getByMediaId(entity.mediaId);

            Long id = _entity != null ? _entity.id : db.albumDao().insert(entity);
            albumIdMap.put(entity.title, id);
        }

        List<SongEntity> songEntities = EntityExtractor.extractSongEntities(items, artistIdMap, albumIdMap);
        for (SongEntity entity : songEntities) {
            if (db.songDao().getByMediaId(entity.mediaId) == null) {
                db.songDao().insert(entity);
            }
        }
    }

}
