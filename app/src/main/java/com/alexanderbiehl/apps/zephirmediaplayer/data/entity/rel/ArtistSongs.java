package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.rel;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Artist;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.Song;

import java.util.List;

@Entity
public class ArtistSongs {

    @Embedded
    public Artist artist;
    @Relation(
            parentColumn = "id",
            entityColumn = "songArtistId"
    )
    public List<Song> songs;
}
