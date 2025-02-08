package com.alexanderbiehl.apps.zephirmediaplayer.common;

/**
 * Abstract class to represent either a positive or negative
 * result of some asynchronous work.
 * @param <T>
 */
public abstract class Result<T> {

    private Result() {}

    public static final class Success<T> extends Result<T> {
        public T data;

        public Success(T data) {
            this.data = data;
        }
    }

    public static final class Error<T> extends Result<T> {
        public Exception ex;

        public Error(Exception ex) {
            this.ex = ex;
        }
    }
}
