package com.xando.chefsclub.Helpers;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

public class PermissionHelper {

    private Fragment mFragment;
    private String[] mPermissions;
    private Activity mActivity;

    public PermissionHelper(@NonNull Fragment fragment, @NonNull String[] permissions) {
        this(fragment.getActivity(), permissions);

        if (fragment.getActivity() == null) throw new NullPointerException("Fragment must" +
                " be attached to activity");

        mFragment = fragment;
    }

    public PermissionHelper(@NonNull Activity activity, @NonNull String[] permissions) {
        this(activity);

        mPermissions = permissions;
    }

    public PermissionHelper(@NonNull Fragment fragment) {
        this(fragment.getActivity());

        mFragment = fragment;
    }

    public PermissionHelper(@NonNull Activity activity) {
        mActivity = activity;
    }

    public String[] getPermissions() {
        return mPermissions;
    }

    public PermissionHelper setPermissions(String[] permissions) {
        mPermissions = permissions;

        return this;
    }

    public boolean isPermissionsGranted() {
        for (String permission : mPermissions) {
            if (ActivityCompat.checkSelfPermission(mActivity,
                    permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public void requestPermissions(int requestCode) {
        if (mFragment != null)
            requestPermissionsFragment(requestCode);
        else requestPermissionsActivity(requestCode);
    }

    private void requestPermissionsActivity(int requestCode) {
        ActivityCompat.requestPermissions(mActivity, mPermissions,
                requestCode);
    }

    private void requestPermissionsFragment(int requestCode) {
        mFragment.requestPermissions(mPermissions, requestCode);
    }
}
