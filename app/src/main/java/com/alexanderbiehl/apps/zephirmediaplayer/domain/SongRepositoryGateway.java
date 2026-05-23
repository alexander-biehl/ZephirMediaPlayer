package com.alexanderbiehl.apps.zephirmediaplayer.domain;

import androidx.annotation.NonNull;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.SongEntity;

public interface SongRepositoryGateway {

    SongEntity getById(@NonNull String mediaId);
}

