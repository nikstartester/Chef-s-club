package com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.ViewHolder;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xando.chefsclub.DataWorkers.ParcResourceByParc;
import com.xando.chefsclub.Helpers.DateTimeHelper;
import com.xando.chefsclub.Helpers.FirebaseHelper;
import com.xando.chefsclub.Images.ImageData.ImageData;
import com.xando.chefsclub.Images.ImageLoaders.GlideImageLoader;
import com.xando.chefsclub.Profiles.Data.ProfileData;
import com.xando.chefsclub.Profiles.Repository.ProfileRepository;
import com.xando.chefsclub.R;
import com.xando.chefsclub.Recipes.ViewRecipes.SingleRecipe.Comments.Data.CommentData;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xando.chefsclub.Profiles.Repository.ProfileRepository.CHILD_USERS;


public class CommentViewHolder extends RecyclerView.ViewHolder {
    private final MutableLiveData<ParcResourceByParc<ProfileData>> mProfileData = new MutableLiveData<>();
    public CommentData commentData;
    @BindView(R.id.comment_text)
    protected TextView text;
    @BindView(R.id.comment_time)
    protected TextView time;
    @BindView(R.id.comment_pofile_name)
    protected TextView profileName;
    @BindView(R.id.comment_profile_image)
    protected ImageView profileImage;
    @BindView(R.id.comment_reply)
    protected ImageButton reply;
    private OnReplyComment mOnReplyComment;


    public CommentViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public CommentViewHolder addClickerToAuthorData(View.OnClickListener userLoginClick) {
        profileImage.setOnClickListener(userLoginClick);
        profileName.setOnClickListener(userLoginClick);

        return this;
    }

    public CommentViewHolder addOnReplyComment(OnReplyComment onReplyComment) {
        mOnReplyComment = onReplyComment;

        return this;
    }

    public void bindToComment(LifecycleOwner lifecycleOwner, CommentData data) {
        commentData = data;

        text.setText(data.text);

        time.setText(DateTimeHelper.transform(commentData.date));

        loadAuthorData();

        if (commentData.authorId.equals(FirebaseHelper.getUid()))
            reply.setVisibility(View.GONE);
        else reply.setVisibility(View.VISIBLE);

        reply.setOnClickListener(v -> {
            if (mOnReplyComment != null) mOnReplyComment.onReplyComment(data);
        });

        mProfileData.observe(lifecycleOwner, res -> {
            if (res != null && res.status == ParcResourceByParc.Status.SUCCESS) {

                profileName.setText(res.data.login);

                commentData.authorLogin = res.data.login;

                if (res.data.imageURL != null) {
                    ImageData imageData = new ImageData(res.data.imageURL, res.data.lastTimeUpdate);

                    GlideImageLoader.getInstance().loadSmallCircularImage(profileImage.getContext(),
                            profileImage, imageData);
                } else {
                    profileImage.setImageResource(R.drawable.ic_account_circle_elements_48dp);
                }
            }
        });
    }


    private void loadAuthorData() {
        ProfileRepository.with((Application) profileImage.getContext().getApplicationContext())
                .setFirebaseId(commentData.authorId)
                .setFirebaseChild(CHILD_USERS)
                .to(mProfileData)
                .build()
                .loadData();

    }

    public interface OnReplyComment {
        void onReplyComment(CommentData commentData);
    }
}
