package com.app.khaleeji.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.khaleeji.Activity.FriendDailiesViewActivity;
import com.app.khaleeji.Fragments.FriendDailyFragment;
import com.app.khaleeji.Fragments.IntroFragment;
import com.app.khaleeji.Response.MediaModel;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;

import java.util.ArrayList;
import java.util.List;


public class FriendDailiesAdapter extends FragmentPagerAdapter {

    private List<FriendListDailiesOfFriends> listDailiesOfFriends;


    public FriendDailiesAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new FriendDailyFragment(listDailiesOfFriends.get(position).getMedia());
    }

    @Override
    public int getCount() {
        if(listDailiesOfFriends == null)
            return 0;
        return listDailiesOfFriends.size();
    }

    public void setData(List<FriendListDailiesOfFriends> list){
        listDailiesOfFriends = list;
        notifyDataSetChanged();
    }
}