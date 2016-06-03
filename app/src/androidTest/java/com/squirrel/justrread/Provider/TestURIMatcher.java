package com.squirrel.justrread.Provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.squirrel.justrread.data.RedditContract;
import com.squirrel.justrread.data.RedditProvider;

/**
 * Created by squirrel on 5/12/16.
 */
public class TestURIMatcher extends AndroidTestCase {
    private static final Uri TEST_POST_DIR = RedditContract.PostEntry.CONTENT_URI;
    private static final Uri TEST_SUBSCRIPTION_DIR = RedditContract.SubscriptionEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = RedditProvider.buildUriMatcher();

        assertEquals("Error: The POST URI was matched incorrectly.",
                testMatcher.match(TEST_POST_DIR), RedditProvider.POST);

        assertEquals("Error: The SUBSCRIPION URI was matched incorrectly.",
                testMatcher.match(TEST_SUBSCRIPTION_DIR), RedditProvider.SUBSCRIPTION);
    }
}
