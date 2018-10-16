package com.example.nikis.bludogramfirebase.Recipe.Data;

import android.os.Parcel;

import com.example.nikis.bludogramfirebase.BaseData;

import java.util.Map;

public class RecipeData extends BaseData{
    public OverviewData overviewData;
    public StepsData stepsData;

    @Deprecated
    @Override
    public Map<String, Object> toMap() {
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.overviewData, flags);
        dest.writeParcelable(this.stepsData, flags);
    }

    public RecipeData() {
        overviewData = new OverviewData();
        stepsData = new StepsData();
    }

    protected RecipeData(Parcel in) {
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
