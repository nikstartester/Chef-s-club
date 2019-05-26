package com.example.nikis.bludogramfirebase.Search.Profiles.Filter;

import com.algolia.search.saas.Query;
import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.Search.Core.FilterAdapter;

import static com.example.nikis.bludogramfirebase.Search.Profiles.Filter.ProfileFilterData.FROM_SUBSCRIPTIONS;

public class ProfileFilterAdapter extends FilterAdapter<ProfileFilterData> {
    @Override
    public FilterAdapter<ProfileFilterData> setEmptyData() {
        super.data = new ProfileFilterData();

        return this;
    }

    @Override
    public Query addToQuery(Query query) {
        String filterSearchFrom = "";

        if (data.searchFrom == FROM_SUBSCRIPTIONS) {
            filterSearchFrom += "subscribers." + FirebaseHelper.getUid() + ":true";
        }

        query.setFilters(filterSearchFrom);

        return query;
    }
}
