package com.example.nikis.bludogramfirebase.Recipes.EditRecipe.RecyclerViewItems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.Images.ImageData.ImageData;
import com.example.nikis.bludogramfirebase.Images.ImageLoaders.GlideImageLoader;
import com.example.nikis.bludogramfirebase.R;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class ImageAddItem extends AbstractItem<ImageAddItem, ImageAddItem.ViewHolder> {
    private final ImageData mImageData;

    private boolean isInUploadTask;

    private ImageView imageView;

    private ProgressBar progressBar;


    public ImageAddItem(ImageData imageData) {
        mImageData = imageData;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.image_item_add;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_image_add_item;
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

        /*if(mImageData.imagePath.split("/")[0].equals("recipe_images")){
            setImageFromStorageRef();
        }else {
            setImage();
        }*/
    }

    @Deprecated
    private void setImageFromStorageRef() {
        StorageReference storageReference = FirebaseReferences.getStorageReference(mImageData.imagePath);

        GlideApp.with(imageView.getContext())
                .load(storageReference)
                .thumbnail(0.2f)
                .centerCrop()
                .placeholder(R.color.zhihu_album_placeholder)
                .error(R.drawable.ic_add_a_photo_blue_1080dp)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .signature(new ObjectKey(mImageData.lastUpdateTime))
                .into(imageView);
    }

    @Deprecated
    private void setLocalImage() {
        GlideApp.with(imageView.getContext())
                .load(mImageData.imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .placeholder(R.color.zhihu_album_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
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
        ImageView imageView;
        ImageButton btnDelete;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_image);
            btnDelete = itemView.findViewById(R.id.img_btn_remove);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
