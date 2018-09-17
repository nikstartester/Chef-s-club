package com.example.nikis.bludogramfirebase.Work;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nikis.bludogramfirebase.R;

public class StepsViewRecipeFragment extends Fragment {

    public StepsViewRecipeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_steps_view_recipe, container, false);
    }

}
