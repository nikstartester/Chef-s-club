package com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest;

import android.animation.LayoutTransition;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nikis.bludogramfirebase.ChooseCategoriesActivity;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipe.Data.OverviewData;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems.ChipCategoryWithRemoveItem;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems.ImageAddItem;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems.IngredientsAddItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.example.nikis.bludogramfirebase.ChooseCategoriesActivity.SELECTED_ITEMS;
import static com.example.nikis.bludogramfirebase.ChooseCategoriesActivity.SELECTED_NONE;

public class OverviewEditRecipeFragment extends BaseEditRecipeFragment implements View.OnClickListener {

    private static final String KEY_INGREDIENTS = "ingredients";

    public static final int REQUEST_CODE_CHOOSE_CATEGORIES = 89;

    private static final String KEY_OVERVIEW_DATA = "overviewData";

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

    private FastItemAdapter<IngredientsAddItem> mIngredientsAdapter;
    private FastItemAdapter<ImageAddItem> mImagesAdapter;
    private FastItemAdapter<ChipCategoryWithRemoveItem> mCategoriesAdapter;

    private ArrayList<Integer> mSelectedCategories;

    private boolean isMainPictureClick;

    private OverviewData mOverviewData;

    public static Fragment getInstance(@Nullable String recipeId){

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

        if(savedInstanceState != null){
            mOverviewData = savedInstanceState.getParcelable(KEY_OVERVIEW_DATA);
        }else {
            mOverviewData = new OverviewData();
        }
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

        if (savedInstanceState != null){
            ArrayList<String> ingredientsArray = savedInstanceState.getStringArrayList(KEY_INGREDIENTS);
            setIngredientsToRv(ingredientsArray);
            mSelectedCategories = savedInstanceState.getIntegerArrayList(SELECTED_ITEMS);

            setImages();
        }else {
            mSelectedCategories = new ArrayList<>();

            mIngredientsAdapter.add(new IngredientsAddItem());
            mIngredientsAdapter.add(new IngredientsAddItem());
        }

        setCategoriesToRv();

        return view;

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

        recyclerViewIngredients.setAdapter(mIngredientsAdapter);
        recyclerViewImages.setAdapter(mImagesAdapter);
        recyclerVewCategories.setAdapter(mCategoriesAdapter);

        recyclerViewIngredients.setItemAnimator(new DefaultItemAnimator());
        recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
        recyclerVewCategories.setItemAnimator(new DefaultItemAnimator());

    }

    private void setOnClickListeners(){
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
            public void onClick(View v, int position, FastAdapter<IngredientsAddItem> fastAdapter, IngredientsAddItem item) {
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
            public void onClick(View v, int position, FastAdapter<ChipCategoryWithRemoveItem> fastAdapter, ChipCategoryWithRemoveItem item) {
                mCategoriesAdapter.remove(position);
                mSelectedCategories.set(item.getCategoryType(), SELECTED_NONE);
            }
        });

        mImagesAdapter.withEventHook(new ClickEventHook<ImageAddItem>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if(viewHolder instanceof ImageAddItem.ViewHolder)
                    return ((ImageAddItem.ViewHolder)viewHolder).itemView
                            .findViewById(R.id.img_btn_remove);
                else return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<ImageAddItem> fastAdapter, ImageAddItem item) {
                mImagesAdapter.remove(position);

                mOverviewData.imagePathsWithoutMainList.remove(position);
            }
        });
    }

    private void setImages(){
        setMainImage(mOverviewData.mainImagePath);

        List<String> imagePaths = mOverviewData.imagePathsWithoutMainList;
        setRecyclerViewImages(imagePaths.toArray(new String[0]));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_addIngredient:
                mIngredientsAdapter.add(new IngredientsAddItem(true));
                break;
            case R.id.imgView_main:
                imageViewClick(true);
                break;
            case R.id.btn_addPictures:
                imageViewClick(false);
                break;
            case R.id.btn_addCategories:
                startActivityForResult(
                        new Intent(getActivity(), ChooseCategoriesActivity.class)
                                .putExtra(SELECTED_ITEMS, mSelectedCategories),
                        REQUEST_CODE_CHOOSE_CATEGORIES);
        }
    }

    private void setIngredientsToRv(ArrayList<String> ingredientsArray) {
        for (String ing : ingredientsArray){
            mIngredientsAdapter.add(new IngredientsAddItem(ing));
        }
    }

    private void setCategoriesToRv(){
        mCategoriesAdapter.removeItemRange(0, mCategoriesAdapter.getItemCount());
        for(int i = 0; i < mSelectedCategories.size(); i++){
            int pos = mSelectedCategories.get(i);
            if(pos != SELECTED_NONE) {
                String category = "unknown";
                switch (i){
                    case 0:
                        category = getResources().getStringArray(R.array.mealType)[pos];
                        break;
                    case 1:
                        category = getResources().getStringArray(R.array.dishType)[pos];
                        break;
                    case 2:
                        category = getResources().getStringArray(R.array.worldCuisine)[pos];
                        break;
                }
                mCategoriesAdapter.add(new ChipCategoryWithRemoveItem(category, i));
            }
        }
    }

    private void imageViewClick(boolean isMain){
        isMainPictureClick = isMain;

        super.startMatisseGallery(12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_CHOOSE_CATEGORIES && resultCode == RESULT_OK){
            mSelectedCategories = data.getIntegerArrayListExtra(SELECTED_ITEMS);
            setCategoriesToRv();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onGalleryFinish(List<Uri> selected) {
        String[] paths = super.covert(selected);

        if(isMainPictureClick){
            setMainImage(paths[0]);
        }
        setRecyclerViewImages(paths);

        setData(paths);
    }

    private void setMainImage(String imagePath){
        if(imagePath == null){
            imageView.setImageResource(R.drawable.ic_add_a_photo_blue_108dp);
        } else GlideApp.with(this)
                .load(imagePath)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    private void setRecyclerViewImages(String[] imagePaths){
        int startIndex = 0;
        if(isMainPictureClick){
            startIndex = 1;
        }
        for (int i = startIndex; i < imagePaths.length; i++) {
            mImagesAdapter.add(new ImageAddItem(imagePaths[i]));
        }
    }

    private void setData(String[] imagePaths){
        int startIndex = 0;

        if(isMainPictureClick){
            mOverviewData.mainImagePath = imagePaths[0];
            startIndex = 1;
        }

        mOverviewData.imagePathsWithoutMainList
                .addAll(Arrays.asList(imagePaths).subList(startIndex, imagePaths.length));

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_INGREDIENTS, getAllIngredients());
        outState.putIntegerArrayList(SELECTED_ITEMS, mSelectedCategories);
        outState.putParcelable(KEY_OVERVIEW_DATA, mOverviewData);
    }

    public ArrayList<String> getAllIngredients() {
        ArrayList<String> ingredientsArray = new ArrayList<>();
        for(int i = 0; i < mIngredientsAdapter.getItemCount(); i++){
            ingredientsArray.add(mIngredientsAdapter.getAdapterItem(i).getIngredient());
        }
        return ingredientsArray;
    }
}
