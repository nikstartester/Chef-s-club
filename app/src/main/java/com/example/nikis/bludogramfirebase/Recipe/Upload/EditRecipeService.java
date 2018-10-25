package com.example.nikis.bludogramfirebase.Recipe.Upload;

import android.content.Context;
import android.content.Intent;

import com.example.nikis.bludogramfirebase.Recipe.Data.RecipeData;
import com.example.nikis.bludogramfirebase.DataUploaderService;
import com.example.nikis.bludogramfirebase.Uploader;


public class EditRecipeService extends DataUploaderService<RecipeData> {

    public static Intent getIntent(Context context, RecipeData recipeData){
        Intent intent = new Intent(context, EditRecipeService.class);
        return intent.putExtra(EXTRA_DATA, recipeData);
    }

    @Override
    public Uploader<RecipeData> getUploader() {
        return new RecipeUploader(getApplicationContext());
    }
}
