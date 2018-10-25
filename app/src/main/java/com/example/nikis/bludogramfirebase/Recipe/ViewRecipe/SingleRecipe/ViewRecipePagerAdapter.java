package com.example.nikis.bludogramfirebase.Recipe.ViewRecipe.SingleRecipe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class ViewRecipePagerAdapter extends FragmentStatePagerAdapter {
    private final int NUMBER_OF_TABS = 2;

    public ViewRecipePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new BaseViewRecipeFragment();
            case 1: return new StepsViewRecipeFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return NUMBER_OF_TABS;
    }
}
