package com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest.RecyclerViewItems;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikis.bludogramfirebase.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class ChipCategoryWithRemoveItem extends AbstractItem<ChipCategoryWithRemoveItem, ChipCategoryWithRemoveItem.ViewHolder> {
    private String categoryText;
    private int categoryType;

    public ChipCategoryWithRemoveItem(String categoryText, int categoryType) {
        this.categoryText = categoryText;
        this.categoryType = categoryType;
    }
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.chip_category_with_remove_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.shape_chip_with_remove;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.category.setText(categoryText);
    }

    public int getCategoryType() {
        return categoryType;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView category;
        ImageView btnDelete;
        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.tv_category_rem);
            btnDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}

