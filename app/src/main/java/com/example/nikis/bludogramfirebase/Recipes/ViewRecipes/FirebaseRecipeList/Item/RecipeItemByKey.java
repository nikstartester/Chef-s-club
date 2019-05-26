package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList.Item;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.nikis.bludogramfirebase.DataWorkers.BaseRepository;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceByParc;
import com.example.nikis.bludogramfirebase.Recipes.Data.ActualRecipeDataCheckerSingleton;
import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipes.Repository.RecipeRepository;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SubsciptionsRecipes.Data.RecipeIdData;

import java.util.List;

public class RecipeItemByKey extends AbsRecipeItem {

    private final RecipeIdData mRecipeIdData;

    private final MutableLiveData<ParcResourceByParc<RecipeData>> mRecipeLiveData;

    public RecipeItemByKey(RecipeIdData recipeIdData, LifecycleOwner owner) {
        super(owner);

        mRecipeIdData = recipeIdData;

        mRecipeLiveData = new MutableLiveData<>();

    }

    public RecipeIdData getRecipeIdData() {
        return mRecipeIdData;
    }

    @Override
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);

        LifecycleOwner owner = super.getLifecycleOwner();

        if (owner != null) {
            observeRecipeLiveData(owner);

            if (mRecipeLiveData.getValue() == null)
                loadRecipeData((Application) holder.itemView.getContext().getApplicationContext());
            else super.bindToRecipe();
        }

    }

    private void observeRecipeLiveData(@NonNull LifecycleOwner owner) {
        mRecipeLiveData.observe(owner, res -> {
            if (res != null && res.status == ParcResourceByParc.Status.SUCCESS) {

                boolean isNeedUpdateUi = super.onUpdateData(res.data,
                        ActualRecipeDataCheckerSingleton.getInstance());

                if (isNeedUpdateUi) super.bindToRecipe();
            }
        });
    }

    private void loadRecipeData(Application app) {
        RecipeRepository.with(app)
                .setPriority(BaseRepository.Priority.DATABASE_FIRST_OR_SERVER)
                .setFirebaseId(mRecipeIdData.recipeKey)
                .to(mRecipeLiveData)
                .build()
                .loadData();
    }
}
