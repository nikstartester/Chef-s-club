package com.xando.chefsclub.Recipes.EditRecipe.Fragments;

import android.animation.LayoutTransition;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.Helpers.DateTimeHelper;
import com.xando.chefsclub.Helpers.MatisseHelper;
import com.xando.chefsclub.Images.ImageData.ImageData;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.Data.StepOfCooking;
import com.xando.chefsclub.Recipes.Data.StepsData;
import com.xando.chefsclub.Recipes.EditRecipe.DialogTimePicker;
import com.xando.chefsclub.Recipes.EditRecipe.RecyclerViewItems.StepAddItem;
import com.xando.chefsclub.Recipes.EditRecipe.RequiredFields.NormalizeRecipeData;
import com.xando.chefsclub.Recipes.ViewModel.RecipeViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xando.chefsclub.Recipes.EditRecipe.DialogTimePicker.NOT_SELECTED;

public class StepsEditRecipeFragment extends BaseEditRecipeWithKeyFragment
        implements View.OnClickListener, BaseEditRecipeWithKeyFragment.StepsDataSender {

    public static final int MAX_STEPS = 15;
    private static final String TAG = "StepsEditRecipeFragment";
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

    @BindView(R.id.root8)
    protected ViewGroup rootView;

    private StepsData mStepsData;

    private int mCurrentPosition;

    private FastItemAdapter<StepAddItem> mStepsAdapter;

    private DialogFragment mTimeDialog;

    private RecipeViewModel mRecipeViewModel;

    private long mTime;

    public static Fragment getInstance(@Nullable String recipeId) {

        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECIPE_ID, recipeId);

        Fragment fragment = new StepsEditRecipeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);

        mStepsData = new StepsData();

        mTimeDialog = new DialogTimePicker();
        mTimeDialog.setTargetFragment(this, REQUEST_CODE_TIME_PECKER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_recipe_steps, container, false);

        ButterKnife.bind(this, view);

        rootView.getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        unitViews();

        setOnClickListeners();

        mRecipeViewModel.getResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                if (resource.status == ParcResourceByParc.Status.SUCCESS) {

                    mStepsData = resource.data.stepsData;

                    mTime = resource.data.dateTime;
                    try {
                        setDataToViews();
                    } catch (Exception ex) {

                    }
                } else if (resource.status == ParcResourceByParc.Status.ERROR) {

                }
            }
        });

        if (savedInstanceState == null) {
            if (recipeId == null) {
                for (int i = 0; i < NormalizeRecipeData.MIN_STEPS_COUNT; i++) {
                    addEmptyStep(false);
                }
            }
        }

        return view;
    }

    private void setDataToViews() {
        tvTimeMain.setText(DateTimeHelper.convertTime(mStepsData.timeMainNum));

        /*
        Use delay because we have a BUG: when back from captures activities ->
            get exception (java.lang.illegalargumentexception: parameter must be a descendant of this view)
            when adding items.
         */
        recyclerViewSteps.postDelayed(() -> {
            setStepsToAdapter();
        }, 1);
    }

    private void unitViews() {
        recyclerViewSteps.setLayoutManager(ChipsLayoutManager
                .newBuilder(getActivity())
                .setMaxViewsInRow(1)
                .build());

        recyclerViewSteps.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSteps.setNestedScrollingEnabled(false);

        mStepsAdapter = new FastItemAdapter<>();

        recyclerViewSteps.setAdapter(mStepsAdapter);
    }

    private void setOnClickListeners() {
        btnChooseTimeMain.setOnClickListener(this);
        imgViewChooseTimeMain.setOnClickListener(this);
        btnAddStep.setOnClickListener(this);

        mStepsAdapter.withEventHook(initClickEventHook());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addStep:
                onAddSteps();
                break;
            case R.id.btn_chooseTimeMain:
                showTimerPicker(POSITION_MAIN);
                break;
            case R.id.imgView_chooseTimeMain:
                showTimerPicker(POSITION_MAIN);
                break;
        }
    }

    private void onAddSteps() {
        int stepsCount = mStepsAdapter.getAdapterItemCount();

        if (MAX_STEPS - stepsCount > 0) {
            addEmptyStep(true);
        } else {
            String m = "Maximum steps is " + MAX_STEPS;
            Toast.makeText(getActivity(), m, Toast.LENGTH_SHORT).show();
        }
    }

    private ClickEventHook<StepAddItem> initClickEventHook() {
        return new ClickEventHook<StepAddItem>() {
            @Nullable
            @Override
            public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof StepAddItem.ViewHolder) {
                    ArrayList<View> views = new ArrayList<>(4);

                    StepAddItem.ViewHolder castViewHolder = (StepAddItem.ViewHolder) viewHolder;

                    views.add(castViewHolder.itemView
                            .findViewById(R.id.img_image));
                    views.add(castViewHolder.itemView
                            .findViewById(R.id.img_time));
                    views.add(castViewHolder.itemView
                            .findViewById(R.id.img_btn_remove));
                    views.add(castViewHolder.itemView
                            .findViewById(R.id.btn_deleteStep));

                    return views;
                } else return null;
            }

            @Override
            public void onClick(@NonNull View v, int position, @NonNull FastAdapter<StepAddItem> fastAdapter, @NonNull StepAddItem item) {
                switch (v.getId()) {
                    case R.id.img_image:
                        mCurrentPosition = position;
                        item.setFocus();
                        StepsEditRecipeFragment.super.showChooseDialog(1, isHavePhoto());
                        break;
                    case R.id.img_btn_remove:
                        removeImage(position);
                        break;
                    case R.id.img_time:
                        showTimerPicker(position);
                        break;
                    case R.id.btn_deleteStep:
                        mCurrentPosition = position;

                        StepsEditRecipeFragment.super.
                                addToDeleteIfCapture(mStepsData.stepsOfCooking.get(position).imagePath);

                        deleteItemByCurrentPosition();

                        StepsEditRecipeFragment.super.deleteOldCaptures();
                        break;
                }
            }
        };
    }

    private void removeImage(int position) {
        StepsEditRecipeFragment.super.
                addToDeleteIfCapture(mStepsData.stepsOfCooking.get(position).imagePath);

        mStepsAdapter.getAdapterItem(position).removeImage();
        mStepsData.stepsOfCooking.get(position).imagePath = null;

        StepsEditRecipeFragment.super.deleteOldCaptures();
    }

    private boolean isHavePhoto() {
        return mStepsData.stepsOfCooking.get(mCurrentPosition).imagePath != null;
    }

    @Override
    protected void onGalleryFinish(List<Uri> selected) {
        final String imagePath = MatisseHelper.getRealPathFromURIPath(getActivity(), selected.get(0));

        super.addToDeleteIfCapture(mStepsData.stepsOfCooking.get(mCurrentPosition).imagePath);

        mStepsData.stepsOfCooking.get(mCurrentPosition).imagePath = imagePath;

        setImageToCurrentPosition(imagePath);

        super.deleteOldCaptures();
    }

    @Override
    protected void onDeleteImage() {
        removeImage(mCurrentPosition);
    }

    private void setStepsToAdapter() {
        mStepsAdapter.clear();

        for (int i = 0; i < mStepsData.stepsOfCooking.size(); i++) {

            StepOfCooking step = mStepsData.stepsOfCooking.get(i);

            mStepsAdapter.add(new StepAddItem(step, new ImageData(step.imagePath, mTime)));
        }
    }

    private void setImageToCurrentPosition(String imagePath) {
        mStepsAdapter.getAdapterItem(mCurrentPosition).setImage(imagePath);
    }

    private void addEmptyStep(boolean isFocusOnBind) {
        mStepsAdapter.add(new StepAddItem(isFocusOnBind));
        mStepsData.stepsOfCooking.add(new StepOfCooking());
    }

    private void showTimerPicker(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(DialogTimePicker.SELECTED_ITEM, position);

        mTimeDialog.setArguments(bundle);
        mTimeDialog.show(getFragmentManager(), "mTimeDialog");
    }

    private void deleteItemByCurrentPosition() {
        mStepsAdapter.getAdapterItem(mCurrentPosition).setFocus();

        mStepsAdapter.remove(mCurrentPosition);

        mStepsData.removeStepOfPosition(mCurrentPosition);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TIME_PECKER) {
            final int num = data.getIntExtra(DialogTimePicker.SELECTED_ITEM, -404);

            if (resultCode == DialogTimePicker.RESULT_CODE_TIME_SELECTED
                    || resultCode == DialogTimePicker.RESULT_CODE_TIME_CLEAR) {
                final int time = data.getIntExtra(DialogTimePicker.SELECTED_TIME, NOT_SELECTED);

                setTimeToStepData(num, time);
                setTimeToTextView(num, time);

            }

        }
    }

    private void setTimeToStepData(int currentPosition, int time) {
        if (currentPosition == POSITION_MAIN) {
            mStepsData.timeMainNum = time;
        } else {
            mStepsData.stepsOfCooking.get(currentPosition).timeNum = time;
        }
    }

    private void setTimeToTextView(int currentPosition, int time) {
        if (currentPosition == POSITION_MAIN) {
            tvTimeMain.setText(time == NOT_SELECTED ? "" : DateTimeHelper.convertTime(time));
        } else {
            mStepsAdapter.getAdapterItem(currentPosition).setTime(time);
        }
    }

    private void setTextsToData() {
        if (mStepsData.stepsOfCooking.size() != mStepsAdapter.getAdapterItems().size())
            throw new IndexOutOfBoundsException("StepsOfCooking size might == adapter item count");

        for (int i = 0; i < mStepsData.stepsOfCooking.size(); i++) {
            mStepsData.stepsOfCooking.get(i).text = mStepsAdapter.getAdapterItem(i).getTextOfStep();
        }
    }

    @Override
    public boolean isValidate() {
        return true;
    }

    @Override
    public StepsData getData() {
        setTextsToData();
        return mStepsData;
    }
}
