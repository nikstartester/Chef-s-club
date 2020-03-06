package com.xando.chefsclub.compilations.viewcompilations.Item;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.R;
import com.xando.chefsclub.compilations.data.CompilationData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompilationItem extends AbstractItem<CompilationItem, CompilationItem.ViewHolder> {

    private CompilationData mCompilationData;
    private ImageView mMoreBtn;

    public CompilationItem(CompilationData compilationData) {
        mCompilationData = compilationData;
    }

    public CompilationData getCompilationData() {
        return mCompilationData;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bindView(@NotNull ViewHolder holder, @NotNull List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.name.setText(mCompilationData.name);

        String countText = String.valueOf(mCompilationData.count);
        if (mCompilationData.count == 1)
            countText += " recipe";
        else countText += " recipes";

        holder.count.setText(countText);

        mMoreBtn = holder.more;
    }

    public ImageView getMoreBtn() {
        return mMoreBtn;
    }

    @Override
    public int getType() {
        return R.id.compilation_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_compilation_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        protected TextView name;
        @BindView(R.id.count)
        protected TextView count;
        @BindView(R.id.img_more)
        protected ImageView more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
