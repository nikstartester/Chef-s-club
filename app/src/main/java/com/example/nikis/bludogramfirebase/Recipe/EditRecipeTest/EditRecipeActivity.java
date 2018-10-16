package com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nikis.bludogramfirebase.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EditRecipeActivity extends AppCompatActivity {

    private static final String EXTRA_RECIPE_ID = "userUid";

    @Nullable
    private String mRecipeId;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.tab_layout)
    protected TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getRecipeId();

        setContentView(R.layout.activity_new_recipe_test);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.edit_recipe_overview)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.edit_recipe_steps_cooking)));

        final ViewPager viewPager = findViewById(R.id.view_pager);

        final FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0: return OverviewEditRecipeFragment.getInstance(mRecipeId);
                    case 1: return StepsEditRecipeFragment.getInstance(mRecipeId);
                    default: return null;
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
    }

    private void getRecipeId(){
        mRecipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);
    }

    public static Intent getIntent(Context context, @Nullable String recipeId){
        Intent intent = new Intent(context, EditRecipeActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);

        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.only_done_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_done){
            //startCreateRecipe();
        }
        return super.onOptionsItemSelected(item);
    }
}
