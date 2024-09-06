package com.app.khaleeji.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.MyStatusUpdatesAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentMyStatusUpdatesBinding;


public class MyStatusUpdatesFragment  extends BaseFragment {

    private Context mContext;
    private FragmentMyStatusUpdatesBinding mbinding;

    public MyStatusUpdatesFragment() {

    }

    public static MyStatusUpdatesFragment newInstance() {
        MyStatusUpdatesFragment fragment = new MyStatusUpdatesFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_status_updates, container, false);
        View rootView = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private void initView(){
        ((MainActivity)mActivity).hide();
        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ((MainActivity)mActivity).back();
            }
        });

        MyStatusUpdatesAdapter listAdapter = new MyStatusUpdatesAdapter(getContext(), new MyStatusUpdatesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index, int type) {
//                if(type == AppConstants.TYPE_LIKES)
//                    ((MainActivity)mActivity).openPostLikesFragment();
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mbinding.rvMyStatusUpdates.setLayoutManager(mLayoutManager);
        mbinding.rvMyStatusUpdates.setAdapter(listAdapter);
    }

}
