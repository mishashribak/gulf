package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.app.khaleeji.R;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Constants.AppConstants;
import Model.ChatUserData;
import Model.LastMessage;
import Utility.SavePref;
import static Constants.AppConstants.TYPE_COMMENT_PHOTO;
import static Constants.AppConstants.TYPE_COMMENT_VIDEO;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> implements Filterable {

    private List<ChatUserData> chatUserDataList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private TopFilter topFilter;

    public ContactListAdapter(List<ChatUserData> chatUserDataList,
                              Context context,
                              OnItemClickListener onItemClickListener) {
        this.chatUserDataList = chatUserDataList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_friend_chat, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if(SavePref.getInstance(context).getUserdetail() != null){
            holder.rlRow.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(!chatUserDataList.get(position).isFromGroup())
                        onItemClickListener.onItemClick(0,chatUserDataList.get(position).getRoomId(),chatUserDataList.get(position));
                    else
                        onItemClickListener.onItemClick(2,chatUserDataList.get(position).getRoomId(),chatUserDataList.get(position));
                }
            });

            holder.moreHoriz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, holder.moreHoriz);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.menu_msg, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            onItemClickListener.onItemClick(3,chatUserDataList.get(position).getRoomId(),chatUserDataList.get(position));
                            return true;
                        }
                    });

                    popup.show(); //showing popup menu
                }
            });

            holder.imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( !chatUserDataList.get(position).isFromGroup())
                        onItemClickListener.onItemClick( 1,"", chatUserDataList.get(position));
                }
            });

//            holder.imgOfflineMark.setVisibility(View.GONE);
//            holder.imgOnlineMark.setVisibility(View.GONE);

            if( !chatUserDataList.get(position).isFromGroup()){
                String prefUsername = SavePref.getInstance(context).getUserdetail().getUsername();
                LastMessage lastMsg = chatUserDataList.get(position).getLastMessage();
                if (lastMsg != null) {

                    if(!lastMsg.getUsername().equals(prefUsername)){
                        if(lastMsg.getIsRead().equals("1")){
                            holder.imgUnread.setVisibility(View.GONE);
                        }else{
                            holder.imgUnread.setVisibility(View.VISIBLE);
                        }
                    }else{
                        holder.imgUnread.setVisibility(View.GONE);
                    }
                }else{
                    holder.imgUnread.setVisibility(View.GONE);
                }

                if (!chatUserDataList.get(position).getUser1().getUsername()
                        .equals(prefUsername)) {
//                    if(chatUserDataList.get(position).getUser1().getIsOnline().equals("1")){
//                        holder.imgOnlineMark.setVisibility(View.VISIBLE);
//                        holder.imgOfflineMark.setVisibility(View.GONE);
//                    }else{
//                        holder.imgOfflineMark.setVisibility(View.VISIBLE);
//                        holder.imgOnlineMark.setVisibility(View.GONE);
//                    }
                    if(!chatUserDataList.get(position).getUser1().getProfilePic().equals(""))
                        Picasso.with(context).load(chatUserDataList.get(position).getUser1().getProfilePic()).error(R.drawable.profile_placeholder)
                                .placeholder(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);
                    holder.txtName.setText(chatUserDataList.get(position).getUser1().getUsername());
                } else {
//                    if(chatUserDataList.get(position).getUser2().getIsOnline().equals("1")){
//                        holder.imgOnlineMark.setVisibility(View.VISIBLE);
//                        holder.imgOfflineMark.setVisibility(View.GONE);
//                    }else{
//                        holder.imgOfflineMark.setVisibility(View.VISIBLE);
//                        holder.imgOnlineMark.setVisibility(View.GONE);
//                    }
                    if(!chatUserDataList.get(position).getUser2().getProfilePic().equals(""))
                        Picasso.with(context).load(chatUserDataList.get(position).getUser2().getProfilePic()).error(R.drawable.profile_placeholder)
                                .placeholder(R.drawable.profile_placeholder).fit().centerInside()
                                .into(holder.imgProfile);
                    holder.txtName.setText(chatUserDataList.get(position).getUser2().getUsername());
                }

                if (lastMsg != null) {
                    if(lastMsg.getMessage().contains(TYPE_COMMENT_PHOTO) || lastMsg.getMessage().contains(TYPE_COMMENT_VIDEO)){
                        holder.txtLastMessage.setText(context.getResources().getString(R.string.dailies_comment));
                    }else if (lastMsg.getMessage().contains(AppConstants.TYPE_IMAGE)) {
                        holder.txtLastMessage.setText(context.getResources().getString(R.string.sent_photo));
//                        holder.imgMsgPic.setImageDrawable(context.getResources().getDrawable(R.drawable.message_photo));
                    } else if (lastMsg.getMessage().contains(AppConstants.TYPE_VIDEO)) {
                        holder.txtLastMessage.setText(context.getResources().getString(R.string.sent_video));
//                        holder.imgMsgPic.setImageDrawable(context.getResources().getDrawable(R.drawable.message_video));
                    } else if (lastMsg.getMessage().contains(AppConstants.TYPE_AUDIO)) {
                        holder.txtLastMessage.setText(context.getResources().getString(R.string.sent_audio));
//                        holder.imgMsgPic.setImageDrawable(context.getResources().getDrawable(R.drawable.message_audio));
                    }else if (lastMsg.getMessage().contains(AppConstants.MEDIA_TYPE_ONE_TIME_VIDEO)) {
                        holder.txtLastMessage.setText(context.getResources().getString(R.string.sent_video));
//                        holder.imgMsgPic.setImageDrawable(context.getResources().getDrawable(R.drawable.message_video));
                    } else {
                        holder.txtLastMessage.setText(lastMsg.getMessage());
//                        holder.imgMsgPic.setImageDrawable(context.getResources().getDrawable(R.drawable.message_text));
                    }

                    String dateString = lastMsg.getDate();
                    try{
                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                        Date date = fmt.parse(dateString);
                        Date date = new Date(Long.parseLong(dateString));
                        SimpleDateFormat fmtOut = new SimpleDateFormat("MMM dd hh:mm a");

                        holder.txtDate.setText(fmtOut.format(date));
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }else{
                    holder.txtDate.setText("");
                    holder.txtLastMessage.setText(context.getString(R.string.start_conversation));
                }
//                holder.txtLastMessage.setVisibility(View.VISIBLE);
//                holder.txtDate.setVisibility(View.VISIBLE);
            }else{
//                holder.txtLastMessage.setVisibility(View.INVISIBLE);
//                holder.txtDate.setVisibility(View.INVISIBLE);
                holder.imgUnread.setVisibility(View.GONE);
                holder.imgOfflineMark.setVisibility(View.GONE);
                holder.imgOnlineMark.setVisibility(View.GONE);
                if(chatUserDataList.get(position).getUser1() == null)
                    return;
                if(chatUserDataList.get(position).getUser1().getProfilePic() != null &&
                        !chatUserDataList.get(position).getUser1().getProfilePic().equals(""))
                    Picasso.with(context).load(chatUserDataList.get(position).getUser1().getProfilePic()).error(R.drawable.profile_placeholder)
                            .placeholder(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);
                holder.txtName.setText(chatUserDataList.get(position).getUser1().getUsername());
                LastMessage lastMsg = chatUserDataList.get(position).getLastMessage();
                if (lastMsg != null) {
                    if (lastMsg.getMessage().contains(AppConstants.TYPE_IMAGE)) {
                        holder.txtLastMessage.setText(context.getResources().getString(R.string.sent_photo));
//                        holder.imgMsgPic.setImageDrawable(context.getResources().getDrawable(R.drawable.message_photo));
                    } else if (lastMsg.getMessage().contains(AppConstants.TYPE_VIDEO)) {
                        holder.txtLastMessage.setText(context.getResources().getString(R.string.sent_video));
//                        holder.imgMsgPic.setImageDrawable(context.getResources().getDrawable(R.drawable.message_video));
                    } else if (lastMsg.getMessage().contains(AppConstants.TYPE_AUDIO)) {
                        holder.txtLastMessage.setText(context.getResources().getString(R.string.sent_audio));
//                        holder.imgMsgPic.setImageDrawable(context.getResources().getDrawable(R.drawable.message_audio));
                    }else if (lastMsg.getMessage().contains(AppConstants.MEDIA_TYPE_ONE_TIME_VIDEO)) {
                        holder.txtLastMessage.setText(context.getResources().getString(R.string.sent_video));
//                        holder.imgMsgPic.setImageDrawable(context.getResources().getDrawable(R.drawable.message_video));
                    } else {
                        holder.txtLastMessage.setText(lastMsg.getMessage());
//                        holder.imgMsgPic.setImageDrawable(context.getResources().getDrawable(R.drawable.message_text));
                    }

                    String dateString = lastMsg.getDate();
                    try{
                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                        Date date = new Date(Long.parseLong(dateString));
//                        Date date = fmt.parse(dateString);

                        SimpleDateFormat fmtOut = new SimpleDateFormat("MMM dd hh:mm a");

                        holder.txtDate.setText(fmtOut.format(date));
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }else{
                    holder.txtDate.setText("");
                    holder.txtLastMessage.setText("");
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        if(chatUserDataList == null)
            return 0;
        return chatUserDataList.size();
    }

    @Override
    public Filter getFilter() {
        if (topFilter == null)
            topFilter = new TopFilter(this, chatUserDataList);
        return topFilter;
    }

    public interface OnItemClickListener {
        void onItemClick(int type, String nodeName, ChatUserData index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtDate, txtLastMessage;
        private ImageView imgProfile, imgOfflineMark;
        private RelativeLayout rlRow;
        private ImageView imgOnlineMark;
        private ImageView imgMsgPic;
        private ImageView moreHoriz;
        private ImageView imgUnread;

            public MyViewHolder(View view) {
                super(view);
                rlRow = view.findViewById(R.id.rlRow);
                imgProfile = view.findViewById(R.id.imgCircleProfile);
                txtName = view.findViewById(R.id.txt_name);
                txtDate = view.findViewById(R.id.txt_date);
                txtLastMessage = view.findViewById(R.id.txt_last_message);
                imgOfflineMark = view.findViewById(R.id.img_seen_status);
                imgOnlineMark = view.findViewById(R.id.imgOnlineMark);
                imgMsgPic = view.findViewById(R.id.imgMsgPic);
                moreHoriz = view.findViewById(R.id.moreHoriz);
                imgUnread = view.findViewById(R.id.unread);
            }
        }

    public class TopFilter extends Filter{

        private final ContactListAdapter adapter;
        private List<ChatUserData> originalList = new ArrayList<>();
        private List<ChatUserData> filteredList;

        private TopFilter(ContactListAdapter adapter, List<ChatUserData> originalList) {
            super();
            this.adapter = adapter;
            this.originalList.addAll(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //Here you have to implement filtering way
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = constraint.toString().trim();
                for (final ChatUserData user : originalList) {
                    if (!user.getUser1().getUsername()
                            .equals(SavePref.getInstance(context)
                                    .getUserdetail().getUsername())){
                        if(user.getUser1().getUsername().contains(filterPattern))
                            filteredList.add(user);
                    }
                    else if(!user.getUser2().getUsername()
                            .equals(SavePref.getInstance(context)
                                    .getUserdetail().getUsername().contains(filterPattern))){
                        if(user.getUser2().getUsername().contains(filterPattern))
                            filteredList.add(user);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.chatUserDataList.clear();
            adapter.chatUserDataList.addAll((List<ChatUserData>) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}