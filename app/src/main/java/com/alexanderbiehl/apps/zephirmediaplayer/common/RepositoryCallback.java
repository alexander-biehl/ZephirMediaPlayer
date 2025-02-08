package com.alexanderbiehl.apps.zephirmediaplayer.common;

/**
 * Interface representing a completable callback to return from
 * a repository.
 * @param <T>
 */
public interface RepositoryCallback<T> {

    void onComplete(Result<T> result);
}
