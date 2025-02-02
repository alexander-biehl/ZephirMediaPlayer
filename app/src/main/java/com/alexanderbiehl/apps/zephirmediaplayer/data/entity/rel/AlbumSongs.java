package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Album;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;

import java.util.List;

public class AlbumSongs {

    @Embedded
    public Album album;
    @Relation(
            parentColumn = "id",
            entityColumn = "songAlbumId"
    )
    public List<Song> songs;
}
