package com.xando.chefsclub.Search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.viewmodels.SearchBoxViewModel;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.DataWorkers.BaseData;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Search.Core.BaseFilterData;
import com.xando.chefsclub.Search.Core.DataToItemsSetter;
import com.xando.chefsclub.Search.Core.FilterAdapter;
import com.xando.chefsclub.Search.Core.FilteringAlgoliaSearch;
import com.xando.chefsclub.Search.Core.IData;
import com.xando.chefsclub.Search.Core.ItemsClickListenerGetter;
import com.xando.chefsclub.Search.Parse.SearchResultJsonParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public abstract class SearchListFragment<Data extends BaseData, Item extends AbstractItem & IData<Data>,
        FilterData extends BaseFilterData> extends Fragment implements DataToItemsSetter<Data, Item>,
        ItemsClickListenerGetter<Item>, FilteringAlgoliaSearch<FilterData> {

    private static final String KEY_QUERY = "SearchListFragment.keyQuery";
    private static final String KEY_FILTER_DATA = "SearchListFragment.keyFilterData";
    private static final String KEY_DATA_LIST = "SearchListFragment.keyDataList";
    private static final String KEY_RESULT_INFO = "SearchListFragment.keyResultInfo";

    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;
    @BindView(R.id.btn_load_more)
    protected Button loadMoreButton;
    @BindView(R.id.root_scroll)
    protected NestedScrollView scrollView;
    @BindView(R.id.result_info)
    protected TextView resultInfo;
    @BindView(R.id.imageBtn_search_filter)
    protected ImageButton searchFilterBtn;

    protected FilterAdapter<FilterData> filterAdapter;
    private FastItemAdapter<Item> mAdapter;
    private Searcher mSearcher;
    private String mQuery;
    private String mResultInfo;
    private boolean isToShowFiter;

    public static Bundle getArgs(BaseFilterData filterData) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(KEY_FILTER_DATA, filterData);

        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filterAdapter = getFilterAdapterInstance();
        mAdapter = new FastItemAdapter<>();

        mSearcher = Searcher.create(
                Constants.AlgoliaSearch.ALGOLIA_APP_ID,
                Constants.AlgoliaSearch.ALGOLIA_SEARCH_API_KEY,
                getIndexName()
        );

        FilterData mFilterData = null;

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(KEY_QUERY);

            mFilterData = savedInstanceState.getParcelable(KEY_FILTER_DATA);
        } else {
            if (getArguments() != null) {
                mFilterData = getArguments().getParcelable(KEY_FILTER_DATA);
                if (mFilterData != null) isToShowFiter = true;

            }
        }

        if (mFilterData == null)
            filterAdapter.setEmptyData();
        else filterAdapter.setData(mFilterData);

        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setAdapter(mAdapter);

        mSearcher.setQuery(getBaseQuery());

        mSearcher.registerResultListener((results, isLoadingMore) -> {
            mQuery = results.query;

            if (TextUtils.isEmpty(mQuery) || mQuery.equals("")) {
                mResultInfo = "Enter the text for search";
            } else {
                if (results.nbHits > 1) mResultInfo = results.nbHits + " results found";
                else if (results.nbHits == 1) mResultInfo = results.nbHits + " result found";
                else mResultInfo = "Nothing found";
            }

            resultInfo.setText(mResultInfo);

            if (results.nbHits > results.hitsPerPage && !results.page.equals(results.nbPages - 1)) {
                loadMoreButton.setVisibility(View.VISIBLE);
            } else loadMoreButton.setVisibility(View.GONE);


            List<Data> dataList = getParserInstance().parseResults(results.content);

            if (!isLoadingMore) {
                mAdapter.clear();
            }


            Item[] searchItems = getItems(dataList);

            if (isLoadingMore) {
                /*
                Else when add items scrollView scroll to top!
                 */
                recyclerView.setVisibility(View.INVISIBLE);

                mAdapter.add(searchItems);

                recyclerView.setVisibility(View.VISIBLE);
            } else {
                mAdapter.add(searchItems);
            }

        });

        ClickEventHook<Item> clickEventHook = getClickEventHookInstance();
        if (clickEventHook != null)
            mAdapter.withEventHook(clickEventHook);

        OnClickListener<Item> clickListener = getClickItemListenerInstance();
        if (clickListener != null)
            mAdapter.withOnClickListener(getClickItemListenerInstance());

        if (savedInstanceState != null) {
            List<Data> dataList = savedInstanceState.getParcelableArrayList(KEY_DATA_LIST);
            if (dataList != null) {
                mAdapter.add(getItems(dataList));
            }

            mResultInfo = savedInstanceState.getString(KEY_RESULT_INFO);
            if (mResultInfo != null) resultInfo.setText(mResultInfo);
        }

        return view;
    }

    @OnClick(R.id.btn_load_more)
    protected void loadMore() {
        mSearcher.loadMore();
    }

    @OnClick(R.id.imageBtn_search_filter)
    protected void showFilterClick(View view) {

        showFilter(view);
    }

    protected void updateFilterData(@NonNull FilterData filterData) {
        filterAdapter.setData(filterData);

        mSearcher.setQuery(filterAdapter.addToQuery(filterData, mSearcher.getQuery()));
    }

    protected void updateFilter() {
        mSearcher.setQuery(filterAdapter.addToQuery(mSearcher.getQuery()));
    }

    protected void emptySearch() {
        if (mQuery != null && !mQuery.equals("") && !TextUtils.isEmpty(mQuery))
            mSearcher.search(mQuery);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchBox searchView = (SearchBox) myActionMenuItem.getActionView();

        if (mQuery != null) searchView.setQuery(mQuery, true);

        InstantSearch helper = new InstantSearch(getActivity(), mSearcher, this);
        //mHelper = new InstantSearch(getActivity(), menu, R.id.action_search, mSearcher);

        SearchBoxViewModel searchBoxViewModel = new SearchBoxViewModel(searchView);
        helper.registerSearchView(getActivity(), searchBoxViewModel);
        helper.enableProgressBar();
        helper.setSearchOnEmptyString(false);

        if (isToShowFiter) {
            mSearcher.setQuery(filterAdapter.addToQuery(mSearcher.getQuery()));
            emptySearch();

            showFilter(searchFilterBtn);

            isToShowFiter = false;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(KEY_DATA_LIST, getDataList());
        outState.putString(KEY_QUERY, mQuery);
        outState.putParcelable(KEY_FILTER_DATA, filterAdapter.getData());
        outState.putString(KEY_RESULT_INFO, mResultInfo);

        super.onSaveInstanceState(outState);
    }

    private ArrayList<Data> getDataList() {
        ArrayList<Data> dataList = new ArrayList<>();

        for (Item item : mAdapter.getAdapterItems()) {
            dataList.add(item.getData());
        }

        return dataList;
    }

    protected abstract void showFilter(View filterBtn);

    @NonNull
    protected abstract SearchResultJsonParser<Data> getParserInstance();

}
