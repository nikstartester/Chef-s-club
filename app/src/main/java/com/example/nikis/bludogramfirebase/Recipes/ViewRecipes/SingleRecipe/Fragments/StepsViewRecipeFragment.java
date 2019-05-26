package com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SingleRecipe.Fragments;


import android.animation.LayoutTransition;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.example.nikis.bludogramfirebase.Constants.Constants;
import com.example.nikis.bludogramfirebase.DataWorkers.ParcResourceByParc;
import com.example.nikis.bludogramfirebase.Helpers.DateTimeHelper;
import com.example.nikis.bludogramfirebase.Images.ImageData.ImageData;
import com.example.nikis.bludogramfirebase.Images.ViewImages.ViewImagesActivity;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.Data.StepOfCooking;
import com.example.nikis.bludogramfirebase.Recipes.Data.StepsData;
import com.example.nikis.bludogramfirebase.Recipes.EditRecipe.DialogTimePicker;
import com.example.nikis.bludogramfirebase.Recipes.ViewModel.RecipeViewModel;
import com.example.nikis.bludogramfirebase.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.StepViewItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsViewRecipeFragment extends BaseFragmentWithRecipeKey {

    private static final int REQUEST_CODE_TIME_PECKER = 7;

    private static final int POSITION_MAIN = -1;

    @BindView(R.id.tv_timeMain)
    protected TextView tvTimeMain;

    @BindView(R.id.imgView_chooseTimeMain)
    protected ImageView imgViewChooseTimeMain;

    @BindView(R.id.rv_steps)
    protected RecyclerView recyclerViewSteps;

    private StepsData mStepsData;

    private FastItemAdapter<StepViewItem> mStepsAdapter;

    private DialogFragment mTimeDialog;

    private RecipeViewModel mRecipeViewModel;

    private long mTime = Constants.ImageConstants.DEF_TIME;

    public static Fragment getInstance(@Nullable String recipeId) {

        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECIPE_ID, recipeId);

        Fragment fragment = new StepsViewRecipeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStepsAdapter = new FastItemAdapter<>();

        mRecipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);

        mStepsData = new StepsData();

        mTimeDialog = new DialogTimePicker();
        mTimeDialog.setTargetFragment(this, REQUEST_CODE_TIME_PECKER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_recipe_steps, container, false);

        ((ViewGroup) view.findViewById(R.id.root8)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        ButterKnife.bind(this, view);

        unitViews();

        setOnClickListeners();

        mRecipeViewModel.getResourceLiveData().observe(this, resource -> {
            if (resource != null) {
                if (resource.status == ParcResourceByParc.Status.SUCCESS) {

                    if (mTime == Constants.ImageConstants.DEF_TIME) {
                        mStepsData = resource.data.stepsData;

                        mTime = resource.data.dateTime;

                        setDataToViews();
                    }

                } else if (resource.status == ParcResourceByParc.Status.ERROR) {

                }
            }
        });

        return view;
    }

    private void setDataToViews() {
        tvTimeMain.setText(DateTimeHelper.convertTime(mStepsData.timeMainNum));

        setStepsToAdapter();
    }

    private void unitViews() {
        recyclerViewSteps.setLayoutManager(ChipsLayoutManager
                .newBuilder(getActivity())
                .setMaxViewsInRow(1)
                .build());

        recyclerViewSteps.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSteps.setNestedScrollingEnabled(false);

        recyclerViewSteps.setAdapter(mStepsAdapter);
    }

    private void setOnClickListeners() {
        mStepsAdapter.withEventHook(initClickEventHook());
    }


    private ClickEventHook<StepViewItem> initClickEventHook() {
        return new ClickEventHook<StepViewItem>() {
            @Nullable
            @Override
            public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof StepViewItem.ViewHolder) {
                    ArrayList<View> views = new ArrayList<>();

                    StepViewItem.ViewHolder castViewHolder = (StepViewItem.ViewHolder) viewHolder;

                    views.add(castViewHolder.itemView
                            .findViewById(R.id.img_image));
                    views.add(castViewHolder.itemView
                            .findViewById(R.id.img_time));

                    return views;
                } else return null;
            }

            @Override
            public void onClick(@NonNull View v, int position, @NonNull FastAdapter<StepViewItem> fastAdapter, @NonNull StepViewItem item) {
                switch (v.getId()) {
                    case R.id.img_image:

                        imageViewClick(position);
                        break;
                    case R.id.img_time:

                        showTimerPicker(position);
                        break;
                }
            }
        };
    }


    private void setStepsToAdapter() {

        mStepsAdapter.clear();

        for (int i = 0; i < mStepsData.stepsOfCooking.size(); i++) {

            StepOfCooking step = mStepsData.stepsOfCooking.get(i);

            mStepsAdapter.add(new StepViewItem(step, new ImageData(step.imagePath, mTime)));
        }
    }

    private void imageViewClick(int pos) {

        ArrayList<ImageData> dataList = new ArrayList<>();
        int count = 0;

        for (int i = 0; i < mStepsData.stepsOfCooking.size(); i++) {
            StepOfCooking step = mStepsData.stepsOfCooking.get(i);

            if (step.imagePath != null) {
                dataList.add(new ImageData(step.imagePath, mTime));

                if (i == pos) {
                    pos = count;
                }

                count++;
            }

        }


        if (!dataList.isEmpty()) {
            startActivity(ViewImagesActivity.getIntent(getActivity(), dataList, pos));
        }
    }

    private void showTimerPicker(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(DialogTimePicker.SELECTED_ITEM, position);

        mTimeDialog.setArguments(bundle);
        mTimeDialog.show(getFragmentManager(), "mTimeDialog");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TIME_PECKER && resultCode == DialogTimePicker.RESULT_CODE_TIME_SELECTED) {
            /*final String time = data.getStringExtra(DialogTimePicker.SELECTED_TIME);
            final int num = data.getIntExtra(DialogTimePicker.SELECTED_ITEM, -404);

            setTimeToStepData(num, time);
            setTimeToTextView(num, time);*/
        }
    }


}
