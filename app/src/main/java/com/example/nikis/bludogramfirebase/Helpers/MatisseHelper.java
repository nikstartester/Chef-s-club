package com.example.nikis.bludogramfirebase.Helpers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;

import com.example.nikis.bludogramfirebase.GlideEngineV4;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.SelectionSpecBuilder;

public class MatisseHelper {

    public static SelectionSpecBuilder getMatisseBuilder(Activity context, int maxSelectable){
        return  addMaxSelectable(makeMatisseBuilder(Matisse.from(context)), maxSelectable);
    }
    public static SelectionSpecBuilder getMatisseBuilder(Fragment fragment, int maxSelectable){
        return addMaxSelectable(makeMatisseBuilder(Matisse.from(fragment)), maxSelectable);
    }

    private static SelectionSpecBuilder makeMatisseBuilder(Matisse matisse){
        return matisse.choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.9f)
                .imageEngine(new GlideEngineV4());
    }

    private static SelectionSpecBuilder addMaxSelectable(SelectionSpecBuilder builder, int count){
        if(count == 1){
            builder.countable(false)
                    .maxSelectable(1);
        }else builder.maxSelectable(count);

        return builder;
    }

    public static String getRealPathFromURIPath(Context context, Uri contentUri) {

        /*if (Build.VERSION.SDK_INT < 19)
            return getRealPathFromURI_API11to18(context, contentUri);
        else
            return getRealPathFromURI_API19(context, contentUri);
        */
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

            String path = cursor.getString(idx);

            cursor.close();

            return path;
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);

            cursor.close();
        }
        return result;
    }

}
