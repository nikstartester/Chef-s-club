package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.view.View;

import com.daimajia.androidanimations.library.YoYo;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.Data.CommentData;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.ViewHolder.CommentViewHolder;

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
    public CommentViewHolder getViewHolder(View v) {
        return new CommentViewHolder(v);
    }

    @Override
    public void bindView(CommentViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.changeVisible(isVisible);

        holder.bindToComment(mOwnerWeakReference.get(), mCommentData);

        mViewHolder = holder;
    }

    @Override
    public void unbindView(CommentViewHolder holder) {
        stopAnims();

        super.unbindView(holder);
    }

    public void stopAnims() {
        if (getViewHolder() != null) {
            for (YoYo.YoYoString anim : getViewHolder().anims) {
                anim.stop();
            }

            getViewHolder().anims.clear();
        }
    }

    public void changeVisible(boolean vis) {
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
