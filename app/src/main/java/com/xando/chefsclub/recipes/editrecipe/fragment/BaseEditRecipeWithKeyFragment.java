package com.xando.chefsclub.recipes.editrecipe.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.xando.chefsclub.basescreen.fragment.BaseFragmentWithImageChoose;
import com.xando.chefsclub.constants.Constants;
import com.xando.chefsclub.dataworkers.BaseData;
import com.xando.chefsclub.recipes.data.OverviewData;
import com.xando.chefsclub.recipes.data.StepsData;


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
