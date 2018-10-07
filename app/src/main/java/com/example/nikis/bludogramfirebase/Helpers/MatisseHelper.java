package com.example.nikis.bludogramfirebase.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

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
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}
