package com.example.nikis.bludogramfirebase.Recipes.Data;

import android.os.Parcel;

import com.example.nikis.bludogramfirebase.DataWorkers.BaseData;

import java.util.Map;

public class StepOfCooking extends BaseData implements Cloneable {
    public String text;

    public int timeNum;

    public String imagePath;

    public StepOfCooking(String text, int timeNum, String imagePath) {
        this.text = text;
        this.timeNum = timeNum;
        this.imagePath = imagePath;
    }

    public StepOfCooking() {
    }

    @Deprecated
    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public StepOfCooking clone() {

        return new StepOfCooking(text, timeNum, imagePath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeInt(this.timeNum);
        dest.writeString(this.imagePath);
    }


    private StepOfCooking(Parcel in) {
        this.text = in.readString();
        this.timeNum = in.readInt();
        this.imagePath = in.readString();
    }

    public static final Creator<StepOfCooking> CREATOR = new Creator<StepOfCooking>() {
        @Override
        public StepOfCooking createFromParcel(Parcel source) {
            return new StepOfCooking(source);
        }

        @Override
        public StepOfCooking[] newArray(int size) {
            return new StepOfCooking[size];
        }
    };
}
