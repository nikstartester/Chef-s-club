package com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.example.nikis.bludogramfirebase.Helpers.MatisseHelper;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipe.Data.StepOfCooking;
import com.example.nikis.bludogramfirebase.Recipe.NewRecipe.DialogTimePicker;
import com.example.nikis.bludogramfirebase.Recipe.EditRecipeTest.RecyclerViewItems.StepAddItem;
import com.example.nikis.bludogramfirebase.Recipe.Data.StepsData;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.nikis.bludogramfirebase.Recipe.NewRecipe.DialogTimePicker.NOT_SELECTED;

public class StepsEditRecipeFragment extends BaseEditRecipeFragment implements View.OnClickListener {

    private static final String KEY_STEPS_DATA = "stepsData";

    protected static final int REQUEST_CODE_CHOOSE_IMAGE = 5;

    private static final int REQUEST_CODE_TIME_PECKER = 7;

    private static final int POSITION_MAIN = -1;

    @BindView(R.id.tv_timeMain)
    protected TextView tvTimeMain;

    @BindView(R.id.btn_chooseTimeMain)
    protected Button btnChooseTimeMain;

    @BindView(R.id.imgView_chooseTimeMain)
    protected ImageView imgViewChooseTimeMain;

    @BindView(R.id.btn_addStep)
    protected Button btnAddStep;

    @BindView(R.id.rv_steps)
    protected RecyclerView recyclerViewSteps;

    private StepsData mStepsData;

    private int mCurrentPosition;

    private FastItemAdapter<StepAddItem> mStepsAdapter;

    private DialogFragment mTimeDialog;

    public static Fragment getInstance(@Nullable String recipeId){

        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECIPE_ID, recipeId);

        Fragment fragment = new StepsEditRecipeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            mStepsData = savedInstanceState.getParcelable(KEY_STEPS_DATA);
        }else {
            mStepsData = new StepsData();
        }

        mTimeDialog = new DialogTimePicker();
        mTimeDialog.setTargetFragment(this, REQUEST_CODE_TIME_PECKER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_recipe_steps, container, false);

        ((ViewGroup) view.findViewById(R.id.root8)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        ButterKnife.bind(this, view);

        unitViews();

        setOnClickListeners();

        if(savedInstanceState != null){
            tvTimeMain.setText(mStepsData.timeMain);

            for(int i = 0; i < mStepsData.stepsOfCooking.size(); i++){
                mStepsAdapter.add(new StepAddItem(mStepsData.stepsOfCooking.get(i)));
            }
        }else {

            addEmptyStep(false);
        }

        return view;
    }

    private void unitViews(){
        recyclerViewSteps.setLayoutManager(ChipsLayoutManager
                .newBuilder(getActivity())
                .setMaxViewsInRow(1)
                .build());

        recyclerViewSteps.setItemAnimator(new DefaultItemAnimator());

        mStepsAdapter = new FastItemAdapter<>();

        recyclerViewSteps.setAdapter(mStepsAdapter);
    }

    private void setOnClickListeners(){
        btnChooseTimeMain.setOnClickListener(this);
        imgViewChooseTimeMain.setOnClickListener(this);
        btnAddStep.setOnClickListener(this);

        mStepsAdapter.withEventHook(initClickEventHook());
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
                        mCurrentPosition = position;
                        StepsEditRecipeFragment.super.startMatisseGallery(1);
                        break;
                    case R.id.img_btn_remove:
                        item.setEmptyImage();
                        mStepsData.stepsOfCooking.get(position).imagePath = null;
                        break;
                    case R.id.img_time:
                        showTimerPicker(position);
                        break;
                    case R.id.btn_deleteStep:
                        mCurrentPosition = position;
                        deleteItemByCurrentPosition();
                        break;
                }
            }
        };
    }

    @Override
    protected void onGalleryFinish(List<Uri> selected) {
        final String imagePath = MatisseHelper.getRealPathFromURIPath(getActivity(), selected.get(0));

        mStepsData.stepsOfCooking.get(mCurrentPosition).imagePath = imagePath;

        setImageToCurrentPosition(imagePath);
    }

    private void setImageToCurrentPosition(String imagePath) {
        mStepsAdapter.getAdapterItem(mCurrentPosition).setImage(imagePath);
    }


    private void addEmptyStep(boolean isFocusOnBind){
        mStepsAdapter.add(new StepAddItem(isFocusOnBind));
        mStepsData.stepsOfCooking.add(new StepOfCooking());
    }

    private void showTimerPicker(int position){
        Bundle bundle = new Bundle();
        bundle.putInt(DialogTimePicker.SELECTED_ITEM, position);

        mTimeDialog.setArguments(bundle);
        mTimeDialog.show(getFragmentManager(),"mTimeDialog");
    }

    private void deleteItemByCurrentPosition(){
        mStepsAdapter.getAdapterItem(mCurrentPosition).setFocus();

        mStepsAdapter.remove(mCurrentPosition);

        mStepsData.removeStepOfPosition(mCurrentPosition);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_TIME_PECKER && resultCode == DialogTimePicker.RESULT_CODE_TIME_SELECTED){
            final String time = data.getStringExtra(DialogTimePicker.SELECTED_TIME);
            final int num = data.getIntExtra(DialogTimePicker.SELECTED_ITEM, -404);

            setTimeToStepData(num, time);
            setTimeToTextView(num, time);
        }
    }

    private void setTimeToStepData(int currentPosition, String time){
        if(time.equals(NOT_SELECTED))
            return;

        if(currentPosition == POSITION_MAIN){
            mStepsData.timeMain = time;
        }else {
            mStepsData.stepsOfCooking.get(currentPosition).time = time;
        }
    }

    private void setTimeToTextView(int currentPosition, String time){
        if(time.equals(NOT_SELECTED))
            return;

        if(currentPosition == POSITION_MAIN){
            tvTimeMain.setText(time);
        }else {
            mStepsAdapter.getAdapterItem(currentPosition).setTime(time);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        setTextsToData();

        outState.putParcelable(KEY_STEPS_DATA, mStepsData);

    }

    private void setTextsToData() {
        if(mStepsData.stepsOfCooking.size() != mStepsAdapter.getAdapterItems().size())
            throw new IndexOutOfBoundsException("StepsOfCooking size might == adapter item count");

        for (int i = 0; i < mStepsData.stepsOfCooking.size(); i++) {
            mStepsData.stepsOfCooking.get(i).text = mStepsAdapter.getAdapterItem(i).getTextOfStep();
        }
    }

}
