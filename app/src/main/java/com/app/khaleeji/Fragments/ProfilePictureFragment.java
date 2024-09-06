package com.app.khaleeji.Fragments;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.applozic.mobicomkit.Applozic;
//import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
//import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
//import com.applozic.mobicomkit.api.account.user.User;
//import com.applozic.mobicomkit.listners.AlLoginHandler;
//import com.applozic.mobicomkit.listners.AlPushNotificationHandler;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.FriendDailiesViewActivity;
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.OtpResponse;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.databinding.FragmentProfilePictureBinding;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.Bundle_Identifier;
import CustomView.SelectPicPopupWindow;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import Utility.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class ProfilePictureFragment extends BaseFragment{

    static String TAG = ProfilePictureFragment.class.getName();
    private Context mContext;
    private FragmentProfilePictureBinding mbinding;

    private String mStrSelGender;
    private String mStrSelBirthday;
    private String mStrSelHomeTown;
    private String mStrSelQuestions;
    private boolean mIsAllowedDOB;
    private SelectPicPopupWindow selectPicPopupWindow;
    private View mRootView;
    private String strImagepath, strBkImagePath;
    private ChooseGenderFragment.SignUpData mSignUpData;
    private FirebaseAuth mAuth;
    private boolean isProfileFg = true;
    private OtpResponse mOtpResponse;

    public ProfilePictureFragment(ChooseGenderFragment.SignUpData signUpData, String strSelGender, String strSelBirthday, boolean isAllowedDOB, String strSelHomeTown, String strSelQuestions){
        mStrSelGender = strSelGender;
        mStrSelBirthday = strSelBirthday;
        mStrSelHomeTown = strSelHomeTown;
        mStrSelQuestions = strSelQuestions;
        mIsAllowedDOB = isAllowedDOB;
        mSignUpData = signUpData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;

        mAuth = FirebaseAuth.getInstance();

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_picture, container, false);
        mRootView = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            mRootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void initView(){
        mbinding.imgProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                isProfileFg = true;
                openSelectDlg();
            }
        });

        mbinding.imgProfileBk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isProfileFg = false;
                openSelectDlg();
            }
        });

        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((LoginActivity)mActivity).back();
            }
        });

        mbinding.txtSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                callSignupApi();
            }
        });

    }


    private void callSignupApi() {

        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        RequestBody fullname = RequestBody.create(MediaType.parse("text/plain"), mSignUpData.mStrFullName);
        RequestBody username = RequestBody.create(MediaType.parse("text/plain"),mSignUpData.mUserDetails.getUsername());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), mSignUpData.mUserDetails.getEmail());
        RequestBody mobileNo = RequestBody.create(MediaType.parse("text/plain"),mSignUpData.mUserDetails.getMobileNumber());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain") ,mSignUpData.mStrPassword);
        RequestBody countryId = RequestBody.create(MediaType.parse("text/plain"), mSignUpData.mStrCountryId);
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "ANDROID");
        RequestBody deviceId = RequestBody.create(MediaType.parse("text/plain"), SavePref.getInstance(mContext).getFirebase_DeviceKey());
        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), mSignUpData.mStrLat);
        RequestBody lng = RequestBody.create(MediaType.parse("text/plain"), mSignUpData.mStrLng);
        RequestBody allowDob = RequestBody.create(MediaType.parse("text/plain"), mIsAllowedDOB? "1":  "0");
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), mStrSelBirthday);
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), mStrSelGender);
        RequestBody question = RequestBody.create(MediaType.parse("text/plain"), mStrSelQuestions);
        RequestBody country = RequestBody.create(MediaType.parse("text/plain"), mStrSelHomeTown);

//        String countryName = "";
//        if(mSignUpData.mStrCountryId.contains("973")){
//            countryName = "Bahrain";
//        } else  if(mSignUpData.mStrCountryId.contains("964")){
//            countryName = "Iraq";
//        }else  if(mSignUpData.mStrCountryId.contains("965")){
//            countryName = "Kuwait";
//        }else  if(mSignUpData.mStrCountryId.contains("968")){
//            countryName = "Oman";
//        }else  if(mSignUpData.mStrCountryId.contains("974")){
//            countryName = "Qatar";
//        }else  if(mSignUpData.mStrCountryId.contains("966")){
//            countryName = "Saudi Arabia";
//        }else  if(mSignUpData.mStrCountryId.contains("971")){
//            countryName = "United Arab Emirates";
//        }else  if(mSignUpData.mStrCountryId.contains("967")){
//            countryName = "Yemen";
//        }



        MultipartBody.Part body = null;
        if(strImagepath!=null && new File(strImagepath).exists())
        {
            File file = new File(strImagepath);
            RequestBody requestFile =RequestBody.create(MediaType.parse("multipart/form-data"), file);
            // MultipartBody.Part is used to send also the actual file name
            body = MultipartBody.Part.createFormData(ApiClass.getmApiClass().PROFILE_PICTURE, file.getName(), requestFile);
        }

        MultipartBody.Part bodyBg = null;
        if(strBkImagePath!=null && new File(strBkImagePath).exists())
        {
            File file = new File(strBkImagePath);
            RequestBody requestFile =RequestBody.create(MediaType.parse("multipart/form-data"), file);
            // MultipartBody.Part is used to send also the actual file name
            bodyBg = MultipartBody.Part.createFormData("bg_picture", file.getName(), requestFile);
        }

        RequestBody lang;
        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            lang = RequestBody.create(MediaType.parse("text/plain"), "ar");
        }else{
            lang = RequestBody.create(MediaType.parse("text/plain"), "en");
        }

        Call<OtpResponse> call = mApiInterface.getsignup(
                fullname, username, email,mobileNo,password,countryId, deviceType,deviceId,lat,
                lng,allowDob, dob, gender, question,country ,body, bodyBg, lang);
        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
//                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    mOtpResponse = response.body();
                    if (isAdded() && mOtpResponse != null) {
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {

                            ProgressDialog.hideprogressbar();
                            SavePref.getInstance(mActivity).saveUserdetail(mOtpResponse.getUserDetails());
                            Intent intent = new Intent(mActivity, MainActivity.class);
                            intent.putExtra("startFragment", "ProfileFragment");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

//                            createAccount(mOtpResponse.getUserDetails().getEmail(), mSignUpData.mStrPassword);
//                            loginToApplozic();
                        } else {
                            ProgressDialog.hideprogressbar();
                            Toast.makeText(mActivity,mOtpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    ProgressDialog.hideprogressbar();
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }

    private void loginToApplozic(){

//        if(!Applozic.isConnected(mActivity)){
//            User user = new User();
//            user.setUserId(mOtpResponse.getUserDetails().getId()+""); //userId it can be any unique user identifier NOTE : +,*,? are not allowed chars in userId.
//            user.setDisplayName(mOtpResponse.getUserDetails().getFullName()); //displayName is the name of the user which will be shown in chat messages
//            user.setEmail(mOtpResponse.getUserDetails().getEmail()); //optional
//            user.setImageLink(ApiClass.ImageBaseUrl + mOtpResponse.getUserDetails().getProfilePicture());
//            user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());  //User.AuthenticationType.APPLOZIC.getValue() for password verification from Applozic server and User.AuthenticationType.CLIENT.getValue() for access Token verification from your server set access token as password
//
//            Applozic.connectUser(mActivity, user, new AlLoginHandler() {
//                @Override
//                public void onSuccess(RegistrationResponse registrationResponse, Context context) {
//                    if(MobiComUserPreference.getInstance(context).isRegistered()) {
//                        Applozic.registerForPushNotification(context, Applozic.getInstance(mActivity).getDeviceRegistrationId(), new   AlPushNotificationHandler() {
//                            @Override
//                            public void onSuccess(RegistrationResponse registrationResponse) {
//                                SavePref.getInstance(mActivity).saveUserdetail(mOtpResponse.getUserDetails());
//                                Intent intent = new Intent(mActivity, MainActivity.class);
//                                intent.putExtra("startFragment", "ProfileFragment");
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                            }
//
//                            @Override
//                            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
//
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
//
//
//                }
//            });
//        }else{
//            SavePref.getInstance(mActivity).saveUserdetail(mOtpResponse.getUserDetails());
//            Intent intent = new Intent(mActivity, MainActivity.class);
//            intent.putExtra("startFragment", "ProfileFragment");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            ProgressDialog.hideprogressbar();
                            SavePref.getInstance(mActivity).saveUserdetail(mOtpResponse.getUserDetails());
                            Intent intent = new Intent(mActivity, MainActivity.class);
                            intent.putExtra("startFragment", "ProfileFragment");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }

                    }
                });
    }

    private void openVideoFragment(UserDetails userDetails) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFromSignUp", true);
        bundle.putString("userName", mSignUpData.mUserDetails.getUsername());
        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(),new VideoFragment(), bundle, R.id.framelayout_login, false);
    }

    private void openSelectDlg(){
        View view = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_layout_camera, null);
        selectPicPopupWindow = new SelectPicPopupWindow(view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btCancel:
                        selectPicPopupWindow.dismiss();
                        break;
                    case R.id.btCamera:
                        selectPicPopupWindow.dismiss();
                        openCamera();
                        break;
                    case R.id.btGallery:
                        selectPicPopupWindow.dismiss();
                        openGallery();
                        break;
                }
            }
        }, 1);
        selectPicPopupWindow.showAtLocation(mRootView, Gravity.CENTER, 0, 0);
    }

    private Uri imageUri;
    public void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = mActivity.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 1);
    }

    public void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 2);
    }

    private void captureImageResult(File compressedFile) {
        try {
            if(isProfileFg){
                strImagepath = compressedFile.getPath();
            }else{
                strBkImagePath = compressedFile.getPath();
            }
            if(isProfileFg){
                Glide.with(mActivity).load(compressedFile.getPath()).centerCrop().into( mbinding.imgProfile);
            }else{
                Glide.with(mActivity).load(compressedFile.getPath()).centerCrop().into( mbinding.imgProfileBk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == 1){
                Uri sourceUri = imageUri;
                File file = getImageFile();
                if(file == null)
                    return;
                Uri destinationUri = Uri.fromFile(file);
                if(isProfileFg){
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(1,1)
                            .start(mActivity, this);
                }else{
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(16, 9)
                            .start(mActivity, this);
                }
            }else if (requestCode == 2 && data != null) {
                Uri sourceUri = data.getData();
                File file = getImageFile();
                if(file == null)
                    return;
                Uri destinationUri = Uri.fromFile(file);
                if(isProfileFg){
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(1,1)
                            .start(mActivity, this);
                }else{
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(16, 9)
                            .start(mActivity, this);
                }

            }else if (requestCode == UCrop.REQUEST_CROP) {
                final Uri resultUri = UCrop.getOutput(data);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            mActivity.getContentResolver(), resultUri);
                    int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                    File compressedFile = Utils.createFileFromBitmap(scaledBitmap, mContext);
                    captureImageResult(compressedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File getImageFile() {
        try {
            String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
            File storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM
                    ), "Camera"
            );
            if (!storageDir.exists())
                storageDir.mkdir();
            File file = File.createTempFile(
                    imageFileName, ".jpg", storageDir
            );
//            String currentPhotoPath = "file:" + file.getAbsolutePath();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
