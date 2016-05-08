package com.squirrel.justrread.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squirrel.justrread.R;
import com.squirrel.justrread.adapters.FeedRecyclerViewAdapter;
import com.squirrel.justrread.adapters.PostClickListener;
import com.squirrel.justrread.data.Post;

import java.util.ArrayList;


public class FeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView mRecyclerView;
    private FeedRecyclerViewAdapter mFeedRecyclerViewAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String SELECTED_KEY = "selected_position";

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

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Post post);
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "test");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFeedRecyclerViewAdapter = new FeedRecyclerViewAdapter(new ArrayList<Post>(), getActivity().getApplicationContext());
        mRecyclerView.setAdapter(mFeedRecyclerViewAdapter);

        mRecyclerView.addOnItemTouchListener(new PostClickListener(getActivity().getApplicationContext(),
                mRecyclerView, new PostClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mFeedRecyclerViewAdapter.onClick(view, position);
//                ((Callback) getActivity())
//                        .onItemSelected(mFeedRecyclerViewAdapter.getPost(position));
                mPosition = position;
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //TODO do tmth on long click
            }
        }));

        // If there's instance state, get the last selected position
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        //TODO set the scroll listener (endless listener)

        return rootView;

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

    @Override
    public void onResume() {
        super.onResume();
        mFeedRecyclerViewAdapter.addPostsToList(generateDummyData());
        //TODO load information to the list, check the previous selected item and scroll to this item
    }

    private ArrayList<Post> generateDummyData(){
        ArrayList<Post> postList = new ArrayList<>();
        for(int i = 0; i<10; i++){
            Post post = new Post("1", "123",
                    123, 240, false, 2344, "username123", "imgur.com",
                    false, 17, true, "/r/WTF",
                    "123", "Russia Arrests Scientologist for Stealing $2 Million and Giving to Church",
                    "", "Tuna swallows a seagull and spits it out",
                    "google.com");
            postList.add(post);
        }
        return postList;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
