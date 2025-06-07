package com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl;

import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.ArtistDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.data.models.Artist;
import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.ArtistDao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArtistDbDataSource implements ArtistDataSource {

    private final ArtistDao artistDao;

    public ArtistDbDataSource(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }


    @Override
    public List<Artist> getAll() {
        return Arrays.stream(artistDao.getArtists())
                .map(Artist::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Artist getById(String mediaId) {
        return null;
    }

    @Override
    public Long insert(Artist artist) {
        return 0L;
    }

    @Override
    public void update(Artist artist) {

    }

    @Override
    public void delete(Artist artist) {

    }

    @Override
    public Artist getArtistAlbumsByMediaId(String mediaId) {
        return null;
    }
}
