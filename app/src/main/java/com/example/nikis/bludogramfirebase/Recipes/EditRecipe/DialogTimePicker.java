package com.example.nikis.bludogramfirebase.Recipes.EditRecipe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.nikis.bludogramfirebase.R;


public class DialogTimePicker extends AppCompatDialogFragment implements View.OnClickListener {
    private static final String TAG = "DialogTimePicker";
    public static final int NOT_SELECTED = -1;
    public static final String SELECTED_TIME = "selectedTime";
    public static final String SELECTED_ITEM = "selectedItem";
    private static final String TIME_LIMITS = "timeLimits";
    public static final int RESULT_CODE_TIME_SELECTED = 90;
    private static final int RESULT_CODE_TIME_CANCELED = 91;
    public static final int RESULT_CODE_TIME_CLEAR = 92;
    private static final String DEF_TIME = "defTime";

    @IntDef({ITEM_MIN, ITEM_MAX})
    @interface Items {
    }

    private static final int ITEM_MIN = 0;
    private static final int ITEM_MAX = 1;

    private static final int DEF_MAX_HOURS = 72, DEF_MAX_MINUTES = 60;
    private static final int DEF_MIN_HOURS = 0, DEF_MIN_MINUTES = 0;

    private NumberPicker numberPickerHours;
    private NumberPicker numberPickerMinutes;

    private TimeLimits mLimits;

    public static DialogFragment newInstance(final int selectedItem) {
        DialogFragment f = new DialogTimePicker();
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_ITEM, selectedItem);
        f.setArguments(bundle);
        return f;
    }

    /*
    Use it if you have 2 items: min and max (times)
     */
    public static Bundle getArgs(@Items int selectedItem, int defTime, TimeLimits limits) {
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_ITEM, selectedItem);
        bundle.putInt(DEF_TIME, defTime);
        bundle.putParcelable(TIME_LIMITS, limits);

        return bundle;
    }

    public static Bundle getArgs(int selectedItem) {
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_ITEM, selectedItem);

        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mLimits = getArguments().getParcelable(TIME_LIMITS);
            int position = getArguments().getInt(SELECTED_ITEM, -1);

            if (mLimits == null) mLimits = new TimeLimits();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_time_picker, container, false);

        v.findViewById(R.id.btn_timeDialog_cancel).setOnClickListener(this);
        v.findViewById(R.id.btn_timeDialog_accept).setOnClickListener(this);
        v.findViewById(R.id.btn_timeDialog_clear).setOnClickListener(this);

        numberPickerHours = v.findViewById(R.id.numberPicker_hours);
        numberPickerMinutes = v.findViewById(R.id.numberPicker_minutes);

        initTimers();

        if (savedInstanceState != null) {
            setValueOfTimer(savedInstanceState.getIntArray("times"));
        } else setStandardValueOfTimer();


        return v;
    }

    private void initTimers() {
        numberPickerHours.setMaxValue(getMaxHours());
        numberPickerHours.setMinValue(getMinHours());

        numberPickerMinutes.setMaxValue(getMaxMinutes());
        numberPickerMinutes.setMinValue(getMinMinutes());

        numberPickerHours.setOnValueChangedListener((picker, oldVal, newVal) -> updateMinutesLimit(newVal));

    }

    private int getMinHours() {
        return changeHourIfSelected(DEF_MIN_HOURS, mLimits.minTime);
    }

    private int getMinMinutes() {
        return changeMinutesIfSelected(DEF_MIN_MINUTES, mLimits.minTime);
    }

    private int getMaxHours() {
        return changeHourIfSelected(DEF_MAX_HOURS, mLimits.maxTime);
    }

    private int getMaxMinutes() {
        return changeMinutesIfSelected(DEF_MAX_MINUTES, mLimits.maxTime);
    }

    private int changeHourIfSelected(int defTime, int toTime) {
        int t = defTime;

        if (toTime != NOT_SELECTED && toTime > 0)
            t = getHours(toTime);

        return t;
    }

    private int changeMinutesIfSelected(int defTime, int toTime) {
        int t = defTime;

        if (toTime != NOT_SELECTED && toTime > 0)
            t = getMinutes(toTime);

        return t;
    }

    private int getHours(int time) {
        return time / 60;
    }

    private int getMinutes(int time) {
        return time % 60;
    }

    private void updateMinutesLimit(int newVal) {
        if (mLimits.minTime != NOT_SELECTED) {
            if (newVal > getHours(mLimits.minTime)) {
                numberPickerMinutes.setMinValue(DEF_MIN_MINUTES);

            } else {
                numberPickerMinutes.setMinValue(getMinutes(mLimits.minTime));
            }
        }
        if (mLimits.maxTime != NOT_SELECTED) {
            if (newVal < getHours(mLimits.maxTime)) {
                numberPickerMinutes.setMaxValue(DEF_MAX_MINUTES);
            } else {
                numberPickerMinutes.setMaxValue(getMinutes(mLimits.maxTime));
            }
        }
        //Log.d(TAG, "onValueChange: "+newVal);
    }

    private void setStandardValueOfTimer() {
        if (getArguments() != null) {
            int defTime = getArguments().getInt(DEF_TIME, -1);

            if (defTime != -1) {
                setHoursAndMinutesLimit(getHours(defTime));
                numberPickerMinutes.setValue(getMinutes(defTime));

                return;
            }
        }

        if (mLimits.minTime != NOT_SELECTED) {
            setHoursAndMinutesLimit(getHours(mLimits.minTime));
            numberPickerMinutes.setValue(getMinutes(mLimits.minTime));
        } else if (mLimits.maxTime != NOT_SELECTED) {
            setHoursAndMinutesLimit(getHours(mLimits.maxTime));
            numberPickerMinutes.setValue(getMinutes(mLimits.maxTime));
        } else setDefTime();
    }

    private void setHoursAndMinutesLimit(int hours) {
        numberPickerHours.setValue(hours);

        updateMinutesLimit(hours);
    }

    private void setDefTime() {
        setHoursAndMinutesLimit(0);
        numberPickerMinutes.setValue(15);
    }

    private void setValueOfTimer(int[] times) {
        setHoursAndMinutesLimit(times[0]);
        numberPickerMinutes.setValue(times[1]);
    }

    @Override
    public void onClick(View v) {
        int time = NOT_SELECTED;
        int selectedItem = NOT_SELECTED;

        if (getArguments() != null) {
            selectedItem = getArguments().getInt(SELECTED_ITEM);
        }

        final Intent intent = new Intent();

        int resultCode = RESULT_CODE_TIME_SELECTED;

        switch (v.getId()) {
            case R.id.btn_timeDialog_accept:
                time = determinateTimeInt();
                break;
            case R.id.btn_timeDialog_cancel:
                resultCode = RESULT_CODE_TIME_CANCELED;
                break;
            case R.id.btn_timeDialog_clear:
                resultCode = RESULT_CODE_TIME_CLEAR;
                break;
        }

        intent.putExtra(SELECTED_TIME, time);
        intent.putExtra(SELECTED_ITEM, selectedItem);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        dismiss();
    }


    private String determinateTime() {
        int hours = numberPickerHours.getValue();
        int minutes = numberPickerMinutes.getValue();
        String time = "";
        if (hours != 0) {
            time = hours + "h ";
        }
        if (minutes != 0) {
            time += minutes + "m";
        }
        return time;
    }

    private int determinateTimeInt() {
        int hours = numberPickerHours.getValue();
        int minutes = numberPickerMinutes.getValue();

        int time = hours * 60 + minutes;
        return time != 0 ? time : NOT_SELECTED;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("times", getTimes());
    }

    private int[] getTimes() {
        final int[] times = new int[2];
        times[0] = numberPickerHours.getValue();
        times[1] = numberPickerMinutes.getValue();
        return times;
    }

    public static final class TimeLimits implements Parcelable {
        public int minTime = NOT_SELECTED;
        public int maxTime = NOT_SELECTED;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.minTime);
            dest.writeInt(this.maxTime);
        }

        public TimeLimits(int minTime, int maxTime) {
            this.minTime = minTime;
            this.maxTime = maxTime;
        }

        public TimeLimits() {
        }

        TimeLimits(Parcel in) {
            this.minTime = in.readInt();
            this.maxTime = in.readInt();
        }

        public static final Parcelable.Creator<TimeLimits> CREATOR = new Parcelable.Creator<TimeLimits>() {
            @Override
            public TimeLimits createFromParcel(Parcel source) {
                return new TimeLimits(source);
            }

            @Override
            public TimeLimits[] newArray(int size) {
                return new TimeLimits[size];
            }
        };
    }
}
