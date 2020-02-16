package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.comments;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.view.View;

import com.daimajia.androidanimations.library.YoYo;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.R;
import com.xando.chefsclub.recipes.viewrecipes.singlerecipe.comments.Data.CommentData;
import com.xando.chefsclub.recipes.viewrecipes.singlerecipe.comments.ViewHolder.CommentViewHolder;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

public class CommentItem extends AbstractItem<CommentItem, CommentViewHolder> {

    private final WeakReference<LifecycleOwner> mOwnerWeakReference;

    private CommentData mCommentData;

    private boolean isVisible;

    private CommentViewHolder mViewHolder;

    public CommentItem(LifecycleOwner owner, CommentData commentData, boolean isVisible) {
        mOwnerWeakReference = new WeakReference<>(owner);
        mCommentData = commentData;
        this.isVisible = isVisible;
    }

    @NonNull
    @Override
    public CommentViewHolder getViewHolder(@NotNull View v) {
        return new CommentViewHolder(v);
    }

    @Override
    public void bindView(@NotNull CommentViewHolder holder, @NotNull List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.changeVisible(isVisible);

        holder.bindToComment(mOwnerWeakReference.get(), mCommentData);

        mViewHolder = holder;
    }

    @Override
    public void unbindView(@NotNull CommentViewHolder holder) {
        stopAnims();

        super.unbindView(holder);
    }

    private void stopAnims() {
        if (getViewHolder() != null) {
            for (YoYo.YoYoString anim : getViewHolder().anims) {
                if (anim != null)
                    anim.stop();
            }

            getViewHolder().anims.clear();
        }
    }

    void changeVisible(boolean vis) {
        if (mViewHolder != null) {
            mViewHolder.changeVisible(vis);
        }
        isVisible = vis;
    }

    public void startHighlight() {
        if (mViewHolder != null) {
            mViewHolder.startHighlight();
        }
    }

    public CommentViewHolder getViewHolder() {
        return mViewHolder;
    }

    public CommentData getCommentData() {
        return mCommentData;
    }

    @Override
    public int getType() {
        return R.id.comment_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_comment_item;
    }
}
