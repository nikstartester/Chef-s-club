package com.xando.chefsclub.profiles.viewprofiles.subscriptions;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.R;
import com.xando.chefsclub.basescreen.fragment.FragmentWithSearchButton;
import com.xando.chefsclub.profiles.data.ProfileData;
import com.xando.chefsclub.profiles.viewprofiles.single.ViewProfileActivityTest;
import com.xando.chefsclub.profiles.viewprofiles.subscriptions.viewholder.SubscriptionViewHolder;
import com.xando.chefsclub.recipes.viewrecipes.ToSearcher;
import com.xando.chefsclub.search.profiles.filter.ProfileFilterData;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class SubscriptionsListFragment extends FragmentWithSearchButton {

    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;

    @BindView(R.id.filter)
    protected View filterForProgress;

    @BindView(R.id.empty_placeholder)
    protected View emptyPlaceholder;

    private DatabaseReference mDatabaseReference;

    private FirebaseRecyclerAdapter<ProfileData, SubscriptionViewHolder> mAdapter;

    private ToSearcher mToSearcher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabaseReference = FirebaseReferences.getDataBaseReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        ButterKnife.bind(this, view);

        showProgress();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ToSearcher) {
            mToSearcher = (ToSearcher) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Query query = getQuery(mDatabaseReference);

        FirebaseRecyclerOptions<ProfileData> options = new FirebaseRecyclerOptions.Builder<ProfileData>()
                .setQuery(query, ProfileData.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<ProfileData, SubscriptionViewHolder>(options) {
            @NonNull
            @Override
            public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new SubscriptionViewHolder(inflater.inflate(R.layout.list_subscription_item,
                        parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull SubscriptionViewHolder holder, int position, @NonNull ProfileData model) {
                holder.bind(model);

                holder.itemView.setOnClickListener(v -> startActivity(ViewProfileActivityTest
                        .getIntent(getActivity(), model.userUid)));

            }

            @Override
            public void onDataChanged() {
                if (getSnapshots().size() > 0) {
                    hideEmptyPlaceholder();
                } else showEmptyPlaceholder();

                super.onDataChanged();

                hideProgress();
            }
        };

        recyclerView.setAdapter(mAdapter);
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

    @Override
    protected void toSearch() {
        if (mToSearcher != null)
            mToSearcher.toSearch(ToSearcher.LOOK_FOR_PROFILES, ProfileFilterData.FROM_SUBSCRIPTIONS);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    protected abstract Query getQuery(DatabaseReference databaseReference);
}
