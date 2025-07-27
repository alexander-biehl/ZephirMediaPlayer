package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.ArtistDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.data.models.Artist;

import java.util.List;

public class ArtistRepository {

    private final ArtistDataSource localArtistDataSource;

    public ArtistRepository(ArtistDataSource localArtistDataSource) {
        this.localArtistDataSource = localArtistDataSource;
    }

    public Artist getAlbumsByArtistId(String mediaId) {
        return localArtistDataSource.getArtistAlbumsByMediaId(mediaId);
    }

    public Artist getById(String mediaId) {
        return localArtistDataSource.getById(mediaId);
    }

    public List<Artist> getArtists() {
        return localArtistDataSource.getAll();
    }
}
