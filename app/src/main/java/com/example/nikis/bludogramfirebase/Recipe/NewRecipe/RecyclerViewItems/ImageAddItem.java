package com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class ImageAddItem extends AbstractItem<ImageAddItem, ImageAddItem.ViewHolder> {
    private String imagePath;
    private boolean isInUploadTask;
    private ImageView imageView;
    private ProgressBar progressBar;

    public ImageAddItem(String imagePath) {
        this.imagePath = imagePath;
    }

    public ImageAddItem(String imagePath, boolean isInUploadTask) {
        this.imagePath = imagePath;
        this.isInUploadTask = isInUploadTask;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.image_item_add;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_image_item;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        imageView = holder.imageView;
        progressBar = holder.progressBar;
        if (isInUploadTask) {
            imageView.setColorFilter(R.color.colorPrimary);
            progressBar.setVisibility(View.VISIBLE);
        }
        GlideApp.with(imageView.getContext())
                .load(imagePath)
                .override(320,320)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.color.zhihu_album_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    public void setInUploadTask(boolean isInUploadTask){
        this.isInUploadTask = isInUploadTask;
        if(imageView != null && progressBar != null)
        if(isInUploadTask){
            imageView.setColorFilter(R.color.colorPrimary);
            progressBar.setVisibility(View.VISIBLE);
        }else {
            imageView.setColorFilter(null);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton btnDelete;
        ProgressBar progressBar;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_image);
            btnDelete = itemView.findViewById(R.id.img_btn_remove);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
