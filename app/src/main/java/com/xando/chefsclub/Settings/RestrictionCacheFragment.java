package com.xando.chefsclub.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xando.chefsclub.Constants.Constants;
import com.xando.chefsclub.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RestrictionCacheFragment extends BottomSheetDialogFragment {

    private static final String KEY_VALUES = "VALUES";
    private static final String KEY_CURR_INDEX = "CURR_INDEX";

    @BindView(R.id.seekBar)
    protected SeekBar seekBar;
    @BindView(R.id.tv_max_cache_size)
    protected TextView maxCacheSize;

    private int mCurrMaxCacheSize;
    private int mStartIndex;
    private int[] mValues;

    public static Bundle getArguments(int[] values, int currIndex) {
        if (currIndex < 0 || currIndex > values.length - 1)
            throw new ArrayIndexOutOfBoundsException();

        Bundle bundle = new Bundle();
        bundle.putIntArray(KEY_VALUES, values);
        bundle.putInt(KEY_CURR_INDEX, currIndex);

        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mValues = getArguments().getIntArray(KEY_VALUES);
            mStartIndex = getArguments().getInt(KEY_CURR_INDEX, 0);

            mCurrMaxCacheSize = mValues[mStartIndex];
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restriction_cache, container, false);

        ButterKnife.bind(this, view);

        seekBar.setProgress(mStartIndex);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCurrMaxCacheSize = mValues[progress];

                maxCacheSize.setText(getStringSize(mCurrMaxCacheSize));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                maxCacheSize.setText(getStringSize(mCurrMaxCacheSize));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                maxCacheSize.setText("Max size");
            }
        });

        return view;
    }

    private String getStringSize(int size) {
        if (size == Constants.Settings.INFINITY) {
            return "∞ TB";
        } else return size + " MB";
    }

    @OnClick(R.id.btn_save)
    protected void save() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.Settings.APP_PREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(Constants.Settings.MAX_IMAGE_CACHE_SIZE, mCurrMaxCacheSize);

        editor.apply();

        Toast.makeText(getActivity(), "Changes will take effect after the restart", Toast.LENGTH_SHORT).show();

        dismiss();
    }

    @OnClick(R.id.btn_cancel)
    protected void cancel() {
        dismiss();
    }
}
