package com.alexanderbiehl.apps.zephirmediaplayer.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.AlbumDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.ArtistDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Album;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Artist;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel.ArtistAlbums;
import com.alexanderbiehl.apps.zephirmediaplayer.util.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class ArtistEntityTests {

    private ArtistDao dao;
    private AppDatabase db;

    @Before
    public void onBefore() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        dao = db.artistDao();
    }

    @After
    public void cleanup() throws IOException {
        db.close();
    }

    @Test
    public void testCreate() throws IOException {
        dao.insert(TestUtils.createArtist());
        Artist a = dao.getByMediaId(TestUtils.artistMediaId);
        assertNotNull(a);
        assertEquals("Artist One", a.title);
    }

    @Test
    public void testUpdate() throws IOException {
        dao.insert(TestUtils.createArtist());
        Artist a = dao.getByMediaId(TestUtils.artistMediaId);
        assertNotNull(a);
        assertEquals("Artist One", a.title);
        a.title = "Artist 1";
        dao.update(a);
        a = dao.getById(a.id);
        assertEquals("Artist 1", a.title);
    }

    @Test
    public void testDelete() throws IOException {
        dao.insert(TestUtils.createArtist());
        Artist a = dao.getByMediaId(TestUtils.artistMediaId);
        assertNotNull(a);
        dao.delete(a);
        Artist[] artists = dao.getArtists();
        assertEquals(0, artists.length);
    }

    @Test
    public void testGetAlbumsForArtist() throws IOException {
        dao.insert(TestUtils.createArtist());
        Artist a = dao.getByMediaId(TestUtils.artistMediaId);

        AlbumDao albumDao = db.albumDao();
        Album one = TestUtils.createAlbumOne();
        Album two = TestUtils.createAlbumTwo();

        one.albumArtistId = a.id;
        two.albumArtistId = a.id;
        albumDao.insertAll(one, two);

        ArtistAlbums albums = dao.getArtistAlbumsByMediaId(TestUtils.artistMediaId);
        assertNotNull(albums);
        assertEquals(a.id, albums.artist.id);
        assertEquals(2, albums.albums.size());
    }
}
