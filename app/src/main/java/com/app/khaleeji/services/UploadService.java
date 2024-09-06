package com.app.khaleeji.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.AddMemoryResponse;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.SessionData;
import Model.UploadInfoModel;
import Utility.ApiClass;
import Utility.GlobalVariable;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadService extends JobService {
    
    AmazonS3 s3;
    TransferUtility transferUtility;
    private String finalCroppedUrl;
    private String finalOrgUrl;
    private String media_link;
    private int media_time = -1;
    private String caption;
    private boolean isFromGroupChat;
    private boolean isDailiesSel;
    private boolean isMemoriesSel;
    private boolean isHotDailiesSel;
    private boolean isHotMemoriesSel;
    private int hotSpotId;
    private JobParameters params;
    private String videoPath;
    private String selectionType="";
    private int[] ids;
    private String groupId="";
    private String originalPath;
    private UploadInfoModel uploadInfoModel;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.MessageType.UPLOAD_CANCEL) {
            for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                    SessionData.I().uploadInfoModelList.remove(i);
                    break;
                }
            }
            MessageEvent msg = new MessageEvent();
            msg.setType(MessageEvent.MessageType.UPLOAD_INFO);
            EventBus.getDefault().post(msg);
            stopJobNow();
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.e("zzz", "onStartJob");

        EventBus.getDefault().register(this);

        this.params = params;

        credentialsProvider();
        transferUtility = new TransferUtility(s3, UploadService.this);

        PersistableBundle bundle = params.getExtras();
        selectionType = bundle.getString("slectionType");
        videoPath = bundle.getString("videoPath");
        media_time = bundle.getInt("media_time");
        media_link = bundle.getString("media_link");
        caption = bundle.getString("caption");
        isFromGroupChat = bundle.getBoolean("isFromGroupChat");
        isDailiesSel =  bundle.getBoolean("isDailiesSel");
        isMemoriesSel =  bundle.getBoolean("isMemoriesSel");
        isHotMemoriesSel =  bundle.getBoolean("isHotMemoriesSel");
        isHotDailiesSel =  bundle.getBoolean("isHotDailiesSel");
        hotSpotId = bundle.getInt("hotSpotId");
        ids = bundle.getIntArray("friendIds");
        groupId = bundle.getString("groupId");
        originalPath = bundle.getString("originalPath");

        uploadInfoModel = new UploadInfoModel();
        uploadInfoModel.isVideo = false;
        uploadInfoModel.bundle = bundle;
        uploadInfoModel.uploadId = System.currentTimeMillis()+"";

        if(selectionType.equalsIgnoreCase("daily")){
            uploadInfoModel.uploadType = UploadInfoModel.UploadType.DAILY;
        }else{
            if(isDailiesSel && isMemoriesSel || isHotDailiesSel && isHotMemoriesSel){
                uploadInfoModel.uploadType = UploadInfoModel.UploadType.BOTH;
            }else if((isDailiesSel && ! isMemoriesSel) || (isHotDailiesSel && ! isHotMemoriesSel)){
                uploadInfoModel.uploadType = UploadInfoModel.UploadType.DAILY;
            }else if( (!isDailiesSel && isMemoriesSel) || (!isHotDailiesSel && isHotMemoriesSel)){
                uploadInfoModel.uploadType = UploadInfoModel.UploadType.MEMORY;
            }
        }

        SessionData.I().uploadInfoModelList.add(uploadInfoModel);
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setType(MessageEvent.MessageType.UPLOAD_INFO);
        EventBus.getDefault().post(messageEvent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(selectionType.equalsIgnoreCase("daily")){
                    setFileToUploadDaily(videoPath);
                }else{
                    setFileToUpload(videoPath, originalPath);
                }
            }
        }).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e("image_upload", "onStopJob");
        EventBus.getDefault().unregister(this);
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("image_upload", "myService created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("image_upload", "myService destroyed");
    }

    public void credentialsProvider() {
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                UploadService.this,
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

    public void stopJobNow() {
        Log.e("image_upload", "stopJobNow");
        jobFinished(params, false);
    }

    public void stopJobOnFail(){
        for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
            if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                SessionData.I().uploadInfoModelList.get(i).isUploadFail = true;
                break;
            }
        }
        MessageEvent msg = new MessageEvent();
        msg.setType(MessageEvent.MessageType.UPLOAD_INFO);
        EventBus.getDefault().post(msg);
        jobFinished(params, false);
    }

    private  File tempFile;
    private File tempFileCrop;
    public void setFileToUpload(String croppedImg, String originalImg) {

        if(isDailiesSel || isHotDailiesSel){
            tempFile = new File(originalImg);
            final Uri fileUri = Uri.parse(tempFile.toString());
            String key = tempFile.getName();

            if (fileUri != null) {
                TransferObserver transferObserver = transferUtility.upload(
                        ApiClass.S3_BUCKET_NAME,      /* The bucket to upload to */
                        key,                         /* The key for the uploaded object */
                        tempFile                    /* The file where the data to upload exists */
                );
                Log.e("sendto", "setFileToUpload transferObserver");
                finalOrgUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME + "/" + key;

                transferObserverListenerOrg(transferObserver);

            }
        }

        if(isMemoriesSel || isHotMemoriesSel){
            tempFileCrop = new File(croppedImg);
            final Uri fileUri = Uri.parse(tempFileCrop.toString());
            String key = tempFileCrop.getName();

            if (fileUri != null) {
                TransferObserver transferObserver = transferUtility.upload(
                        ApiClass.S3_BUCKET_NAME,      /* The bucket to upload to */
                        key,                         /* The key for the uploaded object */
                        tempFileCrop                    /* The file where the data to upload exists */
                );

                finalCroppedUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME + "/" + key;

                transferObserverListenerCrop(transferObserver);

            }
        }
    }

    public void transferObserverListenerCrop(TransferObserver transferObserver) {
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    addmeetupMediaCrop();
                } else if (TransferState.FAILED == state) {
                    stopJobOnFail();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.e("SendToImageCrop", "onProgressChanged : " + percentDone + "%");

                for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                    if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                        SessionData.I().uploadInfoModelList.get(i).currentProgress = percentDone;
                        break;
                    }
                }
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.UPLOAD_INFO);
                EventBus.getDefault().post(messageEvent);
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("SendToImageCrop", "transferObserverListener onError : " + ex.getMessage());
                ProgressDialog.hideprogressbar();
            }

        });
    }

    public void transferObserverListenerOrg(TransferObserver transferObserver) {
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    addmeetupMediaOriginal();

                } else if (TransferState.FAILED == state) {
                    stopJobOnFail();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.e("SendToImage", "onProgressChanged : " + percentDone + "%");

                for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                    if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                        SessionData.I().uploadInfoModelList.get(i).currentProgress = percentDone;
                        break;
                    }
                }
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.UPLOAD_INFO);
                EventBus.getDefault().post(messageEvent);
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("SendToImage", "transferObserverListener onError : " + ex.getMessage());
                ProgressDialog.hideprogressbar();
            }

        });
    }

    public void transferObserverListenerDaily(TransferObserver transferObserver) {
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
//                    ProgressDialog.hideprogressbar();

//                    if (recordedVideoThumb != null && recordedVideoThumb.exists()) {
//                        recordedVideoThumb.delete();
//                    }
//                    if (tempFile != null && tempFile.exists()) {
//                        tempFile.delete();
//                    }

                    addmeetupMediaNewDaily();

                } else if (TransferState.FAILED == state) {
                    stopJobOnFail();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.e("SendTo", "onProgressChanged : " + percentDone + "%");

                for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                    if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                        SessionData.I().uploadInfoModelList.get(i).currentProgress = percentDone;
                        break;
                    }
                }
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.UPLOAD_INFO);
                EventBus.getDefault().post(messageEvent);

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("SendTo", "transferObserverListener onError : " + ex.getMessage());
            }

        });
    }

    public void setFileToUploadDaily(String strImagepath) {
        tempFile = new File(strImagepath);
        final Uri fileUri = Uri.parse(tempFile.toString());
        String key = tempFile.getName();
        Log.e("sendto", "key :" + key);
        if (fileUri != null) {
            TransferObserver transferObserver = transferUtility.upload(
                    ApiClass.S3_BUCKET_NAME,      /* The bucket to upload to */
                    key,                         /* The key for the uploaded object */
                    tempFile                    /* The file where the data to upload exists */
            );
            Log.e("sendto", "setFileToUpload transferObserver");
            finalCroppedUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME + "/" + key;

            transferObserverListenerDaily(transferObserver);

        }
    }

    public void addmeetupMediaNewDaily(){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(getApplicationContext()).getUserdetail().getId();

        JsonObject requestParam = new JsonObject();

        requestParam.addProperty(ApiClass.getmApiClass().USER_ID, String.valueOf(userid));
        String replacedUrl = finalCroppedUrl.replace("https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME, "https://cdn.gulflinksa.com");
        requestParam.addProperty("url", replacedUrl);
        requestParam.addProperty("media_type", "image");
        requestParam.addProperty("thumbnail", finalCroppedUrl);
        String contentType="daily";
        requestParam.addProperty("content_type", contentType);
        if(media_link !=  null && media_time != -1){
            requestParam.addProperty("media_link", media_link);
            requestParam.addProperty("media_time", media_time);
        }

        if(groupId != null && !groupId.isEmpty())
            requestParam.addProperty("groupId", groupId);

        requestParam.addProperty("sourceType", GlobalVariable.isFromCamera ? "camera" : "gallery");

        Call<AddMemoryResponse> call = mApiInterface.addMemoryServiceNew(requestParam);

        call.enqueue(new Callback<AddMemoryResponse>() {
            @Override
            public void onResponse(Call<AddMemoryResponse> call, Response<AddMemoryResponse> response) {
                if (response.isSuccessful()) {
                    AddMemoryResponse mOtpResponse = response.body();
                    if (mOtpResponse != null) {
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {

//                            if (tempFile != null && tempFile.exists()) {
//                                tempFile.delete();
//                            }
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),getString(R.string.upload_success),Toast.LENGTH_SHORT).show();
                                }
                            });
                            for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                                if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                                    SessionData.I().uploadInfoModelList.remove(i);
                                    break;
                                }
                            }
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.PROFILE_REFRESH);
                            EventBus.getDefault().post(messageEvent);
                            stopJobNow();
                            return;
                        }

                    }
                    stopJobOnFail();
                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),getString(R.string.upload_fail),Toast.LENGTH_SHORT).show();
                        }
                    });
                    System.out.println(response.errorBody());
                }
                stopJobOnFail();

            }
            @Override
            public void onFailure(Call<AddMemoryResponse> call, Throwable t) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),getString(R.string.upload_fail),Toast.LENGTH_SHORT).show();
                    }
                });
                stopJobOnFail();
            }

        });
    }

    private boolean isCropFinished;
    private boolean isOrgFinished;
    public void addmeetupMediaCrop(){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        JsonObject requestParam = new JsonObject();

        if(ids != null){
            JsonArray idArray = new JsonArray();
            for(int i=0; i< ids.length; i++){
                idArray.add(ids[i]);
            }
            requestParam.add("ids", idArray);
        }

        if(groupId != null && !groupId.isEmpty())
            requestParam.addProperty("groupId", groupId);

        int userid = SavePref.getInstance(getApplicationContext()).getUserdetail().getId();
        requestParam.addProperty(ApiClass.getmApiClass().USER_ID, String.valueOf(userid));
        String replacedUrl = finalCroppedUrl.replace("https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME, "https://cdn.gulflinksa.com");
        requestParam.addProperty("url", replacedUrl);
        requestParam.addProperty("media_type", "image");

        if(media_link !=  null && media_time != -1){
            requestParam.addProperty("media_link", media_link);
            requestParam.addProperty("media_time", media_time);
        }

        String contentType="";
        if(isMemoriesSel){
            contentType="memory";
        }

        String hotspotContentType="";
        if(isHotMemoriesSel){
            hotspotContentType="memory";
        }

        if(! contentType.isEmpty()){
            requestParam.addProperty("content_type", contentType);
        }

        if(hotSpotId != -1 && ! hotspotContentType.isEmpty()){
            requestParam.addProperty("hotspot_content_type", hotspotContentType);
            requestParam.addProperty("hotspot_id", hotSpotId);
        }

        requestParam.addProperty("thumbnail", finalCroppedUrl);
        requestParam.addProperty("caption", caption);

        requestParam.addProperty("sourceType", GlobalVariable.isFromCamera ? "camera" : "gallery");

        Call<AddMemoryResponse> call = mApiInterface.addMemoryServiceNew(requestParam);

        call.enqueue(new Callback<AddMemoryResponse>() {
            @Override
            public void onResponse(Call<AddMemoryResponse> call, Response<AddMemoryResponse> response) {
                if (response.isSuccessful()) {
                    AddMemoryResponse mOtpResponse = response.body();
                    if (mOtpResponse != null) {
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {
                            isCropFinished = true;
                            Log.e("upload", "crop image upload success");
                            if(isDailiesSel || isHotDailiesSel){
                                if(isOrgFinished){

                                    Log.e("SendToImageOrg", "success");
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),getString(R.string.upload_success),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                                        if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                                            SessionData.I().uploadInfoModelList.remove(i);
                                            break;
                                        }
                                    }
                                    MessageEvent messageEvent = new MessageEvent();
                                    messageEvent.setType(MessageEvent.MessageType.PROFILE_REFRESH);
                                    EventBus.getDefault().post(messageEvent);
                                    stopJobNow();
                                    return;
                                }
                            }else{
//                                SavePref.getInstance(getApplicationContext()).setUploadFlag(1);
                                Log.e("SendToImageOrg", "success");
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),getString(R.string.upload_success),Toast.LENGTH_SHORT).show();
                                    }
                                });
                                for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                                    if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                                        SessionData.I().uploadInfoModelList.remove(i);
                                        break;
                                    }
                                }
                                MessageEvent messageEvent = new MessageEvent();
                                messageEvent.setType(MessageEvent.MessageType.PROFILE_REFRESH);
                                EventBus.getDefault().post(messageEvent);
                                stopJobNow();
                                return;
                            }

                        }
                    }
                    stopJobOnFail();
                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),getString(R.string.upload_fail),Toast.LENGTH_SHORT).show();
                        }
                    });
                    System.out.println(response.errorBody());
                    stopJobOnFail();
                }

            }
            @Override
            public void onFailure(Call<AddMemoryResponse> call, Throwable t) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),getString(R.string.upload_fail),Toast.LENGTH_SHORT).show();
                    }
                });
                stopJobOnFail();
            }

        });
    }

    public void addmeetupMediaOriginal(){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        JsonObject requestParam = new JsonObject();

        if(ids != null){
            JsonArray idArray = new JsonArray();
            for(int i=0; i< ids.length; i++){
                idArray.add(ids[i]);
            }
            requestParam.add("ids", idArray);
        }


        if(groupId != null && !groupId.isEmpty())
            requestParam.addProperty("groupId", groupId);

        int userid = SavePref.getInstance(getApplicationContext()).getUserdetail().getId();
        requestParam.addProperty(ApiClass.getmApiClass().USER_ID, String.valueOf(userid));
        String replacedUrl = finalOrgUrl.replace("https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME, "https://cdn.gulflinksa.com");
        requestParam.addProperty("url", replacedUrl);
        requestParam.addProperty("media_type", "image");

        if(media_link !=  null && media_time != -1){
            requestParam.addProperty("media_link", media_link);
            requestParam.addProperty("media_time", media_time);
        }

        String contentType="";
        if(isDailiesSel){
            contentType="daily";
        }

//        if(isDailiesSel && isMemoriesSel){
//            contentType = "both";
//        }else if(isDailiesSel && ! isMemoriesSel){
//            contentType="daily";
//        }else if( !isDailiesSel && isMemoriesSel){
//            contentType="memory";
//        }

        String hotspotContentType="";
        if(isHotDailiesSel){
            hotspotContentType="daily";
        }

//        if(isHotDailiesSel && isHotMemoriesSel){
//            hotspotContentType = "both";
//        }else if(isHotDailiesSel && ! isHotMemoriesSel){
//            hotspotContentType="daily";
//        }else if( !isHotDailiesSel && isHotMemoriesSel){
//            hotspotContentType="memory";
//        }

        if(! contentType.isEmpty()){
            requestParam.addProperty("content_type", contentType);
        }

        if(hotSpotId != -1 && ! hotspotContentType.isEmpty()){
            requestParam.addProperty("hotspot_content_type", hotspotContentType);
            requestParam.addProperty("hotspot_id", hotSpotId);
        }

        requestParam.addProperty("thumbnail", finalCroppedUrl);
        requestParam.addProperty("caption", caption);

        requestParam.addProperty("sourceType", GlobalVariable.isFromCamera ? "camera" : "gallery");

        Call<AddMemoryResponse> call = mApiInterface.addMemoryServiceNew(requestParam);

        call.enqueue(new Callback<AddMemoryResponse>() {
            @Override
            public void onResponse(Call<AddMemoryResponse> call, Response<AddMemoryResponse> response) {
                if (response.isSuccessful()) {
                    AddMemoryResponse mOtpResponse = response.body();
                    if (mOtpResponse != null) {
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {
                            Log.e("upload", "org image upload success");
                            isOrgFinished = true;
                            if(isMemoriesSel || isHotMemoriesSel){
                                if(isCropFinished ){
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),getString(R.string.upload_success),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                                        if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                                            SessionData.I().uploadInfoModelList.remove(i);
                                            break;
                                        }
                                    }
                                    MessageEvent messageEvent = new MessageEvent();
                                    messageEvent.setType(MessageEvent.MessageType.PROFILE_REFRESH);
                                    EventBus.getDefault().post(messageEvent);
                                    stopJobNow();
                                    return;
                                }
                            }else{
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),getString(R.string.upload_success),Toast.LENGTH_SHORT).show();
                                    }
                                });
                                for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                                    if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                                        SessionData.I().uploadInfoModelList.remove(i);
                                        break;
                                    }
                                }
                                MessageEvent messageEvent = new MessageEvent();
                                messageEvent.setType(MessageEvent.MessageType.PROFILE_REFRESH);
                                EventBus.getDefault().post(messageEvent);
                                stopJobNow();
                                return;
                            }

                        }
                    }
                    stopJobOnFail();
                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),getString(R.string.upload_fail),Toast.LENGTH_SHORT).show();
                        }
                    });
                    System.out.println(response.errorBody());
                    stopJobOnFail();
                }
            }
            @Override
            public void onFailure(Call<AddMemoryResponse> call, Throwable t) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),getString(R.string.upload_fail),Toast.LENGTH_SHORT).show();
                    }
                });
                stopJobOnFail();
            }

        });
    }
}