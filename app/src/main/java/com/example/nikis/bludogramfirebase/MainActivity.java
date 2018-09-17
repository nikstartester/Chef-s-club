package com.example.nikis.bludogramfirebase;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.nikis.bludogramfirebase.Profile.EditProfile;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.NewRecipeTestActivity;
import com.example.nikis.bludogramfirebase.Recipe.ViewRecipe.AllRecipes;
import com.example.nikis.bludogramfirebase.Recipe.ViewRecipe.UserRecipes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import static com.example.nikis.bludogramfirebase.BaseActivities.BaseActivityWithImageClick.TAG_TEST;
import static com.example.nikis.bludogramfirebase.Profile.EditProfile.RESULT_CODE_UPDATE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_UPDATE_IMAGE = 532;
    SparseArray<Fragment> fragments;
    ImageView userProfileImage;
    TextView tvLogin, tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view ->{
            Intent intent = new Intent(this, NewRecipeTestActivity.class);
            startActivity(intent);
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        tvLogin = header.findViewById(R.id.tv_login);
        tvName = header.findViewById(R.id.tv_firstAndLastName);


        userProfileImage = header.findViewById(R.id.image_profileImage);

        setUserData();
        fragments = new SparseArray<>(2);
    }

    private void setUserData(){
        LocalUserData localUserData = LocalUserData.getInstance();
        if(localUserData.isNeedUpdate()) {
            Log.d(TAG_TEST, "setUserData: needUpdate!");
            localUserData.addOnUpdateDataListener(userData -> {
                showProfileData();
                localUserData.putToPreferences(this);
            });
            localUserData.updateData();
        }else {
            showProfileData();
        }

    }
    private void setProfileImage(){
        StorageReference storageReference = FirebaseReferences.getStorageReference(
                "profilesImage/" + LocalUserData.getInstance().getUserData().userUid + ".jpg");

        GlideApp.with(this)
                .load(storageReference)
                .override(1080,1080)
                .thumbnail(0.2f)
                .error(R.drawable.ic_add_a_photo_blue_108dp)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .signature(new ObjectKey(LocalUserData.getInstance().getTimeLastImageUpdate()))
                .into(userProfileImage);
    }

    private void showProfileData(){
        if(!isDestroyed()) {
            setProfileImage();
            tvLogin.setText(LocalUserData.getInstance().getLogin());
            Log.d(TAG_TEST, "showProfileData: login:" + LocalUserData.getInstance().getLogin());
            tvName.setText(LocalUserData.getInstance().getFirstName() + " " + LocalUserData.getInstance().getSecondName());
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, EditProfile.class);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_IMAGE);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        switch (id){
           case R.id.nav_settings:
               Intent intent = new Intent(this, EditProfile.class);
               startActivityForResult(intent, REQUEST_CODE_UPDATE_IMAGE);
               break;
           case R.id.nav_recipes:
               fragment = fragments.get(id);
               if(fragment != null)
                    showFragment(id);
               else {
                   hideAllFragments();

                   fragment = new AllRecipes();
                   ftrans.add(R.id.container, fragment);

                   fragments.put(id, fragment);
               }
               ftrans.commit();
               break;
           case R.id.nav_user_cookbook:
               fragment = fragments.get(id);

               if(fragment != null)
                   showFragment(id);
               else {
                   hideAllFragments();

                   fragment = new UserRecipes();
                   ftrans.add(R.id.container, fragment);

                   fragments.put(id, fragment);
               }
               ftrans.commit();
               break;
            case R.id.nav_sign_out:
                LocalUserData.getInstance().clear().putToPreferences(this);

                FirebaseAuth.getInstance().signOut();

                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_UPDATE_IMAGE && resultCode == RESULT_CODE_UPDATE){
            setProfileImage();
        }
    }

    private void hideAllFragments(){
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        for(Fragment fragment : getSupportFragmentManager().getFragments()){
                ftrans.hide(fragment);
        }
        ftrans.commit();
    }
    private void showFragment(int menuId){
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();

        hideAllFragments();

        ftrans.show(fragments.get(menuId));
        ftrans.commit();
    }
}
