package com.xando.chefsclub.profiles.viewprofiles.subscriptions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.xando.chefsclub.helper.FirebaseHelper;

public class UserSubscriptionsFragment extends SubscriptionsListFragment {

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("users").orderByChild("subscribers/" + FirebaseHelper.getUid()).equalTo(true);
    }
}
