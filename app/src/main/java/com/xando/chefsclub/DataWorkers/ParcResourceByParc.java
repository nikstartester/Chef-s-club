package com.xando.chefsclub.DataWorkers;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ParcResourceByParc<T extends Parcelable> implements Parcelable {
    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    private final String message;

    @Nullable
    public final Exception exception;

    @Nullable
    public final From from;

    private ParcResourceByParc(@NonNull Status status, @Nullable T data,
                               @Nullable Exception exception) {
        this(status, data, null, exception, null);
    }

    private ParcResourceByParc(@NonNull Status status, @Nullable T data,
                               @Nullable String message, @Nullable Exception exception, @Nullable From from) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.exception = exception;
        this.from = from;
    }

    public static <T extends BaseData> ParcResourceByParc<T> success(@NonNull T data) {
        return ParcResourceByParc.success(data, null);
    }

    public static <T extends BaseData> ParcResourceByParc<T> success(@NonNull T data, @Nullable From from) {
        return new ParcResourceByParc<>(Status.SUCCESS, data, null, null, from);
    }

    public static <T extends BaseData> ParcResourceByParc<T> error(Exception e, @Nullable T data) {
        return ParcResourceByParc.error(e, data, null);
    }

    public static <T extends BaseData> ParcResourceByParc<T> error(Exception e, @Nullable T data, @Nullable From from) {
        return new ParcResourceByParc<>(Status.ERROR, data, null, e, from);
    }

    public static <T extends BaseData> ParcResourceByParc<T> loading(@Nullable T data) {
        return ParcResourceByParc.loading(data, null);
    }

    public static <T extends BaseData> ParcResourceByParc<T> loading(@Nullable T data, @Nullable From from) {
        return new ParcResourceByParc<>(Status.LOADING, data, null, null, from);
    }

    public enum Status {SUCCESS, ERROR, LOADING}

    public enum From {SERVER, DATABASE}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeParcelable(this.data, flags);
        dest.writeString(this.message);
        dest.writeSerializable(this.exception);
        dest.writeInt(this.from == null ? -1 : this.from.ordinal());
    }

    protected ParcResourceByParc(Parcel in) {
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : Status.values()[tmpStatus];
        this.data = in.readParcelable(BaseData.class.getClassLoader());
        this.message = in.readString();
        this.exception = (Exception) in.readSerializable();
        int tmpFrom = in.readInt();
        this.from = tmpFrom == -1 ? null : From.values()[tmpFrom];
    }

    public static final Creator<ParcResourceByParc> CREATOR = new Creator<ParcResourceByParc>() {
        @Override
        public ParcResourceByParc createFromParcel(Parcel source) {
            return new ParcResourceByParc(source);
        }

        @Override
        public ParcResourceByParc[] newArray(int size) {
            return new ParcResourceByParc[size];
        }
    };
}
