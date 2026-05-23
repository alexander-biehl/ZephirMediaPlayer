package com.alexanderbiehl.apps.zephirmediaplayer.domain;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.data.models.Artist;

import java.util.List;

public interface ArtistRepositoryGateway {

    Artist getAlbumsByArtistId(@NonNull String mediaId);

    Artist getById(@NonNull String mediaId);

    List<Artist> getArtists();
}

