package com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.BaseData;
import com.example.nikis.bludogramfirebase.BaseFragments.BaseFragmentWithMatisseGallery;
import com.example.nikis.bludogramfirebase.Recipe.Data.OverviewData;
import com.example.nikis.bludogramfirebase.Recipe.Data.StepsData;


public abstract class BaseEditRecipeFragment extends BaseFragmentWithMatisseGallery {
    protected static final String KEY_RECIPE_ID = "recipeId";

    @Nullable
    protected String recipeId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            recipeId = getArguments().getString(KEY_RECIPE_ID);
        }
    }

    interface DataSender<Data extends BaseData>{
        public boolean isValidate();
        public Data getData();
    }

    public interface OverviewDataSender extends DataSender<OverviewData>{

    }

    public interface StepsDataSender extends DataSender<StepsData>{

    }
}
