package com.xando.chefsclub.image.viewimages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.xando.chefsclub.image.data.ImageData;
import com.xando.chefsclub.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewImagesActivity extends AppCompatActivity {

    private static final String EXTRA_DATA = "extraData";
    private static final String EXTRA_POSITION = "extraPosition";

    @BindView(R.id.viewPager)
    protected PreviewViewPager viewPager;

    private List<ImageData> mImageDataList;

    public static Intent getIntent(Context context, ArrayList<ImageData> imageDataList, int position) {
        Intent intent = new Intent(context, ViewImagesActivity.class);

        intent.putParcelableArrayListExtra(EXTRA_DATA, imageDataList);
        intent.putExtra(EXTRA_POSITION, position);

        return intent;
    }

    public static Intent getIntent(Context context, ImageData imageData) {
        Intent intent = new Intent(context, ViewImagesActivity.class);

        final ArrayList<ImageData> list = new ArrayList<>(1);
        list.add(imageData);

        intent.putParcelableArrayListExtra(EXTRA_DATA, list);
        intent.putExtra(EXTRA_POSITION, 0);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        ButterKnife.bind(this);

        mImageDataList = getIntent().getParcelableArrayListExtra(EXTRA_DATA);
        int position = getIntent().getIntExtra(EXTRA_POSITION, -1);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return ImageFragment.getInstance(mImageDataList.get(position));
            }

            @Override
            public int getCount() {
                return mImageDataList.size();
            }
        });

        if (position != -1) {
            viewPager.setCurrentItem(position);
        }

        findViewById(R.id.view_images_back).setOnClickListener(v -> finish());
    }
}
