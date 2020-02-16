package com.xando.chefsclub.firebaseList;

import android.arch.lifecycle.LifecycleObserver;
import android.support.annotation.NonNull;

import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.ObservableSnapshotArray;

public interface FirebaseAdapter<T> extends ChangeEventListener, LifecycleObserver {

    void startListening();

    void stopListening();

    @NonNull
    ObservableSnapshotArray<T> getSnapshots();
}
