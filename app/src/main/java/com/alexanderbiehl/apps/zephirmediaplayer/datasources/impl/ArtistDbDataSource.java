package com.alexanderbiehl.apps.zephirmediaplayer.datasources.impl;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.model.Artist;
import com.alexanderbiehl.apps.zephirmediaplayer.datasources.ArtistDataSource;

import java.util.List;

public class ArtistDbDataSource implements ArtistDataSource {

    @Override
    public void getById(Long id, RepositoryCallback<Artist> callback) {

    }

    @Override
    public void getByMediaId(String mediaId, RepositoryCallback<Artist> callback) {

    }

    @Override
    public void create(ArtistEntity entity, RepositoryCallback<Long> callback) {

    }

    @Override
    public void update(ArtistEntity entity, RepositoryCallback<Void> callback) {

    }

    @Override
    public void delete(ArtistEntity entity, RepositoryCallback<Void> callback) {

    }

    @Override
    public void delete(Long id, RepositoryCallback<Void> callback) {

    }
}
