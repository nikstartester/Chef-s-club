package com.example.nikis.bludogramfirebase.BaseFragments;

import android.support.v4.app.Fragment;

import com.example.nikis.bludogramfirebase.RecipeData.RecipeData;

import java.util.ArrayList;


public abstract class BaseNewRecipeFragment extends Fragment {
    public abstract RecipeData getData();
    public abstract ArrayList<String> getImagesPath();
    public abstract boolean getIsStepsCooking();
}
