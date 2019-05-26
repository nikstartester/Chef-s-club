package com.example.nikis.bludogramfirebase.Search.Recipes;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.algolia.search.saas.Query;
import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceByParc;
import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.Profiles.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profiles.ViewModel.ProfileViewModel;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SingleRecipe.ViewRecipeActivity;
import com.example.nikis.bludogramfirebase.Recipes.db.RecipeToFavoriteEntity;
import com.example.nikis.bludogramfirebase.Search.Core.FilterAdapter;
import com.example.nikis.bludogramfirebase.Search.Parse.SearchResultJsonParser;
import com.example.nikis.bludogramfirebase.Search.Recipes.Filter.FilterDialog.FilterDialog;
import com.example.nikis.bludogramfirebase.Search.Recipes.Filter.RecipeFilterAdapter;
import com.example.nikis.bludogramfirebase.Search.Recipes.Filter.RecipeFilterData;
import com.example.nikis.bludogramfirebase.Search.Recipes.Item.SearchRecipeItem;
import com.example.nikis.bludogramfirebase.Search.Recipes.Parse.RecipesResultParser;
import com.example.nikis.bludogramfirebase.Search.SearchListFragment;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchRecipesFragmentTest extends SearchListFragment<RecipeData, SearchRecipeItem, RecipeFilterData> {
    private static final String ALGOLIA_INDEX_NAME = "recipes";
    private static final int REQUEST_CODE_FILTER = 7;

    private ProfileViewModel mProfileViewModel;

    private FilterDialog mFilterDialog;

    public static Fragment getInstance(RecipeFilterData filterData) {
        Fragment fragment = new SearchRecipesFragmentTest();

        fragment.setArguments(SearchListFragment.getArgs(filterData));

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFilterDialog = new FilterDialog();
        mFilterDialog.setTargetFragment(this, REQUEST_CODE_FILTER);

        mProfileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);

        mProfileViewModel.getResourceLiveData().observe(this, res -> {
            if (res != null) {
                if (res.status == ParcResourceByParc.Status.SUCCESS) {
                    super.filterAdapter.getData().subscriptions = getSubscriptionsList(res.data.subscriptions);
                }
            }
        });
    }

    private List<String> getSubscriptionsList(Map<String, Boolean> subscrMap) {
        List<String> subscrList = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : subscrMap.entrySet()) {
            if (entry.getValue()) {
                subscrList.add(entry.getKey());
            }
        }
        return subscrList;
    }

    @Override
    protected void showFilter(View filterBtn) {
        mFilterDialog.setArguments(FilterDialog.getArgs(filterAdapter.getData()));

        mFilterDialog.show(getFragmentManager(), "filterDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FILTER && resultCode == FilterDialog.RESULT_CODE_FILTER_APPLY) {
            RecipeFilterData filterData = data.getParcelableExtra(FilterDialog.FILTER_DATA);

            ParcResourceByParc<ProfileData> resProfileData = mProfileViewModel.getResourceLiveData().getValue();
            if (resProfileData != null && resProfileData.status == ParcResourceByParc.Status.SUCCESS) {
                filterData.subscriptions = getSubscriptionsList(resProfileData.data.subscriptions);
            }

            if (filterData != null) {
                super.updateFilterData(filterData);

                super.emptySearch();
            }
        }
    }

    @NonNull
    @Override
    public String getIndexName() {
        return ALGOLIA_INDEX_NAME;
    }

    @NonNull
    @Override
    public FilterAdapter<RecipeFilterData> getFilterAdapterInstance() {
        return new RecipeFilterAdapter();
    }

    @NonNull
    @Override
    public Query getBaseQuery() {
        return new Query()
                .setRestrictSearchableAttributes("overviewData.name");
    }

    @NonNull
    @Override
    protected SearchResultJsonParser<RecipeData> getParserInstance() {
        return new RecipesResultParser();
    }

    @NonNull
    @Override
    public SearchRecipeItem[] getItems(@NonNull List<RecipeData> dataList) {
        SearchRecipeItem[] searchRecipeItems = new SearchRecipeItem[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            searchRecipeItems[i] = new SearchRecipeItem(dataList.get(i));
        }
        return searchRecipeItems;
    }

    @Override
    public ClickEventHook<SearchRecipeItem> getClickEventHookInstance() {
        return (new ClickEventHook<SearchRecipeItem>() {

            @Nullable
            @Override
            public List<View> onBindMany(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof SearchRecipeItem.ViewHolder) {
                    List<View> views = new ArrayList<>();

                    SearchRecipeItem.ViewHolder castViewHolder = (SearchRecipeItem.ViewHolder) viewHolder;

                    views.add(castViewHolder.itemView.findViewById(R.id.imageBtn_star));

                    return views;

                } else return null;
            }

            @Override
            public void onClick(@NonNull View v, int position,
                                @NonNull FastAdapter<SearchRecipeItem> fastAdapter,
                                @NonNull SearchRecipeItem item) {
                switch (v.getId()) {
                    case R.id.imageBtn_star:

                        RecipeData recipeData = item.getRecipeData();

                        FirebaseHelper.Favorite.updateFavorite((App) getActivity().getApplication(),
                                new RecipeToFavoriteEntity(recipeData.recipeKey, recipeData.authorUId));

                        FirebaseHelper.Favorite.updateRecipeDataAndDBAfterFavoriteChange(
                                (App) getActivity().getApplication(),
                                recipeData);

                        item.setRecipeData(recipeData)
                                .updateFavoriteImage()
                                .updateStarCount();


                        break;
                }
            }
        });
    }

    @Override
    public OnClickListener<SearchRecipeItem> getClickItemListenerInstance() {
        return (v, adapter, item, position) -> {
            startActivity(ViewRecipeActivity.getIntent(
                    getActivity(),
                    item.getRecipeData().recipeKey,
                    FirebaseHelper.getUid().equals(item.getRecipeData().authorUId))
            );
            return true;
        };
    }
}
