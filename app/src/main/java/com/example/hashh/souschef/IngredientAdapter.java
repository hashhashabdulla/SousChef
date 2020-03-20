package com.example.hashh.souschef;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hashh on 21-05-2017.
 */

public class IngredientAdapter extends ArrayAdapter {
    List list = new ArrayList();

    public IngredientAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Ingredient object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public void remove(Object object) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.inglist, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ing_name = (TextView)convertView.findViewById(R.id.ingItem);
            viewHolder.removeButton = (Button)convertView.findViewById(R.id.removeButton);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final Ingredient ingredient = (Ingredient) this.getItem(position);
        viewHolder.ing_name.setText(ingredient.getIng_name());
        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngredientAdapter.this.remove(ingredient);
                SearchIngredients.arrayList.remove(position);
            }
        });
        return convertView;
    }

    static class ViewHolder{
        TextView ing_name;
        Button removeButton;
    }
}
