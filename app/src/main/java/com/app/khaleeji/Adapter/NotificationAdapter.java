package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Notification_Datum;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Locale;
import Constants.Bundle_Identifier;
import CustomView.CustomTextView;
import Utility.ApiClass;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final String TAG = NotificationAdapter.class.getName();
    private List<Notification_Datum> mList;
    private Context mcontext;
    private OnClickListener mOnclickListener;


    public NotificationAdapter(Context mcontext, OnClickListener onclickListener) {
        this.mcontext = mcontext;
        this.mOnclickListener = onclickListener;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NotificationViewHolder holder, final int position) {

        holder.rlRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(position, 1);
            }
        });

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(position, 0);
            }
        });

        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            holder.txtExp.setText(mList.get(position).getArabicMessage());
        }else{
            holder.txtExp.setText(mList.get(position).getMsg());
        }

        if(mList.get(position).getProfile_picture() != null && ! mList.get(position).getProfile_picture().isEmpty())

            Picasso.with(mcontext).load(ApiClass.ImageBaseUrl + mList.get(position).getProfile_picture()).placeholder(R.drawable.profile_placeholder).error(
                    R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);
        else
            Picasso.with(mcontext).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);

        holder.txtName.setText(mList.get(position).getFull_name());


        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            holder.txtDistance.setText(mList.get(position).getAgoArabic());
        }else{
            holder.txtDistance.setText(mList.get(position).getAgo());
        }


    }

    @Override
    public int getItemCount() {
        if( mList != null)
            return mList.size();
        return 0;
    }

     public void setData( List<Notification_Datum> mList){
        this.mList = mList;
        notifyDataSetChanged();
     }


    public interface OnClickListener {
        void onClick(int pos, int type);
    }



    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rlRowItem;
        private CustomTextView txtExp;
        private com.mikhaellopez.circularimageview.CircularImageView imgProfile;
        private CustomTextView txtName;
        private CustomTextView txtDistance;

        public NotificationViewHolder(View view) {
            super(view);
            rlRowItem = view.findViewById(R.id.rlRowItem);
            txtExp = view.findViewById(R.id.txtExp);
            imgProfile = view.findViewById(R.id.imgProfile);
            txtName = view.findViewById(R.id.txtName);
            txtDistance = view.findViewById(R.id.txtDistance);
        }
    }



}
