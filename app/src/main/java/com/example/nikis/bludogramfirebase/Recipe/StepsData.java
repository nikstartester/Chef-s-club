package com.example.nikis.bludogramfirebase.Recipe;

import java.util.ArrayList;

import static com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems.StepAddItem.DEFAULT_TEXT;
import static com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems.StepAddItem.DEFAULT_TIME;

public class StepsData {
    private ArrayList<String> texts, imagesPath, times;

    public StepsData(){
        texts = new ArrayList<>();
        imagesPath = new ArrayList<>();
        times = new ArrayList<>();
    }

    public StepsData(ArrayList<String> texts, ArrayList<String> imagesPath, ArrayList<String> times) {
        this.texts = texts;
        this.imagesPath = imagesPath;
        this.times = times;
    }

    public void addStepData(String text, String imagePath, String time){
        texts.add(text);
        imagesPath.add(imagePath);
        times.add(time);
    }
    public void addEmptyStepData(){
        addStepData(DEFAULT_TEXT, null, DEFAULT_TIME);
    }

    public void setTextsToPosition(String text, int position){
        removePositionFromArray(texts, position);
        texts.add(position, text);
    }
    public void setImagePathToPosition(String imagePath, int position){
        removePositionFromArray(imagesPath, position);
        imagesPath.add(position,imagePath);
    }
    public void setTimeToPosition(String time, int position){
        removePositionFromArray(times, position);
        times.add(position, time);
    }

    private void removePositionFromArray(ArrayList arrayList, int position){
        if(arrayList.size() > position)
            arrayList.remove(position);
    }

    public void removePositionOfImagesPath(int position){
        removePositionFromArray(imagesPath, position);
        imagesPath.add(position,null);
    }
    public void removeStepOfPosition(int position){
        removePositionFromArray(texts, position);
        removePositionFromArray(imagesPath, position);
        removePositionFromArray(times, position);
    }

    public ArrayList<String> getTexts() {
        return texts;
    }

    public ArrayList<String> getImagesPath() {
        return imagesPath;
    }

    public ArrayList<String> getTimes() {
        return times;
    }

}
