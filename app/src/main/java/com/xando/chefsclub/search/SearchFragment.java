package com.xando.chefsclub.search;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xando.chefsclub.R;
import com.xando.chefsclub.search.core.BaseFilterData;
import com.xando.chefsclub.search.profiles.SearchProfilesFragment;
import com.xando.chefsclub.search.profiles.filter.ProfileFilterData;
import com.xando.chefsclub.search.recipes.SearchRecipesFragment;
import com.xando.chefsclub.search.recipes.filter.RecipeFilterData;


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
                        if (mRecipeFilterData == null) return new SearchRecipesFragment();
                        else return SearchRecipesFragment.getInstance(mRecipeFilterData);

                    case 1:
                        if (mProfileFilterData == null) return new SearchProfilesFragment();
                        else return SearchProfilesFragment.getInstance(mProfileFilterData);

                    default:
                        return new SearchRecipesFragment();
                }
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
}
