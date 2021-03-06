package com.xando.chefsclub.search.profiles

import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.algolia.search.saas.Query
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.listeners.OnClickListener
import com.xando.chefsclub.R
import com.xando.chefsclub.profiles.data.ProfileData
import com.xando.chefsclub.profiles.viewprofiles.single.ViewProfileActivityTest
import com.xando.chefsclub.search.SearchListFragment
import com.xando.chefsclub.search.profiles.filter.ProfileFilterAdapter
import com.xando.chefsclub.search.profiles.filter.ProfileFilterData
import com.xando.chefsclub.search.profiles.item.SearchProfilesItem
import com.xando.chefsclub.search.profiles.parser.ProfilesResultParser

class SearchProfilesFragment : SearchListFragment<ProfileData, SearchProfilesItem, ProfileFilterData>() {

    private lateinit var popupMenu: PopupMenu

    override fun showFilter(filterBtn: View) {
        if (::popupMenu.isInitialized.not()) {
            initFilter(filterBtn)
        }
        popupMenu.show()
    }

    private fun initFilter(filterBtn: View) {
        popupMenu = PopupMenu(activity!!, filterBtn)
        popupMenu.inflate(R.menu.profiles_filter_menu)

        val idToCheck = when (filterAdapter.data.searchFrom) {
            ProfileFilterData.FROM_ALL_PROFILES -> R.id.choose_search_from_all_profiles
            ProfileFilterData.FROM_SUBSCRIPTIONS -> R.id.choose_search_from_subscriptions
            else -> throw UnsupportedOperationException("Unsupported ${filterAdapter.data.searchFrom} for Filter menu")
        }

        popupMenu.menu.findItem(idToCheck).isChecked = true

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            val isCheckedNow: Boolean

            val searchFrom = when (item.itemId) {
                R.id.choose_search_from_all_profiles -> {
                    item.isChecked = !item.isChecked.also { isCheckedNow = it }
                    popupMenu.menu.findItem(R.id.choose_search_from_subscriptions).isChecked = isCheckedNow
                    if (isCheckedNow) ProfileFilterData.FROM_SUBSCRIPTIONS else ProfileFilterData.FROM_ALL_PROFILES
                }
                R.id.choose_search_from_subscriptions -> {
                    item.isChecked = !item.isChecked.also { isCheckedNow = it }
                    popupMenu.menu.findItem(R.id.choose_search_from_all_profiles).isChecked = isCheckedNow
                    if (isCheckedNow) ProfileFilterData.FROM_ALL_PROFILES else ProfileFilterData.FROM_SUBSCRIPTIONS
                }
                else -> throw UnsupportedOperationException("Unsupported ${item.itemId} for click id")
            }

            updateFilterData(filterAdapter.data.setSearchFrom(searchFrom))
            emptySearch()

            true
        }
    }

    override fun getIndexName() = ALGOLIA_INDEX_NAME

    override fun getFilterAdapterInstance() = ProfileFilterAdapter()

    override fun getBaseQuery() = Query()

    override val searchResultJsonParser = ProfilesResultParser()

    override fun getItems(dataList: List<ProfileData>) =
            dataList.map { SearchProfilesItem(it) }

    override val clickEventHook: ClickEventHook<IItem<out Any, out RecyclerView.ViewHolder>>? = null

    override val clickItemListener: OnClickListener<IItem<out Any, out RecyclerView.ViewHolder>> =
            OnClickListener { v: View?, adapter: IAdapter<IItem<out Any, out RecyclerView.ViewHolder>>, item: IItem<out Any, out RecyclerView.ViewHolder>, position: Int ->
                if (item.type == R.id.search_profiles_item)
                    startActivity(ViewProfileActivityTest
                            .getIntent(activity, (item as SearchProfilesItem).data.userUid))
                true
            }

    companion object {

        private const val ALGOLIA_INDEX_NAME = "profiles"

        @JvmStatic
        fun getInstance(filterData: ProfileFilterData?): Fragment {
            val fragment: Fragment = SearchProfilesFragment()
            fragment.arguments = getArgs(filterData)
            return fragment
        }
    }
}