package com.paraschivescu.tudor.nbanews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    public ArticleAdapter(@NonNull Context context, @NonNull List<Article> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        // Inflate the view if it does not exist
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the article placed at that position
        final Article article = getItem(position);

        // Set the title of the article
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.article_title);
        titleTextView.setText(article.getWebTitle());

        // Set the section of the article
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.article_section);
        sectionTextView.setText(article.getSectionName());

        // Set the date of the article
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.article_date);
        dateTextView.setText(article.getWebPublicationDate());

        // Add a listener to the view
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(article.getWebUrl()));
                getContext().startActivity(i);
            }
        });

        return listItemView;
    }
}
