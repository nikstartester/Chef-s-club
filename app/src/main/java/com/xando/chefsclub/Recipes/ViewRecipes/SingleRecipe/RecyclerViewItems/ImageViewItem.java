package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.Images.ImageData.ImageData;
import com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader;
import com.xando.chefsclub.R;

import java.util.List;

public class ImageViewItem extends AbstractItem<ImageViewItem, ImageViewItem.ViewHolder> {

    private final ImageData mImageData;

    private boolean isInUploadTask;

    private ImageView imageView;

    private ProgressBar progressBar;

    public ImageViewItem(ImageData imageData) {
        mImageData = imageData;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.image_item_view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_image_view_item;
    }

    @Override
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);
        imageView = holder.imageView;
        progressBar = holder.progressBar;
        if (isInUploadTask) {
            imageView.setColorFilter(R.color.colorPrimary);
            progressBar.setVisibility(View.VISIBLE);
        }

        if (mImageData.imagePath != null)
            setImage();
    }

    private void setImage() {
        GlideImageLoader.getInstance()
                .loadImage(imageView.getContext(), imageView, mImageData);

    }

    public void setInUploadTask(boolean isInUploadTask) {
        this.isInUploadTask = isInUploadTask;
        if (imageView != null && progressBar != null)
            if (isInUploadTask) {
                imageView.setColorFilter(R.color.colorPrimary);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                imageView.setColorFilter(null);
                progressBar.setVisibility(View.INVISIBLE);
            }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;
        final ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_image);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
