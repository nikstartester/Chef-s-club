package com.xando.chefsclub.profiles.data;

import android.os.Parcel;

import androidx.annotation.Nullable;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import com.google.firebase.database.ServerValue;
import com.xando.chefsclub.constants.Constants;
import com.xando.chefsclub.dataworkers.BaseData;
import com.xando.chefsclub.recipes.db.converter.MapBoolConverter;

import java.util.HashMap;
import java.util.Map;

public class ProfileData extends BaseData {

    public static final String GENDER_MALE = "Male";
    public static final String GENDER_FEMALE = "Female";
    public static final String GENDER_NONE = "none";

    public static final Creator<ProfileData> CREATOR = new Creator<ProfileData>() {
        @Override
        public ProfileData createFromParcel(Parcel source) {
            return new ProfileData(source);
        }

        @Override
        public ProfileData[] newArray(int size) {
            return new ProfileData[size];
        }
    };

    public String firstName, secondName, login, gender;
    public String userUid;
    @Nullable
    public String imageURL;
    @Ignore
    @Nullable
    public String localImagePath;
    @TypeConverters(MapBoolConverter.class)
    public Map<String, Boolean> subscriptions = new HashMap<>();
    public int subscriptionsCount;
    @TypeConverters(MapBoolConverter.class)
    public Map<String, Boolean> subscribers = new HashMap<>();
    public int subscribersCount;
    public long lastTimeUpdate = Constants.ImageConstants.DEF_TIME;

    public ProfileData() {

    }

    public ProfileData(String firstName, String secondName, String login, String gender,
                       @Nullable String localImagePath) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.login = login;
        this.gender = gender;
        this.localImagePath = localImagePath;
    }

    protected ProfileData(Parcel in) {
        this.firstName = in.readString();
        this.secondName = in.readString();
        this.login = in.readString();
        this.gender = in.readString();
        this.userUid = in.readString();
        this.imageURL = in.readString();
        this.localImagePath = in.readString();
        int subscriptionsSize = in.readInt();
        this.subscriptions = new HashMap<>(subscriptionsSize);
        for (int i = 0; i < subscriptionsSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.subscriptions.put(key, value);
        }
        this.subscriptionsCount = in.readInt();
        int subscribersSize = in.readInt();
        this.subscribers = new HashMap<>(subscribersSize);
        for (int i = 0; i < subscribersSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.subscribers.put(key, value);
        }
        this.subscribersCount = in.readInt();
        this.lastTimeUpdate = in.readLong();
    }

    @Override
    public Map<String, Object> toMap() {
        if (userUid == null) throw new IllegalArgumentException("userUid might not null!");

        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", firstName);
        result.put("secondName", secondName);
        result.put("gender", gender);
        result.put("login", login);
        result.put("userUid", userUid);
        result.put("imageURL", imageURL);
        result.put("lastTimeUpdate", ServerValue.TIMESTAMP);
        result.put("subscriptions", subscriptions);
        result.put("subscribers", subscribers);
        result.put("subscriptionsCount", subscriptionsCount);
        result.put("subscribersCount", subscribersCount);

        result.put("loginLowerCase", login.toLowerCase());

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
        dest.writeString(this.gender);
        dest.writeString(this.userUid);
        dest.writeString(this.imageURL);
        dest.writeString(this.localImagePath);
        dest.writeInt(this.subscriptions.size());
        for (Map.Entry<String, Boolean> entry : this.subscriptions.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.subscriptionsCount);
        dest.writeInt(this.subscribers.size());
        for (Map.Entry<String, Boolean> entry : this.subscribers.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.subscribersCount);
        dest.writeLong(this.lastTimeUpdate);
    }
}
