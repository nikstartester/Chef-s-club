package com.example.nikis.bludogramfirebase.Compilations.ViewCompilations.Item;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikis.bludogramfirebase.Compilations.Data.CompilationData;
import com.example.nikis.bludogramfirebase.R;
import com.mikepenz.fastadapter.items.AbstractItem;

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
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }


    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.name.setText(mCompilationData.name);
        holder.count.setText(mCompilationData.count + " recipes");

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