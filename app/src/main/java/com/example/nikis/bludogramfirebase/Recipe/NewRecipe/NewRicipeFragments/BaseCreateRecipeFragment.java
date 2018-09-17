package com.example.nikis.bludogramfirebase.Recipe.NewRecipe.NewRicipeFragments;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.example.nikis.bludogramfirebase.BaseFragments.BaseFragmentWithImageClick;
import com.example.nikis.bludogramfirebase.ChooseCategoriesActivity;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.NewRecipeTestActivity;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems.ChipCategoryWithRemoveItem;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems.ImageAddItem;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems.IngredientsAddItem;
import com.example.nikis.bludogramfirebase.RecipeData.RecipeData;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.nikis.bludogramfirebase.ChooseCategoriesActivity.SELECTED_ITEMS;
import static com.example.nikis.bludogramfirebase.ChooseCategoriesActivity.SELECTED_NONE;

public class BaseCreateRecipeFragment extends BaseFragmentWithImageClick implements View.OnClickListener, NewRecipeTestActivity.DataSender {
    private static final String KEY_INGREDIENTS = "ingr";
    public static final String SYMBOL = "(&&)";
    public static final int REQUEST_CODE_CHOOSE_CATEGORIES = 89;
    private RecyclerView recyclerViewImages;

    protected FastItemAdapter<IngredientsAddItem> ingredientsAdapter;
    protected FastItemAdapter<ImageAddItem> imagesAdapter;
    protected FastItemAdapter<ChipCategoryWithRemoveItem> adapterCategories;


    protected ImageView imageView;
    protected ProgressBar progressBar;

    protected EditText edtDescription;
    protected EditText edtName;

    ArrayList<Integer> selectedCategories;

    public BaseCreateRecipeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_recipe_base, container, false);

        ((ViewGroup) view.findViewById(R.id.root2)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        imageView = view.findViewById(R.id.imgView_main);
        progressBar = view.findViewById(R.id.progressBar);

        edtDescription = view.findViewById(R.id.edt_description);
        edtName = view.findViewById(R.id.edt_name);

        RecyclerView recyclerViewIngredients = view.findViewById(R.id.rv_ingredients);
        recyclerViewImages = view.findViewById(R.id.rv_images);
        RecyclerView rvCategories = view.findViewById(R.id.rv_selectedCategories);

        ingredientsAdapter = new FastItemAdapter<>();
        imagesAdapter = new FastItemAdapter<>();
        adapterCategories = new FastItemAdapter<>();

        recyclerViewIngredients.setLayoutManager(ChipsLayoutManager
                .newBuilder(getActivity())
                .setMaxViewsInRow(1)
                .build());

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewImages.setLayoutManager(layoutManager1);

        rvCategories.setLayoutManager(ChipsLayoutManager
                .newBuilder(getActivity())
                .build()
        );

        recyclerViewIngredients.setAdapter(ingredientsAdapter);
        recyclerViewImages.setAdapter(imagesAdapter);
        rvCategories.setAdapter(adapterCategories);

        recyclerViewIngredients.setItemAnimator(new DefaultItemAnimator());
        recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
        rvCategories.setItemAnimator(new DefaultItemAnimator());

        if (savedInstanceState != null){
            super.setImagesWithSavedState(imageView, recyclerViewImages);
            ArrayList<String> ingredientsArray = savedInstanceState.getStringArrayList(KEY_INGREDIENTS);
            setIngredientsToRv(ingredientsArray);
            selectedCategories = savedInstanceState.getIntegerArrayList(SELECTED_ITEMS);
        }else {
            selectedCategories = new ArrayList<>();
            ingredientsAdapter.add(new IngredientsAddItem());
            ingredientsAdapter.add(new IngredientsAddItem());
        }

        setCategoriesToRv();

        Button btnAddIngr = view.findViewById(R.id.btn_addIngredient);
        btnAddIngr.setOnClickListener(this);
        Button btnAddPict = view.findViewById(R.id.btn_addPictures);
        btnAddPict.setOnClickListener(this);
        Button btnChooseCategories = view.findViewById(R.id.btn_addCategories);
        btnChooseCategories.setOnClickListener(this);

        imageView.setOnClickListener(this);

        ingredientsAdapter.withEventHook(new ClickEventHook<IngredientsAddItem>() {

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
                ingredientsAdapter.getAdapterItem(position).setFocus();

                ingredientsAdapter.remove(position);
            }
        });

        adapterCategories.withEventHook(new ClickEventHook<ChipCategoryWithRemoveItem>() {
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
                adapterCategories.remove(position);
                selectedCategories.set(item.getCategoryType(), SELECTED_NONE);
            }
        });
        return view;
    }

    private void setIngredientsToRv(ArrayList<String> ingredientsArray) {
        for (String ing : ingredientsArray){
            ingredientsAdapter.add(new IngredientsAddItem(ing));
        }
    }

    private void setCategoriesToRv(){
        adapterCategories.removeItemRange(0, adapterCategories.getItemCount());
        for(int i = 0; i < selectedCategories.size(); i++){
            int pos = selectedCategories.get(i);
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
                adapterCategories.add(new ChipCategoryWithRemoveItem(category, i));
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_addIngredient:
                ingredientsAdapter.add(new IngredientsAddItem(true));
                break;
            case R.id.imgView_main:
                super.imageViewClick(imageView, recyclerViewImages);
                break;
            case R.id.btn_addPictures:
                super.imageViewClick(recyclerViewImages);
                break;
            case R.id.btn_addCategories:
                startActivityForResult(
                        new Intent(getActivity(), ChooseCategoriesActivity.class)
                                .putExtra(SELECTED_ITEMS, selectedCategories),
                        REQUEST_CODE_CHOOSE_CATEGORIES);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_CHOOSE_CATEGORIES && resultCode == RESULT_OK){
            selectedCategories = data.getIntegerArrayListExtra(SELECTED_ITEMS);
            setCategoriesToRv();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_INGREDIENTS, getAllIngredients());
        outState.putIntegerArrayList(SELECTED_ITEMS, selectedCategories);
    }

    public ArrayList<String> getAllIngredients() {
        ArrayList<String> ingredientsArray = new ArrayList<>();
        for(int i = 0; i < ingredientsAdapter.getItemCount(); i++){
            ingredientsArray.add(ingredientsAdapter.getAdapterItem(i).getIngredient());
        }
        return ingredientsArray;
    }


    public RecipeData getBaseRecipeData(){
        ArrayList<String> allIngredients = getAllIngredients();
        String name = edtName.getText().toString();
        String descr = edtDescription.getText().toString();
        return new RecipeData(name, descr, allIngredients);
    }
    public static String stringArrayToString(ArrayList<String> stringArrayList){
        StringBuilder allStr = new StringBuilder();
        for(String string : stringArrayList){
            allStr.append(string);
            allStr.append(SYMBOL);
        }
        return allStr.toString();
    }

    @Override
    public RecipeData getData() {
        return getBaseRecipeData();
    }

    @Override
    public ArrayList<String> getImagesPath() {
        return super.getImagesPath();
    }

    @Override
    public boolean getIsStepsCooking() {
        return false;
    }

    @Override
    public boolean isValidateForm() {
        boolean isValidate = true;
        if(!edtName.getText().toString().equals(""))
        {
            edtName.setError(null);
        }
        else {
            isValidate = false;
            edtName.setError("Required!");
        }
        if(!edtDescription.getText().toString().equals("")){
            edtDescription.setError(null);
        }else {
            isValidate = false;
            edtDescription.setError("Required!");
        }
        return isValidate;
    }
}
