package com.example.nikis.bludogramfirebase.Search.Core;

import android.support.annotation.NonNull;

public interface FilteringAlgoliaSearch<FilterData extends BaseFilterData> extends AlgoliaSearchCreator {
    @NonNull
    FilterAdapter<FilterData> getFilterAdapterInstance();
}
