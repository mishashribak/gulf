package com.app.khaleeji.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.applozic.mobicomkit.Applozic;
//import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
//import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
//import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
//import com.applozic.mobicomkit.api.account.user.User;
//import com.applozic.mobicomkit.listners.AlLoginHandler;
//import com.applozic.mobicomkit.listners.AlPushNotificationHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.NotficationsSettings;
import com.app.khaleeji.Response.OtpResponse;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.databinding.FragmentLoginBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.Bundle_Identifier;
import CustomView.CustomForgotPwdDlg;
import Model.Android;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends BaseFragment {

    Call<NotficationsSettings> call;
    NotficationsSettings notificationssettings;
    private FragmentLoginBinding mbinding;
    private String TAG = LoginFragment.class.getSimpleName();
    private Context mContext;
    private boolean isForgot = false;
    private FirebaseAuth mAuth;

    public LoginFragment(Context ctx) {
        // Required empty public constructor
        mContext = ctx;
    }

    public LoginFragment(){

    }

    public LoginFragment(Context ctx, boolean isForgot) {
        // Required empty public constructor
        this.isForgot = isForgot;
        mContext = ctx;
    }

    public static LoginFragment newInstance(Context ctx) {
        LoginFragment fragment = new LoginFragment(ctx);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = mActivity;
        ((BaseActivity)mActivity).setStatusBarColor(R.color.primaryDarkColor);
        mAuth = FirebaseAuth.getInstance();
        if(SavePref.getInstance(mActivity).getFirebase_DeviceKey().isEmpty()){
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                SavePref.getInstance(mActivity).save_FirebaseDeviceKey(newToken);
//                Applozic.getInstance(mActivity).setDeviceRegistrationId(newToken);
//                if (MobiComUserPreference.getInstance(mActivity).isRegistered()) {
//                    try {
//                        RegistrationResponse registrationResponse = new RegisterUserClientService(mActivity).updatePushNotificationId(newToken);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            });
        }
//        else{
//            Applozic.getInstance(mActivity).setDeviceRegistrationId(SavePref.getInstance(mActivity).getFirebase_DeviceKey());
//            if (MobiComUserPreference.getInstance(mActivity).isRegistered()) {
//                try {
//                    RegistrationResponse registrationResponse = new RegisterUserClientService(mActivity).updatePushNotificationId(SavePref.getInstance(mActivity).getFirebase_DeviceKey());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        View view = mbinding.getRoot();
        initView();
        return view;
    }

    public boolean checkAndRequestPermissions() {
        int permissionAccessFine = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionAccessCoarse = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionAccessFine != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionAccessCoarse != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            this.requestPermissions( listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),1);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    try{
                        if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                            getCurrentLocation();
                        }
                    }catch (NullPointerException ex){
                        ex.printStackTrace();
                    }

                }
                return;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if(checkAndRequestPermissions()){
                getCurrentLocation();
            }
        }
    }

    public void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener( new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();
                            try{
                                SavePref.getInstance(mActivity).setLatitude(latitude+"");
                                SavePref.getInstance(mActivity).setLongitude(longitude+"");
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void initView() {

        mbinding.rlmain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(mActivity);
                return false;
            }
        });

        mbinding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchsingup();

            }
        });

        mbinding.tvForgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showForget_PasswordDialog(true);
            }
        });

        mbinding.tvForgotUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchForgotUsername();
            }
        });


        mbinding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateSignin();
            }
        });

        if(isForgot){
            showForget_PasswordDialog(true);
        }
    }

    /*function to validate sigin process */
    private void ValidateSignin() {
        if (mbinding.etusername.getText().toString().trim().length() == 0) {
            Toast.makeText(mActivity, getString(R.string.usernameBlank), Toast.LENGTH_SHORT).show();
//            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.usernameBlank), getString(R.string.txt_Done),"", false, null, null);
        } else if (mbinding.etpassword.getText().toString().trim().length() == 0) {
            Toast.makeText(mActivity,getString(R.string.passwordBlank), Toast.LENGTH_SHORT).show();
//            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.passwordBlank), getString(R.string.txt_Done), "", false, null, null);
        } else {
            //run login api here
            if (CheckConnection.isNetworkAvailable(mActivity))
                call_Login("");
            else {
                Toast.makeText(mActivity,getString(R.string.network_down), Toast.LENGTH_SHORT).show();
//                AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.network_down),
//                        getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
            }

        }

    }

    /*showing forget password dialog */
    private void showForget_PasswordDialog(final boolean isForgetpassword) {

        CustomForgotPwdDlg dialog = new CustomForgotPwdDlg(getContext(), new CustomForgotPwdDlg.OnDlgCloseListener() {
            public void onClose(boolean isPhone) {
                launchForgotPassword(isPhone);
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.showDialog();
    }

    //calling  Forgot Password
    private void launchForgotPassword(boolean isPhone) {
        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ForgotPasswordFragment.newInstance(isPhone, mActivity), mActivity, R.id.framelayout_login);
    }

    private void launchForgotUsername() {
        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ForgotPasswordFragment.newInstance(true, true, mActivity), mActivity, R.id.framelayout_login);
    }

    //calling  Signup page
    private void launchsingup() {
        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), RegistrationFragment.newInstance(mActivity), mActivity, R.id.framelayout_login);
    }

    OtpResponse mOtpResponse;
    private void call_Login(String userId) {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = ApiClass.getmApiClass().buildDefaultParams(mActivity);
        mparams.put(ApiClass.getmApiClass().USERNAME, mbinding.etusername.getText().toString().trim());
        mparams.put(ApiClass.getmApiClass().PASSWORD, mbinding.etpassword.getText().toString().trim());
        mparams.put("lat", SavePref.getInstance(mContext).getLatitude()+"");
        mparams.put("lng",SavePref.getInstance(mContext).getLongitude()+"");

        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            mparams.put("language", "ar");
        }else{
            mparams.put("language", "en");
        }


        Call<OtpResponse> call = mApiInterface.getLogin(mparams);

        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
//                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    mOtpResponse = response.body();
                    if (mOtpResponse != null && isAdded()) {
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {
                            mOtpResponse.getUserDetails().setProfilePicture(mOtpResponse.getUserDetails().getProfilePicture());
                            call_GetSetting(mOtpResponse.getUserDetails());
//                                loginToApplozic();
//                            signInFirebase(mOtpResponse.getUserDetails().getEmail(), mbinding.etpassword.getText().toString().trim());
                        } else {
                            ProgressDialog.hideprogressbar();
                            Toast.makeText(mActivity,mOtpResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                            Utility.AlertDialog.showAlert(mActivity, getString(R.string.app_name), mOtpResponse.getMessage(), getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                        }
                    }
                } else {
                    ProgressDialog.hideprogressbar();
                    DebugLog.log(1, TAG, "onResponsee error" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });

    }

    private void signInFirebase(String email, String password) {
        Log.e(TAG, "signInFirebase:" + email);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            FirebaseUser user = mAuth.getCurrentUser();
                            call_GetSetting(mOtpResponse.getUserDetails());
                            Log.e("LoginFragment", "Firebase Success");
                        }

                        if (!task.isSuccessful()) {
                            Log.e("LoginFragment", "Firebase Fail");
                        }

                    }
                });
    }



    //*handle login success response *//*
    private void handleLoginResponse(List<Android> androids) {
        DebugLog.log(1, TAG, androids.get(0).getName().toString());
    }

    //* handle login error response *//*
    private void handleLoginError(Throwable throwable) {
        Toast.makeText(mActivity, TAG + "  Error " + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
//                                call_GetSetting(mOtpResponse.getUserDetails());
//                            }
//
//                            @Override
//                            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
//
//                            }
//                        });
//                    }
//
//                }
//
//                @Override
//                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
//
//
//                }
//            });
//        }else{
//            call_GetSetting(mOtpResponse.getUserDetails());
//        }
    }

    private void call_GetSetting(final UserDetails userDetails) {
//        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = mOtpResponse.getUserDetails().getId().intValue();
        String token = mOtpResponse.getUserDetails().getToken();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USERID, userid);
        mparams.put(ApiClass.getmApiClass().TOKEN, token);
        if (call != null) {
            call.cancel();
            call = null;
        }
        call = mApiInterface.getSettingList(mparams);
        call.enqueue(new Callback<NotficationsSettings>() {
            @Override
            public void onResponse(Call<NotficationsSettings> call, Response<NotficationsSettings> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    notificationssettings = response.body();
                    if (notificationssettings != null && isAdded()) {
                        if (notificationssettings.getStatus().equalsIgnoreCase("deactive")) {
                            Utility.AlertDialog.showAlert(mActivity, getString(R.string.app_name),
                                    getString(R.string.are_you_sure_want_to_activate_your_account),
                                    getString(R.string.txt_Done), getString(R.string.cancel), true,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            call_Login(String.valueOf(userDetails.getId()));
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                        } else {

                            SavePref.getInstance(mActivity).saveUserdetail(mOtpResponse.getUserDetails());
                            if (notificationssettings.getData() != null && notificationssettings.getData().getChatNotification() == 1) {
                                Intent intent = new Intent(mActivity, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

//                                mActivity.overridePendingTransition(0, 0);
//                                mActivity.finish();

                            } else {
                                Intent intent = new Intent(mActivity, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                        }
                    }

                } else {
                    System.out.println(response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<NotficationsSettings> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }

//    private String token;
    public void getFirebaseToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Get new Instance ID token
//                        token = task.getResult().getToken();

                    }
                });
    }

}