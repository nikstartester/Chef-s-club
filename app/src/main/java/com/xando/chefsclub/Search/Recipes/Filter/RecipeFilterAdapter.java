package com.xando.chefsclub.Search.Recipes.Filter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.algolia.search.saas.Query;
import com.xando.chefsclub.Search.Core.FilterAdapter;

import static com.xando.chefsclub.Helpers.FirebaseHelper.getUid;
import static com.xando.chefsclub.Search.Recipes.Filter.RecipeFilterData.FROM_ALL_RECIPES;
import static com.xando.chefsclub.Search.Recipes.Filter.RecipeFilterData.FROM_FAVORITE;
import static com.xando.chefsclub.Search.Recipes.Filter.RecipeFilterData.FROM_MY_RECIPES;
import static com.xando.chefsclub.Search.Recipes.Filter.RecipeFilterData.FROM_SUBSCRIPTIONS;

public class RecipeFilterAdapter extends FilterAdapter<RecipeFilterData> {
    private static final String TAG = "RecipeFilterAdapter";

    public RecipeFilterAdapter() {
    }

    @Override
    public FilterAdapter<RecipeFilterData> setEmptyData() {
        super.data = new RecipeFilterData();

        return this;
    }

    @Override
    public Query addToQuery(Query query) {

        String filterSearchFrom = getSearchFrom();

        String filterCategories = getCategories();

        String filterTime = getTime();

        String resultFilter = "";

        if (!filterSearchFrom.equals("")) resultFilter += "( " + filterSearchFrom + " )";

        if (!filterCategories.equals("") && !resultFilter.equals("")) resultFilter += " AND ";

        if (!filterCategories.equals("")) resultFilter += "(" + filterCategories + ")";

        if (!filterTime.equals("") && !resultFilter.equals("")) resultFilter += " AND ";

        if (!filterTime.equals("")) resultFilter += "(" + filterTime + ")";

        Log.d(TAG, "addToQuery: " + resultFilter);
        query.setFilters(resultFilter);

        return query;
    }

    @NonNull
    private String getSearchFrom() {
        StringBuilder filterSearchFrom = new StringBuilder();
        switch (data.searchFrom) {
            case FROM_ALL_RECIPES:
                break;
            case FROM_MY_RECIPES:
                filterSearchFrom.append("authorUId:\"").append(getUid()).append("\"");
                break;
            case FROM_FAVORITE:
                filterSearchFrom.append("stars.").append(getUid()).append(":true");
                break;
            case FROM_SUBSCRIPTIONS:
                for (int i = 0; i < data.subscriptions.size(); i++) {
                    String subscription = data.subscriptions.get(i);
                    filterSearchFrom.append("authorUId:" + "\"").append(subscription).append("\"");

                    if (i < data.subscriptions.size() - 1)
                        filterSearchFrom.append(" OR ");
                }
                break;
        }
        return filterSearchFrom.toString();
    }

    @NonNull
    private String getCategories() {
        StringBuilder filterCategories = new StringBuilder();
        for (int i = 0; i < data.categories.size(); i++) {
            String category = data.categories.get(i);
            {
                if (category != null) {
                    filterCategories.append("overviewData.strCategories:" + "\"").append(category).append("\"");

                    if (i < data.categories.size() - 1)
                        filterCategories.append(" OR ");
                }
            }
        }
        return filterCategories.toString();
    }

    @NonNull
    private String getTime() {
        String filterTime = "";

        if (data.minTime > 0 && data.maxTime > 0) {
            filterTime += "stepsData.timeMainNum:" + data.minTime + " TO " + data.maxTime;
        } else if (data.minTime > 0) {
            filterTime += "stepsData.timeMainNum >= " + data.minTime;
        } else if (data.maxTime > 0) {
            filterTime += "stepsData.timeMainNum <= " + data.maxTime;
        }

        return filterTime;
    }


}
