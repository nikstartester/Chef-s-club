package com.xando.chefsclub.Recipes.Data;

import android.os.Parcel;

import com.xando.chefsclub.DataWorkers.BaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StepsData extends BaseData implements Cloneable {

    public int timeMainNum;

    public List<StepOfCooking> stepsOfCooking;

    public StepsData() {
        stepsOfCooking = new ArrayList<>();
    }

    @Deprecated
    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public StepsData clone() {
        StepsData clone = new StepsData();

        clone.timeMainNum = timeMainNum;
        clone.stepsOfCooking = new ArrayList<>();

        for (StepOfCooking step : stepsOfCooking) {
            clone.stepsOfCooking.add(step.clone());
        }

        return clone;
    }

    public void removeStepOfPosition(int position) {
        stepsOfCooking.remove(position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.timeMainNum);
        dest.writeTypedList(this.stepsOfCooking);
    }

    StepsData(Parcel in) {
        this.timeMainNum = in.readInt();
        this.stepsOfCooking = in.createTypedArrayList(StepOfCooking.CREATOR);
    }

    public static final Creator<StepsData> CREATOR = new Creator<StepsData>() {
        @Override
        public StepsData createFromParcel(Parcel source) {
            return new StepsData(source);
        }

        @Override
        public StepsData[] newArray(int size) {
            return new StepsData[size];
        }
    };
}
