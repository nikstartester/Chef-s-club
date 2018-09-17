package com.example.nikis.bludogramfirebase.ViewRecipes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikis.bludogramfirebase.R;
import com.example.nikis.bludogramfirebase.RecipeData.RecipeData;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;
import java.util.Map;


public class RecipeItem extends AbstractItem<RecipeItem,RecipeItem.ViewHolder> {

    private Map<String, Object> recipeData;

    public RecipeItem(@NonNull RecipeData recipeData){
        this.recipeData = recipeData.toMap();
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.recipe_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_recipe_item;
    }

    @Override
    public void bindView(final ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.tvName.setText(recipeData.get("name").toString());
        holder.tvFavoriteCount.setText(recipeData.get("starCount").toString());
    }
    @Override
    public void unbindView(ViewHolder holder){
        holder.tvFavoriteCount.setText("");
        holder.tvName.setText("");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgImage;
        TextView tvName, tvFavoriteCount;
        ImageView imgFavorite;

        public ViewHolder(View itemView) {
            super(itemView);
            imgImage = itemView.findViewById(R.id.img_image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvFavoriteCount = itemView.findViewById(R.id.tv_starCount);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
        }
    }
}
