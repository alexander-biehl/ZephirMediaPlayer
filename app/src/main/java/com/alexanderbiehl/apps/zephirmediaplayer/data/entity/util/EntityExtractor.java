package com.alexanderbiehl.apps.zephirmediaplayer.data.entity.util;

import android.content.ContentUris;
import android.provider.MediaStore;

import androidx.media3.common.MediaItem;

import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.AlbumEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.ArtistEntity;
import com.alexanderbiehl.apps.zephirmediaplayer.data.entity.SongEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityExtractor {

    public static List<ArtistEntity> extractArtistEntities(List<MediaItem> items) {

        Map<String, ArtistEntity> entityMap = new HashMap<>();
        for (MediaItem item : items) {
            String artist = item.mediaMetadata.artist.toString();
            String mediaId = item.mediaId;
            if (!entityMap.containsKey(artist)) {
                entityMap.put(artist, new ArtistEntity(artist, mediaId));
            }
        }
        return new ArrayList<>(entityMap.values());
    }

    public static List<AlbumEntity> extractAlbumEntities(List<MediaItem> items, Map<String, Long> artistIdMap) {

        Map<String, AlbumEntity> entityMap = new HashMap<>();
        for (MediaItem item : items) {
            String album = item.mediaMetadata.albumTitle.toString();
            String artist = item.mediaMetadata.artist.toString();
            String mediaId = item.mediaId;
            if (!entityMap.containsKey(album)) {
                entityMap.put(album, new AlbumEntity(album, mediaId, artistIdMap.get(artist)));
            }
        }
        return new ArrayList<>(entityMap.values());
    }

    public static List<SongEntity> extractSongEntities(
            List<MediaItem> items,
            Map<String, Long> artistIdMap,
            Map<String, Long> albumIdMap
    ) {
        return items
                .stream()
                .map(e -> new SongEntity(
                        e.mediaMetadata.title.toString(),
                        e.mediaId,
                        String.valueOf(e.mediaMetadata.trackNumber),
                        ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                Long.parseLong(e.mediaId)
                        ).toString(),
                        artistIdMap.get(e.mediaMetadata.artist.toString()),
                        albumIdMap.get(e.mediaMetadata.albumTitle.toString())
                ))
                .collect(Collectors.toList());
    }
}
