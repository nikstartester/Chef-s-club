package com.xando.chefsclub.compilations.addrecipe.item;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.compilations.data.CompilationData;
import com.xando.chefsclub.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompilationShortItem extends AbstractItem<CompilationShortItem, CompilationShortItem.ViewHolder> {

    private CompilationData mCompilationData;

    public CompilationShortItem(CompilationData compilationData) {
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
        holder.count.setText(mCompilationData.count + " recipes");
    }

    @Override
    public int getType() {
        return R.id.compilation_short_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_compilation_short_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        protected TextView name;
        @BindView(R.id.count)
        protected TextView count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
