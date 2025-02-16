package com.alexanderbiehl.apps.zephirmediaplayer.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class StorageUtilities {

    // Storage Permissions
    public static final int REQUEST_CODE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            // Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
    };

    /**
     * Checks to see if the read storage permission has been given to our app,
     * opens a dialog to request the permissions.
     *
     * @param activity
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static boolean verifyStoragePermission(Activity activity) {
        // check if we have read permission
        int permission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_MEDIA_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted so we need to ask the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_CODE
            );
            return false;
        }
        return true;
    }
}
