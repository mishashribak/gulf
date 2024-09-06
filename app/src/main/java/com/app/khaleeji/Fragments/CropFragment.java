package com.app.khaleeji.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.CameraActivity;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CropFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private String edittedImagePath, media_link, media_time;
    private boolean isFromGroupChatCreate;
    private ImageView ivSend, img, imgRotate, imgReset;
    private com.theartofdev.edmodo.cropper.CropImageView cropImageView;
    private boolean isFromChat = false;

    public CropFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crop, container, false);
        initView();
        // Inflate the layout for this fragment
        return view;
    }

    private void initView() {
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        edittedImagePath = getArguments().getString("edittedImagePath");
        media_link = getArguments().getString("media_link");
        media_time = getArguments().getString("media_time");
        isFromGroupChatCreate = getArguments().getBoolean("isFromGroupChatCreate");
        isFromChat = getArguments().getBoolean("isFromChat");
        img = view.findViewById(R.id.img);
        ivSend = view.findViewById(R.id.ivSend);
        imgRotate = view.findViewById(R.id.ivRotate);
        imgReset = view.findViewById(R.id.ivReset);
        cropImageView = view.findViewById(R.id.CropImageView);
        cropImageView.setImageBitmap(getBitmap(edittedImagePath));
        cropImageView.setAspectRatio(5, 5);
        cropImageView.setFixedAspectRatio(true);

        ivSend.setOnClickListener(this);
        imgRotate.setOnClickListener(this);
        imgReset.setOnClickListener(this);
        img.setOnClickListener(this);

    }

    public Bitmap getBitmap(String path) {
        Bitmap bitmap;
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    private void openActivity(String cropImagePath) {
//        Bundle bundle = new Bundle();
//        bundle.putString("originalPath", edittedImagePath);
//        bundle.putString("destPath", cropImagePath);
//        bundle.putString("media_link", media_link);
//        bundle.putString("media_time", String.valueOf(media_time));
//        if(isFromChat || isFromGroupChatCreate){
//            MessageEvent messageEvent = new MessageEvent();
//            messageEvent.setType(MessageEvent.MessageType.MSG_IMAGE);
//            messageEvent.setSavedUrl(cropImagePath);
//            EventBus.getDefault().postSticky(messageEvent);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mActivity.finish();
//                }
//            }, 300);
//        }else{
//            ((CameraActivity)mActivity).openSendToFragment(bundle);
//        }

        Bundle bundle = new Bundle();
        bundle.putString("selectedImagePath", cropImagePath);
        ((CameraActivity)mActivity).openPhotoEditorFragment(bundle);
    }

    private  String persistImage(Bitmap bitmap, String name) {
        File filesDir = mActivity.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile.getAbsolutePath();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivSend:
                Bitmap bitmap  = cropImageView.getCroppedImage();
                Log.d("", "onClick: "+ bitmap);
                openActivity(persistImage(bitmap,"cropFile_"+ System.currentTimeMillis()));
                break;
            case R.id.ivReset:
                cropImageView.rotateImage(360);
                break;
            case R.id.ivRotate:
                cropImageView.rotateImage(90);
                break;
            case R.id.img:
                mActivity.onBackPressed();
                break;
        }
    }
}
