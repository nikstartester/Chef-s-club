package com.xando.chefsclub.image.viewimages;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xando.chefsclub.image.data.ImageData;
import com.xando.chefsclub.image.loaders.GlideImageLoader;
import com.xando.chefsclub.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class ImageFragment extends Fragment {

    private static final String KEY_IMAGE_DATA = "keyImagePath";

    @BindView(R.id.imageView)
    protected ImageViewTouch imageView;

    private ImageData mImageData;

    public static Fragment getInstance(ImageData imageData) {
        Fragment fragment = new ImageFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_IMAGE_DATA, imageData);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        mImageData = getArguments().getParcelable(KEY_IMAGE_DATA);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_fullscreen_image, container, false);

        ButterKnife.bind(this, view);

        imageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        if (mImageData != null && mImageData.imagePath != null) {
            GlideImageLoader.getInstance().getBaseBuilder(getActivity(), mImageData)
                    .placeholder(R.color.md_black_1000)
                    .fitCenter()
                    .into(imageView);
        }
        return view;
    }
}
