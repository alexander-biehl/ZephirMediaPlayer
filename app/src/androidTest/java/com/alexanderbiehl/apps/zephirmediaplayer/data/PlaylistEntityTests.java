package com.alexanderbiehl.apps.zephirmediaplayer.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.PlaylistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Playlist;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.PlaylistSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.m2m.PlaylistSongM2M;
import com.alexanderbiehl.apps.zephirmediaplayer.util.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class PlaylistEntityTests {


    private PlaylistDao dao;
    private SongDao songDao;
    private AppDatabase db;

    @Before
    public void onBefore() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        dao = db.playlistDao();
        songDao = db.songDao();
    }

    @After
    public void cleanup() throws IOException {
        db.close();
    }

    @Test
    public void testCreateAndGetPlaylist() throws IOException {
        Playlist p = new Playlist();
        p.title = "Playlist One";
        long rowId = dao.insert(p);

        Song one = TestUtils.createSongOne();
        Song two = TestUtils.createSongTwo();
        long[] songIds = songDao.insertAll(one, two);

        PlaylistSongM2M ps1 = new PlaylistSongM2M();
        ps1.playlistId = p.id;
        ps1.songId = songIds[0];
        ps1.order = 0;

        PlaylistSongM2M ps2 = new PlaylistSongM2M();
        ps2.playlistId = p.id;
        ps2.songId = songIds[1];
        ps2.order = 1;

        dao.addToPlaylist(ps1, ps2);

        PlaylistSongs ps = dao.getPlaylistSongsById(p.id);
        assertNotNull(ps);
        assertEquals(p.id, ps.playlist.id);
        assertEquals(2, ps.songs.size());
    }
}
