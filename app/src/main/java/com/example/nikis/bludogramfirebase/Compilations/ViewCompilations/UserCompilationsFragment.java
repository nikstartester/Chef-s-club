package com.example.nikis.bludogramfirebase.Compilations.ViewCompilations;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nikis.bludogramfirebase.Compilations.Data.CompilationData;
import com.example.nikis.bludogramfirebase.Compilations.EditCompilation.EditCompilationDialogFragment;
import com.example.nikis.bludogramfirebase.Compilations.ViewCompilations.Item.CompilationItem;
import com.example.nikis.bludogramfirebase.Compilations.ViewModel.CompilationsViewModel;
import com.example.nikis.bludogramfirebase.Helpers.NetworkHelper;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.Compilations.RecipesActivity;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserCompilationsFragment extends Fragment {
    private static final String TAG = "UserCompilationsFragmen";

    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;
    @BindView(R.id.empty_placeholder)
    protected View emptyPlaceholder;

    private CompilationsViewModel mViewModel;
    private FastItemAdapter<CompilationItem> mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(CompilationsViewModel.class);

        mAdapter = new FastItemAdapter<>();

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

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
            startActivity(RecipesActivity.getIntent(getActivity(), item.getCompilationData()));

            return true;
        });

        mAdapter.withEventHook(new ClickEventHook<CompilationItem>() {

            @Nullable
            @Override
            public List<View> onBindMany(RecyclerView.ViewHolder viewHolder) {
                List<View> views = new ArrayList<>();
                if (viewHolder instanceof CompilationItem.ViewHolder) {
                    CompilationItem.ViewHolder castViewHolder = (CompilationItem.ViewHolder) viewHolder;

                    views.add(castViewHolder.itemView.findViewById(R.id.img_more));
                }

                return views;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<CompilationItem> fastAdapter, CompilationItem item) {
                switch (v.getId()) {
                    case R.id.img_more:
                        moreClick(item);
                        break;
                }
            }
        });
        return view;
    }

    private void onDataLoaded(List<CompilationData> list) {
        mAdapter.clear();
        if (list.size() == 0) {
            showEmptyPlaceholder();
        } else {
            hideEmptyPlaceholder();

            for (CompilationData tittle : list) {
                mAdapter.add(new CompilationItem(tittle));
            }
        }

    }

    private void moreClick(CompilationItem item) {
        if (item.getMoreBtn() == null) return;

        PopupMenu popupMenu = new PopupMenu(getActivity(), item.getMoreBtn());
        popupMenu.inflate(R.menu.compilation_more_menu);

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.act_rename:
                    rename(item.getCompilationData());
                    break;
                case R.id.act_delete:
                    delete(item.getCompilationData());
                    break;
            }
            return true;
        });

        popupMenu.show();
    }

    private void rename(CompilationData compilation) {
        EditCompilationDialogFragment dialog = new EditCompilationDialogFragment();

        dialog.setArguments(EditCompilationDialogFragment.getArguments(compilation));

        dialog.show(getChildFragmentManager(), "renameCompilation");
    }

    private void delete(CompilationData compilation) {
        if (NetworkHelper.isConnected(getActivity())) {
            mViewModel.getRepository().deleteFromServer(compilation);
        } else
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();

    }

    private void showEmptyPlaceholder() {
        emptyPlaceholder.setVisibility(View.VISIBLE);
    }

    private void hideEmptyPlaceholder() {
        emptyPlaceholder.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_btn_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                EditCompilationDialogFragment dialogFragment = new EditCompilationDialogFragment();

                dialogFragment.show(getChildFragmentManager(), "createNewCompilation");

                return true;
        }
        return false;
    }
}
