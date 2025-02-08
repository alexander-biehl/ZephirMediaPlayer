package com.alexanderbiehl.apps.zephirmediaplayer.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.AlbumDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.AlbumSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.util.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class AlbumEntityTests {

    private AlbumDao dao;
    private AppDatabase db;

    @Before
    public void onBefore() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        dao = db.albumDao();
    }

    @After
    public void cleanup() throws IOException {
        db.close();
    }

    @Test
    public void testCreate() throws Exception {
        dao.insert(TestUtils.createAlbumOne());
        AlbumEntity a = dao.getByMediaId(TestUtils.albumOneMediaId);
        assertNotNull(a.id);
        assertEquals("AlbumEntity One", a.title);
    }

    @Test
    public void testUpdate() throws Exception {
        dao.insert(TestUtils.createAlbumOne());
        AlbumEntity a = dao.getByMediaId(TestUtils.albumOneMediaId);
        assertEquals("AlbumEntity One", a.title);
        a.title = "AlbumEntity 1";
        dao.update(a);
        a = dao.getById(a.id);
        assertEquals("AlbumEntity 1", a.title);
    }

    @Test
    public void testDelete() throws Exception {
        dao.insert(TestUtils.createAlbumOne());
        AlbumEntity a = dao.getByMediaId(TestUtils.albumOneMediaId);
        dao.delete(a);
        AlbumEntity[] albumEntities = dao.getAll();
        assertEquals(0, albumEntities.length);
    }

    @Test
    public void testGetSongsForAlbum() throws IOException {
        SongDao songDao = db.songDao();
        dao.insert(TestUtils.createAlbumOne());

        AlbumEntity a = dao.getByMediaId(TestUtils.albumOneMediaId);
        SongEntity songEntityOne = TestUtils.createSongOne();
        SongEntity songEntityTwo = TestUtils.createSongTwo();
        songEntityOne.songAlbumId = a.id;
        songEntityTwo.songAlbumId = a.id;
        songDao.insert(songEntityOne);
        songDao.insert(songEntityTwo);

        AlbumSongs albumSongs = dao.getAlbumSongsByMediaId(TestUtils.albumOneMediaId);

        assertNotNull(albumSongs);
        assertEquals(a.id, albumSongs.albumEntity.id);
        assertEquals(2, albumSongs.songEntities.size());
    }
}
