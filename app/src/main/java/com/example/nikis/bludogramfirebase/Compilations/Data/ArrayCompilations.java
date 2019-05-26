package com.example.nikis.bludogramfirebase.Compilations.Data;

import android.os.Parcel;

import com.example.nikis.bludogramfirebase.DataWorkers.BaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArrayCompilations extends BaseData {
    public List<CompilationData> mCompilationData;

    public ArrayCompilations() {
        this(new ArrayList<>());
    }

    public ArrayCompilations(List<CompilationData> compilationData) {
        mCompilationData = compilationData;
    }

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
        dest.writeTypedList(this.mCompilationData);
    }

    protected ArrayCompilations(Parcel in) {
        this.mCompilationData = in.createTypedArrayList(CompilationData.CREATOR);
    }

    public static final Creator<ArrayCompilations> CREATOR = new Creator<ArrayCompilations>() {
        @Override
        public ArrayCompilations createFromParcel(Parcel source) {
            return new ArrayCompilations(source);
        }

        @Override
        public ArrayCompilations[] newArray(int size) {
            return new ArrayCompilations[size];
        }
    };
}
