package com.app.khaleeji.Activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.app.khaleeji.Adapter.TutorialAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.ActivityIntroBinding;

import java.util.ArrayList;

import Model.TutorialModel;

public class IntroActivity extends BaseActivity {

    private ActivityIntroBinding mbinding;
    private TutorialAdapter mTutorialAdapter;
    private ArrayList<TutorialModel> Intro_List = new ArrayList<TutorialModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_intro_);
        setUpAdaper();
    }

    //code for adding intro screen into viewpage
    private void setUpAdaper() {

        mTutorialAdapter = new TutorialAdapter(getSupportFragmentManager(), Intro_List);
        mbinding.vpIntro.setAdapter(mTutorialAdapter);

        Intro_List.add(new TutorialModel(R.mipmap.intro_5, R.mipmap.meetup_new_icon, getString(R.string.intro5_title), getString(R.string.intro5_description), getString(R.string.intro_button_title), R.drawable.rectangle_box_pink, 0));
        Intro_List.add(new TutorialModel(R.mipmap.intro_6, R.mipmap.camera_new_icon, getString(R.string.nav_gsnap), getString(R.string.intro_description), getString(R.string.intro_button_title), R.drawable.rectangle_box_radish, 0));
        Intro_List.add(new TutorialModel(R.mipmap.intro_khaleeji, R.mipmap.funnel_icon, getString(R.string.intro2_title), getString(R.string.intro2_description), getString(R.string.intro_button_title), R.drawable.rectangle_box_dark_purple, 0));
        Intro_List.add(new TutorialModel(R.mipmap.intro_4, R.drawable.intro_home, getString(R.string.timeline), getString(R.string.intro4_description), getString(R.string.intro_button_title), R.drawable.rectangle_box_radish, 0));
       // Intro_List.add(new TutorialModel(R.mipmap.meetup_new_intro, R.mipmap.timeline_new_icon, getString(R.string.timeline), getString(R.string.intro4_description), getString(R.string.intro_button_title), R.drawable.rectangle_box_blue, 1));
        Intro_List.add(new TutorialModel(R.drawable.intro_new_last, 0, "", "", getString(R.string.intro_button_title), R.drawable.rectangle_box_blue, 1));

        //**********************

        mTutorialAdapter.notifyDataSetChanged();
        mbinding.indicator.setViewPager(mbinding.vpIntro);

    }


}
