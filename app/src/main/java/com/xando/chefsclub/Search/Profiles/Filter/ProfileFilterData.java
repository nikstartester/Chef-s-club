package com.xando.chefsclub.Search.Profiles.Filter;

import android.os.Parcel;
import android.support.annotation.IntDef;

import com.xando.chefsclub.Search.Core.BaseFilterData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ProfileFilterData extends BaseFilterData {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FROM_ALL_PROFILES, FROM_SUBSCRIPTIONS,})
    public @interface SearchFrom {
    }

    public static final int FROM_ALL_PROFILES = 0;
    public static final int FROM_SUBSCRIPTIONS = 1;

    int searchFrom = FROM_ALL_PROFILES;


    @SearchFrom
    public int getSearchFrom() {
        return searchFrom;
    }

    public ProfileFilterData setSearchFrom(@SearchFrom int searchFrom) {
        this.searchFrom = searchFrom;

        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.searchFrom);
    }

    public ProfileFilterData() {
    }

    private ProfileFilterData(Parcel in) {
        this.searchFrom = in.readInt();
    }

    public static final Creator<ProfileFilterData> CREATOR = new Creator<ProfileFilterData>() {
        @Override
        public ProfileFilterData createFromParcel(Parcel source) {
            return new ProfileFilterData(source);
        }

        @Override
        public ProfileFilterData[] newArray(int size) {
            return new ProfileFilterData[size];
        }
    };
}
