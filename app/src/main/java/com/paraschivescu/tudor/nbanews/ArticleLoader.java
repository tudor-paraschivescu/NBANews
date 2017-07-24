package com.paraschivescu.tudor.nbanews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    /** Tag for log messages*/
    private static final String LOG_TAG = ArticleLoader.class.getName();

    private String mUrl;
    private View mProgressBar;

    public ArticleLoader(Context context, String url, View progressBar) {
        super(context);
        mUrl = url;
        mProgressBar = progressBar;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public List<Article> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        Log.i(LOG_TAG, "Fetching articles from " + mUrl);
        return QueryUtils.fetchArticles(mUrl);
    }
}
