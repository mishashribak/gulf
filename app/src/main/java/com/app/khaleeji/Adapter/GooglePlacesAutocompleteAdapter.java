package com.app.khaleeji.Adapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import Model.AreaModel;


public class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

    private ArrayList<AreaModel> resultList;

    private Context context;

    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context=context;
    }
    private OnclickListener mOnclickListener;

    public void setList(ArrayList<AreaModel> list) {
        if(list!=null){
            this.resultList = list;

        }
        notifyDataSetChanged();
    }

    public ArrayList<AreaModel>  getList() {
        if(resultList!=null){
            return resultList;

        }
        return new ArrayList<AreaModel>();
    }


    public interface OnclickListener {
        public void OnSerachPlace(String place);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index).getName();
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.

                    if (mOnclickListener != null) {
                        mOnclickListener.OnSerachPlace(constraint.toString());
                    }

                    // Assign the data to the FilterResults
                    if(resultList!=null){
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;

}
    public void setOnClickListener(OnclickListener mOnclickListener) {
        this.mOnclickListener = mOnclickListener;
    }


}
