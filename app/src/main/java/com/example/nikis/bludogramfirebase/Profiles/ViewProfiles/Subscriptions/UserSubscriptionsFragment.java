package com.example.nikis.bludogramfirebase.Profiles.ViewProfiles.Subscriptions;

import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class UserSubscriptionsFragment extends SubscriptionsListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("users").orderByChild("subscribers/" + FirebaseHelper.getUid()).equalTo(true);
    }
}
