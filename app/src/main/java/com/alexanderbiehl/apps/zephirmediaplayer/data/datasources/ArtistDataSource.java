package com.alexanderbiehl.apps.zephirmediaplayer.data.datasources;

import com.alexanderbiehl.apps.zephirmediaplayer.data.models.Artist;

import java.util.List;

public interface ArtistDataSource {

    List<Artist> getAll();

    Artist getById(String mediaId);

    Long insert(Artist artist);

    void update(Artist artist);

    void delete(Artist artist);

    Artist getArtistAlbumsByMediaId(String mediaId);
}
