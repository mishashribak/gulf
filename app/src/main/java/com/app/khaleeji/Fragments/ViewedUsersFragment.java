package com.app.khaleeji.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.ViewedUsersAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.MediaViewsData;
import com.app.khaleeji.Response.MediaViewsListResponse;
import com.app.khaleeji.databinding.FragmentViewedUsersBinding;
import java.util.List;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewedUsersFragment extends BaseFragment{

    static String TAG = ViewedUsersFragment.class.getName();
    private FragmentViewedUsersBinding mbinding;
    private ViewedUsersAdapter mViewersAdapter;
    private int mediaId;
    private  List<MediaViewsData> listMediaViewers;

    public ViewedUsersFragment(){

    }

    public ViewedUsersFragment(int mediaId){
        this.mediaId = mediaId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_viewed_users, container, false);
        View view = mbinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            view.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return view;
    }

    private void initView(){
        ((MainActivity)mActivity).hide();

        mViewersAdapter = new ViewedUsersAdapter(mActivity, new ViewedUsersAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int pos){
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(listMediaViewers.get(pos).getUser_id()),
                        mActivity, R.id.framelayout_main);
            }});

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mbinding.rvViewers.setLayoutManager(linearLayoutManager);

        mbinding.rvViewers.setAdapter(mViewersAdapter);
        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).back();
            }
        });
        mbinding.imgSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openSearchTabFragment();
            }
        });
        mbinding.imgMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMessageFragment();
            }
        });

        callGetViewers();
    }

    private void callGetViewers(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        Call<MediaViewsListResponse> call = mApiInterface.getMediaViews(mediaId);

        call.enqueue(new Callback<MediaViewsListResponse>() {
            @Override
            public void onResponse(Call<MediaViewsListResponse> call, Response<MediaViewsListResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful())
                {
                    MediaViewsListResponse mediaViewsListResponse = response.body();
                    if(mediaViewsListResponse!=null ){
                        if (mediaViewsListResponse.getStatus().equalsIgnoreCase("true")) {
                            listMediaViewers = mediaViewsListResponse.getData();
                            if(listMediaViewers != null && listMediaViewers.size() > 0){
                                mbinding.txtNoData.setVisibility(View.GONE);
                                mViewersAdapter.setData(listMediaViewers);
                            }else{
                               mbinding.txtNoData.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<MediaViewsListResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }
}
