package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Ingredients;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.xando.chefsclub.App;
import com.xando.chefsclub.Helpers.UiHelper;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Dialogs.ChooseIngredientDialog;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.IngredientsViewItem;
import com.xando.chefsclub.ShoppingList.ViewShoppingListActivity;
import com.xando.chefsclub.ShoppingList.db.Helper;
import com.xando.chefsclub.ShoppingList.db.IngredientEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class IngredientsListFragment extends Fragment {
    private static final String TAG = "IngredientsListFragment";
    private static final int REQUEST_CODE_CHOOSE_INGREDIENT = 98;
    private static final String KEY_RECIPE_ID = "RECIPE_ID";

    @BindView(R.id.rv_ingredients)
    protected RecyclerView recyclerViewIngredients;

    /*@BindView(R.id.progress)
    protected ProgressBar progressBar;*/

    @BindView(R.id.edit_mode)
    protected View editModeView;
    @BindView(R.id.checkBox_all_available)
    protected CheckBox allToAvailableCheckBox;
    @BindView(R.id.checkBox_all_to_shoppingList)
    protected CheckBox allToShoppingListCheckBox;

    private FastItemAdapter<IngredientsViewItem> mIngredientsAdapter;

    private DialogFragment mIngredientChooseDialog;

    @Nullable
    private String mRecipeId;

    private Map<String, Integer> mIngredientsMap;

    private IngrediensViewModel mIngredientIdsViewModel;

    private boolean isEditMode = false;

    public static Fragment getInstance(@Nullable String recipeId) {
        Fragment fragment = new IngredientsListFragment();

        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECIPE_ID, recipeId);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIngredientsAdapter = new FastItemAdapter<>();

        mIngredientsAdapter.withSelectable(true);

        mIngredientChooseDialog = new ChooseIngredientDialog();
        mIngredientChooseDialog.setTargetFragment(this, REQUEST_CODE_CHOOSE_INGREDIENT);

        mIngredientsMap = new HashMap<>();

        mIngredientIdsViewModel = ViewModelProviders.of(this).get(IngrediensViewModel.class);

        if (getArguments() != null) {
            mRecipeId = getArguments().getString(KEY_RECIPE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_recipe_owerview_ingredients,
                container, false);

        ButterKnife.bind(this, view);

        initViews();

        setOnClickListeners();

        mIngredientIdsViewModel.getData().observe(this, res -> {
            if (res != null) {
                onDataLoadedFromDb(res);
            }
        });

        return view;
    }

    private void onDataLoadedFromDb(List<IngredientEntity> res) {
        showProgress();

        List<Integer> changedPosList = new ArrayList<>();

        for (IngredientEntity ingr : res) {
            Integer changedPos = setDataToAdapterFromShoppingList(ingr);

            changedPosList.add(changedPos);
        }

        syncRemovedItemsAndHideProgress(changedPosList);
    }


    private void initViews() {
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewIngredients.setAdapter(mIngredientsAdapter);
        recyclerViewIngredients.setItemAnimator(new DefaultItemAnimator());
        recyclerViewIngredients.setNestedScrollingEnabled(false);
    }

    private void setOnClickListeners() {
        mIngredientsAdapter.withOnPreClickListener((v, adapter, item, position) -> {
            mIngredientChooseDialog.setArguments(ChooseIngredientDialog.getArg(position, item.isSelected()));
            mIngredientChooseDialog.show(getFragmentManager(), "Action ingredient");

            return true;
        });

        mIngredientsAdapter.withEventHook(new IngredientsViewItem.CheckBoxClickEvent());

        mIngredientsAdapter.withEventHook(new ClickEventHook<IngredientsViewItem>() {
            @Nullable
            @Override
            public View onBind(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof IngredientsViewItem.ViewHolder)
                    return ((IngredientsViewItem.ViewHolder) viewHolder)
                            .itemView.findViewById(R.id.checkBox_available);
                return null;
            }

            @Override
            public void onClick(@NonNull View v, int position, @NonNull FastAdapter<IngredientsViewItem> fastAdapter,
                                @NonNull IngredientsViewItem item) {
                switch (v.getId()) {
                    case R.id.checkBox_available:
                        changeAvailable(item);

                        break;
                }
            }
        });

        mIngredientsAdapter.withSelectionListener((item, selected) -> {
            App app = (App) getActivity().getApplication();

            if (selected) {
                Helper.addToDB(app, item.getEntity());

            } else {
                item.setToUnavailable();

                Helper.deleteFromDB(app, item.getEntity());
            }
        });

        mIngredientsAdapter.withOnLongClickListener((v, adapter, item, position) -> {
            changeEditMode(true);
            return true;
        });

        allToShoppingListCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isEditMode) {
                return;
            }
            if (isChecked) {
                addAllToShoppingList();
            } else deleteAllFromShoppingList();
        });

        allToAvailableCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isEditMode) {
                return;
            }
            if (isChecked) {
                setAllAvailable();
            } else setAllNotAvailable();

        });
    }

    private void changeAvailable(@NonNull IngredientsViewItem item) {
        changeAvailable(item, !item.getEntity().isAvailable);
    }

    private void changeAvailable(@NonNull IngredientsViewItem item, boolean isAvailable) {
        if (item.getEntity().isAvailable == isAvailable) {
            return;
        }

        item.getEntity().changeAvailable(isAvailable);

        if (!item.isSelected() && isAvailable) {
            int pos = mIngredientsAdapter.getAdapterPosition(item);
            mIngredientsAdapter.getExtension(SelectExtension.class).select(pos);
        } else {
            App app = (App) getActivity().getApplication();

            Helper.changeAvailableFromDB(app, item.getEntity());
        }
    }

    public void refresh(@NonNull List<IngredientEntity> recipeIngredients) {
        setIngredientsToAdapter(recipeIngredients);
    }

    private void setIngredientsToAdapter(@NonNull List<IngredientEntity> recipeIngredients) {
        showProgress();

        mIngredientsAdapter.clear();

        for (int i = 0; i < recipeIngredients.size(); i++) {
            IngredientEntity inrg = recipeIngredients.get(i);

            mIngredientsAdapter.add(new IngredientsViewItem(inrg));

            mIngredientsMap.put(inrg.ingredient, i);
        }

        if (mRecipeId != null) {
            if (mIngredientIdsViewModel.getData().getValue() == null) {
                mIngredientIdsViewModel.loadData(mRecipeId);
            } else {
                onDataLoadedFromDb(mIngredientIdsViewModel.getData().getValue());
            }
        }
    }

    private Integer setDataToAdapterFromShoppingList(IngredientEntity ingr) {
        if (ingr == null)
            return null;

        Integer pos = mIngredientsMap.get(ingr.ingredient);
        if (pos != null) {
            mIngredientsAdapter.getItem(pos).updateAvailable(ingr.isAvailable);

            mIngredientsAdapter.getAdapterItem(pos).changeSelected(true);
        }

        return pos;
    }

    //Ingredients can be removed from like shoppingList. So check that
    private void syncRemovedItemsAndHideProgress(final List<Integer> changedPosList) {
        for (int i = 0; i < mIngredientsAdapter.getAdapterItems().size(); i++) {
            if (!changedPosList.contains(i)) {
                boolean isSelected = isItemSelected(i);
                if (isSelected) {
                    mIngredientsAdapter.getAdapterItem(i).changeSelected(false);
                }
            }
        }
        hideProgress();
    }

    private boolean isItemSelected(int pos) {
        return mIngredientsAdapter.getAdapterItem(pos).isSelected();
    }

    private void showProgress() {
        //progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        //progressBar.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.imageBtn_ingredients_actions)
    protected void showIngredientsActions(final View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);

        popupMenu.inflate(R.menu.ingredients_menu);

        if (isEditMode)
            popupMenu.getMenu().findItem(R.id.act_edit_available).setVisible(false);
        else
            popupMenu.getMenu().findItem(R.id.act_edit_available).setVisible(true);

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.act_edit_available:
                    changeEditMode();
                    break;
                case R.id.act_add_all_to_shopping_list:
                    addAllToShoppingList();
                    break;
                case R.id.act_delete_all_from_shopping_list:
                    deleteAllFromShoppingList();
                    break;
                case R.id.act_open_shopping_list:
                    startActivity(ViewShoppingListActivity.getIntent(getActivity(), mRecipeId)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                    break;
            }
            return true;
        });

        popupMenu.show();
    }

    private void changeEditMode() {
        changeEditMode(!isEditMode);
    }

    private void changeEditMode(boolean isEdit) {
        if (mRecipeId != null) {

            isEditMode = isEdit;

            UiHelper.Other.showFadeAnim(editModeView, isEdit ? View.VISIBLE : View.GONE);

            for (IngredientsViewItem item : mIngredientsAdapter.getAdapterItems()) {
                item.changeEditMode(isEdit);
            }
        }
    }

    private void addAllToShoppingList() {
        if (mRecipeId != null) {
            Helper.addListToDB((App) getActivity().getApplication(), getIngrList());
        }
    }

    private List<IngredientEntity> getIngrList() {
        List<IngredientEntity> list = new ArrayList<>();
        for (IngredientsViewItem item : mIngredientsAdapter.getAdapterItems()) {
            list.add(item.getEntity());
        }
        return list;
    }

    private void deleteAllFromShoppingList() {
        if (mRecipeId != null) {
            Helper.deleteByRecipeIdFromDB((App) getActivity().getApplication(), mRecipeId);

            clearShoppingListDataFromAdapter();
        }
    }

    private void clearShoppingListDataFromAdapter() {
        for (IngredientsViewItem item : mIngredientsAdapter.getAdapterItems()) {
            item.changeSelected(false);
        }
    }

    private void setAllAvailable() {
        for (IngredientsViewItem item : mIngredientsAdapter.getAdapterItems()) {
            changeAvailable(item, true);
        }
    }

    private void setAllNotAvailable() {
        for (IngredientsViewItem item : mIngredientsAdapter.getAdapterItems()) {
            changeAvailable(item, false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        stopEdit();
    }

    @OnClick(R.id.imgBtn_close_edit_mode)
    protected void stopEdit() {
        isEditMode = false;

        UiHelper.Other.showFadeAnim(editModeView, View.GONE);

        for (IngredientsViewItem item : mIngredientsAdapter.getAdapterItems()) {
            item.stopEditMode();
        }

        allToAvailableCheckBox.setChecked(false);
        allToShoppingListCheckBox.setChecked(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE_INGREDIENT && resultCode == ChooseIngredientDialog.RESULT_CODE_OK) {
            int pos = data.getIntExtra(ChooseIngredientDialog.KEY_POSITION, -1);

            boolean isSelectChange = data.getBooleanExtra(
                    ChooseIngredientDialog.KEY_IS_CHECKED_CHANGED, false);

            if (isSelectChange) {
                if (data.getBooleanExtra(ChooseIngredientDialog.KEY_IS_CHECKED, false))
                    mIngredientsAdapter.getExtension(SelectExtension.class).select(pos);
                else mIngredientsAdapter.getExtension(SelectExtension.class).deselect(pos);
            } else {
                startActivity(ViewShoppingListActivity.getIntent(getActivity(),
                        mIngredientsAdapter.getAdapterItem(pos).getEntity())
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
