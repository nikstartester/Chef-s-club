package com.xando.chefsclub.compilations.viewcompilations;

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

import butterknife.BindView;
import butterknife.ButterKnife;


public class CompilationsFragment extends Fragment {

    @BindView(R.id.tab_layout)
    protected TabLayout tabLayout;
    @BindView(R.id.view_pager)
    protected ViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_pager, container, false);

        ButterKnife.bind(this, v);

        tabLayout.addTab(tabLayout.newTab().setText("My"));
        tabLayout.addTab(tabLayout.newTab().setText("Favorite"));

        final PagerAdapter adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new UserCompilationsFragment();
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

        return v;
    }
}
