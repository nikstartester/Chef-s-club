package com.xando.chefsclub.Search.Recipes.Item;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.xando.chefsclub.Helpers.DateTimeHelper;
import com.xando.chefsclub.Helpers.FirebaseHelper;
import com.xando.chefsclub.Images.ImageData.ImageData;
import com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.Data.RecipeData;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.RecyclerViewItems.ChipCategoryItem;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.ViewRecipeActivity;
import com.xando.chefsclub.Search.Core.IData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader.getDim;

public class SearchRecipeItem extends AbstractItem<SearchRecipeItem, SearchRecipeItem.ViewHolder>
        implements IData<RecipeData> {

    private static final String TAG = "SearchRecipeItem";

    private RecipeData mRecipeData;

    private final FastItemAdapter<ChipCategoryItem> mCategoriesAdapter;

    private TextView starCount;

    private ImageButton imageFavorite;

    public SearchRecipeItem(RecipeData recipeData) {
        mRecipeData = recipeData;
        mCategoriesAdapter = new FastItemAdapter<>();
    }

    public SearchRecipeItem setRecipeData(RecipeData recipeData) {
        mRecipeData = recipeData;

        return this;
    }

    public RecipeData getRecipeData() {
        return mRecipeData;
    }

    public SearchRecipeItem updateFavoriteImage() {
        if (mRecipeData.stars.containsKey(FirebaseHelper.getUid())) {
            imageFavorite.setImageResource(R.drawable.ic_star_blue_24dp);
        } else {
            imageFavorite.setImageResource(R.drawable.ic_star_border_blue_24dp);
        }

        return this;
    }

    public SearchRecipeItem updateStarCount() {
        starCount.setText(String.valueOf(mRecipeData.starCount));

        return this;
    }

    @Override
    public RecipeData getData() {
        return mRecipeData;
    }

    @Override
    public void setData(RecipeData data) {
        mRecipeData = data;
    }

    @Override
    public void bindView(@NonNull ViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);

        imageFavorite = holder.starButton;
        starCount = holder.starCount;

        holder.name.setText(mRecipeData.overviewData.name);
        holder.starCount.setText(String.valueOf(mRecipeData.starCount));
        holder.creatingTime.setText(DateTimeHelper.transform(mRecipeData.dateTime));

        int time = mRecipeData.stepsData.timeMainNum;
        if (time <= 0) {
            holder.imageTime.setVisibility(View.INVISIBLE);
            holder.time.setVisibility(View.INVISIBLE);
        } else {
            holder.time.setText(DateTimeHelper.convertTime(mRecipeData.stepsData.timeMainNum));
            holder.imageTime.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
        }

        updateFavoriteImage();

        Context context = holder.name.getContext();

        mCategoriesAdapter.clear();

        RecyclerView categories = holder.categories;

        categories.setLayoutManager(ChipsLayoutManager
                .newBuilder(context)
                .build());

        categories.setHasFixedSize(false);
        categories.setAdapter(mCategoriesAdapter);

        for (String category : mRecipeData.overviewData.strCategories) {
            if (category != null && !category.isEmpty())
                mCategoriesAdapter.add(new ChipCategoryItem(category,
                        ChipCategoryItem.SMALL_SIZE_WITHOUT_BORDER));
        }

        //Fix clicks bug
        mCategoriesAdapter.withOnClickListener((v, adapter, item, position) -> {
            holder.itemView.callOnClick();
            return true;
        });

        if (mRecipeData.overviewData.mainImagePath != null) {
            ImageData imageData = new ImageData(mRecipeData.overviewData.mainImagePath, mRecipeData.dateTime);

            GlideImageLoader.getInstance()
                    .loadRoundedImage(context,
                            holder.image,
                            imageData,
                            getDim(holder.image.getContext(), R.dimen.image_corner_radius));
        } else holder.image.setImageDrawable(null);
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.search_recipe_item;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_search_recipe_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView image;

        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.categories)
        RecyclerView categories;

        @BindView(R.id.imageBtn_star)
        ImageButton starButton;

        @BindView(R.id.star_count)
        TextView starCount;

        @BindView(R.id.time)
        TextView time;

        @BindView(R.id.image_time)
        ImageView imageTime;

        @BindView(R.id.date_time)
        TextView creatingTime;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
