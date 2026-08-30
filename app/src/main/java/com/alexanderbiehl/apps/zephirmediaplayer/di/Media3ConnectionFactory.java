package com.alexanderbiehl.apps.zephirmediaplayer.di;

import android.content.ComponentName;
import android.content.Context;

import androidx.media3.session.MediaBrowser;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.alexanderbiehl.apps.zephirmediaplayer.service.Media3Service;
import com.google.common.util.concurrent.ListenableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Media3ConnectionFactory implements MediaConnectionFactory {

    @Inject
    public Media3ConnectionFactory() {
    }

    @Override
    public ListenableFuture<MediaBrowser> createBrowser(Context context) {
        SessionToken sessionToken = new SessionToken(
                context,
                new ComponentName(context, Media3Service.class)
        );
        return new MediaBrowser.Builder(context, sessionToken).buildAsync();
    }

    @Override
    public ListenableFuture<MediaController> createController(Context context) {
        SessionToken sessionToken = new SessionToken(
                context,
                new ComponentName(context, Media3Service.class)
        );
        return new MediaController.Builder(context, sessionToken).buildAsync();
    }
}

