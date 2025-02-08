package com.alexanderbiehl.apps.zephirmediaplayer.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class SongEntityTests {
    private SongDao songDao;
    private AppDatabase db;

    @Before
    public void onBefore() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        songDao = db.songDao();
    }

    @After
    public void cleanup() throws IOException {
        db.close();
    }

    @Test
    public void testCreateAndQuerySong() throws Exception {
        SongEntity songEntity = new SongEntity();
        songEntity.title = "Test";
        songDao.insert(songEntity);
        SongEntity[] songEntities = songDao.getAllSongs();
        assertEquals(1, songEntities.length);
        assertNotNull(songEntities[0].id);
        assertEquals("Test", songEntities[0].title);
    }

    @Test
    public void testCreateAndQueryByMediaId() throws Exception {
        SongEntity songEntity = new SongEntity();
        final String mediaId = "001";
        songEntity.title = "test";
        songEntity.mediaId = mediaId;
        songDao.insert(songEntity);

        SongEntity dbSongEntity = songDao.getByMediaId(mediaId);
        assertNotNull(dbSongEntity);
        assertEquals("test", dbSongEntity.title);
        assertEquals(mediaId, dbSongEntity.mediaId);
    }

    @Test
    public void testSongDelete() throws Exception {
        SongEntity songEntity = new SongEntity();
        final String mediaID = "001";
        songEntity.title = "Test";
        songEntity.mediaId = mediaID;
        songDao.insert(songEntity);

        songEntity = songDao.getByMediaId(mediaID);
        songEntity.title = "test2";
        songDao.update(songEntity);

        SongEntity updatedSongEntity = songDao.getById(songEntity.id);
        assertEquals("test2", updatedSongEntity.title);
    }
}
