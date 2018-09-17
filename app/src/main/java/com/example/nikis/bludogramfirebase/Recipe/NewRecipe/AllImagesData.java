package com.example.nikis.bludogramfirebase.Recipe.NewRecipe;

import com.example.nikis.bludogramfirebase.Exceptions.IncorrectPositionException;

import java.util.ArrayList;

public class AllImagesData extends Object {
    private ArrayList<String> imagesPathArray;
    private ArrayList<String> selectedImagesPathArray;
    private ArrayList<Integer> statuses;

    public AllImagesData() {
        imagesPathArray = new ArrayList<>();

        statuses = new ArrayList<>();
    }

    public AllImagesData(ArrayList<String> imagesPathArray) {
        this.imagesPathArray = imagesPathArray;
    }
    public AllImagesData(ArrayList<String> imagesPathArray, ArrayList<String> selectedImagesPathArray){
        this.imagesPathArray = imagesPathArray;
        this.selectedImagesPathArray = selectedImagesPathArray;
    }

    public void setFirstElementToFirstPosition(String imagePath){
        removeFirstPosition(imagesPathArray);
        imagesPathArray.add(0, imagePath);
    }
    private void removeFirstPosition(ArrayList arrayList){
        if(arrayList.size() > 0 ){
            arrayList.remove(0);
        }
    }

    public void addFirstElementsToNOTFirstPosition(String imagePath){
        if(imagesPathArray.size() == 0) {
            imagesPathArray.add(null);
        }
        imagesPathArray.add(imagePath);
    }
    public void addElement(String imagePath){
        imagesPathArray.add(imagePath);
    }
    public int getCountOfAllImagesPath(){
        return imagesPathArray.size();
    }

    public ArrayList<String> getImagesPathArray() {
        return imagesPathArray;
    }

    public String getImagePath(int position)throws IncorrectPositionException{
        if(getCountOfAllImagesPath() <= position)
            throw new IncorrectPositionException("CountOfAllImagesPath <= position");
        return imagesPathArray.get(position);
    }

    public void removePath(int position){
        imagesPathArray.remove(position);
    }

    public void setEmptySelectedImagesPathArray(){
        selectedImagesPathArray = new ArrayList<>();
    }
    public void addSelectedImagePath(String selectedImagePath){
        selectedImagesPathArray.add(selectedImagePath);
    }
    public String getSelectedImagePath(int position){
        return selectedImagesPathArray.get(position);
    }
    public int getCountOfSelectedImagesPath(){
        return selectedImagesPathArray.size();
    }

    public ArrayList<String> getSelectedImagesPathArray() {
        return selectedImagesPathArray;
    }

}
