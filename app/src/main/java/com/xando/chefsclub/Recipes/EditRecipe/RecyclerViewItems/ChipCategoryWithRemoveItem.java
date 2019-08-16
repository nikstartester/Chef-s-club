package com.xando.chefsclub.Recipes.EditRecipe.RecyclerViewItems;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.R;

import java.util.List;

public class ChipCategoryWithRemoveItem extends AbstractItem<ChipCategoryWithRemoveItem,
        ChipCategoryWithRemoveItem.ViewHolder> {

    private static final int NORMAL_SIZE = 0;
    public static final int SMALL_SIZE = 1;

    private final String categoryText;
    private final int categoryType;

    private int mSize = NORMAL_SIZE;

    public ChipCategoryWithRemoveItem(String categoryText, int categoryType) {
        this(categoryText, categoryType, NORMAL_SIZE);
    }

    public ChipCategoryWithRemoveItem(String categoryText, int categoryType, int size) {
        this.categoryText = categoryText;
        this.categoryType = categoryType;
        mSize = size;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.chip_category_with_remove_item;
    }

    @Override
    public int getLayoutRes() {
        switch (mSize) {
            case SMALL_SIZE:
                return R.layout.shape_chip_with_remove_small;
            default:
                return R.layout.shape_chip_with_remove;
        }
    }

    @Override
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.category.setText(categoryText);
    }

    @Deprecated
    public int getCategoryType() {
        return categoryType;
    }

    public String getCategoryText() {
        return categoryText;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView category;
        final ImageView btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.tv_category_rem);
            btnDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}

