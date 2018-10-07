package com.example.nikis.bludogramfirebase.Helpers;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

public class PermissionHelper {
    private Fragment mFragment;
    private String[] mPermissions;

    public PermissionHelper(Fragment fragment, String[] permissions) {
        mFragment = fragment;
        mPermissions = permissions;
    }

    public boolean isPermissionsGranted(){
        for (String permission : mPermissions) {
            if(ActivityCompat.checkSelfPermission(mFragment.getActivity(),
                    permission)!= PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public void requestPermissionsActivity(int requestCode) {
        ActivityCompat.requestPermissions(mFragment.getActivity(), mPermissions,
                requestCode);
    }
    public void requestPermissionsFragment(int requestCode){
        mFragment.requestPermissions(mPermissions, requestCode);
    }
}
