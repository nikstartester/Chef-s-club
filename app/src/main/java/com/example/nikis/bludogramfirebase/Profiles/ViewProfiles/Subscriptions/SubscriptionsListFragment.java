package com.example.nikis.bludogramfirebase.Profiles.ViewProfiles.Subscriptions;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nikis.bludogramfirebase.BaseFragments.FragmentWithSearchButton;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.Profiles.Data.ProfileData;
import com.example.nikis.bludogramfirebase.Profiles.ViewProfiles.SingleProfile.ViewProfileActivityTest;
import com.example.nikis.bludogramfirebase.Profiles.ViewProfiles.Subscriptions.ViewHolder.SubscriptionViewHolder;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.ToSearcher;
import com.example.nikis.bludogramfirebase.Search.Profiles.Filter.ProfileFilterData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

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

        recyclerView.setNestedScrollingEnabled(false);

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
