package com.app.khaleeji.Fragments;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentIntroBinding;

import Constants.Bundle_Identifier;
import Model.TutorialModel;

public class IntroFragment extends BaseFragment {

    private TutorialModel mTutorialModel;
    private FragmentIntroBinding mbinding;

    public IntroFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IntroFragment.
     */
    public static IntroFragment newInstance(TutorialModel mTutorialModel) {
        IntroFragment fragment = new IntroFragment();
        Bundle args = new Bundle();
        args.putParcelable(Bundle_Identifier.Tutorial_data, mTutorialModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTutorialModel = getArguments().getParcelable(Bundle_Identifier.Tutorial_data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false);
        View view = mbinding.getRoot();
        initView();
        return view;
    }

    /*set Background button and text of tutorial*/
    private void initView() {
        if (mTutorialModel != null) {
            mbinding.ivBackground.setImageResource(mTutorialModel.getBackground());
            mbinding.ivIcon.setImageResource(mTutorialModel.getTitle_icon());
            mbinding.tvTitle.setText(mTutorialModel.getTitle());
            mbinding.tvDiscription.setText(mTutorialModel.getDescription());
            mbinding.btnGetstarted.setText(mTutorialModel.getButton_title());
            mbinding.btnGetstarted.setBackgroundResource(mTutorialModel.getButton_color());
            if (mTutorialModel.is_shownext() == 1) {
                //show button
                mbinding.btnGetstarted.setVisibility(View.VISIBLE);
                mbinding.btStarted.setVisibility(View.GONE);
            }else{
                mbinding.btStarted.setVisibility(View.VISIBLE);
                mbinding.btnGetstarted.setVisibility(View.GONE);
            }

            mbinding.btStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                    mActivity.finish();
                }
            });
            mbinding.btnGetstarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                    mActivity.finish();
                }
            });
        }
    }
}