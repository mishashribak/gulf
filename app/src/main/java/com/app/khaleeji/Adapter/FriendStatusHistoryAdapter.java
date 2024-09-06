package com.app.khaleeji.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.app.khaleeji.Fragments.SearchTabFragments;
import com.app.khaleeji.R;
import com.bumptech.glide.Glide;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import Constants.AppConstants;
import CustomView.CustomTextView;
import Model.StatusModel;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.SavePref;

public class FriendStatusHistoryAdapter extends RecyclerView.Adapter<FriendStatusHistoryAdapter.MyViewHolder> {

    private List<StatusModel> listData;
    private FragmentActivity context;
    private OnItemClickListener onItemClickListener;
    private SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private boolean isFromMe;

    public FriendStatusHistoryAdapter(FragmentActivity context, boolean isFromMe, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.isFromMe = isFromMe;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_status_updates, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        StatusModel statusModel = listData.get(position);

        if(statusModel.likeBefore){
            holder.imgRedHeart.setVisibility(View.VISIBLE);
            holder.imgHeart.setVisibility(View.GONE);
        }else{
            holder.imgRedHeart.setVisibility(View.GONE);
            holder.imgHeart.setVisibility(View.VISIBLE);
        }

        try {
            Date date = dateParser.parse(statusModel.created_at);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            holder.txtDate.setText(sdf.format(date));
//            holder.txtDate.setText("5 "+context.getResources().getString(R.string.days)+" "+context.getResources().getString(R.string.ago));
//            if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
//                holder.txtDate.setText(context.getResources().getString(R.string.ago)+" 5 "+context.getResources().getString(R.string.days));
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(statusModel.profile_picture == null){
            Glide.with(context).load(R.drawable.profile_placeholder).into(holder.imgUserPic);
        }else{
            Glide.with(context).load(ApiClass.ImageBaseUrl +statusModel.profile_picture).into(holder.imgUserPic);
        }

        holder.txtName.setText(statusModel.full_name);
        holder.txtLikes.setText(context.getResources().getString(R.string.likes)+ " "+statusModel.likes_count);
        holder.txtComments.setText(context.getResources().getString(R.string.comments_post)+ " "+statusModel.comments_count);
        holder.txtEye.setText(statusModel.views_count+ "");



        SpannableString tag = getTags(statusModel.text);
        holder.txtDesc.setText(tag == null ? "" : tag);
        holder.txtDesc.setMovementMethod(LinkMovementMethod.getInstance());

        holder.txtLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onLikers(statusModel);
            }
        });

        holder.readBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onViewers(statusModel);
            }
        });

        holder.imgRedHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(statusModel, AppConstants.TYPE_LIKES);
            }
        });

        holder.imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(statusModel, AppConstants.TYPE_LIKES);
            }
        });

        holder.imgUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(statusModel, AppConstants.TYPE_CIRCLE_IMG);
            }
        });

        holder.rlRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(statusModel, -1);
            }
        });

        holder.moreHoriz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.moreHoriz);
                //Inflating the Popup using xml file
                if(statusModel.user_id.equals(String.valueOf(SavePref.getInstance(context).getUserdetail().getId()))){
                    popup.getMenuInflater().inflate(R.menu.menu_msg, popup.getMenu());
                }else{
                    popup.getMenuInflater().inflate(R.menu.menu_home_report, popup.getMenu());
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(isFromMe){
                            onItemClickListener.onItemClick(statusModel, AppConstants.TYPE_MORE_REMOVE);
                        }else{
                            onItemClickListener.onItemClick(statusModel, AppConstants.TYPE_MORE_REPORT);
                        }

                        return true;
                    }
                });
                popup.show();

            }
        });

    }

    public void setData(List<StatusModel> list){
        this.listData = list;
        notifyDataSetChanged();
    }


    public SpannableString getTags(String str){
        if(str != null &&! str.isEmpty()){
            SpannableString ss = new SpannableString(str);
            int startPos;
            int len = 0;
            while ((startPos = str.indexOf("#")) != -1){
                int endPos;
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        TextView tv = (TextView) textView;
                        Spanned s = (Spanned) tv.getText();
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);
                        Log.d("ProfileFragment", "onClick [" + s.subSequence(start, end) + "]");
                        Fragment_Process.replaceFragment(context.getSupportFragmentManager(), new SearchTabFragments(s.subSequence(start, end).toString(), "2"), context, R.id.framelayout_main);
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };
                len = len + startPos;
                str = str.substring(startPos);
                if ((endPos = str.indexOf(" ")) != -1){
                    ss.setSpan(clickableSpan, len, len + endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else{
                    ss.setSpan(clickableSpan, len, len + str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return ss;
                }
                len = len + endPos + 1;
                str = str.substring(endPos + 1);
            }

            return ss;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if(listData == null)
            return 0;
        return listData.size();
    }


    public interface OnItemClickListener {
        void onItemClick(StatusModel statusModel, int type);
        void onLikers(StatusModel statusModel);
        void onViewers(StatusModel statusModel);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtDate, txtName, txtDesc, txtEye;
        private CustomTextView txtLikes;
        private CustomTextView txtComments;
        private LinearLayout llLikesBox;
        private com.mikhaellopez.circularimageview.CircularImageView imgUserPic;
        private RelativeLayout rlRow;
        private ImageView moreHoriz, imgRedHeart, imgHeart;
        private LinearLayout readBox;
        public MyViewHolder(View view) {
            super(view);
            txtDate = view.findViewById(R.id.txtDate);
            txtLikes = view.findViewById(R.id.txtLikes);
            txtComments = view.findViewById(R.id.txtComments);
            txtName = view.findViewById(R.id.txtName);
            txtDesc = view.findViewById(R.id.txtDesc);
            txtEye = view.findViewById(R.id.txtEye);
            llLikesBox = view.findViewById(R.id.likesBox);
            imgUserPic = view.findViewById(R.id.imgProfile);
            rlRow = view.findViewById(R.id.rlRow);
            moreHoriz = view.findViewById(R.id.moreHoriz);
            imgRedHeart = view.findViewById(R.id.imgRedHeart);
            imgHeart = view.findViewById(R.id.imgHeart);
            readBox = view.findViewById(R.id.readBox);
        }
    }
}