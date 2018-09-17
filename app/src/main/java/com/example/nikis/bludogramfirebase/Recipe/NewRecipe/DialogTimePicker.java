package com.example.nikis.bludogramfirebase.Recipe.NewRecipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.nikis.bludogramfirebase.R;


public class DialogTimePicker extends DialogFragment implements View.OnClickListener {
    public static final String NOT_SELECTED = "notSelected";
    public static final String SELECTED_TIME = "selectedTime";
    public static final String SELECTED_ITEM = "selectedItem";
    public static final int RESULT_CODE_TIME_SELECTED = 90;

    private NumberPicker numberPickerHours;
    private NumberPicker numberPickerMinutes;

    public static DialogFragment newInstance(final int selectedItem) {
        DialogFragment f = new DialogTimePicker();
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_ITEM, selectedItem);
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_time_picker, container);

        v.findViewById(R.id.btn_timeDialog_cancel).setOnClickListener(this);
        v.findViewById(R.id.btn_timeDialog_accept).setOnClickListener(this);

        numberPickerHours = v.findViewById(R.id.numberPicker_hours);
        numberPickerMinutes = v.findViewById(R.id.numberPicker_minutes);
        initTimers();

        if (savedInstanceState != null) {
            setValueOfTimer(savedInstanceState.getIntArray("times"));
        }else setStandardValueOfTimer();


        return v;
    }

    private void initTimers(){
        numberPickerHours.setMaxValue(48);
        numberPickerHours.setMinValue(0);
        numberPickerMinutes.setMaxValue(60);
        numberPickerMinutes.setMinValue(0);

    }
    private void setStandardValueOfTimer(){
        setValueOfTimer(new int[]{0, 15});
    }

    private void setValueOfTimer(int[] times){
        numberPickerHours.setValue(times[0]);
        numberPickerMinutes.setValue(times[1]);
    }

    @Override
    public void onClick(View v) {
        String time = NOT_SELECTED;
        final int selectedItem = getArguments().getInt(SELECTED_ITEM);
        final Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btn_timeDialog_accept:
                time = determinateTime();
                break;
            case R.id.btn_timeDialog_cancel:
                break;
        }

        intent.putExtra(SELECTED_TIME, time);
        intent.putExtra(SELECTED_ITEM, selectedItem);
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_CODE_TIME_SELECTED, intent);
        dismiss();
    }


    private String determinateTime() {
        int hours = numberPickerHours.getValue();
        int minutes = numberPickerMinutes.getValue();
        String time = "";
        if(hours != 0){
            time = hours + "h ";
        }
        if(minutes != 0){
            time += minutes + "m";
        }
        return time;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("times", getTimes());
    }

    private int[] getTimes(){
        final int[] times = new int[2];
        times[0] = numberPickerHours.getValue();
        times[1] = numberPickerMinutes.getValue();
        return times;
    }
}
