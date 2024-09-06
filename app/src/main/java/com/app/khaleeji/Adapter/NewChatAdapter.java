package com.app.khaleeji.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.khaleeji.R;

/**
 * Created by user on 15/9/17.
 */

public class NewChatAdapter extends BaseAdapter {

    int color_arr[] = new int[6];
    int count = 0;
    private String[] mList;
    private Context mcontext;


    public NewChatAdapter(Context mcontext, String[] userList) {
        this.mcontext = mcontext;
        this.mList = userList;
        color_arr[0] = mcontext.getResources().getColor(R.color.light_blue);
        color_arr[1] = mcontext.getResources().getColor(R.color.myfriend_color);
        color_arr[2] = mcontext.getResources().getColor(R.color.logout_color);
        color_arr[3] = mcontext.getResources().getColor(R.color.setting_color);
        color_arr[4] = mcontext.getResources().getColor(R.color.backgroundDefaultColor);
        color_arr[5] = mcontext.getResources().getColor(R.color.discover_color);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.length;
    }

    @Override
    public Object getItem(int position) {
        return mList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewChatViewHolder mviewholder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mcontext);
            convertView = inflater.inflate(R.layout.row_newchat, parent, false);
            mviewholder = new NewChatViewHolder();
            mviewholder.llparent = (LinearLayout) convertView.findViewById(R.id.llparent);
            mviewholder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
            mviewholder.vline = (View) convertView.findViewById(R.id.vline);
            mviewholder.iv_smile = (ImageView) convertView.findViewById(R.id.iv_smile);
            mviewholder.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);
            convertView.setTag(mviewholder);
        } else {
            mviewholder = (NewChatViewHolder) convertView.getTag();
        }

        mviewholder.tv_username.setText(mList[position]);
        mviewholder.vline.setBackgroundColor(color_arr[count]);

        if (count >= color_arr.length - 1) {
            count = 0;
        } else {
            count++;
        }


        if (position == 2 || position == 4) {
            mviewholder.iv_smile.setVisibility(View.VISIBLE);
            mviewholder.iv_check.setVisibility(View.VISIBLE);
        } else {
            mviewholder.iv_smile.setVisibility(View.GONE);
            mviewholder.iv_check.setVisibility(View.GONE);

        }


        return convertView;
    }


    /*Meetup View Holder*/
    public class NewChatViewHolder {
        public TextView tv_username;
        public LinearLayout llparent;
        public View vline;
        public ImageView iv_smile, iv_check;

    }


}
