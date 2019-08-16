package com.xando.chefsclub.Images.ImageData;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageData implements Parcelable {

    public String imagePath;

    public final long lastUpdateTime;

    public ImageData(String imagePath, long lastUpdateTime) {
        this.imagePath = imagePath;
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imagePath);
        dest.writeLong(this.lastUpdateTime);
    }

    private ImageData(Parcel in) {
        this.imagePath = in.readString();
        this.lastUpdateTime = in.readLong();
    }

    public static final Parcelable.Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>() {
        @Override
        public ImageData createFromParcel(Parcel source) {
            return new ImageData(source);
        }

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };
}
