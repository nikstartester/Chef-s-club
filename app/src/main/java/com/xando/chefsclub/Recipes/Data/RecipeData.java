package com.xando.chefsclub.Recipes.Data;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;

import com.google.firebase.database.ServerValue;
import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.DataWorkers.BaseData;
import com.xando.chefsclub.Recipes.db.Converters.MapBoolConverter;

import java.util.HashMap;
import java.util.Map;

/*
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

When you want to change some field or add or delete SEE RecipeEntity

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
*/
public class RecipeData extends BaseData implements Cloneable {

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
    public String recipeKey;
    public String authorUId;
    public int starCount;
    public long dateTime = Constants.ImageConstants.DEF_TIME;
    public long creatingTime = Constants.ImageConstants.DEF_TIME;
    @TypeConverters(MapBoolConverter.class)
    public Map<String, Boolean> stars = new HashMap<>();
    @Ignore
    public Map<String, Integer> tags = new HashMap<>();
    @TypeConverters(MapBoolConverter.class)
    public Map<String, Boolean> inCompilations = new HashMap<>();
    @Ignore
    public OverviewData overviewData;
    @Ignore
    public StepsData stepsData;
    public boolean isUpdated = false;
    @Ignore
    private boolean isNeedUpdateDateTime = true;

    public RecipeData() {
        this(new OverviewData(), new StepsData());
    }

    public RecipeData(OverviewData overviewData, StepsData stepsData) {
        this.overviewData = overviewData;
        this.stepsData = stepsData;
    }

    protected RecipeData(Parcel in) {
        this.recipeKey = in.readString();
        this.authorUId = in.readString();
        this.starCount = in.readInt();
        this.dateTime = in.readLong();
        this.creatingTime = in.readLong();
        int starsSize = in.readInt();
        this.stars = new HashMap<String, Boolean>(starsSize);
        for (int i = 0; i < starsSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.stars.put(key, value);
        }
        int tagsSize = in.readInt();
        this.tags = new HashMap<String, Integer>(tagsSize);
        for (int i = 0; i < tagsSize; i++) {
            String key = in.readString();
            Integer value = (Integer) in.readValue(Integer.class.getClassLoader());
            this.tags.put(key, value);
        }
        int inCompilationsSize = in.readInt();
        this.inCompilations = new HashMap<String, Boolean>(inCompilationsSize);
        for (int i = 0; i < inCompilationsSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.inCompilations.put(key, value);
        }
        this.overviewData = in.readParcelable(OverviewData.class.getClassLoader());
        this.stepsData = in.readParcelable(StepsData.class.getClassLoader());
        this.isNeedUpdateDateTime = in.readByte() != 0;
        this.isUpdated = in.readByte() != 0;
    }

    @Override
    public Map<String, Object> toMap() {

        HashMap<String, Object> map = new HashMap<>();

        map.put("recipeKey", recipeKey);
        map.put("authorUId", authorUId);
        map.put("starCount", starCount);
        map.put("overviewData", overviewData);
        map.put("stepsData", stepsData);
        map.put("stars", stars);
        map.put("inCompilations", inCompilations);
        map.put("tags", tags);
        map.put("isUpdated", isUpdated);

        if (!isUpdated || creatingTime == Constants.ImageConstants.DEF_TIME) {
            map.put("creatingTime", ServerValue.TIMESTAMP);
        } else map.put("creatingTime", creatingTime);

        if (isNeedUpdateDateTime)
            map.put("dateTime", ServerValue.TIMESTAMP);
        else map.put("dateTime", dateTime);

        return map;
    }

    /*@Override
    public RecipeData clone(){
        RecipeData clone = new RecipeData();
        clone.recipeKey = recipeKey;
        clone.authorUId = authorUId;
        clone.starCount = starCount;
        clone.dateTime = dateTime;
        clone.stars = new HashMap<>(stars);
        clone.overviewData = overviewData.clone();
        clone.stepsData = stepsData.clone();
        clone.inCompilations = new HashMap<>(inCompilations);
        clone.tags = new HashMap<>(tags);
        clone.isNeedUpdateDateTime = isNeedUpdateDateTime;
        return clone;
    }*/

    public boolean isNeedUpdateDateTime() {
        return isNeedUpdateDateTime;
    }

    public RecipeData setNeedUpdateDateTime(boolean needUpdateDateTime) {
        isNeedUpdateDateTime = needUpdateDateTime;

        return this;
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
        dest.writeLong(this.creatingTime);
        dest.writeInt(this.stars.size());
        for (Map.Entry<String, Boolean> entry : this.stars.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.tags.size());
        for (Map.Entry<String, Integer> entry : this.tags.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.inCompilations.size());
        for (Map.Entry<String, Boolean> entry : this.inCompilations.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeParcelable(this.overviewData, flags);
        dest.writeParcelable(this.stepsData, flags);
        dest.writeByte(this.isNeedUpdateDateTime ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isUpdated ? (byte) 1 : (byte) 0);
    }
}
