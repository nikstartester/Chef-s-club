package com.example.nikis.bludogramfirebase.RecipeData;

import com.example.nikis.bludogramfirebase.RecipeData.Images.ListUploadImagePath;
import com.example.nikis.bludogramfirebase.RecipeData.Images.UploadImagePath;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeData extends RecipeAdapterData {
    public ArrayList<String> ingredients;
    public ArrayList<String> stepsOfCooking;
    public ArrayList<String> timesOfStepsCooking;
    private ListUploadImagePath listUploadImagePath;


    private ArrayList<ArrayList<String>> imagesPath = new ArrayList<>();
    private ArrayList<Boolean> isStepsCooking = new ArrayList<>();

    public RecipeData() {
        // Default constructor required for calls to DataSnapshot.getValue(RecipeData.class)
    }

    public RecipeData(
            String uid,
            String userLogin,
            String name,
            String description,
            ArrayList<String> ingredients,
            String allTimeCooking,
            ArrayList<String> stepsOfCooking,
            ArrayList<String> timesOfStepsCooking,
            String createDate,
            int starCount) {

        super(
                uid,
                userLogin,
                name,
                description,
                allTimeCooking,
                starCount,
                createDate
        );

        this.ingredients = ingredients;
        this.stepsOfCooking = stepsOfCooking;
        this.timesOfStepsCooking = timesOfStepsCooking;
    }

    public RecipeData(String name, String description, ArrayList<String> ingredients) {
       super(name, description);
        this.ingredients = ingredients;
    }

    public RecipeData(String allTimeCooking, ArrayList<String> stepsOfCooking, ArrayList<String> timesOfStepsCooking, String createDate) {
        super();
        super.allTimeCooking = allTimeCooking;
        this.stepsOfCooking = stepsOfCooking;
        this.timesOfStepsCooking = timesOfStepsCooking;
        super.createDate = createDate;
    }

    @Deprecated
    public RecipeData copyNotEmptyRowsFrom(RecipeData recipeData){
        if (recipeData.uid != null) {
            uid = recipeData.uid;
        }
        if (recipeData.name != null) {
            name = recipeData.name;
        }
        if (recipeData.description != null) {
            description = recipeData.description;
        }
        if (recipeData.ingredients != null) {
            ingredients = recipeData.ingredients;
        }
        if (recipeData.allTimeCooking != null) {
            allTimeCooking = recipeData.allTimeCooking;
        }
        if (recipeData.stepsOfCooking != null) {
            stepsOfCooking = recipeData.stepsOfCooking;
        }
        if (recipeData.timesOfStepsCooking != null) {
            timesOfStepsCooking = recipeData.timesOfStepsCooking;
        }
        if(recipeData.starCount != 0){
            starCount = recipeData.starCount;
        }
        if (recipeData.createDate != null) {
            createDate = recipeData.createDate;
        }
        return this;
    }

    @Override
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("ingredients", ingredients);
        result.put("stepsOfCooking", stepsOfCooking);
        result.put("timesOfStepsCooking", timesOfStepsCooking);

        listUploadImagePath = getListUploadImagePath();

        result.put("listUploadImagePath", listUploadImagePath);


        super.uploadMainImagePath = listUploadImagePath.getMainPath();
        result.putAll(super.toMap());

        return result;
    }


    public ListUploadImagePath getListUploadImagePath(){
        if(listUploadImagePath !=  null)
            return listUploadImagePath;

        ListUploadImagePath listUploadImagePath = new ListUploadImagePath();
        for(int i = 0; i < isStepsCooking.size(); i++){
            ArrayList<String> currImagesPath = imagesPath.get(i);
            boolean isCurrStepImages = isStepsCooking.get(i);
            for (int j = 0; j < currImagesPath.size(); j++){
                if(currImagesPath.get(j) != null){
                    listUploadImagePath.add(new UploadImagePath(j, isCurrStepImages,currImagesPath.get(j), recipeKey));
                }
            }
        }
        return listUploadImagePath;
    }

    public void setRecipeKey(String recipeKey) {
        super.recipeKey = recipeKey;
    }

    public void setImagesPath(ArrayList<ArrayList<String>> imagesPath) {
        this.imagesPath = imagesPath;
    }

    public void setIsStepsCooking(ArrayList<Boolean> isStepsCooking) {
        this.isStepsCooking = isStepsCooking;
    }
}
