package com.example.nikis.bludogramfirebase.Recipe.Repository;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;

import com.example.nikis.bludogramfirebase.BaseRepository;
import com.example.nikis.bludogramfirebase.Recipe.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Resource;


public class RecipeRepository extends BaseRepository<RecipeData> {

    public static final String CHILD_RECIPES = "recipes";

    public static final String CHILD_USER_RECIPES = "user-recipes";

    private RecipeRepository(){
        super();
    }

    public static Builder with(Application application){
        RecipeRepository repository = new RecipeRepository();
        repository.mApplication = application;

        return repository.new Builder();
    }

    @Override
    public void loadDataFromDB() {
        resData.setValue(Resource.loading(null));

        onDataLoadedFromDB(null);
    }

    @Override
    public Class<RecipeData> getDataClass() {
        return RecipeData.class;
    }

    public class Builder extends BaseRepository.Builder{

        @Override
        protected void checkAndSetStandardValue() {
            if(resData == null) resData = new MutableLiveData<>();

            if(mFirebaseId == null) throw new NullPointerException("mFirebaseId (recipeKey) might not null!");

            if(mFirebaseChild == null) mFirebaseChild = CHILD_RECIPES;

            //TODO use localSever further
            if(mLocalSaver == null) mLocalSaver = null;
        }
    }
}
