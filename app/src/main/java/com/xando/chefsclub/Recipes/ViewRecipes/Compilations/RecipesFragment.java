package com.xando.chefsclub.Recipes.ViewRecipes.Compilations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.Compilations.Data.CompilationData;
import com.xando.chefsclub.Helpers.FirebaseHelper;
import com.xando.chefsclub.Helpers.NetworkHelper;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.ViewRecipes.FirebaseRecipeList.Item.RecipeItem;
import com.xando.chefsclub.Recipes.ViewRecipes.FirebaseRecipeList.RecipesListFragment;


public class RecipesFragment extends RecipesListFragment {
    private static final String KEY_COMPILATION = "COMPILATION";

    private CompilationData mCompilation;

    public static Bundle getArguments(CompilationData compilation) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(KEY_COMPILATION, compilation);

        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCompilation = getArguments().getParcelable(KEY_COMPILATION);
        }
    }


    @Nullable
    @Override
    protected ClickEventHook<RecipeItem> getCustomClickEventHook() {
        return new CompilationRecipeEventHook();
    }

    @Override
    protected Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("recipes").orderByChild("inCompilations/" + mCompilation.compilationKey)
                .equalTo(true);
    }

    @Override
    protected boolean isNestedScrolling() {
        return true;
    }

    public class CompilationRecipeEventHook extends EventHookForRecipeItem<RecipeItem> {
        @Override
        protected void onPrepareMoreMenu(@NonNull PopupMenu popupMenu) {
            popupMenu.getMenu()
                    .findItem(R.id.act_remove_from_compilation)
                    .setVisible(true);

            super.onPrepareMoreMenu(popupMenu);
        }

        @Override
        protected void removeFromCompilation(RecipeData recipeData) {
            if (NetworkHelper.isConnected(getActivity())) {
                new FirebaseHelper.Compilations.CompilationActions()
                        .removeFromRecipe(mCompilation, recipeData.recipeKey)
                        .saveChangesOnCompilation()
                        .updateCompilationOnServer();
            } else {
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
