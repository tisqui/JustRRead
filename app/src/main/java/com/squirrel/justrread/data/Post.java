package com.squirrel.justrread.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by squirrel on 4/25/16.
 */
public class Post implements Serializable {

    private String mPostId; //this item's identifier, e.g. "8xwlg"
    private String mName; //Fullname of comment, e.g. "t1_c3v7f8u"
    private int mUpVotes;
    private int mDownVotes;
    private int mLikes;
    private Date mCreated;
    private String mAuthor;
    private String mDomain;
    private boolean mSelfPost;
    private int mNumComments;
    private boolean mNsfw;
    private String mSubreddit;
    private String mSubredditId;
    private String mSelfTextHtml; //the formatted escaped HTML text. this is the HTML formatted version of the marked up text.
    private String mThumbnail;
    private String mTitle;
    private String mUrl;

    public Post(){}
    //TODO add the objects media, media_embed https://github.com/reddit/reddit/wiki/JSON

    public Post(String postId, String name,
                int upVotes, int downVotes,
                int likes, Date created,
                String author, String domain,
                boolean selfPost, int numComments,
                boolean nsfw, String subreddit,
                String subredditId, String selfTextHtml,
                String thumbnail, String title, String url) {
        mPostId = postId;
        mName = name;
        mUpVotes = upVotes;
        mDownVotes = downVotes;
        mLikes = likes;
        mCreated = created;
        mAuthor = author;
        mDomain = domain;
        mSelfPost = selfPost;
        mNumComments = numComments;
        mNsfw = nsfw;
        mSubreddit = subreddit;
        mSubredditId = subredditId;
        mSelfTextHtml = selfTextHtml;
        mThumbnail = thumbnail;
        mTitle = title;
        mUrl = url;
    }

    public String getPosId() {
        return mPostId;
    }

    public void setId(String id) {
        mPostId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getUpVotes() {
        return mUpVotes;
    }

    public void setUpVotes(int upVotes) {
        mUpVotes = upVotes;
    }

    public int getDownVotes() {
        return mDownVotes;
    }

    public void setDownVotes(int downVotes) {
        mDownVotes = downVotes;
    }

    public int isLikes() {
        return mLikes;
    }

    public void setLikes(int likes) {
        mLikes = likes;
    }

    public Date getCreated() {
        return mCreated;
    }

    public void setCreated(Date created) {
        mCreated = created;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getDomain() {
        return mDomain;
    }

    public void setDomain(String domain) {
        mDomain = domain;
    }

    public boolean isSelfPost() {
        return mSelfPost;
    }

    public void setSelfPost(boolean selfPost) {
        mSelfPost = selfPost;
    }

    public int getNumComments() {
        return mNumComments;
    }

    public void setNumComments(int numComments) {
        mNumComments = numComments;
    }

    public boolean isNsfw() {
        return mNsfw;
    }

    public void setNsfw(boolean nsfw) {
        mNsfw = nsfw;
    }

    public String getSubreddit() {
        return mSubreddit;
    }

    public void setSubreddit(String subreddit) {
        mSubreddit = subreddit;
    }

    public String getSubredditId() {
        return mSubredditId;
    }

    public void setSubredditId(String subredditId) {
        mSubredditId = subredditId;
    }

    public String getSelfTextHtml() {
        return mSelfTextHtml;
    }

    public void setSelfTextHtml(String selfTextHtml) {
        mSelfTextHtml = selfTextHtml;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public String toString() {
        return "Post{" +
                "mId='" + mPostId + '\'' +
                ", mName='" + mName + '\'' +
                ", mUpVotes=" + mUpVotes +
                ", mDownVotes=" + mDownVotes +
                ", mLikes=" + mLikes +
                ", mCreated=" + mCreated +
                ", mAuthor='" + mAuthor + '\'' +
                ", mDomain='" + mDomain + '\'' +
                ", mSelfPost=" + mSelfPost +
                ", mNumComments=" + mNumComments +
                ", mNsfw=" + mNsfw +
                ", mSubreddit='" + mSubreddit + '\'' +
                ", mSubredditId='" + mSubredditId + '\'' +
                ", mSelfTextHtml='" + mSelfTextHtml + '\'' +
                ", mThumbnail='" + mThumbnail + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mUrl='" + mUrl + '\'' +
                '}';
    }
}
