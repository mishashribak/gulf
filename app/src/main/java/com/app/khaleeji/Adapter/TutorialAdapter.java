package com.app.khaleeji.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.khaleeji.Fragments.IntroFragment;

import java.util.ArrayList;

import Model.TutorialModel;

public class TutorialAdapter extends FragmentPagerAdapter {

    private ArrayList<TutorialModel> Intro_List;


    public TutorialAdapter(FragmentManager fm) {
        super(fm);
    }

    public TutorialAdapter(FragmentManager fm, ArrayList<TutorialModel> Intro_List) {
        super(fm);
        this.Intro_List = Intro_List;
    }

    @Override
    public Fragment getItem(int position) {
        return IntroFragment.newInstance(Intro_List.get(position));
    }

    @Override
    public int getCount() {
        return Intro_List.size();
    }


}
