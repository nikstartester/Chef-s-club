package com.example.nikis.bludogramfirebase.Recipe.NewRecipe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.NewRicipeFragments.BaseCreateRecipeFragment;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.NewRicipeFragments.StepsCreateRecipeFragment;

public class CreateRecipePagerAdapter extends FragmentStatePagerAdapter {
    private final int NUMBER_OF_TABS = 2;
    public CreateRecipePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new BaseCreateRecipeFragment();
            case 1: return new StepsCreateRecipeFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return NUMBER_OF_TABS;
    }
}
