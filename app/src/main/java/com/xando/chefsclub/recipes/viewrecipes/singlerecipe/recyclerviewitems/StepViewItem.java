package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.recyclerviewitems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.helper.DateTimeHelper;
import com.xando.chefsclub.image.data.ImageData;
import com.xando.chefsclub.image.loaders.GlideImageLoader;
import com.xando.chefsclub.R;
import com.xando.chefsclub.recipes.data.StepOfCooking;

import java.util.List;

import static com.xando.chefsclub.image.loaders.GlideImageLoader.getDim;

public class StepViewItem extends AbstractItem<StepViewItem, StepViewItem.ViewHolder> {

    private ImageView imageView;
    private TextView tvTime;
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
        imgTime = holder.imageTime;

        holder.text.setText(mStepOfCooking.text);

        setTime();

        if (mImageData.imagePath == null) setEmptyImage();
        else setImage();
    }

    private void setEmptyImage() {
        imageView.setVisibility(View.GONE);
    }

    private void setImage() {
        GlideImageLoader.getInstance()
                .loadRoundedImage(imageView.getContext(),
                        imageView,
                        mImageData,
                        getDim(imageView.getContext(), R.dimen.image_corner_radius));
        imageView.setVisibility(View.VISIBLE);
    }

    private void setTime() {
        tvTime.setText(mStepOfCooking.timeNum > 0 ?
                DateTimeHelper.convertTime(mStepOfCooking.timeNum)
                : "");

        tvTime.setVisibility(mStepOfCooking.timeNum > 0 ? View.VISIBLE : View.GONE);
        imgTime.setVisibility(mStepOfCooking.timeNum > 0 ? View.VISIBLE : View.GONE);
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
