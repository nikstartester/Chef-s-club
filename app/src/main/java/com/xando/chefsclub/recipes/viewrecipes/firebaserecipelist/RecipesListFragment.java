package com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.R;
import com.xando.chefsclub.dataworkers.OnItemCountChanged;
import com.xando.chefsclub.firebaseList.FirebaseListAdapter;
import com.xando.chefsclub.recipes.data.ActualRecipeDataChecker;
import com.xando.chefsclub.recipes.data.RecipeData;
import com.xando.chefsclub.recipes.viewrecipes.firebaserecipelist.item.RecipeItem;
import com.xando.chefsclub.recipes.viewrecipes.singlerecipe.ViewRecipeActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xando.chefsclub.helper.FirebaseHelper.getUid;

public abstract class RecipesListFragment extends RecipeEventHookFragment {

    private static final String TAG = "RecipesListFragment";

    protected static final int REQUEST_CODE_RECIPE_ACTIVITY = 121;
    private static final String KEY_DATA_LIST = "DATA_LIST";

    private final ActualRecipeDataChecker mDataChecker = new ActualRecipeDataChecker();

    @BindView(R.id.rv_allRecipes)
    protected RecyclerView allRecipesRv;
    @BindView(R.id.filter)
    protected View filterForProgress;
    @BindView(R.id.empty_placeholder)
    protected View emptyPlaceholder;
    @BindView(R.id.noInternet)
    protected RelativeLayout noInternet;
    private DatabaseReference databaseReference;

    private FirebaseListAdapter<RecipeData, RecipeItem> mAdapter;

    private OnItemCountChanged mItemCountChanged;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseReferences.getDataBaseReference();

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_all_recipes, container, false);

        ButterKnife.bind(this, view);

        showProgress();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        allRecipesRv.setLayoutManager(layoutManager);

        allRecipesRv.setItemAnimator(new DefaultItemAnimator());

        allRecipesRv.setNestedScrollingEnabled(isNestedScrolling());

        List<RecipeData> recipeDataList = null;
        if (savedInstanceState != null)
            recipeDataList = savedInstanceState.getParcelableArrayList(KEY_DATA_LIST);

        initList(recipeDataList);

        return view;
    }

    private void initList(List<RecipeData> recipeDataList) {
        Query query = getQuery(databaseReference);

        query.keepSynced(true);

        FirebaseRecyclerOptions<RecipeData> options = new FirebaseRecyclerOptions.Builder<RecipeData>()
                .setQuery(query, RecipeData.class)
                .build();

        mAdapter = new FirebaseListAdapter<RecipeData, RecipeItem>(options, recipeDataList) {
            @Override
            public String getUniqueId(@NonNull RecipeData data) {
                return data.recipeKey;
            }

            @NonNull
            @Override
            public RecipeItem getNewItemInstance(@NonNull RecipeData data, int pos) {
                return new RecipeItem(data, RecipesListFragment.this);
            }

            @Override
            public boolean onItemChanged(RecipeItem item, RecipeData data, int pos) {
                boolean isNeedUpdateUi = item.onUpdateData(data, mDataChecker);

                //Log.d(TAG, "onItemChanged: isNeedUpdate: " + isNeedUpdateUi);
                return isNeedUpdateUi;
            }

            @Override
            public void onDataChanged() {
                if (getSnapshots().size() > 0) {
                    hideEmptyPlaceholder();
                } else showEmptyPlaceholder();

                super.onDataChanged();

                hideProgress();
                hideInternetError();

                if (mItemCountChanged != null)
                    mItemCountChanged.onItemCountChanged(getSnapshots().size());


            }
        };

        mAdapter.withOnClickListener((v, adapter, item, position) -> {

            boolean b = item.getRecipeData().authorUId.equals(getUid());

            startActivityForResult(ViewRecipeActivity.getIntent(getActivity(), item.getRecipeData().recipeKey, b),
                    REQUEST_CODE_RECIPE_ACTIVITY);

            return true;
        });

        setClickEventHook();

        allRecipesRv.setAdapter(mAdapter);

    }

    private void setClickEventHook() {
        ClickEventHook<RecipeItem> clickEventHook;
        if (getCustomClickEventHook() != null) {
            clickEventHook = getCustomClickEventHook();
        } else clickEventHook = new EventHookForRecipeItem<>();

        mAdapter.withEventHook(clickEventHook);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnItemCountChanged) {
            mItemCountChanged = (OnItemCountChanged) context;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null)
            mAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            for (RecipeItem item : mAdapter.getAdapterItems()) {
                item.stopAnimations();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.stopListening();

            for (RecipeItem item : mAdapter.getAdapterItems()) {
                item.unbind();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RECIPE_ACTIVITY) {
            onResultFromRecipeActivity(resultCode, data);
        }

    }

    protected void onResultFromRecipeActivity(int resultCode, Intent data) {

    }

    private void showProgress() {
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.INVISIBLE);
        }
    }

    private void showEmptyPlaceholder() {
        emptyPlaceholder.setVisibility(View.VISIBLE);
    }

    private void hideEmptyPlaceholder() {
        emptyPlaceholder.setVisibility(View.GONE);
    }

    private void showInternetError() {
        if (noInternet != null) {
            noInternet.setVisibility(View.VISIBLE);
        }
    }

    private void hideInternetError() {
        if (noInternet != null) {
            noInternet.setVisibility(View.GONE);
        }
    }

    protected abstract Query getQuery(DatabaseReference databaseReference);

    protected abstract boolean isNestedScrolling();

    @Nullable
    protected abstract ClickEventHook<RecipeItem> getCustomClickEventHook();
}
