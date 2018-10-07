package com.example.nikis.bludogramfirebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Resource<T> {
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

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }

    public static <T> Resource<T> error(Exception e, String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg, e);
    }

    public static <T> Resource<T> error(Exception e, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, e);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null, null);
    }

    public enum Status { SUCCESS, ERROR, LOADING }
}
