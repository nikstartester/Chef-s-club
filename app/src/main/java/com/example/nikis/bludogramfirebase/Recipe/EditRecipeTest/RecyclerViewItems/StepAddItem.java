package com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest.RecyclerViewItems;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipe.Data.StepOfCooking;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class StepAddItem extends AbstractItem<StepAddItem, StepAddItem.ViewHolder> {
    public static final String DEFAULT_TEXT = "";
    public static final String DEFAULT_TIME = "";

    private ImageView mImageView;


    protected TextView tvTime;

    protected EditText edtStepText;

    protected ImageView btnRemove;

    private StepOfCooking mStepOfCooking;

    private boolean isFocusOnBind;

    public StepAddItem() {
        this(new StepOfCooking());
    }

    public StepAddItem(StepOfCooking step){
        this(step, false);
    }

    public StepAddItem(boolean isFocusOnBind){
        this(new StepOfCooking(), isFocusOnBind);
    }

    public StepAddItem(StepOfCooking step, boolean isFocusOnBind){
        mStepOfCooking = step;

        this.isFocusOnBind = isFocusOnBind;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
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
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        mImageView = holder.image;
        tvTime = holder.time;
        edtStepText = holder.text;
        btnRemove = holder.imageBtnRemove;

        holder.text.setText(mStepOfCooking.text);
        holder.time.setText(mStepOfCooking.time);

        if(isFocusOnBind)
            holder.text.requestFocus();

        if(mStepOfCooking.imagePath == null){
            setEmptyImage();
        }else {
            setImage(mStepOfCooking.imagePath);
        }

    }

    public void setEmptyImage(){
        mStepOfCooking.imagePath = null;
        GlideApp.with(mImageView.getContext())
                .load(R.drawable.ic_add_a_photo_blue_108dp)
                .centerCrop()
                .into(mImageView);

        btnRemove.setVisibility(View.GONE);
    }

    public void setImage(final String imagePath){

        mStepOfCooking.imagePath = imagePath;

        btnRemove.setVisibility(View.VISIBLE);

        if(!imagePath.split("/")[0].equals("recipe_images")){

            GlideApp.with(mImageView.getContext())
                    .load(imagePath)
                    .override(480,480)
                    .centerCrop()
                    .placeholder(R.color.zhihu_album_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mImageView);

        }else {
            StorageReference reference = FirebaseReferences.getStorageReference(imagePath);

            GlideApp.with(mImageView.getContext())
                    .load(reference)
                    .override(480,480)
                    .thumbnail(0.2f)
                    .error(R.drawable.ic_add_a_photo_blue_108dp)
                    .centerCrop()
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mImageView);
        }

    }

    public void setFocus(){
        if(edtStepText != null)
            edtStepText.requestFocus();
    }

    public void setTime(String time){
        mStepOfCooking.time = time;
        tvTime.setText(time);
    }

    public String getTextOfStep(){
        return edtStepText.getText().toString();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        EditText text;

        ImageView image, imageTime;

        TextView time;

        ImageView imageBtnRemove;

        Button btnDeleteStep;

        public ViewHolder(View itemView) {
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
