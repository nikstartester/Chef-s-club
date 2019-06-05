package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Fragments;


import android.animation.LayoutTransition;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.daimajia.androidanimations.library.YoYo;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.xando.chefsclub.App;
import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.DataWorkers.BaseRepository;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.Helpers.DateTimeHelper;
import com.xando.chefsclub.Helpers.FirebaseHelper;
import com.xando.chefsclub.Helpers.Keyboard;
import com.xando.chefsclub.Helpers.NetworkHelper;
import com.xando.chefsclub.Helpers.UiHelper;
import com.xando.chefsclub.Images.ImageData.ImageData;
import com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader;
import com.xando.chefsclub.Images.ViewImages.ViewImagesActivity;
import com.xando.chefsclub.Profiles.ViewModel.ProfileViewModel;
import com.xando.chefsclub.Profiles.ViewProfiles.SingleProfile.ViewProfileActivityTest;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.Data.OverviewData;
import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.ViewModel.RecipeViewModel;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.CommentUploader;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.CommentsListFragment;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.Data.CommentData;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.RecipesCommentsFragment;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.ViewHolder.CommentViewHolder;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Ingredients.IngredientsListFragment;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.ChipCategoryItem;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.ImageViewItem;
import com.xando.chefsclub.Recipes.db.RecipeEntity;
import com.xando.chefsclub.Recipes.db.RecipeToFavoriteEntity;
import com.xando.chefsclub.ShoppingList.db.IngredientEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.xando.chefsclub.Helpers.UiHelper.DURATION_NORMAL;


public class OverviewViewRecipeFragment extends BaseFragmentWithRecipeKey
        implements View.OnClickListener, CommentsListFragment.OnUserAddedComment,
        CommentViewHolder.OnReplyComment {
    private static final String TAG = "OverviewEditRecipeFragm";
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final List<YoYo.YoYoString> mAnimations = new ArrayList<>();

    @BindView(R.id.imgView_main)
    protected ImageView imageView;
    @BindView(R.id.rv_images)
    protected RecyclerView recyclerViewImages;
    @BindView(R.id.rv_selectedCategories)
    protected RecyclerView recyclerVewCategories;
    @BindView(R.id.tv_description)
    protected TextView tvDescription;
    @BindView(R.id.tv_name)
    protected TextView tvName;
    @BindView(R.id.checkBox_isSaveOnLocal)
    protected CheckBox checkBoxIsSaveOnLocal;
    @BindView(R.id.imageView_sdStorage)
    protected ImageView imageSdStorage;
    @BindView(R.id.login_profile)
    protected TextView authorLogin;
    @BindView(R.id.image_profile)
    protected ImageView authorImage;
    @BindView(R.id.imgFavorite)
    protected ImageView imageFavorite;
    @BindView(R.id.tv_starCount)
    protected TextView tvStartCount;
    @BindView(R.id.comment_text)
    protected EditText newCommentText;
    @BindView(R.id.root1)
    protected NestedScrollView scrollView;
    @BindView(R.id.comment_send)
    protected ImageButton sendComment;
    @BindView(R.id.comment_progress)
    protected ProgressBar progressComment;
    @BindView(R.id.tv_time)
    protected TextView tvTime;

    @BindView(R.id.categories_content)
    protected View categoriesContent;
    @BindView(R.id.description_content)
    protected View descriptionContent;
    @BindView(R.id.add_comment_content)
    protected View addCommentContent;
    @BindView(R.id.ingredients_content)
    protected View ingredientsContent;
    @BindView(R.id.comments_content)
    protected View commentsContent;

    private FastItemAdapter<ImageViewItem> mImagesAdapter;
    private FastItemAdapter<ChipCategoryItem> mCategoriesAdapter;

    private OverviewData mOverviewData;

    private RecipeViewModel mRecipeViewModel;
    private ProfileViewModel mProfileViewModel;

    private long mTime = Constants.ImageConstants.DEF_TIME;

    private String mAuthorId;
    private boolean isCommentInProgress;

    public static Fragment getInstance(@Nullable final String recipeId) {

        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECIPE_ID, recipeId);

        Fragment fragment = new OverviewViewRecipeFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImagesAdapter = new FastItemAdapter<>();
        mCategoriesAdapter = new FastItemAdapter<>();

        mRecipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        mOverviewData = new OverviewData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_recipe_owerview, container, false);

        ButterKnife.bind(this, view);

        ((ViewGroup) view.findViewById(R.id.root2)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        unitView();

        if (savedInstanceState == null) {
            addFragments();
        }

        setOnClickListeners();

        mRecipeViewModel.getResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                if (resource.status == ParcResourceByParc.Status.SUCCESS) {
                    onSuccessLoaded(resource);

                } else if (resource.status == ParcResourceByParc.Status.ERROR) {
                    onErrorLoaded(resource);
                }
            }
        });

        mProfileViewModel.getResourceLiveData().observe(this, res -> {
            if (res != null && res.status == ParcResourceByParc.Status.SUCCESS) {

                authorLogin.setText(res.data.login);

                YoYo.YoYoString anim = UiHelper.Other.showFadeAnim(authorLogin, View.VISIBLE, DURATION_NORMAL);
                addAnim(anim);

                if (res.data.imageURL != null) {
                    ImageData imageData = new ImageData(res.data.imageURL, res.data.lastTimeUpdate);

                    GlideImageLoader.getInstance().loadSmallCircularImage(getActivity(),
                            authorImage,
                            imageData);
                } else authorImage.setImageResource(R.drawable.ic_account_circle_elements_48dp);
            }
        });

        return view;

    }

    private void onSuccessLoaded(ParcResourceByParc<RecipeData> resource) {
        assert resource.data != null;

        if (mTime == Constants.ImageConstants.DEF_TIME) {
            setDateAndUpdViews(resource);

            if (mProfileViewModel.getResourceLiveData().getValue() == null) {
                mProfileViewModel.loadDataWithoutSaver(resource.data.authorUId);
            }

        }
        if (resource.from == ParcResourceByParc.From.SERVER) {

            sendComment.setEnabled(true);
        }
    }

    private void setDateAndUpdViews(ParcResourceByParc<RecipeData> resource) {
        assert resource.data != null;

        mOverviewData = resource.data.overviewData;

        mAuthorId = resource.data.authorUId;

        mTime = resource.data.dateTime;

        setOverviewDataToViews();

        setStars(resource.data);

        setIngredients(mOverviewData.ingredientsList);

        commentsContent.setVisibility(View.VISIBLE);

        addCommentContent.setVisibility(View.VISIBLE);
    }

    private void onErrorLoaded(ParcResourceByParc<RecipeData> resource) {
        if (resource.exception instanceof BaseRepository.NothingFoundFromServerException) {
            sendComment.setEnabled(false);
        }
    }

    private void addFragments() {
        addIngredientsFragment();

        addCommentFragment();
    }

    private void addIngredientsFragment() {
        FragmentManager fm = getChildFragmentManager();
        Fragment ingredientsFragment = fm.findFragmentById(R.id.container_ingredients);

        if (ingredientsFragment == null) {
            ingredientsFragment = IngredientsListFragment.getInstance(recipeId);
        }
        fm.beginTransaction().add(R.id.container_ingredients, ingredientsFragment).commit();

    }

    private void addCommentFragment() {
        if (recipeId != null) {
            FragmentManager fm = getChildFragmentManager();
            Fragment commentsFragment = fm.findFragmentById(R.id.container_comments);

            if (commentsFragment == null) {
                commentsFragment = RecipesCommentsFragment.getInstance(recipeId);
            }
            fm.beginTransaction().add(R.id.container_comments, commentsFragment).commit();

        }
    }

    private void unitView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerViewImages.setLayoutManager(layoutManager);

        recyclerVewCategories.setLayoutManager(ChipsLayoutManager
                .newBuilder(getActivity())
                .build());

        recyclerViewImages.setNestedScrollingEnabled(false);

        recyclerViewImages.setAdapter(mImagesAdapter);
        recyclerVewCategories.setAdapter(mCategoriesAdapter);

        recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
        recyclerVewCategories.setItemAnimator(new DefaultItemAnimator());

        recyclerVewCategories.setNestedScrollingEnabled(false);

        if (recipeId != null) {

            Flowable<List<RecipeEntity>> single = ((App) getActivity().getApplication())
                    .getDatabase()
                    .recipeDao()
                    .getFlowableByRecipeKey(recipeId);

            Disposable disposable = single.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(recipeEntities -> {
                        //
                        // it calls twice if use sync when open recipe(see BaseRepository class)
                        // because use flowable
                        //
                        checkBoxIsSaveOnLocal.setChecked(!recipeEntities.isEmpty());

                        YoYo.YoYoString anim = UiHelper.Other.showFadeAnim(imageSdStorage,
                                recipeEntities.isEmpty() ? View.INVISIBLE : View.VISIBLE);
                        addAnim(anim);
                    });

            mDisposable.add(disposable);
        }

    }

    private void setOnClickListeners() {

        imageView.setOnClickListener(this);

        authorImage.setOnClickListener(this);
        authorLogin.setOnClickListener(this);

        imageFavorite.setOnClickListener(this);

        mImagesAdapter.withOnClickListener((v, adapter, item, position) -> {
            /*
            position + 1 because have main image(pos = 0)
             */
            imageViewClick(false, position + 1);

            return true;
        });
    }

    private void setOverviewDataToViews() {
        tvName.setText(mOverviewData.name);

        tvTime.setText(DateTimeHelper.simpleTransform(mTime));

        setCategoriesToRv();

        setDescription();

        setImages();
    }

    private void setDescription() {
        String description = mOverviewData.description;
        if (description == null || TextUtils.isEmpty(description)) {
            descriptionContent.setVisibility(View.GONE);
        } else {
            descriptionContent.setVisibility(View.VISIBLE);

            tvDescription.setText(mOverviewData.description);

            YoYo.YoYoString anim = UiHelper.Other.showFadeAnim(tvDescription, View.VISIBLE);

            addAnim(anim);

        }
    }

    private void addAnim(YoYo.YoYoString anim) {
        if (anim != null) mAnimations.add(anim);
    }

    private void setImages() {
        setMainImage();

        setRecyclerViewImages();
    }

    private void setMainImage() {
        String imagePath = mOverviewData.mainImagePath;

        if (imagePath == null) {
            setEmptyImage();
        } else {
            ImageData imageData = new ImageData(imagePath, mTime);

            GlideImageLoader.getInstance()
                    .loadImage(getActivity(), imageView, imageData);
        }

    }

    private void setEmptyImage() {
        imageView.setImageResource(R.drawable.ic_gallery_fill_96dp);
    }

    private void setRecyclerViewImages() {
        mImagesAdapter.clear();

        for (String imagePath : mOverviewData.imagePathsWithoutMainList) {
            mImagesAdapter.add(new ImageViewItem(new ImageData(imagePath, mTime)));
        }
    }

    private void setIngredients(List<String> ingredients) {
        ingredientsContent.setVisibility(View.VISIBLE);

        IngredientsListFragment ingredientsFragment = (IngredientsListFragment) getChildFragmentManager()
                .findFragmentById(R.id.container_ingredients);

        if (ingredientsFragment != null) {
            ingredientsFragment.refresh(getIngredientsEntity(ingredients));
        }

    }

    private List<IngredientEntity> getIngredientsEntity(List<String> ingrStrList) {
        List<IngredientEntity> entities = new ArrayList<>();
        for (String ingrStr : ingrStrList) {
            entities.add(new IngredientEntity(recipeId, mOverviewData.name, ingrStr, mTime));
        }
        return entities;
    }

    private void setStars(@NonNull RecipeData data) {
        tvStartCount.setText(String.valueOf(data.starCount));
        UiHelper.Favorite.setFavoriteImage(imageFavorite, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDisposable.dispose();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopAnimations();
    }

    public void stopAnimations() {
        for (YoYo.YoYoString anim : mAnimations) {
            anim.stop();
        }

        mAnimations.clear();
    }

    @Override
    public void onUserAddedComment() {
        if (isCommentInProgress) {

            isCommentInProgress = false;

            newCommentText.setText(null);

            Keyboard.hideKeyboardFrom(getContext(), newCommentText);

            //scrollView.fullScroll(View.FOCUS_DOWN);

            hideCommentProgress();
        }
    }

    @Override
    public void onReplyComment(final CommentData commentData) {
        newCommentText.setText(commentData.authorLogin + ", ");
        newCommentText.setSelection(newCommentText.getText().length());

        //scrollView.fullScroll(View.FOCUS_DOWN);

        newCommentText.requestFocus();

        Keyboard.showKeyboardFrom(getContext(), newCommentText);
    }


    @OnClick(R.id.comment_send)
    protected void sendNewComment() {
        if (NetworkHelper.isConnected(getActivity())) {
            String commentText = newCommentText.getText().toString();
            if (!TextUtils.isEmpty(commentText) && commentText.charAt(0) != ' ') {
                startSendComment();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void startSendComment() {
        isCommentInProgress = true;

        showCommentProgress();

        String commentText = newCommentText.getText().toString();
        CommentData data = new CommentData(commentText, FirebaseHelper.getUid(), recipeId);

        new CommentUploader().start(data, resource -> {
            if (resource.status == ParcResourceByParc.Status.SUCCESS) {
                onUserAddedComment();
            } else if (resource.status == ParcResourceByParc.Status.ERROR) {
                if (resource.exception instanceof BaseRepository.NothingFoundFromServerException) {

                    sendComment.setEnabled(false);

                    Toast.makeText(getActivity(), getString(R.string.impossible_actions_recipe_deleted),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), resource.exception.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                onUserAddedComment();
            }
        });
    }

    private void showCommentProgress() {
        sendComment.setVisibility(View.INVISIBLE);
        progressComment.setVisibility(View.VISIBLE);
    }

    private void hideCommentProgress() {
        progressComment.setVisibility(View.INVISIBLE);
        sendComment.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgView_main:
                imageViewClick(true);
                break;
            case R.id.image_profile:
                startViewProfile();
                break;
            case R.id.login_profile:
                startViewProfile();
                break;
            case R.id.imgFavorite:
                //TODO change this when will use new method to get author data
                if (recipeId != null && mAuthorId != null) {

                    App app = (App) getActivity().getApplication();
                    FirebaseHelper.Favorite.updateFavorite(app, new RecipeToFavoriteEntity(recipeId, mAuthorId));

                    RecipeData recipeData = mRecipeViewModel.getResourceLiveData().getValue().data;

                    FirebaseHelper.Favorite.updateDBAfterFavoriteChange(app,
                            FirebaseHelper.Favorite.updateRecipeDataWithFavoriteChange(recipeData));

                    UiHelper.Favorite.setFavoriteImageWithAnim(imageFavorite, recipeData);

                    UiHelper.Favorite.setFavoriteCountWithAnim(tvStartCount, recipeData.starCount);
                }

                break;
        }
    }

    private void startViewProfile() {
        if (mAuthorId != null)
            startActivity(ViewProfileActivityTest.getIntent(getActivity(), mAuthorId));
    }

    private void setCategoriesToRv() {
        mCategoriesAdapter.clear();

        List<String> categories = new ArrayList<>();

        for (String category : mOverviewData.strCategories) {
            if (category != null && !TextUtils.isEmpty(category)) {
                categories.add(category);
            }
        }

        if (categories.size() > 0) {
            categoriesContent.setVisibility(View.VISIBLE);

            for (String category : categories) {
                mCategoriesAdapter.add(new ChipCategoryItem(category));
            }
        } else {
            categoriesContent.setVisibility(View.GONE);
        }

    }

    private void imageViewClick(boolean isMain) {
        imageViewClick(isMain, -1);
    }

    private void imageViewClick(boolean isMain, int pos) {
        ArrayList<ImageData> dataList = new ArrayList<>();

        for (int i = 0; i < mOverviewData.allImagePathList.size(); i++) {
            ImageData imageData = null;

            if (i != 0 || mOverviewData.allImagePathList.get(i) != null) {
                imageData = new ImageData(mOverviewData.allImagePathList.get(i), mTime);
            } else pos--;

            if (imageData != null) {
                dataList.add(imageData);
            }
        }
        if (!dataList.isEmpty()) {
            startActivity(ViewImagesActivity.getIntent(getActivity(), dataList, isMain ? 0 : pos));
        }
    }
}
