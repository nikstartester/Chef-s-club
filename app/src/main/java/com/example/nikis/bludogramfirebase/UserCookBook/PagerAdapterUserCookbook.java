package com.example.nikis.bludogramfirebase.UserCookBook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class PagerAdapterUserCookbook extends FragmentStatePagerAdapter {
    private final int NUMBER_OF_TABS = 3;
    public PagerAdapterUserCookbook(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new AllUserRecipes();
            case 1: return new RecipesCreatedByUser();
            case 2: return new UserFavoriteRecipes();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return NUMBER_OF_TABS;
    }
}
