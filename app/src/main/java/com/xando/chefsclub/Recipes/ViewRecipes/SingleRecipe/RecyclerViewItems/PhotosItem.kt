package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.daimajia.androidanimations.library.YoYo
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.xando.chefsclub.DataWorkers.ParcResourceByParc
import com.xando.chefsclub.Helpers.UiHelper
import com.xando.chefsclub.Helpers.UiHelper.DURATION_NORMAL
import com.xando.chefsclub.Images.ImageData.ImageData
import com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader
import com.xando.chefsclub.Profiles.Data.ProfileData
import com.xando.chefsclub.Profiles.ViewModel.ProfileViewModel
import com.xando.chefsclub.R
import com.xando.chefsclub.Recipes.Data.RecipeData
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.list_view_recipe_overview_images_item.view.*
import java.lang.ref.WeakReference


class PhotosItem(var recipeData: RecipeData,
                 val profileViewModel: ProfileViewModel,
                 lifecycleOwner: LifecycleOwner,
                 var isInit: Boolean = false,
                 val onImageClick: (isMain: Boolean, pos: Int) -> Unit,
                 val onProfileClick: () -> Unit)
    : AbstractItem<PhotosItem, PhotosItem.PhotosViewHolder>() {

    private lateinit var viewHolder: PhotosViewHolder

    private val disposer = CompositeDisposable()

    private val ownerWeakReference = WeakReference<LifecycleOwner>(lifecycleOwner)

    private val animations = mutableListOf<YoYo.YoYoString>()

    private val imagesAdapter: FastItemAdapter<ImageViewItem> = FastItemAdapter()

    override fun getType() = R.id.overview_recipe_photos_item
    override fun getViewHolder(v: View) = PhotosViewHolder(v)
    override fun getLayoutRes() = R.layout.list_view_recipe_overview_images_item

    override fun bindView(holder: PhotosViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        viewHolder = holder

        if (isInit)
            resetView()

        viewHolder.imageView.setOnClickListener { onImageClick(true, 0) }

        imagesAdapter.withOnClickListener { v, adapter, item, position ->
            /*
            position + 1 because have main image(pos = 0)
             */
            onImageClick(false, position)

            true
        }

        viewHolder.authorImage.setOnClickListener { onProfileClick() }
        viewHolder.authorLogin.setOnClickListener { onProfileClick() }

        val owner = ownerWeakReference.get()
        if (owner != null)
            profileViewModel.resourceLiveData.observe(ownerWeakReference.get()!!, Observer { res ->
                if (res != null && res.status == ParcResourceByParc.Status.SUCCESS) {
                    setAuthorData(res.data!!)
                }
            })
    }

    fun resetView() {
        if (::viewHolder.isInitialized.not()) return

        viewHolder.recipeName.text = recipeData.overviewData.name

        setMainImage()

        setPhotos()
    }

    private fun setMainImage() {
        val imagePath = recipeData.overviewData.mainImagePath

        if (imagePath == null) {
            setEmptyImage()
        } else {
            val imageData = ImageData(imagePath, recipeData.dateTime)

            GlideImageLoader.getInstance()
                    .loadImage(viewHolder.imageView.context, viewHolder.imageView, imageData)
        }

    }

    private fun setEmptyImage() {
        viewHolder.imageView.setImageResource(R.drawable.ic_gallery_fill_96dp)
    }

    private fun setPhotos() {
        val layoutManager = LinearLayoutManager(viewHolder.recyclerViewImages.context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        viewHolder.recyclerViewImages.apply {
            setLayoutManager(layoutManager)
            adapter = imagesAdapter
            itemAnimator = DefaultItemAnimator()
        }

        imagesAdapter.clear()

        recipeData.overviewData.imagePathsWithoutMainList.forEach {
            imagesAdapter.add(ImageViewItem(ImageData(it, recipeData.dateTime)))
        }
    }

    private fun setAuthorData(data: ProfileData) {
        viewHolder.authorLogin.text = data.login

        val anim = UiHelper.Other.showFadeAnim(viewHolder.authorLogin, View.VISIBLE, DURATION_NORMAL)
        addAnim(anim)

        if (data.imageURL != null) {
            val imageData = ImageData(data.imageURL, data.lastTimeUpdate)

            GlideImageLoader.getInstance().loadSmallCircularImage(viewHolder.authorImage.context,
                    viewHolder.authorImage,
                    imageData)
        } else
            viewHolder.authorImage.setImageResource(R.drawable.ic_account_circle_elements_48dp)
    }

    private fun addAnim(anim: YoYo.YoYoString?) {
        if (anim != null) animations.add(anim)
    }

    override fun unbindView(holder: PhotosViewHolder) {
        super.unbindView(holder)
        stopAnimations()
    }

    fun stopAnimations() {
        animations.forEach {
            it.stop()
        }
        animations.clear()
    }

    class PhotosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.imgView_main
        val recyclerViewImages: RecyclerView = itemView.rv_images
        val recipeName: TextView = itemView.tv_name
        val authorLogin: TextView = itemView.login_profile
        val authorImage: ImageView = itemView.image_profile
    }
}