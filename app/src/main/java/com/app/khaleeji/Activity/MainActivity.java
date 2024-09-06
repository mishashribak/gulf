package com.app.khaleeji.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.NonNull;

//import com.applozic.mobicomkit.Applozic;
//import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
//import com.applozic.mobicomkit.api.account.user.User;
//import com.applozic.mobicomkit.listners.AlLoginHandler;
//import com.applozic.mobicomkit.listners.AlLogoutHandler;
//import com.applozic.mobicomkit.listners.AlPushNotificationHandler;
//import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.app.khaleeji.Fragments.FindFriendFragment;
import com.app.khaleeji.Fragments.MyFriendFragment;
import com.app.khaleeji.Fragments.NearByUserFragment;
import com.app.khaleeji.Fragments.WhereFragment;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.app.khaleeji.Adapter.NearByUserOnMapAdapter;
import com.app.khaleeji.Adapter.HotspotListAdapter;
import com.app.khaleeji.Adapter.LocationListAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.Fragments.HomeFragment;
import com.app.khaleeji.Fragments.HotSpotDetailsFragment;
import com.app.khaleeji.Fragments.MeetUpHotspotFragments;
import com.app.khaleeji.Fragments.MsgContactFragment;
import com.app.khaleeji.Fragments.NotificationFragments;
import com.app.khaleeji.Fragments.ProfileEditFragment;
import com.app.khaleeji.Fragments.ProfileFragment;
import com.app.khaleeji.Fragments.SearchTabFragments;
import com.app.khaleeji.Fragments.SendToFragment;
import com.app.khaleeji.Fragments.FriendProfileFragment;
import com.app.khaleeji.Fragments.UpdateGroupFinalFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.GulfProfiledata;
import com.app.khaleeji.Response.HotSpotDatum;
import com.app.khaleeji.Response.HotspotUpdateResponse;
import com.app.khaleeji.Response.Logout_Response;
import com.app.khaleeji.Response.NearByResponse;
import com.app.khaleeji.Response.NearUserDetail;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.ZipDownloadUtills.ExtrectZipFile;
import com.app.khaleeji.ZipDownloadUtills.ZipDownloadServices;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.SessionData;
import CustomView.CustomLocationDlg;
import CustomView.CustomMindDlg;
import CustomView.CustomTextView;
import CustomView.CustomUserGroupLocatonDlg;
import CustomView.Video.VideoCache;
import Interfaces.OnDlgCloseListener;
import Model.CameraUpdates;
import Model.GroupData;
import Model.ZipData;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.GlobalVariable;
import Utility.Permission;
import Utility.ProgressDialog;
import Utility.SavePref;
import Utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static Constants.AppConstants.loadedHomeAllTab;
import static Constants.AppConstants.loadedHomeFriendTab;
import static Constants.AppConstants.loadedNotificationTab;
import static Constants.AppConstants.loadedProfileTab;
import static Utility.Permission.MY_PERMISSIONS_REQUEST_CAMERA;

public class MainActivity extends BaseActivity implements View.OnClickListener, OnDlgCloseListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private CustomTextView txtMenuFullName;
    private de.hdodenhof.circleimageview.CircleImageView imgMenuCircleProfile;
    private ImageView imgMenuProfile;
    public DrawerLayout drawer;
    private ImageView iv_navedit;
    private LinearLayout llmeetup, llnav_myfriends, llnav_addfriends, llnav_setting,llHome, llnav_logout, llnav_about, llnav_feedback;
    private CustomTextView txtNoData;
    private ImageView ivtabHome;
    private ImageView ivtabLocation;
    private ImageView ivtabPlus;
    private ImageView ivtabNotification;
    private ImageView ivtabUser;
    private View statusBar;
    private View statusBarYellow;
    private ImageView mImgCheckedInMark;
    private View viewBlackTransMask;
    private MediaPlayer mOpenPlayer, mClosePlayer;
    private CustomTextView txtViewAccount;
    private FloatingActionMenu actionMenu;
    private FirebaseAuth mAuth;
    private ImageView imgBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoCache.getInstance(this).getSimpleCache();

        mAuth = FirebaseAuth.getInstance();
        String deviceLocale = Locale.getDefault().getLanguage();
        if (deviceLocale.equalsIgnoreCase("ar")) {
            SavePref.getInstance(this).setLang("ar");
        } else {
            SavePref.getInstance(this).setLang("en");
        }
        drawer = findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        txtNoData = findViewById(R.id.txtNoData);
        String device_key = SavePref.getInstance(MainActivity.this).getFirebase_DeviceKey();
        Log.e(TAG, "device_key : " + device_key);
        initView();
    }

    private void initView() {
        imgBadge = findViewById(R.id.imgBadge);
        mOpenPlayer = MediaPlayer.create(MainActivity.this, R.raw.open_plus);
        mClosePlayer = MediaPlayer.create(MainActivity.this, R.raw.close_plus);
        setupNavigationView();
        accessLocation();

        if(GlobalVariable.strDynamicLinkUserId != null){
            Log.e("userIdMain", GlobalVariable.strDynamicLinkUserId);
            int userId = Integer.parseInt(GlobalVariable.strDynamicLinkUserId);
            if(userId == SavePref.getInstance(this).getUserdetail().getId()){
                openProfileFragment();
            }else{
                openFriendProfileFragment(userId);
            }
            GlobalVariable.strDynamicLinkUserId = null;
            return;
        }
        final Intent intent = getIntent();

        if (intent.getExtras() != null) {
            if (intent.getExtras().getString("startFragment") == null)
                return;
            if (intent.getExtras().getString("startFragment").equals("ProfileFragment")) {
                openProfileFragment();
            }
            else if(intent.getExtras().getString("startFragment").equals("FriendProfileFragment")) {
                openFriendProfileFragment(intent.getExtras().getInt("userId"));
            }else if(intent.getExtras().getString("startFragment").equals("UpdateGroupFinalFragment")){
                GroupData groupData = (GroupData) intent.getSerializableExtra("groupData");
                openUpdateGroupFinalFragment(groupData);
            }else if(intent.getExtras().getString("startFragment").equals("NotificationFragments")){
                imgBadge.setVisibility(View.GONE);
                SavePref.getInstance(this).setShowmessageReminder(false);
                Fragment_Process.replaceFragment(getSupportFragmentManager(), NotificationFragments.newInstance(), MainActivity.this, R.id.framelayout_main);
            }else if(intent.getExtras().getString("startFragment").equals("message")){
                Fragment_Process.replaceFragment(getSupportFragmentManager(), MsgContactFragment.newInstance(), MainActivity.this, R.id.framelayout_main);
            }
        }else{
            hideCheckedInMark();
            showMeetupObjects();
            setStatusBarColor(R.color.darkPurple);
            openMeetupFragment(false);
        }
    }

    private void setupNavigationView() {
        showFloatingActionButtons();
        viewBlackTransMask = findViewById(R.id.viewBlackTransMask);
        iv_navedit = findViewById(R.id.iv_navedit);
        txtMenuFullName = findViewById(R.id.txtMenuFullName);
        imgMenuCircleProfile = findViewById(R.id.imgMenuCircleProfile);
        imgMenuProfile = findViewById(R.id.imgMenuProfile);
        llmeetup = findViewById(R.id.llmeetup);
        llHome = findViewById(R.id.llHome);
        llnav_myfriends = findViewById(R.id.llnav_myfriends);
        llnav_addfriends = findViewById(R.id.llnav_addfriends);
        llnav_setting = findViewById(R.id.llnav_setting);
        llnav_about = findViewById(R.id.llnav_aboutUs);
        llnav_feedback = findViewById(R.id.llnav_feedback_contact_us);
        llnav_logout = findViewById(R.id.llnav_logout);
        ivtabHome = findViewById(R.id.ivtab_home);
        ivtabLocation = findViewById(R.id.ivtab_location);
        ivtabPlus = findViewById(R.id.ivtab_plus);
        ivtabNotification = findViewById(R.id.ivtab_notification);
        ivtabUser = findViewById(R.id.ivtab_user);
        statusBar = findViewById(R.id.statusBar);
        statusBarYellow = findViewById(R.id.statusBarYellow);
        mImgCheckedInMark = findViewById(R.id.imgCheckedInMark);
        txtViewAccount = findViewById(R.id.txtViewAccount);

        ivtabHome.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    sendTopScrollNotification();
                    return super.onDoubleTap(e);
                }

            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });

        ivtabLocation.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    sendTopScrollNotification();
                    return super.onDoubleTap(e);
                }

            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });


        ivtabUser.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    sendTopScrollNotification();
                    return super.onDoubleTap(e);
                }

            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });

        UserDetails userDetails = SavePref.getInstance(this).getUserdetail();
        if(userDetails.getProfilePicture() != null && ! userDetails.getProfilePicture().isEmpty()){
            Picasso.with(this).load(ApiClass.ImageBaseUrl + userDetails.getProfilePicture()).fit().centerInside().into(imgMenuCircleProfile);
        }
        if(userDetails.getBg_picture() != null && ! userDetails.getBg_picture().isEmpty()){
            Picasso.with(this).load(ApiClass.ImageBaseUrl + userDetails.getBg_picture()).fit().centerInside().into(imgMenuProfile);
        }
        txtMenuFullName.setText(userDetails.getUsername());

        txtViewAccount.setOnClickListener(this);
        mImgCheckedInMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        viewBlackTransMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actionMenu != null){
                    actionMenu.close(true);
                    ivtabPlus.setImageDrawable(getResources().getDrawable(R.drawable.selector_plus));
                    hideBlackTransMask();
                }
            }
        });

        iv_navedit.setOnClickListener(this);
        llmeetup.setOnClickListener(this);
        llHome.setOnClickListener(this);
        llnav_myfriends.setOnClickListener(this);
        llnav_addfriends.setOnClickListener(this);
        llnav_setting.setOnClickListener(this);
        llnav_about.setOnClickListener(this);
        llnav_feedback.setOnClickListener(this);
        llnav_logout.setOnClickListener(this);
        iv_navedit.setOnClickListener(this);
        ivtabHome.setOnClickListener(this);
        ivtabLocation.setOnClickListener(this);
        ivtabPlus.setOnClickListener(this);
        ivtabNotification.setOnClickListener(this);
        ivtabUser.setOnClickListener(this);
    }

    private void sendTopScrollNotification(){
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setType(MessageEvent.MessageType.TOP_SCROLL);
        EventBus.getDefault().post(messageEvent);
    }

    private void accessLocation() {
        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(this, "Google play service error", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void updateProfile(){
        UserDetails userDetails = SavePref.getInstance(this).getUserdetail();
        if(userDetails.getProfilePicture() != null && ! userDetails.getProfilePicture().isEmpty()){
            Picasso.with(this).load(ApiClass.ImageBaseUrl + userDetails.getProfilePicture()).fit().centerInside().into(imgMenuCircleProfile);
        }
        if(userDetails.getBg_picture() != null && ! userDetails.getBg_picture().isEmpty()){
            Picasso.with(this).load(ApiClass.ImageBaseUrl + userDetails.getBg_picture()).fit().centerInside().into(imgMenuProfile);
        }

        txtMenuFullName.setText(userDetails.getUsername());
    }

    public void openFriendProfileFragment(int userId){
        CloseDrawer();
        Fragment_Process.replaceFragment(getSupportFragmentManager(),new FriendProfileFragment(userId),this, R.id.framelayout_main);
    }

    private void openUpdateGroupFinalFragment(GroupData groupData){
        CloseDrawer();
        Fragment_Process.replaceFragment(getSupportFragmentManager(), new UpdateGroupFinalFragment(groupData),this, R.id.framelayout_main);
    }

    public void openHomeFragment(){
        CloseDrawer();
        Fragment_Process.replaceFragment(getSupportFragmentManager(), HomeFragment.newInstance(), MainActivity.this, R.id.framelayout_main);
    }

    public void openProfileFragment(){
        CloseDrawer();
        Fragment_Process.replaceFragment(getSupportFragmentManager(), ProfileFragment.newInstance(), MainActivity.this, R.id.framelayout_main);
    }

    public void openMyFriends(){
        CloseDrawer();
        Fragment_Process.replaceFragment(getSupportFragmentManager(), new MyFriendFragment(), MainActivity.this, R.id.framelayout_main);
    }


    public void openEditProfileFragment(GulfProfiledata data){
        CloseDrawer();
        Fragment_Process.replaceFragment(getSupportFragmentManager(), ProfileEditFragment.newInstance(data), MainActivity.this, R.id.framelayout_main);
    }

    public void openFindFriends() {
        CloseDrawer();
        Fragment_Process.replaceFragment(getSupportFragmentManager(), new FindFriendFragment(), MainActivity.this, R.id.framelayout_main);
    }

        public void openSearchTabFragment(){
        CloseDrawer();
        Fragment mCurrentFragment = getCurrentFragment();
        if (mCurrentFragment != null && (!(mCurrentFragment instanceof SearchTabFragments))) {
            Fragment_Process.replaceFragment(getSupportFragmentManager(), new SearchTabFragments(), this, R.id.framelayout_main);
        }
    }

    public void openMeetupFragment(boolean isShowMemoryDesc){
        CloseDrawer();
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            boolean isPermission = Permission.checkPermissionCamera(MainActivity.this);
            if (isPermission) {
                Fragment_Process.replaceFragment(getSupportFragmentManager(), MeetUpHotspotFragments.newInstance(), MainActivity.this, R.id.framelayout_main);
                if(isShowMemoryDesc){
                    showMindDialog(true);
                }
                accessLocation();
            }
        }else {
            Fragment_Process.replaceFragment(getSupportFragmentManager(), MeetUpHotspotFragments.newInstance(), MainActivity.this, R.id.framelayout_main);
            if(isShowMemoryDesc){
                showMindDialog(true);
            }
            accessLocation();
        }
    }

    public void openSendToFragment(boolean isFromMindDlg){
        CloseDrawer();
        Fragment_Process.replaceFragment(getSupportFragmentManager(),new SendToFragment(isFromMindDlg),MainActivity.this, R.id.framelayout_main);
    }

    public void openMessageFragment(){
        CloseDrawer();
        Fragment_Process.replaceFragment(getSupportFragmentManager(), MsgContactFragment.newInstance(), this, R.id.framelayout_main);
//        startActivity(new Intent(this, ConversationActivity.class));
    }

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final int REQUEST_ID_STORAGE_PERMISSIONS = 2;
    public static final int REQUEST_ID_MESSAGE_PERMISSIONS = 3;

    private boolean checkStoragePermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ID_STORAGE_PERMISSIONS);
            return  false;
        }
        return true;
    }

    private  boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionRecord = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
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
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_STORAGE_PERMISSIONS:
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialogOK("Storage Permission required for this app",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            checkStoragePermission();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // proceed with logic by disabling the related features or quit the app.
                                            break;
                                    }
                                }
                            });
                }
                //permission is denied (and never ask again is  checked)
                //shouldShowRequestPermissionRationale will return false
                else {
                    Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                            .show();
                    //                            //proceed with logic by disabling the related features or quit the app.
                }

            return;
            case REQUEST_ID_MULTIPLE_PERMISSIONS:

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ) {
                        // process the normal flow
                        openCameraActivity();
                        //else any one or both the permissions are not granted
                    }
//                    else {
//                        Log.d(TAG, "Some permissions are not granted ask again ");
//                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
////                        // shouldShowRequestPermissionRationale will return true
//                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                            showDialogOK("SMS and Location Services Permission required for this app",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            switch (which) {
//                                                case DialogInterface.BUTTON_POSITIVE:
//                                                    checkAndRequestPermissions();
//                                                    break;
//                                                case DialogInterface.BUTTON_NEGATIVE:
//                                                    // proceed with logic by disabling the related features or quit the app.
//                                                    break;
//                                            }
//                                        }
//                                    });
//                        }
//                        //permission is denied (and never ask again is  checked)
//                        //shouldShowRequestPermissionRationale will return false
//                        else {
//                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
//                                    .show();
//                            //                            //proceed with logic by disabling the related features or quit the app.
//                        }
//                    }
                }
            return;

            case MY_PERMISSIONS_REQUEST_CAMERA:
                Map<String, Integer> perm = new HashMap<>();
                perm.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perm.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perm.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perm.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perm.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perm.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
//                perm.put(Manifest.permission.WRITE_SETTINGS, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perm.put(permissions[i], grantResults[i]);
                    if (perm.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perm.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perm.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && perm.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && perm.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        Fragment_Process.replaceFragment(getSupportFragmentManager(), MeetUpHotspotFragments.newInstance(), MainActivity.this, R.id.framelayout_main);
                        accessLocation();
                    }

                }
                return;
        }
    }

    public void openCameraActivity(){
        startActivity(new Intent(MainActivity.this, CameraActivity.class));
    }

    public void openCamera(){
        if(checkAndRequestPermissions()) {
            openCameraActivity();
        }
    }

    int nFileCount = 0;
    public void downloadZipAndExtrect(String path, String zip_url, final String destinationPath) {

        File file = new File(path);
        if(file.exists()) {
            Log.d("PhotoEditorSDK", "downloadZipAndExtrect: " + "File Already exist: " + path);
        }else {
                ZipDownloadServices download =
                    new ZipDownloadServices(path, this, new ZipDownloadServices.PostDownload() {
                        @Override
                        public void downloadDone(File fd) {
                            ExtrectZipFile unzip = new ExtrectZipFile();
                            try {
                                unzip.unzip(fd.getAbsolutePath(), destinationPath);
//                                addDelay();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("PhotoEditorSDK", "error: " + e.getLocalizedMessage());
                            }

                        }
                    }
                );
            download.execute(zip_url);
        }

    }

    public void hideCheckedInMark(){
        mImgCheckedInMark.setVisibility(View.GONE);
    }

    public void onClose(){
        if(getCurrentFragment() instanceof MeetUpHotspotFragments){
            resumeMeetupObjects();
        }
    }

    private LocationListAdapter locationListAdpater;
    private List<HotSpotDatum>  mListHotspot;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        /*if(messageEvent.getType() == MessageEvent.MessageType.WHERE_ARE_YOU){
            mListHotspot = messageEvent.getListHotspotData();
            if(mListHotspot != null){
                if(isFirstDlg){
                    locationListAdpater = new LocationListAdapter(mListHotspot
                            , this, new LocationListAdapter.OnLocationClickListener() {
                        @Override
                        public void onLocationClick(List<HotSpotDatum> dataList, int index) {
                            dlgLocation.disMissDialog();
                            isFirstDlg = true;
                            Fragment_Process.replaceFragment(getSupportFragmentManager(), HotSpotDetailsFragment.newInstance(dataList.get(index)),MainActivity.this, R.id.framelayout_main);
//                            openHotSpotDetailsFragment(dataList.get(index));
                        }
                    });
                    dlgLocation = new CustomLocationDlg(this, locationListAdpater, new CustomLocationDlg.LocationImgItemClick() {
                        @Override
                        public void onLocationItemClick(int categoryId, String category) {
                            mStrCategory = category;
                            isFirstDlg = false;
                            locationListAdpater.setData(null);
                            callGetHotspotApi();
                        }
                    });
                    dlgLocation.setOnCloseListener(this);
                    dlgLocation.setCanceledOnTouchOutside(false);
                    dlgLocation.showDialog();
                    if(mListHotspot.size() == 0)
                        dlgLocation.showNoData();
                    else
                        dlgLocation.hideNoData();
                }else if(dlgLocation.isShowing()){
                    locationListAdpater.setData(mListHotspot);
                    if(mListHotspot.size() == 0)
                        dlgLocation.showNoData();
                    else
                        dlgLocation.hideNoData();
                }
            }
        }*/

        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG){
            imgBadge.setVisibility(View.VISIBLE);
        }
    }


    CustomMindDlg customMindDlg;
    public void showMindDialog(boolean isMemory){
        customMindDlg  = new CustomMindDlg(this, isMemory, new CustomMindDlg.ItemClickInterface() {
            @Override
            public void onMindClick() {
                hideMeetupObjects();
                hideCheckedInMark();
                //is from mind dialog
                openSendToFragment(true);
            }

            @Override
            public void onMemoryDescClick() {

            }
        });
        customMindDlg.setCanceledOnTouchOutside(false);
        customMindDlg.setOnCloseListener(this);
        customMindDlg.showDialog();
    }

    private NearByResponse mNearByResponseModel;
    private List<NearUserDetail> mListNearUserDetail = null;

    public void callSearchNearBy(float radius, double centerLat, double centerLng){
        ProgressDialog.showProgress(this);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        int userid = SavePref.getInstance(this).getUserdetail().getId();

        mparams.put(ApiClass.getmApiClass().USER_ID, userid);
        mparams.put(ApiClass.getmApiClass().Lat, centerLat);
        mparams.put(ApiClass.getmApiClass().LNG, centerLng);
        mparams.put("radius", radius);
        Call<NearByResponse> call = mApiInterface.getNearByProfiles(mparams);
        call.enqueue(new Callback<NearByResponse>() {
            @Override
            public void onResponse(Call<NearByResponse> call, Response<NearByResponse> response) {
                ProgressDialog.hideprogressbar();

                if (response.isSuccessful()) {
                    boolean isNoData = false;
                    mNearByResponseModel = response.body();
                    if (mNearByResponseModel != null && mNearByResponseModel.getData() != null && mNearByResponseModel.getData().getData().size() > 0) {
                        isNoData = false;
                        mListNearUserDetail = mNearByResponseModel.getData().getData();
                    } else {
                        isNoData = true;
                    }

                    showNearByUserDlg ( isNoData );

                } else {
                    System.out.println(response.errorBody());
//                    showNearByUserDlg ( true );
                }

            }

            @Override
            public void onFailure(Call<NearByResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                showNearByUserDlg ( true );
            }
        });
    }

    private void showNearByUserDlg( boolean isNoData){
        Fragment_Process.replaceFragment(getSupportFragmentManager(), new NearByUserFragment(nearByUserAdapter), this, R.id.framelayout_main);
        nearByUserAdapter.setData(mListNearUserDetail);
    }

    private NearByUserOnMapAdapter nearByUserAdapter;
    public void showGroupUserDlg(float radius, double centerLat, double centerLng){
        nearByUserAdapter = new NearByUserOnMapAdapter(this, new NearByUserOnMapAdapter.OnGroupUserClickListener() {
            @Override
            public void onGroupUserClick(int index) {
                if(mListNearUserDetail.get(index).getId().intValue() != SavePref.getInstance(MainActivity.this).getUserdetail().getId().intValue()){
                    Fragment_Process.replaceFragment(getSupportFragmentManager(),new FriendProfileFragment(mListNearUserDetail.get(index).getId().intValue()),MainActivity.this, R.id.framelayout_main);
                }else{
                    Fragment_Process.replaceFragment(MainActivity.this.getSupportFragmentManager(), ProfileFragment.newInstance(), MainActivity.this, R.id.framelayout_main);
                }

            }
        });
        callSearchNearBy(radius,  centerLat, centerLng);
    }

    public void showFloatingActionButtons(){
        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.size_25);
        FloatingActionButton.LayoutParams params = new FloatingActionButton.LayoutParams(redActionButtonSize, redActionButtonSize);
        params.setMargins(0,0,0,33);
        FloatingActionButton floatingActionButton = new FloatingActionButton.Builder(this).setBackgroundDrawable(R.drawable.rounded_box_trans)
                .setLayoutParams(params)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER).build();

        // create the menu items
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        int subActionButtonSize = getResources().getDimensionPixelSize(R.dimen.size_35);

        SubActionButton dailyActionButton = itemBuilder.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.floating_daily))
                .setLayoutParams(new FloatingActionButton.LayoutParams(subActionButtonSize, subActionButtonSize)).build();

        SubActionButton memoryActionButton = itemBuilder.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.floating_memory))
                .setLayoutParams(new FloatingActionButton.LayoutParams(subActionButtonSize, subActionButtonSize)).build();

        SubActionButton whereActionButton = itemBuilder.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.where))
                .setLayoutParams(new FloatingActionButton.LayoutParams(subActionButtonSize, subActionButtonSize)).build();

        SubActionButton mindActionButton = itemBuilder.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.mind))
                .setLayoutParams(new FloatingActionButton.LayoutParams(subActionButtonSize, subActionButtonSize)).build();

        int redActionButtonRadius = getResources().getDimensionPixelSize(R.dimen.size_80);
        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(memoryActionButton)
                .addSubActionView(dailyActionButton)
                .addSubActionView(whereActionButton)
                .addSubActionView(mindActionButton)
                .setStartAngle(230)
                .setEndAngle(310)
                .setRadius(redActionButtonRadius)
                .attachTo(floatingActionButton)
                .build();

        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                ivtabPlus.setImageDrawable(getResources().getDrawable(R.drawable.close_yellow));
                mOpenPlayer.start();
                showBlackTransMask();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
                ivtabPlus.setImageDrawable(getResources().getDrawable(R.drawable.selector_plus));
                hideBlackTransMask();
                mClosePlayer.start();
            }
        });

        dailyActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                ivtabPlus.setImageDrawable(getResources().getDrawable(R.drawable.selector_plus));
                hideBlackTransMask();
                GlobalVariable.isFloatingDaily = true;
                openCamera();
            }
        });

        memoryActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                ivtabPlus.setImageDrawable(getResources().getDrawable(R.drawable.selector_plus));
                hideBlackTransMask();
                GlobalVariable.isFloatingDaily = false;
                openCamera();
            }
        });

        whereActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.replaceFragment(getSupportFragmentManager(), new WhereFragment(), MainActivity.this, R.id.framelayout_main);
            }
        });

        // set OnClickListener
        mindActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                ivtabPlus.setImageDrawable(getResources().getDrawable(R.drawable.selector_plus));
                hideBlackTransMask();
                showMindDialog(false);
            }
        });
    }

    public void openMenu(){
        try{
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View view, float v) {
                    hideSoftKeyboard();
                }

                @Override
                public void onDrawerOpened(View view) {
                    hideSoftKeyboard();
                }

                @Override
                public void onDrawerClosed(View view) {
                    Intent intent = new Intent("icon_show");
                    intent.putExtra("is_show", true);
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                    // your refresh code can be called from here
                }

                @Override
                public void onDrawerStateChanged(int i) {

                }
            });
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                Intent intent = new Intent("icon_show");
                intent.putExtra("is_show", false);
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                drawer.openDrawer(GravityCompat.START);
            }
        }catch (Exception ex){
//            Crashlytics.log(Log.DEBUG, "MainActivity.openMenu", ex.toString());
        }
    }

    public void hide(){
        hideMeetupObjects();
        hideCheckedInMark();
    }

    public void hideMeetupObjects(){
        statusBar.setVisibility(View.GONE);
        statusBarYellow.setVisibility(View.VISIBLE);
    }

    public void resumeMeetupObjects(){
        statusBar.setVisibility(View.VISIBLE);
        statusBarYellow.setVisibility(View.GONE);
    }

    public void showMeetupObjects(){
        statusBar.setVisibility(View.VISIBLE);
        statusBarYellow.setVisibility(View.GONE);
    }

    public void showBlackTransMask(){
        viewBlackTransMask.setVisibility(View.VISIBLE);
    }

    public void hideBlackTransMask(){
        viewBlackTransMask.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivtab_notification:
                CloseDrawer();
                Fragment_Process.clearBackStack(getSupportFragmentManager());
                imgBadge.setVisibility(View.GONE);
                SavePref.getInstance(this).setShowmessageReminder(false);
                Fragment_Process.replaceFragment(getSupportFragmentManager(), NotificationFragments.newInstance(), MainActivity.this, R.id.framelayout_main);
                break;
            case R.id.txtViewAccount:
            case R.id.ivtab_user:
                Fragment_Process.clearBackStack(getSupportFragmentManager());
                openProfileFragment();
                break;
            case R.id.ivtab_home:
            case R.id.llHome:
                Fragment_Process.clearBackStack(getSupportFragmentManager());
                openHomeFragment();
                break;
            case R.id.ivtab_location:
            case R.id.llmeetup:
                Fragment_Process.clearBackStack(getSupportFragmentManager());
                openMeetupFragment(false);
                break;
            case R.id.llnav_myfriends:
                CloseDrawer();
                openMyFriends();
                break;

            case R.id.llnav_addfriends:
                CloseDrawer();
                openFindFriends();
//                openSearchTabFragment();
                break;

            case R.id.llnav_setting:
                CloseDrawer();

                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;

            case R.id.llnav_aboutUs:
                Utils.openURL("https://khaleejiapp.com/", MainActivity.this);
                break;

            case R.id.llnav_feedback_contact_us:
                Utils.openURL("https://khaleejiapp.com/contact-us/", MainActivity.this);
                break;

            case R.id.llnav_logout:
                CloseDrawer();
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.app_name));
                builder.setMessage(getString(R.string.logout_text));
                builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logout();

                    }
                });
                builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.show();

                break;
        }
    }

    private void logout(){
        ProgressDialog.showProgress(this);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        Call<Logout_Response> call = mApiInterface.logout(SavePref.getInstance(this).getUserdetail().getId());

        call.enqueue(new Callback<Logout_Response>() {
            @Override
            public void onResponse(Call<Logout_Response> call, Response<Logout_Response> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    Logout_Response logout_response = response.body();
                    if (logout_response != null) {
                        if (logout_response.getStatus().equalsIgnoreCase("true")) {
                            call_Logout();
                        } else {
                            Toast.makeText(MainActivity.this, logout_response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    DebugLog.log(1, TAG, "onResponsee error" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Logout_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }

    private void call_Logout() {
        mAuth.signOut();
        finish();
        SavePref.getInstance(MainActivity.this).clearPref();
        loadedHomeFriendTab = false;
        loadedHomeAllTab = false;
        loadedProfileTab = false;
        loadedNotificationTab = false;
        startActivity(new Intent(this, LoginActivity.class));

//        Applozic.logoutUser(this, new AlLogoutHandler() {
//            @Override
//            public void onSuccess(Context context) {
//                finish();
//                SavePref.getInstance(MainActivity.this).clearPref();
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            }
//
//            @Override
//            public void onFailure(Exception exception) {
//
//            }
//        });

    }

    private void CloseDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onBackPressed() {
        back();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SavePref.getInstance(this).getShowRemider()){
            imgBadge.setVisibility(View.VISIBLE);
        }else{
            imgBadge.setVisibility(View.GONE);
        }
    }

    public void back(){
        hideSoftKeyboard();
        int backstack_count = getSupportFragmentManager().getBackStackEntryCount();
        if (backstack_count <= 1) {
            if (getIntent().getExtras() != null) {
                finish();
            }
        } else if (backstack_count > 1) {
            getSupportFragmentManager().popBackStack();
        }
    }
}


