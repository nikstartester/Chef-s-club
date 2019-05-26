package com.example.nikis.bludogramfirebase.BaseActivities;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.EditRecipe.RecyclerViewItems.ChipCategoryItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;

public abstract class BaseChooseCategoriesActivity extends AppCompatActivity implements View.OnClickListener {

    protected static final int SELECTED_NONE = -1;
    public static final String SELECTED_ITEMS = "selectedItems";
    public static final String IS_MULTI_SELECT = "isMultiSelect";
    protected ArrayList<String[]> listsCategories;
    protected ArrayList<FastItemAdapter<ChipCategoryItem>> adapters;

    protected ArrayList<String> selectedCategories;

    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_categories);

        ((ViewGroup) findViewById(R.id.root3)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        listsCategories = new ArrayList<>(3);

        info = findViewById(R.id.info);

        info.setText(setInfoText());

        listsCategories.add(getResources().getStringArray(R.array.mealType));
        listsCategories.add(getResources().getStringArray(R.array.dishType));
        listsCategories.add(getResources().getStringArray(R.array.worldCuisine));

        adapters = new ArrayList<>(3);

        for (int i = 0; i < listsCategories.size(); i++) {
            adapters.add(new FastItemAdapter<>());
        }

        RecyclerView rvMealType = findViewById(R.id.rv_categories_mealType);
        RecyclerView rvDishType = findViewById(R.id.rv_categories_dishType);
        RecyclerView rvWorldCuisine = findViewById(R.id.rv_categories_worldCuisine);

        setLayoutManager(rvMealType);
        setLayoutManager(rvDishType);
        setLayoutManager(rvWorldCuisine);

        rvMealType.setAdapter(adapters.get(0));
        rvDishType.setAdapter(adapters.get(1));
        rvWorldCuisine.setAdapter(adapters.get(2));

        if (savedInstanceState == null)
            selectedCategories = getIntent().getStringArrayListExtra(SELECTED_ITEMS);
        else selectedCategories = savedInstanceState.getStringArrayList(SELECTED_ITEMS);


        if (selectedCategories.size() < listsCategories.size())
            fillEmptySelectedItems();

        fillAdapters();

        addListeners();

        Button btn = findViewById(R.id.btn_cancel);
        btn.setOnClickListener(this);
        btn = findViewById(R.id.btn_apply);
        btn.setOnClickListener(this);
    }

    private void setLayoutManager(RecyclerView recyclerView) {
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(this)
                .build();

        recyclerView.setLayoutManager(chipsLayoutManager);
    }

    private void fillEmptySelectedItems() {
        for (int i = 0; i < listsCategories.size(); i++) {
            selectedCategories.add(null);
        }
    }

    protected abstract String setInfoText();

    protected abstract void fillAdapters();

    protected abstract void addListeners();

    protected int getActivePosition(int adapterNum) {
        for (int i = 0; i < adapters.get(adapterNum).getAdapterItems().size(); i++) {
            if (adapters.get(adapterNum).getItem(i).isActive())
                return i;
        }
        return SELECTED_NONE;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(SELECTED_ITEMS, selectedCategories);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_apply:
                Intent intent = new Intent();

                intent.putExtra(SELECTED_ITEMS, getResult());

                setResult(RESULT_OK, intent);

                finish();
                break;
        }
    }

    protected abstract ArrayList<String> getResult();
}
