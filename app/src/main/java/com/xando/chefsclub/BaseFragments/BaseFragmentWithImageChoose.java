package com.xando.chefsclub.BaseFragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.Helpers.MatisseHelper;
import com.xando.chefsclub.Helpers.PermissionHelper;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


public abstract class BaseFragmentWithImageChoose extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 5;
    private static final int REQUEST_CODE_CHOOSE_MATISSE = 6;
    private static final int REQUEST_CODE_CHOOSE_DIALOG = 12;
    private static final int REQUEST_CODE_PHOTO = 13;

    private final String[] mPermissions = new String[]{READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE};

    private PermissionHelper mPermissionHelper;

    private DialogFragment mChooseActDialog;

    private File mPhotoFile;

    private int mCount;

    protected List<String> capturePathsToDelete = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionHelper = new PermissionHelper(this, mPermissions);

        mChooseActDialog = new ChooseImagePickerDialog();
        mChooseActDialog.setTargetFragment(this, REQUEST_CODE_CHOOSE_DIALOG);
    }


    protected void showChooseDialog(int count) {
        this.mCount = count;

        mChooseActDialog.show(getFragmentManager(), "Choose_action");
    }

    protected void createNewPhoto() {
        mPhotoFile = createPhotoFile();

        if (!mPhotoFile.getParentFile().exists())
            mPhotoFile.getParentFile().mkdirs();

        if (!mPhotoFile.exists()) {
            try {
                mPhotoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final PackageManager packageManager = getActivity().getPackageManager();

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;

        if (canTakePhoto) {
            startActivityForResult(makeCaptureIntent(captureImage), REQUEST_CODE_PHOTO);
        }
    }

    protected File createPhotoFile() {
        String unique = UUID.randomUUID().toString();
        return createPhotoFile(unique);
    }

    protected File createPhotoFile(String unique) {
        return new File(getParentDirectoryPath(), getPhotoName(unique));
    }

    protected String getParentDirectoryPath() {
        return Constants.Files.getDirectoryForCaptures(getActivity());
    }

    protected String getPhotoName(String unique) {
        return "IMG_" + unique + ".jpg";
    }

    private Intent makeCaptureIntent() {
        return makeCaptureIntent(null);
    }

    private Intent makeCaptureIntent(@Nullable Intent captureImage) {
        if (captureImage == null || captureImage.getAction() == null
                || !captureImage.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE))
            captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri uri = FileProvider.getUriForFile(getActivity(),
                "com.example.bludogramfirebase.fileprovider", mPhotoFile);

        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        List<ResolveInfo> cameraActivities = getActivity()
                .getPackageManager().queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo activity : cameraActivities) {
            getActivity().grantUriPermission(activity.activityInfo.packageName,
                    uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        return captureImage;
    }

    protected void startMatisseGallery(int count) {
        this.mCount = count;

        startMatisseGallery();
    }

    private void startMatisseGallery() {
        if (mPermissionHelper.isPermissionsGranted()) {
            startGallery();
        } else mPermissionHelper.requestPermissions(PERMISSION_REQUEST_CODE);
    }

    private void startGallery() {
        MatisseHelper.getMatisseBuilder(this, mCount)
                .forResult(REQUEST_CODE_CHOOSE_MATISSE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
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
        if (requestCode == REQUEST_CODE_CHOOSE_MATISSE && resultCode == RESULT_OK) {
            List<Uri> selected = Matisse.obtainResult(data);

            onGalleryFinish(selected);
        } else if (requestCode == REQUEST_CODE_CHOOSE_DIALOG) {
            switch (resultCode) {
                case ChooseImagePickerDialog.RESULT_CODE_GALLERY_SELECTED:
                    startMatisseGallery();
                    break;
                case ChooseImagePickerDialog.RESULT_CODE_NEW_PHOTO_SELECTED:
                    createNewPhoto();
                    break;
            }
        } else if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.example.bludogramfirebase.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            List<Uri> list = new ArrayList<>(1);
            list.add(Uri.fromFile(mPhotoFile));

            onGalleryFinish(list);
        }
    }

    protected abstract void onGalleryFinish(List<Uri> selected);

    protected void addToDeleteIfCapture(String path) {
        if (path != null && path.startsWith(getParentDirectoryPath())) {
            capturePathsToDelete.add(path);
        }
    }

    protected void deleteOldCaptures() {
        for (String path : capturePathsToDelete) {
            new File(path).delete();
        }
    }

    protected String[] convert(List<Uri> selected) {
        String[] paths = new String[selected.size()];

        for (int i = 0; i < paths.length; i++) {
            paths[i] = MatisseHelper.getRealPathFromURIPath(getActivity(), selected.get(i));
        }

        return paths;
    }
}
