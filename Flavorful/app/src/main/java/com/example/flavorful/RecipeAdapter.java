package com.example.flavorful;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flavorful.object.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeAdapter extends BaseAdapter {

    private static  final int ID_CONSTANT = 0x01000000;

    final Context mContext;
    final ArrayList<Recipe> mCollection;

    public RecipeAdapter(Context mContext, ArrayList<Recipe> mCollection) {
        this.mContext = mContext;
        this.mCollection = mCollection;
    }

    @Override
    public int getCount() {
        if(mCollection != null) {
            return mCollection.size();
        } else {
            return 0;
        }
    }

    @Override
    public Recipe getItem(int position) {
        if (mCollection != null && position < mCollection.size() && position >= 0){
            return  mCollection.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(mCollection != null && position < mCollection.size() && position >= 0) {
            return  ID_CONSTANT + position;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Return child view at specified position
        if(convertView == null){
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_layout, parent,false);
        }

        Recipe recipe = getItem(position);
        //Grab layout views
        TextView name = convertView.findViewById(R.id.recipe_title);
        ImageView imageView = convertView.findViewById(R.id.recipe_image);

        //Set layout views
        name.setText(recipe.getName());
        Picasso.get().load(recipe.getImage()).into(imageView);

        return  convertView;
    }
}
