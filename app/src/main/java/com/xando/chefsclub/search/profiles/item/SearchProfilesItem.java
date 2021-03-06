package com.xando.chefsclub.search.profiles.item;

import android.view.View;

import androidx.annotation.NonNull;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.R;
import com.xando.chefsclub.profiles.data.ProfileData;
import com.xando.chefsclub.profiles.viewprofiles.subscriptions.viewholder.SubscriptionViewHolder;
import com.xando.chefsclub.search.core.IData;

import java.util.List;

public class SearchProfilesItem extends AbstractItem<SearchProfilesItem, SubscriptionViewHolder>
        implements IData<ProfileData> {

    private ProfileData mProfileData;

    public SearchProfilesItem(ProfileData profileData) {
        mProfileData = profileData;
    }

    @Override
    public void bindView(@NonNull SubscriptionViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.bind(mProfileData);
    }

    @NonNull
    @Override
    public SubscriptionViewHolder getViewHolder(@NonNull View v) {
        return new SubscriptionViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.search_profiles_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_subscription_item;
    }

    @Override
    public ProfileData getData() {
        return mProfileData;
    }

    @Override
    public void setData(ProfileData data) {
        mProfileData = data;
    }
}
