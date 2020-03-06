package com.xando.chefsclub.search.recipes.filter;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.IntDef;

import com.xando.chefsclub.search.core.BaseFilterData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class RecipeFilterData extends BaseFilterData {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FROM_ALL_RECIPES, FROM_MY_RECIPES, FROM_FAVORITE, FROM_SUBSCRIPTIONS})
    public @interface SearchFrom {
    }

    public static final int FROM_ALL_RECIPES = 0;
    public static final int FROM_MY_RECIPES = 1;
    public static final int FROM_FAVORITE = 2;
    public static final int FROM_SUBSCRIPTIONS = 3;

    public static final String[] searchFromStrings = new String[]{
            "All recipes",
            "My recipes",
            "Favorite",
            "Subscriptions"
    };

    public int minTime = -1;
    public int maxTime = -1;

    public List<String> categories = new ArrayList<>();

    public List<String> subscriptions = new ArrayList<>();

    int searchFrom;

    @SuppressWarnings("unchecked")
    public RecipeFilterData getClone() {
        RecipeFilterData clone = new RecipeFilterData();

        clone.searchFrom = this.searchFrom;

        clone.maxTime = this.maxTime;
        clone.minTime = this.minTime;

        clone.categories = (ArrayList<String>) ((ArrayList<String>) this.categories).clone();

        clone.subscriptions = (ArrayList<String>) ((ArrayList<String>) this.subscriptions).clone();

        return clone;
    }

    @SearchFrom
    public int getSearchFrom() {
        return searchFrom;
    }

    public RecipeFilterData setSearchFrom(@SearchFrom int searchFrom) {
        this.searchFrom = searchFrom;

        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.minTime);
        dest.writeInt(this.maxTime);
        dest.writeStringList(this.categories);
        dest.writeInt(this.searchFrom);
    }

    public RecipeFilterData() {
    }

    private RecipeFilterData(Parcel in) {
        this.minTime = in.readInt();
        this.maxTime = in.readInt();
        this.categories = in.createStringArrayList();
        this.searchFrom = in.readInt();
    }

    public static final Parcelable.Creator<RecipeFilterData> CREATOR = new Parcelable.Creator<RecipeFilterData>() {
        @Override
        public RecipeFilterData createFromParcel(Parcel source) {
            return new RecipeFilterData(source);
        }

        @Override
        public RecipeFilterData[] newArray(int size) {
            return new RecipeFilterData[size];
        }
    };
}
