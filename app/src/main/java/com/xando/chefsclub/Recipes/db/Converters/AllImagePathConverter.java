package com.xando.chefsclub.Recipes.db.Converters;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.xando.chefsclub.Recipes.db.RecipeEntity.EMPTY_FIELD;

public class AllImagePathConverter {
    private static final String STR_CONVERTER_SYMBOL = "//sAnd//";

    @TypeConverter
    public static String fromAllImagePathList(List<String> allImagePathList) {
        return stringConverter(allImagePathList);
    }

    @TypeConverter
    public static List<String> toAllImagePathList(String data) {
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
