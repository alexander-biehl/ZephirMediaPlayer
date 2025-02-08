package com.alexanderbiehl.apps.zephirmediaplayer.datasources.song;

import com.alexanderbiehl.apps.zephirmediaplayer.common.RepositoryCallback;
import com.alexanderbiehl.apps.zephirmediaplayer.data.model.Song;

public interface SongDataSource {

    public void getSong(RepositoryCallback<Song> callback);


}
