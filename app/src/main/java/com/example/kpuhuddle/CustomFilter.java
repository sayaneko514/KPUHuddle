package com.example.kpuhuddle;

import android.widget.Filter;

import java.util.ArrayList;

public class CustomFilter extends Filter {

    ArrayList<Event> filterList;
    myAdapter adapter;

    public CustomFilter(ArrayList<Event> filterList, myAdapter adapter){
        this.filterList = filterList;
        this.adapter = adapter;

    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results = new FilterResults();

        if (constraint != null && constraint.length()>0){

            constraint = constraint.toString().toUpperCase();

            ArrayList<Event> filterEvents = new ArrayList<>();

            for (int i=0; i<filterList.size(); i++){
                if (filterList.get(i).getEventName().toUpperCase().contains(constraint)) {

                    filterEvents.add(filterList.get(i));
                }

            }

            results.count = filterEvents.size();
            results.values = filterEvents;

        }
        else {
            results.count = filterList.size();
            results.values = filterList;

        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.events = (ArrayList<Event>) results.values;
        adapter.notifyDataSetChanged();

    }
}