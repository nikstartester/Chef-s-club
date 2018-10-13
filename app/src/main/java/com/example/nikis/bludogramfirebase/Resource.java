package com.example.nikis.bludogramfirebase;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Resource<T extends BaseData> implements Parcelable {
    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    @Nullable
    public final Exception exception;

    private Resource(@NonNull Status status, @Nullable T data,
                      @Nullable Exception exception) {
        this(status, data, null, exception);
    }

    private Resource(@NonNull Status status, @Nullable T data,
                     @Nullable String message, @Nullable Exception exception) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.exception = exception;
    }

    public static <T extends BaseData> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }

    public static <T extends BaseData> Resource<T> error(Exception e, String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg, e);
    }

    public static <T extends BaseData> Resource<T> error(Exception e, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, e);
    }

    public static <T extends BaseData> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null, null);
    }

    public enum Status { SUCCESS, ERROR, LOADING }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status.ordinal());
        dest.writeParcelable(this.data, flags);
        dest.writeString(this.message);
        dest.writeSerializable(this.exception);
    }

    protected Resource(Parcel in) {
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Status.values()[tmpStatus];
        this.data = in.readParcelable(BaseData.class.getClassLoader());
        this.message = in.readString();
        this.exception = (Exception) in.readSerializable();
    }

    public static final Parcelable.Creator<Resource> CREATOR = new Parcelable.Creator<Resource>() {
        @Override
        public Resource createFromParcel(Parcel source) {
            return new Resource(source);
        }

        @Override
        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };
}
