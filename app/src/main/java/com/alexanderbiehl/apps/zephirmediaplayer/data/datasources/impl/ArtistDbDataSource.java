package com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl;

import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.ArtistDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.data.models.Artist;
import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.ArtistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.ArtistEntity;

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
        var entity = artistDao.getByMediaId(mediaId);
        return entity == null ? null : Artist.fromEntity(entity);
    }

    @Override
    public Long insert(Artist artist) {
        ArtistEntity entity = new ArtistEntity();
        entity.mediaId = artist.getMediaId();
        entity.title = artist.getTitle();
        return artistDao.insert(entity);
    }

    @Override
    public void update(Artist artist) {
        ArtistEntity existing = artistDao.getByMediaId(artist.getMediaId());
        if (existing == null) {
            return;
        }
        existing.title = artist.getTitle();
        artistDao.update(existing);
    }

    @Override
    public void delete(Artist artist) {
        ArtistEntity existing = artistDao.getByMediaId(artist.getMediaId());
        if (existing != null) {
            artistDao.delete(existing);
        }
    }

    @Override
    public Artist getArtistAlbumsByMediaId(String mediaId) {
        var artistAlbums = artistDao.getArtistAlbumsByMediaId(mediaId);
        return artistAlbums == null ? null : Artist.fromEntity(artistAlbums);
    }
}
