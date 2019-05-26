package com.example.nikis.bludogramfirebase.Recipes.Upload;

import android.os.Parcel;

import com.example.nikis.bludogramfirebase.DataWorkers.BaseData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class RecipeImagesData extends BaseData {
    public String recipeKey;

    public OverviewRecipesImages overviewImages = new OverviewRecipesImages();

    public List<String> stepsImages = new ArrayList<>();

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("recipeKey", recipeKey);
        map.put("overviewImages", overviewImages);
        map.put("stepsImages", stepsImages);

        return map;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.recipeKey);
        dest.writeParcelable(this.overviewImages, flags);
        dest.writeStringList(this.stepsImages);
    }

    public RecipeImagesData() {
    }

    protected RecipeImagesData(Parcel in) {
        this.recipeKey = in.readString();
        this.overviewImages = in.readParcelable(OverviewRecipesImages.class.getClassLoader());
        this.stepsImages = in.createStringArrayList();
    }

    public static final Creator<RecipeImagesData> CREATOR = new Creator<RecipeImagesData>() {
        @Override
        public RecipeImagesData createFromParcel(Parcel source) {
            return new RecipeImagesData(source);
        }

        @Override
        public RecipeImagesData[] newArray(int size) {
            return new RecipeImagesData[size];
        }
    };
}
