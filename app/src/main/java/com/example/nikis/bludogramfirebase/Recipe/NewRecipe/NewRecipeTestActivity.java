package com.example.nikis.bludogramfirebase.Recipe.NewRecipe;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nikis.bludogramfirebase.BaseActivities.BaseActivityForTesting;
import com.example.nikis.bludogramfirebase.BaseFragments.BaseNewRecipeFragment;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.LocalUserData;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.RecipeData.Images.ListUploadImagePath;
import com.example.nikis.bludogramfirebase.RecipeData.Images.UploadImagePath;
import com.example.nikis.bludogramfirebase.RecipeData.RecipeData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.nikis.bludogramfirebase.Recipe.NewRecipe.CompressAndUploadImagesService.KEY_IMAGE_PATH;
import static com.example.nikis.bludogramfirebase.Recipe.NewRecipe.CompressAndUploadImagesService.KEY_IS_STEP_IMAGE;
import static com.example.nikis.bludogramfirebase.Recipe.NewRecipe.CompressAndUploadImagesService.KEY_POSITION;
import static com.example.nikis.bludogramfirebase.Recipe.NewRecipe.CompressAndUploadImagesService.KEY_P_INTENT;
import static com.example.nikis.bludogramfirebase.Recipe.NewRecipe.CompressAndUploadImagesService.KEY_RECIPE_KEY;
import static com.example.nikis.bludogramfirebase.Recipe.NewRecipe.CompressAndUploadImagesService.STATUS_FINISHED;

public class NewRecipeTestActivity extends BaseActivityForTesting {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_recipe_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        final TabLayout tabLayout = findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Base"));
        tabLayout.addTab(tabLayout.newTab().setText("Cooking"));

        final ViewPager viewPager = findViewById(R.id.view_pager);
        final CreateRecipePagerAdapter adapter = new CreateRecipePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab  tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(Tab tab) {
            }

            @Override
            public void onTabReselected(Tab tab) {
            }
        });
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
            startCreateRecipe();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startCreateRecipe() {
        if(!checkValidateFragmentsForm()) {
            Toast.makeText(this, "ПОЖАР, ГОРИМ!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isOnline()) {
            RecipeData recipeData = getRecipeData();
            writeNewRecipe(recipeData);
            startUploadImagesService(recipeData.getListUploadImagePath());
            showProgressDialog();
        }
        else {
            Snackbar.make(findViewById(R.id.tab_layout), "No network connection", Snackbar.LENGTH_LONG)
                    .setAction("retry", v -> startCreateRecipe())
                    .show();
        }
    }

    private boolean isOnline(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean checkValidateFragmentsForm(){
        boolean isValidate = false;
        for(Fragment fragment : getSupportFragmentManager().getFragments()){
            if(fragment instanceof DataSender){
                isValidate = ((DataSender)fragment).isValidateForm();
                if(!isValidate) return false;
            }
        }
        return isValidate;
    }

    private RecipeData getRecipeData(){
        RecipeData recipeData = new RecipeData();

        ArrayList<ArrayList<String>> imagesPath = new ArrayList<>();
        ArrayList<Boolean> isStepsCooking = new ArrayList<>();

        for(Fragment fragment : getSupportFragmentManager().getFragments()){
            if(fragment instanceof BaseNewRecipeFragment) {
                BaseNewRecipeFragment baseNewRecipeFragment = (BaseNewRecipeFragment)fragment;

                imagesPath.add(baseNewRecipeFragment.getImagesPath());
                isStepsCooking.add(baseNewRecipeFragment.getIsStepsCooking());

                recipeData.copyNotEmptyRowsFrom(baseNewRecipeFragment.getData());
            }
        }
        recipeData.setImagesPath(imagesPath);
        recipeData.setIsStepsCooking(isStepsCooking);
        recipeData.userLogin = LocalUserData.getInstance().getLogin();
        return recipeData;
    }

    private void writeNewRecipe(RecipeData recipeData) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();


        recipeData.setRecipeKey(myRef.child("recipes").push().getKey());
        recipeData.uid = user.getUid();

        Map<String, Object> postValues = recipeData.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/recipes/" + recipeData.recipeKey, postValues);
        childUpdates.put("/user-recipes/" + recipeData.uid  + "/" + recipeData.recipeKey, postValues);

        myRef.updateChildren(childUpdates).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d("WriteNewRecipe", "writeNewRecipe success");
            }else{
                Log.d("WriteNewRecipe", "writeNewRecipe failure", task.getException());
            }
            //hideProgressDialog();
        });
    }

    private void startUploadImagesService(ListUploadImagePath listUploadImagePath){
        for(UploadImagePath uploadImagePath : listUploadImagePath){
            Intent intent = new Intent(getApplication(), CompressAndUploadImagesService.class)
                    .putExtra(KEY_POSITION, uploadImagePath.position)
                    .putExtra(KEY_IS_STEP_IMAGE, uploadImagePath.isStepImage)
                    .putExtra(KEY_IMAGE_PATH, uploadImagePath.getImagePath())
                    .putExtra(KEY_RECIPE_KEY, uploadImagePath.recipeKey);

            PendingIntent pendingIntent = createPendingResult(STATUS_FINISHED, intent, 0);

            intent.putExtra(KEY_P_INTENT, pendingIntent);

            startService(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == STATUS_FINISHED){
            hideProgressDialog();
            Toast.makeText(this, "UPLOAD IMAGES FINISHED!", Toast.LENGTH_SHORT).show();
        }
    }
    public interface DataSender{
        boolean isValidateForm();
        //Что-то_там get data();
    }
}
