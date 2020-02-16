package com.xando.chefsclub.recipes.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.xando.chefsclub.recipes.data.StepOfCooking;

import java.util.ArrayList;
import java.util.List;

import static com.xando.chefsclub.recipes.editrecipe.DialogTimePicker.NOT_SELECTED;
import static com.xando.chefsclub.recipes.db.RecipeEntity.EMPTY_FIELD;

public class StepsOfCookingConverter {

    private static final String STR_CONVERTER_SYMBOL = "//sAnd//";
    private static final String STEP_FIELDS_SYMBOL = "/&s&/";

    @TypeConverter
    public static String fromStepsOfCooking(List<StepOfCooking> steps) {
        return stingConverter(steps);
    }

    @TypeConverter
    public static List<StepOfCooking> toSteps(String data) {
        List<StepOfCooking> steps = new ArrayList<>();

        for (String stepStr : data.split(STR_CONVERTER_SYMBOL)) {
            String[] fields = stepStr.split(STEP_FIELDS_SYMBOL);

            StepOfCooking step = new StepOfCooking();

            step.imagePath = fields[0].equals(EMPTY_FIELD) ? null : fields[0];
            step.text = fields[1].equals(EMPTY_FIELD) ? null : fields[1];
            step.timeNum = fields[2].equals(EMPTY_FIELD) ? NOT_SELECTED : Integer.valueOf(fields[2]);

            steps.add(step);
        }

        return steps;
    }

    private static String stingConverter(List<StepOfCooking> data) {
        StringBuilder str = new StringBuilder();

        for (StepOfCooking step : data) {
            str.append(step.imagePath)
                    .append(STEP_FIELDS_SYMBOL)
                    .append(step.text)
                    .append(STEP_FIELDS_SYMBOL)
                    .append(step.timeNum);

            str.append(STR_CONVERTER_SYMBOL);
        }

        return str.toString();
    }
}
