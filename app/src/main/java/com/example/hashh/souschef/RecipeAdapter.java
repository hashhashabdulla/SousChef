package com.example.hashh.souschef;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hashh on 09-05-2017.
 */

public class RecipeAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public RecipeAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Recipe object) {
        super.add(object);
        list.add(object);
    }


    public void remove(Recipe object) {
        super.remove(object);
        list.remove(object);
        notifyDataSetChanged();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null){
            convertView = layoutInflater.inflate(R.layout.reclist, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.recipename = (TextView)convertView.findViewById(R.id.recipeName);
            viewHolder.recipeimg = (ImageView)convertView.findViewById(R.id.recipeImage);
            viewHolder.ratingBar = (RatingBar)convertView.findViewById(R.id.listRatingBar);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Recipe recipe = (Recipe) this.getItem(position);
        viewHolder.recipename.setText(recipe.getRecipename());
        viewHolder.recipeimg.setImageBitmap(recipe.getRecipeimg());
        viewHolder.ratingBar.setRating(recipe.getRating());

        return convertView;
    }

    static class ViewHolder{
        TextView recipename;
        ImageView recipeimg;
        RatingBar ratingBar;
    }
}
