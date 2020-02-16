package com.xando.chefsclub.recipes.data;

import com.xando.chefsclub.constants.Constants;
import com.xando.chefsclub.dataworkers.ActualDataChecker;
import com.xando.chefsclub.helper.FirebaseHelper;


public class ActualRecipeDataChecker extends ActualDataChecker<RecipeData> {

    @Override
    public boolean hasChanged(RecipeData data1, RecipeData data2) {
        if (data1 == null || data2 == null) return true;
        /*
        Check recipeKey != null (it possible if recipes wasn't upload yet)
         */

        if (data1.recipeKey == null || data2.recipeKey == null) return false;

        if (!data1.recipeKey.equals(data2.recipeKey)) return true;

        return hasTimeChanged(data1.dateTime, data2.dateTime);
    }

    private boolean hasTimeChanged(long time1, long time2) {

        /*
        Check that times not def (it possible if recipes wasn't upload yet)
         */

        if (time1 == Constants.ImageConstants.DEF_TIME
                || time2 == Constants.ImageConstants.DEF_TIME)
            return true;

        return time1 != time2;
    }

    /*
    return true if current user update favorite else return false
     */
    public boolean isNeedUpdateFavorite(RecipeData data1, RecipeData data2) {
        if (data1 == null || data2 == null || !data1.recipeKey.equals(data2.recipeKey))
            return true;

        boolean isFormData1, isFromData2;

        String userId = FirebaseHelper.getUid();

        isFormData1 = data1.stars.containsKey(userId);
        isFromData2 = data2.stars.containsKey(userId);

        boolean isFavEquals;

        if (isFormData1 && isFromData2) {
            isFavEquals = data1.stars.get(userId) == data2.stars.get(userId);
        } else {
            isFavEquals = isFormData1 == isFromData2;
        }

        return !isFavEquals;
    }
}
