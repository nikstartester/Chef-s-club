package com.example.nikis.bludogramfirebase.Recipe.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.nikis.bludogramfirebase.BaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OverviewData extends BaseData {

    public String name;

    public String description;

    public List<String> ingredientsList;

    public String mainImagePath;

    public List<String> imagePathsWithoutMainList;

    public List<String> allImagePathList;


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
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeStringList(this.ingredientsList);
        dest.writeStringList(this.imagePathsWithoutMainList);
    }

    public OverviewData() {
        ingredientsList = new ArrayList<>();
        imagePathsWithoutMainList = new ArrayList<>();
        allImagePathList = new ArrayList<>();
    }

    protected OverviewData(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.ingredientsList = in.createStringArrayList();
        this.imagePathsWithoutMainList = in.createStringArrayList();
    }

    public static final Parcelable.Creator<OverviewData> CREATOR = new Parcelable.Creator<OverviewData>() {
        @Override
        public OverviewData createFromParcel(Parcel source) {
            return new OverviewData(source);
        }

        @Override
        public OverviewData[] newArray(int size) {
            return new OverviewData[size];
        }
    };


}
