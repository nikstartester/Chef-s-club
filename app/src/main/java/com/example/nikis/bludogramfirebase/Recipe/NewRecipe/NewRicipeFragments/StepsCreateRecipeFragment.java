package com.example.nikis.bludogramfirebase.Recipe.NewRecipe.NewRicipeFragments;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.example.nikis.bludogramfirebase.BaseFragments.BaseNewRecipeFragment;
import com.example.nikis.bludogramfirebase.GlideEngineV4;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.DialogTimePicker;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems.StepAddItem;
import com.example.nikis.bludogramfirebase.Recipe.StepsData;
import com.example.nikis.bludogramfirebase.RecipeData.RecipeData;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.nikis.bludogramfirebase.Recipe.NewRecipe.DialogTimePicker.NOT_SELECTED;

public class StepsCreateRecipeFragment extends BaseNewRecipeFragment implements View.OnClickListener{
    private static final String KEY_TEXT_STEPS = "textSteps";
    private static final String KEY_IMAGES_PATH = "imagesPath(0)";
    private static final String KEY_TIMES = "times";
    private static final String KEY_TIME_MAIN = "timeMain";

    protected static final int REQUEST_CODE_CHOOSE_IMAGE = 5;

    private static final int REQUEST_CODE_TIME_PECKER = 7;
    private static final int POSITION_MAIN = -1;

    protected volatile FastItemAdapter<StepAddItem> adapterSteps;

    private DialogFragment timeDialogTest;

    private StepsData stepsData;
    private int currentPosition;

    private TextView tvTimeMain;
    private String timeMain;

    public StepsCreateRecipeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_recipe_steps, container, false);

        ((ViewGroup) view.findViewById(R.id.root8)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        tvTimeMain = view.findViewById(R.id.tv_timeMain);

        Button btnChooseTimeMain = view.findViewById(R.id.btn_chooseTimeMain);
        ImageView imgViewChooseTimeMain = view.findViewById(R.id.imgView_chooseTimeMain);
        Button btnAddStep = view.findViewById(R.id.btn_addStep);

        btnChooseTimeMain.setOnClickListener(this);
        imgViewChooseTimeMain.setOnClickListener(this);
        btnAddStep.setOnClickListener(this);

        RecyclerView recyclerViewSteps = view.findViewById(R.id.rv_steps);

        recyclerViewSteps.setLayoutManager(ChipsLayoutManager
                .newBuilder(getActivity())
                .setMaxViewsInRow(1)
                .build());

        recyclerViewSteps.setItemAnimator(new DefaultItemAnimator());

        adapterSteps = new FastItemAdapter<>();

        recyclerViewSteps.setAdapter(adapterSteps);

        if(savedInstanceState != null){
            ArrayList<String> texts = savedInstanceState.getStringArrayList(KEY_TEXT_STEPS);
            ArrayList<String> imagesPath = savedInstanceState.getStringArrayList(KEY_IMAGES_PATH);
            ArrayList<String> times = savedInstanceState.getStringArrayList(KEY_TIMES);

            stepsData = new StepsData(texts, imagesPath, times);

            timeMain = savedInstanceState.getString(KEY_TIME_MAIN);
            tvTimeMain.setText(timeMain);

            /*
              //There is a guarantee that the arrays sizes are equal and != null
              JUST TRUST ME!
             */
            for(int i = 0; i < times.size(); i++){
                adapterSteps.add(new StepAddItem(texts.get(i), imagesPath.get(i), times.get(i)));
            }
        }else {
            stepsData = new StepsData();
            addEmptyStep(false);
        }

        adapterSteps.withEventHook(initClickEventHook());
        adapterSteps.withOnLongClickListener((v, adapter, item, position) -> onLongClickToItem(position));

        timeDialogTest = new DialogTimePicker();
        timeDialogTest.setTargetFragment(this, REQUEST_CODE_TIME_PECKER);

        return view;
    }



    private void addEmptyStep(boolean isFocusOnBind){
        adapterSteps.add(new StepAddItem(isFocusOnBind));
        stepsData.addEmptyStepData();
    }

    private ClickEventHook<StepAddItem> initClickEventHook(){
        return new ClickEventHook<StepAddItem>() {
            @Nullable
            @Override
            public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
                if(viewHolder instanceof StepAddItem.ViewHolder){
                    ArrayList<View> views = new ArrayList<>(4);

                    StepAddItem.ViewHolder castViewHolder = (StepAddItem.ViewHolder)viewHolder;

                    views.add(castViewHolder.itemView
                            .findViewById(R.id.img_image));
                    views.add(castViewHolder.itemView
                            .findViewById(R.id.img_time));
                    views.add(castViewHolder.itemView
                            .findViewById(R.id.img_btn_remove));
                    views.add(castViewHolder.itemView
                            .findViewById(R.id.btn_deleteStep));

                    return views;
                }
                else return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<StepAddItem> fastAdapter, StepAddItem item) {
                switch (v.getId()){
                    case R.id.img_image:
                        currentPosition = position;
                        pickImage();
                        break;
                    case R.id.img_btn_remove:
                        item.setEmptyImage();
                        stepsData.removePositionOfImagesPath(position);
                        break;
                    case R.id.img_time:
                        showTimerPicker(position);
                        break;
                    case R.id.btn_deleteStep:
                        currentPosition = position;
                        deleteItemByCurrentPosition();
                        break;
                }
            }
        };
    }

    private void pickImage() {
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .countable(false)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.9f)
                .imageEngine(new GlideEngineV4())
                .forResult(REQUEST_CODE_CHOOSE_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            final List<Uri> mSelected = Matisse.obtainResult(data);

            final String imagePath = getRealPathFromURIPath(mSelected.get(0));

            stepsData.setImagePathToPosition(imagePath, currentPosition);

            setImageToImageViewIntoRv(imagePath);
        }
        if(requestCode == REQUEST_CODE_TIME_PECKER && resultCode == DialogTimePicker.RESULT_CODE_TIME_SELECTED){
            final String time = data.getStringExtra(DialogTimePicker.SELECTED_TIME);
            final int num = data.getIntExtra(DialogTimePicker.SELECTED_ITEM, -404);

            setTimeToStepDataAndTextView(num, time);
        }

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
    private void setImageToImageViewIntoRv(String imagePath){
        adapterSteps.getAdapterItem(currentPosition).setImage(imagePath);
    }

    private void showTimerPicker(int position){
        Bundle bundle = new Bundle();
        bundle.putInt(DialogTimePicker.SELECTED_ITEM, position);

        timeDialogTest.setArguments(bundle);
        timeDialogTest.show(getFragmentManager(),"timeDialog");
    }

    private boolean onLongClickToItem(int position){
        currentPosition = position;
        createAndShowAlertDialogToDeleteItem();
        return false;
    }
    private void createAndShowAlertDialogToDeleteItem(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        String positiveButton = getString(R.string.action_delete);
        String negativeButton = getString(R.string.action_cancel);

        alertDialogBuilder.setMessage("Are you sure you want to delete the selected item?");
        alertDialogBuilder.setPositiveButton(positiveButton, (dialog, which) -> {
            deleteItemByCurrentPosition();
        });
        alertDialogBuilder.setNegativeButton(negativeButton, null);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.show();
    }

    private void deleteItemByCurrentPosition(){
        adapterSteps.getAdapterItem(currentPosition).setFocus();

        adapterSteps.remove(currentPosition);

        stepsData.removeStepOfPosition(currentPosition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_addStep:
                addEmptyStep(true);
                break;
            case R.id.btn_chooseTimeMain:
                showTimerPicker(POSITION_MAIN);
                break;
            case R.id.imgView_chooseTimeMain:
                showTimerPicker(POSITION_MAIN);
                break;
        }
    }
    private void setTimeToStepDataAndTextView(int currentPosition, String time){
        if(time.equals(NOT_SELECTED))
            return;

        if(currentPosition == POSITION_MAIN){
            tvTimeMain.setText(time);
            timeMain = time;
        }else {
            adapterSteps.getAdapterItem(currentPosition).setTime(time);

            stepsData.setTimeToPosition(time, currentPosition);
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(KEY_TEXT_STEPS, getAllStepsText());
        outState.putStringArrayList(KEY_IMAGES_PATH, stepsData.getImagesPath());
        outState.putStringArrayList(KEY_TIMES, stepsData.getTimes());
        outState.putString(KEY_TIME_MAIN, timeMain);

    }
    private ArrayList<String> getAllStepsText() {
        ArrayList<String> texts = new ArrayList<>();
        for(StepAddItem item : adapterSteps.getAdapterItems()){
            texts.add(item.getTextOfStep());
        }
        return texts;
    }
    public RecipeData getStepsData(){
        String allTime = timeMain;
        ArrayList<String> steps = getAllStepsText();
        ArrayList<String> timesOfSteps = stepsData.getTimes();
        return new RecipeData(allTime, steps, timesOfSteps,"unknown");
    }

    @Override
    public RecipeData getData() {
        return getStepsData();
    }

    @Override
    public ArrayList<String> getImagesPath() {
        return stepsData.getImagesPath();
    }

    @Override
    public boolean getIsStepsCooking() {
        return true;
    }
}
