package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;

import java.util.Locale;
import java.util.Objects;

@JsonClass(generateAdapter = false)
public class GalleryItem {
    @Json(name = "id")
    private String mId = "";

    @Json(name = "owner")
    private String mOwner = "";

    @Json(name = "secret")
    private String mSecret = "";

    @Json(name = "server")
    private String mServerId = "";

    @Json(name = "farm")
    private String mFarm = "";

    @Json(name = "title")
    private String mTitle = "";

    @Json(name = "ispublic")
    private int mIsPublic = 1;

    @Json(name = "isfriend")
    private int mIsFriend = 0;

    @Json(name = "isfamily")
    private int mIsFamily = 0;

    public GalleryItem(String id, String owner, String secret, String serverId, String farm,
                       String title, int isPublic, int isFriend, int isFamily) {
        mId = Objects.requireNonNull(id);
        mOwner = Objects.requireNonNull(owner);
        mSecret = Objects.requireNonNull(secret);
        mServerId = Objects.requireNonNull(serverId);
        mFarm = Objects.requireNonNull(farm);
        mTitle = Objects.requireNonNull(title);
        mIsPublic = isPublic;
        mIsFriend = isFriend;
        mIsFamily = isFamily;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        this.mOwner = owner;
    }

    public String getSecret() {
        return mSecret;
    }

    public void setSecret(String secret) {
        this.mSecret = secret;
    }

    public String getServerId() {
        return mServerId;
    }

    public void setServerId(String serverId) {
        this.mServerId = serverId;
    }

    public String getFarm() {
        return mFarm;
    }

    public void setFarm(String farm) {
        this.mFarm = farm;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public int getIsPublic() {
        return mIsPublic;
    }

    public void setIsPublic(int isPublic) {
        mIsPublic = isPublic;
    }

    public int getIsFriend() {
        return mIsFriend;
    }

    public void setIsFriend(int isFriend) {
        mIsFriend = isFriend;
    }

    public int getIsFamily() {
        return mIsFamily;
    }

    public void setIsFamily(int isFamily) {
        mIsFamily = isFamily;
    }

    public Uri getPhotoUri() {
        return Uri.parse("https://live.staticflickr.com")
                .buildUpon()
                .appendPath(mServerId)
                .appendPath(String.format(Locale.getDefault(), "%s_%s.jpg", mId, mSecret))
                .build();
    }

    public Uri getPhotoPageUri() {
        return Uri.parse("https://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GalleryItem that = (GalleryItem) o;
        return mId.equals(that.mId) && mOwner.equals(that.mOwner) && mSecret.equals(that.mSecret) &&
                mServerId.equals(that.mServerId) && mFarm.equals(that.mFarm) && mTitle.equals(that.mTitle) &&
                mIsPublic == that.mIsPublic && mIsFriend == that.mIsFriend && mIsFamily == that.mIsFamily;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mOwner, mSecret, mServerId, mFarm, mTitle, mIsPublic, mIsFriend, mIsFamily);
    }

    @Override
    @NonNull
    public String toString() {
        return String.format(Locale.getDefault(), "mId = '%s'; mOwner = '%s'; mSecret = '%s'; " +
                "mServer = '%s'; mFarm = '%s'; mTitle = '%s'; mIsPublic = %d; mIsFriend = %d; " +
                "mIsFamily = %d", mId, mOwner, mSecret, mServerId, mFarm, mTitle, mIsPublic, mIsFriend, mIsFamily);
    }
}
