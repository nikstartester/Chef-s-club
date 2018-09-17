package com.example.nikis.bludogramfirebase.RecipeData.Images;

import java.util.ArrayList;


public class ListUploadImagePath extends ArrayList<UploadImagePath> {

    public ListUploadImagePath(ArrayList<UploadImagePath> arrayList){
        super(arrayList);
    }

    public ListUploadImagePath(int initialCapacity) {
        super(initialCapacity);
    }

    public ListUploadImagePath() {
        super();
    }

    public String getMainPath(){
        for (int i = 0; i < super.size(); i++){
            UploadImagePath uploadImagePath = super.get(i);
            if(uploadImagePath.position == 0 && !uploadImagePath.isStepImage)
                return uploadImagePath.getUploadPath();
        }
        return null;
    }
    public ArrayList<String> getBaseImages(){
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < super.size(); i++){
            UploadImagePath uploadImagePath = super.get(i);
            if(!uploadImagePath.isStepImage && uploadImagePath.position != 0)
                list.add(uploadImagePath.getUploadPath());
        }
        return list;
    }
    public ArrayList<String> getStepsImages(){
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < super.size(); i++){
            UploadImagePath uploadImagePath = super.get(i);
            if(uploadImagePath.isStepImage)
                list.add(uploadImagePath.getUploadPath());
        }
        return list;
    }
}
