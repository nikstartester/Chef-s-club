package com.xando.chefsclub.search.core;

import androidx.annotation.NonNull;

import com.algolia.search.saas.Query;

public interface AlgoliaSearchCreator {

    @NonNull
    String getIndexName();

    @NonNull
    Query getBaseQuery();
}
