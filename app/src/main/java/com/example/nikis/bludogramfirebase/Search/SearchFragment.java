package com.example.nikis.bludogramfirebase.Search;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Search.Core.BaseFilterData;
import com.example.nikis.bludogramfirebase.Search.Core.Filter;
import com.example.nikis.bludogramfirebase.Search.Profiles.Filter.ProfileFilterData;
import com.example.nikis.bludogramfirebase.Search.Profiles.SearchProfilesFragment;
import com.example.nikis.bludogramfirebase.Search.Recipes.Filter.RecipeFilterData;
import com.example.nikis.bludogramfirebase.Search.Recipes.SearchRecipesFragmentTest;


public class SearchFragment extends Fragment {
    private static final String KEY_FILTER_DATA = "keyFilterData";

    private RecipeFilterData mRecipeFilterData;
    private ProfileFilterData mProfileFilterData;

    private int mPosToView;

    private FragmentStatePagerAdapter mAdapter;
    private ViewPager mViewPager;

    public static Fragment getInstance(BaseFilterData filterData) {
        Fragment fragment = new SearchFragment();

        Bundle bundle = new Bundle();

        bundle.putParcelable(KEY_FILTER_DATA, filterData);

        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Parcelable arg = getArguments().getParcelable(KEY_FILTER_DATA);

            if (arg instanceof RecipeFilterData) {
                mRecipeFilterData = (RecipeFilterData) arg;
                mPosToView = 0;
            } else if (arg instanceof ProfileFilterData) {
                mProfileFilterData = (ProfileFilterData) arg;
                mPosToView = 1;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager, container, false);

        final TabLayout tabLayout = v.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Recipes"));
        tabLayout.addTab(tabLayout.newTab().setText("People"));

        mViewPager = v.findViewById(R.id.view_pager);
        mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        if (mRecipeFilterData == null) return new SearchRecipesFragmentTest();
                        else return SearchRecipesFragmentTest.getInstance(mRecipeFilterData);

                    case 1:
                        if (mProfileFilterData == null) return new SearchProfilesFragment();
                        else return SearchProfilesFragment.getInstance(mProfileFilterData);

                    default:
                        return new SearchRecipesFragmentTest();
                }
                /*if(mRecipeFilterData == null) return new SearchRecipesFragment();
                else return SearchRecipesFragment.getInstance(mRecipeFilterData);*/
            }
        };
        mViewPager.setAdapter(mAdapter);

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

        mViewPager.setCurrentItem(mPosToView);

        return v;
    }

    public void searchRecipes(RecipeFilterData recipeFilterData) {
        mViewPager.setCurrentItem(0);
        Fragment fragment = mAdapter.getItem(0);

        if (fragment instanceof Filter) {
            ((Filter) fragment).setFilter(recipeFilterData);
        }
    }
}
