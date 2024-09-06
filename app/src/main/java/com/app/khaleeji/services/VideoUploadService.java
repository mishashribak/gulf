package com.app.khaleeji.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.AddMemoryResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.SessionData;
import CustomView.SingleMediaScanner;
import Model.UploadInfoModel;
import Utility.ApiClass;
import Utility.GlobalVariable;
import Utility.ImagePicker;
import Utility.ProgressDialog;
import Utility.SavePref;
import Utility.Utils;
import Utility.videocompressor.VideoCompress;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoUploadService extends JobService {

    public static final String VIDEO_URL = "VIDEO_URL";
    private static final String TAG = VideoUploadService.class.getName();
    private final String CONTENT_TYPE_DAILY = "daily";
    private final String CONTENT_TYPE_MEMORY = "memory";
    private final String HOSTPOT_DAILY = "HOSTPOT_DAILY";
    private final String HOTSPOT_MEMORY = "HOTSPOT_MEMORY";
    File recordedVideoThumb;
    AmazonS3 s3;
    TransferUtility transferUtility;
    private FFmpeg ffmpeg;
    private String selectionType;
    private String videoPath;
    private String waterMarkPath;
    private boolean isSoundOn;
    private JobParameters params;
    private String finalVideoPath;
    private String destPath;
    private Context context;
    private String finalUrl;
    private String thumbUrl;
    private String media_link;
    private int media_time = -1;
    private String removeAudioPath;
    private String caption;
    private boolean isFromGroupChat;
    private boolean isDailiesSel;
    private boolean isMemoriesSel;
    private boolean isHotDailiesSel;
    private boolean isHotMemoriesSel;
    private int hotSpotId;
    private int[] ids;
    private boolean isFromChat = false;
    private String groupId="";
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
        EventBus.getDefault().register(this);
        this.params = params;
        ffmpeg = FFmpeg.getInstance(this);
        context = VideoUploadService.this;
        credentialsProvider();
        setTransferUtility();

        PersistableBundle bundle = params.getExtras();
        selectionType = bundle.getString("slectionType");
        videoPath = bundle.getString("videoPath");
        waterMarkPath = bundle.getString("waterMarkPath");
        isSoundOn = bundle.getBoolean("isSoundOn");
        media_time = bundle.getInt("media_time");
        media_link = bundle.getString("media_link");
        caption = bundle.getString("caption");
        thumbUrl = bundle.getString("thumb");
        isFromGroupChat = bundle.getBoolean("isFromGroupChat");
        isFromChat = bundle.getBoolean("isFromChat");
        isDailiesSel =  bundle.getBoolean("isDailiesSel");
        isMemoriesSel =  bundle.getBoolean("isMemoriesSel");
        isHotMemoriesSel =  bundle.getBoolean("isHotMemoriesSel");
        isHotDailiesSel =  bundle.getBoolean("isHotDailiesSel");
        hotSpotId = bundle.getInt("hotSpotId");
        ids = bundle.getIntArray("friendIds");
        groupId = bundle.getString("groupId");

        uploadInfoModel = new UploadInfoModel();
        uploadInfoModel.bundle = bundle;
        uploadInfoModel.isVideo = true;
        uploadInfoModel.uploadId = System.currentTimeMillis()+"";

        if(! selectionType.equalsIgnoreCase("save")){
            if(selectionType.equalsIgnoreCase("daily")){
                uploadInfoModel.uploadType = UploadInfoModel.UploadType.DAILY;
            }else{
                if(isDailiesSel && isMemoriesSel || isHotDailiesSel && isHotMemoriesSel){
                    uploadInfoModel.uploadType = UploadInfoModel.UploadType.BOTH;
                }else if((isDailiesSel && ! isMemoriesSel) || (isHotDailiesSel && ! isHotMemoriesSel)){
                    uploadInfoModel.uploadType = UploadInfoModel.UploadType.DAILY;
                }else if( (! isDailiesSel && isMemoriesSel) || (!isHotDailiesSel && isHotMemoriesSel)){
                    uploadInfoModel.uploadType = UploadInfoModel.UploadType.MEMORY;
                }
            }

            SessionData.I().uploadInfoModelList.add(uploadInfoModel);
            MessageEvent messageEvent = new MessageEvent();
            messageEvent.setType(MessageEvent.MessageType.UPLOAD_INFO);
            EventBus.getDefault().post(messageEvent);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isSoundOn) {
                    if( waterMarkPath == null || waterMarkPath.isEmpty()){
                        performActionOnSlctn(videoPath, selectionType);
                    }else{
                        try {
                            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                                @Override
                                public void onStart() {
                                    Log.e(TAG, "onStart");
                                }

                                @Override
                                public void onFailure() {
                                    Log.e(TAG, "onFailure");
                                }

                                @Override
                                public void onSuccess() {
                                    Log.e(TAG, "onSuccess");
                                    addWaterMark(selectionType, videoPath, waterMarkPath);
                                }
                                @Override
                                public void onFinish() {
                                    Log.e(TAG, "onFinish");
                                }
                            });

                        } catch (FFmpegNotSupportedException e) {
                            // Handle if FFmpeg is not supported by device
                            Log.e(TAG, "e.getMessage() :  " + e.getMessage());
                        }

                    }
                } else {
                    removeAudio(selectionType, videoPath);
                }

            }
        }).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG, "onStopJob");
        EventBus.getDefault().unregister(this);
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "myService created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "myService destroyed");
    }

    private void addWaterMark(final String selectionType, final String videoPath, final String waterMarkPath) {
        destPath = ImagePicker.getOutputVideoFile().getAbsolutePath();
        Log.e(TAG, "destPath : " + destPath);
        String[] сmd = new String[]{
                "-i",
                videoPath,
                "-i",
                waterMarkPath,
                "-filter_complex",
                "overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2",
                destPath
        };
        //[0:v]setpts=PTS/1.15,crop=iw/1.2:ih/1.2,boxblur=1:2,scale=1280:720 [v1]; [1:v][v1]scale2ref[wm][v1];[v1][wm]overlay=0:0,setdar=16/9
        //overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2
        //[0:v]scale=1280:720[bg];[bg][1:v]overlay=0:0
        //(main_w-overlay_w)/2:(main_h-overlay_h)/2   Best one
   //     performActionOnSlctn(destPath, selectionType);
        try {
            ffmpeg.execute(сmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    Log.e(TAG, "onProgress message : " + message);
//                    Toast.makeText(VideoUploadService.this, message + " Watermarking...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(VideoUploadService.this, "Error in adding watermark", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String message) {
                    Log.e(TAG, "onSuccess message : " + message);
//                    File waterMarkFile = new File(waterMarkPath);
//                    if (waterMarkFile.exists()) {
//                        waterMarkFile.delete();
//                    }
                    performActionOnSlctn(destPath, selectionType);
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.e(TAG, "e.getMessage() : " + e.getMessage());
        }
    }

    private void compressVideo(final String inputPath, final String slcnType)   //, final String videoName, final String strCaption
    {
        final File compressedVideoFile = ImagePicker.getOutputVideoFile();

        final String compressedVideoPath = compressedVideoFile.getPath();

        VideoCompress.compressVideoMedium(inputPath, compressedVideoPath, new VideoCompress.CompressListener() {
            @Override
            public void onStart() {
                // ProgressDialog.showProgress(context);
            }

            @Override
            public void onSuccess() {
                performActionOnSlctn(compressedVideoPath, slcnType);
                Log.e(TAG, "compressedVideoFile size : " + Utils.getFileSizeInKb(compressedVideoFile));
            }

            @Override
            public void onFail() {
                Log.e(TAG, "Compress Failed!");
//                SavePref.getInstance(getApplicationContext()).setUploadFlag(2);
                stopJobNow();
            }

            @Override
            public void onProgress(float percent) {
                //tv_progress.setText(String.valueOf(percent) + "%");
                Log.v(TAG, String.valueOf(percent) + "%");
            }
        });
    }
    private void removeAudio(final String slctnType, String compressedVideoPath) {

        removeAudioPath = ImagePicker.getOutputVideoFile().getPath();

        Log.e(TAG, "removeAudioPath : " + removeAudioPath);
        String[] сmd = new String[]{
                "-i",
                compressedVideoPath,
                "-c",
                "copy",
                "-an",
                removeAudioPath
        };

        //[0:v]setpts=PTS/1.15,crop=iw/1.2:ih/1.2,boxblur=1:2,scale=1280:720 [v1]; [1:v][v1]scale2ref[wm][v1];[v1][wm]overlay=0:0,setdar=16/9
        //overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2
        //[0:v]scale=1280:720[bg];[bg][1:v]overlay=0:0
        //(main_w-overlay_w)/2:(main_h-overlay_h)/2   Best one

        try {

            ffmpeg.execute(сmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    // ProgressDialog.showProgress(context);
                }

                @Override
                public void onProgress(String message) {
                    Log.e(TAG, "onProgress message : " + message);

                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "onFailure message : " + message);
                    //ProgressDialog.hideprogressbar();
                    Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show();
//                    SavePref.getInstance(getApplicationContext()).setUploadFlag(2);
                    stopJobNow();
                }

                @Override
                public void onSuccess(String message) {
                    Log.e(TAG, "onSuccess message : " + message);
                    addWaterMark(selectionType, removeAudioPath, waterMarkPath);
                }

                @Override
                public void onFinish() {
                    //  ProgressDialog.hideprogressbar();
                    // finish();

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            Log.e(TAG, "e.getMessage() : " + e.getMessage());
        }

    }

    private void copyFileToDCIM(String inputFile) {

        InputStream in = null;
        OutputStream out = null;
        try {

            File storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM
                    ), "Camera"
            );
            if(!storageDir.exists())
                storageDir.mkdir();
            // Create a media file name
//            String timeStamp1 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String outputPath = storageDir.getPath() + File.separator + "VID_" +  System.currentTimeMillis() + ".mp4";


            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

            new SingleMediaScanner(context, new File(outputPath), new SingleMediaScanner.CompletedListener() {
                @Override
                public void completed() {
                    ProgressDialog.hideprogressbar();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.save), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e(TAG, "Video Saved to Gallery");
                    stopJobNow();
                }
            });


        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    private void performActionOnSlctn(String compressedVideoPath, String slctnType) {
        finalVideoPath = compressedVideoPath;
        if(isFromChat){
            MessageEvent messageEvent = new MessageEvent();
            messageEvent.setType(MessageEvent.MessageType.MSG_VIDEO);
            messageEvent.setSavedUrl(compressedVideoPath);
            EventBus.getDefault().post(messageEvent);
            stopJobNow();
        }else{
            switch (slctnType.toLowerCase()) {

                case "save":
                    copyFileToDCIM(finalVideoPath);
                    break;

                case "send":
                    Log.e(TAG, "send");
                    createThumb(compressedVideoPath);
                    break;

                case CONTENT_TYPE_DAILY:
                    if (compressedVideoPath != null) {
                        setFileToUploadToDaily(compressedVideoPath, "", CONTENT_TYPE_DAILY);
                    } else {
                        Log.e(TAG, "CONTENT_TYPE_DAILY Some error occured");
                        stopJobOnFail();
                    }

                    break;
//
//            case CONTENT_TYPE_MEMORY:
//                if (compressedVideoPath != null) {
//                    setFileToUpload(compressedVideoPath, caption, CONTENT_TYPE_MEMORY);
//                } else {
//                    Log.e(TAG, "CONTENT_TYPE_MEMORY Some error occured");
//                    stopJobNow();
//                }
//                break;
            }
        }

    }

    private File savedVideoThumb;
    public void createThumb(String videoPath){
        Log.e(TAG, "createThumb");
        File movFile = new File(videoPath);
        Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(movFile.getAbsolutePath(),
                MediaStore.Video.Thumbnails.MINI_KIND);
        savedVideoThumb = saveThumbnail(thumbBmp);

        Log.e(TAG, "savedVideoThumb"+savedVideoThumb);

        setThumbToUpload(savedVideoThumb);

    }

    public void setThumbToUpload(File f) {
        Log.e(TAG, "setThumbToUpload");
        if(f == null)
            return;
        final Uri fileUri = Uri.parse(f.toString());
        String key = f.getName();
        if (fileUri != null) {
            TransferObserver transferObserver = transferUtility.upload(
                    ApiClass.S3_BUCKET_NAME,      /* The bucket to upload to */
                    key,                         /* The key for the uploaded object */
                    f                    /* The file where the data to upload exists */
            );
            thumbUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME + "/" + key;

            Log.e(TAG, "thumbUrl"+thumbUrl);

            transferThumbObserverListener(transferObserver);
        }
    }

    public void transferThumbObserverListener(TransferObserver transferObserver) {
        Log.e(TAG, "transferThumbObserverListener");
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    setSendFileToUpload(finalVideoPath);
                } else if (TransferState.FAILED == state) {
                    Log.e(TAG, "transferFAILED");
                    stopJobOnFail();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.e("SendToVideo", "onProgressChangedThumb : " + percentDone + "%");
                for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                    if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                        SessionData.I().uploadInfoModelList.get(i).currentProgress = percentDone/2;
                        break;
                    }
                }
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.UPLOAD_INFO);
                EventBus.getDefault().post(messageEvent);
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("SendToVideo", "transferObserverListener onError : " + ex.getMessage());
            }

        });
    }

    private  File tempFile;
    public void setSendFileToUpload(String strImagepath) {
        tempFile = new File(strImagepath);
        final Uri fileUri = Uri.parse(tempFile.toString());
        String key = tempFile.getName();
        Log.e("setSendFileToUpload", "key :" + key);
        if (fileUri != null) {
            TransferObserver transferObserver = transferUtility.upload(
                    ApiClass.S3_BUCKET_NAME,      /* The bucket to upload to */
                    key,                         /* The key for the uploaded object */
                    tempFile                    /* The file where the data to upload exists */
            );
            Log.e("setSendFileToUpload", "setFileToUpload transferObserver");
            finalUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME + "/" + key;

            transferObserverListener(transferObserver);

        }
    }

    public void transferObserverListenerForThumb(TransferObserver transferObserver, final String caption, final String contentType) {

        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED == state) {

                    if (recordedVideoThumb != null) {
                        setThumbToUpload(recordedVideoThumb.getAbsolutePath(), recordedVideoThumb.getName(), caption, contentType);
                    }

                } else if (TransferState.FAILED == state) {
                    stopJobOnFail();
                    // file.delete();
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.e("SendTo", "onProgressChanged : " + percentDone + "%");

                for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                    if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                        SessionData.I().uploadInfoModelList.get(i).currentProgress = percentDone/2;
                        break;
                    }
                }
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.UPLOAD_INFO);
                EventBus.getDefault().post(messageEvent);
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, "transferObserverListener onError : " + ex.getMessage());
            }

        });
    }

    public void transferObserverListener(TransferObserver transferObserver) {
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

                    addmeetupMedia();

                } else if (TransferState.FAILED == state) {
                    stopJobOnFail();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.e("SendToVideoContent", "onProgressChanged : " + percentDone + "%");
                for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                    if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                        SessionData.I().uploadInfoModelList.get(i).currentProgress = 50 + percentDone/2;
                        break;
                    }
                }
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.UPLOAD_INFO);
                EventBus.getDefault().post(messageEvent);
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("SendToVideoContent", "transferObserverListener onError : " + ex.getMessage());
                ProgressDialog.hideprogressbar();
            }

        });
    }

    public void uploadThumbToS3(TransferObserver transferObserver, final String caption, final String contentType) {


        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {

                if (TransferState.COMPLETED == state) {
                    addmeetupMediaNew();
                } else if (TransferState.FAILED == state) {
                    stopJobOnFail();
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.e(TAG, "onProgressChanged : " + percentDone + "%");
                for(int i = 0; i < SessionData.I().uploadInfoModelList.size(); i++){
                    if(SessionData.I().uploadInfoModelList.get(i).uploadId == uploadInfoModel.uploadId){
                        SessionData.I().uploadInfoModelList.get(i).currentProgress = 50 + percentDone/2;
                        break;
                    }
                }
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.UPLOAD_INFO);
                EventBus.getDefault().post(messageEvent);

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, "transferObserverListener onError : " + ex.getMessage());
            }

        });
    }

    public void setFileToUploadToDaily(String strVideopath, String caption, String contentType) {

        File movFile = new File(destPath);

        Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(movFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);

        recordedVideoThumb = saveThumbnail(thumbBmp);

        tempFile = new File(strVideopath);

        final Uri fileUri = Uri.parse(tempFile.toString());

        String key = tempFile.getName();

        Log.e(TAG, "key :" + key);

        if (fileUri != null) {

            TransferObserver transferObserver = transferUtility.upload(
                    ApiClass.S3_BUCKET_NAME,     /* The bucket to upload to */
                    key,    /* The key for the uploaded object */
                    tempFile       /* The file where the data to upload exists */
            );

            Log.e(TAG, "setFileToUpload transferObserver");

            // finalUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME +"/images/"+ key;

            finalUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME + "/" + key;

            Log.e(TAG, "finalUrl : " + finalUrl);

            // ProgressDialog.showProgress(context);

            transferObserverListenerForThumb(transferObserver, caption, contentType);

        }

    }

    public void credentialsProvider() {

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                VideoUploadService.this,
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

        transferUtility = new TransferUtility(s3, context);
    }


    public void setThumbToUpload(String strThumbPath, String fileName, String caption, String contentType) {

        final File tempFile = new File(strThumbPath);

        final Uri fileUri = Uri.parse(tempFile.toString());

        String key = fileName;

        if (fileUri != null) {
            TransferObserver transferObserver = transferUtility.upload(
                    ApiClass.S3_BUCKET_NAME,     /* The bucket to upload to */
                    key,    /* The key for the uploaded object */
                    tempFile       /* The file where the data to upload exists */
            );

            Log.e(TAG, "setThumbToUpload transferObserver");

            //finalUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME +"/video/"+ key;

            thumbUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME + "/" + key;

            Log.e(TAG, "thumbUrl : " + thumbUrl);

            uploadThumbToS3(transferObserver, caption, contentType);

        }

    }

    public void addmeetupMedia(){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        JsonObject requestParam = new JsonObject();

        if(ids != null) {
            JsonArray idArray = new JsonArray();
            for (int i = 0; i < ids.length; i++) {
                idArray.add(ids[i]);
            }
            requestParam.add("ids", idArray);
        }


        int userid = SavePref.getInstance(context).getUserdetail().getId();
        requestParam.addProperty(ApiClass.getmApiClass().USER_ID, String.valueOf(userid));
        String replacedUrl = finalUrl.replace("https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME, "https://cdn.gulflinksa.com");
        requestParam.addProperty("url", replacedUrl);
        requestParam.addProperty("media_type", "video");
        if(groupId != null && !groupId.isEmpty())
            requestParam.addProperty("groupId", groupId);

        String contentType="";
        if(isDailiesSel && isMemoriesSel){
            contentType = "both";
        }else if(isDailiesSel && ! isMemoriesSel){
            contentType="daily";
        }else if( !isDailiesSel && isMemoriesSel){
            contentType="memory";
        }

        String hotspotContentType="";
        if(isHotDailiesSel && isHotMemoriesSel){
            hotspotContentType = "both";
        }else if(isHotDailiesSel && ! isHotMemoriesSel){
            hotspotContentType="daily";
        }else if( !isHotDailiesSel && isHotMemoriesSel){
            hotspotContentType="memory";
        }

        if(! contentType.isEmpty()){
            requestParam.addProperty("content_type", contentType);
        }

        if(hotSpotId != -1 && ! hotspotContentType.isEmpty()){
            requestParam.addProperty("hotspot_content_type", hotspotContentType);
            requestParam.addProperty("hotspot_id", hotSpotId);
        }

        requestParam.addProperty("thumbnail", thumbUrl);
        requestParam.addProperty("caption", caption);

        if(media_link !=  null && media_time != -1){
            requestParam.addProperty("media_link", media_link);
            requestParam.addProperty("media_time", media_time);
        }

        requestParam.addProperty("sourceType", GlobalVariable.isFromCamera ? "camera" : "gallery");

        Call<AddMemoryResponse> call = mApiInterface.addMemoryServiceNew(requestParam);

        call.enqueue(new Callback<AddMemoryResponse>() {
            @Override
            public void onResponse(Call<AddMemoryResponse> call, Response<AddMemoryResponse> response) {
                if (response.isSuccessful()) {
                    AddMemoryResponse mOtpResponse = response.body();
                    if (mOtpResponse != null) {
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {
//                            if (recordedVideoThumb != null && recordedVideoThumb.exists()) {
//                                recordedVideoThumb.delete();
//                            }
//                            if (tempFile != null && tempFile.exists()) {
//                                tempFile.delete();
//                            }

                            Log.e("videoUpload", "upload success");
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
                    Log.e("videoUpload", response.errorBody().toString());
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
                Log.e("videoUpload", "fail");
                stopJobOnFail();
            }

        });
    }

    public void addmeetupMediaNew(){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(context).getUserdetail().getId();

        JsonObject requestParam = new JsonObject();

        requestParam.addProperty(ApiClass.getmApiClass().USER_ID, String.valueOf(userid));
        String replacedUrl = finalUrl.replace("https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME, "https://cdn.gulflinksa.com");
        requestParam.addProperty("url", replacedUrl);

        requestParam.addProperty("media_type", "video");
        requestParam.addProperty("thumbnail", thumbUrl);
        String contentType="daily";
        requestParam.addProperty("content_type", contentType);
        if(groupId != null && !groupId.isEmpty())
            requestParam.addProperty("groupId", groupId);

        if(media_link !=  null && media_time != -1){
            requestParam.addProperty("media_link", media_link);
            requestParam.addProperty("media_time", media_time);
        }

        requestParam.addProperty("sourceType", GlobalVariable.isFromCamera ? "camera" : "gallery");

        Call<AddMemoryResponse> call = mApiInterface.addMemoryServiceNew(requestParam);

        call.enqueue(new Callback<AddMemoryResponse>() {
            @Override
            public void onResponse(Call<AddMemoryResponse> call, Response<AddMemoryResponse> response) {
                ProgressDialog.hideprogressbar();
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

    private File saveThumbnail(Bitmap bitmap) {
        try {
            File file = ImagePicker.getThumbnailFile();
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(null, "Save file error!");
            return null;
        }
    }

    public void stopJobNow() {
        Log.e(TAG, "stopJobNow");
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

}