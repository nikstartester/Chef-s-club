package com.example.nikis.bludogramfirebase.Recipe.ViewRecipe;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.RecipeData.RecipeAdapterData;
import com.example.nikis.bludogramfirebase.Work.ViewRecipeActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import static com.example.nikis.bludogramfirebase.Work.ViewRecipeActivity.KEY_TAG;


public abstract class RecipesListFragment extends Fragment {
    private RecyclerView allRecipesRv;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<RecipeAdapterData, RecipeViewHolder> adapter;

    private RelativeLayout filterForProgress;
    private RelativeLayout noInternet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_all_recipes, container, false);
        allRecipesRv = view.findViewById(R.id.rv_allRecipes);
        databaseReference = FirebaseReferences.getDataBaseReference();

        filterForProgress = view.findViewById(R.id.filter);
        noInternet = view.findViewById(R.id.noInternet);

        /* if(isOnline())
            showProgress();
        else showInternetError();*/

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        allRecipesRv.setLayoutManager(layoutManager);
        allRecipesRv.setItemAnimator(new DefaultItemAnimator());

        Query postsQuery = getQuery(databaseReference);

        FirebaseRecyclerOptions<RecipeAdapterData> options = new FirebaseRecyclerOptions.Builder<RecipeAdapterData>()
                .setQuery(postsQuery, RecipeAdapterData.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<RecipeAdapterData, RecipeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecipeViewHolder holder, int position, @NonNull RecipeAdapterData model) {
                final DatabaseReference recipeRef = getRef(position);

                // Set click listener for the whole post view
                final String recipeKey = recipeRef.getKey();

                holder.bindToRecipe(model, v -> {
                    DatabaseReference globalPostRef = databaseReference.child("recipes").child(recipeKey);
                    DatabaseReference userPostRef = databaseReference.child("user-recipes").child(model.uid).child(recipeKey);

                    // Run two transactions
                    onStarClicked(globalPostRef);
                    onStarClicked(userPostRef);
                });
                holder.itemView.setOnClickListener(v -> startActivity(
                        new Intent(getActivity(), ViewRecipeActivity.class)
                                .putExtra(KEY_TAG, recipeKey)));
            }


            @NonNull
            @Override
            public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new RecipeViewHolder(inflater.inflate(R.layout.list_recipe_item, parent, false));
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                hideProgress();
                hideInternetError();
            }
        };
        allRecipesRv.setAdapter(adapter);

    }

    private void onStarClicked(DatabaseReference ref) {
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                RecipeAdapterData recipeAdapterData = mutableData.getValue(RecipeAdapterData.class);
                if (recipeAdapterData == null) {
                    return Transaction.success(mutableData);
                }

                if (recipeAdapterData.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    recipeAdapterData.starCount = recipeAdapterData.starCount - 1;
                    recipeAdapterData.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    recipeAdapterData.starCount = recipeAdapterData.starCount + 1;
                    recipeAdapterData.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(recipeAdapterData);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("click", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private boolean isOnline(){
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter!= null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void showProgress(){
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.VISIBLE);
        }
    }
    private void hideProgress(){
        if (filterForProgress != null) {
            filterForProgress.setVisibility(View.INVISIBLE);
        }
    }

    private void showInternetError(){
        if (noInternet != null) {
            noInternet.setVisibility(View.VISIBLE);
        }
    }
    private void hideInternetError(){
        if (noInternet != null) {
            noInternet.setVisibility(View.GONE);
        }
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}
