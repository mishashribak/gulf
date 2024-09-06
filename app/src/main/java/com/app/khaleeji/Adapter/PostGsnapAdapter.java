package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.fetchHotspotAndFrndsDetail.FetchHotspotAndFrndsDetailData;

public class PostGsnapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private FetchHotspotAndFrndsDetailData fetchHotspotAndFrndsDetailData;
    private Context context;


    public PostGsnapAdapter(FetchHotspotAndFrndsDetailData fetchHotspotAndFrndsDetailData, Context context) {
        this.fetchHotspotAndFrndsDetailData = fetchHotspotAndFrndsDetailData;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.row_header, parent, false);
                return new ViewHolder0(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.row_picture_recent_frnds, parent, false);
                return new ViewHolder2(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder0 extends RecyclerView.ViewHolder {
        public ViewHolder0(View itemView) {
            super(itemView);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {

        public ViewHolder2(View itemView) {
            super(itemView);
        }


    }
}