package com.xando.chefsclub.dataworkers;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class ParcResourceBySerializable<T extends Serializable> implements Parcelable {

    @NonNull
    public final ParcResourceByParc.Status status;

    @Nullable
    public final T data;

    @Nullable
    private final String message;

    @Nullable
    public final Exception exception;

    private ParcResourceBySerializable(@NonNull ParcResourceByParc.Status status, @Nullable T data,
                                       @Nullable Exception exception) {
        this(status, data, null, exception);
    }

    private ParcResourceBySerializable(@NonNull ParcResourceByParc.Status status, @Nullable T data,
                                       @Nullable String message, @Nullable Exception exception) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.exception = exception;
    }

    public static <T extends Serializable> ParcResourceBySerializable<T> success(@NonNull T data) {
        return new ParcResourceBySerializable<>(ParcResourceByParc.Status.SUCCESS, data, null, null);
    }

    public static <T extends Serializable> ParcResourceBySerializable<T> error(Exception e, String msg, @Nullable T data) {
        return new ParcResourceBySerializable<>(ParcResourceByParc.Status.ERROR, data, msg, e);
    }

    public static <T extends Serializable> ParcResourceBySerializable<T> error(Exception e, @Nullable T data) {
        return new ParcResourceBySerializable<>(ParcResourceByParc.Status.ERROR, data, e);
    }

    public static <T extends Serializable> ParcResourceBySerializable<T> loading(@Nullable T data) {
        return new ParcResourceBySerializable<>(ParcResourceByParc.Status.LOADING, data, null, null);
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

    private ParcResourceBySerializable(Parcel in) {
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : ParcResourceByParc.Status.values()[tmpStatus];
        this.data = (T) in.readSerializable();
        this.message = in.readString();
        this.exception = (Exception) in.readSerializable();
    }

    public static final Parcelable.Creator<ParcResourceBySerializable> CREATOR = new Parcelable.Creator<ParcResourceBySerializable>() {
        @Override
        public ParcResourceBySerializable createFromParcel(Parcel source) {
            return new ParcResourceBySerializable(source);
        }

        @Override
        public ParcResourceBySerializable[] newArray(int size) {
            return new ParcResourceBySerializable[size];
        }
    };
}
