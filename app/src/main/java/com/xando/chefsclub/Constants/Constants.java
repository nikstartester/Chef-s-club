package com.xando.chefsclub.Constants;

import android.content.Context;

import com.xando.chefsclub.R;

import java.io.File;

public final class Constants {

    public static final class Files {

        public static final String TEMPORARY_FILES_FOLDER_NAME = "temporary_files";
        public static final String EDIT_RECIPES_FOLDER_NAME = "edit_recipes";
        public static final String EDIT_PROFILES_FOLDER_NAME = "edit_profiles";
        public static final String CAPTURES_FOLDER_NAME = "captures";
        public static final String COMPRESSED_IMAGES_FOLDER_NAME = "compressed_files";

        public static String getDirectoryForCompressRecipesImages(Context context) {
            return getDirectoryForEditRecipeImages(context)
                    + File.separator + COMPRESSED_IMAGES_FOLDER_NAME;
        }

        public static String getDirectoryForEditRecipeImages(Context context) {
            return getDirectoryForTemporaryFiles(context)
                    + File.separator + Files.EDIT_RECIPES_FOLDER_NAME;
        }

        public static String getDirectoryForCompressProfilesImages(Context context) {
            return getDirectoryForEditProfileImages(context)
                    + File.separator + COMPRESSED_IMAGES_FOLDER_NAME;
        }

        public static String getDirectoryForEditProfileImages(Context context) {
            return getDirectoryForTemporaryFiles(context)
                    + File.separator + Files.EDIT_PROFILES_FOLDER_NAME;
        }

        public static String getDirectoryForCompressFiles(Context context) {
            return getDirectoryForTemporaryFiles(context)
                    + File.separator + COMPRESSED_IMAGES_FOLDER_NAME;
        }

        public static String getDirectoryForTemporaryFiles(Context context) {
            return context.getFilesDir().getPath() + File.separator + Files.TEMPORARY_FILES_FOLDER_NAME;
        }

        public static String getDirectoryForCaptures(Context context) {
            return context.getFilesDir().getPath() + File.separator + Files.CAPTURES_FOLDER_NAME;
        }
    }

    public static final class Settings {

        public static final String APP_PREFERENCES = "mysettings";

        public static final String MAX_IMAGE_CACHE_SIZE = "maxImageCacheSize";
        public static final int INFINITY = -1;
        public static final int[] DEFAULT_MAX_IMAGE_CACHE_SIZE_VALUES = {10, 25, 50, 100, 200, INFINITY};
        public static final int DEFAULT_MAX_IMAGE_CACHE_SIZE = DEFAULT_MAX_IMAGE_CACHE_SIZE_VALUES[2];

        public static final String SAVING_LOCAL_VIEWED_RECIPES = "saving_on_local_all_viewed_recipes";
        public static final String SAVING_LOCAL_NEW_RECIPES = "saving_on_local_new_recipes";
        public static final boolean DEF_SAVING_LOCAL_VIEWED_RECIPES = false;
        public static final boolean DEF_SAVING_LOCAL_NEW_RECIPES = false;
    }

    public static final class ImageConstants {

        public static final String FIREBASE_STORAGE_AT_START = "fbs_chef's_club_images";

        public static final int RESIZE_PROFILE_SMALL_IMAGE_SIZE = 156;
        public static final int RESIZE_PROFILE_NORMAL_IMAGE_SIZE = 720;

        public static final int DRAWABLE_ERROR = R.drawable.ic_add_a_photo_blue_1080dp;
        public static final int DRAWABLE_ADD_PHOTO_PLACEHOLDER = R.drawable.ic_add_a_photo_blue_1080dp;
        public static final int PLACEHOLDER = R.color.image_placeholder;

        public static final long DEF_TIME = -1354;
    }

    public static final class Login {

        public static final String KEY_IS_ALREADY_REGISTERED = "com.xando.chefsclub." +
                "Constants.isAlreadyRegistered";
    }

    public static final class AlgoliaSearch {

        public static final String ALGOLIA_APP_ID = "WTD7CAVR1C";
        public static final String ALGOLIA_SEARCH_API_KEY = "cc9dd431ad0646ca03f9a7d0b85a426f";
    }
}
