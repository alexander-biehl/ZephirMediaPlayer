package com.alexanderbiehl.apps.zephirmediaplayer.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.media3.common.MediaItem;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alexanderbiehl.apps.zephirmediaplayer.common.Result;
import com.alexanderbiehl.apps.zephirmediaplayer.data.datasources.impl.PlaylistDbDataSource;
import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.PlaylistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.util.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class PlaylistDbDataSourceTests {

    private AppDatabase db;
    private PlaylistDao playlistDao;
    private SongDao songDao;
    private PlaylistDbDataSource dataSource;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        playlistDao = db.playlistDao();
        songDao = db.songDao();
        dataSource = new PlaylistDbDataSource(playlistDao, songDao, Runnable::run);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void renameAndDeletePlaylist_updatesDatabaseState() {
        PlaylistEntity playlist = new PlaylistEntity();
        playlist.mediaId = "[playlistEntity]rename-delete";
        playlist.title = "Before Rename";
        playlistDao.insert(playlist);

        AtomicReference<Result<Void>> renameResult = new AtomicReference<>();
        dataSource.renamePlaylist(playlist.mediaId, "After Rename", renameResult::set);
        assertTrue(renameResult.get() instanceof Result.Success);

        PlaylistEntity renamed = playlistDao.getByMediaId(playlist.mediaId);
        assertNotNull(renamed);
        assertEquals("After Rename", renamed.title);

        AtomicReference<Result<Void>> deleteResult = new AtomicReference<>();
        dataSource.deletePlaylist(playlist.mediaId, deleteResult::set);
        assertTrue(deleteResult.get() instanceof Result.Success);

        assertNull(playlistDao.getByMediaId(playlist.mediaId));
    }

    @Test
    public void addMediaItemsToPlaylist_linksSongsAndIgnoresDuplicates() {
        PlaylistEntity playlist = new PlaylistEntity();
        playlist.mediaId = "[playlistEntity]add-items";
        playlist.title = "Target Playlist";
        long playlistRowId = playlistDao.insert(playlist);
        PlaylistEntity persistedPlaylist = playlistDao.getByMediaId(playlist.mediaId);
        assertNotNull(persistedPlaylist);
        assertEquals(Long.valueOf(playlistRowId), persistedPlaylist.id);

        SongEntity songOne = TestUtils.createSongOne();
        SongEntity songTwo = TestUtils.createSongTwo();
        songDao.insertAll(songOne, songTwo);

        List<MediaItem> mediaItems = List.of(
                new MediaItem.Builder().setMediaId(TestUtils.songOneMediaId).build(),
                new MediaItem.Builder().setMediaId(TestUtils.songTwoMediaId).build()
        );

        AtomicReference<Result<Void>> firstAddResult = new AtomicReference<>();
        dataSource.addMediaItemsToPlaylist(persistedPlaylist, mediaItems, firstAddResult::set);
        assertTrue(firstAddResult.get() instanceof Result.Success);
        assertEquals(2, playlistDao.getSongsByPlaylistMediaId(playlist.mediaId).length);

        AtomicReference<Result<Void>> secondAddResult = new AtomicReference<>();
        dataSource.addMediaItemsToPlaylist(persistedPlaylist, mediaItems, secondAddResult::set);
        assertTrue(secondAddResult.get() instanceof Result.Success);

        SongEntity[] songs = playlistDao.getSongsByPlaylistMediaId(playlist.mediaId);
        assertEquals(2, songs.length);
        assertEquals(TestUtils.songOneMediaId, songs[0].mediaId);
        assertEquals(TestUtils.songTwoMediaId, songs[1].mediaId);
    }
}


