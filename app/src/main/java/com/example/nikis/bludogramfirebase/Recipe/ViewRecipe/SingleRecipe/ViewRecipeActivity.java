package com.example.nikis.bludogramfirebase.Recipe.ViewRecipe.SingleRecipe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.RecipeData.RecipeAdapterData;
import com.example.nikis.bludogramfirebase.RecipeData.RecipeData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class ViewRecipeActivity extends AppCompatActivity {

    public static final String KEY_TAG = "recipeKey";
    private String recipeKey;
    private RecipeData recipeData;
    private ValueEventListener valueEventListener, valueEventListenerWithViewsUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        initViews();

        initCallbacks();

        recipeKey = getIntent().getStringExtra(KEY_TAG);

        startUploadRecipeData(valueEventListenerWithViewsUpdate);
    }

    private void initViews(){
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        final TabLayout tabLayout = findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Base"));
        tabLayout.addTab(tabLayout.newTab().setText("Cooking"));

        final ViewPager viewPager = findViewById(R.id.view_pager);
        final ViewRecipePagerAdapter adapter = new ViewRecipePagerAdapter(getSupportFragmentManager());
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

    private void initCallbacks(){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeData = dataSnapshot.getValue(RecipeData.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        valueEventListenerWithViewsUpdate = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeData = dataSnapshot.getValue(RecipeData.class);
                updateViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void startUploadRecipeData(ValueEventListener valueEventListener){
        final DatabaseReference databaseReference = FirebaseReferences.getDataBaseReference();
        databaseReference.child("recipes").child(recipeKey).addListenerForSingleValueEvent(valueEventListener);
    }

    private void updateViews(){

        for(Fragment fragment : getSupportFragmentManager().getFragments()){
            if(fragment instanceof OnUpdateViews){
                ((OnUpdateViews)fragment).onUpdate(recipeData);
            }
        }
    }

    interface OnUpdateViews{
        void onUpdate(RecipeData recipeData);
        void onUpdate(RecipeAdapterData recipeAdapterData);
    }
}
