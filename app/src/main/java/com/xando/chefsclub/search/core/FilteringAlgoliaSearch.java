package com.xando.chefsclub.search.core;

import android.support.annotation.NonNull;

public interface FilteringAlgoliaSearch<FilterData extends BaseFilterData> extends AlgoliaSearchCreator {

    @NonNull
    FilterAdapter<FilterData> getFilterAdapterInstance();
}
