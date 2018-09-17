package com.example.nikis.bludogramfirebase.RecipeData;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class RecipeAdapterData extends AbsRecipeData {
    public String uid;
    public String userLogin;
    public String name;
    public String description;
    public String allTimeCooking;
    public int starCount;
    public String recipeKey;
    public String createDate;
    public String uploadMainImagePath;
    public Map<String, Boolean> stars = new HashMap<>();

    public RecipeAdapterData() {
    }

    public RecipeAdapterData(String uid, String userLogin, String name, String description, String allTimeCooking, int starCount, String createDate) {
        this.uid = uid;
        this.userLogin = userLogin;
        this.name = name;
        this.description = description;
        this.allTimeCooking = allTimeCooking;
        this.starCount = starCount;
        this.createDate = createDate;
    }

    public RecipeAdapterData(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("description", description);
        result.put("allTimeCooking", allTimeCooking);
        result.put("starCount", starCount);
        result.put("recipeKey", recipeKey);
        result.put("uploadMainImagePath", uploadMainImagePath);
        result.put("userLogin", userLogin);
        result.put("stars", stars);
        return result;
    }
}
