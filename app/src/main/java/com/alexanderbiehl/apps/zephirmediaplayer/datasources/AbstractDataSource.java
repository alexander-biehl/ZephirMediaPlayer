package com.alexanderbiehl.apps.zephirmediaplayer.datasources;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;

public interface AbstractDataSource<T, D> {

    void getById(Long id, RepositoryCallback<D> callback);

    void getByMediaId(String mediaId, RepositoryCallback<D> callback);

    void create(T entity, RepositoryCallback<Long> callback);

    void update(T entity, RepositoryCallback<Void> callback);

    void delete(T entity, RepositoryCallback<Void> callback);

    void delete(Long id, RepositoryCallback<Void> callback);
}
