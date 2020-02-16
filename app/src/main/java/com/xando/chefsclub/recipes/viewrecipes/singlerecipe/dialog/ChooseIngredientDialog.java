package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xando.chefsclub.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChooseIngredientDialog extends AppCompatDialogFragment implements View.OnClickListener {

    public static final String KEY_POSITION = "keyPosition";
    public static final String KEY_IS_CHECKED = "keyIsChecked";
    public static final String KEY_IS_CHECKED_CHANGED = "keyChanged";
    public static final int RESULT_CODE_OK = 76;

    @BindView(R.id.btn_goToShoppingList)
    protected Button goToCart;

    @BindView(R.id.btn_addToShoppingList)
    protected Button addToCart;

    @BindView(R.id.btn_deleteFromShoppingList)
    protected Button deleteFromCart;

    private int mPosition;

    private boolean isChecked;

    public static Bundle getArg(int position, boolean isChecked) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        bundle.putBoolean(KEY_IS_CHECKED, isChecked);

        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPosition = getArguments().getInt(KEY_POSITION);
            isChecked = getArguments().getBoolean(KEY_IS_CHECKED);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_ingredient, container, false);

        ButterKnife.bind(this, view);

        if (isChecked) {
            addToCart.setVisibility(View.GONE);
            deleteFromCart.setVisibility(View.VISIBLE);
        } else {
            addToCart.setVisibility(View.VISIBLE);
            deleteFromCart.setVisibility(View.GONE);
        }

        goToCart.setOnClickListener(this);
        addToCart.setOnClickListener(this);
        deleteFromCart.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        boolean isCheckChange = false;
        switch (v.getId()) {
            case R.id.btn_goToShoppingList:
                break;
            case R.id.btn_addToShoppingList:
                isChecked = true;
                isCheckChange = true;
                break;
            case R.id.btn_deleteFromShoppingList:
                isChecked = false;
                isCheckChange = true;
                break;
        }

        Intent intent = new Intent();

        intent.putExtra(KEY_POSITION, mPosition);
        intent.putExtra(KEY_IS_CHECKED, isChecked);
        intent.putExtra(KEY_IS_CHECKED_CHANGED, isCheckChange);

        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_CODE_OK, intent);
        dismiss();
    }
}
