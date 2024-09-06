package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.HotspotData;
import com.app.khaleeji.Response.HotspotUpdate;

import java.util.List;
import java.util.Locale;

import Constants.Bundle_Identifier;
import CustomView.CustomTextView;

public class HotspotListAdapter extends RecyclerView.Adapter<HotspotListAdapter.MyViewHolder> {

    private List<HotspotUpdate> dataList;
    private Context context;
    private OnHotSpotClickListener onItemClickListener;

    public HotspotListAdapter( Context context, OnHotSpotClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_hotspot, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        HotspotUpdate hotspotUpdate = dataList.get(position);
        holder.txtMark.setVisibility(View.GONE);
        if(hotspotUpdate.gettype() == 1){
            holder.txtUserName.setText("@"+hotspotUpdate.getusername());
            holder.imgPurpleCircle.setVisibility(View.VISIBLE);
            holder.imgYellowCircle.setVisibility(View.GONE);
            holder.endLineurple.setVisibility(View.VISIBLE);
            holder.endLine.setVisibility(View.GONE);
            Spanned htmlAsSpanned = Html.fromHtml(context.getResources().getString(R.string.location_you_check) +" <b>"+ hotspotUpdate.getLocationName()+",</b>" + context.getResources().getString(R.string.is_now_hotspot));
            holder.txtExp.setText(htmlAsSpanned);
        }else{
            if(hotspotUpdate.getStatus() == 1){
                holder.txtUserName.setText("@"+hotspotUpdate.getusername());
                holder.txtMark.setVisibility(View.VISIBLE);
                holder.imgPurpleCircle.setVisibility(View.GONE);
                holder.imgYellowCircle.setVisibility(View.VISIBLE);
                holder.endLineurple.setVisibility(View.GONE);
                holder.endLine.setVisibility(View.VISIBLE);
                holder.txtMark.setVisibility(View.VISIBLE);
                Spanned htmlAsSpanned = Html.fromHtml(context.getResources().getString(R.string.checked_into) +" <b>"+ hotspotUpdate.getLocationName()+",</b>");
                holder.txtExp.setText(htmlAsSpanned);
            }else{
                holder.txtUserName.setText("@"+hotspotUpdate.getusername());
                holder.imgPurpleCircle.setVisibility(View.VISIBLE);
                holder.imgYellowCircle.setVisibility(View.GONE);
                holder.endLineurple.setVisibility(View.VISIBLE);
                holder.endLine.setVisibility(View.GONE);
                Spanned htmlAsSpanned = Html.fromHtml(context.getResources().getString(R.string.checked_into)
                        +" <b>"+ hotspotUpdate.getLocationName()+",</b> " + context.getResources().getString(R.string.near_by));
                holder.txtExp.setText(htmlAsSpanned);
            }
        }

        if(dataList.size()-1 == position){
            holder.endLineurple.setVisibility(View.GONE);
            holder.endLine.setVisibility(View.GONE);
        }

        holder.hotspotItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onHotSpotClick(position);
            }
        });

        holder.txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onHotSpotTitleClick(position);
            }
        });

        holder.txtTime.setText(hotspotUpdate.getago());
        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            holder.txtTime.setText(hotspotUpdate.getagoArabic());
        }
    }

    @Override
    public int getItemCount() {
        if( dataList != null)
            return dataList.size();
        return 0;
    }

    public void setData(List<HotspotUpdate> list){
        dataList = list;
        notifyDataSetChanged();
    }

    public interface OnHotSpotClickListener {
        void onHotSpotClick(int index);
        void onHotSpotTitleClick(int index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtTime, txtExp,txtUserName,txtMark;
        private View endLine, endLineurple;
        private LinearLayout hotspotItem;
        private ImageView imgYellowCircle;
        private ImageView imgPurpleCircle;

        public MyViewHolder(View view) {
            super(view);
            txtExp = view.findViewById(R.id.txtExp);
            endLine = view.findViewById(R.id.endLineView);
            hotspotItem = view.findViewById(R.id.hotspotItem);
            imgYellowCircle = view.findViewById(R.id.imgYellowCircle);
            imgPurpleCircle = view.findViewById(R.id.imgPurpleCircle);
            txtUserName = view.findViewById(R.id.txtUserName);
            txtTime = view.findViewById(R.id.txtTime);
            endLineurple = view.findViewById(R.id.endLinePurpleView);
            txtMark = view.findViewById(R.id.txtMark);

//            txtHeaderHotspot = view.findViewById(R.id.txt_header_hotspot);
        }
    }
}