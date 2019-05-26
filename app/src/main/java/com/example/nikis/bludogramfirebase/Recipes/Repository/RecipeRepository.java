package com.example.nikis.bludogramfirebase.Recipes.Repository;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.Constants.Constants;
import com.example.nikis.bludogramfirebase.DataWorkers.BaseLocalDataSaver;
import com.example.nikis.bludogramfirebase.DataWorkers.BaseRepository;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipes.Data.StepOfCooking;
import com.example.nikis.bludogramfirebase.Recipes.Local.LocalRecipeSaver;
import com.example.nikis.bludogramfirebase.Recipes.db.RecipeEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class RecipeRepository extends BaseRepository<RecipeData> {

    public static final String CHILD_RECIPES = "recipes";

    public static final String CHILD_USER_RECIPES = "user-recipes";

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private RecipeRepository() {
        super();
    }

    public static Builder with(Application application) {
        RecipeRepository repository = new RecipeRepository();
        repository.mApplication = application;

        return repository.new Builder();
    }

    @Override
    public void loadDataFromDB() {
        Disposable disposable = ((App) mApplication)
                .getDatabase()
                .recipeDao()
                .getSingleByRecipeKey(mFirebaseId)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(recipeEntities -> {
                    List<RecipeData> recipeDataList = new ArrayList<>();

                    for (RecipeEntity entity : recipeEntities) {
                        recipeDataList.add(entity.toRecipeData());
                    }

                    super.onDataLoadedFromDB(recipeDataList);

                });

        compositeDisposable.add(disposable);

    }

    @Override
    public void deleteFromServer(RecipeData data) {
        //deleteRecipeFromAllChild(data.recipeKey);
    }

    /*
    All necessary steps to remove recipe: get actual Recipe from server -> delete from compilation, delete recipe
                                          if isSavedLocal remove inCompilation value in db
     */
    public static RecipeData deleteRecipeFromAllChild(Application application, RecipeData recipeData, boolean isSavedLocal) {
        DatabaseReference ref = FirebaseReferences.getDataBaseReference();

        ref.child(CHILD_RECIPES).child(recipeData.recipeKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RecipeData data = dataSnapshot.getValue(RecipeData.class);

                if (data != null) {
                    deleteFromCompilations(data);

                    deleteRecipe(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recipeData.inCompilations.clear();

        if (isSavedLocal) {
            new Thread(() -> {
                ((App) application).getDatabase()
                        .recipeDao()
                        .deleteInCompilations(recipeData.recipeKey);
            }).start();
        }

        return recipeData;
    }

    /*
    Deprecated: use deleteRecipeFromAllChild(Application application, RecipeData recipeData, boolean isSavedLocal)
        instead
     */
    @Deprecated
    public static void deleteRecipeFromAllChild(String recipeKey) {
        DatabaseReference ref = FirebaseReferences.getDataBaseReference();

        ref.child(CHILD_RECIPES).child(recipeKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RecipeData data = dataSnapshot.getValue(RecipeData.class);

                if (data != null) {
                    deleteFromCompilations(data);

                    deleteRecipe(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void deleteComments(String recipeKey) {
        DatabaseReference ref = FirebaseReferences.getDataBaseReference();

        ref.child("comments").child("recipes").child(recipeKey).removeValue();
    }

    public static void deleteFromCompilations(RecipeData data) {
        Set<Map.Entry<String, Boolean>> inCompilationsSet = data.inCompilations.entrySet();

        FirebaseHelper.Compilations.CompilationActions compAct =
                new FirebaseHelper.Compilations.CompilationActions();

        for (Map.Entry<String, Boolean> entry : inCompilationsSet) {
            compAct.removeRecipeFromCompilation(entry.getKey(), data.recipeKey);
        }

        data.inCompilations.clear();
    }

    public static void deleteRecipe(RecipeData data) {
        DatabaseReference ref = FirebaseReferences.getDataBaseReference();

        DatabaseReference globalRef = ref.child(CHILD_RECIPES).child(data.recipeKey);
        DatabaseReference userRef = ref.child(CHILD_USER_RECIPES).child(data.authorUId).child(data.recipeKey);

        globalRef.removeValue();
        userRef.removeValue();
    }

    public static void deleteImages(RecipeData data) {

        deleteImages(data.overviewData.allImagePathList);

        List<String> stepsImagePaths = new ArrayList<>();

        for (StepOfCooking stepData : data.stepsData.stepsOfCooking) {
            stepsImagePaths.add(stepData.imagePath);
        }

        deleteImages(stepsImagePaths);
    }

    public static void deleteImages(List<String> paths) {
        for (String path : paths) {
            deleteImage(path);
        }
    }

    public static void deleteImage(String path) {
        StorageReference storageRef = FirebaseReferences.getStorageReference();

        if (path != null && path.startsWith(Constants.ImageConstants.FIREBASE_STORAGE_AT_START)) {
            storageRef.child(path).delete();
        }
    }

    @Override
    public void deleteFromDataBase(RecipeData data) {

    }

    @Nullable
    @Override
    protected BaseLocalDataSaver<RecipeData> getDefSaverIfRowExistInDB() {
        return new LocalRecipeSaver(mApplication, false);
    }

    @Override
    public Class<RecipeData> getDataClass() {
        return RecipeData.class;
    }

    public class Builder extends BaseRepository.Builder {

        @Override
        protected void checkAndSetStandardValue() {
            if (resData == null && !isWithoutStatus) resData = new MutableLiveData<>();

            if (mFirebaseId == null)
                throw new NullPointerException("mFirebaseId (recipeKey) might not null!");

            if (mFirebaseChild == null) mFirebaseChild = CHILD_RECIPES;

            //TODO use localSever further
            if (mLocalSaver == null) mLocalSaver = null;

        }
    }
}
