package com.example.nikis.bludogramfirebase.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nikis.bludogramfirebase.App;
import com.example.nikis.bludogramfirebase.Constants.Constants;
import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.Recipes.db.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;


public class SettingsLoacalFragment extends Fragment {

    @BindView(R.id.sw_saving_viewed)
    protected SwitchCompat savingViewedSwich;

    @BindView(R.id.sw_saving_new)
    protected SwitchCompat savingNewSwich;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_local_cookbook, container, false);

        ButterKnife.bind(this, view);

        initSwitches();

        return view;
    }

    private void initSwitches() {
        savingViewedSwich.setChecked(isSavingViewedRecipes(getActivity()));

        savingNewSwich.setChecked(isSavingNewRecipes(getActivity()));

        savingViewedSwich.setOnCheckedChangeListener((buttonView, isChecked) -> {
            putBooleanSettings(Constants.Settings.SAVING_LOCAL_VIEWED_RECIPES, isChecked);
        });
        savingNewSwich.setOnCheckedChangeListener((buttonView, isChecked) -> {
            putBooleanSettings(Constants.Settings.SAVING_LOCAL_NEW_RECIPES, isChecked);
        });
    }

    public static boolean isSavingViewedRecipes(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(Constants.Settings.APP_PREFERENCES, MODE_PRIVATE);

        return preferences.getBoolean(Constants.Settings.SAVING_LOCAL_VIEWED_RECIPES,
                Constants.Settings.DEF_SAVING_LOCAL_VIEWED_RECIPES);
    }

    public static boolean isSavingNewRecipes(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(Constants.Settings.APP_PREFERENCES, MODE_PRIVATE);

        return preferences.getBoolean(Constants.Settings.SAVING_LOCAL_NEW_RECIPES,
                Constants.Settings.DEF_SAVING_LOCAL_NEW_RECIPES);
    }

    private void putBooleanSettings(String key, boolean param) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                Constants.Settings.APP_PREFERENCES, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(key, param);

        editor.apply();
    }

    @OnClick(R.id.btn_remove_all)
    protected void onRemoveClick() {
        Helper.deleteAll((App) getActivity().getApplication());

        Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();
    }
}
