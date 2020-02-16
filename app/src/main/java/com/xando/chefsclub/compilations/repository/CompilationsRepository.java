package com.xando.chefsclub.compilations.repository;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.xando.chefsclub.App;
import com.xando.chefsclub.compilations.data.ArrayCompilations;
import com.xando.chefsclub.compilations.data.CompilationData;
import com.xando.chefsclub.compilations.db.CompilationEntity;
import com.xando.chefsclub.dataworkers.AppDatabase;
import com.xando.chefsclub.dataworkers.DataBaseLoader;
import com.xando.chefsclub.dataworkers.DeletableData;
import com.xando.chefsclub.dataworkers.ParcResourceByParc;
import com.xando.chefsclub.dataworkers.ProgressUpdate;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.helper.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class CompilationsRepository implements DataBaseLoader<CompilationData>, DeletableData<CompilationData> {

    private final Application mApplication;

    @Nullable
    private final MutableLiveData<List<CompilationData>> resData;

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public CompilationsRepository(Application application,
                                  @Nullable MutableLiveData<List<CompilationData>> resData) {
        mApplication = application;
        this.resData = resData;
    }

    /*
    When you call sync() method: if data will change on server -> data will change in db
     */
    public void sync(String userUid, ProgressUpdate<ArrayCompilations> progressUpdate) {
        progressUpdate.updateProgress(ParcResourceByParc.loading(null));

        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

        myRef.child("/compilations/").orderByChild("authorUId/").equalTo(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        progressUpdate.updateProgress(ParcResourceByParc.loading(null));

                        new Thread(() -> {
                            ArrayCompilations arrayCompilations = new ArrayCompilations();
                            List<CompilationEntity> entities = new ArrayList<>();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CompilationData compilationData = snapshot.getValue(CompilationData.class);

                                arrayCompilations.mCompilationData.add(compilationData);

                                entities.add(new CompilationEntity(compilationData));
                            }

                            clearRemovedTittles(entities);

                            ((App) mApplication)
                                    .getDatabase()
                                    .compilationTittleDao()
                                    .insertAll(entities);

                            progressUpdate.updateProgress(ParcResourceByParc.success(arrayCompilations));

                        }).start();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressUpdate.updateProgress(ParcResourceByParc
                                .error(databaseError.toException(), null));
                    }
                });
    }

    private void clearRemovedTittles(List<CompilationEntity> newEntities) {
        AppDatabase appDatabase = ((App) mApplication)
                .getDatabase();

        List<CompilationEntity> oldTittles = appDatabase.compilationTittleDao().getAll();

        List<CompilationEntity> entitiesToDelete = new ArrayList<>();

        for (CompilationEntity oldTittle : oldTittles) {
            boolean isExist = false;
            for (CompilationEntity newEntity : newEntities) {

                if (newEntity.mCompilationData.compilationKey.equals(oldTittle.mCompilationData.compilationKey)) {
                    isExist = true;
                    entitiesToDelete.add(oldTittle);
                    break;
                }
            }
            if (!isExist) {
                entitiesToDelete.add(oldTittle);
            }
        }
        appDatabase.compilationTittleDao().delete(entitiesToDelete);
    }

    @Override
    public void loadDataFromDB() {
        Disposable disposable = ((App) mApplication)
                .getDatabase()
                .compilationTittleDao()
                .getFlowableAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onDataLoadedFromDB);

        mCompositeDisposable.add(disposable);
    }

    public void dispose() {
        mCompositeDisposable.dispose();
    }

    @Override
    public void onDataLoadedFromDB(@Nullable List<CompilationData> compilationData) {
        if (resData != null) {
            resData.setValue(compilationData);
        }
    }

    @Override
    public void deleteFromServer(CompilationData data) {
        DatabaseReference dataBaseReference = FirebaseReferences.getDataBaseReference();

        DatabaseReference compilationRef = dataBaseReference
                .child("compilations")
                .child(data.compilationKey);

        compilationRef.removeValue();

        FirebaseHelper.Compilations.CompilationActions compilationAct = new FirebaseHelper.Compilations.CompilationActions();


        List<String> recipes = new ArrayList<>(data.recipesKey);
        for (int i = 0; i < recipes.size(); i++) {
            compilationAct
                    .removeFromRecipe(data, recipes.get(i))
                    .saveChangesOnCompilation();
        }

        /*for (String recipeKey : data.recipesKey) {
            compilationAct.removeFromRecipe(data, recipeKey);
        }*/
    }

    @Override
    public void deleteFromDataBase(CompilationData data) {

    }
}
