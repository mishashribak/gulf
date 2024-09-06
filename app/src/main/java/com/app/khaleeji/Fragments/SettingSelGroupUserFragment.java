package com.app.khaleeji.Fragments;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.Adapter.FamilyGroupAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.FriendlistResponse;
import com.app.khaleeji.databinding.FragmentSelGroupUserBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingSelGroupUserFragment extends BaseFragment{

    private Context contextd;
    private FragmentSelGroupUserBinding mBinding;
    private String TAG = SettingSelGroupUserFragment.class.getSimpleName();
    private FamilyGroupAdapter listAdapter;
    private FriendlistResponse mFriendlistResponse;
    private List<FriendData> mFriendList;
    private ArrayList<FriendData> mSelFriendList = new ArrayList<FriendData>();
    public SettingSelGroupUserFragment() {

    }

    public static SettingSelGroupUserFragment newInstance() {
        SettingSelGroupUserFragment fragment = new SettingSelGroupUserFragment();
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sel_group_user, container, false);

        View rootView = mBinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private void initView() {
        contextd = mActivity;
        mBinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((SettingActivity)mActivity).back();
            }
        });

        listAdapter = new FamilyGroupAdapter(getContext(), new FamilyGroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index, int type) {
                if(type == 0){
                    //selected
                    mSelFriendList.add(mFriendList.get(index));
                }else{
                    if(mSelFriendList.size() > 0)
                        mSelFriendList.remove(mFriendList.get(index));
                }
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvGroup.setLayoutManager(mLayoutManager);
        mBinding.rvGroup.setAdapter(listAdapter);
        mBinding.rvGroup.setIndexBarColor("#00ffffff");
        mBinding.rvGroup.setIndexBarTextColor("#382C70");
        mBinding.rvGroup.setIndexBarStrokeVisibility(false);
        mBinding.rvGroup.setIndexTextSize(12);

       /* mBinding.alphSectionIndex.onSectionIndexClickListener(new Alphabetik.SectionIndexClickListener() {
            @Override
            public void onItemClick(View view, int position, String character) {
                String info = " Position = " + position + " Char = " + character;
                //Log.i("View: ", view + "," + info);
                //Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
                //listView.smoothScrollToPosition(getPositionFromData(character));
            }
        });*/

        mBinding.txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelFriendList != null && mSelFriendList.size() > 0)
                    Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), SettingCreateNewGroupFragment.newInstance(mSelFriendList),mActivity, R.id.framelayout_main);
            }
        });

        getMyfriendList();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void getMyfriendList() {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().REQUEST_TYPE, "myfriends");
        mparams.put(ApiClass.getmApiClass().USER_ID, String.valueOf(userid));
        String token = SavePref.getInstance(mActivity).getUserdetail().getToken();
        Call<FriendlistResponse> call = mApiInterface.friendRequestList("Bearer "+token,mparams);
        call.enqueue(new Callback<FriendlistResponse>() {
            @Override
            public void onResponse(Call<FriendlistResponse> call, Response<FriendlistResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        mFriendlistResponse = response.body();
                        if(mFriendlistResponse != null) {
                            if (mFriendlistResponse.getStatus().equalsIgnoreCase("true")) {
                                if (mFriendlistResponse.getData() != null && mFriendlistResponse.getData().getData().size() > 0) {
                                    mFriendList = mFriendlistResponse.getData().getData();
                                    listAdapter.setData(mFriendList);
                                    mBinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    mBinding.txtNoData.setVisibility(View.VISIBLE);
                                }
                            } else {
                                AlertDialog.showAlert(mActivity, getString(R.string.app_name), mFriendlistResponse.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                            }
                        }

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<FriendlistResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }

}
