package com.example.nikis.bludogramfirebase;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems.ChipCategoryItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;

public class ChooseCategoriesActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int SELECTED_NONE = -1;
    public static final String SELECTED_ITEMS = "selectedItems";
    ArrayList<String[]> listsCategories;
    ArrayList<FastItemAdapter<ChipCategoryItem>> adapters;

    ArrayList<Integer> selectedCategories;

    public ChooseCategoriesActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_categories);

        ((ViewGroup) findViewById(R.id.root3)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        listsCategories = new ArrayList<>(3);

        listsCategories.add(getResources().getStringArray(R.array.mealType));
        listsCategories.add(getResources().getStringArray(R.array.dishType));
        listsCategories.add(getResources().getStringArray(R.array.worldCuisine));

        adapters = new ArrayList<>(3);

        for(int i = 0; i < listsCategories.size(); i++){
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

        if(savedInstanceState == null)
            selectedCategories = getIntent().getIntegerArrayListExtra(SELECTED_ITEMS);
        else selectedCategories = savedInstanceState.getIntegerArrayList(SELECTED_ITEMS);

        if(selectedCategories.size() < listsCategories.size())
            fillEmptySelectedItems();

        fillAdapters();

        addListeners();

        Button btn = findViewById(R.id.btn_cancel);
        btn.setOnClickListener(this);
        btn = findViewById(R.id.btn_apply);
        btn.setOnClickListener(this);
    }

    private void setLayoutManager(RecyclerView recyclerView){
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(this)
                .build();

        recyclerView.setLayoutManager(chipsLayoutManager);
    }

    private void fillEmptySelectedItems(){
        for(int i = 0; i < listsCategories.size(); i++){
            selectedCategories.add(SELECTED_NONE);
        }
    }

    private void fillAdapters(){
        for(int i = 0; i < listsCategories.size(); i++){
            for (int j = 0; j < listsCategories.get(i).length; j++){
                boolean isActive = false;
                if(selectedCategories.get(i) == j)
                    isActive = true;
                adapters.get(i).add(new ChipCategoryItem(listsCategories.get(i)[j], isActive));
            }
        }
    }

    private void addListeners(){
        for(int i = 0; i < adapters.size(); i++){
            final int finalI = i;
            adapters.get(i).withOnClickListener((v, adapter, item, position) -> {
                item.changeActive();

                int prevSelected = selectedCategories.get(finalI);

                if(prevSelected != SELECTED_NONE)
                    if(prevSelected != position){
                        adapter.getAdapterItem(prevSelected).changeActive();
                        selectedCategories.set(finalI, position);
                    }else selectedCategories.set(finalI, SELECTED_NONE);
                else selectedCategories.set(finalI, position);

                return false;
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(SELECTED_ITEMS, selectedCategories);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_apply:
                Intent intent = new Intent();
                intent.putExtra(SELECTED_ITEMS, selectedCategories);

                setResult(RESULT_OK, intent);

                finish();
                break;
        }
    }
}
