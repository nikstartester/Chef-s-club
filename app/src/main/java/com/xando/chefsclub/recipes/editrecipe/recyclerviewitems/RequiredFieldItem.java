package com.xando.chefsclub.recipes.editrecipe.recyclerviewitems;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequiredFieldItem extends AbstractItem<RequiredFieldItem,
        RequiredFieldItem.ViewHolder> {

    public boolean isSatisfy;
    public String text;

    public RequiredFieldItem(String text, boolean isSatisfy) {
        this.text = text;
        this.isSatisfy = isSatisfy;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bindView(@NotNull ViewHolder holder, @NotNull List<Object> payloads) {
        super.bindView(holder, payloads);

        if (isSatisfy) {
            holder.image.setImageResource(R.drawable.ic_done_blue_36dp);
        } else holder.image.setImageResource(R.drawable.ic_close_red_36dp);

        holder.text.setText(text);
    }

    @Override
    public int getType() {
        return R.id.required_field_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_required_field;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_required)
        protected TextView text;

        @BindView(R.id.img_required)
        protected ImageView image;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

    }
}
