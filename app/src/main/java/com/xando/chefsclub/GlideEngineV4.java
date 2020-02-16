package com.xando.chefsclub;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.xando.chefsclub.constants.Constants;
import com.xando.chefsclub.image.loaders.GlideImageLoader;
import com.zhihu.matisse.engine.ImageEngine;

public class GlideEngineV4 implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        GlideApp.with(context)
                .load(uri)
                //.asBitmap()  // some .jpeg files are actually gif
                .override(resize, resize)
                .transforms(new CenterCrop(), new RoundedCorners(Constants.ImageConstants.CORNER_RADIUS))
                .placeholder(GlideImageLoader.getRoundedPlaceholder(context))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        GlideApp.with(context)
                .load(uri)
                //.asBitmap()
                .placeholder(placeholder)
                .override(resize, resize)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        GlideApp.with(context)
                .load(uri)
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        GlideApp.with(context)
                .load(uri)
                //.asGif()
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return false;
    }
}
