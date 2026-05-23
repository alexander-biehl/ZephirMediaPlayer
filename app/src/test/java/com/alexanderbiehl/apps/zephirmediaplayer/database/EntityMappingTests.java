package com.alexanderbiehl.apps.zephirmediaplayer.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.PlaylistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;

import org.junit.Test;

public class EntityMappingTests {

    @Test
    public void playlistEntity_toItem_mapsCoreMetadata() {
        PlaylistEntity playlist = new PlaylistEntity();
        playlist.mediaId = "playlist-1";
        playlist.title = "Road Trip";

        MediaItem item = PlaylistEntity.toItem(playlist);

        assertNotNull(item);
        assertEquals("playlist-1", item.mediaId);
        assertEquals("Road Trip", String.valueOf(item.mediaMetadata.title));
        assertEquals(Boolean.TRUE, item.mediaMetadata.isBrowsable);
        assertEquals(Boolean.TRUE, item.mediaMetadata.isPlayable);
    }

    @Test
    public void songEntity_toItem_mapsUriAndTrackNumber() {
        SongEntity song = new SongEntity();
        song.mediaId = "song-1";
        song.title = "Track One";
        song.trackNumber = "7";
        song.sourceUri = null;

        MediaItem item = SongEntity.toItem(song);

        assertNotNull(item);
        assertEquals("song-1", item.mediaId);
        assertEquals("Track One", String.valueOf(item.mediaMetadata.title));
        assertEquals(Integer.valueOf(7), item.mediaMetadata.trackNumber);
    }
}



