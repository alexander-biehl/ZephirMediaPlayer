package com.alexanderbiehl.apps.zephirmediaplayer.data.repositories;

import com.alexanderbiehl.apps.zephirmediaplayer.database.entity.ArtistEntity;

import java.util.ArrayList;
import java.util.List;

public class ArtistRepository {
    public List<ArtistEntity> getAlbumsByArtistId(String mediaId) {
        return new ArrayList<>();
    }

    public ArtistEntity getById(String mediaId) {
        return null;
    }

    public List<ArtistEntity> getArtists() {
        return new ArrayList<>();
    }
}
