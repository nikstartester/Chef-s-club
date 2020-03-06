package com.xando.chefsclub.search.core;

import androidx.annotation.NonNull;

public interface FilteringAlgoliaSearch<FilterData extends BaseFilterData> extends AlgoliaSearchCreator {

    @NonNull
    FilterAdapter<FilterData> getFilterAdapterInstance();
}
