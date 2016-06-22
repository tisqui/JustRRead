package com.squirrel.justrread.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squirrel.justrread.R;
import com.squirrel.justrread.Utils;
import com.squirrel.justrread.data.RedditContract;

import java.util.Date;

/**
 * Created by squirrel on 6/21/16.
 */
public class WidgetService extends RemoteViewsService {
    public final String LOG_TAG = WidgetService.class.getSimpleName();
    private static final String[] WIDGET_POST_COLUMNS = {
            RedditContract.PostColumns.COLUMN_ID,
            RedditContract.PostColumns.COLUMN_DATE_CREATED,
            RedditContract.PostColumns.COLUMN_AUTHOR,
            RedditContract.PostColumns.COLUMN_DOMAIN,
            RedditContract.PostColumns.COLUMN_NUM_COMMENTS,
            RedditContract.PostColumns.COLUMN_SUBREDDIT,
            RedditContract.PostColumns.COLUMN_THUMBNAIL,
            RedditContract.PostColumns.COLUMN_TITLE
    };

    private static final int INDEX_ID = 0;
    private static final int INDEX_DATE_CREATED = 1;
    private static final int INDEX_AUTHOR = 2;
    private static final int INDEX_DOMAIN = 3;
    private static final int INDEX_NUM_COMMENTS = 4;
    private static final int INDEX_SUBREDDIT = 5;
    private static final int INDEX_THUMBNAIL = 6;
    private static final int INDEX_TITLE = 7;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(RedditContract.PostEntry.CONTENT_URI,
                        //projection
                        WIDGET_POST_COLUMNS,
                        //selection
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);


                // Extract the Post data from the Cursor
                String author = data.getString(INDEX_AUTHOR);
                String domain = data.getString(INDEX_DOMAIN);
                String num_comments = data.getInt(INDEX_NUM_COMMENTS) + "";
                String subreddit = "/" + data.getString(INDEX_SUBREDDIT);
                String thumbnail = data.getString(INDEX_THUMBNAIL);
                String title = data.getString(INDEX_TITLE);
                String dateCreated = Utils.getPostedTimeAgo(new Date(data.getLong(INDEX_DATE_CREATED)), getApplicationContext());


                views.setTextViewText(R.id.widget_list_item_title, title);
                views.setTextViewText(R.id.widget_list_item_time, dateCreated);
                views.setTextViewText(R.id.widget_list_item_source, domain);
                views.setTextViewText(R.id.widget_list_item_comments_num, num_comments);

                //TODO load image to the thumbnail

                final Intent fillInIntent = new Intent();
                views.setOnClickFillInIntent(R.id.widget_list_item_frame, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
//                if (data.moveToPosition(position))
//                    return Long.getLong(data.getString(INDEX_ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}
