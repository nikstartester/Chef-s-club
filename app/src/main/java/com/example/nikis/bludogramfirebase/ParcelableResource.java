package com.example.nikis.bludogramfirebase;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

public class ParcelableResource<T extends Serializable> implements Parcelable {

    @NonNull
    public final Resource.Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    @Nullable
    public final Exception exception;

    private ParcelableResource(@NonNull Resource.Status status, @Nullable T data,
                               @Nullable Exception exception) {
        this(status, data, null, exception);
    }

    private ParcelableResource(@NonNull Resource.Status status, @Nullable T data,
                               @Nullable String message, @Nullable Exception exception) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.exception = exception;
    }

    public static <T extends Serializable> ParcelableResource<T> success(@NonNull T data) {
        return new ParcelableResource<>(Resource.Status.SUCCESS, data, null, null);
    }

    public static <T extends Serializable> ParcelableResource<T> error(Exception e, String msg, @Nullable T data) {
        return new ParcelableResource<>(Resource.Status.ERROR, data, msg, e);
    }

    public static <T extends Serializable> ParcelableResource<T> error(Exception e, @Nullable T data) {
        return new ParcelableResource<>(Resource.Status.ERROR, data, e);
    }

    public static <T extends Serializable> ParcelableResource<T> loading(@Nullable T data) {
        return new ParcelableResource<>(Resource.Status.LOADING, data, null, null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status.ordinal());
        dest.writeSerializable(this.data);
        dest.writeString(this.message);
        dest.writeSerializable(this.exception);
    }

    protected ParcelableResource(Parcel in) {
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Resource.Status.values()[tmpStatus];
        this.data = (T) in.readSerializable();
        this.message = in.readString();
        this.exception = (Exception) in.readSerializable();
    }

    public static final Parcelable.Creator<ParcelableResource> CREATOR = new Parcelable.Creator<ParcelableResource>() {
        @Override
        public ParcelableResource createFromParcel(Parcel source) {
            return new ParcelableResource(source);
        }

        @Override
        public ParcelableResource[] newArray(int size) {
            return new ParcelableResource[size];
        }
    };
}
