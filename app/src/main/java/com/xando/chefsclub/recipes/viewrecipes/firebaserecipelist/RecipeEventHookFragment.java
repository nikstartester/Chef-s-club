package com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist;

import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.App;
import com.xando.chefsclub.R;
import com.xando.chefsclub.compilations.addrecipe.AddToCompilationDialogFragment;
import com.xando.chefsclub.helper.NetworkHelper;
import com.xando.chefsclub.profiles.viewprofiles.single.ViewProfileActivityTest;
import com.xando.chefsclub.recipes.data.RecipeData;
import com.xando.chefsclub.recipes.db.RecipeToFavoriteEntity;
import com.xando.chefsclub.recipes.editrecipe.EditRecipeActivity;
import com.xando.chefsclub.recipes.repository.RecipeRepository;
import com.xando.chefsclub.recipes.repository.local.LocalRecipeSaver;
import com.xando.chefsclub.recipes.upload.EditRecipeService;
import com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist.item.AbsRecipeItem;
import com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist.item.RecipeItem;
import com.xando.chefsclub.repository.Favorite;
import com.xando.chefsclub.repository.RecipeFavoriteTransactionsKt;
import com.xando.chefsclub.shoppinglist.ViewShoppingListActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;

public abstract class RecipeEventHookFragment extends Fragment {

    private CompositeDisposable disposer = new CompositeDisposable();

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
            RecipeData model = item.getRecipeData();

            if (model == null) return;

            RecipeFavoriteTransactionsKt.switchFavorite(model);

            disposer.add(Favorite.INSTANCE.updateFavoriteInDB((App) requireContext().getApplicationContext(), model));

            item.setRecipeData(model);

            if (item.isReadyToUpdateUi())
                item.updateUiFavoriteData(true);

            disposer.add(Favorite.INSTANCE.switchFavorite((App) requireContext().getApplicationContext(),
                    new RecipeToFavoriteEntity(model.recipeKey, model.authorUId)));
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
