package com.app.khaleeji.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.AddMemoryResponse;
import com.squareup.picasso.Picasso;

import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.SessionData;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.ProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

public class CaptionFragment extends Fragment {

    private final String CONTENT_TYPE_DAILY = "daily";
    private final String CONTENT_TYPE_MEMORY = "memory";
    private Boolean isLast = false;


    private ImageView imgBack, imgPicture;
    private TextView txtShare;
    private EditText txtCaption;
    private View view;
    private String content;

    public CaptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_caption, container, false);

        initView();


        return view;
    }

    private void initView() {
        imgBack = view.findViewById(R.id.img_back);
        txtCaption = view.findViewById(R.id.txt_caption);
        txtShare = view.findViewById(R.id.txt_share);
        imgPicture = view.findViewById(R.id.img);

        Picasso.with(getContext()).load(getArguments().getString("url")).
                into(imgPicture, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });

        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map mparams = (Map) getArguments().getSerializable("mparams");
                mparams.put(ApiClass.getmApiClass().caption, txtCaption.getText().toString());

                content = getArguments().getString("content");
                Log.d("hello", "onClick: " + content);

                if(content.equals("both")) {
                    isLast = true;
                    addMemoryApiRequest();

                }else if(content.equals("normal")){
                    addMemoryNewRequest(mparams);
                }
                else addMemoryApiRequest();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void addMemoryApiRequest() {
       /* ProgressDialog.showProgress(getContext());
        final Map mparams = (Map) getArguments().getSerializable("mparams");
        //mparams.replace(ApiClass.getmApiClass().hotspot_id, "");
        mparams.put(ApiClass.getmApiClass().caption, txtCaption.getText().toString());

        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<AddMemoryResponse> call = mApiInterface.addMemoryServiceNew(mparams);
        call.enqueue(new Callback<AddMemoryResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<AddMemoryResponse> call, Response<AddMemoryResponse> response) {
                Log.d("", "onResponse: ");
                if (response.isSuccessful()) {
                    AddMemoryResponse mOtpResponse = response.body();
                    if (mOtpResponse != null) {
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {
                            ProgressDialog.hideprogressbar();

                            if(isLast)
                            {
                                mparams.put(ApiClass.getmApiClass().hotspot_id, "");
                                addMemoryNewRequest(mparams);
                                //return;
                            }else {
                                Toast.makeText(getActivity(), mOtpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                if (isAdded()) {
                                    openMainActivity();
                                    return;
                                }
                            }
                        } else {
                            AlertDialog.showAlert(getActivity(), getString(R.string.app_name),
                                    mOtpResponse.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
                ProgressDialog.hideprogressbar();
            }

            @Override
            public void onFailure(Call<AddMemoryResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });*/

    }

    private void addMemoryNewRequest(Map mparams){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        ProgressDialog.showProgress(getContext());
        //final Map mparams = (Map) getArguments().getSerializable("mparams");
        mparams.put(ApiClass.getmApiClass().caption, txtCaption.getText().toString());

        Call<AddMemoryResponse> call = mApiInterface.addMemoryService(mparams);

        call.enqueue(new Callback<AddMemoryResponse>() {
            @Override
            public void onResponse(Call<AddMemoryResponse> call, Response<AddMemoryResponse> response) {
                ProgressDialog.hideprogressbar();

                if (response.isSuccessful()) {
                    AddMemoryResponse mOtpResponse = response.body();

                    if (mOtpResponse != null) {
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {


                            //if(isLast){
                                Toast.makeText(getContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                                openMainActivity();
                                return;
                            //}

                        }
                    }
                } else {
                    Toast.makeText(getContext(),response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddMemoryResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                //DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }


    private void openMainActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        SessionData.makeIntentAsClearHistory(intent);
        startActivity(intent);
        getActivity().finish();

    }
}
