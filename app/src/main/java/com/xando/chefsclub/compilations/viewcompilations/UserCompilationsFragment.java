package com.xando.chefsclub.compilations.viewcompilations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.R;
import com.xando.chefsclub.compilations.data.CompilationData;
import com.xando.chefsclub.compilations.editcompilation.EditCompilationDialogFragment;
import com.xando.chefsclub.compilations.viewcompilations.Item.CompilationItem;
import com.xando.chefsclub.compilations.viewmodel.CompilationsViewModel;
import com.xando.chefsclub.helper.NetworkHelper;
import com.xando.chefsclub.recipes.viewrecipes.compilations.RecipesActivity;

import org.jetbrains.annotations.NotNull;

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

        mViewModel.getData().observe(getViewLifecycleOwner(), dataList -> {
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
            public void onClick(@NotNull View v, int position, @NotNull FastAdapter<CompilationItem> fastAdapter, @NotNull CompilationItem item) {
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
