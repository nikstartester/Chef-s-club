package com.xando.chefsclub.recipes.viewrecipes.compilations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.xando.chefsclub.R;
import com.xando.chefsclub.SingleFragmentActivity;
import com.xando.chefsclub.compilations.data.CompilationData;

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
