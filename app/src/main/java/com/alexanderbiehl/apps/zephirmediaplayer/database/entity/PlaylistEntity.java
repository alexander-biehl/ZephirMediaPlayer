package com.alexanderbiehl.apps.zephirmediaplayer.database.entity;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.room.Entity;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.base.EntityBase;

@Entity(tableName = "playlists")
public class PlaylistEntity extends EntityBase {

    public String title;

    public Integer numTracks;

    public static MediaItem toItem(PlaylistEntity entity) {
        if (entity == null) {
            return null;
        }
        return new MediaItem.Builder()
                .setMediaId(entity.mediaId)
                .setMediaMetadata(
                        new MediaMetadata.Builder()
                                .setTitle(entity.title)
                                .setIsBrowsable(true)
                                .setIsPlayable(true)
                                .setTotalTrackCount(entity.numTracks)
                                .setMediaType(MediaMetadata.MEDIA_TYPE_PLAYLIST)
                                .build())
                .build();
    }
}
