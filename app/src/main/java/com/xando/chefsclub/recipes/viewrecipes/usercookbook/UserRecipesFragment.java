package com.xando.chefsclub.recipes.viewrecipes.usercookbook;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xando.chefsclub.basescreen.fragment.FragmentWithSearchButton;
import com.xando.chefsclub.R;
import com.xando.chefsclub.recipes.viewrecipes.ToSearcher;
import com.xando.chefsclub.search.recipes.filter.RecipeFilterData;

public class UserRecipesFragment extends FragmentWithSearchButton {

    private ViewPager mViewPager;
    private ToSearcher mToSearcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager, container, false);

        final TabLayout tabLayout = v.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("My Recipes"));
        tabLayout.addTab(tabLayout.newTab().setText("Favorite"));
        tabLayout.addTab(tabLayout.newTab().setText("Subscriptions"));

        mViewPager = v.findViewById(R.id.view_pager);
        final PagerAdapterUserCookbook adapter = new PagerAdapterUserCookbook(getChildFragmentManager());
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ToSearcher) {
            mToSearcher = (ToSearcher) context;
        }
    }

    @Override
    protected void toSearch() {
        if (mToSearcher != null) {
            int toSearchFrom = -1;
            switch (mViewPager.getCurrentItem()) {
                case 0:
                    toSearchFrom = RecipeFilterData.FROM_MY_RECIPES;
                    break;
                case 1:
                    toSearchFrom = RecipeFilterData.FROM_FAVORITE;
                    break;
                case 2:
                    toSearchFrom = RecipeFilterData.FROM_SUBSCRIPTIONS;
                    break;
            }
            if (toSearchFrom != -1) mToSearcher.toSearch(ToSearcher.LOOK_FOR_RECIPES, toSearchFrom);
        }
    }

    /*@Override
    public Query getQuery(DatabaseReference databaseReference) {
        Toast.makeText(getActivity(), getFireBaseAuthUid(), Toast.LENGTH_SHORT).show();
        return databaseReference.child("user-recipes").
                child(getFireBaseAuthUid());
    }*/
}
