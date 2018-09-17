package com.example.nikis.bludogramfirebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseReferences {
    public static StorageReference getStorageReference(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReference();
    }
    public static StorageReference getStorageReference(String ref){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReference(ref);
    }
    public static DatabaseReference getDataBaseReference(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference();
    }
    public static DatabaseReference getSyncDataBaseReference(){

        DatabaseReference dr = getDataBaseReference();
        dr.keepSynced(true);
        return dr;
    }
}
