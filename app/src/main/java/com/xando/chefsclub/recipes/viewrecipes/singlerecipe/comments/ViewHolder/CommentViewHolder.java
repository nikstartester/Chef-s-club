package com.xando.chefsclub.recipes.viewrecipes.singlerecipe.comments.ViewHolder;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.YoYo;
import com.xando.chefsclub.R;
import com.xando.chefsclub.dataworkers.ParcResourceByParc;
import com.xando.chefsclub.helper.DateTimeHelper;
import com.xando.chefsclub.helper.UiHelper;
import com.xando.chefsclub.image.data.ImageData;
import com.xando.chefsclub.image.loaders.GlideImageLoader;
import com.xando.chefsclub.profiles.data.ProfileData;
import com.xando.chefsclub.profiles.repository.ProfileRepository;
import com.xando.chefsclub.recipes.viewrecipes.singlerecipe.comments.Data.CommentData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xando.chefsclub.profiles.repository.ProfileRepository.CHILD_USERS;


public class CommentViewHolder extends RecyclerView.ViewHolder {

    private final MutableLiveData<ParcResourceByParc<ProfileData>> mProfileData = new MutableLiveData<>();
    private final MutableLiveData<ParcResourceByParc<ProfileData>> mReplyProfileData = new MutableLiveData<>();

    public List<YoYo.YoYoString> anims = new ArrayList<>();

    @BindView(R.id.highlight)
    protected View highlight;

    @BindView(R.id.comment_text)
    public TextView text;

    @BindView(R.id.comment_time)
    protected TextView time;

    @BindView(R.id.comment_pofile_name)
    public TextView profileName;

    @BindView(R.id.comment_profile_image)
    public ImageView profileImage;

    @BindView(R.id.comment_reply)
    public ImageButton reply;

    @BindView(R.id.reply_content)
    public View replyContent;

    @BindView(R.id.reply_comment_text)
    protected TextView replyText;

    @BindView(R.id.reply_comment_pofile_name)
    protected TextView replyProfileName;
    private CommentData commentData;

    public CommentViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void bindToComment(LifecycleOwner lifecycleOwner, CommentData data) {
        highlight.setBackgroundColor(highlight.getContext().getResources().getColor(R.color.transparent));

        profileImage.setImageResource(R.color.white);

        commentData = data;

        text.setText(data.text);

        time.setText(DateTimeHelper.transform(commentData.date));

        loadAuthorData(commentData.authorId, mProfileData);

        reply.setVisibility(View.VISIBLE);

        if (commentData.replyId != null) {
            replyText.setText(commentData.replyText.replace("\n", " "));

            replyContent.setVisibility(View.VISIBLE);

            loadAuthorData(commentData.replyAuthorId, mReplyProfileData);
        } else {
            replyContent.setVisibility(View.GONE);
        }

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

        mReplyProfileData.observe(lifecycleOwner, res -> {
            if (res != null && res.status == ParcResourceByParc.Status.SUCCESS) {
                replyProfileName.setText(res.data.login);
            }
        });
    }

    public void changeVisible(boolean isVisible) {
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        if (isVisible) {
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        } else {
            itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        itemView.setLayoutParams(param);
    }

    private void loadAuthorData(String id, MutableLiveData<ParcResourceByParc<ProfileData>> to) {
        ProfileRepository.with((Application) profileImage.getContext().getApplicationContext())
                .setFirebaseId(id)
                .setFirebaseChild(CHILD_USERS)
                .to(to)
                .build()
                .loadData();

    }

    public void startHighlight() {
        final int color = highlight.getContext().getResources().getColor(R.color.colorAccent_medium_transparent);

        List<YoYo.YoYoString> anims = UiHelper.Other.highlight(highlight, color, new int[]{500, 1000});

        this.anims.addAll(anims);
    }

    public interface OnReplyComment {
        void onReplyComment(CommentData commentData);
    }
}
