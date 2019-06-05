package com.xando.chefsclub.Recipes.EditRecipe.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xando.chefsclub.BaseFragments.BaseFragmentWithImageChoose;
import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.DataWorkers.BaseData;
import com.xando.chefsclub.Recipes.Data.OverviewData;
import com.xando.chefsclub.Recipes.Data.StepsData;


public abstract class BaseEditRecipeWithKeyFragment extends BaseFragmentWithImageChoose {
    static final String KEY_RECIPE_ID = "recipeId";

    @Nullable
    String recipeId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            recipeId = getArguments().getString(KEY_RECIPE_ID, null);
        }
    }

    interface DataSender<Data extends BaseData> {
        boolean isValidate();

        Data getData();
    }

    @Override
    protected String getParentDirectoryPath() {
        return Constants.Files.getDirectoryForEditRecipeImages(getActivity());
    }

    public interface OverviewDataSender extends DataSender<OverviewData> {

    }

    public interface StepsDataSender extends DataSender<StepsData> {

    }

    interface IsSaveOnLocal {
        boolean isSaveOnLocal();
    }
}
