package com.example.nikis.bludogramfirebase.RecipeData.Images;


public class UploadImagePath extends Object {
    public int position;
    public boolean isStepImage;
    private String imagePath;
    public String recipeKey;

    public UploadImagePath(int position, boolean isStepImage, String imagePath, String recipeKey) {
        this.position = position;
        this.isStepImage = isStepImage;
        this.imagePath = imagePath;
        this.recipeKey = recipeKey;
    }

    public String getUploadPath(){
        String path = "recipeImages/" + recipeKey + "/";
        if(!isStepImage)
            if (position == 0)
                return path + "main.jpg";
            else path +=  "baseImages/"+ (position - 1) + ".jpg";
        else path +=  "stepsImages/" + position + ".jpg";
        return path;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString() {
        return "Is Step Image: " + isStepImage + "\nPath: "+ imagePath+
                "\n Position: " + position + "\n UploadPath: " + getUploadPath();
    }
}
