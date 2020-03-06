package com.xando.chefsclub.search.recipes.filter.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.xando.chefsclub.R;
import com.xando.chefsclub.helper.DateTimeHelper;
import com.xando.chefsclub.recipes.editrecipe.DialogTimePicker;
import com.xando.chefsclub.recipes.editrecipe.recyclerviewitems.ChipCategoryWithRemoveItem;
import com.xando.chefsclub.search.core.FilterAdapter;
import com.xando.chefsclub.search.recipes.filter.RecipeFilterAdapter;
import com.xando.chefsclub.search.recipes.filter.RecipeFilterData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.xando.chefsclub.recipes.editrecipe.ChooseCategoriesActivity.SELECTED_ITEMS;
import static com.xando.chefsclub.recipes.editrecipe.DialogTimePicker.NOT_SELECTED;


public class FilterDialog extends BottomSheetDialogFragment {

    public static final String FILTER_DATA = "filterData";
    public static final int RESULT_CODE_FILTER_APPLY = 421;
    private static final String KEY_FILTER_DATA = "keyFilterData";
    private static final int REQUEST_CODE_CHOOSE_CATEGORIES = 89;
    private static final int REQUEST_CODE_TIME_PECKER = 7;
    private static final int POS_MIN = 0;
    private static final int POS_MAX = 1;

    @BindView(R.id.search_from)
    protected Spinner searchFrom;
    @BindView(R.id.categories)
    protected RecyclerView categories;
    @BindView(R.id.btn_time_min)
    protected Button timeMinBtn;
    @BindView(R.id.btn_time_max)
    protected Button timeMaxBtn;

    private FilterAdapter<RecipeFilterData> mFilterAdapter;

    private DialogFragment mTimeDialog;

    private FastItemAdapter<ChipCategoryWithRemoveItem> mCategoriesAdapter;

    public static DialogFragment getInstance(RecipeFilterData filterData) {
        DialogFragment dialogFragment = new FilterDialog();

        dialogFragment.setArguments(getArgs(filterData));

        return dialogFragment;
    }

    public static Bundle getArgs(RecipeFilterData filterData) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(KEY_FILTER_DATA, filterData);

        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFilterAdapter = new RecipeFilterAdapter();

        RecipeFilterData filterData = null;

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                filterData = getArguments().getParcelable(KEY_FILTER_DATA);
            }
        } else {
            filterData = savedInstanceState.getParcelable(KEY_FILTER_DATA);
        }
        mFilterAdapter.setData(filterData == null ? new RecipeFilterData() : filterData.getClone());

        mCategoriesAdapter = new FastItemAdapter<>();

        mTimeDialog = new DialogTimePicker();
        mTimeDialog.setTargetFragment(this, REQUEST_CODE_TIME_PECKER);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_search_filter_recipes, container, false);

        ButterKnife.bind(this, view);

        initViews();

        setDataToViews();

        setListeners();

        return view;
    }

    private void initViews() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, RecipeFilterData.searchFromStrings);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        searchFrom.setAdapter(adapter);

        categories.setLayoutManager(ChipsLayoutManager
                .newBuilder(getActivity())
                .build());
        categories.setNestedScrollingEnabled(false);

        categories.setAdapter(mCategoriesAdapter);

        categories.setItemAnimator(new DefaultItemAnimator());
    }

    private void setDataToViews() {
        searchFrom.setSelection(mFilterAdapter.getData().getSearchFrom());

        setCategoriesToRv();

        setTimesToBtns();
    }

    private void setListeners() {
        searchFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFilterAdapter.getData().setSearchFrom(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCategoriesAdapter.withEventHook(new ClickEventHook<ChipCategoryWithRemoveItem>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ChipCategoryWithRemoveItem.ViewHolder) {
                    return ((ChipCategoryWithRemoveItem.ViewHolder) viewHolder).itemView
                            .findViewById(R.id.img_delete);
                }
                return null;
            }

            @Override
            public void onClick(@NonNull View v, int position, @NonNull FastAdapter<ChipCategoryWithRemoveItem> fastAdapter, @NonNull ChipCategoryWithRemoveItem item) {
                mCategoriesAdapter.remove(position);

                mFilterAdapter.getData().categories.remove(item.getCategoryText());
                //filterAdapter.getData().categories.set(item.getCategoryType(), null);
            }
        });
    }

    @OnClick(R.id.btn_choose_categories)
    protected void chooseCategories() {
        startActivityForResult(new Intent(getActivity(), MultiChooseCategoriesActivity.class)
                        .putExtra(SELECTED_ITEMS, (ArrayList<String>) mFilterAdapter.getData().categories),
                REQUEST_CODE_CHOOSE_CATEGORIES);

    }

    @OnClick(R.id.btn_time_min)
    protected void chooseMinTime() {
        showTimerPicker(POS_MIN);
    }

    @OnClick(R.id.btn_time_max)
    protected void chooseMaxTime() {
        showTimerPicker(POS_MAX);
    }

    @OnClick(R.id.btn_apply)
    protected void apply() {
        Intent intent = new Intent();
        intent.putExtra(FILTER_DATA, mFilterAdapter.getData());

        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_CODE_FILTER_APPLY, intent);
        dismiss();
    }

    @OnClick(R.id.btn_cancel)
    protected void cancel() {
        dismiss();
    }

    private void showTimerPicker(int position) {
        /*Bundle bundle = new Bundle();
        bundle.putInt(DialogTimePicker.SELECTED_ITEM, position);*/

        DialogTimePicker.TimeLimits limits = new DialogTimePicker.TimeLimits();

        int minTime = mFilterAdapter.getData().minTime;
        int maxTime = mFilterAdapter.getData().maxTime;

        int defTime = NOT_SELECTED;

        if (minTime > 0 || maxTime > 0) {
            switch (position) {
                case POS_MIN:
                    limits.maxTime = maxTime;
                    defTime = minTime;
                    break;
                case POS_MAX:
                    defTime = maxTime;
                    limits.minTime = minTime;
                    break;
            }
        }

        mTimeDialog.setArguments(DialogTimePicker.getArgs(position, defTime, limits));
        mTimeDialog.show(getFragmentManager(), "mTimeDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE_CATEGORIES && resultCode == RESULT_OK) {
            mFilterAdapter.getData().categories = data.getStringArrayListExtra(SELECTED_ITEMS);

            setCategoriesToRv();
        }
        if (requestCode == REQUEST_CODE_TIME_PECKER &&
                (resultCode == DialogTimePicker.RESULT_CODE_TIME_SELECTED
                        || resultCode == DialogTimePicker.RESULT_CODE_TIME_CLEAR)) {

            final int time = data.getIntExtra(DialogTimePicker.SELECTED_TIME, NOT_SELECTED);
            final int btn = data.getIntExtra(DialogTimePicker.SELECTED_ITEM, -404);

            setTimeToFilterData(btn, time);

            setTimeToBtn(btn, time);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setCategoriesToRv() {
        mCategoriesAdapter.clear();

        for (int i = 0; i < mFilterAdapter.getData().categories.size(); i++) {
            String category = mFilterAdapter.getData().categories.get(i);

            if (category != null)
                mCategoriesAdapter.add(new ChipCategoryWithRemoveItem(category, i,
                        ChipCategoryWithRemoveItem.SMALL_SIZE));
        }
    }

    private void setTimeToFilterData(int pos, int time) {
        switch (pos) {
            case POS_MIN:
                mFilterAdapter.getData().minTime = time;
                break;
            case POS_MAX:
                mFilterAdapter.getData().maxTime = time;
                break;
        }
    }

    private void setTimeToBtn(int pos, int time) {
        switch (pos) {
            case POS_MIN:
                timeMinBtn.setText(time == NOT_SELECTED ? "Min" : DateTimeHelper.convertTime(time));
                break;
            case POS_MAX:
                timeMaxBtn.setText(time == NOT_SELECTED ? "Max" : DateTimeHelper.convertTime(time));
                break;
        }
    }

    private void setTimesToBtns() {
        if (mFilterAdapter.getData().minTime != NOT_SELECTED)
            timeMinBtn.setText(DateTimeHelper.convertTime(mFilterAdapter.getData().minTime));
        else timeMinBtn.setText("Min");

        if (mFilterAdapter.getData().maxTime != NOT_SELECTED)
            timeMaxBtn.setText(DateTimeHelper.convertTime(mFilterAdapter.getData().maxTime));
        else timeMaxBtn.setText("Max");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(KEY_FILTER_DATA, mFilterAdapter.getData());

        super.onSaveInstanceState(outState);
    }
}
