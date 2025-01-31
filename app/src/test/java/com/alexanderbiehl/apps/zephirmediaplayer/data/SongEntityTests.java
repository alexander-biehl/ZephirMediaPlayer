package com.alexanderbiehl.apps.zephirmediaplayer.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alexanderbiehl.apps.zephirmediaplayer.data.dao.SongDao;
import com.alexanderbiehl.apps.zephirmediaplayer.data.database.AppDatabase;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

// @RunWith(AndroidJUnit4.class)
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
        Song song = new Song();
        song.title = "Test";
        songDao.insert(song);
        Song[] songs = songDao.getAllSongs();
        assertEquals(1, songs.length);
        assertNotNull(songs[0].id);
        assertEquals("Test", songs[0].title);
    }
}
