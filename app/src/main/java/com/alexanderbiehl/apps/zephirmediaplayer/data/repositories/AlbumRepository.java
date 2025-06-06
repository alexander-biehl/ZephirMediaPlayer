package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.AlbumEntity;

import java.util.List;

public class AlbumRepository {
    public List<AlbumEntity> getSongsByAlbumId(String mediaId) {
        return List.of();
    }

    public AlbumEntity getById(String mediaId) {
        return null;
    }

    public List<AlbumEntity> getAlbums() {
        return List.of();
    }
}
