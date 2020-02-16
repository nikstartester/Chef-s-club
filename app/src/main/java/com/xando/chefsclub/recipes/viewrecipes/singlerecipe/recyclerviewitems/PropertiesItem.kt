package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.recyclerviewitems

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.daimajia.androidanimations.library.YoYo
import com.mikepenz.fastadapter.items.AbstractItem
import com.xando.chefsclub.helper.DateTimeHelper
import com.xando.chefsclub.helper.UiHelper
import com.xando.chefsclub.R
import com.xando.chefsclub.recipes.data.RecipeData
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.list_view_recipe_overview_properties_item.view.*


class PropertiesItem(var recipeData: RecipeData, var isSavedLocal: Boolean, var isInit: Boolean = false)
    : AbstractItem<PropertiesItem, PropertiesItem.PropertiesViewHolder>() {

    private lateinit var viewHolder: PropertiesViewHolder

    private val disposer = CompositeDisposable()

    private val animations = mutableListOf<YoYo.YoYoString>()

    override fun getType() = R.id.overview_recipe_properties_item
    override fun getViewHolder(v: View) = PropertiesViewHolder(v)
    override fun getLayoutRes() = R.layout.list_view_recipe_overview_properties_item

    override fun bindView(holder: PropertiesViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        viewHolder = holder

        if(isInit)
            resetView()
    }

    fun resetView() {
        if (::viewHolder.isInitialized.not()) return

        var time = if (recipeData.isUpdated) "(UPD) " else ""
        time += DateTimeHelper.simpleTransform(recipeData.dateTime)

        viewHolder.tvTime.text = time

        updateStarsViews(false)

        updateSaveLocalViews()
    }

    override fun unbindView(holder: PropertiesViewHolder) {
        super.unbindView(holder)
        animations.forEach { it.stop() }
        animations.clear()
    }

    fun updateStarsViews(isUseAnim: Boolean) {
        if (::viewHolder.isInitialized.not()) return

        if (isUseAnim) updateStarsViewsWithAnim()
        else updateStarsViewsWithoutAnim()

        viewHolder.starContainer.visibility = View.VISIBLE
    }

    private fun updateStarsViewsWithAnim() {
        UiHelper.Favorite.setFavoriteImageWithAnim(viewHolder.imageFavorite, recipeData)
        UiHelper.Favorite.setFavoriteCountWithAnim(viewHolder.tvStarCount, recipeData.starCount)
    }

    private fun updateStarsViewsWithoutAnim() {
        viewHolder.tvStarCount.text = recipeData.starCount.toString()
        UiHelper.Favorite.setFavoriteImage(viewHolder.imageFavorite, recipeData)
    }

    fun updateSaveLocalViews() {
        if (::viewHolder.isInitialized.not()) return

        viewHolder.checkBoxIsSaveOnLocal.isChecked = isSavedLocal

        val anim = UiHelper.Other.showFadeAnim(viewHolder.imageSdStorage,
                if (isSavedLocal) View.VISIBLE else View.INVISIBLE)
        addAnim(anim)
    }

    private fun addAnim(anim: YoYo.YoYoString?) {
        if (anim != null) animations.add(anim)
    }

    class PropertiesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val checkBoxIsSaveOnLocal: CheckBox = itemView.checkBox_isSaveOnLocal
        val imageSdStorage: ImageView = itemView.imageView_sdStorage
        val imageFavorite: ImageView = itemView.imgFavorite
        val tvStarCount: TextView = itemView.tv_starCount
        val tvTime: TextView = itemView.tv_time

        val starContainer: View = itemView.star_container
    }
}