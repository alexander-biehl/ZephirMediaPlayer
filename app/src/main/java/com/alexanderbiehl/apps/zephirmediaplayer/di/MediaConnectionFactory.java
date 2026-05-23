package com.alexanderbiehl.apps.zephirmediaplayer.di;

import android.content.Context;

import androidx.media3.session.MediaBrowser;
import androidx.media3.session.MediaController;

import com.google.common.util.concurrent.ListenableFuture;

public interface MediaConnectionFactory {

    ListenableFuture<MediaBrowser> createBrowser(Context context);

    ListenableFuture<MediaController> createController(Context context);
}

