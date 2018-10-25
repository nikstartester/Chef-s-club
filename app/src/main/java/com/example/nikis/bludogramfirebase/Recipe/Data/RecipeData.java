package com.example.nikis.bludogramfirebase.Recipe.Data;

import android.os.Parcel;

import com.example.nikis.bludogramfirebase.BaseData;

import java.util.HashMap;
import java.util.Map;

public class RecipeData extends BaseData{

    public String recipeKey;

    public String authorUId;

    public int starCount;

    public long dateTime;

    public Map<String, Boolean> stars = new HashMap<>();

    public OverviewData overviewData;

    public StepsData stepsData;

    public RecipeData() {
        this(new OverviewData(), new StepsData());
    }

    public RecipeData(OverviewData overviewData, StepsData stepsData) {
        this.overviewData = overviewData;
        this.stepsData = stepsData;
    }


    @Override
    public Map<String, Object> toMap() {

        HashMap<String, Object> map = new HashMap<>();

        map.put("recipeKey", recipeKey);
        map.put("authorUId", authorUId);
        map.put("starCount", starCount);
        map.put("overviewData", overviewData);
        map.put("stepsData", stepsData);
        map.put("dateTime", dateTime);
        map.put("stars", stars);

        return map;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.recipeKey);
        dest.writeString(this.authorUId);
        dest.writeInt(this.starCount);
        dest.writeLong(this.dateTime);
        dest.writeInt(this.stars.size());
        for (Map.Entry<String, Boolean> entry : this.stars.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeParcelable(this.overviewData, flags);
        dest.writeParcelable(this.stepsData, flags);
    }

    protected RecipeData(Parcel in) {
        this.recipeKey = in.readString();
        this.authorUId = in.readString();
        this.starCount = in.readInt();
        this.dateTime = in.readLong();
        int starsSize = in.readInt();
        this.stars = new HashMap<String, Boolean>(starsSize);
        for (int i = 0; i < starsSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.stars.put(key, value);
        }
        this.overviewData = in.readParcelable(OverviewData.class.getClassLoader());
        this.stepsData = in.readParcelable(StepsData.class.getClassLoader());
    }

    public static final Creator<RecipeData> CREATOR = new Creator<RecipeData>() {
        @Override
        public RecipeData createFromParcel(Parcel source) {
            return new RecipeData(source);
        }

        @Override
        public RecipeData[] newArray(int size) {
            return new RecipeData[size];
        }
    };
}
