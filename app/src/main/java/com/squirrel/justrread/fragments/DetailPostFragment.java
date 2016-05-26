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
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squirrel.justrread.R;
import com.squirrel.justrread.activities.BaseActivity;
import com.squirrel.justrread.adapters.CommentsRecyclerViewAdapter;
import com.squirrel.justrread.api.RedditAPI;
import com.squirrel.justrread.data.Post;

import net.dean.jraw.models.CommentNode;

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
    View mEmptyCommentsView;

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

    private RecyclerView mCommentsRecyclerView;
    private LinearLayoutManager mCommentsLinearLayoutManager;
    private CommentsRecyclerViewAdapter mCommentsRecyclerViewAdapter;
    private RedditAPI mCommentsRedditAPI;

    private CommentNode mCommentNode;


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
                mDetailSubreddit.setText(mPost.getSubreddit());
            }
            mDetailAuthor.setText(mPost.getAuthor());
            mDetailTitle.setText(mPost.getTitle());

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

            mDetailText.setText(Html.fromHtml(mPost.getSelfTextHtml()));
            if(mPost.getNumComments() == 0){
                mDetailNumComments.setText("No comments");
            } else{
                mDetailNumComments.setText(mPost.getNumComments() + " comments");
            }

            mDetailVotesNum.setText(mPost.getUpVotes() + "");

            //set the recycler view
            mCommentsRecyclerView = (RecyclerView) rootView.findViewById(R.id.comments_recycler_view);
            mCommentsLinearLayoutManager = new LinearLayoutManager(getContext());
            mCommentsLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mCommentsRecyclerView.setLayoutManager(mCommentsLinearLayoutManager);


            mCommentsRecyclerViewAdapter = new CommentsRecyclerViewAdapter(new ArrayList<CommentNode>(), mEmptyCommentsView);
            mCommentsRecyclerView.setAdapter(mCommentsRecyclerViewAdapter);

        }
        else{
            Toast.makeText(getContext(), "Post information was not properly passed to the fragment", Toast.LENGTH_SHORT).show();
        }

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
        getComments();
    }

    private void getComments(){
        mCommentsProgressbar.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, List<CommentNode>>(){
            @Override
            protected List<CommentNode> doInBackground(Void... params) {
                Log.d(LOG_TAG, "Trying to download the post by id " + mPost.getPosId());
                return mCommentsRedditAPI.getTopNodeAllComments(mPost.getPosId());
            }
            @Override
            protected void onPostExecute(List<CommentNode> result) {
                super.onPostExecute(result);
                mCommentsProgressbar.setVisibility(View.INVISIBLE);
                //update the list
                Log.d(LOG_TAG, "Loading of the comments finished");
                mCommentsRecyclerViewAdapter.swapTopNode(result);
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
                    + " must implement OnFragmentInteractionListener");
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
}
