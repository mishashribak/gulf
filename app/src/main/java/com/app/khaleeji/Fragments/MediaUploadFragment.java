package com.app.khaleeji.Fragments;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.MediaUploadInfoAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentMediaUploadsBinding;
import com.app.khaleeji.services.UploadService;
import com.app.khaleeji.services.VideoUploadService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import Constants.SessionData;
import Model.UploadInfoModel;


public class MediaUploadFragment extends BaseFragment{

    private FragmentMediaUploadsBinding mBinding;
    private String TAG = MediaUploadFragment.class.getSimpleName();
    private MediaUploadInfoAdapter mediaUploadInfoAdapter;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.MessageType.UPLOAD_INFO) {
            mediaUploadInfoAdapter.setData(SessionData.I().uploadInfoModelList);
        }
    }

    public MediaUploadFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_media_uploads, container, false);

        View rootView = mBinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private void initView() {
        ((MainActivity)mActivity).hide();
        mBinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).back();
            }
        });

        mediaUploadInfoAdapter = new MediaUploadInfoAdapter(mActivity, new MediaUploadInfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UploadInfoModel uploadInfoModel) {
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.UPLOAD_CANCEL);
                messageEvent.uploadId = uploadInfoModel.uploadId;
                EventBus.getDefault().post(messageEvent);
            }

            @Override
            public void uploadAgain(UploadInfoModel uploadInfoModel) {
                try{
                    for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                        if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                            SessionData.I().uploadInfoModelList.remove(i);
                            break;
                        }
                    }
                    if(uploadInfoModel.isVideo){
                        ComponentName serviceComponent = new ComponentName(mActivity, VideoUploadService.class);
                        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
                        builder.setMinimumLatency(1 * 1000); // wait at least
                        builder.setOverrideDeadline(3 * 1000); // maximum delay
                        PersistableBundle bundle = uploadInfoModel.bundle;
//                   bundle.putBoolean("isUploadAgain", true);
                        builder.setExtras(bundle);
                        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
                        builder.setRequiresDeviceIdle(true); // device should be idle
                        builder.setRequiresCharging(false); // we don't care if the device is charging or not
                        JobScheduler jobScheduler = mActivity.getSystemService(JobScheduler.class);
                        jobScheduler.schedule(builder.build());
                    }else{
                        ComponentName serviceComponent = new ComponentName(mActivity, UploadService.class);
                        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
                        builder.setMinimumLatency(1 * 1000); // wait at least
                        builder.setOverrideDeadline(3 * 1000); // maximum delay
                        PersistableBundle bundle = uploadInfoModel.bundle;
//                   bundle.putBoolean("isUploadAgain", true);
                        builder.setExtras(bundle);
                        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
                        builder.setRequiresDeviceIdle(true); // device should be idle
                        builder.setRequiresCharging(false); // we don't care if the device is charging or not
                        JobScheduler jobScheduler = mActivity.getSystemService(JobScheduler.class);
                        jobScheduler.schedule(builder.build());
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false);
        mBinding.rvUpload.setLayoutManager(layoutManager);
        mBinding.rvUpload.setAdapter(mediaUploadInfoAdapter);

        mediaUploadInfoAdapter.setData(SessionData.I().uploadInfoModelList);
    }
}
