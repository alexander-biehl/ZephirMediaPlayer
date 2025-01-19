package com.alexanderbiehl.apps.zephirmediaplayer.service;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.util.UnstableApi;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

public class MediaItemTree {

    private static final String ROOT_ID = "[rootID]";
    private static final String ALBUM_ID = "[albumID]";
    private static final String ARTIST_ID = "[artistID]";
    private static final String ALBUM_PREFIX = "[album]";
    private static final String ARTIST_PREFIX = "[artist]";
    private static final String ITEM_PREFIX = "[item]";
    private static MediaItemTree INSTANCE;
    private final TreeMap<String, MediaItemNode> treeNodes;
    private boolean isInitialized;

    private MediaItemTree() {
        this.isInitialized = false;
        this.treeNodes = new TreeMap<>();
    }

    public static synchronized MediaItemTree getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MediaItemTree();
        }
        return INSTANCE;
    }

    private MediaItem buildMediaItem(
            String title,
            String mediaID,
            boolean isPlayable,
            boolean isBrowsable,
            @MediaMetadata.MediaType int mediaType
    ) {
        return buildMediaItem(
                title,
                mediaID,
                isPlayable,
                isBrowsable,
                mediaType,
                "",
                "",
                null
        );
    }

    private MediaItem buildMediaItem(
            String title,
            String mediaID,
            boolean isPlayable,
            boolean isBrowsable,
            @MediaMetadata.MediaType int mediaType,
            String album,
            String artist,
            Uri sourceUri
    ) {
        MediaMetadata metadata = new MediaMetadata.Builder()
                .setAlbumTitle(album)
                .setTitle(title)
                .setArtist(artist)
                .setIsBrowsable(isBrowsable)
                .setIsPlayable(isPlayable)
                .setMediaType(mediaType)
                .build();

        return new MediaItem.Builder()
                .setMediaId(mediaID)
                .setMediaMetadata(metadata)
                .setUri(sourceUri)
                .build();
    }

    public void initialize(List<MediaItem> media) {
        if (this.isInitialized) {
            return;
        }
        this.isInitialized = true;

        // create root and folders for album/artist
        treeNodes.put(
                ROOT_ID,
                new MediaItemNode(
                        buildMediaItem(
                                "Root Folder",
                                ROOT_ID,
                                false,
                                true,
                                MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
                        )
                )
        );
        treeNodes.put(
                ALBUM_ID,
                new MediaItemNode(
                        buildMediaItem(
                                "Albums",
                                ALBUM_ID,
                                false,
                                true,
                                MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS
                        )
                )
        );
        treeNodes.put(
                ARTIST_ID,
                new MediaItemNode(
                        buildMediaItem(
                                "Artists",
                                ARTIST_ID,
                                false,
                                true,
                                MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS
                        )
                )
        );

        // TODO add playlists folder

        treeNodes.get(ROOT_ID).addChild(ALBUM_ID);
        treeNodes.get(ROOT_ID).addChild(ARTIST_ID);

        for (MediaItem item : media) {
            addNodeToTree(item);
        }
    }

    private void addNodeToTree(MediaItem item) {
        final String idInTree = ITEM_PREFIX + item.mediaId;
        final String albumFolderIdInTree = ALBUM_PREFIX + item.mediaMetadata.albumTitle;
        final String artistFolderIdInTree = ARTIST_PREFIX + item.mediaMetadata.artist;

        // store node in tree
        treeNodes.put(
                idInTree,
                new MediaItemNode(
                        buildMediaItem(
                                item.mediaMetadata.title.toString(),
                                idInTree,
                                true,
                                false,
                                MediaMetadata.MEDIA_TYPE_MUSIC,
                                item.mediaMetadata.albumTitle.toString(),
                                item.mediaMetadata.artist.toString(),
                                ContentUris.withAppendedId(
                                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                        Long.parseLong(item.mediaId)
                                )
                        )
                )
        );

        // if album isn't already in tree
        if (!treeNodes.containsKey(albumFolderIdInTree)) {
            treeNodes.put(
                    albumFolderIdInTree,
                    new MediaItemNode(
                            buildMediaItem(
                                    item.mediaMetadata.albumTitle.toString(),
                                    albumFolderIdInTree,
                                    true,
                                    true,
                                    MediaMetadata.MEDIA_TYPE_ALBUM
                            )
                    )
            );
            treeNodes.get(ALBUM_ID).addChild(albumFolderIdInTree);
        }
        treeNodes.get(albumFolderIdInTree).addChild(idInTree);

        // if artist isn't already in tree
        if (!treeNodes.containsKey(artistFolderIdInTree)) {
            treeNodes.put(
                    artistFolderIdInTree,
                    new MediaItemNode(
                            buildMediaItem(
                                    item.mediaMetadata.artist.toString(),
                                    artistFolderIdInTree,
                                    true,
                                    true,
                                    MediaMetadata.MEDIA_TYPE_ARTIST
                            )
                    )
            );
            treeNodes.get(ARTIST_ID).addChild(artistFolderIdInTree);
        }
        treeNodes.get(artistFolderIdInTree).addChild(idInTree);
    }

    public Optional<MediaItem> getItem(String id) {
        return Optional.ofNullable(treeNodes.get(id).getItem());
    }

    @OptIn(markerClass = UnstableApi.class)
    public Optional<MediaItem> expandItem(MediaItem item) {
        Optional<MediaItem> treeItem = getItem(item.mediaId);
        if (treeItem.isEmpty()) {
            return Optional.empty();
        }
        MediaItem foundItem = treeItem.get();
        MediaMetadata metadata = foundItem.mediaMetadata.buildUpon()
                .populate(item.mediaMetadata).build();
        return Optional.of(
                item.buildUpon().setMediaMetadata(metadata)
                        .setUri(foundItem.localConfiguration.uri).build()
        );
    }

    /**
     * Returns the media ID of the parent of the given media ID, or null if the media ID
     * wasn't found.
     *
     * @param mediaID  The media ID of which to search the parent.
     * @param parentID The optional media ID of the media item to start the search from,
     *                 or undefined to search
     * @return Optional of the found ID.
     */
    public Optional<String> getParentID(String mediaID, Optional<String> parentID) {
        String resolvedParentID = parentID.orElse(ROOT_ID);
        for (MediaItem child : treeNodes.get(resolvedParentID).getChildren()) {
            if (child.mediaId.equals(mediaID)) {
                return Optional.of(resolvedParentID);
            } else if (child.mediaMetadata.isBrowsable) {
                Optional<String> nextParentID = getParentID(mediaID, Optional.of(child.mediaId));
                if (nextParentID.isPresent()) {
                    return nextParentID;
                }
            }
        }
        return Optional.empty();
    }

    public MediaItem getRootItem() {
        return treeNodes.get(ROOT_ID).getItem();
    }

    public List<MediaItem> getChildren(String id) {
        return treeNodes.containsKey(id) ?
                treeNodes.get(id).getChildren() :
                new ArrayList<>();
    }

    private class MediaItemNode {
        private final List<MediaItem> children;
        private final MediaItem item;

        public MediaItemNode(MediaItem mediaItem) {
            this.children = new ArrayList<>();
            this.item = mediaItem;
        }

        public MediaItem getItem() {
            return this.item;
        }

        public void addChild(String childID) {
            if (treeNodes.containsKey(childID)) {
                this.children.add(treeNodes.get(childID).getItem());
            }
        }

        public List<MediaItem> getChildren() {
            return ImmutableList.copyOf(this.children);
        }
    }
}
