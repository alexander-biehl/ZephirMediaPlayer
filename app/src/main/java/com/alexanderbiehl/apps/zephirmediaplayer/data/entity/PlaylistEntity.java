package com.alexanderbiehl.apps.zephirmediaplayer.data.entity;

import androidx.room.Entity;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.base.EntityBase;

@Entity(tableName = "playlists")
public class PlaylistEntity extends EntityBase {

    public String title;
}
