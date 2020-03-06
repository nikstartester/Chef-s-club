package com.xando.chefsclub.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.xando.chefsclub.R;
import com.xando.chefsclub.SingleFragmentActivity;
import com.xando.chefsclub.shoppinglist.db.IngredientEntity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewShoppingListActivity extends SingleFragmentActivity {

    private static final String EXTRA_INGREDIENT = "ExtraIngr";
    private static final String EXTRA_RECIPE_ID = "extraRecipeId";

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    public static Intent getIntent(Context context, @Nullable IngredientEntity entity) {
        Intent intent = new Intent(context, ViewShoppingListActivity.class);

        intent.putExtra(EXTRA_INGREDIENT, entity);

        return intent;
    }

    public static Intent getIntent(Context context, @Nullable String recipeId) {
        Intent intent = new Intent(context, ViewShoppingListActivity.class);

        intent.putExtra(EXTRA_RECIPE_ID, recipeId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected Fragment createFragment() {
        IngredientEntity entity = getIntent().getParcelableExtra(EXTRA_INGREDIENT);
        if (entity != null)
            return ViewShoppingListFragment.getInstance(entity);
        else
            return ViewShoppingListFragment.getInstance(getIntent().getStringExtra(EXTRA_RECIPE_ID));
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_fragment;
    }
}
