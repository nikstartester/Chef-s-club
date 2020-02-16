package com.xando.chefsclub.search.profiles.filter;

import com.algolia.search.saas.Query;
import com.xando.chefsclub.helper.FirebaseHelper;
import com.xando.chefsclub.search.core.FilterAdapter;

import static com.xando.chefsclub.search.profiles.filter.ProfileFilterData.FROM_SUBSCRIPTIONS;

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
