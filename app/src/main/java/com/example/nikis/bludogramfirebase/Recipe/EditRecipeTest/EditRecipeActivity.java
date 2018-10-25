package com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipe.Data.OverviewData;
import com.example.nikis.bludogramfirebase.Recipe.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipe.Data.StepsData;
import com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest.Fragments.BaseEditRecipeFragment;
import com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest.Fragments.OverviewEditRecipeFragment;
import com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest.Fragments.StepsEditRecipeFragment;
import com.example.nikis.bludogramfirebase.Recipe.Upload.EditRecipeService;
import com.example.nikis.bludogramfirebase.Resource;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.nikis.bludogramfirebase.Profile.Repository.ProfileRepository.getFireBaseAuthUid;


public class EditRecipeActivity extends AppCompatActivity {
    private static final String TAG = "EditRecipeActivity";

    private static final String KEY_IS_IN_PROGRESS = "isProgress";
    private static final String EXTRA_RECIPE_ID = "recipeId";

    @Nullable
    private String mRecipeId;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.tab_layout)
    protected TabLayout tabLayout;

    @VisibleForTesting
    @BindView(R.id.filter)
    protected RelativeLayout filterForProgress;

    private RecipeData mRecipeData;

    private RecipeUploaderBroadcastReceiver mBroadcastReceiver;

    private boolean isInProgress;

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
                    case 0: return OverviewEditRecipeFragment.getInstance(getRecipeId());
                    case 1: return StepsEditRecipeFragment.getInstance(getRecipeId());
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


        IntentFilter intentFilter = new IntentFilter(
                EditRecipeService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        registerReceiver(mBroadcastReceiver = new RecipeUploaderBroadcastReceiver(),
                intentFilter);

        if (savedInstanceState != null) {

            isInProgress = savedInstanceState.getBoolean(KEY_IS_IN_PROGRESS);

            if (isInProgress) {

                showProgress();
            }

            Log.d(TAG, "onCreate: isInProgress: " + isInProgress);

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private String getRecipeId(){
        //return mRecipeId = "-LPfwWW9BCYiG0YBNigI";
        return mRecipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);
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
            startEditRecipe();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startEditRecipe() {
        getRecipeData();

        startService(EditRecipeService.getIntent(this, mRecipeData));
    }

    private void getRecipeData(){
        OverviewEditRecipeFragment overviewFragment = null;
        StepsEditRecipeFragment stepsFragment = null;

        for(Fragment fragment : getSupportFragmentManager().getFragments()){
            if(fragment instanceof BaseEditRecipeFragment.OverviewDataSender){
                overviewFragment = (OverviewEditRecipeFragment) fragment;
            }else if (fragment instanceof BaseEditRecipeFragment.StepsDataSender){
                stepsFragment = (StepsEditRecipeFragment) fragment;
            }
        }

        if (stepsFragment == null || overviewFragment == null)
            throw new NullPointerException("StepsFragment and OverviewFragment might not null");

        if (overviewFragment.isValidate() && stepsFragment.isValidate()) {
            OverviewData overviewData = overviewFragment.getData();

            StepsData stepsData = stepsFragment.getData();

            mRecipeData = new RecipeData(overviewData, stepsData);


            //TODO for TEST ONLY
            mRecipeData.recipeKey = getRecipeId();

            if(mRecipeData.authorUId == null)
                mRecipeData.authorUId = getFireBaseAuthUid();

            mRecipeData.overviewData.allImagePathList.add(0, mRecipeData.overviewData.mainImagePath);
            mRecipeData.overviewData.allImagePathList.addAll(1, mRecipeData.overviewData.imagePathsWithoutMainList);

            mRecipeData.dateTime = System.currentTimeMillis();

        }
    }

    private void showProgress() {
        isInProgress = true;
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.VISIBLE);
        }
    }
    private void hideProgress(){
        isInProgress = false;
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_IN_PROGRESS, isInProgress);
    }

    private class RecipeUploaderBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Resource<RecipeData> dataResource = intent.getParcelableExtra(EditRecipeService.EXTRA_RESOURCE);
            if (dataResource != null && dataResource.status == Resource.Status.SUCCESS) {
                onSuccess(dataResource);
            }else if (dataResource != null && dataResource.status == Resource.Status.LOADING) {
                showProgress();
            }else if (dataResource != null && dataResource.status == Resource.Status.ERROR) {
                onError(dataResource);
            }
        }

        private void onSuccess(Resource<RecipeData> resource){
            hideProgress();

            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();

        }

        private void onError(Resource<RecipeData> resource){
            hideProgress();

            Toast.makeText(getApplicationContext(), resource.exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
