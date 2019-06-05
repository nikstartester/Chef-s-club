package com.xando.chefsclub.Images.ImageLoaders;

import android.content.Context;
import android.widget.ImageView;

interface ImageLoader {

    void loadImage(Context context, ImageView imageView, String imagePath);

    void loadImage(Context context, ImageView imageView, String imagePath, long time);

    void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, String imagePath);

    void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, String imagePath, long time);

    int getDrawableError();
}
