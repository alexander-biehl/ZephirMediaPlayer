package com.alexanderbiehl.apps.zephirmediaplayer.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StorageUtilities {

    // Storage Permissions
    public static final int REQUEST_CODE = 1;

    // index 0 must always be the "read storage" permission - callers key off of
    // grantResults[0] to decide whether the user granted library access.
    private static final String[] PERMISSIONS_STORAGE_TIRAMISU_PLUS = {
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
    };

    private static final String[] PERMISSIONS_STORAGE_LEGACY = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static String[] getRequiredPermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? PERMISSIONS_STORAGE_TIRAMISU_PLUS
                : PERMISSIONS_STORAGE_LEGACY;
    }

    /**
     * Checks to see if the read storage permission has been given to our app,
     * opens a dialog to request the permissions.
     *
     * @param activity
     */
    public static boolean verifyStoragePermission(Activity activity) {
        String readPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_AUDIO
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        int permission = ContextCompat.checkSelfPermission(activity, readPermission);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted so we need to ask the user
            ActivityCompat.requestPermissions(
                    activity,
                    getRequiredPermissions(),
                    REQUEST_CODE
            );
            return false;
        }
        return true;
    }
}
