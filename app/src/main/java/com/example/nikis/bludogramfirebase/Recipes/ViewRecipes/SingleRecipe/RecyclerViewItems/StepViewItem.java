package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikis.bludogramfirebase.Helpers.DateTimeHelper;
import com.example.nikis.bludogramfirebase.Images.ImageData.ImageData;
import com.example.nikis.bludogramfirebase.Images.ImageLoaders.GlideImageLoader;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.Data.StepOfCooking;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class StepViewItem extends AbstractItem<StepViewItem, StepViewItem.ViewHolder> {
    public static final String DEFAULT_TEXT = "";
    public static final String DEFAULT_TIME = "";

    private ImageView imageView;

    private TextView tvTime;

    private TextView tvStepText;

    private ImageView imgTime;

    private final StepOfCooking mStepOfCooking;

    private final ImageData mImageData;

    public StepViewItem(StepOfCooking stepOfCooking, ImageData imageData) {
        mStepOfCooking = stepOfCooking;
        mImageData = imageData;
    }


    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.step_item_view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_step_view_item;
    }

    @Override
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);
        imageView = holder.image;
        tvTime = holder.time;
        tvStepText = holder.text;
        imgTime = holder.imageTime;

        holder.text.setText(mStepOfCooking.text);

        setTime();

        if (mImageData.imagePath == null) {
            setEmptyImage();
        } else {
            setImage();
        }

    }

    private void setEmptyImage() {
        imageView.setVisibility(View.GONE);
    }

    private void setImage() {
        GlideImageLoader.getInstance()
                .loadImage(imageView.getContext(), imageView, mImageData);

    }

    private void setTime() {
        if (mStepOfCooking.timeNum > 0) {
            tvTime.setText(DateTimeHelper.convertTime(mStepOfCooking.timeNum));
            tvTime.setVisibility(View.VISIBLE);
            imgTime.setVisibility(View.VISIBLE);
        } else {
            tvTime.setText(null);
            tvTime.setVisibility(View.GONE);
            imgTime.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        final ImageView image;
        final ImageView imageTime;

        final TextView time;

        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tv_stepText);

            image = itemView.findViewById(R.id.img_image);

            imageTime = itemView.findViewById(R.id.img_time);

            time = itemView.findViewById(R.id.tv_time);

        }
    }
}
