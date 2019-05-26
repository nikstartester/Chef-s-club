package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SingleRecipe.Comments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.Helpers.FirebaseHelper;
import com.example.nikis.bludogramfirebase.Profiles.ViewProfiles.SingleProfile.ViewProfileActivityTest;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SingleRecipe.Comments.Data.CommentData;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SingleRecipe.Comments.ViewHolder.CommentViewHolder;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class CommentsListFragment extends Fragment {
    private static final String TAG = "CommentsListFragment";

    @BindView(R.id.recycler_view)
    protected volatile RecyclerView recyclerView;

    private DatabaseReference mDatabaseReference;
    private volatile FirebaseRecyclerAdapter<CommentData, CommentViewHolder> mAdapter;

    private OnUserAddedComment mOnUserAddedComment;

    private CommentViewHolder.OnReplyComment mOnReplyComment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        ButterKnife.bind(this, view);

        mDatabaseReference = FirebaseReferences.getDataBaseReference();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnUserAddedComment) {
            mOnUserAddedComment = (OnUserAddedComment) context;
        }

        if (context instanceof CommentViewHolder.OnReplyComment) {
            mOnReplyComment = (CommentViewHolder.OnReplyComment) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setHasFixedSize(false);

        Query query = getQuery(mDatabaseReference);


        FirebaseRecyclerOptions<CommentData> options = new FirebaseRecyclerOptions.Builder<CommentData>()
                .setQuery(query, CommentData.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<CommentData, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull CommentData model) {

                holder.bindToComment(CommentsListFragment.this, model);

                holder
                        .addClickerToAuthorData(
                                v -> startActivity(ViewProfileActivityTest.getIntent(getActivity(),
                                        model.authorId)))
                        .addOnReplyComment(commentData -> {
                            if (mOnReplyComment != null)
                                mOnReplyComment.onReplyComment(commentData);
                        });
                holder.getItemId();

            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);

                if (type == ChangeEventType.ADDED) {

                    if (snapshot.getValue(CommentData.class).authorId.equals(FirebaseHelper.getUid())) {
                        //if(mOnUserAddedComment != null) mOnUserAddedComment.onItemAdded();
                    }
                }
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new CommentViewHolder(inflater.inflate(R.layout.list_comment_item, parent,
                        false));
            }


        };

        recyclerView.setAdapter(mAdapter);
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

    @NonNull
    protected abstract Query getQuery(DatabaseReference databaseReference);

    @NonNull
    public abstract String getRecipeId();

    public interface OnUserAddedComment {
        void onUserAddedComment();
    }
}
