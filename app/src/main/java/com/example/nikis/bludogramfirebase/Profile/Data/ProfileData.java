package com.example.nikis.bludogramfirebase.Profile.Data;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.example.nikis.bludogramfirebase.BaseData;

import java.util.HashMap;
import java.util.Map;

public class ProfileData extends BaseData {
    public static final String GENDER_MALE = "Male";
    public static final String GENDER_FEMALE = "Female";
    public static final String GENDER_NONE = "none";

    public String firstName, secondName, login, gender;

    public String userUid;

    @Nullable
    public String imageURL;

    @Ignore
    @Nullable
    public String localImagePath;

    //TODO think
    @Nullable
    public String timeLastImageUpdate;

    public ProfileData(){

    }

    public ProfileData(String firstName, String secondName, String login, String gender,
                       @Nullable String localImagePath) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.login = login;
        this.gender = gender;
        this.localImagePath = localImagePath;
    }

    public Map<String, Object> toMap(){
        if(userUid == null) throw new IllegalArgumentException("userUid might not null!");

        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", firstName);
        result.put("secondName", secondName);
        result.put("gender", gender);
        result.put("login", login);
        result.put("userUid", userUid);
        result.put("imageURL", imageURL);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.secondName);
        dest.writeString(this.login);
        dest.writeString(this.userUid);
        dest.writeString(this.gender);
        dest.writeString(this.imageURL);
        dest.writeString(this.localImagePath);
    }

    protected ProfileData(Parcel in) {
        this.firstName = in.readString();
        this.secondName = in.readString();
        this.login = in.readString();
        this.userUid = in.readString();
        this.gender = in.readString();
        this.imageURL = in.readString();
        this.localImagePath = in.readString();
    }

    public static final Parcelable.Creator<ProfileData> CREATOR = new Parcelable.Creator<ProfileData>() {
        @Override
        public ProfileData createFromParcel(Parcel source) {
            return new ProfileData(source);
        }

        @Override
        public ProfileData[] newArray(int size) {
            return new ProfileData[size];
        }
    };
}
