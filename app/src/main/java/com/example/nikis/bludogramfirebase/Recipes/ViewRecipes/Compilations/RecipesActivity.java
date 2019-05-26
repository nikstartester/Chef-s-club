package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.Compilations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.example.nikis.bludogramfirebase.Compilations.Data.CompilationData;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.SingleFragmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipesActivity extends SingleFragmentActivity {

    private static final String EXTRA_COMPILATION = "extra_compilation";

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    private CompilationData mCompilation;

    public static Intent getIntent(Context context, CompilationData compilation) {
        Intent intent = new Intent(context, RecipesActivity.class);

        intent.putExtra(EXTRA_COMPILATION, compilation);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCompilation = getIntent().getParcelableExtra(EXTRA_COMPILATION);

        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> super.onBackPressed());

        getSupportActionBar().setTitle(mCompilation.name);

    }

    @Override
    protected Fragment createFragment() {
        Fragment fragment = new RecipesFragment();
        fragment.setArguments(RecipesFragment.getArguments(mCompilation));

        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_fragment;
    }
}
