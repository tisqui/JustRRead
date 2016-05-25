package com.squirrel.justrread.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squirrel.justrread.R;
import com.squirrel.justrread.activities.BaseActivity;
import com.squirrel.justrread.data.Post;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailPostFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private OnFragmentInteractionListener mListener;
    private static final int POST_COMMENTS_LOADER = 1;
    private Post mPost;

    @Bind(R.id.detail_fragment_text)
    TextView mTextView;

    @Bind(R.id.detail_fragment_empty)
    TextView mEmptyView;

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
            mTextView.setText(mPost.toString());
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
        //load data
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
