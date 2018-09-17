package com.example.nikis.bludogramfirebase.Recipe.NewRecipe.RecyclerViewItems;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.nikis.bludogramfirebase.R;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;


public class IngredientsAddItem extends AbstractItem<IngredientsAddItem, IngredientsAddItem.ViewHolder> {
    private EditText edtIngredient;
    private String ingredient;
    private boolean isFocusOnBind;

    public IngredientsAddItem() {
        this(false);
    }

    public IngredientsAddItem(boolean isFocusOnBind) {
        this("", isFocusOnBind);
    }

    public IngredientsAddItem(String ingredient){
        this(ingredient, false);
    }

    private IngredientsAddItem(String ingredient, boolean isFocusOnBind) {
        this.ingredient = ingredient;
        this.isFocusOnBind = isFocusOnBind;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
    public String getIngredient(){
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
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        edtIngredient = holder.edt_ingredient;

        holder.edt_ingredient.setText(ingredient);

        if(isFocusOnBind)
            holder.edt_ingredient.requestFocus();
    }

    public void setFocus(){
        edtIngredient.requestFocus();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText edt_ingredient;
        ImageButton btnDelete;
        public ViewHolder(View itemView) {
            super(itemView);
            edt_ingredient = itemView.findViewById(R.id.inputIngredient);
            btnDelete = itemView.findViewById(R.id.btn_delete_ingredient_from_rv);
        }

    }
}
