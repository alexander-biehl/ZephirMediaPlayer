package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.AlbumDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.rel.AlbumSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.AlbumRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlbumRepositoryImpl implements AlbumRepository {

    private final AlbumDao albumDao;

    public AlbumRepositoryImpl(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }

    public List<SongEntity> getSongsByAlbumId(@NonNull String mediaId) {
        AlbumSongs albumSongs = albumDao.getAlbumSongsByMediaId(mediaId);
        if (albumSongs == null || albumSongs.songEntities == null) {
            return Collections.emptyList();
        }
        return albumSongs.songEntities;
    }

    public AlbumEntity getById(@NonNull String mediaId) {
        return albumDao.getByMediaId(mediaId);
    }

    public List<AlbumEntity> getAlbums() {
        return Arrays.asList(albumDao.getAll());
    }

    public List<AlbumEntity> getAlbumsByArtistId(long artistId) {
        return Arrays.asList(albumDao.getAlbumsForArtist(artistId));
    }
}
