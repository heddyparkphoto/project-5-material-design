package com.learn.heddy.xyzreader.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.learn.heddy.xyzreader.R;
import com.learn.heddy.xyzreader.data.ArticleLoader;
import com.learn.heddy.xyzreader.data.ItemsContract;
import com.learn.heddy.xyzreader.data.UpdaterService;
import com.learn.heddy.xyzreader.util.Utility;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ImageView mEmptyBgImageView;
    private TextView mEmptyView;
    private View mCoorLayout;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        final View toolbarContainerView = findViewById(R.id.toolbar_container);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mEmptyBgImageView = (ImageView)findViewById(R.id.bg_empty);
        mEmptyView = (TextView) findViewById(R.id.list_empty);
        mCoorLayout = (View)findViewById(R.id.main_coorLayout);

        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            startService(new Intent(this, UpdaterService.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }
//
//    @Override
//    protected void onResume() {
//
//        super.onResume();
//    }

    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mSwipeRefreshLayout.setRefreshing(false);

        Adapter adapter = new Adapter(cursor, this);
        adapter.setHasStableIds(true);

        mRecyclerView.setAdapter(adapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);

        updateEmptyView(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private void updateEmptyView(Adapter dapter) {

        if (dapter.getItemCount() ==0){
            boolean isOn = Utility.isOnline(this);
            Log.d("EmptyView test", "no data status");
            if (!isOn){
                mEmptyView.setText(getString(R.string.noConnectivity));
                // Snackbar with Action to open directly the Devices Settings
                if (mCoorLayout!=null) {
                    mSnackbar = Snackbar.make(mCoorLayout,
                            R.string.noConnectivity, Snackbar.LENGTH_LONG);
                    mSnackbar.setAction(R.string.open_settings,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                }
                            });
                    mSnackbar.show();
                }
            }
            mEmptyBgImageView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            Log.d("EmptyView test", "Hmm... we have data? "+dapter.getItemCount());
            mEmptyBgImageView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.INVISIBLE);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Cursor mCursor;
        private Activity mContext;

        public Adapter(Cursor cursor, Activity context) {
            mCursor = cursor;
            mContext = context;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                            mContext,
                            vh.thumbnailView,
                            vh.thumbnailView.getTransitionName())
                            .toBundle();

                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition())));
                    startActivity(intent, bundle);
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            holder.subtitleView.setText(
//                    DateUtils.getRelativeTimeSpanString(
//                            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
//                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
//                            DateUtils.FORMAT_ABBREV_ALL).toString()
//                            + " by "
                            "by " + mCursor.getString(ArticleLoader.Query.AUTHOR));
            holder.thumbnailView.setImageUrl(
                    mCursor.getString(ArticleLoader.Query.THUMB_URL),
                    ImageLoaderHelper.getInstance(ArticleListActivity.this).getImageLoader());
            holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }


//        public void showHideEmptyView() {

//            if (getItemCount() == 0){
//                adapterEmptyView.setVisibility(View.VISIBLE);
//            } else {
//                adapterEmptyView.setVisibility(View.INVISIBLE);
//            }
//        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public DynamicHeightNetworkImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }
}
