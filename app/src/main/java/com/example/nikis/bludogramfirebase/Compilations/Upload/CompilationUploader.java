package com.example.nikis.bludogramfirebase.Compilations.Upload;

import com.example.nikis.bludogramfirebase.Compilations.Data.CompilationData;
import com.example.nikis.bludogramfirebase.DataWorkers.DataUploader;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceByParc;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class CompilationUploader extends DataUploader<CompilationData> {
    @Override
    protected void start() {
        mDataResource = ParcResourceByParc.loading(mData);

        super.updateProgress(mDataResource);

        updateChildren();
    }

    private void updateChildren() {
        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();


        if (mData.compilationKey == null) {
            mData.compilationKey = myRef.child("compilations").push().getKey();
        }

        Map<String, Object> childUpdates = createChildUpdates();

        myRef.updateChildren(childUpdates, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                mDataResource = ParcResourceByParc.success(mData);
            } else {
                mDataResource = ParcResourceByParc.error(databaseError.toException(), mData);
            }

            updateProgress(mDataResource);
        });
    }

    private Map<String, Object> createChildUpdates() {

        Map<String, Object> postValues = mData.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/compilations/" + mData.compilationKey, postValues);

        return childUpdates;
    }

    @Override
    protected boolean checkRelevance(CompilationData data) {
        return false;
    }

    @Override
    protected void cancel() {

    }
}
