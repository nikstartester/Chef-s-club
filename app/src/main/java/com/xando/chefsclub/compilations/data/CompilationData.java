package com.xando.chefsclub.compilations.data;

import android.os.Parcel;

import com.google.firebase.database.ServerValue;
import com.xando.chefsclub.dataworkers.BaseData;
import com.xando.chefsclub.recipes.db.converter.AllImagePathConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.room.Ignore;
import androidx.room.TypeConverters;

public class CompilationData extends BaseData {

    public String name;
    public String compilationKey;
    public String authorUId;
    public long creatingTime;
    public int count;

    @TypeConverters(AllImagePathConverter.class)
    public List<String> recipesKey = new ArrayList<>();

    public CompilationData() {
    }

    @Ignore
    public CompilationData(String name, String authorUId, int count) {
        this.name = name;
        this.authorUId = authorUId;
        this.count = count;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("compilationKey", compilationKey);
        map.put("name", name);
        map.put("authorUId", authorUId);
        map.put("count", count);
        map.put("recipesKey", recipesKey);
        map.put("creatingTime", ServerValue.TIMESTAMP);

        return map;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.compilationKey);
        dest.writeString(this.authorUId);
        dest.writeLong(this.creatingTime);
        dest.writeInt(this.count);
        dest.writeStringList(this.recipesKey);
    }

    protected CompilationData(Parcel in) {
        this.name = in.readString();
        this.compilationKey = in.readString();
        this.authorUId = in.readString();
        this.creatingTime = in.readLong();
        this.count = in.readInt();
        this.recipesKey = in.createStringArrayList();
    }

    public static final Creator<CompilationData> CREATOR = new Creator<CompilationData>() {
        @Override
        public CompilationData createFromParcel(Parcel source) {
            return new CompilationData(source);
        }

        @Override
        public CompilationData[] newArray(int size) {
            return new CompilationData[size];
        }
    };
}