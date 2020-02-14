package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments;

import android.content.Context;
import android.graphics.Rect;
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
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.DataWorkers.OnItemCountChanged;
import com.xando.chefsclub.DataWorkers.OnProgressListener;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.FirebaseList.FirebaseListAdapter;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.Profiles.ViewProfiles.SingleProfile.ViewProfileActivityTest;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.Data.CommentData;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.ViewHolder.CommentViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public abstract class CommentsListFragment extends Fragment {

    private static final String TAG = "CommentsListFragment";

    public static final int MAX_AT_START = 6;
    private static final String KEY_IS_SHOWING_ALL = "key_is_showing_all";

    @BindView(R.id.recycler_view_more)
    protected RecyclerView recyclerViewMore;

    @BindView(R.id.btn_more)
    protected Button moreBtn;

    private DatabaseReference mDatabaseReference;

    private volatile FirebaseListAdapter<CommentData, CommentItem> mAdapterMore;

    private OnUserAddedComment mOnUserAddedComment;
    private OnProgressListener mOnProgressListener;
    private CommentViewHolder.OnReplyComment mOnReplyComment;
    private OnItemCountChanged mItemCountChanged;
    private OnReplyMessageClick mReplyMessageClick;

    private boolean isShowingAll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            isShowingAll = savedInstanceState.getBoolean(KEY_IS_SHOWING_ALL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

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

        if (context instanceof OnProgressListener) {
            mOnProgressListener = (OnProgressListener) context;
        }

        if (context instanceof OnItemCountChanged) {
            mItemCountChanged = (OnItemCountChanged) context;
        }

        if (context instanceof OnReplyMessageClick) {
            mReplyMessageClick = (OnReplyMessageClick) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initRV(recyclerViewMore);

        mAdapterMore = new FirebaseListAdapter<CommentData, CommentItem>(createOptions(0)) {

            @Override
            public String getUniqueId(@NonNull CommentData data) {
                return data.commentId;
            }

            @NonNull
            @Override
            public CommentItem getNewItemInstance(@NonNull CommentData data, int pos) {
                if (data.replyId == null)
                    return new CommentItem(CommentsListFragment.this, data,
                            isShowingAll || pos < MAX_AT_START);
                else return new CommentItemSmall(CommentsListFragment.this, data,
                        isShowingAll || pos < MAX_AT_START);
            }

            @Override
            public boolean onItemChanged(CommentItem item, CommentData data, int pos) {
                return true;
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                updateProgress(ParcResourceByParc.Status.SUCCESS);

                int count = getSnapshots().size();

                if (mItemCountChanged != null)
                    mItemCountChanged.onItemCountChanged(count);

                String text = "More";

                if (count > MAX_AT_START && !isShowingAll) {
                    text = count - MAX_AT_START + " more";

                    moreBtn.setText(text);
                    moreBtn.setVisibility(View.VISIBLE);
                } else {
                    moreBtn.setVisibility(View.GONE);

                    moreBtn.setText(text);
                }
            }

            @Override
            public void onError(@NonNull DatabaseError databaseError) {
                super.onError(databaseError);

                updateProgress(ParcResourceByParc.Status.ERROR);
            }
        };

        mAdapterMore.withEventHook(new ClickEventHook<CommentItem>() {
            @Nullable
            @Override
            public List<View> onBindMany(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CommentViewHolder) {
                    ArrayList<View> views = new ArrayList<>(4);

                    CommentViewHolder castViewHolder = (CommentViewHolder) viewHolder;

                    views.add(castViewHolder.itemView.findViewById(R.id.comment_pofile_name));
                    views.add(castViewHolder.itemView.findViewById(R.id.comment_profile_image));
                    views.add(castViewHolder.itemView.findViewById(R.id.comment_reply));
                    views.add(castViewHolder.itemView.findViewById(R.id.reply_content));

                    return views;
                } else return null;
            }

            @Override
            public void onClick(View v, int position, @NotNull FastAdapter<CommentItem> fastAdapter, @NotNull CommentItem item) {
                switch (v.getId()) {
                    case R.id.comment_profile_image:
                    case R.id.comment_pofile_name:
                        startActivity(ViewProfileActivityTest.getIntent(getActivity(),
                                item.getCommentData().authorId));
                        break;
                    case R.id.comment_reply:
                        if (mOnReplyComment != null)
                            mOnReplyComment.onReplyComment(item.getCommentData());
                        break;
                    case R.id.reply_content:
                        onReplyMessageClick(item);
                        break;

                }
            }
        });

        recyclerViewMore.setAdapter(mAdapterMore);
    }

    private void initRV(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setNestedScrollingEnabled(false);
    }

    @NonNull
    private FirebaseRecyclerOptions<CommentData> createOptions(int limit) {
        Query query = limit == 0 ? getQuery(mDatabaseReference)
                : limit > 0 ? getQuery(mDatabaseReference).limitToFirst(limit)
                : getQuery(mDatabaseReference).limitToLast(Math.abs(limit));

        return new FirebaseRecyclerOptions.Builder<CommentData>()
                .setQuery(query, CommentData.class)
                .build();
    }

    private void onReplyMessageClick(CommentItem item) {
        String replyId = item.getCommentData().replyId;

        int pos = -1;

        for (int i = 0; i < mAdapterMore.getAdapterItemCount(); i++) {
            if (mAdapterMore.getAdapterItem(i).getCommentData().commentId.equals(replyId)) {
                pos = i;
                break;
            }
        }

        CommentItem itemReply = pos != -1 ? mAdapterMore.getAdapterItem(pos) : null;

        if (itemReply != null && itemReply.getViewHolder() != null) {
            if (mReplyMessageClick != null) {
                View v = itemReply.getViewHolder().itemView;

                Rect rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());

                mReplyMessageClick.onReplyMessageClick(rect, replyId);
            }

            itemReply.startHighlight();

        }

    }

    public void startListening() {
        if (mAdapterMore != null) {
            mAdapterMore.startListening();
            updateProgress(ParcResourceByParc.Status.LOADING);
        }
    }

    @OnClick(R.id.btn_more)
    public void more() {
        if (!isShowingAll) {
            for (int i = 0; i < mAdapterMore.getAdapterItemCount(); i++) {
                mAdapterMore.getAdapterItem(i).changeVisible(true);
            }

            isShowingAll = true;

            moreBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAdapterMore != null) {
            mAdapterMore.stopListening();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(KEY_IS_SHOWING_ALL, isShowingAll);
        super.onSaveInstanceState(outState);
    }

    private void updateProgress(ParcResourceByParc.Status status) {
        if (mOnProgressListener != null) {
            mOnProgressListener.onProgressChanged(status);
        }
    }

    @NonNull
    protected abstract Query getQuery(DatabaseReference databaseReference);

    @NonNull
    public abstract String getRecipeId();

    public interface OnUserAddedComment {
        void onUserAddedComment();
    }

    public interface OnReplyMessageClick {
        void onReplyMessageClick(Rect posInRv, String replyMesId);
    }
}
