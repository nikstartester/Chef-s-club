package com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class StepAddItem extends AbstractItem<StepAddItem, StepAddItem.ViewHolder> {
    public static final String DEFAULT_TEXT = "";
    public static final String DEFAULT_TIME = "";

    private ImageView imageView;
    private TextView tvTime;
    private EditText edtStepText;
    private ImageView btnRemove;
    private String text, time, imagePath;
    private boolean isFocusOnBind;

    public StepAddItem() {
        this(DEFAULT_TEXT, null, DEFAULT_TIME);
    }
    public StepAddItem(String text, String imagePath, String time) {
        this(text, imagePath, time, false);
    }
    public StepAddItem(boolean isFocusOnBind){
        this(DEFAULT_TEXT, null, DEFAULT_TIME, isFocusOnBind);
    }
    private StepAddItem(String text, String imagePath, String time, boolean isFocusOnBind) {
        this.text = text;
        this.time = time;
        this.imagePath = imagePath;
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
        imageView = holder.image;
        tvTime = holder.time;
        edtStepText = holder.text;
        btnRemove = holder.imageBtnRemove;

        holder.text.setText(text);
        holder.time.setText(time);

        if(isFocusOnBind)
            holder.text.requestFocus();

        if(imagePath == null){
            //setEmptyImage();
        }else {
            setImage(imagePath);
        }

    }

    public void setEmptyImage(){
        this.imagePath = null;
        GlideApp.with(imageView.getContext())
                .load(R.drawable.ic_add_a_photo_blue_108dp)
                //.override(480,480)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                //.placeholder(R.color.zhihu_album_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        //imageView.setImageResource(R.drawable.ic_add_a_photo_blue_108dp);
        btnRemove.setVisibility(View.GONE);
    }

    public void setImage(final String imagePath){
        this.imagePath = imagePath;
        GlideApp.with(imageView.getContext())
                .load(imagePath)
                .override(480,480)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.color.zhihu_album_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
        btnRemove.setVisibility(View.VISIBLE);
    }

    public void setFocus(){
        if(edtStepText != null)
            edtStepText.requestFocus();
    }

    public void setTime(String time){
        this.time = time;
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
