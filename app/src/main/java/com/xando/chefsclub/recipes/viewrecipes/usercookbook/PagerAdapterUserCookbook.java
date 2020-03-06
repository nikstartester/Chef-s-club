package com.xando.chefsclub.recipes.viewrecipes.usercookbook;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.xando.chefsclub.helper.FirebaseHelper;
import com.xando.chefsclub.recipes.viewrecipes.UserRecipesList;
import com.xando.chefsclub.recipes.viewrecipes.subsciptionsrecipes.SubscriptionsRecipesFragment;


class PagerAdapterUserCookbook extends FragmentStatePagerAdapter {

    private static final int NUMBER_OF_TABS = 3;

    PagerAdapterUserCookbook(FragmentManager fm) {
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
