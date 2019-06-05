package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.Data;

import android.os.Parcel;

import com.google.firebase.database.ServerValue;
import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.DataWorkers.BaseData;

import java.util.HashMap;
import java.util.Map;

public class CommentData extends BaseData {
    public String commentId;
    public String text;
    public String authorId;
    public String recipeId;
    public long date;

    //Def == null
    public String authorLogin;

    public CommentData() {
    }

    public CommentData(String text, String authorId, String recipeId) {
        this(text, authorId, recipeId, Constants.ImageConstants.DEF_TIME);
    }

    public CommentData(String text, String authorId, String recipeId, long date) {
        this.text = text;
        this.authorId = authorId;
        this.recipeId = recipeId;
        this.date = date;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("commentId", commentId);
        map.put("text", text);
        map.put("authorId", authorId);
        map.put("recipeId", recipeId);
        map.put("date", ServerValue.TIMESTAMP);

        return map;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.commentId);
        dest.writeString(this.text);
        dest.writeString(this.authorId);
        dest.writeString(this.recipeId);
        dest.writeLong(this.date);
    }

    protected CommentData(Parcel in) {
        this.commentId = in.readString();
        this.text = in.readString();
        this.authorId = in.readString();
        this.recipeId = in.readString();
        this.date = in.readLong();
    }

    public static final Creator<CommentData> CREATOR = new Creator<CommentData>() {
        @Override
        public CommentData createFromParcel(Parcel source) {
            return new CommentData(source);
        }

        @Override
        public CommentData[] newArray(int size) {
            return new CommentData[size];
        }
    };
}
