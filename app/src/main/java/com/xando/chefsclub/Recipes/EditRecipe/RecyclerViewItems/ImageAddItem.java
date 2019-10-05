package com.xando.chefsclub.Recipes.EditRecipe.RecyclerViewItems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.Images.ImageData.ImageData;
import com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader;
import com.xando.chefsclub.R;

import java.util.List;

import static com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader.getDim;

public class ImageAddItem extends AbstractItem<ImageAddItem, ImageAddItem.ViewHolder> {

    private ImageData mImageData;

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

    public void updateImage(ImageData imageData) {
        mImageData = imageData;

        if (imageView != null) {
            setImage();
        }
    }

    private void setImage() {
        GlideImageLoader.getInstance()
                .loadRoundedImage(imageView.getContext(),
                        imageView,
                        mImageData,
                        getDim(imageView.getContext(), R.dimen.image_corner_radius));
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
