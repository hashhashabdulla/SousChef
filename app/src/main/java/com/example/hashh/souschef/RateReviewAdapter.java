package com.example.hashh.souschef;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hashh on 08-05-2017.
 */

public class RateReviewAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public RateReviewAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(RatingsAndReviews object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.ratereviewlist, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.username = (TextView) convertView.findViewById(R.id.rateUsername);
            viewHolder.rating = (RatingBar) convertView.findViewById(R.id.ratingBarIndicator);
            viewHolder.review = (TextView) convertView.findViewById(R.id.rateReview);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RatingsAndReviews ratingsAndReviews = (RatingsAndReviews) this.getItem(position);
        viewHolder.username.setText(ratingsAndReviews.getUsername());
        viewHolder.rating.setRating(ratingsAndReviews.getRating());
        viewHolder.review.setText(ratingsAndReviews.getReview());

        return convertView;
    }

    static class ViewHolder{
        TextView username, review;
        RatingBar rating;
    }
}
