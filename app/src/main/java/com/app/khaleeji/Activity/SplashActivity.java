package com.app.khaleeji.Activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.app.khaleeji.Fragments.PhoneVerificationFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.databinding.ActivitySplashBinding;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.GlobalVariable;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding mbinding;
    public static final String TAG = "Splash_Activity";
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_);
        handleIntent(getIntent());

        /*FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            GlobalVariable.strDynamicLinkUserId = deepLink.getQueryParameter("userId");
                            Log.e("deepLink", deepLink.toString());
                            Log.e("userId", GlobalVariable.strDynamicLinkUserId);
                        } else {
                            GlobalVariable.strDynamicLinkUserId = null;
                        }
                        if (SavePref.getInstance(SplashActivity.this).getFirebase_DeviceKey().isEmpty())
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                                String newToken = instanceIdResult.getToken();
                                Log.e("newToken", newToken);
                                SavePref.getInstance(SplashActivity.this).save_FirebaseDeviceKey(newToken);
                            });
                        setUpDelay();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });*/
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
            username = appLinkData.getQueryParameter("user");
            Log.e("appLinkData", appLinkData.toString());
        } else {
            GlobalVariable.strDynamicLinkUserId = null;
        }

        if (SavePref.getInstance(SplashActivity.this).getFirebase_DeviceKey().isEmpty())
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                SavePref.getInstance(SplashActivity.this).save_FirebaseDeviceKey(newToken);
            });

        setUpDelay();
    }

    private void callGetUserId(){
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getIdFromUsername(SavePref.getInstance(this).getUserdetail().getId(),
                username);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            if(jsonObject.get("data").getAsJsonObject() != null){
                                GlobalVariable.strDynamicLinkUserId = jsonObject.get("data").getAsJsonObject().get("id").getAsString();
                                Log.e("userId", GlobalVariable.strDynamicLinkUserId);
                                openMainActivity();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    //calling Tutorial or Login Page with delay
    private void setUpDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UserDetails mUserdetail = SavePref.getInstance(SplashActivity.this).getUserdetail();
                if (mUserdetail != null && mUserdetail.getId() != null) {
                    if(username != null){
                        callGetUserId();
                    }else{
                        openMainActivity();
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        }, 500);
    }

    private void openMainActivity(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        if (getIntent().getExtras() != null) {
            String type = getIntent().getExtras().getString("type");
            if(type != null){
                if(type.equals("7")){
                    SavePref.getInstance(getApplicationContext()).setNewMsgNotification(true);
                    intent.putExtra("startFragment", "message");
                }else{
                    SavePref.getInstance(getApplicationContext()).setShowmessageReminder(true);
                    intent.putExtra("startFragment", "NotificationFragments");
                }
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
