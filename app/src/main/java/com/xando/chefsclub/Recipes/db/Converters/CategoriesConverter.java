package com.xando.chefsclub.Recipes.db.Converters;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

import static com.xando.chefsclub.Recipes.db.RecipeEntity.EMPTY_FIELD;

class CategoriesConverter {
    private static final String STR_CONVERTER_SYMBOL = "//sAnd//";

    @TypeConverter
    public static String fromCategories(List<Integer> categories) {
        return integerConverter(categories);
    }

    @TypeConverter
    public static List<Integer> toCategories(String data) {
        List<Integer> integers = new ArrayList<>();

        if (data.equals(EMPTY_FIELD) || data.equals("")) return integers;

        for (String str : data.split(STR_CONVERTER_SYMBOL)) {
            integers.add(Integer.valueOf(str.equals(EMPTY_FIELD) ? null : str));
        }

        return integers;
    }


    private static String integerConverter(List<Integer> data) {
        StringBuilder str = new StringBuilder();

        for (Integer tmpString : data) {
            str.append(tmpString);
            str.append(STR_CONVERTER_SYMBOL);
        }
        return str.toString();
    }
}
