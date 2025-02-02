package com.alexanderbiehl.apps.zephirmediaplayer.util;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Album;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Artist;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;

public class TestUtils {

    public static final String songOneMediaId = "000001";
    public static final String songTwoMediaId = "000002";

    public static final String albumOneMediaId = "000010";
    public static final String albumTwoMediaId = "000020";

    public static final String artistMediaId = "000100";

    public static Song createSongOne() {
        Song s = new Song();
        s.mediaId = songOneMediaId;
        s.title = "Song One";
        s.trackNumber = "1";
        return s;
    }

    public static Song createSongTwo() {
        Song s = new Song();
        s.mediaId = songTwoMediaId;
        s.title = "Song Two";
        s.trackNumber = "2";
        return s;
    }

    public static Album createAlbumOne() {
        Album a = new Album();
        a.mediaId = albumOneMediaId;
        a.title = "Album One";
        return a;
    }

    public static Album createAlbumTwo() {
        Album a = new Album();
        a.mediaId = albumTwoMediaId;
        a.title = "Album Two";
        return a;
    }

    public static Artist createArtist() {
        Artist a = new Artist();
        a.mediaId = artistMediaId;
        a.title = "Artist One";
        return a;
    }
}
