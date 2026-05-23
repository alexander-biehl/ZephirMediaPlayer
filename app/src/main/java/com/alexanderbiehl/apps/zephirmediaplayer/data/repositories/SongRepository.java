package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.SongRepositoryGateway;

public class SongRepository implements SongRepositoryGateway {

    private final SongDao songDao;

    public SongRepository(SongDao songDao) {
        this.songDao = songDao;
    }

    public SongEntity getById(@NonNull String mediaId) {
        return songDao.getByMediaId(mediaId);
    }
}
