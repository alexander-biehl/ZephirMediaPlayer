package com.alexanderbiehl.apps.zephirmediaplayer.di;

import android.content.Context;

import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl.ArtistDbDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl.PlaylistDbDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.AlbumRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.ArtistRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.PlaylistRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.SongRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.datastore.ZephirDataStore;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.MediaItemUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.AddMediaItemsToPlaylistUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.CreatePlaylistUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.DeletePlaylistUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.GetPlaylistsUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.RenamePlaylistUseCase;

import java.util.concurrent.Executor;

public class AppContainer {

    private final Context appContext;
    private final Executor executor;

    private AppDatabase database;
    private PlaylistRepository playlistRepository;
    private SongRepository songRepository;
    private AlbumRepository albumRepository;
    private ArtistRepository artistRepository;
    private MediaItemUseCase mediaItemUseCase;
    private MediaConnectionFactory mediaConnectionFactory;
    private GetPlaylistsUseCase getPlaylistsUseCase;
    private CreatePlaylistUseCase createPlaylistUseCase;
    private RenamePlaylistUseCase renamePlaylistUseCase;
    private DeletePlaylistUseCase deletePlaylistUseCase;
    private AddMediaItemsToPlaylistUseCase addMediaItemsToPlaylistUseCase;

    private ZephirDataStore dataStore;

    public AppContainer(Context appContext, Executor executor) {
        this.appContext = appContext;
        this.executor = executor;
    }

    public synchronized AppDatabase getDatabase() {
        if (database == null) {
            database = AppDatabase.getDatabase(appContext);
        }
        return database;
    }

    public synchronized PlaylistRepository getPlaylistRepository() {
        if (playlistRepository == null) {
            playlistRepository = new PlaylistRepository(
                    new PlaylistDbDataSource(getDatabase().playlistDao(), getDatabase().songDao(), executor)
            );
        }
        return playlistRepository;
    }

    public synchronized SongRepository getSongRepository() {
        if (songRepository == null) {
            songRepository = new SongRepository(getDatabase().songDao());
        }
        return songRepository;
    }

    public synchronized AlbumRepository getAlbumRepository() {
        if (albumRepository == null) {
            albumRepository = new AlbumRepository(getDatabase().albumDao());
        }
        return albumRepository;
    }

    public synchronized ArtistRepository getArtistRepository() {
        if (artistRepository == null) {
            artistRepository = new ArtistRepository(
                    new ArtistDbDataSource(getDatabase().artistDao())
            );
        }
        return artistRepository;
    }

    public synchronized MediaItemUseCase getMediaItemUseCase() {
        if (mediaItemUseCase == null) {
            mediaItemUseCase = new MediaItemUseCase(
                    getPlaylistRepository(),
                    getSongRepository(),
                    getAlbumRepository(),
                    getArtistRepository()
            );
        }
        return mediaItemUseCase;
    }

    public synchronized MediaConnectionFactory getMediaConnectionFactory() {
        if (mediaConnectionFactory == null) {
            mediaConnectionFactory = new Media3ConnectionFactory();
        }
        return mediaConnectionFactory;
    }

    public synchronized GetPlaylistsUseCase getGetPlaylistsUseCase() {
        if (getPlaylistsUseCase == null) {
            getPlaylistsUseCase = new GetPlaylistsUseCase(getPlaylistRepository());
        }
        return getPlaylistsUseCase;
    }

    public synchronized CreatePlaylistUseCase getCreatePlaylistUseCase() {
        if (createPlaylistUseCase == null) {
            createPlaylistUseCase = new CreatePlaylistUseCase(getPlaylistRepository());
        }
        return createPlaylistUseCase;
    }

    public synchronized RenamePlaylistUseCase getRenamePlaylistUseCase() {
        if (renamePlaylistUseCase == null) {
            renamePlaylistUseCase = new RenamePlaylistUseCase(getPlaylistRepository());
        }
        return renamePlaylistUseCase;
    }

    public synchronized DeletePlaylistUseCase getDeletePlaylistUseCase() {
        if (deletePlaylistUseCase == null) {
            deletePlaylistUseCase = new DeletePlaylistUseCase(getPlaylistRepository());
        }
        return deletePlaylistUseCase;
    }

    public synchronized AddMediaItemsToPlaylistUseCase getAddMediaItemsToPlaylistUseCase() {
        if (addMediaItemsToPlaylistUseCase == null) {
            addMediaItemsToPlaylistUseCase = new AddMediaItemsToPlaylistUseCase(getPlaylistRepository());
        }
        return addMediaItemsToPlaylistUseCase;
    }

    public synchronized ZephirDataStore getDataStore() {
        if (dataStore == null) {
            dataStore = new ZephirDataStore(appContext);
        }
        return dataStore;
    }
}


