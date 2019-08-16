package com.xando.chefsclub.Search.Profiles.Filter;

import com.algolia.search.saas.Query;
import com.xando.chefsclub.Helpers.FirebaseHelper;
import com.xando.chefsclub.Search.Core.FilterAdapter;

import static com.xando.chefsclub.Search.Profiles.Filter.ProfileFilterData.FROM_SUBSCRIPTIONS;

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
