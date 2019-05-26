package com.example.nikis.bludogramfirebase.Helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.Compilations.Data.CompilationData;
import com.example.nikis.bludogramfirebase.DataWorkers.BaseData;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.Profiles.Data.ProfileData;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipes.db.Converters.MapBoolConverter;
import com.example.nikis.bludogramfirebase.Recipes.db.RecipeDao;
import com.example.nikis.bludogramfirebase.Recipes.db.RecipeToFavoriteEntity;
import com.example.nikis.bludogramfirebase.Recipes.db.RecipesToFavoriteDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {

    @Nullable
    public static String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else return null;
    }

    public interface OnCompleteListener {
        void onComplete(DatabaseError databaseError, boolean b,
                        DataSnapshot dataSnapshot);
    }

    public static final class Favorite {
        private static RecipesToFavoriteDao sDao;

        public static void updateFavorite(App app, RecipeToFavoriteEntity model) {
            updateFavoriteOnServer(app, model);

            updateFavoriteInDB(app, model);
        }

        public static void updateFavoriteOnServer(App app, RecipeToFavoriteEntity model) {

            if (sDao == null) sDao = app.getDatabase().recipesToFavoriteDao();

            final DatabaseReference databaseReference = FirebaseReferences.getDataBaseReference();

            final String recipeKey = model.recipeKey;

            DatabaseReference globalRef = databaseReference
                    .child("recipes")
                    .child(recipeKey);

            DatabaseReference userRef = databaseReference
                    .child("user-recipes")
                    .child(model.authorId)
                    .child(recipeKey);

            // Run two transactions
            onStarClicked(globalRef);
            onStarClicked(userRef);
        }

        static void updateFavoriteInDB(App app, RecipeToFavoriteEntity model) {
            if (sDao == null) sDao = app.getDatabase().recipesToFavoriteDao();

            new Thread(() -> sDao.insert(new RecipeToFavoriteEntity(model.recipeKey, model.authorId))).start();
        }

        public static RecipeData updateRecipeDataWithFavoriteChange(RecipeData model) {
            return onStarClicked(model);
        }

        @Deprecated
        public static void updateRecipeDataAndDBAfterFavoriteChange(App app, RecipeData model) {
            final RecipeData recipeData = onStarClicked(model);

            updateDBAfterFavoriteChange(app, recipeData);
        }

        public static void updateDBAfterFavoriteChange(App app, RecipeData recipeData) {
            RecipeDao recipeDao = app.getDatabase().recipeDao();

            new Thread(() -> recipeDao.updateStar(
                    recipeData.recipeKey,
                    recipeData.starCount,
                    MapBoolConverter.fromStars(recipeData.stars),
                    true
            )).start();
        }

        private static RecipeData onStarClicked(RecipeData recipeData) {
            if (recipeData.stars.containsKey(getUid())) {

                recipeData.starCount = recipeData.starCount - 1;
                recipeData.stars.remove(getUid());

            } else {

                recipeData.starCount = recipeData.starCount + 1;
                recipeData.stars.put(getUid(), true);

            }
            return recipeData;
        }

        private static void onStarClicked(DatabaseReference ref) {
            ref.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    RecipeData recipeData = mutableData.getValue(RecipeData.class);
                    if (recipeData == null) {
                        return Transaction.success(mutableData);
                    }

                    recipeData = onStarClicked(recipeData);

                    // Set value and report transaction success
                    mutableData.setValue(recipeData);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    if (dataSnapshot == null) return;

                    final RecipeData recipeData = dataSnapshot.getValue(RecipeData.class);

                    if (sDao == null)
                        throw new NullPointerException("Dao must not be null! Check it!");

                    if (recipeData != null)
                        new Thread(() -> sDao.deleteByKey(recipeData.recipeKey)).start();
                }
            });
        }
    }

    public static final class Subscriptions {

        public static void updateSubscr(String currUserId, String toSubscrUserId) {

            final DatabaseReference databaseReference = FirebaseReferences.getDataBaseReference();

            DatabaseReference currUserRef = databaseReference
                    .child("users")
                    .child(currUserId);

            DatabaseReference toSubscrUserRef = databaseReference
                    .child("users")
                    .child(toSubscrUserId);

            onUpdate(currUserRef, Subscriptions::onSubscriptionsClick, toSubscrUserId);
            onUpdate(toSubscrUserRef, Subscriptions::onSubscribersClick, getUid());
        }

        private static void onUpdate(DatabaseReference ref, @NonNull OnTransactionMethod method, String userId) {
            ref.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    ProfileData profileData = mutableData.getValue(ProfileData.class);

                    if (profileData == null) return Transaction.success(mutableData);

                    profileData = method.doTransaction(profileData, userId);

                    mutableData.setValue(profileData);

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                }
            });
        }

        static ProfileData onSubscribersClick(ProfileData data, String userId) {
            userId = userId == null ? getUid() : userId;

            if (data.subscribers.containsKey(userId)) {

                data.subscribersCount--;
                data.subscribers.remove(userId);

            } else {

                data.subscribersCount++;
                data.subscribers.put(userId, true);

            }
            return data;
        }

        static ProfileData onSubscriptionsClick(ProfileData data, String userId) {

            if (userId == null) throw new NullPointerException("userId can not be null!");

            if (data.subscriptions.containsKey(userId)) {

                data.subscriptionsCount--;
                data.subscriptions.remove(userId);

            } else {

                data.subscriptionsCount++;
                data.subscriptions.put(userId, true);

            }
            return data;
        }

        interface OnTransactionMethod {
            ProfileData doTransaction(ProfileData data, String userId);
        }
    }

    public static final class Compilations {
        public static final class CompilationActions {

            private CompilationData mData;

            private String mRecipeKey;

            private final List<OnTransactionMethod<CompilationData>> onCompilationMethods = new ArrayList<>();

            public CompilationActions addToRecipe(CompilationData data, String recipeKey) {
                /*data.count++;

                data.recipesKey.add(recipeKey);*/

                checkThatRecipeAndDataEqualsOrNull(data, recipeKey);

                mRecipeKey = recipeKey;

                onCompilationMethods.add(this::addRecipe);

                mData = data;

                final DatabaseReference databaseReference = FirebaseReferences.getDataBaseReference();

                DatabaseReference globalRef = databaseReference
                        .child("recipes")
                        .child(recipeKey);

                OnTransactionMethod<RecipeData> method = this::addToRecipe;

                actToRecipe(globalRef, method);

                return this;
            }

            /*
            You can use any number of methods associated with changing the compilation if you use
            one recipe key and compilation. Otherwise use saveChangesOnCompilation() and use another
             recipeKey or/and compilation.
             */
            private void checkThatRecipeAndDataEqualsOrNull(CompilationData data, String recipeKey) {
                checkThatDataEqualsOrNull(data);
                checkThatRecipeEqualsOrNull(recipeKey);
            }

            private void checkThatRecipeEqualsOrNull(String recipeKey) {
                if (mRecipeKey == null) return;

                if (mRecipeKey.equals(recipeKey)) return;

                if (onCompilationMethods.size() > 0)
                    throw new IllegalArgumentException("Recipe key " +
                            "already used and methods not applying. Use saveChangesOnCompilation() at first");
            }

            private void checkThatDataEqualsOrNull(CompilationData data) {
                if (mData == null) return;

                if (mData.compilationKey.equals(data.compilationKey)) return;

                if (onCompilationMethods.size() > 0)
                    throw new IllegalArgumentException("Compilation " +
                            "already used and methods not applying. Use saveChangesOnCompilation() at first");
            }

            public CompilationActions removeFromRecipe(CompilationData data, String recipeKey) {

                /*if(data.recipesKey.remove(recipeKey)){
                    data.count --;
                }*/

                checkThatRecipeAndDataEqualsOrNull(data, recipeKey);

                mRecipeKey = recipeKey;

                mData = data;

                onCompilationMethods.add(this::removeRecipe);

                final DatabaseReference databaseReference = FirebaseReferences.getDataBaseReference();

                DatabaseReference globalRef = databaseReference
                        .child("recipes")
                        .child(recipeKey);

                OnTransactionMethod<RecipeData> method = this::removeFromRecipe;

                actToRecipe(globalRef, method);

                return this;
            }

            private void actToRecipe(DatabaseReference ref, OnTransactionMethod<RecipeData> method) {
                ref.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        RecipeData recipeData = mutableData.getValue(RecipeData.class);
                        if (recipeData == null) {
                            return Transaction.success(mutableData);
                        }

                        recipeData = method.doTransaction(recipeData);

                        // Set value and report transaction success
                        mutableData.setValue(recipeData);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                    }
                });
            }

            private RecipeData addToRecipe(RecipeData recipeData) {
                recipeData.inCompilations.put(mData.compilationKey, true);

                return recipeData;
            }

            private RecipeData removeFromRecipe(RecipeData recipeData) {
                recipeData.inCompilations.remove(mData.compilationKey);

                return recipeData;
            }

            public CompilationActions saveChangesOnCompilation() {
                for (OnTransactionMethod<CompilationData> method : onCompilationMethods) {
                    method.doTransaction(mData);
                }

                onCompilationMethods.clear();

                return this;
            }

            public CompilationActions updateCompilationOnServer() {
                final DatabaseReference databaseReference = FirebaseReferences.getDataBaseReference();

                databaseReference
                        .child("compilations")
                        .child(mData.compilationKey).setValue(mData);

                return this;
            }

            public CompilationActions removeRecipeFromCompilation(String compKey, String recipeKey) {
                mRecipeKey = recipeKey;

                final DatabaseReference databaseReference = FirebaseReferences.getDataBaseReference();

                DatabaseReference globalRef = databaseReference
                        .child("compilations")
                        .child(compKey);

                OnTransactionMethod<CompilationData> method = this::removeRecipe;

                actToCompilation(globalRef, method);

                return this;
            }

            private void actToCompilation(DatabaseReference ref, OnTransactionMethod<CompilationData> method) {
                ref.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        CompilationData data = mutableData.getValue(CompilationData.class);
                        if (data == null) {
                            return Transaction.success(mutableData);
                        }

                        data = method.doTransaction(data);

                        // Set value and report transaction success
                        mutableData.setValue(data);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                    }
                });
            }

            private CompilationData removeRecipe(CompilationData data) {
                if (data.recipesKey.remove(mRecipeKey)) {
                    data.count--;
                }
                return data;
            }

            private CompilationData addRecipe(CompilationData data) {
                data.count++;

                data.recipesKey.add(mRecipeKey);

                return data;
            }

            interface OnTransactionMethod<T extends BaseData> {
                T doTransaction(T data);
            }
        }
    }

    private static final class View {
        public static ArrayList<String> convertCategories(Context context, List<Integer> intCategories) {
            final ArrayList<String> strCategories = new ArrayList<>();

            for (int i = 0; i < intCategories.size(); i++) {
                int pos = intCategories.get(i);
                if (pos >= 0) {
                    String category = "unknown";
                    switch (i) {
                        case 0:
                            category = context.getResources().getStringArray(R.array.mealType)[pos];
                            break;
                        case 1:
                            category = context.getResources().getStringArray(R.array.dishType)[pos];
                            break;
                        case 2:
                            category = context.getResources().getStringArray(R.array.worldCuisine)[pos];
                            break;
                    }
                    strCategories.add(category);
                }
            }
            return strCategories;
        }
    }
}
