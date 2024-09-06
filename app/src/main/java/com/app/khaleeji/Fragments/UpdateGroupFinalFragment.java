package com.app.khaleeji.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.app.khaleeji.Activity.ChatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.CameraActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.GroupChatUpdateUserAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.databinding.FragmentUpdateGroupFinalBinding;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Constants.AppConstants;
import Constants.SessionData;
import Model.ChatData;
import Model.ChatUserData;
import Model.GroupData;
import Model.GroupInfo;
import Model.LastMessage;
import Model.UserData;
import Utility.ApiClass;
import Utility.ChatHelper;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;

import static android.app.Activity.RESULT_OK;
import static com.app.khaleeji.Activity.MainActivity.REQUEST_ID_MESSAGE_PERMISSIONS;

public class UpdateGroupFinalFragment extends BaseFragment{

    private FragmentUpdateGroupFinalBinding mBinding;
    private String TAG = UpdateGroupFinalFragment.class.getSimpleName();
    private GroupChatUpdateUserAdapter groupChatUpdateUserAdapter;
    private ArrayList<FriendData> mSelFriendList  = new ArrayList<>();
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private String groupImage, groupName;
    private boolean isAdmin = false;
    private String roomId;

    private DatabaseReference databaseGroupReference;
    private FirebaseDatabase database;
    private DatabaseReference postsRef;
    private LastMessage oldLastMessage;
    private List<ChatData> chatDataList = new ArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
//        if (messageEvent.getType() == MessageEvent.MessageType.MSG_IMAGE) {
//            groupImage = messageEvent.getSavedUrl();
//            setFileToUpload(groupImage);
//        }else
        if(messageEvent.getType() == MessageEvent.MessageType.UPDATE_GROUP){
//            mSelFriendList = messageEvent.getFriendData();
//            groupChatUpdateUserAdapter.setData(mSelFriendList);
            for (FriendData friendData: messageEvent.getFriendData()) {
                mSelFriendList.add(friendData);
            }
            groupChatUpdateUserAdapter.setData(mSelFriendList);
        }
    }

    public UpdateGroupFinalFragment(GroupData groupData) {
        finalGroupImgUrl = groupData.getGroupInfo().getGroupImage();
        groupImage = groupData.getGroupInfo().getGroupImage();
        groupName = groupData.getGroupInfo().getGroupName();
        roomId = groupData.getGroupInfo().getRoomId();

        List<UserData> data = groupData.getGroupMembers();
        if(SavePref.getInstance(mActivity).getUserdetail() != null)
            for(int i=0 ; i< data.size(); i++){
//                if(!data.get(i).getUsername().equals(SavePref.getInstance(mActivity).getUserdetail().getUsername())){
                    if(data.get(i).getIsAdmin().equals("1")){
                        if(data.get(i).getUsername().equals(SavePref.getInstance(mActivity).getUserdetail().getUsername())){
                            this.isAdmin = true;
                        }
                    }
                    FriendData friendData = new FriendData();
                    friendData.setUsername(data.get(i).getUsername());
                    friendData.setProfile_picture(data.get(i).profilePic);
                    friendData.setId(Integer.parseInt(data.get(i).getUserId()));
                    mSelFriendList.add(friendData);
//                }
            }
    }

    public  UpdateGroupFinalFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        databaseGroupReference = database.getReference("GroupChatUsers");
        postsRef = database.getReference("GroupPosts");
        //back up
        databaseGroupReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GroupInfo groupInfo = dataSnapshot.child("GroupInfo").getValue(GroupInfo.class);
                if (groupInfo != null && groupInfo.getRoomId().equals(roomId)) {
                    oldLastMessage = dataSnapshot.child("lastMsg").getValue(LastMessage.class);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        postsRef.child(roomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);
                chatDataList.add(chatData);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        credentialsProvider();
        setTransferUtility();
    }

    public void credentialsProvider() {
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                mActivity,
                ApiClass.S3_COGNITO_POOL_ID, // Identity Pool ID
                Regions.AP_SOUTHEAST_1 // Region
        );
        setAmazonS3Client(credentialsProvider);
    }

    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider) {

        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);
        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));

    }

    public void setTransferUtility() {
        transferUtility = new TransferUtility(s3, mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_group_final, container, false);

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

        mBinding.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),
                        CreateGroupChatFragment.newInstance( true, mSelFriendList), mActivity, R.id.framelayout_main);
            }
        });

        if(groupImage != null)
            Picasso.with(mActivity).load(groupImage).placeholder(R.drawable.group_create_camera)
                .error(R.drawable.group_create_camera).fit().into(mBinding.imgCamera);
        mBinding.etGroupSubject.setText(groupName);
//        mBinding.searchEdit.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if ( groupChatUpdateUserAdapter != null) {
//                    groupChatUpdateUserAdapter.getFilter().filter(s);
//                }
//            }
//        });

        groupChatUpdateUserAdapter = new GroupChatUpdateUserAdapter(mActivity, isAdmin,  new GroupChatUpdateUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FriendData friendData, int type) {
                if(type == AppConstants.TYPE_PROFILE){
                    if(friendData.getId().intValue() != SavePref.getInstance(mActivity).getUserdetail().getId().intValue()){
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(friendData.getId()),mActivity, R.id.framelayout_main);
                    }else{
                        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                    }

                }else{
                    if(mSelFriendList.size() > 0){
                        mSelFriendList.remove(friendData);
                    }
                }
                groupChatUpdateUserAdapter.notifyDataSetChanged();
            }
        });
        groupChatUpdateUserAdapter.setData(mSelFriendList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false);
        mBinding.rvProfile.setLayoutManager(layoutManager);
        mBinding.rvProfile.setAdapter(groupChatUpdateUserAdapter);

        mBinding.txtUpdateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGroup();
            }
        });

        mBinding.imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(checkAndRequestPermissions()) {
//                    Intent intent = new Intent(mActivity, CameraActivity.class);
//                    intent.putExtra("isFromGroupChatCreate", true);
//                    startActivity(intent);
//                }

                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImageIntent, 55);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 55) {
//                CropImage.activity(data.getData())
//                        .start(mActivity);
                Uri uri_data = data.getData();
                try {
                    groupImage = getImagePath(uri_data);

                    if (groupImage != null){
                        setFileToUpload(groupImage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void startPhotoZoom(Uri uri)
    {
//        Intent viewMediaIntent = new Intent();
//        viewMediaIntent.setAction(android.content.Intent.ACTION_VIEW);
//        File file = new File("/image/*");
//        viewMediaIntent.setDataAndType(paramUri, "image/*");
//        viewMediaIntent.putExtra("crop","true");
//        viewMediaIntent.putExtra("return-data", true);
//        viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivityForResult(viewMediaIntent,56);

//        Intent localIntent = new Intent("com.android.camera.action.CROP");
//        localIntent.setDataAndType(uri, "image/*");
//        localIntent.putExtra("crop", "true");
//        localIntent.putExtra("aspectX", 1);
//        localIntent.putExtra("aspectY", 1);
//        localIntent.putExtra("outputX", 300);
//        localIntent.putExtra("outputY", 300);
//        localIntent.putExtra("scale", true);
//        localIntent.putExtra("return-data", true);
//        startActivityForResult(localIntent, 56);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = mActivity.getPackageManager().queryIntentActivities( intent, 0 );
        int size = list.size();
        if (size == 0) {
            Toast.makeText(getContext(), "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.setData(uri);
//            intent.putExtra("outputX", 300);
//            intent.putExtra("outputY", 300);
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            if (size == 1) {
                Intent i        = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, 56);
            } else {

            }

        }


    }

    public String getImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mActivity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private void updateGroup(){
        String subject = mBinding.etGroupSubject.getText().toString();
        if(! mBinding.etGroupSubject.getText().toString().isEmpty() && ! finalGroupImgUrl.isEmpty()){
            if ((subject.contains(".")) || (subject.contains("#")) || (subject.contains("$")) || (subject.contains("[")) || (subject.contains("]")) || (subject.contains("/")) ){
                Toast.makeText(mActivity, getString(R.string.invalid_group_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if(mSelFriendList == null || mSelFriendList.size() == 0)
                return;
            ChatHelper helper = new ChatHelper(mActivity);
            helper.updateGroupRoomId(roomId, mSelFriendList, mBinding.etGroupSubject.getText().toString(), finalGroupImgUrl, oldLastMessage,
                    chatDataList);

           ((MainActivity)mActivity).back();
        }else{
            Toast.makeText(mActivity, getString(R.string.no_group_subject), Toast.LENGTH_SHORT).show();
        }
    }

    private  boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.CAMERA);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionRecord = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.RECORD_AUDIO);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (permissionRecord != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            this.requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MESSAGE_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode == REQUEST_ID_MESSAGE_PERMISSIONS){
            Map<String, Integer> permsCamera = new HashMap<>();
            // Initialize the map with both permissions
            permsCamera.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            permsCamera.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            permsCamera.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    permsCamera.put(permissions[i], grantResults[i]);
                // Check for both permissions
                if (permsCamera.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && permsCamera.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && permsCamera.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ) {
                    // process the normal flow
                    Intent intent = new Intent(mActivity, CameraActivity.class);
                    intent.putExtra("isFromGroupChatCreate", true);
                    startActivity(intent);
                }

            }
        }

    }

    private String finalGroupImgUrl = "";
    public void setFileToUpload(String path) {
        try {
            final File tempFile = new File(path);
            final Uri fileUri = Uri.parse(tempFile.toString());
            String key = tempFile.getName();
            if (fileUri != null) {
                TransferObserver transferObserver = transferUtility.upload(
                        ApiClass.S3_BUCKET_NAME,       /* The bucket to upload to */
                        key,                          /* The key for the uploaded object */
                        tempFile                     /* The file where the data to upload exists */
                );
                finalGroupImgUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME + "/" + key;
                ProgressDialog.showProgress(mActivity);
                transferObserverListener(transferObserver);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void transferObserverListener(TransferObserver transferObserver) {
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    try{
//                        Bitmap bm = BitmapFactory.decodeFile(groupImage);
                        Glide.with(mActivity).load(groupImage).into( mBinding.imgCamera);
//                        mBinding.imgCamera.setImageBitmap(bm);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (TransferState.FAILED == state) {
                    finalGroupImgUrl = "";
                }
                ProgressDialog.hideprogressbar();
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                finalGroupImgUrl = "";
                ProgressDialog.hideprogressbar();
            }

        });
    }


}
