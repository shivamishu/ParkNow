package com.sjsu.parknow.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.sjsu.parknow.model.PlaceAPI;

import java.util.ArrayList;

public class PlaceAutoComplete extends ArrayAdapter implements Filterable {

    ArrayList<String> results;

    int resource;
    Context context;

    PlaceAPI placeAPI = new PlaceAPI();

    public PlaceAutoComplete(Context context, int resultId){
        super(context, resultId);
        this.resource = resultId;
        this.context = context;
    }

    @Override
    public String getItem(int pos){
        return results.get(pos);
    }
    @Override
    public int getCount(){
        return results.size();
    }

    @Override
    public Filter getFilter(){
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence textString) {
                FilterResults filteredResults = new FilterResults();
                if(textString != null){
                    results = placeAPI.autoComplete(textString.toString());
                    filteredResults.values = results;
                    filteredResults.count = results.size();
                }
                return filteredResults;
            }

            @Override
            protected void publishResults(CharSequence textString, FilterResults results) {
                if(results != null && results.count > 0){
                    notifyDataSetChanged();
                }
                else{
                    notifyDataSetInvalidated();
                }

            }
        };
        return filter;
    }

}
