package com.xando.chefsclub.Search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.algolia.instantsearch.core.helpers.Searcher
import com.algolia.instantsearch.core.model.SearchResults
import com.algolia.instantsearch.ui.helpers.InstantSearch
import com.algolia.instantsearch.ui.views.SearchBox
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.listeners.OnClickListener
import com.xando.chefsclub.Constants.Constants
import com.xando.chefsclub.DataWorkers.BaseData
import com.xando.chefsclub.Helpers.NetworkHelper
import com.xando.chefsclub.List.GroupAdapter
import com.xando.chefsclub.List.MultiGroupsRecyclerViewAdapterImpl
import com.xando.chefsclub.List.SingleItemGroupAdapter
import com.xando.chefsclub.R
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.MoreItem
import com.xando.chefsclub.Search.Core.*
import com.xando.chefsclub.Search.Parse.SearchResultJsonParser
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_search_list.*
import kotlinx.android.synthetic.main.fragment_search_list.view.*

private const val ITEMS_ADAPTER_ID = 10
private const val MORE_ADAPTER_ID = 11

abstract class SearchListFragmentNew<Data : BaseData,
        Item,
        FilterData : BaseFilterData> :
        Fragment(),
        DataToItemsSetter<Data, Item>,
        ItemsClickListenerGetter<IItem<out Any, out RecyclerView.ViewHolder>>,
        FilteringAlgoliaSearch<FilterData> where Item : IItem<out Any, out RecyclerView.ViewHolder>,
                                                 Item : IData<Data>? {

    protected lateinit var recyclerView: RecyclerView
    private lateinit var resultInfoView: TextView
    private lateinit var searchFilterView: View

    private var delayedSearchBoxViewModel: DelayedSearchBoxViewModel? = null

    protected val filterAdapter: FilterAdapter<FilterData?> = getFilterAdapterInstance()

    private var searcher: Searcher = Searcher.create(
            Constants.AlgoliaSearch.ALGOLIA_APP_ID,
            Constants.AlgoliaSearch.ALGOLIA_SEARCH_API_KEY,
            indexName)

    private val multiGroupsRecyclerViewAdapter = MultiGroupsRecyclerViewAdapterImpl()

    private val fastAdapter = multiGroupsRecyclerViewAdapter.getFastAdapter()

    private val itemsAdapter = GroupAdapter<Item>(ITEMS_ADAPTER_ID, multiGroupsRecyclerViewAdapter)
    private val moreAdapter = SingleItemGroupAdapter<MoreItem>(MORE_ADAPTER_ID, multiGroupsRecyclerViewAdapter)

    private var query = ""
    private var lastResultQuery = ""
    private var resultInfo = EMPTY_RESULT_INFO

    private var isToShowFilter = false

    private var isNeedSearchAfterMenuCreated = false

    private val disposer = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filterAdapter.apply {
            if (savedInstanceState != null) {
                query = savedInstanceState.getString(KEY_QUERY, "")
                lastResultQuery = savedInstanceState.getString(KEY_SEARCHER_QUERY, "")
                data = savedInstanceState.getParcelable(KEY_FILTER_DATA)!!
            } else {
                (arguments?.getParcelable<FilterData>(KEY_FILTER_DATA))?.let {
                    data = it
                    isToShowFilter = true
                } ?: setEmptyData()
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_list, container, false)

        initViews(view)

        recyclerView.adapter = multiGroupsRecyclerViewAdapter

        initSearcher()

        savedInstanceState?.let {
            resultInfo = it.getString(KEY_RESULT_INFO, "")
            resultInfoView.text = resultInfo

            isNeedSearchAfterMenuCreated = true
        }

        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.recycler_view
        resultInfoView = view.result_info
        searchFilterView = view.imageBtn_search_filter

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()

        multiGroupsRecyclerViewAdapter.addGroups(listOf(itemsAdapter.groupId, moreAdapter.groupId))

        view.swipe_refresh.setColorSchemeResources(R.color.colorAccent)

        initClicks(view)
    }

    private fun initSearcher() {
        searcher.query = baseQuery
        searcher.query.setQuery(lastResultQuery)

        searcher.registerResultListener { results: SearchResults, isLoadingMore: Boolean ->
            swipe_refresh?.isRefreshing = false

            lastResultQuery = results.query

            if (query.isNotEmpty()) {
                resultInfo = if (lastResultQuery.isEmpty()) EMPTY_RESULT_INFO
                else results.nbHits.let {
                    if (it == 0) getString(R.string.nothing_found)
                    else
                        resources.getQuantityString(
                                R.plurals.search_result_plurals,
                                results.nbHits,
                                results.nbHits
                        )
                }

                resultInfoView.text = resultInfo


                val dataList = searchResultJsonParser.parseResults(results.content)

                if (!isLoadingMore) {
                    multiGroupsRecyclerViewAdapter.clear()
                }

                itemsAdapter.addItems(getItems(dataList))

                if (results.nbHits > results.hitsPerPage && results.page != results.nbPages - 1) {
                    moreAdapter.setItem(MoreItem("More"))
                } else moreAdapter.removeItem()
            }
        }
    }

    private fun initClicks(view: View) {
        view.imageBtn_search_filter.setOnClickListener { showFilter(it) }

        clickEventHook?.let { fastAdapter.withEventHook(it as ClickEventHook<IItem<Any, RecyclerView.ViewHolder>>) }

        fastAdapter.withOnClickListener(clickItemListener as OnClickListener<IItem<Any, RecyclerView.ViewHolder>>)

        fastAdapter.withEventHook(object : ClickEventHook<IItem<Any, RecyclerView.ViewHolder>>() {

            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): MutableList<View> {
                val list = mutableListOf<View>()
                when (viewHolder) {
                    is MoreItem.MoreViewHolder -> list.add(viewHolder.moreView)
                }
                return list
            }

            override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<IItem<Any, RecyclerView.ViewHolder>>,
                                 item: IItem<Any, RecyclerView.ViewHolder>) {
                when (v.id) {
                    R.id.btn_more -> {
                        searcher.loadMore()
                    }
                }
            }
        })

        view.swipe_refresh.setOnRefreshListener {
            if (getSearcherQuery().isEmpty() || query.isEmpty() || NetworkHelper.isConnected(context!!).not())
                view.swipe_refresh.isRefreshing = false
            else {
                updateFilter()
                searcher.search()
            }
        }
    }

    protected fun updateFilterData(filterData: FilterData) {
        filterAdapter.data = filterData
        searcher.query = filterAdapter.addToQuery(filterData, searcher.query)
    }

    protected fun updateFilter() {
        searcher.query = filterAdapter.addToQuery(searcher.query)
    }

    protected fun emptySearch(fromSavedInstance: Boolean = false) {
        when {
            fromSavedInstance -> if (lastResultQuery.isNotEmpty() && query.isNotEmpty()) searcher.search()
            getSearcherQuery().isNotEmpty() && query.isNotEmpty() -> searcher.search()
        }
    }

    private fun getSearcherQuery() = searcher.query.query ?: ""

    private fun String.checkToSubmit() = trim().replace(" +".toRegex(), " ").length > 2

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchBox = menu.findItem(R.id.searchBox).actionView as SearchBox
        searchBox.setQuery(query, false)

        activity?.let {
            InstantSearch(searcher).apply {
                registerSearchView(it, DelayedSearchBoxViewModel(searchBox).also { delayedSearchBoxViewModel = it })
                enableProgressBar(150)
                setSearchOnEmptyString(false)
            }

            disposer.add(delayedSearchBoxViewModel!!.addTextListener { text ->
                query = text
                if (text.isEmpty()) {
                    resultInfoView.text = EMPTY_RESULT_INFO.also { resultInfo = it }
                    lastResultQuery = ""
                    multiGroupsRecyclerViewAdapter.clear()
                }
            })
        }

        if (isNeedSearchAfterMenuCreated || isToShowFilter) {
            updateFilter()
            emptySearch(true)

            if (isToShowFilter) showFilter(searchFilterView)

            isToShowFilter = false
            isNeedSearchAfterMenuCreated = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_QUERY, query)
        outState.putString(KEY_SEARCHER_QUERY, lastResultQuery)
        outState.putParcelable(KEY_FILTER_DATA, filterAdapter.data)
        outState.putString(KEY_RESULT_INFO, resultInfo)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        disposer.dispose()
        delayedSearchBoxViewModel?.clear()
        super.onDestroyView()
    }

    override fun onDestroy() {
        disposer.dispose()
        super.onDestroy()
    }

    protected abstract fun showFilter(filterBtn: View)

    protected abstract val searchResultJsonParser: SearchResultJsonParser<Data>

    companion object {

        private const val EMPTY_RESULT_INFO = "Enter the text for search"
        private const val KEY_QUERY = "SearchListFragment.keyQuery"
        private const val KEY_SEARCHER_QUERY = "KEY_SEARCHER_QUERY"
        private const val KEY_FILTER_DATA = "SearchListFragment.keyFilterData"
        private const val KEY_RESULT_INFO = "SearchListFragment.keyResultInfo"

        fun getArgs(filterData: BaseFilterData?): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(KEY_FILTER_DATA, filterData)
            return bundle
        }
    }
}