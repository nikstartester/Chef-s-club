package com.xando.chefsclub.Search.Core;

import android.support.annotation.NonNull;

import com.algolia.search.saas.Query;

public interface AlgoliaSearchCreator {

    @NonNull
    String getIndexName();

    @NonNull
    Query getBaseQuery();
}
