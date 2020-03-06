package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.comments;

import androidx.lifecycle.LifecycleOwner;

import com.xando.chefsclub.R;
import com.xando.chefsclub.recipes.viewrecipes.singlerecipe.comments.Data.CommentData;

public class CommentItemSmall extends CommentItem {

    public CommentItemSmall(LifecycleOwner owner, CommentData commentData, boolean isVisible) {
        super(owner, commentData, isVisible);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_comment_item_small;
    }

    @Override
    public int getType() {
        return R.id.comment_item_small;
    }
}
