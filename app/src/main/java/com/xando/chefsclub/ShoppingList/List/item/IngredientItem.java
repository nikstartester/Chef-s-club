package com.xando.chefsclub.ShoppingList.List.item;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.daimajia.androidanimations.library.YoYo;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.fastadapter_extensions.drag.IDraggable;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;
import com.xando.chefsclub.Helpers.UiHelper;
import com.xando.chefsclub.R;
import com.xando.chefsclub.ShoppingList.db.IngredientEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientItem extends AbstractItem<IngredientItem, IngredientItem.ViewHolder>
        implements ISwipeable<IngredientItem, IItem>, IDraggable<IngredientItem, IItem> {
    public final IngredientEntity entity;
    private TextView tvIngredient;
    private CheckBox checkBox;
    private boolean isHighlightOnce;

    private int swipedDirection;
    private Runnable swipedAction;
    private boolean swipeable = true;
    private boolean draggable = true;

    private List<YoYo.YoYoString> anims = new ArrayList<>();

    public IngredientItem(IngredientEntity entity) {

        this(entity, false);
    }

    public IngredientItem(IngredientEntity entity, boolean isHighlightOnce) {

        this.entity = entity;
        this.isHighlightOnce = isHighlightOnce;
    }

    @NonNull
    @Override
    public IngredientItem.ViewHolder getViewHolder(@NonNull View v) {
        return new IngredientItem.ViewHolder(v);
    }

    public String getIngredient() {
        return tvIngredient.getText().toString();
    }

    @Override
    public int getType() {
        return R.id.shopping_list_ingredient_item_view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_shopping_cart_item;
    }

    @Override
    public void bindView(@NonNull IngredientItem.ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);
        tvIngredient = holder.tv_ingredient;
        checkBox = holder.checkBox;

        holder.swipeResultContent.setVisibility(swipedDirection != 0 ? View.VISIBLE : View.GONE);

        /*
        does't work:
         checkBox.setClickable(swipedDirection == 0);
        */

        holder.swipedActionRunnable = this.swipedAction;

        checkBox.setChecked(isSelected());

        holder.tv_ingredient.setText(entity.ingredient);

        if (isHighlightOnce) {
            isHighlightOnce = false;

            final int color = holder.root.getContext().getResources().getColor(R.color.colorAccent);

            List<YoYo.YoYoString> anims = UiHelper.Other.highlight(holder.root, color, new int[]{500, 700});

            this.anims.addAll(anims);
        }

    }

    @Override
    public void unbindView(@NonNull ViewHolder holder) {
        stopAnims();

        super.unbindView(holder);
    }

    public void stopAnims() {
        for (YoYo.YoYoString anim : anims) {
            anim.stop();
        }

        anims.clear();
    }

    @Override
    public boolean isSwipeable() {
        return swipeable;
    }

    @Override
    public IngredientItem withIsSwipeable(boolean swipeable) {
        this.swipeable = swipeable;
        return this;
    }

    @Override
    public boolean isDraggable() {
        return draggable;
    }

    @Override
    public IngredientItem withIsDraggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    public void setSwipedDirection(int swipedDirection) {
        this.swipedDirection = swipedDirection;
    }

    public void setSwipedAction(Runnable action) {
        this.swipedAction = action;
    }

    public IngredientItem setHighlightOnce(boolean highlightOnce) {
        isHighlightOnce = highlightOnce;

        return this;
    }

    public static class CheckBoxClickEvent extends ClickEventHook<IngredientItem> {
        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof IngredientItem.ViewHolder) {
                return ((IngredientItem.ViewHolder) viewHolder).checkBox;
            }
            return null;
        }

        @Override
        public void onClick(@NonNull View v, int position, @NonNull FastAdapter<IngredientItem> fastAdapter, @NonNull IngredientItem item) {
            fastAdapter.getExtension(SelectExtension.class).toggleSelection(position);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkBox)
        public CheckBox checkBox;
        @BindView(R.id.tv_ingredient)
        protected TextView tv_ingredient;
        @BindView(R.id.swiped_action)
        TextView swipedAction;

        @BindView(R.id.swipe_result_content)
        View swipeResultContent;

        @BindView(R.id.highlight)
        View root;


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
}

