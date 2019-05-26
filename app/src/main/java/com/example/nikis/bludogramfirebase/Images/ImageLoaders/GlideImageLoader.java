package com.example.nikis.bludogramfirebase.Images.ImageLoaders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.nikis.bludogramfirebase.Constants.Constants;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.GlideRequest;
import com.example.nikis.bludogramfirebase.Images.ImageData.ImageData;
import com.google.firebase.storage.StorageReference;

public class GlideImageLoader implements ImageLoader {
    private static final int NORMAL_SIZE = -12;
    private static final long DEF_TIME = Constants.ImageConstants.DEF_TIME;

    private static final GlideImageLoader INSTANCE = new GlideImageLoader();

    public static GlideImageLoader getInstance() {
        return INSTANCE;
    }

    public void loadImage(Context context, ImageView imageView, ImageData imageData) {
        loadImage(context, NORMAL_SIZE, NORMAL_SIZE, imageView, imageData);
    }

    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, ImageData imageData) {
        if (imageData.lastUpdateTime == DEF_TIME) {
            loadImage(context, resizeX, resizeY, imageView, imageData.imagePath);
        } else
            loadImage(context, resizeX, resizeY, imageView, imageData.imagePath, imageData.lastUpdateTime);
    }

    public GlideRequest<Drawable> getBaseBuilder(Context context, ImageData imageData) {
        GlideRequest<Drawable> resizeBuilder = getResizeBuilder(getBaseBuilder(context, imageData.imagePath),
                NORMAL_SIZE, NORMAL_SIZE);

        if (imageData.lastUpdateTime == DEF_TIME) {
            return resizeBuilder;
        } else return getCacheBuilder(
                resizeBuilder,
                imageData.lastUpdateTime);

    }

    @Override
    public void loadImage(Context context, ImageView imageView, String imagePath) {
        loadImage(context, NORMAL_SIZE, NORMAL_SIZE, imageView, imagePath);
    }

    @Override
    public void loadImage(Context context, ImageView imageView, String imagePath, long time) {
        loadImage(context, NORMAL_SIZE, NORMAL_SIZE, imageView, imagePath, time);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, String imagePath) {
        getResizeBuilder(getBaseBuilder(context, imagePath), resizeX, resizeY)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, String imagePath, long time) {
        getCacheBuilder(
                getResizeBuilder(getBaseBuilder(context, imagePath), resizeX, resizeY),
                time)
                .into(imageView);
    }

    public void loadSmallCircularImage(Context context, ImageView imageView, ImageData imageData) {
        loadCircularImage(context,
                Constants.ImageConstants.RESIZE_PROFILE_SMALL_IMAGE_SIZE,
                Constants.ImageConstants.RESIZE_PROFILE_SMALL_IMAGE_SIZE,
                imageView,
                imageData.imagePath,
                imageData.lastUpdateTime);
    }

    public void loadNormalCircularImage(Context context, ImageView imageView, ImageData imageData) {
        loadCircularImage(context,
                Constants.ImageConstants.RESIZE_PROFILE_NORMAL_IMAGE_SIZE,
                Constants.ImageConstants.RESIZE_PROFILE_NORMAL_IMAGE_SIZE,
                imageView,
                imageData.imagePath,
                imageData.lastUpdateTime);
    }


    public void loadCircularImage(Context context, int resizeX, int resizeY, ImageView imageView, String imagePath, long time) {
        getCacheBuilder(
                getResizeBuilder(getBaseBuilder(context, imagePath), resizeX, resizeY),
                time)
                .placeholder(null)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }


    private GlideRequest<Drawable> getCacheBuilder(GlideRequest<Drawable> glideRequest, long time) {
        return glideRequest.skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .signature(new ObjectKey(time));
    }

    private GlideRequest<Drawable> getResizeBuilder(GlideRequest<Drawable> glideRequest, int resizeX, int resizeY) {
        if (resizeX == NORMAL_SIZE || resizeY == NORMAL_SIZE) {
            return glideRequest;
        } else return glideRequest.override(resizeX, resizeY);
    }

    private GlideRequest<Drawable> getBaseBuilder(Context context, String imagePath) {
        GlideRequest<Drawable> glr;

        if (imagePath == null) {
            throw new NullPointerException("GlideImageLoader: getBaseBuilder: imagePath == null");
        }

        if (imagePath.startsWith(getFirebasePrefix())) {

            glr = getFBStorageBuilder(context, imagePath);

        } else glr = getLocalBuilder(context, imagePath);


        return glr
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(getDrawableError())
                //.thumbnail(0.15f)
                .placeholder(getPlaceholder())
                .centerCrop();

    }

    private GlideRequest<Drawable> getFBStorageBuilder(Context context, String imagePath) {
        StorageReference storageReference = FirebaseReferences.getStorageReference(imagePath);

        return GlideApp.with(context).load(storageReference);
    }

    private GlideRequest<Drawable> getLocalBuilder(Context context, String imagePath) {
        return GlideApp.with(context).load(imagePath);
    }

    @Override
    public int getDrawableError() {
        return Constants.ImageConstants.DRAWABLE_ERROR;
    }

    private int getPlaceholder() {
        return Constants.ImageConstants.PLACEHOLDER;
    }

    private String getFirebasePrefix() {
        return Constants.ImageConstants.FIREBASE_STORAGE_AT_START;
    }
}
