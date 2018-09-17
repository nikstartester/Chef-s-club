package com.example.nikis.bludogramfirebase;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.zhihu.matisse.engine.ImageEngine;

public class GlideEngineV4 implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        GlideApp.with(context)
                .load(uri)
                //.asBitmap()  // some .jpeg files are actually gif
                .placeholder(placeholder)
                .override(resize, resize)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    @Override
    public void loadAnimatedGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView,
                                         Uri uri) {
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
    public void loadAnimatedGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
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
        return true;
    }

}
