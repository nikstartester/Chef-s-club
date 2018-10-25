package com.example.nikis.bludogramfirebase.Recipe.ViewRecipe.SingleRecipe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.RecipeData.RecipeAdapterData;
import com.example.nikis.bludogramfirebase.RecipeData.RecipeData;
import com.google.firebase.storage.StorageReference;


public class BaseViewRecipeFragment extends Fragment implements ViewRecipeActivity.OnUpdateViews {

    private ImageView imgMain;
    private TextView tvName, tvDescription;

    public BaseViewRecipeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_base_view_recipe, container, false);

        imgMain = v.findViewById(R.id.imgView_main);
        tvName = v.findViewById(R.id.tv_name);
        tvDescription = v.findViewById(R.id.tv_description);

        return v;
    }

    @Override
    public void onUpdate(RecipeData recipeData) {
        onUpdate((RecipeAdapterData) recipeData);
    }

    @Override
    public void onUpdate(RecipeAdapterData recipeAdapterData) {
        tvName.setText(recipeAdapterData.name);
        tvDescription.setText(recipeAdapterData.description);
        setImgMain(recipeAdapterData.uploadMainImagePath);
    }

    private void setImgMain(String imgPath){
        if(imgPath != null) {

            StorageReference storageReference = FirebaseReferences.getStorageReference(imgPath);

            /*GlideApp.with(imgMain.getContext())
                    .load(storageReference)
                    //.asBitmap()
                    .override(1080,1080)
                    .placeholder(R.color.zhihu_album_placeholder)
                    .thumbnail(0.2f)
                    .error(R.drawable.ic_add_a_photo_blue_108dp)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imgMain);*/
        }/*else GlideApp.with(imgMain.getContext())
                .load(R.drawable.ic_add_a_photo_blue_108dp)
                //.asBitmap()
                .override(1080,1080)
                .placeholder(R.color.zhihu_album_placeholder)
                .thumbnail(0.2f)
                .error(R.drawable.ic_add_a_photo_blue_108dp)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgMain);*/
    }

}
