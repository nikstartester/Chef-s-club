package com.xando.chefsclub.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback;
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeDragCallback;
import com.mikepenz.fastadapter_extensions.utilities.DragDropUtil;
import com.xando.chefsclub.App;
import com.xando.chefsclub.R;
import com.xando.chefsclub.constants.Constants;
import com.xando.chefsclub.recipes.viewrecipes.singlerecipe.ViewRecipeActivity;
import com.xando.chefsclub.shoppinglist.List.StickyHeaderAdapter;
import com.xando.chefsclub.shoppinglist.List.StickyHeadersDecoration;
import com.xando.chefsclub.shoppinglist.List.StickyHeadersTouchListener;
import com.xando.chefsclub.shoppinglist.List.item.IngredientItem;
import com.xando.chefsclub.shoppinglist.db.Helper;
import com.xando.chefsclub.shoppinglist.db.IngredientEntity;
import com.xando.chefsclub.shoppinglist.viewmodel.IngredientsViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ViewShoppingListFragment extends Fragment
        implements ItemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback {

    private static final String TAG = "ViewShoppingListFragmen";

    private static final String KEY_INGREDIENT = "keyIngredient";
    private static final String KEY_RECIPE_ID = "keyRecipeId";
    private static final String KEY_DONT_SHOW_AGAIN_SWIPE_INFO = "DONT_SHOW_AGAIN_SWIPE_INFO";
    private static final String KEY_IS_SCROLLED_ONCE = "KEY_IS_SCROLLED_ONCE";

    private final ItemAdapter<IngredientItem> mItemAdapter = new ItemAdapter<>();
    private final List<Runnable> mSwipeRunnables = new ArrayList<>();

    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;
    @BindView(R.id.empty_placeholder)
    protected View emptyPlaceholder;

    private FastAdapter<IngredientItem> mFastAdapter;
    private IngredientsViewModel mIngredientsViewModel;
    private IngredientEntity toFocusIngredient;
    private String toFocusRecipeId;

    public static Fragment getInstance(@Nullable IngredientEntity toFocus) {
        Fragment fragment = new ViewShoppingListFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INGREDIENT, toFocus);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static Fragment getInstance(@Nullable String toFocusRecipeId) {
        Fragment fragment = new ViewShoppingListFragment();

        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECIPE_ID, toFocusRecipeId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIngredientsViewModel = ViewModelProviders.of(this).get(IngredientsViewModel.class);

        if (getArguments() != null && savedInstanceState == null) {
            toFocusIngredient = getArguments().getParcelable(KEY_INGREDIENT);
            toFocusRecipeId = getArguments().getString(KEY_RECIPE_ID, null);
        }

        mFastAdapter = FastAdapter.with(mItemAdapter);
        mFastAdapter.withSelectable(true);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        ButterKnife.bind(this, view);

        final StickyHeadersDecoration decoration = initRecyclerView();

        configureFastAdapter();

        setStickyHeaderTouchListener(decoration);

        setDragCallback();

        mIngredientsViewModel.getData().observe(getViewLifecycleOwner(), res -> {
            if (res != null) {
                if (res.size() > 0) {
                    hideEmptyPlaceHolder();

                    showFirstInfo();

                    onDataLoaded(res);
                } else {
                    showEmptyPlaceHolder();
                }
            } else {
                showEmptyPlaceHolder();
            }
        });

        if (mIngredientsViewModel.getData().getValue() == null)
            mIngredientsViewModel.loadData();

        return view;
    }

    @NonNull
    private StickyHeadersDecoration initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();

        recyclerView.setAdapter(stickyHeaderAdapter.wrap(mFastAdapter));

        final StickyHeadersDecoration decoration = new StickyHeadersDecoration(stickyHeaderAdapter);

        recyclerView.addItemDecoration(decoration);

        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });
        return decoration;
    }

    private void configureFastAdapter() {
        mItemAdapter.getItemFilter().withFilterPredicate((item, constraint) -> constraint != null &&
                (item.entity.ingredient.toLowerCase().contains(constraint.toString().toLowerCase())
                        || item.entity.recipeName.toLowerCase().contains(constraint.toString().toLowerCase())));

        mFastAdapter.withOnPreClickListener((v, adapter, item, position) -> true);

        mFastAdapter.withEventHook(new IngredientItem.CheckBoxClickEvent());

        mFastAdapter.withSelectionListener((item, selected) -> {
            App app = (App) getActivity().getApplication();

            assert item != null;
            if (item.entity.isAvailable != selected) {
                item.entity.isAvailable = selected;

                Helper.INSTANCE.changeAvailableFromDB(app, item.entity);
            }
        });
    }

    private void setStickyHeaderTouchListener(StickyHeadersDecoration decoration) {
        StickyHeadersTouchListener touchListener =
                new StickyHeadersTouchListener(recyclerView, decoration);
        touchListener.setEventHook(new StickyHeadersTouchListener.EventHook() {
            @NonNull
            @Override
            public List<View> onBindViews(@NonNull View header) {
                List<View> views = new ArrayList<>();

                views.add(header.findViewById(R.id.imageButton));

                return views;
            }

            @Override
            public void onClick(View header, View view, int position, long headerId) {
                if (view.getId() == R.id.imageButton) {
                    String recipeId = StickyHeaderAdapter.HeaderIdAdapter.getRecipeIdFromHeader(header);
                    String recipeName = StickyHeaderAdapter.HeaderIdAdapter.getRecipeNameFromHeader(header);

                    showDialogForHeader(recipeName, recipeId);
                }
            }
        });

        recyclerView.addOnItemTouchListener(touchListener);
    }

    private void setDragCallback() {
        Drawable leaveBehindDrawableLeft = VectorDrawableCompat.create(getResources(),
                R.drawable.ic_delete_white_24dp, null);

        SimpleDragCallback touchCallback = new SimpleSwipeDragCallback(
                this,
                this,
                leaveBehindDrawableLeft,
                ItemTouchHelper.LEFT,
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        ItemTouchHelper touchHelper = new ItemTouchHelper(touchCallback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void showFirstInfo() {
        if (isToShowSwipeInfo()) {
            showSwipeInfo();
        }
    }

    private boolean isToShowSwipeInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.Settings.APP_PREFERENCES,
                Context.MODE_PRIVATE);
        return !sharedPreferences.getBoolean(KEY_DONT_SHOW_AGAIN_SWIPE_INFO, false);
    }

    private void showSwipeInfo() {
        String text = getString(R.string.shopping_list_swipe_info);

        Snackbar.make(recyclerView, text, Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.do_not_show_again), (v) -> {
                    saveDontShowAgain();
                }).show();
    }

    private void saveDontShowAgain() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.Settings.APP_PREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(KEY_DONT_SHOW_AGAIN_SWIPE_INFO, true);

        editor.apply();
    }

    private void onDataLoaded(@NonNull List<IngredientEntity> res) {
        mItemAdapter.clear();

        int toFocusPos = -1;

        List<IngredientItem> items = new ArrayList<>();
        List<Integer> selected = new ArrayList<>();

        for (int i = 0; i < res.size(); i++) {
            IngredientItem item = new IngredientItem(res.get(i))
                    .withIsSwipeable(true)
                    .withIsDraggable(false);

            if (res.get(i).isAvailable) selected.add(i);

            if (res.get(i).recipeId != null && res.get(i).ingredient != null) {
                boolean isIngrEquals = toFocusIngredient != null && res.get(i)
                        .ingredient.equals(toFocusIngredient.ingredient);

                boolean isRecipeIdsEquals = toFocusIngredient != null && res.get(i)
                        .recipeId.equals(toFocusIngredient.recipeId);

                if (toFocusIngredient != null && isIngrEquals && isRecipeIdsEquals) {
                    toFocusPos = i;

                    item.setHighlightOnce(true);
                } else if (res.get(i).recipeId.equals(toFocusRecipeId) && toFocusPos == -1) {
                    toFocusPos = i;
                }
            }

            items.add(item);
        }
        mItemAdapter.clear();

        mItemAdapter.add(items);

        mFastAdapter.getExtension(SelectExtension.class).select(selected);

        if (toFocusPos != -1) {
            //need to consider header size
            int add = toFocusPos - 1 >= 0 && toFocusRecipeId == null ? 1 : 0;
            toFocusPos -= add;

            recyclerView.scrollToPosition(toFocusPos);
        }
    }

    public boolean isItemSelected(int pos) {
        return mItemAdapter.getAdapterItem(pos).isSelected();
    }

    private void showEmptyPlaceHolder() {
        emptyPlaceholder.setVisibility(View.VISIBLE);
    }

    private void hideEmptyPlaceHolder() {
        emptyPlaceholder.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.shopping_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.searchBox);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mItemAdapter.filter(null);
                } else {
                    mItemAdapter.filter(newText);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {

            showDeleteAllDialog();
        }
        return true;
    }

    private void showDeleteAllDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        String title = "Delete all";
        String message = "Are you sure you want to delete all ingredients?";
        String positiveButtonStr = "Yes";
        String negativeButtonStr = "Cancel";

        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(R.drawable.ic_delete_blue_24dp);

        dialog.setPositiveButton(positiveButtonStr, (dialog1, which) -> {
            Helper.INSTANCE.deleteAllFromDB((App) getActivity().getApplication());

            mItemAdapter.clear();

            showEmptyPlaceHolder();
        });

        dialog.setNegativeButton(negativeButtonStr, (dialog12, which) -> dialog12.cancel());

        dialog.setCancelable(true);

        dialog.show();
    }

    private void showDialogForHeader(String recipeName, final String recipeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String[] actions = {"Find recipe", "Mark as available",
                "Mark as not available", "Delete"};

        builder.setTitle(recipeName + ": ");

        builder.setItems(actions, (dialog1, which) -> {
            switch (which) {
                case 0:
                    startActivity(ViewRecipeActivity.getIntent(getActivity(), recipeId, false)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                    break;
                case 1:
                    for (int i = 0; i < mFastAdapter.getItemCount(); i++) {
                        IngredientItem item = mItemAdapter.getAdapterItem(i);

                        if (!item.entity.isAvailable && item.entity.recipeId.equals(recipeId)) {
                            mFastAdapter.getExtension(SelectExtension.class).select(i);
                        }
                    }
                    break;
                case 2:
                    for (int i = 0; i < mFastAdapter.getItemCount(); i++) {
                        IngredientItem item = mItemAdapter.getAdapterItem(i);

                        if (item.entity.isAvailable && item.entity.recipeId.equals(recipeId)) {
                            mFastAdapter.getExtension(SelectExtension.class).deselect(i);
                        }
                    }
                    break;
                case 3:
                    //showDeleteForHeader(recipeName, recipeId);
                    deleteAllIngredients(recipeId);
                    break;
            }
        });

        builder.show();
    }

    private void showDeleteForHeader(String recipeName, String recipeId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        String title = "Delete ingredients for \"" + recipeName + "\"";
        String message = "Are you sure you want to delete ingredients?";
        String positiveButtonStr = "Yes";
        String negativeButtonStr = "Cancel";

        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(R.drawable.ic_delete_blue_24dp);

        dialog.setPositiveButton(positiveButtonStr, (dialog1, which) -> {
            deleteAllIngredients(recipeId);
        });

        dialog.setNegativeButton(negativeButtonStr, (dialog12, which) -> dialog12.cancel());

        dialog.setCancelable(true);

        dialog.show();
    }

    private void deleteAllIngredients(String recipeId) {
        Helper.INSTANCE.deleteByRecipeIdFromDB((App) getActivity().getApplication(), recipeId);

        int start = -1;
        int count = 0;

            /*
            Use only if items sorted by recipeId!
             */
        for (int i = 0; i < mItemAdapter.getAdapterItemCount(); i++) {
            if (mItemAdapter.getAdapterItem(i).entity.recipeId.equals(recipeId)) {
                if (start == -1) start = i;

                count++;
            }
        }
        if (start != -1) {
            mItemAdapter.removeRange(start, count);
        }

        checkItemsCountForEmptyPlaceholder();
    }

    private void checkItemsCountForEmptyPlaceholder() {
        if (mFastAdapter.getItemCount() == 0) {
            showEmptyPlaceHolder();
        } else {
            hideEmptyPlaceHolder();
        }
    }

    @Override
    public boolean itemTouchOnMove(int oldPosition, int newPosition) {
        DragDropUtil.onMove(mItemAdapter, oldPosition, newPosition);
        return true;
    }

    @Override
    public void itemTouchDropped(int oldPosition, int newPosition) {

    }

    @Override
    public void itemSwiped(int position, int direction) {
        final IngredientItem item = mItemAdapter.getAdapterItem(position);
        item.setSwipedDirection(direction);

        final Runnable removeRunnable = () -> {
            item.setSwipedAction(null);
            int position12 = mItemAdapter.getAdapterPosition(item);
            if (position12 != RecyclerView.NO_POSITION) {

                mItemAdapter.getItemFilter().remove(position12);

                App app = null;
                if (getActivity() != null) app = (App) getActivity().getApplication();
                if (app != null) {
                    Helper.INSTANCE.deleteFromDB(app, item.entity);
                }
            }
            checkItemsCountForEmptyPlaceholder();
        };

        mSwipeRunnables.add(removeRunnable);
        /*
        Week memory is VERY BAD! remove callbacks in onStop
         */
        recyclerView.postDelayed(removeRunnable, 2500);

        item.setSwipedAction(() -> {
            recyclerView.removeCallbacks(removeRunnable);
            mSwipeRunnables.remove(removeRunnable);

            item.setSwipedDirection(0);
            int position1 = mItemAdapter.getAdapterPosition(item);
            if (position1 != RecyclerView.NO_POSITION) {
                mFastAdapter.notifyItemChanged(position1);

            }
        });

        mFastAdapter.notifyItemChanged(position);
    }

    @Override
    public void onStop() {
        for (Runnable runnable : mSwipeRunnables) {
            runnable.run();
            recyclerView.removeCallbacks(runnable);
        }
        super.onStop();
    }
}
