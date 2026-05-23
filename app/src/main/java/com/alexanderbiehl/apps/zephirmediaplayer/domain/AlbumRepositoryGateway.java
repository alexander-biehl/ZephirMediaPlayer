package com.alexanderbiehl.apps.zephirmediaplayer.domain;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;

import java.util.List;

public interface AlbumRepositoryGateway {

    List<SongEntity> getSongsByAlbumId(@NonNull String mediaId);

    AlbumEntity getById(@NonNull String mediaId);

    List<AlbumEntity> getAlbums();

    List<AlbumEntity> getAlbumsByArtistId(long artistId);
}

