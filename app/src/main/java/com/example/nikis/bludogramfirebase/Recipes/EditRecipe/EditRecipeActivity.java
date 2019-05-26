package com.example.nikis.bludogramfirebase.Recipes.EditRecipe;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.Constants.Constants;
import com.example.nikis.bludogramfirebase.DataWorkers.BaseRepository;
import com.example.nikis.bludogramfirebase.DataWorkers.DataUploaderService;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceByParc;
import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.Helpers.NetworkHelper;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.Data.OverviewData;
import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipes.Data.StepsData;
import com.example.nikis.bludogramfirebase.Recipes.EditRecipe.Fragments.BaseEditRecipeWithKeyFragment;
import com.example.nikis.bludogramfirebase.Recipes.EditRecipe.Fragments.OverviewEditRecipeFragment;
import com.example.nikis.bludogramfirebase.Recipes.EditRecipe.Fragments.StepsEditRecipeFragment;
import com.example.nikis.bludogramfirebase.Recipes.EditRecipe.RequiredFields.NormalizeRecipeData;
import com.example.nikis.bludogramfirebase.Recipes.EditRecipe.RequiredFields.RequiredFieldsDialogFragment;
import com.example.nikis.bludogramfirebase.Recipes.Local.LocalRecipeSaver;
import com.example.nikis.bludogramfirebase.Recipes.Repository.RecipeRepository;
import com.example.nikis.bludogramfirebase.Recipes.Upload.EditRecipeService;
import com.example.nikis.bludogramfirebase.Recipes.ViewModel.RecipeViewModel;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SingleRecipe.ViewRecipeActivity;
import com.example.nikis.bludogramfirebase.Settings.SettingsCacheFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.nikis.bludogramfirebase.Recipes.Repository.RecipeRepository.CHILD_RECIPES;


public class EditRecipeActivity extends AppCompatActivity {
    private static final String TAG = "EditRecipeActivity";

    private static final String KEY_IS_IN_PROGRESS = "isProgress";
    private static final String KEY_IS_SAVE_ON_LOCAL = "isSave";
    private static final String KEY_RECIPE_DATA = "KeyEditRecipeDate";

    private static final String EXTRA_RECIPE_ID = "recipeId";
    private static final String EXTRA_RECIPE_DATA = "recipeData";

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.tab_layout)
    protected TabLayout tabLayout;

    @BindView(R.id.filter)
    protected View filterForProgress;

    private RecipeData mRecipeData;

    private RecipeData mUploadingRecipeData;

    private RecipeUploaderBroadcastReceiver mBroadcastReceiver;

    private boolean isInProgress;

    private boolean isSaveOnLocal;

    private RecipeViewModel mRecipeViewModel;

    public static Intent getIntent(Context context, @Nullable String recipeId) {
        Intent intent = new Intent(context, EditRecipeActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);

        return intent;
    }

    public static Intent getIntent(Context context) {

        return new Intent(context, EditRecipeActivity.class);
    }

    public static Intent getIntent(Context context, @Nullable RecipeData recipeData) {
        Intent intent = new Intent(context, EditRecipeActivity.class);
        intent.putExtra(EXTRA_RECIPE_DATA, recipeData);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecipeData = getIntent().getParcelableExtra(EXTRA_RECIPE_DATA);
        //if(mRecipeData == null) mRecipeData = new RecipeData();

        setContentView(R.layout.activity_new_recipe_test);

        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.edit_recipe_overview)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.edit_recipe_steps_cooking)));

        final ViewPager viewPager = findViewById(R.id.view_pager);

        final PagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return OverviewEditRecipeFragment.getInstance(getRecipeId());
                    case 1:
                        return StepsEditRecipeFragment.getInstance(getRecipeId());
                    default:
                        return null;
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


        mRecipeViewModel.getResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case SUCCESS:
                        hideProgress();

                        if (resource.data.recipeKey != null)
                            //getSupportActionBar().setTitle(resource.data.overviewData.name);

                            mRecipeData = resource.data;
                        break;
                    case LOADING:
                        showProgress();
                        break;
                    case ERROR:
                        hideProgress();
                        break;
                }
            }
        });


        if (savedInstanceState != null) {

            isInProgress = savedInstanceState.getBoolean(KEY_IS_IN_PROGRESS);

            if (isInProgress) {

                showProgress();
            }

            /*
            It need if app restart
             */

            if (mRecipeViewModel.getResourceLiveData().getValue() == null && !isInProgress) {

                if (mRecipeData == null)
                    mRecipeData = savedInstanceState.getParcelable(KEY_RECIPE_DATA);

                if (mRecipeData != null)
                    mRecipeViewModel.setResourceData(ParcResourceByParc.success(mRecipeData));
            }


        } else {
            if (mRecipeData != null) {
                mRecipeViewModel.setResourceData(ParcResourceByParc.success(mRecipeData));

                //getSupportActionBar().setTitle("Preview of: " + getIntentRecipeData().overviewData.name);
            }
        }

        if (getRecipeId() != null) {
            getSupportActionBar().setTitle("Edit Recipe");
        }

    }

    //TODO Need to support old functionality( Or not:) )
    @Deprecated
    @Nullable
    private String getRecipeId() {
        return mRecipeData == null ? null : mRecipeData.recipeKey;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.edit_recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getRecipeId() != null)
            menu.findItem(R.id.act_preview).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @OnClick(R.id.btn_filter_cancel)
    protected void cancelUpload(View view) {
        EditRecipeService.startEditRecipeService(this, mUploadingRecipeData,
                DataUploaderService.Action.CANCEL_UPLOAD);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_done:
                startEditRecipe();
                break;
            case R.id.act_preview:
                askToContinueShowPreview();
                break;
            case R.id.act_clear:
                startActivity(EditRecipeActivity.getIntent(this));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startEditRecipe() {
        if (!NormalizeRecipeData.checkRequired(getRecipeData(), true)) {
            onRequiredFieldsError();
        } else {
            if (NetworkHelper.isConnected(this)) {
                mRecipeData = getRecipeData();

                startUpload();
            } else {
                showConnectionError();
            }
        }
    }

    private void onRequiredFieldsError() {
        RecipeData normalizedRecipeData = NormalizeRecipeData.normalizeRecipeData(getRecipeData());

        mRecipeViewModel.setResourceData(ParcResourceByParc.success(normalizedRecipeData));

        DialogFragment dialogFragment = RequiredFieldsDialogFragment.getInstance(new NormalizeRecipeData().getRequiredFields(normalizedRecipeData, false));
        dialogFragment.show(getSupportFragmentManager(), "required_fields");
    }

    private RecipeData getRecipeData() {
        OverviewEditRecipeFragment overviewFragment = null;
        StepsEditRecipeFragment stepsFragment = null;

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BaseEditRecipeWithKeyFragment.OverviewDataSender) {
                overviewFragment = (OverviewEditRecipeFragment) fragment;
            } else if (fragment instanceof BaseEditRecipeWithKeyFragment.StepsDataSender) {
                stepsFragment = (StepsEditRecipeFragment) fragment;
            }
        }

        if (stepsFragment == null || overviewFragment == null)
            throw new NullPointerException("StepsFragment and OverviewFragment might not null");

        if (overviewFragment.isValidate() && stepsFragment.isValidate()) {
            isSaveOnLocal = overviewFragment.isSaveOnLocal();

            OverviewData overviewData = overviewFragment.getData();

            StepsData stepsData = stepsFragment.getData();

            setRecipeData(overviewData, stepsData);

        }

        return mRecipeData;
    }

    private void setRecipeData(OverviewData overviewData, StepsData stepsData) {
        if (mRecipeData == null) mRecipeData = new RecipeData();

        mRecipeData.overviewData = overviewData;
        mRecipeData.stepsData = stepsData;

        if (mRecipeData.authorUId == null)
            mRecipeData.authorUId = FirebaseHelper.getUid();

        mRecipeData.overviewData.allImagePathList.add(0,
                mRecipeData.overviewData.mainImagePath);

        mRecipeData.overviewData.allImagePathList.addAll(1,
                mRecipeData.overviewData.imagePathsWithoutMainList);
    }

    private void startUpload() {
        EditRecipeService.startEditRecipeService(this, mRecipeData);
    }

    private void showConnectionError() {
        Snackbar.make(tabLayout, getString(R.string.network_error), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.try_again), (v) -> {
                    startEditRecipe();
                }).show();
    }

    /*
    Use only on development
     */
    @VisibleForTesting
    private void askToContinueShowPreview() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        String title = "Warning!!!";
        String message = "This functionality shows how approximately the recipe will be displayed." +
                " ATTENTION: all buttons are working. Their use can lead to fatal errors of the" +
                " application and the server (it's inaccurate)! Use this method only for viewing. VSEM DOBRA :)" +
                "\n" +
                "\n" +
                " Are you sure you want to continue?";
        String positiveButtonStr = "Take a chance";
        String negativeButtonStr = "No, thanks";

        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(R.drawable.ic_warning_red_24dp);

        dialog.setPositiveButton(positiveButtonStr, (dialog1, which) -> {
            startActivity(ViewRecipeActivity.getIntent(this, getRecipeData(), false));
        });

        dialog.setNegativeButton(negativeButtonStr, (dialog12, which) -> dialog12.cancel());

        dialog.setCancelable(true);

        dialog.show();
    }

    private void showProgress() {
        isInProgress = true;
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        isInProgress = false;
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_IN_PROGRESS, isInProgress);
        outState.putBoolean(KEY_IS_SAVE_ON_LOCAL, isSaveOnLocal);

        mRecipeData = getRecipeData();

        outState.putParcelable(KEY_RECIPE_DATA, mRecipeData);
        mRecipeViewModel.setResourceData(ParcResourceByParc.success(mRecipeData));
    }

    private class RecipeUploaderBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ParcResourceByParc<RecipeData> dataResource = intent.
                    getParcelableExtra(EditRecipeService.EXTRA_RESOURCE);

            if (dataResource == null) return;

            if (dataResource.status == ParcResourceByParc.Status.SUCCESS) {
                onSuccess(dataResource);
            } else if (dataResource.status == ParcResourceByParc.Status.LOADING) {
                onLoading(dataResource);
            } else if (dataResource.status == ParcResourceByParc.Status.ERROR) {
                onError(dataResource);
            }
        }

        private void onLoading(ParcResourceByParc<RecipeData> dataResource) {
            mUploadingRecipeData = dataResource.data;

            if (mUploadingRecipeData != null && mUploadingRecipeData.recipeKey != null) {
                findViewById(R.id.btn_filter_cancel).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.btn_filter_cancel).setVisibility(View.GONE);
            }
            showProgress();
        }

        private void onSuccess(ParcResourceByParc<RecipeData> resource) {
            hideProgress();

            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();

            SettingsCacheFragment.deleteDir(new File(Constants.Files.
                    getDirectoryForEditRecipeImages(EditRecipeActivity.this)));

            if (isSaveOnLocal) {
                /*
                Load and save data(load because we use ServerValue.TIMESTAMP to time but need just long)
                 */
                RecipeRepository.with(getApplication())
                        .setFirebaseId(resource.data.recipeKey)
                        .setFirebaseChild(CHILD_RECIPES)
                        .setLocalSever(new LocalRecipeSaver(getApplication(), true))
                        .setPriority(BaseRepository.Priority.DATABASE_FIRST_AND_SERVER)
                        .isWithoutStatus(true)
                        .build()
                        .loadData();

            } else {
                if (getRecipeId() != null)
                    new Thread(() -> ((App) getApplication())
                            .getDatabase()
                            .recipeDao()
                            .deleteByRecipeKey(getRecipeId())).start();
            }

            finish();

        }

        private void onError(ParcResourceByParc<RecipeData> resource) {
            assert resource.exception != null;

            hideProgress();

            Toast.makeText(getApplicationContext(), resource.exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
