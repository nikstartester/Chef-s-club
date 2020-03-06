package com.xando.chefsclub;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import io.reactivex.annotations.Nullable;


public abstract class SingleFragmentActivity extends AppCompatActivity {

    public static final int CONTAINER_ID = R.id.fragment_container;
    public static final int LAYOUT_RES = R.layout.activity_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());

        Fragment fragment = createFragment();

        if (savedInstanceState == null && fragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .add(CONTAINER_ID, fragment)
                    .commit();
        }
    }

    @Nullable
    protected abstract Fragment createFragment();

    protected abstract int getLayoutRes();
}
