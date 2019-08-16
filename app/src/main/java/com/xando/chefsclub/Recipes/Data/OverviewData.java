package com.xando.chefsclub.Recipes.Data;

import android.arch.persistence.room.Entity;
import android.os.Parcel;

import com.xando.chefsclub.DataWorkers.BaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
public class OverviewData extends BaseData implements Cloneable {

    public String name;

    public String description;

    public List<String> ingredientsList;

    public String mainImagePath;

    public List<String> imagePathsWithoutMainList;

    public List<String> allImagePathList;

    public List<String> strCategories;

    public OverviewData() {
        ingredientsList = new ArrayList<>();
        imagePathsWithoutMainList = new ArrayList<>();
        allImagePathList = new ArrayList<>();
        strCategories = new ArrayList<>();
    }

    @Deprecated
    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public OverviewData clone() {
        OverviewData clone = new OverviewData();

        clone.name = name;
        clone.description = description;
        clone.ingredientsList = new ArrayList<>(ingredientsList);
        clone.mainImagePath = mainImagePath;
        clone.imagePathsWithoutMainList = new ArrayList<>(imagePathsWithoutMainList);
        clone.allImagePathList = new ArrayList<>(allImagePathList);
        clone.strCategories = new ArrayList<>(strCategories);

        return clone;
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
        dest.writeList(this.strCategories);
    }

    OverviewData(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.ingredientsList = in.createStringArrayList();
        this.mainImagePath = in.readString();
        this.imagePathsWithoutMainList = in.createStringArrayList();
        this.allImagePathList = in.createStringArrayList();
        this.strCategories = new ArrayList<>();
        in.readList(this.strCategories, Integer.class.getClassLoader());
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
