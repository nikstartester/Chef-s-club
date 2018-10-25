package com.example.nikis.bludogramfirebase.Recipe.Data;

import android.os.Parcel;

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

    public List<Integer> categories;


    @Deprecated
    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    public OverviewData() {
        ingredientsList = new ArrayList<>();
        imagePathsWithoutMainList = new ArrayList<>();
        allImagePathList = new ArrayList<>();
        categories = new ArrayList<>();
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
        dest.writeString(this.mainImagePath);
        dest.writeStringList(this.imagePathsWithoutMainList);
        dest.writeStringList(this.allImagePathList);
        dest.writeList(this.categories);
    }

    protected OverviewData(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.ingredientsList = in.createStringArrayList();
        this.mainImagePath = in.readString();
        this.imagePathsWithoutMainList = in.createStringArrayList();
        this.allImagePathList = in.createStringArrayList();
        this.categories = new ArrayList<Integer>();
        in.readList(this.categories, Integer.class.getClassLoader());
    }

    public static final Creator<OverviewData> CREATOR = new Creator<OverviewData>() {
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
