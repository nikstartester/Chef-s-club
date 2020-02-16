package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.recyclerviewitems;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.R;

import java.util.List;

public class ChipCategoryItem extends AbstractItem<ChipCategoryItem, ChipCategoryItem.ViewHolder> {

    private static final int NORMAL_SIZE = 0;
    public static final int SMALL_SIZE = 1;
    public static final int SMALL_SIZE_WITHOUT_BORDER = 2;

    private final String categoryText;

    private int mSize = NORMAL_SIZE;

    public ChipCategoryItem(String categoryText) {
        this(categoryText, NORMAL_SIZE);
    }

    public ChipCategoryItem(String categoryText, int size) {
        this.categoryText = categoryText;
        mSize = size;

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
        switch (mSize) {
            case SMALL_SIZE:
                return R.layout.shape_chip_small;
            case SMALL_SIZE_WITHOUT_BORDER:
                return R.layout.chip_small;
            default:
                return R.layout.shape_chip;
        }
    }

    @Override
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.category.setText(categoryText);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView category;

        ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.tv_category);
        }
    }
}
