package com.paraschivescu.tudor.nbanews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Article>> {

    private static final String GUARDIAN_REQUEST_URL = "http://content.guardianapis.com/search?order-by=newest&q=nba&api-key=test&page-size=20";

    private ArticleAdapter mAdapter;

    private static final int ARTICLE_LOADER_ID = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh:
                // User chose the "Refresh" action, refresh the news
                Toast.makeText(this, "Refreshing the news...", Toast.LENGTH_SHORT).show();

                if (hasStableConnection()) {
                    mAdapter.clear();
                    findViewById(R.id.no_network).setVisibility(View.INVISIBLE);

                    // Restart the loader
                    getLoaderManager().restartLoader(ARTICLE_LOADER_ID, null, NewsActivity.this);
                } else {
                    findViewById(R.id.no_network).setVisibility(View.VISIBLE);
                    findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
                    mAdapter.clear();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // Check for network connectivity
    private boolean hasStableConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Add icon to my custom toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.icon_with_padding);

        // Create and set the adapter of the list view
        ListView listView = (ListView) findViewById(R.id.articles_list_view);
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        listView.setAdapter(mAdapter);

        if (hasStableConnection()) {
            // Start loader
            getLoaderManager().initLoader(ARTICLE_LOADER_ID, null, NewsActivity.this);
        } else {
            findViewById(R.id.no_network).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        return new ArticleLoader(this, GUARDIAN_REQUEST_URL, findViewById(R.id.progress_bar));
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        mAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }

        findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        mAdapter.clear();
    }
}
