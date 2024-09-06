package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.khaleeji.Fragments.ImageFullScreenFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.GetSlctdMediaComments;

import java.util.ArrayList;
import java.util.List;

import Utility.Utils;

/**
 * Created by Dcube on 20-08-2018.
 */

public class OtherProfileMediaDetailCommentAdapter extends RecyclerView.Adapter<OtherProfileMediaDetailCommentAdapter.SearchViewHolder>{

    private final String TAG = MyProfileMediaDetailCommentAdapter.class.getName();

    private List<GetSlctdMediaComments> mList = new ArrayList<>();
    private List<GetSlctdMediaComments> filteredist;

    private Context mcontext;
    int color_arr[] = new int[7];
    int count = 0;
    String searchtype;
    // callFriendRequestApi calladdinetrface;

    ImageFullScreenFragment mdialog;

    int dpWidth,dpHeight;


    public OtherProfileMediaDetailCommentAdapter(Context mcontext, List<GetSlctdMediaComments> mList) {

        this.mcontext = mcontext;
        this.mList = mList;
        this.filteredist = mList;

        //  calladdinetrface =  search_fragment;
        count = 0;

        color_arr[0] = mcontext.getResources().getColor(R.color.colorLightGray);
        color_arr[1] = mcontext.getResources().getColor(R.color.colorLightGray);
        color_arr[2] = mcontext.getResources().getColor(R.color.colorLightGray);
        color_arr[3] = mcontext.getResources().getColor(R.color.colorLightGray);
        color_arr[4] = mcontext.getResources().getColor(R.color.colorLightGray);
        color_arr[5] = mcontext.getResources().getColor(R.color.colorLightGray);
        color_arr[6] = mcontext.getResources().getColor(R.color.colorLightGray);

        Log.e(TAG,"mList.size() : "+mList.size());

        dpHeight = Utils.dpToPx(45);
        dpWidth = Utils.dpToPx(45);

        Log.e(TAG,"dpHeight : "+dpHeight);

    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_profile_media_detail_comment, parent, false);
        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {

        holder.username.setText(mList.get(position).getUsername());

        holder.discription.setText(mList.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        Log.v("size" ,mList.size()+"");
        return mList == null ? 0 : mList.size();
    }


    public interface CommentDeleteInterface {
        public void Onposclick(int pos);
    }

    /*Meetup View Holder*/
    public class SearchViewHolder extends RecyclerView.ViewHolder {

        public TextView username, discription;
        //   public ImageView userimage, iv_addfriend,iv_cross;
        public RelativeLayout llparent;

        public SearchViewHolder(View view) {
            super(view);
            llparent = (RelativeLayout) view.findViewById(R.id.llparent);
            //   userimage = (ImageView) view.findViewById(R.id.userImage);
            username = (TextView) view.findViewById(R.id.tv_username);
            discription = (TextView) view.findViewById(R.id.tv_discription);
            //   iv_addfriend = (ImageView) view.findViewById(R.id.iv_addfriend);
            //   iv_cross = (ImageView) view.findViewById(R.id.iv_cross);

        }
    }

    /***
     *
     */
    public interface callFriendRequestApi{

        public    void  addFriendRequest(int position);
        public    void  DeteteorFriendRequest(int position, String status);
        public    void  seeAnswerDialog(int position, String status);

    }

    public void updateList(List<GetSlctdMediaComments> itemList){

        mList.clear();
        this.mList.addAll(itemList);
        notifyDataSetChanged();
    }


}

