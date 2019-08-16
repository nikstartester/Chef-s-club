package com.xando.chefsclub.Recipes.EditRecipe.RequiredFields;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.EditRecipe.RecyclerViewItems.RequiredFieldItem;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RequiredFieldsDialogFragment extends AppCompatDialogFragment {

    private static final String KEY_DATA = "DATA";

    private RequiredFieldsData mData;

    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;

    protected FastItemAdapter<RequiredFieldItem> adapter;

    public static DialogFragment getInstance(RequiredFieldsData data) {
        DialogFragment dialogFragment = new RequiredFieldsDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DATA, data);

        dialogFragment.setArguments(bundle);


        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogFragmentStyle);

        adapter = new FastItemAdapter<>();

        if (getArguments() != null) {
            mData = getArguments().getParcelable(KEY_DATA);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        String tittle = "Required fields: ";

        dialog.setTitle(tittle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_required_fields, container, false);

        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        showRequiredFields();

        return view;
    }

    protected void showRequiredFields() {
        adapter.clear();
        for (Map.Entry<String, Boolean> entry : mData.fields.entrySet()) {
            adapter.add(new RequiredFieldItem(entry.getKey(), entry.getValue()));
        }
    }

    @OnClick(R.id.btn_continue)
    protected void continueClick() {
        dismiss();
    }
}
