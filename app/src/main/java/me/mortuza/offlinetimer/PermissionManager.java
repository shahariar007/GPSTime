package me.mortuza.offlinetimer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {
    private Activity mActivity;
    private RequestPermissionListener mRequestPermissionListener;
    private int mRequestCode;

    public void requestPermission(Activity activity, @NonNull String[] permissions, int requestCode,
                                  RequestPermissionListener listener) {
        mActivity = activity;
        mRequestCode = requestCode;
        mRequestPermissionListener = listener;

        if (!needRequestRuntimePermissions()) {
            mRequestPermissionListener.onSuccess();
            return;
        }
        requestUnGrantedPermissions(permissions, requestCode);
    }

    private boolean needRequestRuntimePermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void requestUnGrantedPermissions(String[] permissions, int requestCode) {
        String[] unGrantedPermissions = findUnGrantedPermissions(permissions);
        if (unGrantedPermissions.length == 0) {
            mRequestPermissionListener.onSuccess();
            return;
        }
        ActivityCompat.requestPermissions(mActivity, unGrantedPermissions, requestCode);
    }

    private boolean isPermissionGranted(String permission) {
        return ActivityCompat.checkSelfPermission(mActivity, permission)
                == PackageManager.PERMISSION_GRANTED;

    }

    private String[] findUnGrantedPermissions(String[] permissions) {
        List<String> unGrantedPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (!isPermissionGranted(permission)) {
                unGrantedPermissionList.add(permission);
            } else {
                Log.d("PermissionManager", "findUnGrantedPermissions: go");
            }
        }
        return unGrantedPermissionList.toArray(new String[0]);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == mRequestCode) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {

                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mRequestPermissionListener.onFailed();
                        displayNeverAskAgainDialog();
                        return;
                    }
                }
                mRequestPermissionListener.onSuccess();
            } else {
                mRequestPermissionListener.onFailed();
            }
        }
    }

    private void displayNeverAskAgainDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("We need to save some data on your storage. Please permit the permission through "
                + "Settings screen.\n\nSelect Permissions -> Enable permission");
        builder.setCancelable(false);
        builder.setPositiveButton("Permit Manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
                intent.setData(uri);
                mActivity.startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
