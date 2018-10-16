package com.example.nikis.bludogramfirebase.BaseFragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.nikis.bludogramfirebase.Helpers.MatisseHelper;
import com.example.nikis.bludogramfirebase.Helpers.PermissionHelper;
import com.zhihu.matisse.Matisse;

import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


public abstract class BaseFragmentWithMatisseGallery extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 5;
    private static final int REQUEST_CODE_CHOOSE = 6;

    private final String[] mPermissions = new String[]{READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE};

    private PermissionHelper mPermissionHelper;

    private int mCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionHelper = new PermissionHelper(this, mPermissions);

    }

    protected void startMatisseGallery(int count) {
        this.mCount = count;

        if(mPermissionHelper.isPermissionsGranted()){
            startGallery();
        }else mPermissionHelper.requestPermissionsFragment(PERMISSION_REQUEST_CODE);

    }

    private void startGallery() {
        MatisseHelper.getMatisseBuilder(this, mCount)
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startGallery();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> selected = Matisse.obtainResult(data);

            onGalleryFinish(selected);
        }
    }

    protected abstract void onGalleryFinish(List<Uri> selected);

    protected String[] covert(List<Uri> selected){
        String[] paths = new String[selected.size()];

        for (int i = 0; i < paths.length; i++) {
            paths[i] = MatisseHelper.getRealPathFromURIPath(getActivity(), selected.get(i));
        }

        return paths;
    }
}
