package com.example.nikis.bludogramfirebase.Recipes.EditRecipe.RecyclerViewItems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.Helpers.DateTimeHelper;
import com.example.nikis.bludogramfirebase.Images.ImageData.ImageData;
import com.example.nikis.bludogramfirebase.Images.ImageLoaders.GlideImageLoader;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.Data.StepOfCooking;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import static com.example.nikis.bludogramfirebase.Recipes.EditRecipe.DialogTimePicker.NOT_SELECTED;

public class StepAddItem extends AbstractItem<StepAddItem, StepAddItem.ViewHolder> {
    public static final String DEFAULT_TEXT = "";
    public static final String DEFAULT_TIME = "";

    private ImageView imageView;

    private TextView tvTime;

    private EditText edtStepText;

    private ImageView btnRemove;

    private final StepOfCooking mStepOfCooking;

    private final ImageData mImageData;

    private final boolean isFocusOnBind;

    public StepAddItem(StepOfCooking stepOfCooking, ImageData imageData) {
        this(stepOfCooking, imageData, false);
    }

    public StepAddItem(boolean isFocusOnBind) {
        this(new StepOfCooking(), new ImageData(null, 0L), isFocusOnBind);
    }

    private StepAddItem(StepOfCooking stepOfCooking, ImageData imageData, boolean isFocusOnBind) {
        mStepOfCooking = stepOfCooking;
        mImageData = imageData;
        this.isFocusOnBind = isFocusOnBind;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.step_item_add;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_step_add_item;
    }

    @Override
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);
        imageView = holder.image;
        tvTime = holder.time;
        edtStepText = holder.text;
        btnRemove = holder.imageBtnRemove;

        holder.text.setText(mStepOfCooking.text);
        holder.time.setText(DateTimeHelper.convertTime(mStepOfCooking.timeNum));

        if (isFocusOnBind) {
            holder.text.requestFocus();
        }

        if (mStepOfCooking.imagePath == null) {

            btnRemove.setVisibility(View.INVISIBLE);

            setEmptyImage();
        } else {
            setImageWithRemoveBtn();
        }

    }

    private void setEmptyImage() {
        GlideApp.with(imageView.getContext())
                .load(R.drawable.ic_add_a_photo_blue_1080dp)
                .into(imageView);
    }

    public void removeImage() {
        mStepOfCooking.imagePath = null;

        btnRemove.setVisibility(View.INVISIBLE);

        setEmptyImage();
    }

    private void setImageWithRemoveBtn() {
        btnRemove.setVisibility(View.VISIBLE);

        GlideImageLoader.getInstance()
                .loadImage(imageView.getContext(), 270, 360, imageView, mImageData);

    }

    public void setImage(String imagePath) {
        mImageData.imagePath = imagePath;

        setImageWithRemoveBtn();
    }

    public void setFocus() {
        if (edtStepText != null) {
            edtStepText.requestFocus();
        }
    }

    public void setTime(int time) {
        mStepOfCooking.timeNum = time;

        tvTime.setText(time == NOT_SELECTED ? "" : DateTimeHelper.convertTime(time));
    }

    public String getTextOfStep() {
        return edtStepText.getText().toString();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        EditText text;

        ImageView image;
        ImageView imageTime;

        TextView time;

        ImageView imageBtnRemove;

        Button btnDeleteStep;

        ViewHolder(View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.edt_stepText);

            image = itemView.findViewById(R.id.img_image);

            imageTime = itemView.findViewById(R.id.img_time);

            time = itemView.findViewById(R.id.tv_time);

            imageBtnRemove = itemView.findViewById(R.id.img_btn_remove);

            btnDeleteStep = itemView.findViewById(R.id.btn_deleteStep);

        }
    }
}
