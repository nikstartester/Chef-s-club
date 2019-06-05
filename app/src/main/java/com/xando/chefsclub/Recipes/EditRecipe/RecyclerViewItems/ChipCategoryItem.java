package com.xando.chefsclub.Recipes.EditRecipe.RecyclerViewItems;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.R;

import java.util.List;

public class ChipCategoryItem extends AbstractItem<ChipCategoryItem, ChipCategoryItem.ViewHolder> {
    private final String categoryText;
    private boolean isActive;
    private TextView tvCategory;

    public ChipCategoryItem(String categoryText) {
        this(categoryText, false);
    }

    public ChipCategoryItem(String categoryText, boolean isActive) {
        this.categoryText = categoryText;
        this.isActive = isActive;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
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
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);

        tvCategory = holder.category;
        tvCategory.setText(categoryText);

        setViewActive();
    }

    private void setViewActive() {
        if (isActive) {
            tvCategory.setBackgroundResource(R.drawable.shape_chip_active_drawable);
            tvCategory.setTextColor(tvCategory.getContext().getResources().getColor(R.color.cardview_light_background));
        } else {
            tvCategory.setBackgroundResource(R.drawable.shape_chip_non_drawable);
            tvCategory.setTextColor(tvCategory.getContext().getResources().getColor(R.color.cardview_dark_background));
        }
    }

    public String getCategoryText() {
        return categoryText;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
        setViewActive();
    }

    public boolean isActive() {
        return isActive;
    }

    public void changeActive() {
        isActive = !isActive;
        setViewActive();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView category;

        ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.tv_category);
        }
    }
}
