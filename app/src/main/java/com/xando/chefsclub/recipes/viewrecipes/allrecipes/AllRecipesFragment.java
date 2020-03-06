package com.xando.chefsclub.recipes.viewrecipes.allrecipes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xando.chefsclub.R;
import com.xando.chefsclub.basescreen.fragment.FragmentWithSearchButton;
import com.xando.chefsclub.recipes.viewrecipes.ToSearcher;
import com.xando.chefsclub.search.recipes.filter.RecipeFilterData;

public class AllRecipesFragment extends FragmentWithSearchButton {

    private ViewPager mViewPager;
    private ToSearcher mToSearcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager, container, false);

        final TabLayout tabLayout = v.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("New"));
        tabLayout.addTab(tabLayout.newTab().setText("Best Marks"));

        mViewPager = v.findViewById(R.id.view_pager);
        final PagerAdapter adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new NewestRecipesFragment();
                    case 1:
                        return new BestMarkRecipesFragment();
                    default:
                        return new NewestRecipesFragment();
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
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
        if (context instanceof ToSearcher)
            mToSearcher = (ToSearcher) context;
    }

    @Override
    protected void toSearch() {
        if (mToSearcher != null) {
            mToSearcher.toSearch(ToSearcher.LOOK_FOR_RECIPES, RecipeFilterData.FROM_ALL_RECIPES);
        }
    }
}
