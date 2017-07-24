package com.paraschivescu.tudor.nbanews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;



/**
 * Helper methods related to requesting and receiving data from an API.
 */
public final class QueryUtils {

    /** Tag for log messages*/
    private static final String LOG_TAG = QueryUtils.class.getName();

    private static int READ_TIMEOUT = 10000;        /* milliseconds */
    private static int CONNECT_TIMEOUT = 15000;     /* milliseconds */

    private QueryUtils() {
    }

    /**
     * Return an URL object from a given String.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode() +
                    "for URL: " + url.toString());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader streamReader = new InputStreamReader(
                    inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(streamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of Article objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Article> extractFeatureFromJson(String json) {

        if (TextUtils.isEmpty(json)) {
            return null;
        }

        List<Article> articles = new ArrayList<>();

        try {

            JSONObject responseObject = new JSONObject(json).getJSONObject("response");
            if (!responseObject.has("results")) {
                return articles;
            }

            JSONArray resultsArray = responseObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject currentArticle = resultsArray.getJSONObject(i);

                String sectionName = currentArticle.getString("sectionName");
                String title = currentArticle.getString("webTitle");
                String url = currentArticle.getString("webUrl");

                // Check if "webPublicationDate" exists
                String date;
                if (currentArticle.has("webPublicationDate")) {
                    date = currentArticle.getString("webPublicationDate");
                } else {
                    date = "Unknown date";
                }

                articles.add(new Article(sectionName, title, url, date));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        return articles;
    }

    /**
     * Query the dataset and return a list of Article objects.
     */
    public static List<Article> fetchArticles(String requestUrl) {

        URL url = createUrl(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return extractFeatureFromJson(jsonResponse);
    }


}