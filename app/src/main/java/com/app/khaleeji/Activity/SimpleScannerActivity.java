package com.app.khaleeji.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.app.khaleeji.Adapter.SendAnswerAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.CustomSenderAnswerDlg;
import Model.QAData;
import Utility.ApiClass;
import Utility.ProgressDialog;
import Utility.SavePref;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleScannerActivity extends BaseActivity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;
    private ArrayList<QAData> dataQAList;
    private String answer="", question;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);   // Set the scanner view as the content view


//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("userId", "1592");
//            jsonObject.put("username", SavePref.getInstance(this).getUserdetail());
//            jsonObject.put("question", "what is name of app?|$@#GULFLINK-NHZ#@$|why you use app?");
//
//            JSONObject obj = new JSONObject(jsonObject.toString());
//            String userId = obj.getString("userId");
//            question = obj.getString("question");
//            if(!question.isEmpty()){
//                openSendAnswerDlg(question, userId);
//            }else{
//                callSendFriendRequest(userId);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        mScannerView.resumeCameraPreview(this);
        String json = rawResult.getContents();
        Log.e("QRJson", json);
        try {
            JSONObject obj = new JSONObject(json);
            String userId = obj.getString("userId");
            if(userId.equals(SavePref.getInstance(this).getUserdetail().getId()+"")){
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(getString(R.string.not_scan_own_code));
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.txt_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return;
            }
            question = obj.getString("question");
            if(!question.isEmpty()){
                openSendAnswerDlg(question, userId);
            }else{
                callSendFriendRequest(userId);
            }
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
        }
    }

    public void openSendAnswerDlg(String str, String userId){
        dataQAList = new ArrayList<>();
        if(str != null &&! str.isEmpty()){
            int n;
            while ((n =str.indexOf(AppConstants.SEPERATOR)) != -1){
                String cutStr = str.substring(0, n);
                dataQAList.add( new QAData(cutStr, ""));
                str = str.substring(n+AppConstants.SEPERATOR.length());
            }
            dataQAList.add(new QAData(str, ""));
        }

        if(dataQAList.size() > 0){
            SendAnswerAdapter adapter = new SendAnswerAdapter(this, dataQAList, new SendAnswerAdapter.OnKeypressListener() {
                @Override
                public void onKeyPress(int index, String str) {
                    dataQAList.get(index).setAnswer(str);
                }
            });

            CustomSenderAnswerDlg dlg = new CustomSenderAnswerDlg(this, SavePref.getInstance(this).getUserdetail().getFullName(), adapter);
            dlg.setOnItemClickListener(new CustomSenderAnswerDlg.OnDlgItemClickListener(){
                public void onClick(){
                    answer = createAnswerString(dataQAList);
                    callSendFriendRequest(userId);
                }
            });
            dlg.setCanceledOnTouchOutside(false);
            dlg.showDialog();
        }else{
            answer = "";
            callSendFriendRequest(userId);
        }
    }

    public String createAnswerString(ArrayList<QAData> list){
        int size = list.size();
        String strQuestions="";
        for(int i =0 ; i < size; i++){
            if( i != size - 1){
                strQuestions +=  list.get(i).getAnswer() + AppConstants.SEPERATOR;
            }else{
                strQuestions +=  list.get(i).getAnswer();
            }
        }
        return strQuestions;
    }

    private void callSendFriendRequest(String userId){
        ProgressDialog.showProgress(this);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(this).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, userId);
        mparams.put(ApiClass.getmApiClass().TO_ANS, answer);
        mparams.put(ApiClass.getmApiClass().QUESTION,question);

        Call<Basic_Response> call;
        call = mApiInterface.sendFriendRequest(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();

                if (response.isSuccessful()) {
                    Basic_Response basic_response = response.body();
                    if(basic_response.getStatus().equalsIgnoreCase("true")){
                        if(! answer.isEmpty())
                            Toast.makeText(SimpleScannerActivity.this, getString(R.string.answer_success) , Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(SimpleScannerActivity.this, getString(R.string.request_success) , Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    System.out.println(response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

}
