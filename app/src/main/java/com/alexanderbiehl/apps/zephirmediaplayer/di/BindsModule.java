package com.alexanderbiehl.apps.zephirmediaplayer.di;

import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.ArtistDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.MediaDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl.ArtistDbDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl.MediaLocalDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.AlbumRepositoryImpl;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.ArtistRepositoryImpl;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.PlaylistRepositoryImpl;
import com.alexanderbiehl.apps.zephirmediaplayer.data.repositories.SongRepositoryImpl;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.AlbumRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.ArtistRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.SongRepository;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.PlaylistRepository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class BindsModule {

    @Binds
    public abstract SongRepository bindSongRepository(SongRepositoryImpl impl);

    @Binds
    public abstract AlbumRepository bindAlbumRepository(AlbumRepositoryImpl impl);

    @Binds
    public abstract ArtistRepository bindArtistRepository(ArtistRepositoryImpl impl);

    @Binds
    public abstract PlaylistRepository bindPlaylistRepository(PlaylistRepositoryImpl impl);

    @Binds
    public abstract ArtistDataSource bindArtistDataSource(ArtistDbDataSource impl);

    @Binds
    public abstract MediaDataSource bindMediaDataSource(MediaLocalDataSource impl);

    @Binds
    public abstract MediaConnectionFactory bindMediaConnectionFactory(Media3ConnectionFactory impl);
}
