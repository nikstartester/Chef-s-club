package com.xando.chefsclub.recipes.viewrecipes.compilations;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.R;
import com.xando.chefsclub.compilations.data.CompilationData;
import com.xando.chefsclub.helper.NetworkHelper;
import com.xando.chefsclub.recipes.data.RecipeData;
import com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist.RecipesListFragment;
import com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist.item.RecipeItem;
import com.xando.chefsclub.repository.CompilationsTransactions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import io.reactivex.disposables.CompositeDisposable;


public class RecipesFragment extends RecipesListFragment {

    private static final String KEY_COMPILATION = "COMPILATION";

    private CompilationData mCompilation;

    private CompositeDisposable disposer = new CompositeDisposable();

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

    @Override
    public void onDestroy() {
        disposer.dispose();
        super.onDestroy();
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
            if (NetworkHelper.isConnected(requireActivity())) {
                disposer.add(CompilationsTransactions.INSTANCE.onRemoveRecipeFromCompilation(
                        mCompilation.compilationKey,
                        recipeData.recipeKey
                ));
            } else {
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
