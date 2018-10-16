package com.example.nikis.bludogramfirebase.Recipe.Data;

import android.os.Parcel;

import com.example.nikis.bludogramfirebase.BaseData;

import java.util.Map;

public class StepOfCooking extends BaseData {
    public String text;

    public String time;

    public String imagePath;

    @Deprecated
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
        dest.writeString(this.text);
        dest.writeString(this.time);
        dest.writeString(this.imagePath);
    }

    public StepOfCooking() {
    }

    protected StepOfCooking(Parcel in) {
        this.text = in.readString();
        this.time = in.readString();
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
