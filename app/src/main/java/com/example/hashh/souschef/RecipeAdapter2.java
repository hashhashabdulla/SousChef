package com.example.hashh.souschef;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hashh on 09-05-2017.
 */

public class RecipeAdapter2 extends ArrayAdapter {

    List list = new ArrayList();

    public RecipeAdapter2(Context context, int resource) {
        super(context, resource);
    }

    public void add(Recipe2 object) {
        super.add(object);
        list.add(object);
    }

    public void remove(Recipe2 object) {
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
        ViewHolder2 viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null){
            convertView = layoutInflater.inflate(R.layout.reclist2, parent, false);
            viewHolder = new ViewHolder2();
            viewHolder.recipename = (TextView)convertView.findViewById(R.id.recipeName2);
            viewHolder.recipeimg = (ImageView)convertView.findViewById(R.id.recipeImage3);
            viewHolder.ratingBar = (RatingBar)convertView.findViewById(R.id.listRatingBar2);
            viewHolder.priority = (TextView)convertView.findViewById(R.id.priority);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder2)convertView.getTag();
        }

        Recipe2 recipe2 = (Recipe2) this.getItem(position);
        Log.d("Recipe Name: ", recipe2.getRecipename());
        viewHolder.recipename.setText(recipe2.getRecipename());
        viewHolder.recipeimg.setImageBitmap(recipe2.getRecipeimg());
        viewHolder.ratingBar.setRating(recipe2.getRating());
        if(recipe2.getPriority() < 0){
            String str = "You are missing "+String.valueOf(Math.abs(recipe2.getPriority())+" ingredients");
            viewHolder.priority.setText(str);
            viewHolder.priority.setTextColor(Color.rgb(124, 16, 16));
        }
        else if(recipe2.getPriority() > 0){
            String str = "You'll be left with "+recipe2.getPriority()+" extra ingredients";
            viewHolder.priority.setText(str);
            viewHolder.priority.setTextColor(Color.rgb(16, 124, 16));
        }
        else{
            String str = "It's a perfect match";
            viewHolder.priority.setText(str);
            viewHolder.priority.setTextColor(Color.rgb(16, 16, 124));
        }

        return convertView;
    }

    static class ViewHolder2{
        TextView recipename;
        ImageView recipeimg;
        RatingBar ratingBar;
        TextView priority;
    }
}
