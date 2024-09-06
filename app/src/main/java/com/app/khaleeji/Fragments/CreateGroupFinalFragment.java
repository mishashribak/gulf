package com.app.khaleeji.Fragments;

import android.Manifest;
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
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.CameraActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.CreateGroupChatAdapter;
import com.app.khaleeji.Adapter.GroupChatCreateUserAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.databinding.FragmentCreateGroupFinalBinding;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
//import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Constants.AppConstants;
import Constants.Bundle_Identifier;
import Utility.ApiClass;
import Utility.ChatHelper;
import Utility.Fragment_Process;
import Utility.SavePref;

import static android.app.Activity.RESULT_OK;
import static com.app.khaleeji.Activity.MainActivity.REQUEST_ID_MESSAGE_PERMISSIONS;


public class CreateGroupFinalFragment extends BaseFragment{

    private FragmentCreateGroupFinalBinding mBinding;
    private String TAG = CreateGroupFinalFragment.class.getSimpleName();
    private  CreateGroupChatAdapter groupChatAdapter;
    private GroupChatCreateUserAdapter groupChatCreateUserAdapter;
    private ArrayList<FriendData> mSelFriendList ;
    private int total;
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private String groupImage;
    private boolean isFromGroupChat = false;

//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void onMessageEvent (MessageEvent messageEvent) {
//        if (messageEvent.getType() == MessageEvent.MessageType.MSG_IMAGE) {
//            groupImage = messageEvent.getSavedUrl();
//            setFileToUpload(groupImage);
//        }
//    }

    public CreateGroupFinalFragment(ArrayList<FriendData> listFrined, int total) {
        mSelFriendList = listFrined;
        this.total = total;
    }

    public  CreateGroupFinalFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_group_final, container, false);

        View rootView = mBinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private void initView() {
        mBinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).back();
            }
        });

        mBinding.searchEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ( groupChatCreateUserAdapter != null) {
                    groupChatCreateUserAdapter.getFilter().filter(s);
                }
            }
        });
        if(mSelFriendList != null){
            mBinding.txtGroupNum.setText(getString(R.string.group_participant) + " "+ mSelFriendList.size() +" "+
                    (getString(R.string.out_of)+" "+total));
        }

        groupChatCreateUserAdapter = new GroupChatCreateUserAdapter(mActivity, new GroupChatCreateUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FriendData friendData, int type) {
                if(type == AppConstants.TYPE_PROFILE){
                    if(friendData.getId().intValue() != SavePref.getInstance(mActivity).getUserdetail().getId().intValue()){
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(friendData.getId()),mActivity, R.id.framelayout_main);
                    }else{
                        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                    }

                }else{
                    if(mSelFriendList!=null && mSelFriendList.size() > 0){
                        mSelFriendList.remove(friendData);
                    }
                }
                groupChatCreateUserAdapter.notifyDataSetChanged();
            }
        });
        groupChatCreateUserAdapter.setData(mSelFriendList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity,LinearLayoutManager.HORIZONTAL,false);
        mBinding.rvProfile.setLayoutManager(layoutManager);
        mBinding.rvProfile.setAdapter(groupChatCreateUserAdapter);

        mBinding.txtCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               createGroup();
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

                if(checkAndRequestPermissions()){
                    Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickImageIntent, 55);
                }

            }
        });
    }

    private File getImageFile() {
        try {
            String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
            File storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM
                    ), "Camera"
            );
            if(!storageDir.exists())
                storageDir.mkdir();
            File file = File.createTempFile(
                    imageFileName, ".jpg", storageDir
            );
//            currentPhotoPath = "file:" + file.getAbsolutePath();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 55) {
                Uri sourceUri = data.getData();
                File file = getImageFile();
                if(file == null)
                    return;
                Uri destinationUri = Uri.fromFile(file);
                UCrop.of(sourceUri, destinationUri)
                        .start(mActivity, this);

            }
            else if (requestCode == UCrop.REQUEST_CROP) {
                final Uri resultUri = UCrop.getOutput(data);
                if(resultUri != null){
                    setFileToUpload(resultUri);
                }
            }
        }
    }


    private void createGroup(){
        String subject = mBinding.etGroupSubject.getText().toString();
        if(!subject.isEmpty() && ! finalGroupImgUrl.isEmpty()){
            if ((subject.contains(".")) || (subject.contains("#")) || (subject.contains("$")) || (subject.contains("[")) || (subject.contains("]")) || (subject.contains("/")) ){
                Toast.makeText(mActivity, getString(R.string.invalid_group_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if(mSelFriendList == null || mSelFriendList.size() == 0)
                return;
            ChatHelper helper = new ChatHelper(mActivity);
            helper.createGroupRoomId(mSelFriendList, mBinding.etGroupSubject.getText().toString(), finalGroupImgUrl);
            mActivity.getSupportFragmentManager().popBackStack();
            mActivity.getSupportFragmentManager().popBackStack();
        }else{
            Toast.makeText(mActivity, getString(R.string.no_group_subject), Toast.LENGTH_SHORT).show();
        }
    }

    private  boolean checkAndRequestPermissions() {
//        int permissionCamera = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.CAMERA);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int permissionRecord = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.RECORD_AUDIO);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
//        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.CAMERA);
//        }
//
//        if (permissionRecord != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
//        }
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
//            permsCamera.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            permsCamera.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
//            permsCamera.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    permsCamera.put(permissions[i], grantResults[i]);
                // Check for both permissions
//                if (permsCamera.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
//                        && permsCamera.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//                        && permsCamera.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ) {
                if (permsCamera.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // process the normal flow
                    Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickImageIntent, 55);
                }

            }
        }

    }

    private String finalGroupImgUrl = "";
    private Uri localUri;
    public void setFileToUpload(Uri path) {
        try {
            localUri = path;
            final File tempFile = new File(path.getPath());
            final Uri fileUri = Uri.parse(tempFile.toString());
            String key = tempFile.getName();
            if (fileUri != null) {
                TransferObserver transferObserver = transferUtility.upload(
                        ApiClass.S3_BUCKET_NAME,       /* The bucket to upload to */
                        key,                          /* The key for the uploaded object */
                        tempFile                     /* The file where the data to upload exists */
                );
                finalGroupImgUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME + "/" + key;
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
                        mBinding.imgCamera.setImageURI(localUri);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (TransferState.FAILED == state) {
                    finalGroupImgUrl = "";
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                finalGroupImgUrl = "";
            }

        });
    }


}
