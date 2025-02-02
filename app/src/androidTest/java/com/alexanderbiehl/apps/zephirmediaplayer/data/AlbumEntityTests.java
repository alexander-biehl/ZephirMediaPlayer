package com.alexanderbiehl.apps.zephirmediaplayer.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.runners.model.MultipleFailureException.assertEmpty;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.AlbumDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Album;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.AlbumSongs;
import com.alexanderbiehl.apps.zephirmediaplayer.util.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

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
        Album a = dao.getByMediaId(TestUtils.albumOneMediaId);
        assertNotNull(a.id);
        assertEquals("Album One", a.title);
    }

    @Test
    public void testUpdate() throws Exception {
        dao.insert(TestUtils.createAlbumOne());
        Album a = dao.getByMediaId(TestUtils.albumOneMediaId);
        assertEquals("Album One", a.title);
        a.title = "Album 1";
        dao.update(a);
        a = dao.getById(a.id);
        assertEquals("Album 1", a.title);
    }

    @Test
    public void testDelete() throws Exception {
        dao.insert(TestUtils.createAlbumOne());
        Album a = dao.getByMediaId(TestUtils.albumOneMediaId);
        dao.delete(a);
        Album[] albums = dao.getAll();
        assertEquals(0, albums.length);
    }

    @Test
    public void testGetSongsForAlbum() throws IOException {
        SongDao songDao = db.songDao();
        dao.insert(TestUtils.createAlbumOne());

        Album a = dao.getByMediaId(TestUtils.albumOneMediaId);
        Song songOne = TestUtils.createSongOne();
        Song songTwo = TestUtils.createSongTwo();
        songOne.songAlbumId = a.id;
        songTwo.songAlbumId = a.id;
        songDao.insert(songOne);
        songDao.insert(songTwo);

        List<AlbumSongs> albumSongs = dao.getAlbumSongsByMediaId(TestUtils.albumOneMediaId);

        assertEquals(1, albumSongs.size());
        AlbumSongs albumOneSongs = albumSongs.get(0);
        assertEquals(a.id, albumOneSongs.album.id);
        assertEquals(2, albumOneSongs.songs.size());
    }
}
