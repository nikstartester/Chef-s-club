package com.xando.chefsclub.Profiles.ViewProfiles.Subscriptions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.xando.chefsclub.Helpers.FirebaseHelper;

public class UserSubscriptionsFragment extends SubscriptionsListFragment {

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("users").orderByChild("subscribers/" + FirebaseHelper.getUid()).equalTo(true);
    }
}
