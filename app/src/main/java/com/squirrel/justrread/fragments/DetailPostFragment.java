package com.squirrel.justrread.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.activities.BaseActivity;
import com.squirrel.justrread.activities.Navigator;
import com.squirrel.justrread.adapters.CommentsRecyclerViewAdapter;
import com.squirrel.justrread.api.RedditAPI;
import com.squirrel.justrread.data.Post;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.VoteDirection;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailPostFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG=DetailPostFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private static final int POST_COMMENTS_LOADER = 1;
    private Post mPost;

    @Bind(R.id.detail_fragment_empty)
    TextView mEmptyView;

    @Bind(R.id.detail_comments_list_emply_message)
    TextView mEmptyCommentsView;

    @Bind(R.id.detail_subreddit)
    TextView mDetailSubreddit;

    @Bind(R.id.detail_author)
    TextView mDetailAuthor;

    @Bind(R.id.detail_title)
    TextView mDetailTitle;

    @Bind(R.id.detail_source)
    TextView mDetailSource;

    @Bind(R.id.detail_time)
    TextView mDetailTime;

    @Bind(R.id.detail_thumbnail)
    ImageView mDetailThumb;

    @Bind(R.id.detail_text)
    TextView mDetailText;

    @Bind(R.id.detail_num_comments)
    TextView mDetailNumComments;

    @Bind(R.id.detail_votes_num)
    TextView mDetailVotesNum;

    @Bind(R.id.detail_up_btn)
    Button mUpBtn;

    @Bind(R.id.detail_down_btn)
    Button mDownBtn;

    @Bind(R.id.comments_loading_progress)
    ProgressBar mCommentsProgressbar;

    @Bind(R.id.detail_share_button)
    ImageView mShareButton;

    private RecyclerView mCommentsRecyclerView;
    private LinearLayoutManager mCommentsLinearLayoutManager;
    private CommentsRecyclerViewAdapter mCommentsRecyclerViewAdapter;
    private RedditAPI mCommentsRedditAPI;

    private CommentNode mCommentNode;

    private List<CommentNode> mListOfComments;
    private int mCommentsPerPage = 50;


    public DetailPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail_post, container, false);
        ButterKnife.bind(this, rootView);

        //menu in fragment
        setHasOptionsMenu(true);

        //get the post data from the Intent
        Intent intent = getActivity().getIntent();
        mPost = (Post) intent.getSerializableExtra(BaseActivity.POST_DETAIL_KEY);

        //get the post data from the arguments
        if(mPost == null) {
            Bundle extras = getArguments();
            mPost = (Post) extras.getSerializable(BaseActivity.POST_DETAIL_KEY);

        }

        if(mPost != null){
            mEmptyView.setVisibility(View.GONE);

            //set all the UI elements
            if(mPost.getSubreddit() != null){
                mDetailSubreddit.setText("/" + mPost.getSubreddit());
            }
            mDetailAuthor.setText(mPost.getAuthor());
            mDetailTitle.setText(mPost.getTitle());
            mDetailTime.setText(Utils.getPostedTimeAgo(mPost.getCreated(), getContext()));

            if(mPost.getThumbnail() == null){
                mDetailThumb.setVisibility(View.GONE);
            }else{
                mDetailThumb.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(mPost.getThumbnail())
                        .placeholder(R.drawable.ic_duck_white_36dp)
                        .error(R.drawable.ic_duck_white_36dp)
                        .into(mDetailThumb);
            }

            if(mPost.getSelfTextHtml() == null || mPost.getSelfTextHtml().isEmpty()){
                mDetailText.setVisibility(View.GONE);
            } else{
                mDetailText.setText(Html.fromHtml(Utils.getHtmlFromMarkdown(mPost.getSelfTextHtml())));
            }
            if(mPost.getNumComments() == 0){
                mDetailNumComments.setText(R.string.detail_num_comments_text_no_comments);
            } else{
                mDetailNumComments.setText(mPost.getNumComments() + getContext().getString(R.string.detail_comments_text));
            }

            mDetailVotesNum.setText(mPost.getUpVotes() + "");

            //set the recycler view
            mCommentsRecyclerView = (RecyclerView) rootView.findViewById(R.id.comments_recycler_view);
            mCommentsLinearLayoutManager = new LinearLayoutManager(getContext());
            mCommentsLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mCommentsRecyclerView.setLayoutManager(mCommentsLinearLayoutManager);


            mCommentsRecyclerViewAdapter = new CommentsRecyclerViewAdapter(new ArrayList<CommentNode>(),
                    mEmptyCommentsView, getContext());
            mCommentsRecyclerView.setAdapter(mCommentsRecyclerViewAdapter);

            mListOfComments = new ArrayList<>();

            //set the source and on click go to web view
            if(mPost.getUrl()!= null && !mPost.getUrl().isEmpty()){
                mDetailSource.setText(mPost.getDomain());
                mDetailSource.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigator.navigateToWebview(getContext(), mPost.getUrl());
                    }
                });
            } else {
                mDetailSource.setVisibility(View.GONE);
            }

            mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(getContext(), mShareButton);
                    popup.getMenuInflater()
                            .inflate(R.menu.menu_share, popup.getMenu());
                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.action_share_facebook) {
                                Navigator.sharePostUrlFacebook(mPost.getUrl(), getContext());
                            }
                            if (id == R.id.action_share_twitter) {
                                Navigator.sharePostTwitter(mPost.getTitle() + " " + mPost.getUrl(), getContext());
                            }
                            if( id == R.id.action_share_other){
                                Navigator.shareWebUrl(getContext(), mPost.getUrl());
                            }
                            return true;
                        }
                    });

                    popup.show(); //showing popup menu
                }
            });

            mUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Utils.checkUserLoggedIn()){
                        RedditAPI.vote(mPost, VoteDirection.UPVOTE, getContext());
                    }else{
                        BaseActivity.showLoginAlert(getContext());
                    }
                }
            });

            mDownBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Utils.checkUserLoggedIn()){
                        RedditAPI.vote(mPost, VoteDirection.DOWNVOTE, getContext());
                    }else{
                        BaseActivity.showLoginAlert(getContext());
                    }
                }
            });
        }
        else{
            Toast.makeText(getContext(), R.string.detail_post_erros_no_post, Toast.LENGTH_SHORT).show();
        }

        //TODO comments pagination
//        mCommentsRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mCommentsLinearLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount) {
//                if(!mListOfComments.isEmpty()){
//                    Log.d(LOG_TAG, "getting new page number " + page + " of " + mListOfComments.size());
//                    mCommentsRecyclerViewAdapter.addComments(mListOfComments);
//                    dataSetChanged();
//                }
//            }
//        });

        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                mPost = (Post) data.getSerializableExtra(BaseActivity.POST_DETAIL_KEY);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //load comments
        if(Utils.isNetworkAvailable(getContext())){
            getComments();
        }
        else{
            mEmptyCommentsView.setText(R.string.no_connection_detailed_comments_message);
            mEmptyCommentsView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Get the comments for the post and show them
     */
    private void getComments(){
        mCommentsProgressbar.setVisibility(View.VISIBLE);
        mEmptyCommentsView.setVisibility(View.GONE);
        new AsyncTask<Void, Void, List<CommentNode>>(){
            @Override
            protected List<CommentNode> doInBackground(Void... params) {
                List<CommentNode> resList = new ArrayList<CommentNode>();
                try{
                    resList = mCommentsRedditAPI.getTopNodeAllComments(mPost.getPosId());
                }catch (NetworkException e){
                    e.printStackTrace();
                }
                return resList;
            }
            @Override
            protected void onPostExecute(final List<CommentNode> result) {
                super.onPostExecute(result);
                mCommentsProgressbar.setVisibility(View.GONE);
                //update the list
                Log.d(LOG_TAG, "Loading of the comments finished");
                mListOfComments = result;

                mCommentsRecyclerViewAdapter.swapTopNode(mListOfComments);

                if(result.isEmpty()){
                    mEmptyCommentsView.setText(R.string.empty_comments_list);
                    mEmptyCommentsView.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + context.getString(R.string.detail_must_implement_err_message));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(POST_COMMENTS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCommentsRedditAPI = new RedditAPI();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    private List<CommentNode> getPageOfComments(int page){
        List<CommentNode> res = new ArrayList<CommentNode>();
        if(!mListOfComments.isEmpty() && page > 0){
            int startPos = page == 1 ? 0 : (page - 1) * mCommentsPerPage;
            int endPos = startPos + mCommentsPerPage - 1;
            if (startPos <= mListOfComments.size()){
                if(endPos > mListOfComments.size()){
                    endPos = mListOfComments.size()-1;
                }
                res = mListOfComments.subList(startPos, endPos);
            }
        }
        return res;
    }
}
