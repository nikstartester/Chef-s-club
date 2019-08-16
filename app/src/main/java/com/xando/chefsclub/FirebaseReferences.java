package com.xando.chefsclub;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseReferences {

    public static StorageReference getStorageReference() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReference();
    }

    public static StorageReference getStorageReference(String ref) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReference(ref);
    }

    public static DatabaseReference getDataBaseReference() {
        return getDataBaseReference(false);
    }

    public static DatabaseReference getSyncDataBaseReference() {
        return getDataBaseReference(true);
    }

    private static DatabaseReference getDataBaseReference(boolean keepSynced) {
        DatabaseReference dr = getStandardDataBaseReference();
        dr.keepSynced(keepSynced);

        return dr;
    }

    private static DatabaseReference getStandardDataBaseReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference();
    }
}
