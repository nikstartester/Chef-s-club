package com.xando.chefsclub.firebaseList;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;

import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.ObservableSnapshotArray;

public interface FirebaseAdapter<T> extends ChangeEventListener, LifecycleObserver {

    void startListening();

    void stopListening();

    @NonNull
    ObservableSnapshotArray<T> getSnapshots();
}
