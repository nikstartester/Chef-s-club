package com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.nikis.bludogramfirebase.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class ChipCategoryItem extends AbstractItem<ChipCategoryItem, ChipCategoryItem.ViewHolder>{
    private String categoryText;
    private boolean isActive;
    private TextView tvCategory;

    public ChipCategoryItem(String categoryText) {
        this(categoryText, false);
    }

    public ChipCategoryItem(String categoryText, boolean isActive) {
        this.categoryText = categoryText;
        this.isActive = isActive;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.chip_category_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.shape_chip;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        tvCategory = holder.category;
        tvCategory.setText(categoryText);

        setViewActive();
    }

    private void setViewActive(){
        if(isActive) {
            tvCategory.setBackgroundResource(R.drawable.shape_chip_active_drawable);
            tvCategory.setTextColor(tvCategory.getContext().getResources().getColor(R.color.cardview_light_background));
        }
        else{
            tvCategory.setBackgroundResource(R.drawable.shape_chip_non_drawable);
            tvCategory.setTextColor(tvCategory.getContext().getResources().getColor(R.color.cardview_dark_background));
        }
    }

    public void setActive(boolean isActive){
        this.isActive = isActive;
        setViewActive();
    }

    public void changeActive(){
        isActive = !isActive;
        setViewActive();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView category;
        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.tv_category);
        }
    }
}
