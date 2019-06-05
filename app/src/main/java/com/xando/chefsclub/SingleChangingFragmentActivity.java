package com.xando.chefsclub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public abstract class SingleChangingFragmentActivity extends SingleFragmentActivity {
    private static final String KEY_TITLE = "TITLE";
    private static final String KEY_START_TITLE = "START_TITLE";

    private String mTitle, mStartTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(KEY_TITLE);
            mStartTitle = savedInstanceState.getString(KEY_START_TITLE);
        }
    }

    @Override
    protected Fragment createFragment() {
        return null;
    }

    public String getCurrentTitle() {
        return mTitle;
    }

    protected void setStartFragment(@NonNull Fragment fragment, String title) {
        changeFragment(fragment, title, false);

        mStartTitle = title;

        setTitle(title);
    }

    protected void changeFragment(@NonNull Fragment fragment, String title) {
        changeFragment(fragment, title, true);

        setTitle(title);
    }


    private void changeFragment(@NonNull Fragment fragment, String title, boolean isAddToBackStack) {
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();

        ftrans.replace(SingleFragmentActivity.CONTAINER_ID, fragment, title);

        if (isAddToBackStack)
            ftrans.addToBackStack(title);

        ftrans.commit();
    }

    private void setTitle(String title) {
        if (getSupportActionBar() != null) {
            mTitle = title;
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String lastTitle = getLastTitle();

        if (lastTitle != null)
            setTitle(lastTitle);
        else setTitle(mStartTitle);
    }

    /*@Nullable
    private Fragment getLastFragment(int minusOfEndIndex) {
        int index = getSupportFragmentManager().getBackStackEntryCount() - minusOfEndIndex;
        if (index >= 0) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
            String tag = backEntry.getName();

            return getSupportFragmentManager().findFragmentByTag(tag);
        } else return null;
    }*/

    @Nullable
    private String getLastTitle() {
        int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
        if (index >= 0) {
            FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager()
                    .getBackStackEntryAt(index);

            return backStackEntry.getName();
        } else return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_TITLE, mTitle);
        outState.putString(KEY_START_TITLE, mStartTitle);

        super.onSaveInstanceState(outState);
    }
}
