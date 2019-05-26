package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.ShoppingList.db.IngredientEntity;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.fastadapter_extensions.drag.IDraggable;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IngredientsViewItem extends AbstractItem<IngredientsViewItem, IngredientsViewItem.ViewHolder>
        implements ISwipeable<IngredientsViewItem, IItem>, IDraggable<IngredientsViewItem, IItem> {

    private TextView mTvIngredient;

    private CheckBox mCheckBox, mAvailableCheckBox;

    private IngredientEntity mEntity;

    private int mSwipedDirection;
    private Runnable mSwipedAction;

    private boolean isSwipeable = true;
    private boolean isDraggable = true;

    private boolean isEditAvailableMode;

    public IngredientsViewItem(IngredientEntity entity) {
        this.mEntity = entity;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    public String getIngredient() {
        return mTvIngredient.getText().toString();
    }

    @Override
    public int getType() {
        return R.id.ingredients_item_view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_ingredients_view_item;
    }

    @Override
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);
        mTvIngredient = holder.tv_ingredient;
        mCheckBox = holder.checkBox;

        holder.swipeResultContent.setVisibility(mSwipedDirection != 0 ? View.VISIBLE : View.GONE);

        mAvailableCheckBox = holder.availableCheckBox;

        updateEnabledAvailableCheckBox();

        setAvailableView();

        holder.swipedActionRunnable = this.mSwipedAction;

        mCheckBox.setChecked(isSelected());

        holder.tv_ingredient.setText(mEntity.ingredient);

    }

    public IngredientEntity getEntity() {
        return mEntity;
    }

    public void stopEditMode() {
        changeEditMode(false);
    }

    public void changeEditMode() {
        changeEditMode(!isEditAvailableMode);
    }

    public void changeEditMode(boolean isEdit) {
        isEditAvailableMode = isEdit;

        setAvailableView();

        updateEnabledAvailableCheckBox();
    }

    //Change selected without notify selections callbacks
    public void changeSelected(boolean isSelected) {
        withSetSelected(isSelected);

        if (mCheckBox != null) {
            mCheckBox.setChecked(isSelected);
        }

        if (!isSelected) setToUnavailable();
    }


    private void updateEnabledAvailableCheckBox() {
        if (mAvailableCheckBox == null)
            return;

        if (!isEditAvailableMode) mAvailableCheckBox.setEnabled(false);
        else mAvailableCheckBox.setEnabled(true);
    }

    public void setToUnavailable() {
        mEntity.isAvailable = false;

        setAvailableView();
    }

    public void changeAvailable() {
        updateAvailable(!mEntity.isAvailable);
    }

    public void updateAvailable(boolean isAvailable) {
        mEntity.isAvailable = isAvailable;

        setAvailableView();
    }

    private void setAvailableView() {
        if (mAvailableCheckBox != null) {

            boolean isVisible = mEntity.isAvailable || isEditAvailableMode;

            if (isVisible) {
                mAvailableCheckBox.setVisibility(View.VISIBLE);
            } else {
                mAvailableCheckBox.setVisibility(View.GONE);
            }

            mAvailableCheckBox.setChecked(mEntity.isAvailable);
        }
    }

    //protected abstract void onAvailableChanged(IngredientsViewItem item, boolean isAvailable, boolean isEditAvailableMode);

    @Override
    public boolean isSwipeable() {
        return isSwipeable;
    }

    @Override
    public IngredientsViewItem withIsSwipeable(boolean swipeable) {
        this.isSwipeable = swipeable;
        return this;
    }

    @Override
    public boolean isDraggable() {
        return isDraggable;
    }

    @Override
    public IngredientsViewItem withIsDraggable(boolean draggable) {
        this.isDraggable = draggable;
        return this;
    }

    public void setSwipedDirection(int swipedDirection) {
        this.mSwipedDirection = swipedDirection;
    }

    public void setSwipedAction(Runnable action) {
        this.mSwipedAction = action;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_ingredient)
        protected TextView tv_ingredient;

        @BindView(R.id.checkBox)
        public CheckBox checkBox;

        @BindView(R.id.swiped_action)
        TextView swipedAction;

        @BindView(R.id.swipe_result_content)
        View swipeResultContent;

        @BindView(R.id.checkBox_available)
        public CheckBox availableCheckBox;

        Runnable swipedActionRunnable;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            swipedAction.setOnClickListener(v -> {
                if (swipedActionRunnable != null) {
                    swipedActionRunnable.run();
                }
            });
        }
    }

    public static class CheckBoxClickEvent extends ClickEventHook<IngredientsViewItem> {
        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof IngredientsViewItem.ViewHolder) {
                return ((IngredientsViewItem.ViewHolder) viewHolder).checkBox;
            }
            return null;
        }

        @Override
        public void onClick(@NonNull View v, int position, @NonNull FastAdapter<IngredientsViewItem> fastAdapter, @NonNull IngredientsViewItem item) {
            fastAdapter.getExtension(SelectExtension.class).toggleSelection(position);
        }
    }
}
