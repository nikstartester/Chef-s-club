package com.example.nikis.bludogramfirebase.Recipe.ViewRecipe;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.RecipeData.RecipeAdapterData;
import com.example.nikis.bludogramfirebase.UserData.UserNickName;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

import static com.example.nikis.bludogramfirebase.Recipe.ViewRecipe.RecipesListFragment.getUid;


public class RecipeViewHolder extends RecyclerView.ViewHolder {
    private TextView name, description, timeCooking;
    private ImageView image, imageFavorite;
    private TextView starCount;
    private TextView userLogin;

    private String login;

    public RecipeViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.tv_name);
        description = itemView.findViewById(R.id.tv_description);
        timeCooking = itemView.findViewById(R.id.tv_timeCooking);
        image = itemView.findViewById(R.id.img_image);
        starCount = itemView.findViewById(R.id.tv_starCount);
        userLogin = itemView.findViewById(R.id.tv_userLogin);
        imageFavorite = itemView.findViewById(R.id.imgFavorite);
    }
    public void bindToRecipe(final RecipeAdapterData recipeAdapterData, View.OnClickListener favoriteClick){
        name.setText(recipeAdapterData.name);

        description.setText(recipeAdapterData.description);

        starCount.setText(Integer.toString(recipeAdapterData.starCount));

        timeCooking.setText(recipeAdapterData.allTimeCooking);

        //setUserLogin(recipeAdapterData.uid);
        userLogin.setText(recipeAdapterData.userLogin);

        setFavoriteImage(recipeAdapterData.stars);

        imageFavorite.setOnClickListener(favoriteClick);

        if(recipeAdapterData.uploadMainImagePath != null) {

            StorageReference storageReference = FirebaseReferences.getStorageReference(recipeAdapterData.uploadMainImagePath);

            /*GlideApp.with(image.getContext())
                    .load(storageReference)
                    //.asBitmap()
                    .override(1080,1080)
                    .placeholder(R.color.zhihu_album_placeholder)
                    .thumbnail(0.2f)
                    .error(R.drawable.ic_add_a_photo_blue_108dp)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(image);*/
        }/*else GlideApp.with(image.getContext())
                .load(R.drawable.ic_add_a_photo_blue_108dp)
                //.asBitmap()
                .override(1080,1080)
                .placeholder(R.color.zhihu_album_placeholder)
                .thumbnail(0.2f)
                .error(R.drawable.ic_add_a_photo_blue_108dp)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image);*/
    }

    private void setUserLogin(String uid){
        DatabaseReference databaseReference = FirebaseReferences.getDataBaseReference();
        databaseReference.child("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserNickName nickName = dataSnapshot.getValue(UserNickName.class);


                if (nickName != null) {
                    login = nickName.getLogin();
                    userLogin.setText(login);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private boolean setFavoriteImage(Map<String, Boolean> stars){
        if (stars.containsKey(getUid())) {
            imageFavorite.setImageResource(R.drawable.ic_star_blue_24dp);
            return true;
        } else {
            imageFavorite.setImageResource(R.drawable.ic_star_border_blue_24dp);
            return false;
        }
    }
}
