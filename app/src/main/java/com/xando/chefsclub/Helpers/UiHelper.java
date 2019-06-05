package com.xando.chefsclub.Helpers;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.Data.RecipeData;

public class UiHelper {
    public static final int DURATION_NORMAL = 800;
    public static final int DURATION_SHORT = 400;

    public static final class Favorite {
        public static void setFavoriteImage(ImageView favoriteImage, RecipeData recipeData) {
            if (recipeData.stars.containsKey(FirebaseHelper.getUid())) {
                favoriteImage.setImageResource(R.drawable.ic_star_blue_24dp);
            } else {
                favoriteImage.setImageResource(R.drawable.ic_star_border_blue_24dp);
            }
        }


        public static YoYo.YoYoString setFavoriteImageWithAnim(ImageView favoriteImage, RecipeData recipeData) {
            return YoYo.with(Techniques.RubberBand)
                    .duration(DURATION_NORMAL)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .onStart(animator -> setFavoriteImage(favoriteImage, recipeData))
                    .playOn(favoriteImage);
        }

        public static YoYo.YoYoString setFavoriteCountWithAnim(TextView starCount, int count) {
            return YoYo.with(Techniques.RubberBand)
                    .duration(DURATION_NORMAL)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .onStart(animator -> starCount.setText(String.valueOf(count)))
                    .playOn(starCount);
        }
    }

    public static final class Other {

        @Nullable
        public static YoYo.YoYoString showFadeAnim(View view, int toVisibility) {
            return showFadeAnim(view, toVisibility, DURATION_SHORT);
        }

        @Nullable
        public static YoYo.YoYoString showFadeAnim(View view, int toVisibility, int duration) {
            if (toVisibility == View.VISIBLE) {
                if (view.getVisibility() == View.VISIBLE)
                    return null;
                view.setVisibility(toVisibility);
            }
            return YoYo.with(toVisibility == View.VISIBLE ? Techniques.FadeIn : Techniques.FadeOut)
                    .duration(duration)
                    .onEnd(animator -> view.setVisibility(toVisibility))
                    .playOn(view);
        }
    }
}
