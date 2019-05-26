package com.example.nikis.bludogramfirebase.Recipes.db.Converters;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.nikis.bludogramfirebase.Recipes.db.RecipeEntity.EMPTY_FIELD;

public class ImagePathsWithoutMainConverter {
    private static final String TAG = "ImagePathsWithoutMainCo";

    private static final String STR_CONVERTER_SYMBOL = "//sAnd//";

    @TypeConverter
    public static String fromImagePathsWithoutMainList(List<String> imagePathsWithoutMainList) {
        return stringConverter(imagePathsWithoutMainList);
    }

    @TypeConverter
    public static List<String> toImagePathsWithoutMainList(String data) {
        List<String> tmpList = new ArrayList<>();

        if (data.equals(EMPTY_FIELD) || data.equals("")) return tmpList;

        List<String> list = Arrays.asList(data.split(STR_CONVERTER_SYMBOL));

        for (int i = 0; i < list.size(); i++) {
            tmpList.add(list.get(i).equals(EMPTY_FIELD) ? null : list.get(i));
        }


        return tmpList;
    }

    private static String stringConverter(List<String> strings) {
        StringBuilder str = new StringBuilder();

        for (String tmpString : strings) {
            str.append(tmpString);
            str.append(STR_CONVERTER_SYMBOL);
        }
        return str.toString();
    }
}
