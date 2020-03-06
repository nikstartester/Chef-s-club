package com.xando.chefsclub.recipes.editrecipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.xando.chefsclub.App;
import com.xando.chefsclub.R;
import com.xando.chefsclub.constants.Constants;
import com.xando.chefsclub.dataworkers.BaseRepository;
import com.xando.chefsclub.dataworkers.DataUploaderService;
import com.xando.chefsclub.dataworkers.ParcResourceByParc;
import com.xando.chefsclub.helper.FirebaseHelper;
import com.xando.chefsclub.helper.NetworkHelper;
import com.xando.chefsclub.recipes.data.OverviewData;
import com.xando.chefsclub.recipes.data.RecipeData;
import com.xando.chefsclub.recipes.data.StepsData;
import com.xando.chefsclub.recipes.editrecipe.fragment.BaseEditRecipeWithKeyFragment;
import com.xando.chefsclub.recipes.editrecipe.fragment.OverviewEditRecipeFragment;
import com.xando.chefsclub.recipes.editrecipe.fragment.StepsEditRecipeFragment;
import com.xando.chefsclub.recipes.editrecipe.requiredfields.NormalizeRecipeData;
import com.xando.chefsclub.recipes.editrecipe.requiredfields.RequiredFieldsDialogFragment;
import com.xando.chefsclub.recipes.repository.RecipeRepository;
import com.xando.chefsclub.recipes.repository.local.LocalRecipeSaver;
import com.xando.chefsclub.recipes.upload.EditRecipeService;
import com.xando.chefsclub.recipes.upload.ImageAdapter;
import com.xando.chefsclub.recipes.upload.RecipeImagesData;
import com.xando.chefsclub.recipes.viewmodel.RecipeViewModel;
import com.xando.chefsclub.recipes.viewrecipes.singlerecipe.ViewRecipeActivity;
import com.xando.chefsclub.settings.SettingsCacheFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xando.chefsclub.recipes.repository.RecipeRepository.CHILD_RECIPES;


public class EditRecipeActivity extends AppCompatActivity {
    private static final String TAG = "EditRecipeActivity";

    public static final String KEY_OLD_IMAGES = "key_oldImages";
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

    private RecipeImagesData mOldImages;

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

        if (savedInstanceState != null) {
            mOldImages = savedInstanceState.getParcelable(KEY_OLD_IMAGES);
        }

        mRecipeViewModel.getResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case SUCCESS:
                        hideProgress();

                        if (resource.data.recipeKey != null)
                            mRecipeData = resource.data;

                        if (mOldImages == null) {
                            mOldImages = ImageAdapter.toImagesData(resource.data);
                        }

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

        if (isEdit()) {
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
        setRecipeData();

        if (!NormalizeRecipeData.checkRequired(mRecipeData, true)) {
            onRequiredFieldsError();
        } else {
            if (NetworkHelper.isConnected(this)) {
                startUpload();
            } else {
                showConnectionError();
            }
        }
    }

    private void onRequiredFieldsError() {
        RecipeData normalizedRecipeData = NormalizeRecipeData.normalizeRecipeData(setRecipeData());

        mRecipeViewModel.setResourceData(ParcResourceByParc.success(normalizedRecipeData));

        DialogFragment dialogFragment = RequiredFieldsDialogFragment.getInstance(new NormalizeRecipeData().getRequiredFields(normalizedRecipeData, false));
        dialogFragment.show(getSupportFragmentManager(), "required_fields");
    }

    private RecipeData setRecipeData() {
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

            setDataToRecipeData(overviewData, stepsData);

        }
        return mRecipeData;
    }

    private void setDataToRecipeData(OverviewData overviewData, StepsData stepsData) {
        if (mRecipeData == null) mRecipeData = new RecipeData();

        mRecipeData.overviewData = overviewData;
        mRecipeData.stepsData = stepsData;

        if (mRecipeData.authorUId == null)
            mRecipeData.authorUId = FirebaseHelper.getUid();

        mRecipeData.isUpdated = isEdit();

        mRecipeData.overviewData.allImagePathList.clear();

        mRecipeData.overviewData.allImagePathList.add(0,
                mRecipeData.overviewData.mainImagePath);

        mRecipeData.overviewData.allImagePathList.addAll(1,
                mRecipeData.overviewData.imagePathsWithoutMainList);
    }

    private void startUpload() {
        EditRecipeService.startEditRecipeService(this, NormalizeRecipeData.normalizeRecipeData(setRecipeData()));
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
            startActivity(ViewRecipeActivity.getIntent(this, setRecipeData(), false));
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
        outState.putParcelable(KEY_OLD_IMAGES, mOldImages);

        setRecipeData();

        outState.putParcelable(KEY_RECIPE_DATA, mRecipeData);
        mRecipeViewModel.setResourceData(ParcResourceByParc.success(mRecipeData));
    }

    private boolean isEdit() {
        return mRecipeData != null && mRecipeData.recipeKey != null;
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

            if (mUploadingRecipeData != null && mUploadingRecipeData.recipeKey != null
                    && !isEdit()) {
                findViewById(R.id.btn_filter_cancel).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.btn_filter_cancel).setVisibility(View.GONE);
            }
            showProgress();
        }

        private void onSuccess(ParcResourceByParc<RecipeData> resource) {
            hideProgress();

            showSuccessMessage(resource.data.overviewData.name);

            removeCaptures();

            removedUnnecessaryImagesFromServer(resource.data);

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

            if (isEdit() && getRecipeId() != null) {
                startActivity(ViewRecipeActivity.getIntent(EditRecipeActivity.this,
                        getRecipeId(), true)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }

            finish();
        }

        private void showSuccessMessage(String recipeName) {
            String message;
            if (!isEdit()) {
                message = "\"" + recipeName + "\" successfully added.";
            } else message = "\"" + recipeName + "\" successfully edited.";

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                    .show();
        }

        private void removeCaptures() {
            SettingsCacheFragment.deleteDir(new File(Constants.Files.
                    getDirectoryForEditRecipeImages(EditRecipeActivity.this)));
        }

        private void removedUnnecessaryImagesFromServer(final RecipeData data) {
            if (isEdit() && mOldImages != null) {
                new Thread(() -> {
                    RecipeRepository.deleteImages(ImageAdapter.getRemovedImages(mOldImages,
                            ImageAdapter.toImagesData(data)));
                }).start();
            }
        }

        private void onError(ParcResourceByParc<RecipeData> resource) {
            assert resource.exception != null;

            hideProgress();

            Toast.makeText(getApplicationContext(), resource.exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
