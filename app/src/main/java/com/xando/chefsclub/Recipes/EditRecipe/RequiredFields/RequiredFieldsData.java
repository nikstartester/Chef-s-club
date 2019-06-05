package com.xando.chefsclub.Recipes.EditRecipe.RequiredFields;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedHashMap;

public class RequiredFieldsData implements Parcelable {
    public LinkedHashMap<String, Boolean> fields;

    public RequiredFieldsData(LinkedHashMap<String, Boolean> fields) {
        this.fields = fields;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.fields);
    }

    public RequiredFieldsData() {
    }

    protected RequiredFieldsData(Parcel in) {
        this.fields = (LinkedHashMap<String, Boolean>) in.readSerializable();
    }

    public static final Parcelable.Creator<RequiredFieldsData> CREATOR = new Parcelable.Creator<RequiredFieldsData>() {
        @Override
        public RequiredFieldsData createFromParcel(Parcel source) {
            return new RequiredFieldsData(source);
        }

        @Override
        public RequiredFieldsData[] newArray(int size) {
            return new RequiredFieldsData[size];
        }
    };
}
