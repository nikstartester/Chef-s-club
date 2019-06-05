package com.xando.chefsclub.Recipes.ViewRecipes.UserCookBook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xando.chefsclub.Helpers.FirebaseHelper;
import com.xando.chefsclub.Recipes.ViewRecipes.SubsciptionsRecipes.SubscriptionsRecipesFragment;
import com.xando.chefsclub.Recipes.ViewRecipes.UserRecipesList;


class PagerAdapterUserCookbook extends FragmentStatePagerAdapter {

    private static final int NUMBER_OF_TABS = 3;

    public PagerAdapterUserCookbook(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UserRecipesList.getInstance(FirebaseHelper.getUid(), true);
            case 1:
                return new UserFavoriteRecipesFragment();
            case 2:
                return new SubscriptionsRecipesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUMBER_OF_TABS;
    }
}
