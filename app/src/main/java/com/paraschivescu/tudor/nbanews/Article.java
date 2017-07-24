package com.paraschivescu.tudor.nbanews;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Article {

    private static final String OLD_FORMAT = "yyyy-MM-dd";
    private static final String NEW_FORMAT = "dd-MM-yyyy";

    private String mSectionName;
    private String mWebTitle;
    private String mWebUrl;
    private String mWebPublicationDate;

    public Article(String sectionName, String webTitle, String webUrl, String webPublicationDate) {
        this.mSectionName = sectionName;
        this.mWebTitle = webTitle;
        this.mWebUrl = webUrl;
        this.mWebPublicationDate = webPublicationDate;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getWebTitle() {
        return mWebTitle;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getWebPublicationDate() {
        String[] dateAndTime = mWebPublicationDate.split("T");
        String date = dateAndTime[0];

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        try {
            Date d = sdf.parse(date);
            sdf.applyPattern(NEW_FORMAT);
            date = sdf.format(d);
        } catch (Exception e) {
            Log.e("Article", "Date Format error");
        }

        return date;
    }
}
