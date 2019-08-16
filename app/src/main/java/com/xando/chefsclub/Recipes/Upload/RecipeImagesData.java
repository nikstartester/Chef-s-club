package com.xando.chefsclub.Recipes.Upload;

import android.os.Parcel;

import com.xando.chefsclub.DataWorkers.BaseData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecipeImagesData extends BaseData {
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
    public String recipeKey;
    public OverviewRecipesImages overviewImages = new OverviewRecipesImages();
    public List<String> stepsImages = new ArrayList<>();

    public RecipeImagesData() {
    }

    protected RecipeImagesData(Parcel in) {
        this.recipeKey = in.readString();
        this.overviewImages = in.readParcelable(OverviewRecipesImages.class.getClassLoader());
        this.stepsImages = in.createStringArrayList();
    }

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
}
