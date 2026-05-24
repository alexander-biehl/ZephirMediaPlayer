package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.SongRepository;

public class SongRepositoryImpl implements SongRepository {

    private final SongDao songDao;

    public SongRepositoryImpl(SongDao songDao) {
        this.songDao = songDao;
    }

    public SongEntity getById(@NonNull String mediaId) {
        return songDao.getByMediaId(mediaId);
    }
}
