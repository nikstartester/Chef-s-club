package com.example.nikis.bludogramfirebase.Search.Profiles.Item;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.nikis.bludogramfirebase.Profiles.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profiles.ViewProfiles.Subscriptions.ViewHolder.SubscriptionViewHolder;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Search.Core.IData;
import com.mikepenz.fastadapter.items.AbstractItem;

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
