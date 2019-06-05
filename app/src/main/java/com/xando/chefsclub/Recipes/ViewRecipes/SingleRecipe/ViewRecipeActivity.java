package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xando.chefsclub.App;
import com.xando.chefsclub.Compilations.AddRecipe.AddToCompilationDialogFragment;
import com.xando.chefsclub.DataWorkers.ActualDataChecker;
import com.xando.chefsclub.DataWorkers.BaseRepository;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.Helpers.NetworkHelper;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.Data.ActualRecipeDataChecker;
import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.EditRecipe.EditRecipeActivity;
import com.xando.chefsclub.Recipes.Local.LocalRecipeSaver;
import com.xando.chefsclub.Recipes.Repository.RecipeRepository;
import com.xando.chefsclub.Recipes.Upload.EditRecipeService;
import com.xando.chefsclub.Recipes.ViewModel.RecipeViewModel;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.CommentsListFragment;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.Data.CommentData;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.ViewHolder.CommentViewHolder;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Fragments.OverviewViewRecipeFragment;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Fragments.StepsViewRecipeFragment;
import com.xando.chefsclub.Recipes.db.RecipeEntity;
import com.xando.chefsclub.Settings.SettingsLoacalFragment;
import com.xando.chefsclub.ShoppingList.ViewShoppingListActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.xando.chefsclub.Recipes.Repository.RecipeRepository.CHILD_RECIPES;


public class ViewRecipeActivity extends AppCompatActivity implements CommentsListFragment.OnUserAddedComment,
        CommentViewHolder.OnReplyComment {
    private static final String TAG = "ViewRecipeActivity";

    public static final int RESULT_CODE_REMOVE_RECIPE = 152;
    public static final int RESULT_CODE_UNFAVORITE = 153;
    public static final String EXTRA_RECIPE_KEY = "recipe_key";

    private static final String KEY_IS_IN_PROGRESS = "isProgress";
    private static final String KEY_RECIPE_DATA = "keyViewRecipeData";
    private static final String KEY_RECIPE_ID = "keyViewRecipeId";
    private static final String KEY_IS_REMOVED_FROM_SERVER = "IS_REMOVED_FROM_SERVER";

    private static final String EXTRA_RECIPE_ID = "recipeId";
    private static final String EXTRA_RECIPE_DATA = "recipeData";
    private static final String EXTRA_IS_CURR_USER_CREATE = "isCurrUser";


    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.tab_layout)
    protected TabLayout tabLayout;
    @BindView(R.id.filter)
    protected View filterForProgress;


    @Nullable
    private String mRecipeId;

    @Nullable
    private RecipeData mRecipeData;
    private RecipeData mNotVisibleActualRecipeData;

    private boolean isInProgress;

    private RecipeViewModel mRecipeViewModel;

    private boolean isRecipeCreateCurrUser;
    private boolean isSaved;

    private final ActualDataChecker<RecipeData> mDataChecker = new ActualRecipeDataChecker();

    private Menu mMenu;

    private Boolean isRemovedFromServer = null;

    private RecipeUploaderBroadcastReceiver mBroadcastReceiver;

    public static Intent getIntent(Context context, @Nullable String recipeId,
                                   boolean isRecipeCreateCurrUser) {

        Intent intent = new Intent(context, ViewRecipeActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        intent.putExtra(EXTRA_IS_CURR_USER_CREATE, isRecipeCreateCurrUser);

        return intent;
    }

    public static Intent getIntent(Context context, @Nullable RecipeData recipeData,
                                   boolean isRecipeCreateCurrUser) {
        Intent intent = new Intent(context, ViewRecipeActivity.class);
        intent.putExtra(EXTRA_RECIPE_DATA, recipeData);
        intent.putExtra(EXTRA_IS_CURR_USER_CREATE, isRecipeCreateCurrUser);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_recipe);

        mRecipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);

        mRecipeData = getIntent().getParcelableExtra(EXTRA_RECIPE_DATA);

        isRecipeCreateCurrUser = getIntent().getBooleanExtra(EXTRA_IS_CURR_USER_CREATE, false);

        if (mRecipeId == null) {
            if (mRecipeData != null) mRecipeId = mRecipeData.recipeKey;
            else throw new NullPointerException("recipeId OR recipeData might not be null. " +
                    "Use getIntent methods to normal functionality");
        }

        detectIsSavedOnLocal();

        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        initTabs();

        mRecipeViewModel.getResourceLiveData().observe(this, this::onRecipeLoaded);

        if (savedInstanceState != null) {
            isInProgress = savedInstanceState.getBoolean(KEY_IS_IN_PROGRESS);
            mRecipeId = savedInstanceState.getString(KEY_RECIPE_ID);
            isRemovedFromServer = (Boolean) savedInstanceState.getSerializable(KEY_IS_REMOVED_FROM_SERVER);

            if (isInProgress) {
                showProgress();
            }

            /*
            It need if app restart
             */
            /*if (mRecipeViewModel.getResourceLiveData().getValue() == null && !isInProgress) {
                setOrLoadRecipe();
            }*/

        } else {
            //setOrLoadRecipe();
        }

        if (mRecipeViewModel.getResourceLiveData().getValue() == null)
            setOrLoadRecipe();

        IntentFilter intentFilter = new IntentFilter(
                EditRecipeService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        registerReceiver(mBroadcastReceiver = new RecipeUploaderBroadcastReceiver(),
                intentFilter);

    }

    private void initTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.edit_recipe_overview)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.edit_recipe_steps_cooking)));

        final ViewPager viewPager = findViewById(R.id.view_pager);

        final FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return OverviewViewRecipeFragment.getInstance(mRecipeId);
                    case 1:
                        return StepsViewRecipeFragment.getInstance(mRecipeId);
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
    }

    private void onRecipeLoaded(ParcResourceByParc<RecipeData> resource) {
        if (resource != null) {
            switch (resource.status) {
                case SUCCESS:
                    onSuccessLoaded(resource);
                    break;
                case LOADING:
                    showProgress();
                    break;
                case ERROR:
                    onErrorLoaded(resource);
                    hideProgress();
                    break;
            }
        }
    }

    private void onSuccessLoaded(ParcResourceByParc<RecipeData> resource) {
        if (mRecipeData != null && mDataChecker.hasChanged(mRecipeData, resource.data)) {
            mNotVisibleActualRecipeData = resource.data;

            showNotSyncMessage();

        } else {
            hideNotSyncMessage();

            getSupportActionBar().setTitle(resource.data.overviewData.name);

            mRecipeData = resource.data;
        }

        if (resource.from == ParcResourceByParc.From.SERVER) {
            isRemovedFromServer = false;

            changeMenuIfRecipeDeleted(false);

            hideRecipeRemovedMessage();
        } else if (isRemovedFromServer != null && isRemovedFromServer) {
            changeMenuIfRecipeDeleted(true);

            showRecipeRemovedMessage();
        }

        hideProgress();
    }

    private void hideNotSyncMessage() {
        findViewById(R.id.not_sync_data).setVisibility(View.GONE);
    }

    private void showNotSyncMessage() {
        findViewById(R.id.not_sync_data).setVisibility(View.VISIBLE);
    }

    private void hideAllErrorMessages() {
        hideRecipeRemovedMessage();
    }

    private void onErrorLoaded(ParcResourceByParc<RecipeData> resource) {
        assert resource.exception != null;

        if (resource.exception instanceof BaseRepository.NothingFoundFromServerException) {
            isRemovedFromServer = true;

            changeMenuIfRecipeDeleted(true);

            showRecipeRemovedMessage();

            /*
            This check and action need if data exist in db but recipes has been removed from server
             */
            if (mRecipeData == null) {
                mRecipeViewModel.loadData(mRecipeId, BaseRepository.Priority.DATABASE_ONLY);
            }

        } else if (resource.exception instanceof BaseRepository.NothingFoundFromDbException) {
            Toast.makeText(getApplicationContext(), "Recipe has been deleted", Toast.LENGTH_SHORT).show();

            finish();
        } else {
            changeMenuIfRecipeDeleted(false);

            hideRecipeRemovedMessage();

            Toast.makeText(this, resource.exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void changeMenuIfRecipeDeleted(boolean isDeleted) {
        if (mMenu == null) return;

        mMenu.findItem(R.id.act_add_to_compilation).setEnabled(!isDeleted);
        mMenu.findItem(R.id.act_delete).setEnabled(!isDeleted);

        onPrepareOptionsMenu(mMenu);
    }

    private void showRecipeRemovedMessage() {
        findViewById(R.id.removed_data).setVisibility(View.VISIBLE);

        if (isRecipeCreateCurrUser) {
            findViewById(R.id.btn_remove_undo).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btn_remove_undo).setVisibility(View.GONE);
        }
    }

    private void hideRecipeRemovedMessage() {
        findViewById(R.id.removed_data).setVisibility(View.GONE);
    }

    private void setOrLoadRecipe() {
        if (mRecipeData != null)
            mRecipeViewModel.setResourceData(ParcResourceByParc.success(mRecipeData));
        else {
            boolean isSyncAlways = SettingsLoacalFragment.isSavingViewedRecipes(getApplication()) && !isSaved;

            mRecipeViewModel.loadData(mRecipeId, isSyncAlways, isSyncAlways,
                    BaseRepository.Priority.DATABASE_FIRST_AND_SERVER, false);
        }
    }

    private void detectIsSavedOnLocal() {
        assert mRecipeId != null;

        Single<List<RecipeEntity>> single = ((App) getApplication())
                .getDatabase()
                .recipeDao()
                .getSingleByRecipeKey(mRecipeId);

        single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recipeEntities -> {
                    if (!recipeEntities.isEmpty()) {
                        isSaved = true;
                    }
                });
    }

    @Override
    public void onUserAddedComment() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof CommentsListFragment.OnUserAddedComment)
                ((CommentsListFragment.OnUserAddedComment) fragment).onUserAddedComment();
        }
    }

    @Override
    public void onReplyComment(CommentData commentData) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof CommentViewHolder.OnReplyComment)
                ((CommentViewHolder.OnReplyComment) fragment).onReplyComment(commentData);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.view_recipe_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isRecipeCreateCurrUser) {
            menu.findItem(R.id.act_edit).setVisible(true);
            menu.findItem(R.id.act_delete).setVisible(true);
        }

        if (mRecipeId == null) {
            menu.findItem(R.id.act_delete_from_db).setVisible(false);
            menu.findItem(R.id.act_add_to_db).setVisible(false);
            menu.findItem(R.id.act_open_shopping_list).setVisible(false);
        } else if (isSaved) {
            menu.findItem(R.id.act_delete_from_db).setVisible(true);
            menu.findItem(R.id.act_add_to_db).setVisible(false);
        } else {
            menu.findItem(R.id.act_delete_from_db).setVisible(false);
            menu.findItem(R.id.act_add_to_db).setVisible(true);
        }

        mMenu = menu;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.act_edit:
                startActivity(EditRecipeActivity.getIntent(this, mRecipeData));
                break;
            case R.id.act_open_shopping_list:
                startActivity(ViewShoppingListActivity.getIntent(this, mRecipeId)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;
            case R.id.act_add_to_db:
                addToDb();
                break;
            case R.id.act_delete_from_db:
                deleteFromDb();
                break;
            case R.id.act_add_to_compilation:
                addToCompilation();
                break;
            case R.id.act_delete:
                showDeleteRecipesDialog(mRecipeData);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToDb() {
        if (mRecipeData != null)
            new LocalRecipeSaver(getApplication(), true).save(mRecipeData);
        else {
            if (mRecipeId != null)
                RecipeRepository.with(getApplication())
                        .setFirebaseId(mRecipeId)
                        .setFirebaseChild(CHILD_RECIPES)
                        .setLocalSever(new LocalRecipeSaver(getApplication(), false))
                        .setPriority(BaseRepository.Priority.DATABASE_FIRST_OR_SERVER)
                        .isWithoutStatus(true)
                        .build()
                        .loadData();
        }

        isSaved = true;
        onPrepareOptionsMenu(mMenu);
    }

    private void deleteFromDb() {
        if (mRecipeId != null)
            new Thread(() -> ((App) getApplication())
                    .getDatabase()
                    .recipeDao()
                    .deleteByRecipeKey(mRecipeId)).start();

        isSaved = false;
        onPrepareOptionsMenu(mMenu);
    }

    private void addToCompilation() {
        if (isRemovedFromServer != null && isRemovedFromServer) {
            Toast.makeText(this, "Action is impossible: recipe has been deleted",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        startAddToCompilation();
    }

    private void startAddToCompilation() {
        AddToCompilationDialogFragment dialogFragment = new AddToCompilationDialogFragment();

        dialogFragment.setArguments(AddToCompilationDialogFragment.getArguments(mRecipeData));

        dialogFragment.show(getSupportFragmentManager(), "addToCompilation");
    }

    private void showDeleteRecipesDialog(RecipeData recipeData) {
        assert recipeData != null;

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        String title = "Delete \"" + recipeData.overviewData.name + "\"";
        String message = "Are you sure you want to delete recipe?";
        String positiveButtonStr = "Yes";
        String negativeButtonStr = "Cancel";

        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(R.drawable.ic_delete_blue_24dp);

        dialog.setPositiveButton(positiveButtonStr, (dialog1, which) -> {
            if (NetworkHelper.isConnected(this)) {
                if (mRecipeData != null) {
                    RecipeRepository
                            .deleteRecipeFromAllChild(getApplication(), mRecipeData, isSaved);

                    isRemovedFromServer = true;
                }
            } else
                Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        });

        dialog.setNegativeButton(negativeButtonStr, (dialog12, which) -> dialog12.cancel());

        dialog.setCancelable(true);

        dialog.show();
    }

    @OnClick(R.id.btn_update)
    void update(View v) {
        startActivity(ViewRecipeActivity.getIntent(this,
                mNotVisibleActualRecipeData,
                isRecipeCreateCurrUser));

        finish();
    }

    @OnClick(R.id.btn_remove_undo)
    void undoRemoveRecipe(View view) {
        if (mRecipeData != null) {
            EditRecipeService.startEditRecipeService(this,
                    mRecipeData.setNeedUpdateDateTime(false));
            isRemovedFromServer = false;
        }
    }

    @MainThread
    private void showProgress() {
        isInProgress = true;
        if (filterForProgress != null) {
            findViewById(R.id.img_white).setVisibility(View.VISIBLE);
            findViewById(R.id.img_fun).setVisibility(View.VISIBLE);
            filterForProgress.setVisibility(View.VISIBLE);
        }
    }

    @MainThread
    private void hideProgress() {
        isInProgress = false;
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.INVISIBLE);
            findViewById(R.id.img_fun).setVisibility(View.GONE);
            findViewById(R.id.img_white).setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_IS_IN_PROGRESS, isInProgress);
        outState.putString(KEY_RECIPE_ID, mRecipeId);
        outState.putSerializable(KEY_IS_REMOVED_FROM_SERVER, isRemovedFromServer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private class RecipeUploaderBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ParcResourceByParc<RecipeData> dataResource = intent.
                    getParcelableExtra(EditRecipeService.EXTRA_RESOURCE);

            if (dataResource == null) return;

            if (dataResource.data != null
                    && !dataResource.data.recipeKey.equals(mRecipeId)) {
                return;
            }

            if (dataResource.status == ParcResourceByParc.Status.SUCCESS) {
               /*
                empty
                 */
            } else if (dataResource.status == ParcResourceByParc.Status.LOADING) {
                /*
                empty
                 */

            } else if (dataResource.status == ParcResourceByParc.Status.ERROR) {
                onError(dataResource);
            }
        }

        private void onError(ParcResourceByParc<RecipeData> dataResource) {
            assert dataResource.exception != null;

            Toast.makeText(getApplicationContext(), dataResource.exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
