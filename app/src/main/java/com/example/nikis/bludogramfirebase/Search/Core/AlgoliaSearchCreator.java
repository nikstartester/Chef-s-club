package com.example.nikis.bludogramfirebase.Search.Core;

import android.support.annotation.NonNull;

import com.algolia.search.saas.Query;

public interface AlgoliaSearchCreator {
    @NonNull
    String getIndexName();

    @NonNull
    Query getBaseQuery();
}
