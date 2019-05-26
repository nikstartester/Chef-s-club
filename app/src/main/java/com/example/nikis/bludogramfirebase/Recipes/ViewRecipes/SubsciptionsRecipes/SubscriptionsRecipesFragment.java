package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SubsciptionsRecipes;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList.Item.RecipeItemByKey;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList.RecipeEventHookFragment;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SingleRecipe.ViewRecipeActivity;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SubsciptionsRecipes.Data.RecipeIdData;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SubsciptionsRecipes.ViewModel.SubscriptionsRecipesViewModel;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.ToSearcher;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper.getUid;


public class SubscriptionsRecipesFragment extends RecipeEventHookFragment {
    private static final String TAG = "SubscriptionsRecipesFra";

    @BindView(R.id.rv_allRecipes)
    protected RecyclerView allRecipesRv;

    @BindView(R.id.filter)
    protected View filterForProgress;

    @BindView(R.id.noInternet)
    protected RelativeLayout noInternet;

    @BindView(R.id.empty_placeholder)
    protected View emptyPlaceholder;

    private SubscriptionsRecipesViewModel mViewModel;

    private FastItemAdapter<RecipeItemByKey> mFastAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SubscriptionsRecipesViewModel.class);

        mFastAdapter = new FastItemAdapter<>();

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_all_recipes, container, false);

        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        allRecipesRv.setLayoutManager(layoutManager);
        allRecipesRv.setItemAnimator(new DefaultItemAnimator());

        mViewModel.getData().observe(this, res -> {
            if (res != null) {
                hideProgress();

                mFastAdapter.clear();
                if (res.size() == 0) {
                    showEmptyPlaceHolder();
                } else {
                    hideEmptyPlaceHolder();

                    for (RecipeIdData recipeIdData : res) {
                        mFastAdapter.add(new RecipeItemByKey(recipeIdData, this));
                    }
                }

            } else {
                showProgress();
            }
        });

        if (savedInstanceState == null || mViewModel.getData().getValue() == null)
            mViewModel.load();

        allRecipesRv.setAdapter(mFastAdapter);


        mFastAdapter.withOnClickListener((v, adapter, item, position) -> {

            boolean b = item.getRecipeIdData().authorUId.equals(getUid());

            startActivity(ViewRecipeActivity.getIntent(getActivity(),
                    item.getRecipeIdData().recipeKey, b));

            return true;
        });

        mFastAdapter.withEventHook(new EventHookForRecipeItem<>());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToSearcher) {
            ToSearcher toSearcher = (ToSearcher) context;
        }
    }

    private void showEmptyPlaceHolder() {
        emptyPlaceholder.setVisibility(View.VISIBLE);
    }

    private void hideEmptyPlaceHolder() {
        emptyPlaceholder.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mViewModel.load();

                return true;
        }
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFastAdapter != null) {
            for (RecipeItemByKey item : mFastAdapter.getAdapterItems()) {
                item.stopAnimations();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFastAdapter != null) {
            for (RecipeItemByKey item : mFastAdapter.getAdapterItems()) {
                item.unbind();
            }
        }
    }

    private void showProgress() {
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.INVISIBLE);
        }
    }
}
