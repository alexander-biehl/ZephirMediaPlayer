package com.alexanderbiehl.apps.zephirmediaplayer.di;

import android.content.Context;

import com.alexanderbiehl.apps.zephirmediaplayer.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.AlbumDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.ArtistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.PlaylistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.SongDao;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return AppDatabase.getDatabase(context);
    }

    @Provides
    public SongDao provideSongDao(AppDatabase database) {
        return database.songDao();
    }

    @Provides
    public AlbumDao provideAlbumDao(AppDatabase database) {
        return database.albumDao();
    }

    @Provides
    public ArtistDao provideArtistDao(AppDatabase database) {
        return database.artistDao();
    }

    @Provides
    public PlaylistDao providePlaylistDao(AppDatabase database) {
        return database.playlistDao();
    }

    @Provides
    @Singleton
    public ExecutorService provideExecutorService() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Provides
    @Singleton
    public Executor provideExecutor(ExecutorService executorService) {
        return executorService;
    }
}
