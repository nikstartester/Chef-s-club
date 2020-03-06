package com.xando.chefsclub.profiles.viewprofiles.single;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.xando.chefsclub.R;
import com.xando.chefsclub.ScrollChangingViewPager;
import com.xando.chefsclub.compilations.viewcompilations.UserCompilationsFragment;
import com.xando.chefsclub.recipes.viewrecipes.UserRecipesList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProfileDataViewPagerFragment extends Fragment {

    private static final String KEY_USER_ID = "keyId";

    @BindView(R.id.tab_layout)
    protected TabLayout tabLayout;
    @BindView(R.id.view_pager)
    protected ScrollChangingViewPager viewPager;

    private String mUserId;

    public static Fragment getInstance(String userId) {
        Fragment fragment = new ProfileDataViewPagerFragment();

        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userId);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserId = getArguments().getString(KEY_USER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager, container, false);

        ButterKnife.bind(this, v);

        tabLayout.addTab(tabLayout.newTab().setText("Recipes"));
        tabLayout.addTab(tabLayout.newTab().setText("Compilations"));

        final PagerAdapter adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return UserRecipesList.getInstance(mUserId, true);
                    case 1:
                        return new UserCompilationsFragment();
                    default:
                        return new UserCompilationsFragment();
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.setOnTouchListener((v1, event) -> true);

        return v;
    }
}
