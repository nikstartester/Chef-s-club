package com.xando.chefsclub.Main;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.xando.chefsclub.Compilations.Sync.SyncCompilationService;
import com.xando.chefsclub.Compilations.ViewCompilations.UserCompilationsFragment;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.Helpers.FirebaseHelper;
import com.xando.chefsclub.Images.ImageData.ImageData;
import com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader;
import com.xando.chefsclub.Profiles.Data.ProfileData;
import com.xando.chefsclub.Profiles.ViewModel.ProfileViewModel;
import com.xando.chefsclub.Profiles.ViewProfiles.SingleProfile.ViewProfileActivityTest;
import com.xando.chefsclub.Profiles.ViewProfiles.Subscriptions.UserSubscriptionsFragment;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.EditRecipe.EditRecipeActivity;
import com.xando.chefsclub.Recipes.ViewRecipes.AllRecipes.AllRecipesFragment;
import com.xando.chefsclub.Recipes.ViewRecipes.LocalCookBook.LocalRecipesFragment;
import com.xando.chefsclub.Recipes.ViewRecipes.ToSearcher;
import com.xando.chefsclub.Recipes.ViewRecipes.UserCookBook.UserRecipesFragment;
import com.xando.chefsclub.Search.Core.BaseFilterData;
import com.xando.chefsclub.Search.Profiles.Filter.ProfileFilterData;
import com.xando.chefsclub.Search.Recipes.Filter.RecipeFilterData;
import com.xando.chefsclub.Search.SearchFragment;
import com.xando.chefsclub.Settings.SettingsActivity;
import com.xando.chefsclub.ShoppingList.ViewShoppingListActivity;

import static com.xando.chefsclub.Helpers.FirebaseHelper.getUid;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ToSearcher {

    private static final int REQUEST_CODE_UPDATE_IMAGE = 532;
    protected FloatingActionButton fab;
    private SparseArray<Fragment> fragments;
    private ImageView userProfileImage;
    private TextView tvLogin;
    private TextView tvName;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startSyncCompilationsTittle();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            //Intent intent = new Intent(this, EditRecipeActivity.class);
            startActivity(EditRecipeActivity.getIntent(this));
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        View header = mNavigationView.getHeaderView(0);

        tvLogin = header.findViewById(R.id.tv_login);
        tvName = header.findViewById(R.id.tv_firstAndLastName);
        userProfileImage = header.findViewById(R.id.image_profileImage);


        tvLogin.setOnClickListener(this::getClickListenerForUserData);
        tvName.setOnClickListener(this::getClickListenerForUserData);
        userProfileImage.setOnClickListener(this::getClickListenerForUserData);

        fragments = new SparseArray<>(2);

        ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.getResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                if (resource.status == ParcResourceByParc.Status.SUCCESS) {

                    tvLogin.setText(resource.data.login);
                    tvName.setText(resource.data.firstName + " " + resource.data.secondName);

                    setImage(resource.data);
                }
            }
        });

        if (profileViewModel.getResourceLiveData().getValue() == null)
            profileViewModel.loadDataAndSync(FirebaseAuth.getInstance().getUid());

        if (savedInstanceState == null)
            showStartFragment();

    }

    private void startSyncCompilationsTittle() {
        if (getUid() != null)
            startService(SyncCompilationService.getIntent(this, getUid()));
    }

    private void getClickListenerForUserData(View view) {
        startActivity(ViewProfileActivityTest.getIntent(this, FirebaseHelper.getUid()));
    }

    private void setImage(ProfileData profileData) {
        if (profileData.imageURL != null) {
            ImageData imageData = new ImageData(profileData.imageURL, profileData.lastTimeUpdate);

            GlideImageLoader.getInstance().loadNormalCircularImage(this,
                    userProfileImage,
                    imageData);
        } else userProfileImage.setImageResource(R.mipmap.ic_launcher_round);
    }

    private void showStartFragment() {
        int startFragmentId = R.id.nav_recipes;

        MenuItem item = mNavigationView.getMenu().findItem(startFragmentId);

        item.setChecked(true);

        fragmentClick(item, startFragmentId);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_recipes
                || id == R.id.nav_user_cookbook
                || id == R.id.nav_local_cookbook
                || id == R.id.nav_compilations
                || id == R.id.nav_subscription
                || id == R.id.nav_search) {

            fragmentClick(item, id);

        } else if (id == R.id.nav_shopping_list
                || id == R.id.nav_settings) {

            activityClick(id);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void fragmentClick(@NonNull MenuItem item, int id) {
        Fragment fragment = fragments.get(id);
        if (fragment != null)
            showFragment(fragment);
        else {
            fragment = getFragmentInstance(id);

            if (fragment != null)
                addNewFragment(fragment, id);
        }

        setTittle(item.getTitle().toString());
        changeFabVisibility(id);
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();

        hideAllFragments();

        ftrans.show(fragment);
        ftrans.commit();
    }

    private void hideAllFragments() {
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            ftrans.hide(fragment);
        }
        ftrans.commit();
    }

    @Nullable
    private Fragment getFragmentInstance(int id) {
        Fragment fragment = fragments.get(id);
        if (fragment == null)
            switch (id) {
                case R.id.nav_recipes:
                    fragment = new AllRecipesFragment();
                    break;
                case R.id.nav_user_cookbook:
                    fragment = new UserRecipesFragment();
                    break;
                case R.id.nav_local_cookbook:
                    fragment = new LocalRecipesFragment();
                    break;
                case R.id.nav_compilations:
                    fragment = new UserCompilationsFragment();
                    break;
                case R.id.nav_subscription:
                    fragment = new UserSubscriptionsFragment();
                    break;
                case R.id.nav_search:
                    fragment = new SearchFragment();
                    break;

            }
        return fragment;
    }

    private void setTittle(String tittle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(tittle);
        }
    }

    private void addNewFragment(Fragment fragment, int id) {
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();

        hideAllFragments();

        ftrans.add(R.id.container, fragment);
        fragments.put(id, fragment);

        ftrans.commit();
    }

    private void changeFabVisibility(int fragmentId) {
        if (fragmentId == R.id.nav_recipes
                || fragmentId == R.id.nav_user_cookbook
                || fragmentId == R.id.nav_local_cookbook) {
            fab.show();
        } else fab.hide();
    }

    private void activityClick(int id) {
        Intent intent = getIntentForActivity(id);

        if (intent != null)
            startActivity(intent);
    }

    @Nullable
    private Intent getIntentForActivity(int id) {
        Intent intent = null;

        switch (id) {
            case R.id.nav_shopping_list:
                intent = new Intent(this, ViewShoppingListActivity.class);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }

        return intent;
    }

    @Override
    public void toSearch(int lookFor, int searchFrom) {
        int id = R.id.nav_search;

        Fragment fragment = getSearchFragment(lookFor, searchFrom);

        addNewFragment(fragment, id);

        changeFabVisibility(id);
        mNavigationView.getMenu().findItem(R.id.nav_search).setChecked(true);

    }

    private Fragment getSearchFragment(int lookFor, int searchFrom) {
        BaseFilterData filterData;

        if (lookFor == LOOK_FOR_PROFILES)
            filterData = new ProfileFilterData().setSearchFrom(searchFrom);
        else filterData = new RecipeFilterData().setSearchFrom(searchFrom);

        return SearchFragment.getInstance(filterData);
    }
}
