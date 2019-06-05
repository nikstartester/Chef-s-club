package com.xando.chefsclub.Recipes.db.Converters;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static com.xando.chefsclub.Recipes.db.RecipeEntity.EMPTY_FIELD;

public class MapBoolConverter {
    private static final String TAG = "MapBoolConverter";


    private static final String STR_CONVERTER_SYMBOL = "//sAnd//";
    private static final String STR_RESULT_SYMBOL = "//result//";

    @TypeConverter
    public static Map<String, Boolean> toStars(String data) {
        Map<String, Boolean> map = new HashMap<>();

        if (data == null || data.equals(EMPTY_FIELD) || data.equals("")) return map;

        for (String tmpStr : data.split(STR_CONVERTER_SYMBOL)) {
            String[] parts = tmpStr.split(STR_RESULT_SYMBOL);
            String key = parts[0];

            Boolean value = parts[1].equals("tr*");

            map.put(key, value);
        }
        return map;
    }

    @TypeConverter
    public static String fromStars(Map<String, Boolean> map) {
        StringBuilder str = new StringBuilder();

        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            str.append(entry.getKey());
            str.append(STR_RESULT_SYMBOL);
            str.append(entry.getValue() ? "tr*" : "fls*");

            str.append(STR_CONVERTER_SYMBOL);
        }

        Log.d(TAG, "fromStars: " + str);

        return str.toString();
    }
}
