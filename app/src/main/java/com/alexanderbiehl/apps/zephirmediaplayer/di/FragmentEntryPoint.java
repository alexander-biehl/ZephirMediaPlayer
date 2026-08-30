package com.alexanderbiehl.apps.zephirmediaplayer.di;

import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.AddMediaItemsToPlaylistUseCase;
import com.alexanderbiehl.apps.zephirmediaplayer.domain.playlists.GetPlaylistsUseCase;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * Dependency accessor for fragments that are launched directly in a non-Hilt container
 * by FragmentScenario-based instrumented tests (MediaListFragment, NowPlayingFragment,
 * QueueFragment). Those fragments can't use @AndroidEntryPoint field injection because
 * Hilt requires an @AndroidEntryPoint host Activity, and FragmentScenario's container
 * Activity isn't one. Resolved via EntryPointAccessors.fromApplication(...) instead.
 */
@EntryPoint
@InstallIn(SingletonComponent.class)
public interface FragmentEntryPoint {

    MediaConnectionFactory mediaConnectionFactory();

    GetPlaylistsUseCase getPlaylistsUseCase();

    AddMediaItemsToPlaylistUseCase addMediaItemsToPlaylistUseCase();
}
