package com.xando.chefsclub.recipes.upload;

import android.os.Parcel;

import com.xando.chefsclub.dataworkers.BaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class OverviewRecipesImages extends BaseData {

    public String mainImagePath;

    public List<String> imagePathsWithoutMainList = new ArrayList<>();

    public List<String> allImagePathList = new ArrayList<>();

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
        dest.writeString(this.mainImagePath);
        dest.writeStringList(this.imagePathsWithoutMainList);
        dest.writeStringList(this.allImagePathList);
    }

    OverviewRecipesImages() {
    }

    private OverviewRecipesImages(Parcel in) {
        this.mainImagePath = in.readString();
        this.imagePathsWithoutMainList = in.createStringArrayList();
        this.allImagePathList = in.createStringArrayList();
    }

    public static final Creator<OverviewRecipesImages> CREATOR = new Creator<OverviewRecipesImages>() {
        @Override
        public OverviewRecipesImages createFromParcel(Parcel source) {
            return new OverviewRecipesImages(source);
        }

        @Override
        public OverviewRecipesImages[] newArray(int size) {
            return new OverviewRecipesImages[size];
        }
    };
}
