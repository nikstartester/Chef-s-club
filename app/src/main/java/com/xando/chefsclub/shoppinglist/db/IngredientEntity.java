package com.xando.chefsclub.shoppinglist.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(indices = {@Index(value = {"recipeId", "ingredient"}, unique = true)})
public class IngredientEntity implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public final String recipeId;

    public final String recipeName;

    public final String ingredient;

    public boolean isAvailable;

    public final long time;

    @Ignore
    public IngredientEntity(String recipeId, String recipeName, String ingredient, long time) {
        this(recipeId, recipeName, ingredient, false, time);
    }

    public IngredientEntity(String recipeId, String recipeName, String ingredient, boolean isAvailable, long time) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredient = ingredient;
        this.isAvailable = isAvailable;
        this.time = time;
    }

    public void changeAvailable() {
        changeAvailable(!isAvailable);
    }

    private void changeAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.recipeId);
        dest.writeString(this.recipeName);
        dest.writeString(this.ingredient);
        dest.writeByte(this.isAvailable ? (byte) 1 : (byte) 0);
        dest.writeLong(this.time);
    }

    protected IngredientEntity(Parcel in) {
        this.id = in.readLong();
        this.recipeId = in.readString();
        this.recipeName = in.readString();
        this.ingredient = in.readString();
        this.isAvailable = in.readByte() != 0;
        this.time = in.readLong();
    }

    public static final Creator<IngredientEntity> CREATOR = new Creator<IngredientEntity>() {

        @Override
        public IngredientEntity createFromParcel(Parcel source) {
            return new IngredientEntity(source);
        }

        @Override
        public IngredientEntity[] newArray(int size) {
            return new IngredientEntity[size];
        }
    };
}
