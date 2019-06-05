package com.xando.chefsclub.Search.Profiles;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import com.algolia.search.saas.Query;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.xando.chefsclub.Profiles.Data.ProfileData;
import com.xando.chefsclub.Profiles.ViewProfiles.SingleProfile.ViewProfileActivityTest;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Search.Core.FilterAdapter;
import com.xando.chefsclub.Search.Parse.SearchResultJsonParser;
import com.xando.chefsclub.Search.Profiles.Filter.ProfileFilterAdapter;
import com.xando.chefsclub.Search.Profiles.Filter.ProfileFilterData;
import com.xando.chefsclub.Search.Profiles.Item.SearchProfilesItem;
import com.xando.chefsclub.Search.Profiles.Parse.ProfilesResultParser;
import com.xando.chefsclub.Search.SearchListFragment;

import java.util.List;

import static com.xando.chefsclub.Search.Profiles.Filter.ProfileFilterData.FROM_ALL_PROFILES;
import static com.xando.chefsclub.Search.Profiles.Filter.ProfileFilterData.FROM_SUBSCRIPTIONS;

public class SearchProfilesFragment extends SearchListFragment<ProfileData, SearchProfilesItem, ProfileFilterData> {
    private static final String ALGOLIA_INDEX_NAME = "profiles";

    private PopupMenu mPopupMenu;

    public static Fragment getInstance(ProfileFilterData filterData) {
        Fragment fragment = new SearchProfilesFragment();

        fragment.setArguments(SearchListFragment.getArgs(filterData));

        return fragment;
    }

    @Override
    protected void showFilter(View filterBtn) {
        if (mPopupMenu == null) {
            mPopupMenu = new PopupMenu(getActivity(), filterBtn);

            mPopupMenu.inflate(R.menu.profiles_filter_menu);

            int idToCheck = -1;
            switch (filterAdapter.getData().getSearchFrom()) {
                case FROM_ALL_PROFILES:
                    idToCheck = R.id.choose_search_from_all_profiles;
                    break;
                case FROM_SUBSCRIPTIONS:
                    idToCheck = R.id.choose_search_from_subscriptions;
                    break;
            }

            if (idToCheck != -1) mPopupMenu.getMenu().findItem(idToCheck).setChecked(true);

            mPopupMenu.setOnMenuItemClickListener(item -> {
                boolean isCheckedNow;
                int searchFrom = -1;

                switch (item.getItemId()) {
                    case R.id.choose_search_from_all_profiles:
                        item.setChecked(!(isCheckedNow = item.isChecked()));
                        mPopupMenu.getMenu().findItem(R.id.choose_search_from_subscriptions).setChecked(isCheckedNow);

                        if (isCheckedNow) searchFrom = FROM_SUBSCRIPTIONS;
                        else searchFrom = FROM_ALL_PROFILES;
                        break;
                    case R.id.choose_search_from_subscriptions:
                        item.setChecked(!(isCheckedNow = item.isChecked()));
                        mPopupMenu.getMenu().findItem(R.id.choose_search_from_all_profiles).setChecked(isCheckedNow);

                        if (isCheckedNow) searchFrom = FROM_ALL_PROFILES;
                        else searchFrom = FROM_SUBSCRIPTIONS;
                        break;
                }

                if (searchFrom != -1) {
                    super.updateFilterData(filterAdapter.getData().setSearchFrom(searchFrom));

                    super.emptySearch();
                }
                return true;
            });
        }
        mPopupMenu.show();
    }

    @NonNull
    @Override
    public String getIndexName() {
        return ALGOLIA_INDEX_NAME;
    }

    @NonNull
    @Override
    public FilterAdapter<ProfileFilterData> getFilterAdapterInstance() {
        return new ProfileFilterAdapter();
    }

    @NonNull
    @Override
    public Query getBaseQuery() {
        return new Query();
    }

    @NonNull
    @Override
    protected SearchResultJsonParser<ProfileData> getParserInstance() {
        return new ProfilesResultParser();
    }

    @NonNull
    @Override
    public SearchProfilesItem[] getItems(@NonNull List<ProfileData> dataList) {
        SearchProfilesItem[] searchRecipeItems = new SearchProfilesItem[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            searchRecipeItems[i] = new SearchProfilesItem(dataList.get(i));
        }
        return searchRecipeItems;
    }

    @Nullable
    @Override
    public ClickEventHook<SearchProfilesItem> getClickEventHookInstance() {
        return null;
    }

    @Nullable
    @Override
    public OnClickListener<SearchProfilesItem> getClickItemListenerInstance() {
        return (v, adapter, item, position) -> {
            startActivity(ViewProfileActivityTest
                    .getIntent(getActivity(), item.getData().userUid));
            return true;
        };
    }

}
