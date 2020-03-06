package com.xando.chefsclub.basescreen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.xando.chefsclub.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChooseImagePickerDialog extends BottomSheetDialogFragment {

    public static final int RESULT_CODE_PREVIEW_SELECTED = 126;
    public static final int RESULT_CODE_NEW_PHOTO_SELECTED = 127;
    public static final int RESULT_CODE_GALLERY_SELECTED = 128;
    public static final int RESULT_CODE_DELETE = 129;

    public static final String KEY_WITH_DELETE = "key_withDelete";

    @BindView(R.id.choose_delete)
    protected View contentDelete;

    @BindView(R.id.choose_preview)
    protected View contentPreview;

    private boolean withDelete = false;

    public static DialogFragment getInstance(boolean withDelete) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_WITH_DELETE, withDelete);

        DialogFragment dialog = new ChooseImagePickerDialog();

        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            withDelete = getArguments().getBoolean(KEY_WITH_DELETE, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_photo_list, container, false);

        ButterKnife.bind(this, view);

        if (withDelete) {
            contentDelete.setVisibility(View.VISIBLE);
            contentPreview.setVisibility(View.VISIBLE);
        } else {
            contentDelete.setVisibility(View.GONE);
            contentPreview.setVisibility(View.GONE);
        }

        return view;
    }

    @OnClick(R.id.choose_preview)
    protected void preview() {
        sendResult(RESULT_CODE_PREVIEW_SELECTED);
        dismiss();
    }

    @OnClick(R.id.choose_camera)
    protected void newPhoto() {
        sendResult(RESULT_CODE_NEW_PHOTO_SELECTED);
        dismiss();
    }

    @OnClick(R.id.choose_gallery)
    protected void gallery() {
        sendResult(RESULT_CODE_GALLERY_SELECTED);
        dismiss();
    }

    @OnClick(R.id.choose_delete)
    protected void delete() {
        sendResult(RESULT_CODE_DELETE);
        dismiss();
    }

    private void sendResult(int resultCode) {
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, new Intent());
    }
}
