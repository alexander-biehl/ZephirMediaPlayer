package com.alexanderbiehl.apps.zephirmediaplayer.repositories;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;

import java.util.List;

public interface AbstractRepository<T> {

    void create(T data, RepositoryCallback<T> callback);

    void getById(Long id, RepositoryCallback<T> callback);

    void getByMediaId(String mediaId, RepositoryCallback<T> callback);

    void getAll(RepositoryCallback<List<T>> callback);

    void delete(T data, RepositoryCallback<Void> callback);

    void delete(Long id, RepositoryCallback<Void> callback);
}
