package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.xando.chefsclub.DataWorkers.BaseRepository;
import com.xando.chefsclub.DataWorkers.DataUploader;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.FirebaseReferences;
import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.Data.CommentData;

import java.util.HashMap;
import java.util.Map;

import static com.xando.chefsclub.Recipes.Repository.RecipeRepository.CHILD_RECIPES;


public class CommentUploader extends DataUploader<CommentData> {
    public static void updtInReplays(CommentData data, String id) {
        final DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

        String child = "/comments/recipes/" + data.recipeId + "/" + data.commentId;

        myRef.child(child).child("isInReply").setValue(true);
        myRef.child(child).child("inReplies").child(id).setValue(true);
    }

    @Override
    protected void start() {
        DatabaseReference ref = FirebaseReferences.getDataBaseReference();

        ref.child(CHILD_RECIPES).child(mData.recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RecipeData recipeData = dataSnapshot.getValue(RecipeData.class);

                if (recipeData != null) {
                    updateChildren();
                } else {
                    mDataResource = ParcResourceByParc
                            .error(new BaseRepository.NothingFoundFromServerException("Action is impossible:" +
                                    " recipe has been deleted"), mData);
                    updateProgress(mDataResource);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateChildren() {
        final DatabaseReference myRef = FirebaseReferences.getDataBaseReference();

        if (mData.commentId == null) {
            mData.commentId = myRef.child("comments/recipes").child(mData.recipeId).push().getKey();
        }


        Map<String, Object> childUpdates = createChildUpdates();

        myRef.updateChildren(childUpdates, (databaseError, databaseReference) -> {
            if (databaseError == null) {

                mDataResource = ParcResourceByParc.success(mData);

                updateProgress(mDataResource);
            } else {
                mDataResource = ParcResourceByParc.error(databaseError.toException(), mData);

                updateProgress(mDataResource);
            }
        });
    }

    private Map<String, Object> createChildUpdates() {
        //mData.date = System.currentTimeMillis();

        Map<String, Object> postValues = mData.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/comments/recipes/" + mData.recipeId + "/" + mData.commentId, postValues);

        return childUpdates;
    }

    @Override
    protected boolean checkRelevance(CommentData data) {
        return false;
    }

    @Override
    protected void cancel() {
        /*
        not canceling
         */
    }
}
