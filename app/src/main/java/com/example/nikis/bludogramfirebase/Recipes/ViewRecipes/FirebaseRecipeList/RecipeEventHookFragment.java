package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.Compilations.AddRecipe.AddToCompilationDialogFragment;
import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.Helpers.NetworkHelper;
import com.example.nikis.bludogramfirebase.Profiles.ViewProfiles.SingleProfile.ViewProfileActivityTest;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.example.nikis.bludogramfirebase.Recipes.EditRecipe.EditRecipeActivity;
import com.example.nikis.bludogramfirebase.Recipes.Local.LocalRecipeSaver;
import com.example.nikis.bludogramfirebase.Recipes.Repository.RecipeRepository;
import com.example.nikis.bludogramfirebase.Recipes.Upload.EditRecipeService;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList.Item.AbsRecipeItem;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.FirebaseRecipeList.Item.RecipeItem;
import com.example.nikis.bludogramfirebase.Recipes.db.RecipeToFavoriteEntity;
import com.example.nikis.bludogramfirebase.ShoppingList.ViewShoppingListActivity;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.ArrayList;
import java.util.List;

import static com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper.Favorite.updateFavorite;


public abstract class RecipeEventHookFragment extends Fragment {

    public static void favoriteClick(Context context, AbsRecipeItem item) {
        RecipeData model = item.getRecipeData();

        FirebaseHelper.Favorite.updateRecipeDataAndDBAfterFavoriteChange(
                (App) context.getApplicationContext(), model);

        item.setRecipeData(model);

        if (item.isReadyToUpdateUi())
            item.updateUiFavoriteData(true);

        updateFavorite((App) context.getApplicationContext(),
                new RecipeToFavoriteEntity(model.recipeKey, model.authorUId));
    }

    public class EventHookForRecipeItem<T extends AbsRecipeItem> extends ClickEventHook<T> {
        @Nullable
        @Override
        public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof RecipeItem.ViewHolder) {
                ArrayList<View> views = new ArrayList<>(2);

                RecipeItem.ViewHolder castViewHolder = (RecipeItem.ViewHolder) viewHolder;

                views.add(castViewHolder.itemView
                        .findViewById(R.id.tv_userLogin));
                views.add(castViewHolder.itemView
                        .findViewById(R.id.imgFavorite));
                views.add(castViewHolder.itemView
                        .findViewById(R.id.img_more));

                return views;
            } else return null;
        }

        @Override
        public void onClick(@NonNull View v, int position, @NonNull FastAdapter<T> fastAdapter,
                            @NonNull T item) {
            switch (v.getId()) {
                case R.id.tv_userLogin:
                    startActivity(ViewProfileActivityTest.getIntent(getActivity(),
                            item.getRecipeData().authorUId));
                    break;
                case R.id.imgFavorite:
                    favoriteClick(item);
                    break;
                case R.id.img_more:
                    moreClick(item);
                    break;
            }
        }

        protected void favoriteClick(T item) {
            RecipeEventHookFragment.favoriteClick(getActivity(), item);
        }

        protected void moreClick(@NonNull T item) {
            if (!item.isReadyToUpdateUi()) return;

            PopupMenu popupMenu = item.onPrepareMoreMenu(getActivity());

            onPrepareMoreMenu(popupMenu);

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (item.getRecipeData() == null) return false;

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.act_edit:
                        startActivity(EditRecipeActivity.getIntent(getActivity(), item.getRecipeData()));
                        break;
                    case R.id.act_open_shopping_list:
                        startActivity(ViewShoppingListActivity.getIntent(getActivity(),
                                item.getRecipeData().recipeKey));
                        break;
                    case R.id.act_add_to_db:
                        addRecipeToDb(item.getRecipeData());

                        break;
                    case R.id.act_delete_from_db:
                        deleteRecipeFromDb(item.getRecipeData().recipeKey);

                        break;
                    case R.id.act_add_to_compilation:
                        addRecipeToCompilation(item.getRecipeData());

                        break;

                    case R.id.act_remove_from_compilation:
                        removeFromCompilation(item.getRecipeData());
                        break;
                    case R.id.act_delete:
                        startDeleteRecipe(item);
                        break;
                }
                return true;
            });

            popupMenu.show();
        }

        protected void onPrepareMoreMenu(@NonNull PopupMenu popupMenu) {
            /*
            nothing
             */
        }

        protected void addRecipeToDb(@NonNull RecipeData recipeData) {
            new LocalRecipeSaver(getActivity().getApplication(), true).save(recipeData);
        }

        protected void deleteRecipeFromDb(@NonNull String recipeId) {
            new Thread(() -> ((App) getActivity().getApplication())
                    .getDatabase()
                    .recipeDao()
                    .deleteByRecipeKey(recipeId)).start();
        }

        protected void addRecipeToCompilation(@NonNull RecipeData recipeData) {
            AddToCompilationDialogFragment dialogFragment = new AddToCompilationDialogFragment();

            dialogFragment.setArguments(AddToCompilationDialogFragment.getArguments(recipeData));

            dialogFragment.show(getChildFragmentManager(), "addRecipeToCompilation");
        }

        protected void removeFromCompilation(RecipeData recipeData) {
            /*
            Nothing
             */
        }

        protected void startDeleteRecipe(T item) {
            showDeleteRecipesDialog(item);
        }

        protected final void showDeleteRecipesDialog(T item) {
            assert item.getRecipeData() != null;

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

            String title = "Delete \"" + item.getRecipeData().overviewData.name + "\"";
            String message = "Are you sure you want to delete recipe?";
            String positiveButtonStr = "Yes";
            String negativeButtonStr = "Cancel";

            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setIcon(R.drawable.ic_delete_blue_24dp);

            dialog.setPositiveButton(positiveButtonStr, (dialog1, which) -> {
                if (NetworkHelper.isConnected(getActivity()))
                    deleteRecipe(item);
                else
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            });

            dialog.setNegativeButton(negativeButtonStr, (dialog12, which) -> dialog12.cancel());

            dialog.setCancelable(true);

            dialog.show();
        }

        protected void deleteRecipe(T item) {
            assert item.getRecipeData() != null;

            RecipeData recipeData = RecipeRepository
                    .deleteRecipeFromAllChild(getActivity().getApplication(),
                            item.getRecipeData(), item.isSavedLoacal);

            item.setRecipeData(recipeData);

            showRemovedRecipeSnackBar(item.getMoreBtn(), item.getRecipeData());
        }

        protected final void showRemovedRecipeSnackBar(View view, RecipeData recipeData) {
            String message = "Recipe \"" + recipeData.overviewData.name + "\" has been deleted";

            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction("UNDO", v -> EditRecipeService.startEditRecipeService(getActivity(),
                            recipeData.setNeedUpdateDateTime(false))).show();
        }
    }
}
