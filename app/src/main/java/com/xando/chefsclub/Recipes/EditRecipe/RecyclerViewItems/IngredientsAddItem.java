package com.xando.chefsclub.Recipes.EditRecipe.RecyclerViewItems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.R;

import java.util.List;


public class IngredientsAddItem extends AbstractItem<IngredientsAddItem,
        IngredientsAddItem.ViewHolder> {

    private EditText edtIngredient;
    private final String ingredient;
    private final boolean isFocusOnBind;

    public IngredientsAddItem() {
        this(false);
    }

    public IngredientsAddItem(boolean isFocusOnBind) {
        this("", isFocusOnBind);
    }

    public IngredientsAddItem(String ingredient) {
        this(ingredient, false);
    }

    private IngredientsAddItem(String ingredient, boolean isFocusOnBind) {
        this.ingredient = ingredient;
        this.isFocusOnBind = isFocusOnBind;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    public String getIngredient() {
        return edtIngredient.getText().toString();
    }

    @Override
    public int getType() {
        return R.id.ingredients_item_add;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_ingredients_add_item;
    }

    @Override
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);
        edtIngredient = holder.edt_ingredient;

        holder.edt_ingredient.setText(ingredient);

        if (isFocusOnBind)
            holder.edt_ingredient.requestFocus();
    }

    public void setFocus() {
        edtIngredient.requestFocus();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final EditText edt_ingredient;
        final ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            edt_ingredient = itemView.findViewById(R.id.inputIngredient);
            btnDelete = itemView.findViewById(R.id.btn_delete_ingredient_from_rv);
        }

    }
}
