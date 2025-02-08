package com.alexanderbiehl.apps.zephirmediaplayer.datasources;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.data.model.Artist;

import java.util.List;

public interface ArtistDataSource {

    void getById(RepositoryCallback<Artist> callback);

    void getAll(RepositoryCallback<List<Artist>> callback);

    void update(RepositoryCallback<Void> callback);

    void create(RepositoryCallback<Long> callback);

    void delete(RepositoryCallback<Void> callback);
}
