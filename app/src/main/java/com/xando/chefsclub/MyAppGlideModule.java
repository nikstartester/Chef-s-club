package com.xando.chefsclub;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.xando.chefsclub.Constants.Constants;

import java.io.InputStream;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    // see
    //https://stackoverflow.com/questions/42802447/glide-clear-cache-when-cache-size-is-larger-than-50-mb/42802514#42802514
    private static final int DISK_CACHE_SIZE_FOR_SMALL_INTERNAL_STORAGE_MIB = 20 * 1024 * 1024;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // if (MyApplication.from(context).isTest()) return; // NOTE: StatFs will crash on robolectric.

        int maxCacheMB = getMaxCacheSize(context);
        if (maxCacheMB != Constants.Settings.INFINITY) {
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context,
                    getMaxCacheSize(context) * 1024 * 1024));
        }
    }

    private int getMaxCacheSize(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Settings.APP_PREFERENCES,
                Context.MODE_PRIVATE);

        return sharedPreferences.getInt(Constants.Settings.MAX_IMAGE_CACHE_SIZE,
                Constants.Settings.DEFAULT_MAX_IMAGE_CACHE_SIZE);
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.append(StorageReference.class, InputStream.class,
                new FirebaseImageLoader.Factory());
    }

}