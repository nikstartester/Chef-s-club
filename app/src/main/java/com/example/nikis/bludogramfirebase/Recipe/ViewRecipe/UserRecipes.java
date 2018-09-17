package com.example.nikis.bludogramfirebase.Recipe.ViewRecipe;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.UserCookBook.PagerAdapterUserCookbook;

public class UserRecipes extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_cookbook, container, false);

        final TabLayout tabLayout = v.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("My Recipes"));
        tabLayout.addTab(tabLayout.newTab().setText("Favorite"));

        final ViewPager viewPager = v.findViewById(R.id.view_pager);
        final PagerAdapterUserCookbook adapter = new PagerAdapterUserCookbook(getChildFragmentManager());
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

    /*@Override
    public Query getQuery(DatabaseReference databaseReference) {
        Toast.makeText(getActivity(), getUid(), Toast.LENGTH_SHORT).show();
        return databaseReference.child("user-recipes").
                child(getUid());
    }*/
}
