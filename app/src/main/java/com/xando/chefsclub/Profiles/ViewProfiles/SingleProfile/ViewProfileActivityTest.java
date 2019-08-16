package com.xando.chefsclub.Profiles.ViewProfiles.SingleProfile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.xando.chefsclub.DataWorkers.OnItemCountChanged;
import com.xando.chefsclub.R;
import com.xando.chefsclub.SingleFragmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ViewProfileActivityTest extends SingleFragmentActivity implements OnItemCountChanged {
    private final static String EXTRA_USER_ID = "userId";
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    private String userid;

    public static Intent getIntent(Context context, String userId) {
        Intent intent = new Intent(context, ViewProfileActivityTest.class);
        intent.putExtra(EXTRA_USER_ID, userId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected Fragment createFragment() {
        return ViewProfileFragment.getInstance(getIntent().getStringExtra(EXTRA_USER_ID));
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_fragment;
    }

    @Override
    public void onItemCountChanged(int itemCount) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof OnItemCountChanged) {
                ((OnItemCountChanged) fragment).onItemCountChanged(itemCount);
            }
        }
    }
}
