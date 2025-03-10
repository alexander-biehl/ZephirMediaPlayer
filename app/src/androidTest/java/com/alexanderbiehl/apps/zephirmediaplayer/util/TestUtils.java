package com.alexanderbiehl.apps.zephirmediaplayer.util;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;

public class TestUtils {

    public static final String songOneMediaId = "000001";
    public static final String songTwoMediaId = "000002";

    public static final String albumOneMediaId = "000010";
    public static final String albumTwoMediaId = "000020";

    public static final String artistMediaId = "000100";

    public static SongEntity createSongOne() {
        SongEntity s = new SongEntity();
        s.mediaId = songOneMediaId;
        s.title = "SongEntity One";
        s.trackNumber = "1";
        return s;
    }

    public static SongEntity createSongTwo() {
        SongEntity s = new SongEntity();
        s.mediaId = songTwoMediaId;
        s.title = "SongEntity Two";
        s.trackNumber = "2";
        return s;
    }

    public static AlbumEntity createAlbumOne() {
        AlbumEntity a = new AlbumEntity();
        a.mediaId = albumOneMediaId;
        a.title = "AlbumEntity One";
        return a;
    }

    public static AlbumEntity createAlbumTwo() {
        AlbumEntity a = new AlbumEntity();
        a.mediaId = albumTwoMediaId;
        a.title = "AlbumEntity Two";
        return a;
    }

    public static ArtistEntity createArtist() {
        ArtistEntity a = new ArtistEntity();
        a.mediaId = artistMediaId;
        a.title = "ArtistEntity One";
        return a;
    }
}
