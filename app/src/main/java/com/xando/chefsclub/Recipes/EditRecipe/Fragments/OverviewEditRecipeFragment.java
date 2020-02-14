package com.xando.chefsclub.Recipes.EditRecipe.Fragments;

import android.animation.LayoutTransition;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.App;
import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.Images.ImageData.ImageData;
import com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.Data.OverviewData;
import com.xando.chefsclub.Recipes.EditRecipe.ChooseCategoriesActivity;
import com.xando.chefsclub.Recipes.EditRecipe.RecyclerViewItems.ChipCategoryWithRemoveItem;
import com.xando.chefsclub.Recipes.EditRecipe.RecyclerViewItems.ImageAddItem;
import com.xando.chefsclub.Recipes.EditRecipe.RecyclerViewItems.IngredientsAddItem;
import com.xando.chefsclub.Recipes.EditRecipe.RequiredFields.NormalizeRecipeData;
import com.xando.chefsclub.Recipes.ViewModel.RecipeViewModel;
import com.xando.chefsclub.Recipes.db.RecipeEntity;
import com.xando.chefsclub.Settings.SettingsLoacalFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.xando.chefsclub.Recipes.EditRecipe.ChooseCategoriesActivity.SELECTED_ITEMS;

public class OverviewEditRecipeFragment extends BaseEditRecipeWithKeyFragment
        implements View.OnClickListener, BaseEditRecipeWithKeyFragment.OverviewDataSender,
        BaseEditRecipeWithKeyFragment.IsSaveOnLocal {

    private static final String TAG = "OverviewEditRecipeFragm";

    private static final int REQUEST_CODE_CHOOSE_CATEGORIES = 89;
    public static final int MAX_ADAPTER_PHOTOS = 6;

    @BindView(R.id.imgView_main)
    protected ImageView imageView;

    @BindView(R.id.rv_images)
    protected RecyclerView recyclerViewImages;

    @BindView(R.id.rv_ingredients)
    protected RecyclerView recyclerViewIngredients;

    @BindView(R.id.rv_selectedCategories)
    protected RecyclerView recyclerVewCategories;

    @BindView(R.id.edt_description)
    protected EditText edtDescription;

    @BindView(R.id.edt_name)
    protected EditText edtName;

    @BindView(R.id.btn_addIngredient)
    protected Button btnAddIngredients;

    @BindView(R.id.btn_addPictures)
    protected Button btnAddPictures;

    @BindView(R.id.btn_addCategories)
    protected Button btnAddCategories;

    @BindView(R.id.checkBox_isSaveOnLocal)
    protected CheckBox checkBoxIsSaveOnLocal;

    @BindView(R.id.saved_on_device_content)
    protected View savedOnLocalContent;

    private FastItemAdapter<IngredientsAddItem> mIngredientsAdapter;
    private FastItemAdapter<ImageAddItem> mImagesAdapter;
    private FastItemAdapter<ChipCategoryWithRemoveItem> mCategoriesAdapter;

    private boolean isMainPictureClick;

    private boolean isChangeNotMainPhoto;

    private int mPosToChangeImage;

    private OverviewData mOverviewData;

    private RecipeViewModel mRecipeViewModel;

    private long mTime;

    public static Fragment getInstance(@Nullable String recipeId) {

        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECIPE_ID, recipeId);

        Fragment fragment = new OverviewEditRecipeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIngredientsAdapter = new FastItemAdapter<>();
        mImagesAdapter = new FastItemAdapter<>();
        mCategoriesAdapter = new FastItemAdapter<>();

        mRecipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);

        mOverviewData = new OverviewData();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_recipe_owerview, container, false);

        ButterKnife.bind(this, view);

        ((ViewGroup) view.findViewById(R.id.root2)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        unitView();
        setOnClickListeners();

        mRecipeViewModel.getResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                if (resource.status == ParcResourceByParc.Status.SUCCESS) {

                    mOverviewData = resource.data.overviewData;

                    mTime = resource.data.dateTime;

                    setOverviewDataToViews();

                } else if (resource.status == ParcResourceByParc.Status.ERROR) {

                }
            }
        });

        if (savedInstanceState == null) {

            if (recipeId == null) {
                for (int i = 0; i < NormalizeRecipeData.MIN_INGREDIENTS_COUNT; i++) {
                    mIngredientsAdapter.add(new IngredientsAddItem());
                }
            }

        }

        return view;
    }

    private void setOverviewDataToViews() {
        edtName.setText(mOverviewData.name);

        edtDescription.setText(mOverviewData.description);

        setIngredientsToRv();

        setCategoriesToRv();

        setImages();
    }

    private void unitView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerViewImages.setLayoutManager(layoutManager);

        recyclerViewIngredients.setLayoutManager(ChipsLayoutManager
                .newBuilder(getActivity())
                .setMaxViewsInRow(1)
                .build());

        recyclerVewCategories.setLayoutManager(ChipsLayoutManager
                .newBuilder(getActivity())
                .build());

        recyclerVewCategories.setNestedScrollingEnabled(false);
        recyclerViewIngredients.setNestedScrollingEnabled(false);

        recyclerViewIngredients.setAdapter(mIngredientsAdapter);
        recyclerViewImages.setAdapter(mImagesAdapter);
        recyclerVewCategories.setAdapter(mCategoriesAdapter);

        recyclerViewIngredients.setItemAnimator(new DefaultItemAnimator());
        recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
        recyclerVewCategories.setItemAnimator(new DefaultItemAnimator());

        if (recipeId != null) {
            savedOnLocalContent.setVisibility(View.GONE);

            Single<List<RecipeEntity>> single = ((App) getActivity().getApplication())
                    .getDatabase()
                    .recipeDao()
                    .getSingleByRecipeKey(recipeId);

            single
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(recipeEntities -> {
                        if (!recipeEntities.isEmpty())
                            checkBoxIsSaveOnLocal.setChecked(true);
                    });
        } else {
            checkBoxIsSaveOnLocal.setChecked(SettingsLoacalFragment.isSavingNewRecipes(getActivity()));
        }


    }

    private void setOnClickListeners() {
        btnAddIngredients.setOnClickListener(this);
        btnAddPictures.setOnClickListener(this);
        btnAddCategories.setOnClickListener(this);

        imageView.setOnClickListener(this);

        mIngredientsAdapter.withEventHook(new ClickEventHook<IngredientsAddItem>() {

            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof IngredientsAddItem.ViewHolder) {
                    return ((IngredientsAddItem.ViewHolder) viewHolder).itemView
                            .findViewById(R.id.btn_delete_ingredient_from_rv);
                }
                return null;
            }

            @Override
            public void onClick(@NonNull View v, int position, @NonNull FastAdapter<IngredientsAddItem> fastAdapter, @NonNull IngredientsAddItem item) {
                mIngredientsAdapter.getAdapterItem(position).setFocus();

                mIngredientsAdapter.remove(position);
            }
        });

        mCategoriesAdapter.withEventHook(new ClickEventHook<ChipCategoryWithRemoveItem>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ChipCategoryWithRemoveItem.ViewHolder) {
                    return ((ChipCategoryWithRemoveItem.ViewHolder) viewHolder).itemView
                            .findViewById(R.id.img_delete);
                }
                return null;
            }

            @Override
            public void onClick(@NonNull View v, int position, @NonNull FastAdapter<ChipCategoryWithRemoveItem> fastAdapter, @NonNull ChipCategoryWithRemoveItem item) {
                mCategoriesAdapter.remove(position);
                mOverviewData.strCategories.set(item.getCategoryType(), null);
            }
        });

        mImagesAdapter.withEventHook(new ClickEventHook<ImageAddItem>() {
            @Nullable
            @Override
            public List<View> onBindMany(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ImageAddItem.ViewHolder) {
                    List<View> views = new ArrayList<>();
                    views.add(((ImageAddItem.ViewHolder) viewHolder).itemView
                            .findViewById(R.id.img_btn_remove));
                    views.add(((ImageAddItem.ViewHolder) viewHolder).itemView
                            .findViewById(R.id.img_image));

                    return views;
                }
                return super.onBindMany(viewHolder);
            }

            @Override
            public void onClick(@NonNull View v, int position, @NonNull FastAdapter<ImageAddItem> fastAdapter, @NonNull ImageAddItem item) {
                switch (v.getId()) {
                    case R.id.img_btn_remove:
                        removeImage(position);
                        break;
                    case R.id.img_image:
                        isChangeNotMainPhoto = true;

                        mPosToChangeImage = position;

                        imageViewClick(false, true);
                        break;
                }
            }
        });
    }

    private void removeImage(int position) {
        mImagesAdapter.remove(position);

        OverviewEditRecipeFragment.super.
                addToDeleteIfCapture(mOverviewData.imagePathsWithoutMainList.get(position));

        mOverviewData.imagePathsWithoutMainList.remove(position);

        super.deleteOldCaptures();
    }

    private void setImages() {
        setMainImage();

        setRecyclerViewImages();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addIngredient:
                mIngredientsAdapter.add(new IngredientsAddItem(true));
                break;
            case R.id.imgView_main:
                imageViewClick(true, false);
                break;
            case R.id.btn_addPictures:
                imageViewClick(false, false);
                break;
            case R.id.btn_addCategories:
                startActivityForResult(
                        new Intent(getActivity(), ChooseCategoriesActivity.class)
                                .putExtra(SELECTED_ITEMS, (ArrayList<String>) mOverviewData.strCategories),
                        REQUEST_CODE_CHOOSE_CATEGORIES);
                break;
        }
    }

    private void setIngredientsToRv() {
        mIngredientsAdapter.clear();

        for (String ing : mOverviewData.ingredientsList) {
            mIngredientsAdapter.add(new IngredientsAddItem(ing));
        }
    }

    private void setCategoriesToRv() {
        mCategoriesAdapter.clear();

        for (int i = 0; i < mOverviewData.strCategories.size(); i++) {
            String category = mOverviewData.strCategories.get(i);
            if (category != null) {
                mCategoriesAdapter.add(new ChipCategoryWithRemoveItem(category, i));

            }
        }
    }

    private void imageViewClick(boolean isMain, boolean changeAdapterPhoto) {
        isMainPictureClick = isMain;
        isChangeNotMainPhoto = changeAdapterPhoto;

        boolean withDelete = isMain ? isHaveMainPhoto() : changeAdapterPhoto;

        int picAdapterCount = mImagesAdapter.getAdapterItemCount();

        int maxPic = changeAdapterPhoto ? 1 : MAX_ADAPTER_PHOTOS - picAdapterCount;

        if (isMain) {
            maxPic++;
        }

        if (maxPic > 0) {
            super.showChooseDialog(maxPic, withDelete);
        } else {
            String m = "You can only select up to " + MAX_ADAPTER_PHOTOS + " media files";
            Toast.makeText(getActivity(), m, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isHaveMainPhoto() {
        return mOverviewData.mainImagePath != null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE_CATEGORIES && resultCode == RESULT_OK) {
            mOverviewData.strCategories = data.getStringArrayListExtra(SELECTED_ITEMS);

            setCategoriesToRv();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onGalleryFinish(List<Uri> selected) {
        String[] paths = super.convert(selected);

        if (!isChangeNotMainPhoto) {
            setData(paths);

            if (isMainPictureClick) {
                setMainImage();
            }

            setRecyclerViewImages();

            if (!isMainPictureClick) {
                recyclerViewImages.scrollToPosition(mImagesAdapter.getAdapterItemCount() - 1);
            }
        } else {
            mOverviewData.imagePathsWithoutMainList.set(mPosToChangeImage, paths[0]);

            mImagesAdapter.getAdapterItem(mPosToChangeImage).updateImage(new ImageData(paths[0], mTime));
        }

        super.deleteOldCaptures();
    }

    @Override
    protected void onDeleteImage() {
        if (isChangeNotMainPhoto) {
            removeImage(mPosToChangeImage);
        } else if (isMainPictureClick) {
            removeMainImage();
        }
    }

    private void setMainImage() {
        String imagePath = mOverviewData.mainImagePath;

        ImageData imageData = new ImageData(imagePath, mTime);

        if (imagePath == null) {
            showEmptyMainImage();
        } else GlideImageLoader.getInstance()
                .loadImage(getActivity(), imageView, imageData);
    }

    private void showEmptyMainImage() {
        imageView.setImageResource(Constants.ImageConstants.DRAWABLE_ADD_PHOTO_PLACEHOLDER);
    }

    private void setRecyclerViewImages() {
        mImagesAdapter.clear();

        for (String imagePath : mOverviewData.imagePathsWithoutMainList) {
            mImagesAdapter.add(new ImageAddItem(new ImageData(imagePath, mTime)));
        }
    }

    private void removeMainImage() {
        super.addToDeleteIfCapture(mOverviewData.mainImagePath);

        mOverviewData.mainImagePath = null;

        showEmptyMainImage();

        super.deleteOldCaptures();
    }

    private ArrayList<String> getAllIngredients() {
        ArrayList<String> ingredientsArray = new ArrayList<>();
        for (int i = 0; i < mIngredientsAdapter.getItemCount(); i++) {
            ingredientsArray.add(mIngredientsAdapter
                    .getAdapterItem(i)
                    .getIngredient()
                    .replaceAll("\\n+"," "));
        }
        return ingredientsArray;
    }

    @Override
    public boolean isValidate() {
        return true;
    }

    @Override
    public OverviewData getData() {
        mOverviewData.name = edtName.getText().toString();
        mOverviewData.description = edtDescription.getText().toString();
        mOverviewData.ingredientsList = getAllIngredients();

        return mOverviewData;
    }

    private void setData(String[] imagePaths) {
        int startIndex = 0;

        if (isMainPictureClick) {
            super.addToDeleteIfCapture(mOverviewData.mainImagePath);

            mOverviewData.mainImagePath = imagePaths[0];

            startIndex = 1;
        }

        mOverviewData.imagePathsWithoutMainList
                .addAll(Arrays.asList(imagePaths).subList(startIndex, imagePaths.length));

    }

    @Override
    public boolean isSaveOnLocal() {
        return checkBoxIsSaveOnLocal.isChecked();
    }
}
