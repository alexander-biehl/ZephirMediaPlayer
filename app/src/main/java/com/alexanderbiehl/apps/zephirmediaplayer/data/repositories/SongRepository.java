package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;

public class SongRepository {

    private final SongDao songDao;

    public SongRepository(SongDao songDao) {
        this.songDao = songDao;
    }

    public SongEntity getById(String mediaId) {
        return songDao.getByMediaId(mediaId);
    }
}
