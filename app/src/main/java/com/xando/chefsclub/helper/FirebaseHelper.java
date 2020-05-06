package com.xando.chefsclub.helper;

import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.Nullable;

public class FirebaseHelper {

    @Nullable
    public static String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else return null;
    }
}
