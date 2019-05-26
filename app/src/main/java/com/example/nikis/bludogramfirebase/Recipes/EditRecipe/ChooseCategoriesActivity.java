package com.example.nikis.bludogramfirebase.Recipes.EditRecipe;

import com.example.nikis.bludogramfirebase.BaseActivities.BaseChooseCategoriesActivity;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.EditRecipe.RecyclerViewItems.ChipCategoryItem;

import java.util.ArrayList;

public class ChooseCategoriesActivity extends BaseChooseCategoriesActivity {

    @Override
    protected String setInfoText() {
        return getResources().getString(R.string.select_one);
    }

    @Override
    protected void fillAdapters() {
        for (int i = 0; i < listsCategories.size(); i++) {
            for (int j = 0; j < listsCategories.get(i).length; j++) {
                boolean isActive = false;

                String category = listsCategories.get(i)[j];

                if (selectedCategories.get(i) != null && selectedCategories.get(i).equals(category))
                    isActive = true;
                adapters.get(i).add(new ChipCategoryItem(listsCategories.get(i)[j], isActive));
            }
        }
    }

    @Override
    protected void addListeners() {
        for (int i = 0; i < adapters.size(); i++) {
            final int finalI = i;
            adapters.get(i).withOnClickListener((v, adapter, item, position) -> {
                int prevSelected = getActivePosition(finalI);

                if (prevSelected != SELECTED_NONE)
                    if (prevSelected != position) {
                        adapter.getAdapterItem(prevSelected).changeActive();
                        selectedCategories.set(finalI, item.getCategoryText());
                    } else selectedCategories.set(finalI, null);
                else selectedCategories.set(finalI, item.getCategoryText());

                item.changeActive();

                return false;
            });
        }
    }

    @Override
    protected ArrayList<String> getResult() {
        return super.selectedCategories;
    }
}
