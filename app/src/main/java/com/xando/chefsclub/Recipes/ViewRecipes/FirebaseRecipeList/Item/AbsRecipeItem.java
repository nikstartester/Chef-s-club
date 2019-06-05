package com.xando.chefsclub.Recipes.ViewRecipes.FirebaseRecipeList.Item;

import android.app.Application;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.YoYo;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.App;
import com.xando.chefsclub.DataWorkers.ActualDataChecker;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.Helpers.DateTimeHelper;
import com.xando.chefsclub.Helpers.UiHelper;
import com.xando.chefsclub.Images.ImageData.ImageData;
import com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader;
import com.xando.chefsclub.Profiles.Data.ProfileData;
import com.xando.chefsclub.Profiles.Repository.ProfileRepository;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.Data.ActualRecipeDataChecker;
import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.EditRecipe.DialogTimePicker;
import com.xando.chefsclub.Recipes.db.RecipeEntity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.xando.chefsclub.Helpers.FirebaseHelper.getUid;
import static com.xando.chefsclub.Profiles.Repository.ProfileRepository.CHILD_USERS;

public abstract class AbsRecipeItem extends AbstractItem<AbsRecipeItem, AbsRecipeItem.ViewHolder> implements LifecycleObserver {

    private final MutableLiveData<ParcResourceByParc<ProfileData>> mProfileDataLiveData;

    private final WeakReference<LifecycleOwner> mOwnerWeakReference;

    private final CompositeDisposable mDisposable;

    public boolean isSavedLoacal = true;

    private final List<YoYo.YoYoString> mAnimations;
    @Nullable
    protected AbsRecipeItem.ViewHolder mViewHolder;
    @Nullable
    private RecipeData mRecipeData;

    AbsRecipeItem(@Nullable LifecycleOwner lifecycleOwner) {
        this(null, lifecycleOwner);
    }

    public AbsRecipeItem(@Nullable RecipeData recipeData, @Nullable LifecycleOwner lifecycleOwner) {
        mRecipeData = recipeData;

        mOwnerWeakReference = new WeakReference<>(lifecycleOwner);

        mProfileDataLiveData = new MutableLiveData<>();

        mDisposable = new CompositeDisposable();

        mAnimations = new ArrayList<>();
    }

    @Nullable
    public RecipeData getRecipeData() {
        return mRecipeData;
    }

    public AbsRecipeItem setRecipeData(@Nullable RecipeData recipeData) {
        mRecipeData = recipeData;

        return this;
    }

    public View getMoreBtn() {
        if (mViewHolder == null) throw new NullPointerException("ViewHolder not attached yet!");

        return mViewHolder.more;
    }

    @Nullable
    LifecycleOwner getLifecycleOwner() {
        return mOwnerWeakReference == null ? null : mOwnerWeakReference.get();
    }

    public boolean isReadyToUpdateUi() {
        return mViewHolder != null && mRecipeData != null;
    }

    @NonNull
    @Override
    public AbsRecipeItem.ViewHolder getViewHolder(@NonNull View v) {
        return new AbsRecipeItem.ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.recipe_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_recipe_item;
    }

    @Override
    @CallSuper
    public void bindView(@NonNull AbsRecipeItem.ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);

        mViewHolder = holder;
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);

        unbind();
    }

    public void unbind() {
        mDisposable.clear();

        stopAnimations();
    }

    public void stopAnimations() {
        for (YoYo.YoYoString anim : mAnimations) {
            anim.stop();
        }

        mAnimations.clear();
    }

    protected void bindToRecipe() {
        updateAllUiViews();

        final LifecycleOwner lifecycleOwner = mOwnerWeakReference.get();

        if (lifecycleOwner != null) {
            mProfileDataLiveData.observe(lifecycleOwner, res -> {
                if (mViewHolder != null) {
                    if (res != null && res.status == ParcResourceByParc.Status.SUCCESS) {

                        setLoginVisibility(View.VISIBLE);

                        mViewHolder.userLogin.setText(res.data.login);

                    } else setLoginVisibility(View.GONE);
                }
            });
        }

        loadAuthorData();
    }

    public PopupMenu onPrepareMoreMenu(Context context) {
        if (mViewHolder == null) throw new NullPointerException("ViewHolder not attached yet!");
        if (mRecipeData == null) throw new NullPointerException("RecipeData might not be null! " +
                "Use constructor with recipeData or call setRecipeData before. You can check readiness" +
                " to update ui by isReadyToUpdateUi method");
        PopupMenu morePopupMenu = new PopupMenu(context, mViewHolder.more);

        morePopupMenu.inflate(R.menu.view_recipe_menu);


        boolean isRecipeCreateCurrUser = getRecipeData() != null
                && getRecipeData().authorUId.equals(getUid());

        if (isRecipeCreateCurrUser) {
            morePopupMenu.getMenu().findItem(R.id.act_edit).setVisible(true);
            morePopupMenu.getMenu().findItem(R.id.act_delete).setVisible(true);
        }

        changeSdCardMoreMenu(morePopupMenu, isSavedLoacal);

        return morePopupMenu;
    }

    public void updateAllUiViews() {
        if (mViewHolder == null) throw new NullPointerException("ViewHolder not attached yet!");
        if (mRecipeData == null) throw new NullPointerException("RecipeData might not be null! " +
                "Use constructor with recipeData or call setRecipeData before. You can check readiness" +
                " to update ui by isReadyToUpdateUi method");

        mViewHolder.imageSdStorage.setVisibility(View.INVISIBLE);

        updateUiRecipeDataWithoutFavorite();
        updateImage();
        updateUiFavoriteData(false);

        updateSavedInStorage();
    }

    private void updateUiRecipeDataWithoutFavorite() {
        setName();
        setDescription();
        setTime();
        setLoginVisibility(View.GONE);
    }

    private void setName() {
        assert mViewHolder != null;
        assert mRecipeData != null;

        mViewHolder.name.setText(mRecipeData.overviewData.name);
    }

    private void setDescription() {
        assert mViewHolder != null;
        assert mRecipeData != null;

        String descriptionStr = mRecipeData.overviewData.description;
        if (TextUtils.isEmpty(descriptionStr) || descriptionStr.length() < 90) {
            mViewHolder.description.setVisibility(View.GONE);
        } else {
            mViewHolder.description.setText(mRecipeData.overviewData.description);

            YoYo.YoYoString anim = UiHelper.Other.showFadeAnim(mViewHolder.description, View.VISIBLE);
            addAnim(anim);
        }
    }

    private void addAnim(YoYo.YoYoString anim) {
        if (anim != null) mAnimations.add(anim);
    }

    private void setTime() {
        assert mViewHolder != null;
        assert mRecipeData != null;

        mViewHolder.timeCooking.setText(DateTimeHelper.convertTime(mRecipeData.stepsData.timeMainNum));

        if (mRecipeData.stepsData.timeMainNum == DialogTimePicker.NOT_SELECTED
                || mRecipeData.stepsData.timeMainNum <= 0)
            mViewHolder.imageClock.setVisibility(View.INVISIBLE);
        else mViewHolder.imageClock.setVisibility(View.VISIBLE);
    }

    private void setLoginVisibility(int visibility) {
        assert mViewHolder != null;
        mViewHolder.userLogin.setVisibility(visibility);
    }

    private void updateImage() {
        assert mViewHolder != null;
        assert mRecipeData != null;

        if (mRecipeData.overviewData.mainImagePath == null) {
            setEmptyImage(mViewHolder.image);
        } else {
            GlideImageLoader.getInstance().loadImage(
                    mViewHolder.image.getContext(),
                    mViewHolder.image,
                    new ImageData(mRecipeData.overviewData.mainImagePath, mRecipeData.dateTime)
            );

        }
    }

    private void setEmptyImage(ImageView image) {
        image.setImageResource(R.drawable.ic_gallery_fill_96dp);
    }

    public void updateUiFavoriteData(boolean isToAnimate) {
        if (mViewHolder == null) throw new NullPointerException("ViewHolder not attached yet!");

        if (isToAnimate) {
            animateFavorite();
            animateFavoriteCount();
        } else {
            setFavoriteCount();
            setFavoriteImage();
        }
    }

    private void setFavoriteImage() {
        assert mViewHolder != null;
        assert mRecipeData != null;
        UiHelper.Favorite.setFavoriteImage(mViewHolder.imageFavorite, mRecipeData);
    }

    private void setFavoriteCount() {
        assert mViewHolder != null;
        assert mRecipeData != null;
        mViewHolder.starCount.setText(String.valueOf(mRecipeData.starCount));
    }

    private void animateFavorite() {
        assert mViewHolder != null;
        YoYo.YoYoString anim = UiHelper.Favorite.setFavoriteImageWithAnim(mViewHolder.imageFavorite,
                mRecipeData);
        addAnim(anim);
    }

    private void animateFavoriteCount() {
        assert mViewHolder != null;
        assert mRecipeData != null;
        YoYo.YoYoString anim = UiHelper.Favorite.setFavoriteCountWithAnim(mViewHolder.starCount,
                mRecipeData.starCount);
        addAnim(anim);
    }

    private void updateSavedInStorage() {
        assert mViewHolder != null;
        assert mRecipeData != null;

        String recipeId = mRecipeData.recipeKey;

        Flowable<List<RecipeEntity>> flowable = ((App) mViewHolder.imageSdStorage
                .getContext().getApplicationContext())
                .getDatabase()
                .recipeDao()
                .getFlowableByRecipeKey(recipeId);

        Disposable disposable = flowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recipeEntities -> {
                    YoYo.YoYoString anim = UiHelper.Other.showFadeAnim(mViewHolder.imageSdStorage,
                            recipeEntities.isEmpty() ? View.INVISIBLE : View.VISIBLE);

                    addAnim(anim);

                    isSavedLoacal = !recipeEntities.isEmpty();
                });

        mDisposable.add(disposable);
    }

    private void changeSdCardMoreMenu(PopupMenu morePopupMenu, boolean isSavedLocal) {
        if (isSavedLocal) {
            morePopupMenu.getMenu().findItem(R.id.act_delete_from_db).setVisible(true);
            morePopupMenu.getMenu().findItem(R.id.act_add_to_db).setVisible(false);
        } else {
            morePopupMenu.getMenu().findItem(R.id.act_delete_from_db).setVisible(false);
            morePopupMenu.getMenu().findItem(R.id.act_add_to_db).setVisible(true);
        }
    }

    private void loadAuthorData() {
        assert mViewHolder != null;
        assert mRecipeData != null;
        ProfileRepository.with((Application) mViewHolder.image.getContext().getApplicationContext())
                .setFirebaseId(mRecipeData.authorUId)
                .setFirebaseChild(CHILD_USERS)
                .to(mProfileDataLiveData)
                .build()
                .loadData();

    }

    /*
    Set new data, and return of call onAfterDataChanged (updateUiFavoriteData if need and
                                                            return: see onAfterDataChanged)
    Example to use: when load changed data -> use this method. Then use notifyItemChanged(from adapter)
                                or bindToRecipe(from this) methods if this method return TRUE
     */
    public boolean onUpdateData(RecipeData newData, ActualDataChecker<RecipeData> dataChecker) {
        RecipeData oldData = getRecipeData();

        setRecipeData(newData);

        return onAfterDataChanged(oldData, dataChecker);
    }

    /*
    Return true if need update ui(like notifyItemChanged
    or just call bindToRecipe method) else return false

    !!! And if dataChecker instanceof ActualRecipeDataChecker updateUiFavoriteData if need !!!

     */
    private boolean onAfterDataChanged(RecipeData oldData,
                                       ActualDataChecker<RecipeData> dataChecker) {
        RecipeData newData = getRecipeData();
        boolean isNeedUpdUiAllData = dataChecker.hasChanged(oldData, newData);

        boolean isNeedUpdUiFavorite = false;

        if (dataChecker instanceof ActualRecipeDataChecker) {
            isNeedUpdUiFavorite = ((ActualRecipeDataChecker) dataChecker)
                    .isNeedUpdateFavorite(oldData, newData);
        }

        if (!isNeedUpdUiAllData && isNeedUpdUiFavorite) {

            if (isReadyToUpdateUi())
                updateUiFavoriteData(false);
        }

        return isNeedUpdUiAllData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        protected TextView name;
        @BindView(R.id.tv_description)
        protected TextView description;
        @BindView(R.id.tv_timeCooking)
        protected TextView timeCooking;
        @BindView(R.id.img_image)
        protected ImageView image;
        @BindView(R.id.imgFavorite)
        protected ImageView imageFavorite;
        @BindView(R.id.img_clock)
        protected ImageView imageClock;
        @BindView(R.id.tv_starCount)
        protected TextView starCount;
        @BindView(R.id.tv_userLogin)
        protected TextView userLogin;
        @BindView(R.id.imageView_sdStorage)
        protected ImageView imageSdStorage;
        @BindView(R.id.img_more)
        protected ImageView more;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
