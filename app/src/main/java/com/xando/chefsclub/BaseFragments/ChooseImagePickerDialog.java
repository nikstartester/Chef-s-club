package com.xando.chefsclub.BaseFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xando.chefsclub.R;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChooseImagePickerDialog extends AppCompatDialogFragment {
    public static final int RESULT_CODE_NEW_PHOTO_SELECTED = 127;
    public static final int RESULT_CODE_GALLERY_SELECTED = 128;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_photo, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.imgBtn_new_photo)
    protected void newPhoto() {
        sendResult(RESULT_CODE_NEW_PHOTO_SELECTED);
        dismiss();
    }

    @OnClick(R.id.imgBtn_gallery)
    protected void gallery() {
        sendResult(RESULT_CODE_GALLERY_SELECTED);
        dismiss();
    }

    private void sendResult(int resultCode) {
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, new Intent());
    }
}
