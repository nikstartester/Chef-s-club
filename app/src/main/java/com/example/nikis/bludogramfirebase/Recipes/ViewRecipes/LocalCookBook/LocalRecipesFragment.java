package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.LocalCookBook;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.Data.ActualRecipeDataChecker;
import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList.Item.RecipeItem;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList.RecipeEventHookFragment;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.LocalCookBook.ViewModel.LocalRecipesViewModel;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SingleRecipe.ViewRecipeActivity;
import com.example.nikis.bludogramfirebase.Recipes.db.Helper;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LocalRecipesFragment extends RecipeEventHookFragment {
    private static final String TAG = "LocalRecipesFragment";
    private final ActualRecipeDataChecker mDataChecker = new ActualRecipeDataChecker();

    @BindView(R.id.rv_allRecipes)
    protected RecyclerView rvRecipes;

    @BindView(R.id.empty_placeholder)
    protected View emptyPlaceholder;

    private FastItemAdapter<RecipeItem> mRecipeAdapter;
    private LocalRecipesViewModel mRecipesViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecipeAdapter = new FastItemAdapter<>();

        mRecipesViewModel = ViewModelProviders.of(this).get(LocalRecipesViewModel.class);

        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_all_recipes, container, false);

        ButterKnife.bind(this, view);

        unitViews();

        setOnClickListeners();

        mRecipesViewModel.getData().observe(this, dataList -> {
            if (dataList != null) {
                onDataLoaded(dataList);
            }
        });

        if (savedInstanceState == null) {
            mRecipesViewModel.loadData();
        }

        return view;
    }

    private void unitViews() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        rvRecipes.setLayoutManager(layoutManager);

        rvRecipes.setAdapter(mRecipeAdapter);

        rvRecipes.setItemAnimator(new DefaultItemAnimator());

        mRecipeAdapter.getItemFilter().withFilterPredicate((IItemAdapter.Predicate<RecipeItem>) (item, constraint) ->
                constraint != null &&
                        (item.getRecipeData().overviewData.name.toLowerCase().contains(constraint.toString().toLowerCase())));
    }

    private void setOnClickListeners() {
        mRecipeAdapter.withOnClickListener((v, adapter, item, position) -> {

            boolean b = item.getRecipeData().authorUId.equals(FirebaseHelper.getUid());

            startActivity(ViewRecipeActivity.getIntent(getActivity(),
                    item.getRecipeData().recipeKey, b));

            return true;
        });

        mRecipeAdapter.withEventHook(new EventHookForRecipeItem<>());
    }

    private void onDataLoaded(@NonNull List<RecipeData> dataList) {
        if (dataList.size() > 0) {
            List<Integer> updatedPos = changeOrDeleteItems(dataList);

            addRecipeItems(dataList, updatedPos);

            hideEmptyPlaceHolder();
        } else {
            showEmptyPlaceHolder();

            mRecipeAdapter.clear();
        }

    }

    private void showEmptyPlaceHolder() {
        emptyPlaceholder.setVisibility(View.VISIBLE);
    }

    private void hideEmptyPlaceHolder() {
        emptyPlaceholder.setVisibility(View.GONE);
    }

    private List<Integer> changeOrDeleteItems(@NonNull List<RecipeData> dataList) {
        List<Integer> updatedPos = new ArrayList<>();

        List<RecipeItem> recipeItemList = mRecipeAdapter.getAdapterItems();
        List<Integer> itemsToRemove = new ArrayList<>();

        int itemsCount = recipeItemList.size();

        for (int i = 0; i < itemsCount; i++) {
            int pos = getItemPosition(dataList, recipeItemList.get(i).getRecipeData().recipeKey);

            if (pos != -1) {
                RecipeData newData = dataList.get(pos);

                RecipeItem item = recipeItemList.get(i);

                boolean isNeedUpdUiAllData = item.onUpdateData(newData, mDataChecker);

                if (isNeedUpdUiAllData) {
                    mRecipeAdapter.notifyItemChanged(i);
                }
            } else {
                itemsToRemove.add(i);
                //mRecipeAdapter.remove(i);
            }
            updatedPos.add(i);
        }

        for (int pos : itemsToRemove) {
            mRecipeAdapter.remove(pos);
        }

        return updatedPos;
    }

    private int getItemPosition(List<RecipeData> list, String recipeKey) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).recipeKey.equals(recipeKey)) {
                return i;
            }
        }
        return -1;
    }

    private void addRecipeItems(@NonNull List<RecipeData> dataList, List<Integer> updatedPos) {
        for (int i = 0; i < dataList.size(); i++) {
            boolean isNeedAdd = true;

            for (int p : updatedPos) {
                if (i == p) {
                    isNeedAdd = false;
                    break;
                }
            }
            if (isNeedAdd) {
                mRecipeAdapter.add(new RecipeItem(dataList.get(i), this));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.shopping_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mRecipeAdapter.filter(null);
                } else {
                    mRecipeAdapter.filter(newText);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {

            showDeleteAllDialog();
        }
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecipeAdapter != null) {
            for (RecipeItem recipeItem : mRecipeAdapter.getAdapterItems()) {
                recipeItem.stopAnimations();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecipeAdapter != null) {
            for (RecipeItem recipeItem : mRecipeAdapter.getAdapterItems()) {
                recipeItem.unbind();
            }
        }
    }

    private void showDeleteAllDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        String title = "Remove all";
        String message = "Are you sure you want to remove all recipes?";
        String positiveButtonStr = "Yes";
        String negativeButtonStr = "Cancel";

        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(R.drawable.ic_delete_blue_24dp);

        dialog.setPositiveButton(positiveButtonStr, (dialog1, which) -> {
            Helper.deleteAll((App) getActivity().getApplication());

            mRecipeAdapter.clear();
        });

        dialog.setNegativeButton(negativeButtonStr, (dialog12, which) -> dialog12.cancel());

        dialog.setCancelable(true);

        dialog.show();
    }

}
