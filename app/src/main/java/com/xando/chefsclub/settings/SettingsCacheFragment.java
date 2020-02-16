package com.xando.chefsclub.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xando.chefsclub.constants.Constants;
import com.xando.chefsclub.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xando.chefsclub.constants.Constants.Settings.DEFAULT_MAX_IMAGE_CACHE_SIZE_VALUES;


public class SettingsCacheFragment extends Fragment {

    private static final String TAG = "SettingsCacheFragment";

    @BindView(R.id.all_cache_size)
    protected TextView allCacheSize;

    @BindView(R.id.btn_claer_all_cache)
    protected Button clearAllCache;

    private File[] dirsToCache;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_cache, container, false);

        ButterKnife.bind(this, view);

        initDirsToCache();

        initCache();

        return view;
    }

    private void initDirsToCache() {
        dirsToCache = new File[]{getActivity().getCacheDir(), getActivity().getExternalCacheDir()};
    }

    @SuppressLint("DefaultLocale")
    private void initCache() {
        assert dirsToCache != null : "dirsToCache does not init yet!";

        double size = 0;

        for (File dir : dirsToCache) {
            if (dir != null) size += getDirSize(dir);
        }

        size = size / 1024 / 1024;

        allCacheSize.setText(String.format("%.2f", size) + " MB");
    }

    public long getDirSize(File dir) {
        if (!dir.exists()) {
            Log.d(TAG, "getDirSize: dir is not exist!");
            return 0;
        }
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    @OnClick(R.id.btn_claer_all_cache)
    protected void clearAllCache() {
        assert dirsToCache != null : "dirsToCache does not init yet!";

        deleteCache();

        initCache();
    }

    public void deleteCache() {
        try {
            for (File dir : dirsToCache) {
                if (dir != null) deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean deleteDir(File dir) {
        if (!dir.exists()) {
            Log.d(TAG, "deleteDir: dir is not exist!");
            return false;
        }

        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @OnClick(R.id.btn_restriction_image_cache)
    protected void showRestrictionImageCacheDialog() {
        RestrictionCacheFragment cacheFragment = new RestrictionCacheFragment();
        cacheFragment.setArguments(RestrictionCacheFragment.getArguments(
                DEFAULT_MAX_IMAGE_CACHE_SIZE_VALUES, getCurrIndexOfMaxImageCacheValues()));

        cacheFragment.show(getFragmentManager(), "restr");
    }

    @OnClick(R.id.btn_restriction_other_cache_data)
    protected void showRestrictionOtherCacheData() {
        Toast.makeText(getActivity(), "Do nothing.", Toast.LENGTH_SHORT).show();
    }

    private int getCurrIndexOfMaxImageCacheValues() {
        int currMaxImageSize = getPrefImageCacheSize();
        int minDiff = -1;

        int index = 0;

        for (int i = 0; i < DEFAULT_MAX_IMAGE_CACHE_SIZE_VALUES.length; i++) {
            int tmpDiff = Math.abs(DEFAULT_MAX_IMAGE_CACHE_SIZE_VALUES[i] - currMaxImageSize);

            if (minDiff == -1 || tmpDiff < minDiff) {
                minDiff = tmpDiff;
                index = i;
            }
        }
        return index;
    }

    private int getPrefImageCacheSize() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.Settings.APP_PREFERENCES,
                Context.MODE_PRIVATE);

        return sharedPreferences.getInt(Constants.Settings.MAX_IMAGE_CACHE_SIZE,
                Constants.Settings.DEFAULT_MAX_IMAGE_CACHE_SIZE);

    }
}
