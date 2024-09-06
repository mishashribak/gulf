package com.app.khaleeji.Fragments;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraCharacteristics;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amazonaws.util.IOUtils;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.CameraActivity;
import com.app.khaleeji.Adapter.FilterAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentGsnapvkitkatBinding;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Preview;
import com.otaliastudios.cameraview.filter.Filters;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import Utility.Fragment_Process;
import Utility.GlobalVariable;
import Utility.ImagePicker;
import Utility.OnSwipeTouchListener;
import Utility.RealPathUtility;
import Utility.Utils;

import static Utility.ImagePicker.getJPEGOutputMediaFile;
import static android.app.Activity.RESULT_OK;

public class CameraKitFragment extends BaseFragment
        implements View.OnClickListener {

    private Context mContext;
    private final Filters[] mAllFilters = Filters.values();
    private final long timer = 1000 * 15; // 15 seconds
    public boolean safeToTakePicture = true;
    public ObjectAnimator smoothAnimation;
    private FragmentGsnapvkitkatBinding mbinding;
    private String imgPath;
    private LinearLayoutManager linearLayoutManager;
    private CountDownTimerTest counter;
    private String recordedVidePath = ImagePicker.getOutputVideoFile().getAbsolutePath();
    private View view;
    private FilterAdapter filterAdapter;
    private boolean mIsRecordingVideo;
    private boolean isFlashOn = false;
    private String nodeName = "";
    private boolean isFromChat = false, isFromGroupChat = false;
    private boolean isFromGroupChatCreate = false;
    private File pictureFile = getJPEGOutputMediaFile();
    private long recordedTime = 0;
    private boolean isFlashSupported = true;
    private final String BRIGHTNESS_LOW = "BRIGHTNESS_LOW";
    private final String BRIGHTNESS_MEDIUM = "BRIGHTNESS_MEDIUM";
    private final String BRIGHTNESS_HIGH = "BRIGHTNESS_HIGH";
    private String brightnessLevel = BRIGHTNESS_MEDIUM;

    public CameraKitFragment(){

    }

    public CameraKitFragment(Context ctx){
        mContext = ctx;
    }

    public CameraKitFragment(Context ctx, boolean isFromChat){
        mContext = ctx;
        this.isFromChat = isFromChat;
    }

    public CameraKitFragment(Context ctx, boolean isFromChat, boolean isFromGroupChatCreate){
        mContext = ctx;
        this.isFromChat = isFromChat;
        this.isFromGroupChatCreate = isFromGroupChatCreate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_gsnapvkitkat, container, false);
        view = mbinding.getRoot();

        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);

        mbinding.imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
                startActivityForResult(intent, 2);
            }
        });

        initView();

        mbinding.progressBarCircle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                performActionOnLongClick();
                return true;
            }
        });


        mbinding.progressBarCircle.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        releaseRecorder();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        return view;
    }


    private void initView() {
        setupFlashButton();
        mbinding.camera.setLifecycleOwner(getViewLifecycleOwner());
        mbinding.camera.addCameraListener(new Listener());

        filterAdapter = new FilterAdapter(mActivity, mActivity, mAllFilters, CameraKitFragment.this);
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mbinding.rvFilter.setLayoutManager(linearLayoutManager);
        mbinding.rvFilter.setAdapter(filterAdapter);

        mbinding.ivScreenlight.setOnClickListener(this);
        mbinding.ivFlash.setOnClickListener(this);
        mbinding.ivCameraflip.setOnClickListener(this);
        mbinding.progressBarCircle.setOnClickListener(this);
        mbinding.ivChat.setOnClickListener(this);
        mbinding.ivCross.setOnClickListener(this);
        mbinding.imgBack.setOnClickListener(this);
        mbinding.ivStory.setOnClickListener(this);

        mbinding.camera.setOnTouchListener(new OnSwipeTouchListener(mActivity) {

            public void onSwipeTop() {

            }

            public void onSwipeRight() {

            }

            public void onSwipeLeft() {

            }

            public void onSwipeBottom() {

            }

            @Override
            public void onSingleTapConfirm() {
                super.onSingleTapConfirm();

            }

            @Override
            public void onDoubleTap() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toggleCamera();
                    }
                }, 100);

            }
        });

        filterAdapter.setOnClickListener(new FilterAdapter.OnclickListener() {
            @Override
            public void Onposclick(int pos, Filters filter) {
                int totalVisibleItems = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition();
                int centeredItemPosition = totalVisibleItems / 2;
                mbinding.rvFilter.smoothScrollToPosition(pos);
                mbinding.rvFilter.setScrollY(centeredItemPosition);
                changeCurrentFilter(filter);
            }
        });
    }

    private class Listener extends CameraListener {
        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {

        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            if (mbinding.camera.isTakingVideo()) {
                message("Captured while taking video. Size=" + result.getSize(), false);
                return;
            }
            try {
                result.toFile(pictureFile, new FileCallback() {
                    @Override
                    public void onFileReady(@Nullable File file) {
                        safeToTakePicture = true;
                        imgPath = pictureFile.getPath();

                        launchShowCameraToolPictureFragment(true);
                    }
                });
            } catch (Exception e) {

            }
        }

        @Override
        public void onVideoTaken(@NonNull VideoResult result) {
            super.onVideoTaken(result);
            callPlayVideoActivity(true);
        }

    }


    private void message(@NonNull String content, boolean important) {
        if (important) {
            Toast.makeText(mActivity, content, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mActivity, content, Toast.LENGTH_SHORT).show();
        }
    }

    private void capturePicture() {
        if (!isFrontCamera) {
            if (isFlashSupported) {
                if (isFlashOn) {
                    mbinding.camera.setFlash(Flash.TORCH);
                } else {
                    mbinding.camera.setFlash(Flash.OFF);
                }
            }
        }

        if (mbinding.camera.isTakingPicture()) return;
        mbinding.camera.takePictureSnapshot();

    }

    private void captureVideo() {
        mIsRecordingVideo = true;
        if (mbinding.camera.isTakingPicture() || mbinding.camera.isTakingVideo()) return;
        mbinding.camera.takeVideoSnapshot(new File(recordedVidePath));
    }

    private boolean isFrontCamera;
    private void toggleCamera() {

        if (mbinding.camera.isTakingPicture() || mbinding.camera.isTakingVideo()) return;

        switch (mbinding.camera.toggleFacing()) {
            case BACK:
                isFrontCamera = false;
                mbinding.camera.setFilter(Filters.NONE.newInstance());
                mbinding.ivFilter.setVisibility(View.GONE);
                mbinding.rvFilter.setVisibility(View.GONE);
                break;

            case FRONT:
                isFrontCamera = true;
                mbinding.rvFilter.setVisibility(View.VISIBLE);
                mbinding.ivFilter.setVisibility(View.VISIBLE);
                if (!mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    Toast.makeText(mActivity, "No camera on this device", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
        }
    }

    private void changeCurrentFilter(Filters selectedFilter) {
        if (mbinding.camera.getPreview() != Preview.GL_SURFACE) {
            message("Filters are supported only when preview is Preview.GL_SURFACE.", true);
            return;
        }

        mbinding.camera.setFilter(selectedFilter.newInstance());
    }

    private void performActionOnLongClick() {
        if(! mIsRecordingVideo){
            captureVideo();
            counter = new CountDownTimerTest(timer, 1);
            counter.start();
        }
    }

    private void releaseRecorder() {
        if (mIsRecordingVideo) {
            mbinding.camera.stopVideo();
            mIsRecordingVideo = false;
            stopCountDownnTimer();
            mbinding.progressBarCircle.setProgress(0);
        }
    }

    private void callPlayVideoActivity(boolean isFromCamera) {
        GlobalVariable.isFromCamera = isFromCamera;
        Bundle bundle = new Bundle();
        if (!recordedVidePath.isEmpty()) {
            bundle.putString("recordedVideoPath", recordedVidePath);
            bundle.putBoolean("isFromChat", isFromChat);
            bundle.putBoolean("isFromGroupChat", isFromGroupChat);
            bundle.putString("nodeName", nodeName);
            bundle.putLong("recordedTime", recordedTime);
            VideoEditorFragment videoEditorFragment = new VideoEditorFragment();
            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), videoEditorFragment, bundle, R.id.framelayout_main);
        } else {
            Toast.makeText(mActivity, "Some error occured", Toast.LENGTH_SHORT).show();
        }
    }

    public void launchShowCameraToolPictureFragment(boolean isFromCamera) {
        GlobalVariable.isFromCamera = isFromCamera;
        Bundle bundle = new Bundle();
        if(GlobalVariable.isFloatingDaily){
            bundle.putString("selectedImagePath", imgPath);
            ((CameraActivity)mActivity).openPhotoEditorFragment(bundle);
        }else{
            bundle.putString("edittedImagePath", imgPath);
            bundle.putBoolean("isFromGroupChatCreate", isFromGroupChatCreate);
            bundle.putBoolean("isFromChat", isFromChat);
            bundle.putBoolean("isVideo", false);
            ((CameraActivity)mActivity).openCropFragment(bundle);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        setSmoothAnimation();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2 && data != null) {
                Uri selectedMediaUri = data.getData();
                if (selectedMediaUri.toString().contains("image")) {
                    selectImageFromGalleryResult(data);
                } else  if (selectedMediaUri.toString().contains("video")) {
                    selectVideoFromGalleryResult(data);
                }

            }
        }
    }

    private String copyFileToDCIM(File inputFile) {

        InputStream in = null;
        OutputStream out = null;
        try {

            File storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM
                    ), "khaleeji"
            );
            if(!storageDir.exists())
                storageDir.mkdir();
            // Create a media file name
//            String timeStamp1 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String outputPath = storageDir.getPath() + File.separator + "VID_" + System.currentTimeMillis() + ".mp4";

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

//            new SingleMediaScanner(mActivity, new File(outputPath));

            return outputPath;


        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        return "";
    }

    private void selectVideoFromGalleryResult(Intent data) {
        Uri uri_data = data.getData();
        try {
            recordedVidePath = RealPathUtility.getPath(mActivity, uri_data);
            recordedVidePath = copyFileToDCIM(new File(recordedVidePath));
            if (!recordedVidePath.isEmpty()) {

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(recordedVidePath);
                long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                if(duration > 60000){
                    AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
                    alertDialog.setTitle(getString(R.string.long_video));
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.txt_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }
                callPlayVideoActivity(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImageFromGalleryResult(Intent data) {
        Uri uri_data = data.getData();
        try {
//            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri_data);
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
////            Bitmap compressedBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//            int nh = (int) (originalBitmap.getHeight() * (512.0 / originalBitmap.getWidth()));
//            Bitmap compressedBitmap = Bitmap.createScaledBitmap(originalBitmap, 512, nh, true);

            final InputStream imageStream = mActivity.getContentResolver().openInputStream(uri_data);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            File compressedFile = Utils.createFileFromBitmap(selectedImage, mActivity);
            imgPath = compressedFile.getPath();
            launchShowCameraToolPictureFragment(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSmoothAnimation() {
        smoothAnimation = ObjectAnimator.ofInt(mbinding.progressBarCircle,
                "progress", mbinding.progressBarCircle.getProgress(),
                mbinding.progressBarCircle.getMax());
        smoothAnimation.setDuration(500);
        smoothAnimation.setInterpolator(new AccelerateInterpolator());

        setProgressBarValues();
    }

    private void setProgressBarValues() {
        mbinding.progressBarCircle.setMax((int) (timer / 10));
        // mbinding.progressBarCircle.setProgress((int) (timer / 10));

    }

    @Override
    public void onPause() {
        if (mIsRecordingVideo) {
            mbinding.camera.stopVideo();
            stopCountDownnTimer();
        }

        safeToTakePicture = true;
        if(smoothAnimation != null)
            smoothAnimation.end();
        mbinding.progressBarCircle.setProgress(0);

        super.onPause();
    }

    private void stopCountDownnTimer() {
        counter.cancel();
        smoothAnimation.pause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                mActivity.onBackPressed();
                break;

            case R.id.progressBarCircle:
                try{
                    if (safeToTakePicture) {
                        capturePicture();
                        safeToTakePicture = false;
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                break;

            case R.id.iv_cameraflip:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toggleCamera();
                    }
                }, 100);

                break;

            case R.id.iv_flash:
                switchFlash();
                break;

            case R.id.iv_screenlight:
                if (!android.provider.Settings.System.canWrite(mActivity)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                    startActivity(intent);
                } else {

                    changeScreenBrightness(mActivity);
                }
                break;
        }
    }

    public static int getBrightness(Context context) {
        ContentResolver cResolver = context.getContentResolver();
        try {
            return Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            return 0;
        }
    }

    public void switchFlash() {
        try {
            if (!isFrontCamera) {
                if (isFlashSupported) {
                    if (isFlashOn) {
                        mbinding.ivFlash.setImageResource(R.mipmap.flash_on);
                        isFlashOn = false;
                    } else {
                        mbinding.ivFlash.setImageResource(R.mipmap.flashh_off);
                        isFlashOn = true;
                    }
                }else{
                    isFlashOn = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupFlashButton() {
        if (!isFrontCamera && isFlashSupported) {
            mbinding.ivFlash.setVisibility(View.VISIBLE);

            if (isFlashOn) {
                mbinding.ivFlash.setImageResource(R.mipmap.flashh_off);
            } else {
                mbinding.ivFlash.setImageResource(R.mipmap.flash_on);
            }

        } else {
            mbinding.ivFlash.setVisibility(View.GONE);
        }
    }

    private void changeScreenBrightness(Context context) {
        // Change the screen brightness change mode to manual.
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        // Apply the screen brightness value to the system, this will change the value in Settings ---> Display ---> Brightness level.
        // It will also change the screen brightness for the device.

        if (brightnessLevel.equalsIgnoreCase(BRIGHTNESS_MEDIUM)) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
            brightnessLevel = BRIGHTNESS_HIGH;
            mbinding.ivScreenlight.setImageResource(R.mipmap.brightness_high);
        } else if (brightnessLevel.equalsIgnoreCase(BRIGHTNESS_HIGH)) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 2);
            brightnessLevel = BRIGHTNESS_LOW;
            mbinding.ivScreenlight.setImageResource(R.mipmap.brightness_low);
        } else if (brightnessLevel.equalsIgnoreCase(BRIGHTNESS_LOW)) {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 125);
            brightnessLevel = BRIGHTNESS_MEDIUM;
            mbinding.ivScreenlight.setImageResource(R.mipmap.brightness_medium);
        }
    }

    public class CountDownTimerTest extends CountDownTimer {

        public CountDownTimerTest(long startTime, long interval) {
            super(startTime, interval);

        }

        @Override
        public void onFinish() {
            try {
                releaseRecorder();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            recordedTime = 15 - millisUntilFinished / 1000;
            mbinding.progressBarCircle.setProgress((int) (timer / 10 - millisUntilFinished / 10));
        }
    }


    private int getJpegOrientation(CameraCharacteristics c, int deviceOrientation) {
        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN) return 0;
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;

        // Reverse device orientation for front-facing cameras
        boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront) deviceOrientation = -deviceOrientation;

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        int jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360;

        return jpegOrientation;
    }

}
