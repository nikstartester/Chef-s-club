package com.xando.chefsclub.basescreen.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.xando.chefsclub.camera.CameraDialogFragment;
import com.xando.chefsclub.camera.CameraDialogFragmentKt;
import com.xando.chefsclub.constants.Constants;
import com.xando.chefsclub.helper.MatisseHelper;
import com.xando.chefsclub.helper.PermissionHelper;
import com.zhihu.matisse.Matisse;

import org.jetbrains.annotations.NotNull;

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
    private static final int REQUEST_CODE_CAMERA_DIALOG = 14;

    private static final String IMAGE_CHOOSE_PHOTO_PATH = "IMAGE_CHOOSE_PHOTO_PATH";

    private final String[] mPermissions = new String[]{READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE};
    protected List<String> capturePathsToDelete = new ArrayList<>();
    private PermissionHelper mPermissionHelper;

    @Nullable
    private File mPhotoFile;

    private String mPhotoPath = "";
    private int mCount;

    private CameraDialogFragment cameraDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionHelper = new PermissionHelper(this, mPermissions);
        if (savedInstanceState != null) {
            mPhotoPath = savedInstanceState.getString(IMAGE_CHOOSE_PHOTO_PATH, "");
        }

        cameraDialog = new CameraDialogFragment();
        cameraDialog.setTargetFragment(this, REQUEST_CODE_CAMERA_DIALOG);
    }

    protected void showChooseDialog(int count, boolean withDelete) {
        this.mCount = count;

        DialogFragment chooseDialog = ChooseImagePickerDialog.getInstance(withDelete);

        chooseDialog.setTargetFragment(this, REQUEST_CODE_CHOOSE_DIALOG);

        chooseDialog.show(getFragmentManager(), "Choose_action");
    }

    protected void showCameraDialog() {
        cameraDialog.show(getFragmentManager(), "CameraDialogFragment");
    }

    // TODO: add settings flag to open system camera

    /**
     * @deprecated use showCameraDialog with cameraView instead
     */
    @Deprecated
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
            mPhotoPath = mPhotoFile.getAbsolutePath();
            startActivityForResult(makeCaptureIntent(captureImage), REQUEST_CODE_PHOTO);
        }
    }

    @NotNull
    protected File createPhotoFile() {
        String unique = UUID.randomUUID().toString();
        return createPhotoFile(unique);
    }

    @NotNull
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
                "com.xando.chefsclub.fileprovider", mPhotoFile);

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
                case ChooseImagePickerDialog.RESULT_CODE_PREVIEW_SELECTED:
                    onPreviewImage();
                    break;
                case ChooseImagePickerDialog.RESULT_CODE_GALLERY_SELECTED:
                    startMatisseGallery();
                    break;
                case ChooseImagePickerDialog.RESULT_CODE_NEW_PHOTO_SELECTED:
                    showCameraDialog();
                    break;
                case ChooseImagePickerDialog.RESULT_CODE_DELETE:
                    onDeleteImage();
                    break;
            }
        } else if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK) {
            if (mPhotoFile == null) {
                if (!mPhotoPath.isEmpty())
                    mPhotoFile = new File(mPhotoPath);

                if (mPhotoFile == null || !mPhotoFile.exists()) {
                    //Shit happens. Really happens
                    Toast.makeText(getContext(), "Failed to add photo", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.xando.chefsclub.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            List<Uri> list = new ArrayList<>(1);
            list.add(Uri.fromFile(mPhotoFile));

            mPhotoFile = null;
            mPhotoPath = "";

            onGalleryFinish(list);
        } else if (requestCode == REQUEST_CODE_CAMERA_DIALOG && resultCode == RESULT_OK) {
            List<Uri> list = new ArrayList<>(1);
            list.add(data.getParcelableExtra(CameraDialogFragmentKt.CAMERA_DIALOG_PHOTO_URI));
            onGalleryFinish(list);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(IMAGE_CHOOSE_PHOTO_PATH, mPhotoPath);
        super.onSaveInstanceState(outState);
    }

    protected abstract void onGalleryFinish(List<Uri> selected);

    protected abstract void onPreviewImage();

    protected abstract void onDeleteImage();

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
