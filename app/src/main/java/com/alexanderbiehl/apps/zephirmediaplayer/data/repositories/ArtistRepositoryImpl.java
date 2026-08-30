package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.ArtistDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.data.models.Artist;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.ArtistRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ArtistRepositoryImpl implements ArtistRepository {

    private final ArtistDataSource localArtistDataSource;

    @Inject
    public ArtistRepositoryImpl(ArtistDataSource localArtistDataSource) {
        this.localArtistDataSource = localArtistDataSource;
    }

    public Artist getAlbumsByArtistId(@NonNull String mediaId) {
        return localArtistDataSource.getArtistAlbumsByMediaId(mediaId);
    }

    public Artist getById(@NonNull String mediaId) {
        return localArtistDataSource.getById(mediaId);
    }

    public List<Artist> getArtists() {
        return localArtistDataSource.getAll();
    }
}
