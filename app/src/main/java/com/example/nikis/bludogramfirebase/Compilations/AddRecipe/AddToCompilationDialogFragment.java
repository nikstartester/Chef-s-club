package com.example.nikis.bludogramfirebase.Compilations.AddRecipe;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nikis.bludogramfirebase.Compilations.AddRecipe.Item.CompilationShortItem;
import com.example.nikis.bludogramfirebase.Compilations.Data.ArrayCompilations;
import com.example.nikis.bludogramfirebase.Compilations.Data.CompilationData;
import com.example.nikis.bludogramfirebase.Compilations.EditCompilation.EditCompilationDialogFragment;
import com.example.nikis.bludogramfirebase.Compilations.Sync.SyncCompilationService;
import com.example.nikis.bludogramfirebase.Compilations.ViewModel.CompilationsViewModel;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceByParc;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.Helpers.NetworkHelper;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.Data.RecipeData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.nikis.bludogramfirebase.Recipes.Repository.RecipeRepository.CHILD_RECIPES;


public class AddToCompilationDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_RECIPE_DATA = "RECIPE_DATA";

    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;

    private FastItemAdapter<CompilationShortItem> mAdapter;
    private CompilationsViewModel mViewModel;

    private RecipeData mRecipeData;
    private SyncCompilationsTittleBReceiver mBReceiver;

    public static Bundle getArguments(RecipeData recipeData) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_RECIPE_DATA, recipeData);

        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogFragmentStyle);

        if (getArguments() != null) {
            mRecipeData = getArguments().getParcelable(KEY_RECIPE_DATA);
        }

        mAdapter = new FastItemAdapter<>();
        mViewModel = ViewModelProviders.of(this).get(CompilationsViewModel.class);

        IntentFilter intentFilter = new IntentFilter(
                SyncCompilationService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        getActivity().registerReceiver(mBReceiver = new SyncCompilationsTittleBReceiver(),
                intentFilter);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setTitle("Add to compilation");
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_compilation, container, false);

        ButterKnife.bind(this, view);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mViewModel.getData().observe(this, dataList -> {
            if (dataList != null) {
                onDataLoaded(dataList);
            }
        });

        if (mViewModel.getData().getValue() == null) {
            mViewModel.loadData();
        }

        mAdapter.withOnClickListener((v, adapter, item, position) -> {
            if (NetworkHelper.isConnected(getActivity())) {
                if (mRecipeData != null && !item.getCompilationData().recipesKey.contains(mRecipeData.recipeKey)) {
                    startAddToCompilationWithCheck(item.getCompilationData());
                } else dismiss();
            } else {
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        return view;
    }

    private void startAddToCompilation(CompilationData data) {
        new FirebaseHelper.Compilations.CompilationActions()
                .addToRecipe(data, mRecipeData.recipeKey)
                .saveChangesOnCompilation()
                .updateCompilationOnServer();
    }

    private void startAddToCompilationWithCheck(CompilationData compilationData) {
        DatabaseReference ref = FirebaseReferences.getDataBaseReference();

        ref.child(CHILD_RECIPES).child(mRecipeData.recipeKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RecipeData recipeData = dataSnapshot.getValue(RecipeData.class);

                if (recipeData != null) {
                    startAddToCompilation(compilationData);

                    onSuccessRecipeAdded(compilationData);
                } else {
                    Toast.makeText(getActivity(),
                            getString(R.string.impossible_actions_recipe_deleted), Toast.LENGTH_SHORT).show();

                    dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void onSuccessRecipeAdded(CompilationData compilationData) {
        Toast.makeText(getActivity(), "Recipe added to \"" + compilationData.name + " \"", Toast.LENGTH_SHORT)
                .show();
        dismiss();
    }

    private void onError(Exception ex) {
        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBReceiver);
    }

    private void onDataLoaded(List<CompilationData> list) {
        mAdapter.clear();
        for (CompilationData tittle : list) {
            mAdapter.add(new CompilationShortItem(tittle));
        }
    }

    @OnClick(R.id.btn_cancel)
    protected void cancel() {
        dismiss();
    }

    @OnClick(R.id.btn_new_compilation)
    protected void newCompilation() {
        EditCompilationDialogFragment dialogFragment = new EditCompilationDialogFragment();

        dialogFragment.setArguments(EditCompilationDialogFragment.getArguments(mRecipeData));

        dialogFragment.show(getFragmentManager(), "createNewCompilation");

        dismiss();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private class SyncCompilationsTittleBReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ParcResourceByParc<ArrayCompilations> dataResource = intent
                    .getParcelableExtra(SyncCompilationService.EXTRA_RESOURCE);

            if (dataResource != null && dataResource.status == ParcResourceByParc.Status.SUCCESS) {

                onSuccess(dataResource);
            } else if (dataResource != null && dataResource.status == ParcResourceByParc.Status.LOADING) {
                showProgress();
            } else if (dataResource != null && dataResource.status == ParcResourceByParc.Status.ERROR) {
                onError(dataResource);
            }
        }

        private void onSuccess(ParcResourceByParc<ArrayCompilations> dataResource) {
            hideProgress();
        }

        private void onError(ParcResourceByParc<ArrayCompilations> dataResource) {
            hideProgress();
        }
    }

}
