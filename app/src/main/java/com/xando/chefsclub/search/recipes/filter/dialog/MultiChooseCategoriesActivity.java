package com.xando.chefsclub.search.recipes.filter.dialog;

import com.xando.chefsclub.basescreen.activity.BaseChooseCategoriesActivity;
import com.xando.chefsclub.R;
import com.xando.chefsclub.recipes.editrecipe.recyclerviewitems.ChipCategoryItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class MultiChooseCategoriesActivity extends BaseChooseCategoriesActivity {

    private final LinkedHashMap<String, Boolean> mCategoriesMap = new LinkedHashMap<>();

    @Override
    protected String setInfoText() {
        return getResources().getString(R.string.multi_select);
    }

    @Override
    protected void fillAdapters() {
        for (int i = 0; i < listsCategories.size(); i++) {
            for (int j = 0; j < listsCategories.get(i).length; j++) {
                boolean isActive = false;

                String category = listsCategories.get(i)[j];

                if (selectedCategories.contains(category)) isActive = true;

                mCategoriesMap.put(category, isActive);

                adapters.get(i).add(new ChipCategoryItem(listsCategories.get(i)[j], isActive));
            }
        }
    }

    @Override
    protected void addListeners() {
        for (int i = 0; i < adapters.size(); i++) {
            adapters.get(i).withOnClickListener((v, adapter, item, position) -> {
                mCategoriesMap.put(item.getCategoryText(), !(mCategoriesMap.get(item.getCategoryText())));

                item.changeActive();
                return false;
            });
        }
    }

    @Override
    protected ArrayList<String> getResult() {
        ArrayList<String> result = new ArrayList<>();

        for (Map.Entry<String, Boolean> entry : mCategoriesMap.entrySet()) {
            if (entry.getValue()) result.add(entry.getKey());
        }

        return result;
    }
}
