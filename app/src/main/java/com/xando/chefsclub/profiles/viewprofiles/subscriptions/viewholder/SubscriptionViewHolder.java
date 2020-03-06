package com.xando.chefsclub.profiles.viewprofiles.subscriptions.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.xando.chefsclub.R;
import com.xando.chefsclub.helper.FirebaseHelper;
import com.xando.chefsclub.helper.NetworkHelper;
import com.xando.chefsclub.image.data.ImageData;
import com.xando.chefsclub.image.loaders.GlideImageLoader;
import com.xando.chefsclub.profiles.data.ProfileData;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xando.chefsclub.helper.FirebaseHelper.getUid;


public class SubscriptionViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "SubscriptionViewHolder";

    @BindView(R.id.profile_image)
    protected ImageView profileImage;

    @BindView(R.id.profile_login)
    protected TextView profileLogin;

    @BindView(R.id.profile_name)
    protected TextView profileName;

    @BindView(R.id.subscribers_count)
    protected TextView subscribersCount;

    @BindView(R.id.btn_subscibe)
    protected Button unsubscribe;

    private ProfileData mProfileData;

    public SubscriptionViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void bind(ProfileData data) {
        mProfileData = data;

        profileName.setText(mProfileData.firstName + " " + mProfileData.secondName);

        profileLogin.setText(mProfileData.login);

        subscribersCount.setText(String.valueOf(mProfileData.subscribersCount));

        setVisibilityForUnsubscrBtn(data);

        unsubscribe.setOnClickListener(v -> unsubscribeClick());

        setImage();
    }

    private void setVisibilityForUnsubscrBtn(ProfileData data) {
        if (data.userUid.equals(getUid()) || data.subscribers == null || !data.subscribers.containsKey(getUid())
                || !data.subscribers.get(getUid())) unsubscribe.setVisibility(View.GONE);
        else unsubscribe.setVisibility(View.VISIBLE);
    }

    private void unsubscribeClick() {
        if (NetworkHelper.isConnected(unsubscribe.getContext())) {
            FirebaseHelper.Subscriptions.updateSubscr(getUid(), mProfileData.userUid);

            unsubscribe.setVisibility(View.INVISIBLE);

        } else Toast.makeText(unsubscribe.getContext(),
                unsubscribe.getContext().getString(R.string.network_error), Toast.LENGTH_SHORT)
                .show();
    }

    private void setImage() {
        if (mProfileData.imageURL != null) {
            ImageData imageData = new ImageData(mProfileData.imageURL, mProfileData.lastTimeUpdate);

            GlideImageLoader.getInstance().loadSmallCircularImage(profileImage.getContext(),
                    profileImage,
                    imageData);
        } else profileImage.setImageResource(R.drawable.ic_account_circle_elements_48dp);
    }
}
