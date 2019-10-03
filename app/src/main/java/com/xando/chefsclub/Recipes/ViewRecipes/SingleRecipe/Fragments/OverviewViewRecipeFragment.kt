package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Fragments

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import com.xando.chefsclub.DataWorkers.BaseRepository
import com.xando.chefsclub.DataWorkers.ParcResourceByParc
import com.xando.chefsclub.FirebaseList.FirebaseGroupListAdapter
import com.xando.chefsclub.FirebaseReferences
import com.xando.chefsclub.Helpers.*
import com.xando.chefsclub.Images.ImageData.ImageData
import com.xando.chefsclub.Images.ViewImages.ViewImagesActivity
import com.xando.chefsclub.List.GroupAdapter
import com.xando.chefsclub.List.HideableItemsGroupAdapter
import com.xando.chefsclub.List.MultiGroupsRecyclerViewAdapterImpl
import com.xando.chefsclub.List.SingleItemGroupAdapter
import com.xando.chefsclub.Profiles.ViewModel.ProfileViewModel
import com.xando.chefsclub.Profiles.ViewProfiles.SingleProfile.ViewProfileActivityTest
import com.xando.chefsclub.R
import com.xando.chefsclub.Recipes.Data.RecipeData
import com.xando.chefsclub.Recipes.ViewModel.RecipeViewModel
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.CommentItem
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.CommentItemSmall
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.CommentUploader
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.Data.CommentData
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.ViewHolder.CommentViewHolder
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Ingredients.IngrediensViewModel
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.*
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.IngredientsEditModeItem.IngredientsEditModeViewHolder
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.IngredientsViewItem.IngredientViewItemViewHolder
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.PropertiesItem.PropertiesViewHolder
import com.xando.chefsclub.Recipes.db.RecipeToFavoriteEntity
import com.xando.chefsclub.ShoppingList.ViewShoppingListActivity
import com.xando.chefsclub.ShoppingList.db.Helper
import com.xando.chefsclub.ShoppingList.db.IngredientEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recycler_view.view.recycler_view
import kotlinx.android.synthetic.main.fragment_view_recipe_overview.view.*

private const val TAG = "RECIPE_TEST"

private const val PHOTO_ADAPTER_ID = 10
private const val PROPERTIES_ADAPTER_ID = 11
private const val CATEGORIES_ADAPTER_ID = 12
private const val DESCRIPTION_ADAPTER_ID = 13
private const val INGREDIENTS_HEADER_ADAPTER_ID = 14
private const val INGREDIENTS_EDIT__MODE_ADAPTER_ID = 15
private const val INGREDIENTS_ADAPTER_ID = 16
private const val COMMENTS_HEADER_ID = 17
private const val COMMENTS_ID = 18
private const val MORE_ID = 19


private const val MAX_COMMENTS_AT_START = 6

private const val KEY_REPLAY_DATA = "KEY_REPLAY_DATA"
private const val KEY_REPLAY_PROCESS = "KEY_REPLAY_PROCESS"

class OverviewRecipeFragment : BaseFragmentWithRecipeKey() {

    private val recipeViewModel: RecipeViewModel by lazy { getHostViewModel<RecipeViewModel>() }
    private val profileViewModel: ProfileViewModel by lazy { getViewModel<ProfileViewModel>() }

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingFilterView: View

    private lateinit var addCommentTextView: TextView
    private lateinit var commentUploadProgress: ProgressBar
    private lateinit var sendCommentView: ImageButton

    private lateinit var replyContentView: View
    private lateinit var replyUserNameView: TextView
    private lateinit var replyTextView: TextView

    private var recipeData: RecipeData? = null

    private val disposer = CompositeDisposable()

    private val ingredientsMap = HashMap<String, Int>()
    private val ingredientsViewModel: IngrediensViewModel by lazy { getViewModel<IngrediensViewModel>() }

    private var isCommentInProgress = false

    private var isReplyProcess = false
    private var mReplyComment: CommentData? = null

    private val multiGroupsRecyclerViewAdapter = MultiGroupsRecyclerViewAdapterImpl()

    private val fastAdapter = multiGroupsRecyclerViewAdapter.getFastAdapter()

    private val photosAdapter = SingleItemGroupAdapter<PhotosItem>(PHOTO_ADAPTER_ID,
            multiGroupsRecyclerViewAdapter)
    private val propertiesAdapter = SingleItemGroupAdapter<PropertiesItem>(PROPERTIES_ADAPTER_ID,
            multiGroupsRecyclerViewAdapter)
    private val categoriesAdapter = SingleItemGroupAdapter<CategoriesItem>(CATEGORIES_ADAPTER_ID,
            multiGroupsRecyclerViewAdapter)
    private val descriptionAdapter = SingleItemGroupAdapter<DescriptionItem>(DESCRIPTION_ADAPTER_ID,
            multiGroupsRecyclerViewAdapter)
    private val ingredientsHeaderAdapter = SingleItemGroupAdapter<IngredientsHeaderItem>(INGREDIENTS_HEADER_ADAPTER_ID,
            multiGroupsRecyclerViewAdapter)
    private val ingredientsEditModeAdapter = SingleItemGroupAdapter<IngredientsEditModeItem>(INGREDIENTS_EDIT__MODE_ADAPTER_ID,
            multiGroupsRecyclerViewAdapter)
    private val ingredientsAdapter = GroupAdapter<IngredientsViewItem>(INGREDIENTS_ADAPTER_ID,
            multiGroupsRecyclerViewAdapter)
    private val commentsHeaderAdapter = SingleItemGroupAdapter<CommentsHeaderItem>(COMMENTS_HEADER_ID,
            multiGroupsRecyclerViewAdapter)
    private val commentsAdapter = HideableItemsGroupAdapter<CommentItem>(MAX_COMMENTS_AT_START, COMMENTS_ID,
            multiGroupsRecyclerViewAdapter)
    private val moreAdapter = SingleItemGroupAdapter<MoreItem>(MORE_ID,
            multiGroupsRecyclerViewAdapter)


    private lateinit var firebaseCommentsAdapter: FirebaseGroupListAdapter<CommentData, CommentItem>

    private var isLoaded = false

    companion object {
        fun getInstance(recipeId: String) = OverviewRecipeFragment().withRecipeKey(recipeId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseCommentsAdapter = getCommentsFirebaseAdapterInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_view_recipe_overview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        recyclerView.adapter = multiGroupsRecyclerViewAdapter

        recipeViewModel.resourceLiveData.observe(this, Observer {
            if (it != null) {
                when {
                    it.status == ParcResourceByParc.Status.SUCCESS -> {
                        onSuccessLoaded(it)
                        hideProgress()
                    }
                    it.status == ParcResourceByParc.Status.ERROR -> {
                        onErrorLoaded(it)
                        hideProgress()
                    }
                    else -> showProgress()
                }
            }
        })

        if (recipeId.isNotEmpty()) {
            val flowable = getApp().database
                    .recipeDao()
                    .getFlowableByRecipeKey(recipeId)

            val disposable = flowable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { recipeEntities ->
                        //
                        // it calls twice if use sync when open recipe(see BaseRepository class)
                        // because use flowable
                        //
                        propertiesAdapter.getItem().apply {
                            if (isSavedLocal != recipeEntities.isNotEmpty()) {
                                isSavedLocal = recipeEntities.isNotEmpty()
                                updateSaveLocalViews()
                            }
                        }
                    }

            disposer.add(disposable)
        }

        ingredientsViewModel.data.observe(this, Observer { res ->
            if (res != null) {
                onIngredientsLoadedFromDb(res)
            }
        })

        if (savedInstanceState != null) {
            isReplyProcess = savedInstanceState.getBoolean(KEY_REPLAY_PROCESS)
            if (isReplyProcess) {
                mReplyComment = savedInstanceState.getParcelable(KEY_REPLAY_DATA)

                if (mReplyComment != null) {
                    showReplay(mReplyComment!!)
                }
            }
        }
    }

    private fun getCommentsFirebaseAdapterInstance() = object : FirebaseGroupListAdapter<CommentData, CommentItem>(
            FirebaseRecyclerOptions.Builder<CommentData>()
                    .setQuery(getQuery(), CommentData::class.java).build(), null, commentsAdapter) {

        override fun getUniqueId(data: CommentData) = data.commentId

        override fun getNewItemInstance(data: CommentData, pos: Int) =
                if (data.replyId == null) CommentItem(this@OverviewRecipeFragment, data, true)
                else CommentItemSmall(this@OverviewRecipeFragment, data, true)

        override fun onItemChanged(item: CommentItem, data: CommentData, pos: Int) = true

        override fun onDataChanged() {
            super.onDataChanged()

            updateCommentsHeader(snapshots.size, false)

            updateMoreVisibility(snapshots.size)
        }

        override fun onError(databaseError: DatabaseError) {
            super.onError(databaseError)

            updateCommentsHeader(-1, false)
        }
    }

    private fun getQuery() = FirebaseReferences.getDataBaseReference()
            .child("comments")
            .child("recipes")
            .child(recipeId)
            .orderByChild("date")

    private fun updateCommentsHeader(commentsCount: Int, isProgressVisible: Boolean) {
        commentsHeaderAdapter.getItem().apply {
            setCommentsCount(commentsCount)
            setProgressViewIsVisible(isProgressVisible)
        }
    }

    private fun updateMoreVisibility(count: Int) {
        if (!commentsAdapter.isVisibleAll && count > MAX_COMMENTS_AT_START) {
            moreAdapter.setItem(MoreItem((count - MAX_COMMENTS_AT_START).toString() + " more"))
        } else moreAdapter.removeItem()
    }

    private fun initViews(mainView: View) {
        initRecyclerView(mainView)

        loadingFilterView = mainView.findViewById(R.id.filter)

        addCommentTextView = mainView.comment_text
        commentUploadProgress = mainView.comment_upload_progress

        sendCommentView = mainView.comment_send.apply { setOnClickListener { sendNewComment() } }
        mainView.reply_close.setOnClickListener { closeReply() }


        replyContentView = mainView.conteiner_reply
        replyUserNameView = mainView.reply_comment_pofile_name
        replyTextView = mainView.reply_comment_text

        multiGroupsRecyclerViewAdapter.addGroups(listOf(
                photosAdapter.groupId,
                propertiesAdapter.groupId,
                categoriesAdapter.groupId,
                descriptionAdapter.groupId,
                ingredientsHeaderAdapter.groupId,
                ingredientsEditModeAdapter.groupId,
                ingredientsAdapter.groupId,
                commentsHeaderAdapter.groupId,
                commentsAdapter.groupId,
                moreAdapter.groupId)
        )

        photosAdapter.setItem(PhotosItem(RecipeData(), profileViewModel, this,
                onImageClick = ::onImageClick, onProfileClick = ::onProfileClick))
        propertiesAdapter.setItem(PropertiesItem(RecipeData(), false))

        initAdapters()
    }

    private fun initRecyclerView(mainView: View) {
        recyclerView = mainView.recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
        }
    }

    private fun initAdapters() {
        fastAdapter.apply {
            addExtension(SelectExtension<IItem<Any, RecyclerView.ViewHolder>>()
                    .withSelectionListener { item, selected ->
                        if (selected) Helper.addToDB(getApp(), (item as IngredientsViewItem).data)
                        else {
                            (item as IngredientsViewItem).setUnavailable()
                            Helper.deleteFromDB(getApp(), item.data)
                        }
                    })

            withOnPreClickListener { _, _, item, position ->
                true
            }

            withEventHook(object : ClickEventHook<IItem<Any, RecyclerView.ViewHolder>>() {

                override fun onBindMany(viewHolder: RecyclerView.ViewHolder): MutableList<View> {
                    val list = mutableListOf<View>()
                    when (viewHolder) {
                        is PropertiesViewHolder -> list.add(viewHolder.imageFavorite)
                        is IngredientsEditModeViewHolder -> list.add(viewHolder.closeView)
                        is IngredientViewItemViewHolder -> list.add(viewHolder.availableCheckBox)
                        is MoreItem.MoreViewHolder -> list.add(viewHolder.moreView)
                        is CommentViewHolder -> list.apply {
                            add(viewHolder.profileName)
                            add(viewHolder.profileImage)
                            add(viewHolder.reply)
                            add(viewHolder.replyContent)
                        }
                    }
                    return list
                }

                override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<IItem<Any, RecyclerView.ViewHolder>>,
                                     item: IItem<Any, RecyclerView.ViewHolder>) {
                    when (v.id) {
                        R.id.imgFavorite -> onFavoriteClick(item as PropertiesItem)
                        R.id.imgBtn_close_edit_mode -> changeIngredientsEditMode()
                        R.id.checkBox_available -> changeAvailable(item as IngredientsViewItem)
                        R.id.comments_more -> { showAllComments() }
                        R.id.comment_profile_image, R.id.comment_pofile_name -> startActivity(ViewProfileActivityTest.getIntent(activity,
                                (item as CommentItem).commentData.authorId))
                        R.id.comment_reply -> onReplyComment((item as CommentItem).commentData)
                        R.id.reply_content -> onReplyMessageClick((item as CommentItem))
                    }
                }
            })

            withEventHook(IngredientsViewItem.CheckBoxClickEvent())

            withOnLongClickListener { _, _, item, _ ->
                if (item.type == R.id.ingredients_item_view_new)
                    if (!isIngredientsInEditMode()) {
                        changeIngredientsEditMode()
                        true
                    } else false
                else false
            }
        }
    }

    override fun onStop() {
        if (isIngredientsInEditMode()) changeIngredientsEditMode()
        super.onStop()
    }

    override fun onDestroy() {
        disposer.dispose()
        firebaseCommentsAdapter.stopListening()

        super.onDestroy()
    }

    private fun onSuccessLoaded(res: ParcResourceByParc<RecipeData>) {
        if (isLoaded) return

        isLoaded = true

        recipeData = res.data

        setPhotos()
        setProperties()
        setCategories()
        setDescription()
        setIngredientsHeader()
        setIngredients()

        loadProfileIfNeed()

        commentsHeaderAdapter.setItem(CommentsHeaderItem(-1, true))

        firebaseCommentsAdapter.startListening()
    }

    private fun setPhotos() {
        photosAdapter.getItem().apply {
            recipeData = this@OverviewRecipeFragment.recipeData!!
            resetView()
            isInit = true
        }
    }

    private fun setProperties() {
        propertiesAdapter.getItem().apply {
            recipeData = this@OverviewRecipeFragment.recipeData!!
            resetView()
            isInit = true
        }
    }

    private fun setCategories() {
        recipeData!!.overviewData.strCategories
                .filter { it.isNullOrEmpty().not() }
                .let { if (it.isNotEmpty()) categoriesAdapter.setItem(CategoriesItem(it)) }
    }

    private fun setDescription() {
        recipeData!!.overviewData.description.let {
            if (it.isNotBlank()) descriptionAdapter.setItem(DescriptionItem(it))
        }
    }

    private fun setIngredientsHeader() {
        ingredientsHeaderAdapter.setItem(IngredientsHeaderItem(onActionClick = ::onIngredientsActionsClick))
    }

    private fun setIngredients() {
        ingredientsAdapter.addItems(with(recipeData!!) {
            overviewData.ingredientsList.mapIndexed { index, s ->
                ingredientsMap[s] = index
                IngredientsViewItem(IngredientEntity(recipeKey, overviewData.name, s, dateTime))
            }
        })

        if (ingredientsViewModel.data.value == null) {
            ingredientsViewModel.loadData(recipeData!!.recipeKey)
        } else {
            onIngredientsLoadedFromDb(ingredientsViewModel.data.value!!)
        }
    }

    private fun loadProfileIfNeed() {
        if (profileViewModel.resourceLiveData.value == null) {
            profileViewModel.loadDataWithoutSaver(recipeData!!.authorUId)
        }
    }

    private fun onErrorLoaded(res: ParcResourceByParc<RecipeData>) {
        if (res.exception is BaseRepository.NothingFoundFromServerException)
            sendCommentView.isEnabled = false
    }

    private fun hideProgress() {
        loadingFilterView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showProgress() {
        recyclerView.visibility = View.GONE
        loadingFilterView.visibility = View.VISIBLE
    }

    private fun onImageClick(isMain: Boolean, pos: Int) {
        val hasMain = recipeData!!.overviewData.mainImagePath != null
        if (isMain && hasMain.not())
            return

        val imageDataList = ArrayList<ImageData>()

        (if (hasMain) recipeData!!.overviewData.allImagePathList
        else recipeData!!.overviewData.imagePathsWithoutMainList)
                .forEach { imageDataList.add(ImageData(it, recipeData!!.dateTime)) }

        val selectedItem = if (isMain) 0 else pos + if (hasMain) 1 else 0

        if (imageDataList.isNotEmpty()) {
            startActivity(ViewImagesActivity.getIntent(activity, imageDataList, selectedItem))
        }
    }

    private fun onProfileClick() {
        startActivity(ViewProfileActivityTest.getIntent(activity, recipeData!!.authorUId))
    }

    private fun onFavoriteClick(item: PropertiesItem) {
        FirebaseHelper.Favorite.updateFavorite(getApp(), RecipeToFavoriteEntity(recipeId, recipeData!!.authorUId))

        FirebaseHelper.Favorite.updateDBAfterFavoriteChange(getApp(),
                FirebaseHelper.Favorite.updateRecipeDataWithFavoriteChange(recipeData))

        item.updateStarsViews(true)
    }

    //region Ingredients
    private fun onIngredientsActionsClick(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.act_edit_available -> changeIngredientsEditMode()
            R.id.act_add_all_to_shopping_list -> addAllIngredientsToShoppingList()
            R.id.act_delete_all_from_shopping_list -> deleteAllIngredientsFromShoppingList()
            R.id.act_open_shopping_list -> startActivity(ViewShoppingListActivity.getIntent(activity, recipeData!!.recipeKey)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
    }

    private fun onIngredientsLoadedFromDb(res: List<IngredientEntity>) {
        val changedPosList = mutableListOf<Int>()
        res.forEach {
            val changedPos = setIngredientToAdapterFromDB(it)
            if (changedPos != -1) changedPosList.add(changedPos)
        }

        syncRemovedItems(changedPosList)
    }

    private fun setIngredientToAdapterFromDB(ingr: IngredientEntity): Int {
        val pos = ingredientsMap[ingr.ingredient] ?: return -1

        ingredientsAdapter.getItem(pos).apply {
            if (data.isSameAvailable(ingr).not()) {
                data = ingr
                updateAvailable()
            }
            if (!isSelected)
                changeSelected(true)
        }
        return pos
    }

    private fun IngredientEntity.isSameAvailable(ingr: IngredientEntity) =
            this.isAvailable == ingr.isAvailable

    //Ingredients can be removed from like shoppingList. So check that
    private fun syncRemovedItems(changedPosList: List<Int>) {
        ingredientsAdapter.getItems().forEachIndexed { index, item ->
            if (!changedPosList.contains(index) && item.isSelected)
                item.changeSelected(false)
        }
    }

    private fun changeIngredientsEditMode() {
        val hasAlreadyEditMode = ingredientsEditModeAdapter.count > 0
        if (hasAlreadyEditMode.not())
            ingredientsEditModeAdapter.setItem(IngredientsEditModeItem({ isChecked ->
                if (isChecked) {
                    addAllIngredientsToShoppingList()
                } else
                    deleteAllIngredientsFromShoppingList()
            }, { isChecked ->
                if (isChecked) {
                    setAllIngredientsAvailable(true)
                } else
                    setAllIngredientsAvailable(false)
            }))
        else ingredientsEditModeAdapter.removeItem()
        ingredientsHeaderAdapter.getItem().isEditMode = !hasAlreadyEditMode

        ingredientsAdapter.getItems().forEach { it.changeEditMode(!hasAlreadyEditMode) }
        ingredientsAdapter.notifyDataSetChanged()
    }

    private fun isIngredientsInEditMode() = ingredientsEditModeAdapter.count > 0

    private fun addAllIngredientsToShoppingList() {
        Helper.addListToDB(getApp(), ingredientsAdapter.getItems().map { it.data })
    }

    private fun deleteAllIngredientsFromShoppingList() {
        Helper.deleteByRecipeIdFromDB(getApp(), recipeData!!.recipeKey)
    }

    private fun setAllIngredientsAvailable(isAvailable: Boolean) {
        if (isAvailable)
            Helper.replace(getApp(), ingredientsAdapter.getItems().map {
                with(it.data) { IngredientEntity(recipeId, recipeName, ingredient, isAvailable, time) }
            })
        else Helper.changeAllAvailable(getApp(), recipeData!!.recipeKey, isAvailable)
    }

    private fun changeAvailable(item: IngredientsViewItem) {
        changeAvailable(item, item.data.isAvailable.not())
    }

    private fun changeAvailable(item: IngredientsViewItem, isAvailable: Boolean) {
        if (item.data.isAvailable == isAvailable) {
            return
        }
        Helper.changeAvailableFromDBNew(getApp(), with(item.data) { IngredientEntity(recipeId, recipeName, ingredient, isAvailable, time) })
    }
    //endregion

    //region Comments
    private fun showAllComments() {
        commentsAdapter.updateItemVisibility(true)
        updateMoreVisibility(-1)
    }

    private fun onReplyMessageClick(item: CommentItem) {
        val replyId = item.commentData.replyId

        var pos = -1

        for (i in commentsAdapter.getItems().indices) {
            if (commentsAdapter.getItems()[i].commentData.commentId == replyId) {
                pos = i
                break
            }
        }

        val itemReply = if (pos != -1) commentsAdapter.getItem(pos) else return

        recyclerView.scrollToPosition(commentsAdapter.getAdapterPosition(pos))

        recyclerView.postDelayed({ itemReply.startHighlight() }, 20)
    }

    private fun onUserAddedComment() {
        if (isCommentInProgress) {
            isCommentInProgress = false

            addCommentTextView.text = ""

            Keyboard.hideKeyboardFrom(context!!, addCommentTextView)

            showAllComments()

            scrollDown()

            hideCommentProgress()
        }
    }

    private fun scrollDown() {
        if (fastAdapter.itemCount - 1 > 0)
            recyclerView.scrollToPosition(fastAdapter.itemCount - 1)
    }

    private fun onReplyComment(commentData: CommentData) {
        isReplyProcess = true
        mReplyComment = commentData

        showReplay(commentData)

        addCommentTextView.requestFocus()

        scrollDown()

        Keyboard.showKeyboardFrom(context!!, addCommentTextView)
    }


    private fun showReplay(commentData: CommentData) {
        replyTextView.text = commentData.text.replace("\n", " ")
        replyUserNameView.text = commentData.authorLogin

        replyContentView.visibility = View.VISIBLE
    }

    private fun closeReply() {
        replyContentView.visibility = View.GONE

        isReplyProcess = false
    }

    private fun sendNewComment() {
        if (NetworkHelper.isConnected(activity!!)) {
            val commentText = addCommentTextView.text.toString()
            if (!TextUtils.isEmpty(commentText) && commentText[0] != ' ') {
                startSendComment()
            }
        } else Toast.makeText(activity, getString(R.string.network_error), Toast.LENGTH_SHORT).show()

    }

    private fun startSendComment() {
        isCommentInProgress = true

        showCommentProgress()

        val commentText = addCommentTextView.text.toString()
        val data = CommentData(commentText, FirebaseHelper.getUid(), recipeId)

        if (isReplyProcess && mReplyComment != null) {
            data.replyId = mReplyComment!!.commentId
            data.replyText = mReplyComment!!.text
            data.replyAuthorId = mReplyComment!!.authorId
        }

        CommentUploader().start(data) { resource ->
            if (resource.status == ParcResourceByParc.Status.SUCCESS) {
                onUserAddedComment()

                if (isReplyProcess) {
                    closeReply()
                    CommentUploader.updtInReplays(mReplyComment, resource.data!!.commentId)
                }

            } else if (resource.status == ParcResourceByParc.Status.ERROR) {
                if (resource.exception is BaseRepository.NothingFoundFromServerException) {

                    sendCommentView.isEnabled = false

                    Toast.makeText(activity, getString(R.string.impossible_actions_recipe_deleted),
                            Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, resource.exception!!.message,
                            Toast.LENGTH_SHORT).show()
                }
                onUserAddedComment()
            }
        }
    }

    private fun showCommentProgress() {
        sendCommentView.visibility = View.INVISIBLE
        commentUploadProgress.visibility = View.VISIBLE
    }

    private fun hideCommentProgress() {
        commentUploadProgress.visibility = View.INVISIBLE
        sendCommentView.visibility = View.VISIBLE
    }
    //endregion

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_REPLAY_DATA, mReplyComment)
        outState.putBoolean(KEY_REPLAY_PROCESS, isReplyProcess)
        super.onSaveInstanceState(outState)
    }
}