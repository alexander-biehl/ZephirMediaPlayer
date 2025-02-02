package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Album;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base.EntityBase;

import java.util.List;

@Entity
public class AlbumSongs {

    @Embedded
    public Album album;
    @Relation(
            parentColumn = "id",
            entityColumn = "songAlbumId"
    )
    public List<Song> songs;
}
