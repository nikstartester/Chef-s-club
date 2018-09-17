package com.example.nikis.bludogramfirebase.BaseFragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nikis.bludogramfirebase.Exceptions.IncorrectPositionException;
import com.example.nikis.bludogramfirebase.FirebaseReferences;
import com.example.nikis.bludogramfirebase.GlideApp;
import com.example.nikis.bludogramfirebase.GlideEngineV4;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.AllImagesData;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems.ImageAddItem;
import com.google.firebase.database.DatabaseReference;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.nikis.bludogramfirebase.BaseActivities.BaseActivityWithImageClick.TAG_TEST;


public abstract class BaseFragmentWithImageClick extends BaseNewRecipeFragment{
    protected static final String KEY_IMAGE_PATH = "allTimeImagesPathArray";
    protected static final int REQUEST_CODE_CHOOSE_IMAGE = 2;

    private static String recipeKey  = null;

    private ImageView imageView;

    private FastItemAdapter<ImageAddItem> adapterImages;

    private boolean isMainPictureClick = true;

    private AllImagesData allImagesData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapterImages = new FastItemAdapter<>();

        if(savedInstanceState != null){
            ArrayList<String> imagesPath = savedInstanceState.getStringArrayList(KEY_IMAGE_PATH);
            allImagesData = new AllImagesData(imagesPath, imagesPath);
        }else {
            allImagesData = new AllImagesData();
            //setRecipeKey();
        }
        adapterImages.withEventHook(initClickEventHook());

    }
    private void setRecipeKey(){
        DatabaseReference myRef = FirebaseReferences.getDataBaseReference();
        recipeKey = myRef.child("recipes").push().getKey();
    }

    private ClickEventHook<ImageAddItem> initClickEventHook(){
        return new ClickEventHook<ImageAddItem>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if(viewHolder instanceof ImageAddItem.ViewHolder)
                    return ((ImageAddItem.ViewHolder)viewHolder).itemView
                            .findViewById(R.id.img_btn_remove);
                else return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<ImageAddItem> fastAdapter, ImageAddItem item) {
                adapterImages.remove(position);

                allImagesData.removePath(position + 1);
            }
        };
    }

    protected void imageViewClick(ImageView imageView, RecyclerView recyclerView){
        this.imageView = imageView;

        setAdapterToRvAndStartImagePick(recyclerView);
    }
    protected void imageViewClick(RecyclerView recyclerView){
        isMainPictureClick = false;
        setAdapterToRvAndStartImagePick(recyclerView);
    }
    private void setAdapterToRvAndStartImagePick(RecyclerView recyclerView){
        if (recyclerView.getAdapter() != null)
            recyclerView.setAdapter(adapterImages);

        //TODO permissions in fragments
        /*ActivityCompat.requestPermissions(getActivity(),
                new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},1);
        */
        startImagePicker();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if(requestCode == 1){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startImagePicker();
            }
        }
    }

    private void startImagePicker(){
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .countable(true)
                .maxSelectable(12)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.95f)
                .imageEngine(new GlideEngineV4())
                .forResult(REQUEST_CODE_CHOOSE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            List<Uri> mSelected = Matisse.obtainResult(data);

            setData(mSelected);

            setImages();
        }
    }

    private void setData(List<Uri> mSelected){

        allImagesData.setEmptySelectedImagesPathArray();

        String imagePathOfFirstImage = getRealPathFromURIPath(mSelected.get(0));

        if(isMainPictureClick){
            allImagesData.setFirstElementToFirstPosition(imagePathOfFirstImage);
        }
        else {
            allImagesData.addFirstElementsToNOTFirstPosition(imagePathOfFirstImage);
            allImagesData.addSelectedImagePath(null);
        }
        setOtherElements(mSelected);
    }

    private String getRealPathFromURIPath(Uri contentUri) {
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void setOtherElements(List<Uri> mSelected){
        allImagesData.addSelectedImagePath(getRealPathFromURIPath(mSelected.get(0)));

        for (int i = 1; i < mSelected.size(); i++){
            String currImagePath = getRealPathFromURIPath(mSelected.get(i));
            allImagesData.addElement(currImagePath);
            allImagesData.addSelectedImagePath(currImagePath);
        }
    }

    private void setImages() {
        try {
            if (isMainPictureClick) {
                setImageToImageView();
            } else {
                isMainPictureClick = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        setSelectedImagesToRecyclerView();
    }

    private void setImageToImageView() throws IncorrectPositionException{
        String imagePath = allImagesData.getImagePath(0);
        if(imagePath == null){
            imageView.setImageResource(R.drawable.ic_add_a_photo_blue_108dp);
        } else GlideApp.with(this)
                .load(imagePath)
                //.asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
    private void setSelectedImagesToRecyclerView() {
        for(int i = 1; i < allImagesData.getCountOfSelectedImagesPath(); i++){
            String imagePath = allImagesData.getSelectedImagePath(i);
            adapterImages.add(new ImageAddItem(imagePath));
        }
    }


    protected void setImagesWithSavedState(@NonNull ImageView imageView,
                                           @NonNull RecyclerView recyclerView){
        this.imageView = imageView;
        recyclerView.setAdapter(adapterImages);
        try {
            setImageToImageView();
        } catch (IncorrectPositionException e) {
            e.printStackTrace();
        }
        setSelectedImagesToRecyclerView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG_TEST, "onSaveInstanceState: ");
        outState.putStringArrayList(KEY_IMAGE_PATH, allImagesData.getImagesPathArray());
        super.onSaveInstanceState(outState);
    }

    @Override
    public ArrayList<String> getImagesPath(){
        return allImagesData.getImagesPathArray();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        /*RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);*/
    }

}
