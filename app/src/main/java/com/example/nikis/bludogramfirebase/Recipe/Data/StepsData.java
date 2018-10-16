package com.example.nikis.bludogramfirebase.Recipe.Data;

import android.os.Parcel;

import com.example.nikis.bludogramfirebase.BaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StepsData extends BaseData {

    public String timeMain;

    public List<StepOfCooking> stepsOfCooking;

    @Deprecated
    @Override
    public Map<String, Object> toMap() {
        return null;
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
        dest.writeString(this.timeMain);
        dest.writeTypedList(this.stepsOfCooking);
    }

    public StepsData() {
        stepsOfCooking = new ArrayList<>();
    }

    protected StepsData(Parcel in) {
        this.timeMain = in.readString();
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
