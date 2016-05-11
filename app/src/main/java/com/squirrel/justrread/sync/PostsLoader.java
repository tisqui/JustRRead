package com.squirrel.justrread.sync;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.squirrel.justrread.data.Post;

import java.util.List;

/**
 * Created by squirrel on 5/10/16.
 */
public class PostsLoader extends AsyncTaskLoader {

    public PostsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Post> loadInBackground() {
//        return SugarRecord.getCursor(Post.class, null, null, null, null, null);
        return Post.listAll(Post.class);
    }
}
