package com.xando.chefsclub.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.xando.chefsclub.R;
import com.xando.chefsclub.compilations.sync.SyncCompilationService;
import com.xando.chefsclub.compilations.viewcompilations.UserCompilationsFragment;
import com.xando.chefsclub.dataworkers.ParcResourceByParc;
import com.xando.chefsclub.helper.FirebaseHelper;
import com.xando.chefsclub.image.data.ImageData;
import com.xando.chefsclub.image.loaders.GlideImageLoader;
import com.xando.chefsclub.profiles.data.ProfileData;
import com.xando.chefsclub.profiles.viewmodel.ProfileViewModel;
import com.xando.chefsclub.profiles.viewprofiles.single.ViewProfileActivityTest;
import com.xando.chefsclub.profiles.viewprofiles.subscriptions.UserSubscriptionsFragment;
import com.xando.chefsclub.recipes.editrecipe.EditRecipeActivity;
import com.xando.chefsclub.recipes.viewrecipes.ToSearcher;
import com.xando.chefsclub.recipes.viewrecipes.allrecipes.AllRecipesFragment;
import com.xando.chefsclub.recipes.viewrecipes.localcookbook.LocalRecipesFragment;
import com.xando.chefsclub.recipes.viewrecipes.usercookbook.UserRecipesFragment;
import com.xando.chefsclub.search.SearchFragment;
import com.xando.chefsclub.search.core.BaseFilterData;
import com.xando.chefsclub.search.profiles.filter.ProfileFilterData;
import com.xando.chefsclub.search.recipes.filter.RecipeFilterData;
import com.xando.chefsclub.settings.SettingsActivity;
import com.xando.chefsclub.shoppinglist.ViewShoppingListActivity;

import static com.xando.chefsclub.helper.FirebaseHelper.getUid;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ToSearcher {

    private static final String MAIN_IS_FAB_VISIBLE = "MAIN_IS_FAB_VISIBLE";

    protected FloatingActionButton fab;
    private SparseArray<Fragment> fragments;
    private ImageView userProfileImage;
    private TextView tvLogin;
    private TextView tvName;
    private NavigationView mNavigationView;

    private boolean isFabVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startSyncCompilationsTittle();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
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

        fragments = new SparseArray<>(6);

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
        else {
            if (isFabVisible = savedInstanceState.getBoolean(MAIN_IS_FAB_VISIBLE))
                fab.show();
            else fab.hide();
        }
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

        setTittle(item.getItemId() != R.id.nav_search ? item.getTitle().toString() : "");
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
            isFabVisible = true;
        } else {
            fab.hide();
            isFabVisible = false;
        }
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

        setTittle("");

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(MAIN_IS_FAB_VISIBLE, isFabVisible);
        super.onSaveInstanceState(outState);
    }
}
