package com.app.khaleeji.Activity;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;

import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.Fragments.CameraKitFragment;
import com.app.khaleeji.Fragments.CropFragment;
import com.app.khaleeji.Fragments.PhotoEditorFragment;
import com.app.khaleeji.Fragments.SendToFragment;
import com.app.khaleeji.Fragments.VideoEditorFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.ActivityCameraBinding;
//import com.applozic.mobicomkit.Applozic;
//import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
//import com.applozic.mobicomkit.api.account.user.User;
//import com.applozic.mobicomkit.listners.AlLoginHandler;
//import com.applozic.mobicomkit.listners.AlPushNotificationHandler;
import com.otaliastudios.cameraview.CameraOptions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import Constants.SessionData;
import Model.ZipCategoryData;
import Utility.Fragment_Process;
import Utility.SavePref;

public class CameraActivity extends BaseActivity {

    private ActivityCameraBinding mbinding;
    private boolean isFromChat= false;
    private boolean isFromGroupChatCreate = false;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.SEND_TO){
            openSendToFragment(messageEvent.getBundle());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_camera);
        setStatusBarColor(R.color.black_trans);

        if (getIntent().getExtras() != null) {
            isFromChat = getIntent().getExtras().getBoolean("isFromChat") ;
            isFromGroupChatCreate = getIntent().getExtras().getBoolean("isFromGroupChatCreate") ;
        }

        SessionData.I().localData.getZipCategoryDataList().clear();


        //trending
        ZipCategoryData zipCategoryData1 = new ZipCategoryData();
        List<Integer> stickerDrawableList1 = new ArrayList<>();
        for( int i =9; i< 35; i++){
            stickerDrawableList1.add( getResources().getIdentifier("trending" + i,"drawable",
                    getPackageName()));

        }
        zipCategoryData1.setStickerDrawableList(stickerDrawableList1);
        SessionData.I().localData.setZipCategory(zipCategoryData1);

        //word
        ZipCategoryData zipCategoryData = new ZipCategoryData();
        List<Integer> stickerDrawableList = new ArrayList<>();

        for( int i =1; i< 90; i++){
            stickerDrawableList.add( getResources().getIdentifier("word" + i,"drawable",
                    getPackageName()));
        }
        zipCategoryData.setStickerDrawableList(stickerDrawableList);
        SessionData.I().localData.setZipCategory(zipCategoryData);

        //nationalism
        ZipCategoryData zipCategoryData4 = new ZipCategoryData();
        List<Integer> stickerDrawableList4 = new ArrayList<>();
        for( int i =1; i< 18; i++){
            stickerDrawableList4.add( getResources().getIdentifier("nationalism" + i,"drawable",
                    getPackageName()));

        }
        zipCategoryData4.setStickerDrawableList(stickerDrawableList4);
        SessionData.I().localData.setZipCategory(zipCategoryData4);

        //sports
        ZipCategoryData zipCategoryData2 = new ZipCategoryData();
        List<Integer> stickerDrawableList2 = new ArrayList<>();
        for( int i =1; i< 27; i++){
            stickerDrawableList2.add( getResources().getIdentifier("sports" + i,"drawable",
                    getPackageName()));

        }
        zipCategoryData2.setStickerDrawableList(stickerDrawableList2);
        SessionData.I().localData.setZipCategory(zipCategoryData2);

        //spiritual
        ZipCategoryData zipCategoryData3 = new ZipCategoryData();
        List<Integer> stickerDrawableList3 = new ArrayList<>();
        for( int i =1; i< 7; i++){
            stickerDrawableList3.add( getResources().getIdentifier("spiritual" + i,"drawable",
                    getPackageName()));

        }
        zipCategoryData3.setStickerDrawableList(stickerDrawableList3);
        SessionData.I().localData.setZipCategory(zipCategoryData3);

        Fragment_Process.replaceFragment(getSupportFragmentManager(), new CameraKitFragment(this, isFromChat, isFromGroupChatCreate), this, mbinding.framelayoutMain.getId());
    }

    public void openSendToFragment(Bundle bundle){
        Fragment_Process.replaceFragment(getSupportFragmentManager(),new SendToFragment(), bundle,mbinding.framelayoutMain.getId(), false);
    }

    public void openPhotoEditorFragment(Bundle bundle){
        PhotoEditorFragment photoEditorFragment = new PhotoEditorFragment(this);
        Fragment_Process.replaceFragment(getSupportFragmentManager(), photoEditorFragment ,bundle, R.id.framelayout_main);
    }

    public void openCropFragment(Bundle bundle){
        CropFragment cropFragment = new CropFragment();
        Fragment_Process.replaceFragment(getSupportFragmentManager(), cropFragment, bundle, mbinding.framelayoutMain.getId(), false);
    }

    public void back(){
        int backstack_count = getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = getCurrentFragment();
        if(fragment instanceof VideoEditorFragment || fragment instanceof  PhotoEditorFragment){
            return;
        }
        if(fragment instanceof CameraKitFragment){
            finish();
            return;
        }

        getSupportFragmentManager().popBackStack();

    }

    public Fragment getCurrentFragment() {
        try {
            int index = this.getSupportFragmentManager().getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry backEntry = this.getSupportFragmentManager().getBackStackEntryAt(index);
            String tag = backEntry.getName();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        back();
    }
}
