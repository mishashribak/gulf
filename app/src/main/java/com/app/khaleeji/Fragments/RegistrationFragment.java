package com.app.khaleeji.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.OtpResponse;
import com.app.khaleeji.databinding.FragmentSignupBinding;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Constants.Bundle_Identifier;
import CustomView.CustomCountryCodeDialog;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.Permission;
import Utility.ProgressDialog;
import Utility.SavePref;
import Utility.Utils;
import Utility.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

public class RegistrationFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = RegistrationFragment.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    //variables for current locations
    private FragmentSignupBinding mbinding;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private double longitude;
    private double latitude;
    private CustomCountryCodeDialog mCountryCodeDlg;
    private Context mContext;
    private String mStrCountryName;

    public RegistrationFragment(Context ctx) {
        // Required empty public constructor
        mContext = ctx;
    }

    public static RegistrationFragment newInstance(Context ctx) {
        RegistrationFragment fragment = new RegistrationFragment(ctx);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.primaryDarkColor);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);
        View view = mbinding.getRoot();

        accessLocation();

        initView();
        return view;
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, mActivity, 0).show();
            return false;
        }
    }

    private void accessLocation() {
        if (!isGooglePlayServicesAvailable()) {
            Toast.makeText(mActivity, "Google play service error", Toast.LENGTH_SHORT).show();
            return;
        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        if (Permission.checkLocationPermision(mActivity)) {
            isGpsUnable();
        }

    }

    private int GPS_RESULT = 10;
    private boolean isGpsUnable() {
        LocationManager service = (LocationManager) mActivity.getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, GPS_RESULT);
        }
        return enabled;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      if (requestCode == GPS_RESULT) {
            if (resultCode == RESULT_OK) {
                createLocationRequest();
            }
        } else if (requestCode == Permission.ACCESSLOCATION) {
          if (resultCode == RESULT_OK) {
              if (isGpsUnable()) {
                  createLocationRequest();
              }

          } else {
              Toast.makeText(mActivity, "Please enable Request form setting", Toast.LENGTH_SHORT).show();

          }
      }
    }
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void initView() {

        mStrCountryName = getResources().getString(R.string.saudi);
        mCountryCodeDlg = new CustomCountryCodeDialog(mActivity, new CustomCountryCodeDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                mbinding.txtCountryCode.setText(AppConstants.COUNTRY_CODE[pos]);
                mbinding.imgFlag.setImageDrawable(getResources().getDrawable(AppConstants.FLAGS[pos]));
                mStrCountryName = getResources().getString(AppConstants.COUNTRY_NAME[pos]);
            }
        });
        mCountryCodeDlg.setCanceledOnTouchOutside(false);

        mbinding.llCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryCodeDlg.showDialog();
            }
        });

        mbinding.rlmain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(mActivity);
                return false;
            }
        });

        mbinding.etusername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mbinding.etusername.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mbinding.etfullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mbinding.etfullname.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mbinding.etmobileno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mbinding.etmobileno.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mbinding.etemailaddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mbinding.etemailaddress.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mbinding.etverifypassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mbinding.etverifypassword.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mbinding.etpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mbinding.etpassword.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mbinding.tvsignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.getSupportFragmentManager().popBackStack();
                launchLogin();
            }
        });

        mbinding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(),PhoneVerificationFragment.newInstance(true),mActivity, R.id.framelayout_login);
                validateSignup();
            }
        });

        mbinding.tvReadTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openURL(ApiClass.GULF_TERMS_URL, mActivity);
            }
        });
    }

    private void redMarkingInEditTextFields(EditText editText, String message) {
        editText.requestFocus();
        editText.setText("");
        editText.setHint(message);
        editText.setHintTextColor(getResources().getColor(R.color.red));
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

    /*Method to register user on server */
    private void validateSignup() {
        String username = mbinding.etusername.getText().toString().trim();
        if (mbinding.etfullname.getText().toString().trim().length() == 0)
            redMarkingInEditTextFields(mbinding.etfullname, getString(R.string.fullNameBlank));
        else if (!Validation.isValidFullname(mbinding.etfullname.getText().toString())){
            redMarkingInEditTextFields(mbinding.etfullname, getString(R.string.invalid_fullname));
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.invalid_fullname_title));
            builder.setMessage(getString(R.string.invalid_fullname));
            builder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();

        } else if (mbinding.etfullname.getText().toString().trim().length() > 50) {
            redMarkingInEditTextFields(mbinding.etfullname, getString(R.string.fullNamelength));
        } else if (mbinding.etemailaddress.getText().toString().trim().length() == 0) {
            redMarkingInEditTextFields(mbinding.etemailaddress, getString(R.string.EmailBlank));
        } else if (Validation.isValidEmail(mbinding.etemailaddress.getText().toString().trim()) == false) {
            redMarkingInEditTextFields(mbinding.etemailaddress, getString(R.string.EmailValidation));
        } else if (mbinding.etemailaddress.getText().toString().trim().length() > 50) {
            redMarkingInEditTextFields(mbinding.etemailaddress, getString(R.string.Emaillength));
        }  else if (mbinding.etmobileno.getText().toString().trim().length() == 0) {
            redMarkingInEditTextFields(mbinding.etmobileno, getString(R.string.MobilenoBlank));
        } else if (Validation.isValidPhone(mbinding.etmobileno.getText().toString().trim()) == false) {
            redMarkingInEditTextFields(mbinding.etmobileno, getString(R.string.invalid_phone));
        }else if ((mbinding.etmobileno.getText().toString().trim().length() < 8) ||
                (mbinding.etmobileno.getText().toString().trim().length() > 16)) {
            redMarkingInEditTextFields(mbinding.etmobileno, getString(R.string.MobileValidation));
        } else if (mbinding.etusername.getText().toString().trim().length() == 0) {
            redMarkingInEditTextFields(mbinding.etusername, getString(R.string.usernameBlank));
        }else if (!Validation.isValidUsername(mbinding.etusername.getText().toString())){
            redMarkingInEditTextFields(mbinding.etusername, getString(R.string.invalid_username));
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.invalid_username_title));
            builder.setMessage(getString(R.string.invalid_username));
            builder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();

        } else if (mbinding.etusername.getText().toString().trim().length() > 20) {
            redMarkingInEditTextFields(mbinding.etusername, getString(R.string.usernamelength));
        } else if (mbinding.etpassword.getText().toString().trim().length() == 0) {
            Toast.makeText(mActivity, getString(R.string.passwordBlank), Toast.LENGTH_SHORT).show();
//            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.passwordBlank),
//                    getString(R.string.txt_Done),
//                    getString(R.string.cancel), false, null, null);
        } else if (mbinding.etpassword.getText().toString().trim().length() < 6 ||
                mbinding.etpassword.getText().toString().length() > 20) {
            Toast.makeText(mActivity, getString(R.string.passwordnotlong), Toast.LENGTH_SHORT).show();
//            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.passwordnotlong),
//                    getString(R.string.txt_Done),
//                    getString(R.string.cancel), false, null, null);
        } else if (mbinding.etverifypassword.getText().toString().trim().length() == 0) {
            Toast.makeText(mActivity, getString(R.string.verifypasswordBlanck), Toast.LENGTH_SHORT).show();
//            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.verifypasswordBlanck),
//                    getString(R.string.txt_Done),
//                    getString(R.string.cancel), false, null, null);
        } else if (!mbinding.etverifypassword.getText().toString()
                .equalsIgnoreCase(mbinding.etpassword.getText().toString().trim())) {
            Toast.makeText(mActivity, getString(R.string.passwordnotmatch), Toast.LENGTH_SHORT).show();
//            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.passwordnotmatch),
//                    getString(R.string.txt_Done),
//                    getString(R.string.cancel), false, null, null);

        } else if (!mbinding.cbReadTerms.isChecked()) {
            Toast.makeText(mActivity, getString(R.string.plsReadTerms), Toast.LENGTH_SHORT).show();
//            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.plsReadTerms),
//                    getString(R.string.txt_Done),
//                    getString(R.string.cancel), false, null, null);
        } else if (!mbinding.cbAcceptTerms.isChecked()) {
            Toast.makeText(mActivity, getString(R.string.plsAcceptTerms), Toast.LENGTH_SHORT).show();
//            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.plsAcceptTerms), getString(R.string.txt_Done),
//                    getString(R.string.cancel), false, null, null);
        } else {
            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.disclaimer_text),
                    getResources().getString(R.string.bt_continue), getResources().getString(R.string.disagree), true, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callOtpSignupApi();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
    }


    private void callOtpSignupApi() {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USERNAME, mbinding.etusername.getText().toString().trim());
        mparams.put(ApiClass.getmApiClass().EMAIL, mbinding.etemailaddress.getText().toString().trim());
        mparams.put(ApiClass.getmApiClass().MOBILE_NUMBER, mbinding.etmobileno.getText().toString().trim());
        mparams.put(ApiClass.getmApiClass().COUNTRY_ID, mbinding.txtCountryCode.getText().toString().trim());

        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            mparams.put("language", "ar");
        }else{
            mparams.put("language", "en");
        }


        Call<OtpResponse> call = mApiInterface.getOtp(mparams);
        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    OtpResponse otpResponse = response.body();
                    if (otpResponse != null && isAdded()) {
                        if (otpResponse.getStatus().equalsIgnoreCase("true")) {
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new PhoneVerificationFragment(true, otpResponse.getUserDetails(),
                                            mbinding.txtCountryCode.getText().toString().trim(),
                                            mStrCountryName,
                                            mbinding.etfullname.getText().toString().trim(),
                                            mbinding.etpassword.getText().toString().trim(),
                                            latitude + "", longitude + ""),mActivity, R.id.framelayout_login);
                        } else {
                            Toast.makeText(mActivity, otpResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name), otpResponse.getMessage(),
//                                    getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                        }
                    }
                } else {
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


    private void launchLogin() {
        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), LoginFragment.newInstance(mActivity), mActivity, R.id.framelayout_login);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }



    private void getCurrentLocation() {

        //Creating a location object
        if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            //Getting longitude and latitude

            longitude = location.getLongitude();
            latitude = location.getLatitude();
            String msg = latitude + ", " + longitude;

            //Creating a LatLng Object to store Coordinates
            LatLng latLng = new LatLng(latitude, longitude);

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            Log.d(TAG, "Location update stopped .......................");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG, "Location update latitude ......................." + latitude + "," + longitude);
            SavePref.getInstance(mActivity).setLatitude(""+latitude);
            SavePref.getInstance(mActivity).setLongitude(""+longitude);
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}