package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.items.AbstractItem
import com.xando.chefsclub.R
import kotlinx.android.synthetic.main.list_view_recipe_overview_comments_header_item.view.*

class CommentsHeaderItem(private var commentsCount: Int = -1,
                         private var isProgressVisible: Boolean = false)
    : AbstractItem<CommentsHeaderItem, CommentsHeaderItem.CommentsHeaderViewHolder>() {

    private lateinit var viewHolder: CommentsHeaderViewHolder

    override fun getType() = R.id.overview_recipe_comments_header_item
    override fun getViewHolder(v: View) = CommentsHeaderViewHolder(v)
    override fun getLayoutRes() = R.layout.list_view_recipe_overview_comments_header_item

    override fun bindView(holder: CommentsHeaderViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        viewHolder = holder

        updateCommentsCountView()

        updateProgressViewVisibility()
    }

    fun setCommentsCount(commentsCount: Int) {
        this.commentsCount = commentsCount

        if (::viewHolder.isInitialized)
            updateCommentsCountView()
    }

    private fun updateCommentsCountView() {
        viewHolder.commentsCountView.text = if (commentsCount != -1) "($commentsCount)" else ""
    }

    fun setProgressViewIsVisible(isVisible: Boolean) {
        isProgressVisible = isVisible

        if (::viewHolder.isInitialized)
            updateProgressViewVisibility()
    }

    private fun updateProgressViewVisibility() {
        viewHolder.progressView.visibility = if (isProgressVisible)
            View.VISIBLE
        else View.INVISIBLE
    }

    class CommentsHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val commentsCountView: TextView = itemView.comments_count
        val progressView: TextView = itemView.progress_comment
    }
}