package com.xando.chefsclub.Recipes.ViewRecipes.SubsciptionsRecipes.Repository;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.Helpers.FirebaseHelper;
import com.xando.chefsclub.Recipes.ViewRecipes.SubsciptionsRecipes.Data.RecipeIdData;
import com.xando.chefsclub.Recipes.ViewRecipes.SubsciptionsRecipes.Data.UserIdData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubscriptionsRecipesRepository {

    private static final String TAG = "SubscriptionsRecipesRep";

    private volatile long userCount = 0;

    private volatile long successRecipesLoaded = 0;

    private final List<RecipeIdData> recipeIdDataList = new ArrayList<>();

    private MutableLiveData<List<RecipeIdData>> toData;

    public void loadData(MutableLiveData<List<RecipeIdData>> toData) {
        this.toData = toData;

        toData.setValue(null);

        DatabaseReference databaseReference = FirebaseReferences.getDataBaseReference();

        databaseReference.child("users").orderByChild("subscribers/" +
                FirebaseHelper.getUid()).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        recipeIdDataList.clear();

                        userCount = dataSnapshot.getChildrenCount();

                        if (userCount == 0) {
                            toData.postValue(recipeIdDataList);

                            return;
                        }

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            UserIdData userIdData = snapshot.getValue(UserIdData.class);

                            if (userIdData == null) continue;

                            DatabaseReference userRef = FirebaseReferences.getDataBaseReference();

                            userRef.child("user-recipes").
                                    child(userIdData.userUid)
                                    .limitToLast(35)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                RecipeIdData recipeIdData = snapshot.getValue(RecipeIdData.class);

                                                if (recipeIdData == null) continue;

                                                recipeIdDataList.add(recipeIdData);
                                            }

                                            successRecipesLoaded++;

                                            if (successRecipesLoaded == userCount) {
                                                sortAndReturn();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sortAndReturn() {
        new Thread(() -> {
            Collections.sort(recipeIdDataList, (o1, o2) -> Long.compare(o1.dateTime, o2.dateTime));

            toData.postValue(recipeIdDataList);

        }).start();
    }
}
