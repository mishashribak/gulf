package com.app.khaleeji.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.CameraActivity;
import com.app.khaleeji.Adapter.FontPickerAdapter;
import com.app.khaleeji.Adapter.PreviewSlidePagerAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.services.VideoUploadService;
import com.rtugeek.android.colorseekbar.ColorSeekBar;
import com.viewpagerindicator.PageIndicator;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Constants.AppConstants;
import Constants.SessionData;
import CustomView.CustomTextView;
import CustomView.ExoPlayerHelper;
import Utility.AlertDialog;
import Utility.GlobalVariable;
import Utility.ImagePicker;
import Utility.ProgressDialog;
import Utility.SavePref;
import Utility.Utils;
import Utility.videocompressor.Util;
import Utility.videocompressor.VideoCompress;
import photoEditor.EmbedURLFragment;
import photoEditor.LocationFragment;
import photoEditor.WheelPickerFragment;
import videoEditor.BrushDrawingView;
import videoEditor.OnPhotoEditorSDKListener;
import videoEditor.PhotoEditorSDK;
import videoEditor.ViewType;
import videoEditor.widget.SlidingUpPanelLayout;

public class VideoEditorFragment extends BaseFragment implements View.OnClickListener, OnPhotoEditorSDKListener{
    private View view;
    private final String TAG = "VideoEditorFragment";
    private final String CONTENT_TYPE_DAILY = "daily";
    private final String SAVE = "SAVE";
    private final String SEND = "SEND";
    ImageView ivTime;
    TextView tvTimeAprnc;
    RelativeLayout relAprncLayout;
    String recordedVideoPath;
    com.google.android.exoplayer2.ui.PlayerView videoView;
    ImageView ivSavePicture, ivSend, ivPostToDaily, ivDoneDrawing;
    ViewPager image_emoji_view_pager;
    ImageView embed_link_tv, ivUndoDrawing, ivSound, ivClose;
    List<String> timeApprncList = new ArrayList<>();
    Context context;
    long startTime, endTime;
    FFmpeg ffmpeg;
    String  removeAudioPath;
    String waterMarkPath="";
    int media_time = 3;
    String media_link = "";
    String slcnType;
    Typeface fontType;
    boolean isVideoSaved;
    ColorSeekBar colorSeekBar;
    EmbedURLFragment embedURLFragment;
    ImageView addTextView, addPencil, addImageEmojiTextView;
    String caption;
    private RelativeLayout parentImageRelativeLayout;
    private SlidingUpPanelLayout mLayout;
    private Typeface emojiFont;
    private View topShadow;
    private RelativeLayout topShadowRelativeLayout, relLinkDetailLayout;
    private View bottomShadow;
    private RelativeLayout bottomShadowRelativeLayout;
    private ArrayList<Integer> colorPickerColors;
    private int colorCodeTextView = -6052957;
    private PhotoEditorSDK photoEditorSDK;
    private String[] fontArray;
    private boolean isSoundOn = true;
    private boolean isFromChat = false, isFromGroupChat = false;
    private PreviewSlidePagerAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleJob(Context context, String slctnType, String videoPath,
                                   String waterMarkPath, boolean isSoundOn
            , int media_time, String media_link, String caption, boolean isFromGroupChat, boolean isFromChat) {
        PersistableBundle pb = new PersistableBundle();
        pb.putString("slectionType", slctnType);
        pb.putString("videoPath", videoPath);
        pb.putString("waterMarkPath", waterMarkPath);
        pb.putBoolean("isSoundOn", true);
        pb.putInt("media_time", media_time);
        pb.putString("media_link", media_link);
        pb.putString("caption", caption);
        pb.putBoolean("isFromGroupChat", isFromGroupChat);
        pb.putBoolean("isFromChat", isFromChat);

        ComponentName serviceComponent = new ComponentName(context, VideoUploadService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 1000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        builder.setExtras(pb);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresDeviceIdle(true); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public VideoEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStop() {
        if(exoPlayerHelper != null)
            exoPlayerHelper.killPlayer();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video_editor, container, false);
         initViews();
        return view;
    }

    private void initViews() {
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        context = mActivity;
        recordedVideoPath = getArguments().getString("recordedVideoPath");
        isFromChat = getArguments().getBoolean("isFromChat");
        isFromGroupChat = getArguments().getBoolean("isFromGroupChat");
        media_time = (int)getArguments().getLong("recordedTime");
        Log.e(TAG, "recordedVideoPath : " + recordedVideoPath);
        videoView = view.findViewById(R.id.player);
        emojiFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Amaranth_Regular.ttf");
        Typeface newFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Amaranth_Regular.ttf");

        BrushDrawingView brushDrawingView = (BrushDrawingView) view.findViewById(R.id.drawing_view);
        parentImageRelativeLayout = (RelativeLayout) view.findViewById(R.id.parent_image_rl);
        addTextView = (ImageView) view.findViewById(R.id.add_text_tv);
        addPencil = (ImageView) view.findViewById(R.id.add_pencil_tv);
        RelativeLayout deleteRelativeLayout = (RelativeLayout) view.findViewById(R.id.delete_rl);
        addImageEmojiTextView = (ImageView) view.findViewById(R.id.add_image_emoji_tv);
        colorSeekBar = (ColorSeekBar) view.findViewById(R.id.colorSeekBar);
        ImageView photoEditImageView = (ImageView) view.findViewById(R.id.photo_edit_iv);
        mLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        topShadow = view.findViewById(R.id.top_shadow);
        topShadowRelativeLayout = (RelativeLayout) view.findViewById(R.id.top_parent_rl);
        bottomShadow = view.findViewById(R.id.bottom_shadow);
        bottomShadowRelativeLayout = (RelativeLayout) view.findViewById(R.id.bottom_parent_rl);
        embed_link_tv = (ImageView) view.findViewById(R.id.embed_link_tv);
        tvTimeAprnc = view.findViewById(R.id.tvTimeAprnc);
        relAprncLayout = view.findViewById(R.id.relAprncLayout);
        ivTime = view.findViewById(R.id.ivTime);
        ivSavePicture = view.findViewById(R.id.ivSavePicture);
        ivSend = view.findViewById(R.id.ivSend);
        ivPostToDaily = view.findViewById(R.id.ivPostToDaily);
       // ivSaveToMemory = view.findViewById(R.id.ivSaveToMemory);

//        if(!isFromChat){
//            if(GlobalVariable.isFloatingDaily){
//                ivPostToDaily.setVisibility(View.VISIBLE);
//            }else{
//                ivPostToDaily.setVisibility(View.INVISIBLE);
//            }
//        }

        ivClose = view.findViewById(R.id.ivClose);
        ivDoneDrawing = view.findViewById(R.id.ivDoneDrawing);
        ivUndoDrawing = view.findViewById(R.id.ivUndoDrawing);
        ivSound = view.findViewById(R.id.ivSound);
        image_emoji_view_pager = view.findViewById(R.id.image_emoji_view_pager);
        PageIndicator indicator = view.findViewById(R.id.image_emoji_indicator);
        photoEditImageView.setBackground(new ColorDrawable(Color.TRANSPARENT));
        adapter = new PreviewSlidePagerAdapter(getFragmentManager(),
                SessionData.I().localData.getZipCategoryDataList(), mActivity
                , new PreviewSlidePagerAdapter.OnPagerItemClickListener() {
            @Override
            public void onPagerItemClick(int resId, String type) {
                if (type.equals(AppConstants.TYPE_LOCATION)) {
                    callLocationPage();
                    if (mLayout != null) {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    }
                } else {
                    addImage(resId);
                }
            }
        });
        image_emoji_view_pager.setAdapter(adapter);
//        getStickersFromFiles();
        indicator.setViewPager(image_emoji_view_pager);
        photoEditorSDK = new PhotoEditorSDK.PhotoEditorSDKBuilder(getActivity())
                .parentView(parentImageRelativeLayout) // add parent image view
                .childView(photoEditImageView) // add the desired image view
                .deleteView(deleteRelativeLayout) // add the deleted view that will appear during the movement of the views
                .brushDrawingView(brushDrawingView) // add the brush drawing view that is responsible for drawing on the image view
                .buildPhotoEditorSDK(); // build photo editor sdk
        photoEditorSDK.setOnPhotoEditorSDKListener(this);
        ivClose.setOnClickListener(this);
        addImageEmojiTextView.setOnClickListener(this);
        addTextView.setOnClickListener(this);
//        tvTimeAprnc.setOnClickListener(this);
        addPencil.setOnClickListener(this);
        embed_link_tv.setOnClickListener(this);
        ivSavePicture.setOnClickListener(this);
        ivTime.setOnClickListener(this);
        relAprncLayout.setOnClickListener(this);
        ivSend.setOnClickListener(this);
        ivPostToDaily.setOnClickListener(this);
       // ivSaveToMemory.setOnClickListener(this);
        ivDoneDrawing.setOnClickListener(this);
        ivUndoDrawing.setOnClickListener(this);
        ivSound.setOnClickListener(this);
        addColorsToList();
        addSnapAprncScnds();
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                photoEditorSDK.setBrushColor(color);
            }
        });
        if (SavePref.getInstance(getActivity()).getPref_lang().equalsIgnoreCase("en")) {
            fontArray = getResources().getStringArray(R.array.font_english_array);
        } else {
            fontArray = getResources().getStringArray(R.array.font_arabic_array);
        }
    }

    ExoPlayerHelper exoPlayerHelper;
    private void playVideo(String videoPath) {
        exoPlayerHelper = new ExoPlayerHelper(videoView, mActivity, videoPath, true);
        exoPlayerHelper.initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        if (recordedVideoPath != null) {
            playVideo(recordedVideoPath);
        } else {
            Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();
        }
    }


    private void compressVideo(final String inputPath, final String slcnType)   //, final String videoName, final String strCaption
    {
        final File compressedVideoFile = ImagePicker.getOutputVideoFile();
        final String compressedVideoPath = compressedVideoFile.getPath();
        VideoCompress.compressVideoHigh(inputPath, compressedVideoPath, new VideoCompress.CompressListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
               // performActionOnSlctn(compressedVideoPath, slcnType);
                endTime = System.currentTimeMillis();
                Util.writeFile(getContext(), "End at: " + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "\n");
                Util.writeFile(getContext(), "Total: " + ((endTime - startTime) / 1000) + "s" + "\n");
                Util.writeFile(getContext());
                Log.e(TAG, "compressedVideoFile size : " + Utils.getFileSizeInKb(compressedVideoFile));
            }

            @Override
            public void onFail() {
                if (isSoundOn) {
                    saveVideo(slcnType, inputPath, true);
                } else {
                    removeAudio(slcnType, inputPath);
                }

                Log.e(TAG, "Compress Failed!");
                endTime = System.currentTimeMillis();
                Util.writeFile(getContext(), "Failed Compress!!!" + new SimpleDateFormat("HH:mm:ss").format(new Date()));
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
        try {

            ffmpeg.execute(сmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    ProgressDialog.showProgress(getContext());
                }

                @Override
                public void onProgress(String message) {
                    Log.e(TAG, "onProgress message : " + message);

                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "onFailure message : " + message);
                    ProgressDialog.hideprogressbar();
                    Toast.makeText(getContext(), "Some error occured", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String message) {
                    Log.e(TAG, "onSuccess message : " + message);
                    saveVideo(slctnType, removeAudioPath, false);

                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            Log.e(TAG, "e.getMessage() : " + e.getMessage());
        }

    }

    private void addColorsToList() {
        colorPickerColors = new ArrayList<>();
        colorPickerColors.add(getResources().getColor(R.color.black));
        colorPickerColors.add(getResources().getColor(R.color.blue_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.brown_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.green_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.orange_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.red_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.red_orange_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.sky_blue_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.violet_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.white));
        colorPickerColors.add(getResources().getColor(R.color.yellow_color_picker));
        colorPickerColors.add(getResources().getColor(R.color.yellow_green_color_picker));
    }

    private void addSnapAprncScnds() {
        timeApprncList.add("1");
        timeApprncList.add("2");
        timeApprncList.add("3");
        timeApprncList.add("4");
        timeApprncList.add("5");
        timeApprncList.add("6");
        timeApprncList.add("7");
        timeApprncList.add("8");
        timeApprncList.add("9");
        timeApprncList.add("10");
    }

    private boolean stringIsNotEmpty(String string) {
        if (string != null && !string.equals("null")) {
            if (!string.trim().equals("")) {
                return true;
            }
        }
        return false;
    }

    public void addEmoji(String emojiName) {
        photoEditorSDK.addEmoji(emojiName, emojiFont);
        if (mLayout != null)
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void addImage(int resId) {
        photoEditorSDK.addImage(resId);
        if (mLayout != null)
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void addImage(Bitmap bitmap) {
        photoEditorSDK.addImage(bitmap);
        if (mLayout != null)
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void addText(String text, int colorCodeTextView, Typeface fontType) {
        photoEditorSDK.addText(text, colorCodeTextView, fontType);
    }

    private void clearAllViews() {
        photoEditorSDK.clearAllViews();
    }

    private void undoViews() {
        photoEditorSDK.viewUndo();
    }

    private void eraseDrawing() {
        photoEditorSDK.brushEraser();
        // photoEditorSDK.clearBrushAllViews();
    }

    private void undoDrawing() {
        photoEditorSDK.undoDrawing();
        // photoEditorSDK.clearBrushAllViews();
    }

    private void openAddTextPopupWindow(String text, int colorCode) {

        colorCodeTextView = colorCode;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addTextPopupWindowRootView = inflater.inflate(R.layout.add_text_popup_window, null);
        final EditText addTextEditText = (EditText) addTextPopupWindowRootView.findViewById(R.id.add_text_edit_text);
        //TextView addTextDoneTextView = (TextView) addTextPopupWindowRootView.findViewById(R.id.add_text_done_tv);
        ImageView ivAddTextDone = (ImageView) addTextPopupWindowRootView.findViewById(R.id.ivAddTextDone);

        RecyclerView addTextFontPickerRecyclerView = (RecyclerView) addTextPopupWindowRootView.findViewById(R.id.add_text_font_picker_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addTextFontPickerRecyclerView.setLayoutManager(layoutManager);
        addTextFontPickerRecyclerView.setHasFixedSize(true);
        FontPickerAdapter fontPickerAdapter = new FontPickerAdapter(getActivity(), fontArray);
        fontPickerAdapter.setOnFontPickerClickListener(new FontPickerAdapter.OnFontPickerClickListener() {
            @Override
            public void onFontPickerClickListener(int position) {
                Typeface newFont = Typeface.createFromAsset(getActivity().getAssets(), fontArray[position]);
                addTextEditText.setTypeface(newFont);
                fontType = newFont;
            }
        });
        addTextFontPickerRecyclerView.setAdapter(fontPickerAdapter);
        ColorSeekBar colorSeekBar = addTextPopupWindowRootView.findViewById(R.id.colorSeekBar);
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                Log.e(TAG, "color : " + color);
                if (color == 0) {
                    color = colorCodeTextView;
                }
                addTextEditText.setTextColor(color);
                colorCodeTextView = color;
            }
        });
        colorSeekBar.setColor(Color.WHITE);
        if (stringIsNotEmpty(text)) {
            addTextEditText.setText(text);
            addTextEditText.setTextColor(colorCode == -1 ? getResources().getColor(R.color.white) : colorCode);
        } else {
            colorSeekBar.setColorBarPosition(2);
            addTextEditText.setTextColor(getResources().getColor(R.color.white));
        }
        if (fontType != null) {
            addTextEditText.setTypeface(fontType);
        } else {
            fontType = Typeface.createFromAsset(getActivity().getAssets(), fontArray[0]);
            addTextEditText.setTypeface(fontType);
        }

        final PopupWindow pop = new PopupWindow(getActivity());
        pop.setContentView(addTextPopupWindowRootView);
        pop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        pop.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        pop.setFocusable(true);
        pop.setBackgroundDrawable(null);
        pop.showAtLocation(addTextPopupWindowRootView, Gravity.TOP, 0, 0);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        ivAddTextDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addTextEditText.getText().toString().isEmpty()) {
                    addText(addTextEditText.getText().toString(), colorCodeTextView, fontType);
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                pop.dismiss();
            }
        });

    }

    private void updateView(int visibility) {
        topShadow.setVisibility(visibility);
        topShadowRelativeLayout.setVisibility(visibility);
        bottomShadow.setVisibility(visibility);
        bottomShadowRelativeLayout.setVisibility(visibility);
    }

    private void updateBrushDrawingView(boolean brushDrawingMode) {
        photoEditorSDK.setBrushDrawingMode(brushDrawingMode);
        if (brushDrawingMode) {
            updateView(View.GONE);
            colorSeekBar.setVisibility(View.VISIBLE);
            ivDoneDrawing.setVisibility(View.VISIBLE);
            ivUndoDrawing.setVisibility(View.VISIBLE);
        } else {
            updateView(View.VISIBLE);
            colorSeekBar.setVisibility(View.GONE);
            ivDoneDrawing.setVisibility(View.GONE);
            ivUndoDrawing.setVisibility(View.GONE);
        }
    }

    private void callShareActivty(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("destPath", recordedVideoPath);
        bundle.putString("waterMarkPath", waterMarkPath);
        bundle.putBoolean("isSoundOn", true);
        bundle.putInt("media_time", media_time);
        bundle.putString("media_link", media_link);
        bundle.putBoolean("isFromGroupChat", isFromGroupChat);
        bundle.putBoolean("isVideo", true);
        ((CameraActivity)mActivity).openSendToFragment(bundle);
    }

    private void saveVideo(final String slctnType, final String videoPath, boolean showLoader) {

        new CountDownTimer(1000, 500) {
            public void onTick(long millisUntilFinished) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onFinish() {
//                String imageName = "IMG_" + System.currentTimeMillis() + ".png";
//                waterMarkPath = photoEditorSDK.saveImage("not required",
//                        imageName, mActivity);
                if (slctnType.equalsIgnoreCase("send") && !isFromChat ) {
                    callShareActivty(videoPath);
                    Log.d(TAG, "onFinish: " + videoPath);
                } else {
                    if(slctnType.equalsIgnoreCase(CONTENT_TYPE_DAILY)){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mActivity,getString(R.string.post_media_alert),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    scheduleJob(getActivity(), slctnType, videoPath,
                            waterMarkPath, isSoundOn, media_time, media_link, caption, isFromGroupChat, isFromChat);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(isFromChat){
                                MessageEvent messageEvent = new MessageEvent();
                                messageEvent.setType(MessageEvent.MessageType.SENDING_MSG_PROGRESS);
                                EventBus.getDefault().postSticky(messageEvent);
                                mActivity.finish();
                            }
                        }
                    }, 100);
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivClose) {
            callBackPress();
        } else if (v.getId() == R.id.add_image_emoji_tv) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        } else if (v.getId() == R.id.add_text_tv) {
            openAddTextPopupWindow("", colorCodeTextView);
        } else if (v.getId() == R.id.add_pencil_tv) {
            updateBrushDrawingView(true);
        } else if (v.getId() == R.id.ivDoneDrawing) {
            updateBrushDrawingView(false);
        } else if (v.getId() == R.id.ivSavePicture) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage(getString(R.string.save_confirm_dlg));
            builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    isVideoSaved = true;
                    //slcnType = "save";
                    slcnType = SAVE;
                    //Toast.makeText(getContext(), "Video Saved to gallery", Toast.LENGTH_SHORT).show();
                    saveVideo(slcnType, recordedVideoPath, true);

                }
            });
            builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();

        } else if (v.getId() == R.id.ivSend) {
            if(exoPlayerHelper != null)
                exoPlayerHelper.killPlayer();
            slcnType = SEND;
            //setTransferUtility();
            saveVideo(slcnType, recordedVideoPath, true);

        } else if (v.getId() == R.id.ivUndoDrawing) {
            undoDrawing();
        } else if (v.getId() == R.id.embed_link_tv) {
            showEmbedLink();
        } else if (v.getId() == R.id.ivPostToDaily) {
            slcnType = CONTENT_TYPE_DAILY;
            caption = "";
            saveVideo(slcnType, recordedVideoPath, true);

        } else if (v.getId() == R.id.ivSound) {
            performSoundClick();
        }else if ((v.getId() == R.id.relAprncLayout) || (v.getId() == R.id.ivTime) || (v.getId() == R.id.tvTimeAprnc)) {
            showWheelPicker();
        }
    }

    WheelPickerFragment wheelPickerDialogFragment;
    private void showWheelPicker() {
        hideEdittingIcons();

        wheelPickerDialogFragment = new WheelPickerFragment(media_time, new WheelPickerFragment.SnapAprncTimeInterface() {
            @Override
            public void snapAprncTime(int mediaTime) {
                media_time = mediaTime;

                if (mediaTime == 0) {
                    tvTimeAprnc.setText(getResources().getString(R.string.infinity));
                } else {
                    tvTimeAprnc.setText(String.valueOf(mediaTime));
                }

                showEdittingIcons();
            }
        });
        wheelPickerDialogFragment.setStyle(androidx.fragment.app.DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        wheelPickerDialogFragment.show(getFragmentManager(), "DialogFragment");

    }

    private void performSoundClick() {
        if (isSoundOn) {
            isSoundOn = false;
            ivSound.setImageResource(R.mipmap.sound_off_);
            if(exoPlayerHelper != null){
                exoPlayerHelper.setMute();
            }
        } else {
            isSoundOn = true;
            ivSound.setImageResource(R.mipmap.sound_on_);
            if(exoPlayerHelper != null)
                exoPlayerHelper.setUnMute();
        }
    }

    public void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEmbedLink() {
        hideEdittingIcons();
        embedURLFragment = EmbedURLFragment.newInstance(media_time, new EmbedURLFragment.EmbedURLToPhotoInterface() {
            @Override
            public void embedUrl(String url) {
                if (Utils.stringIsNotEmpty(url)) {
                    media_link = url;

                    embed_link_tv.setImageResource(R.mipmap.link_bg);
                } else {
                    media_link = "";
                }

                showEdittingIcons();
            }
        });
        embedURLFragment.setStyle(androidx.fragment.app.DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        embedURLFragment.show(getFragmentManager(), "DialogFragment");
    }

    private void hideEmbedLink() {
        embed_link_tv.setImageResource(R.mipmap.link_bg);
        if (mLayout != null) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        clearLinkData();
        image_emoji_view_pager.setVisibility(View.VISIBLE);
    }

    private void clearLinkData() {
        relLinkDetailLayout.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onEditTextChangeListener(String text, int colorCode) {
        openAddTextPopupWindow(text, colorCode);
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        if (numberOfAddedViews > 0) {
        }
        switch (viewType) {
            case BRUSH_DRAWING:
                Log.i("BRUSH_DRAWING", "onAddViewListener");
                break;
            case EMOJI:
                Log.i("EMOJI", "onAddViewListener");
                break;
            case IMAGE:
                Log.i("IMAGE", "onAddViewListener");
                break;
            case TEXT:
                Log.i("TEXT", "onAddViewListener");
                break;
        }
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
        Log.i(TAG, "onRemoveViewListener");
        if (numberOfAddedViews == 0) {
        }
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        switch (viewType) {
            case BRUSH_DRAWING:
                Log.i("BRUSH_DRAWING", "onStartViewChangeListener");
                break;
            case EMOJI:
                Log.i("EMOJI", "onStartViewChangeListener");
                break;
            case IMAGE:
                Log.i("IMAGE", "onStartViewChangeListener");
                break;
            case TEXT:
                Log.i("TEXT", "onStartViewChangeListener");
                break;
        }
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        switch (viewType) {
            case BRUSH_DRAWING:
                Log.i("BRUSH_DRAWING", "onStopViewChangeListener");
                break;
            case EMOJI:
                Log.i("EMOJI", "onStopViewChangeListener");
                break;
            case IMAGE:
                Log.i("IMAGE", "onStopViewChangeListener");
                break;
            case TEXT:
                Log.i("TEXT", "onStopViewChangeListener");
                break;
        }
    }

    private void callBackPress() {

        if (isVideoSaved) {
            if(exoPlayerHelper != null)
                exoPlayerHelper.killPlayer();
            openCameraKitFragment();
        } else {
            AlertDialog.showAlert(getActivity(),
                    getString(R.string.app_name),
                    getString(R.string.discardVideo),
                    getString(R.string.txt_yes),
                    getString(R.string.cancel),
                    true, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(exoPlayerHelper != null)
                                exoPlayerHelper.killPlayer();
                            if (!recordedVideoPath.isEmpty()) {
                                File pictureFile = new File(recordedVideoPath);

                                if (pictureFile.exists()) {
                                    pictureFile.delete();
                                }
                            }
                            openCameraKitFragment();
                        }
                    }, null);
        }
    }

    private void openCameraKitFragment() {
        CameraKitFragment fragment = new CameraKitFragment(context, isFromChat, false);
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_main, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    public void callLocationPage() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFromVideoEditorActivity", true);
        FragmentManager fragmentManager = getFragmentManager();
        LocationFragment locationFragment = new LocationFragment(new LocationFragment.OnStrickerClickListener() {
            @Override
            public void onStickerClick(Bitmap bitmap, String type) {
                mActivity.onBackPressed();
                addImage(bitmap);
            }
        });
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        locationFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.framelayout_main, locationFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void hideEdittingIcons() {
        ivClose.setVisibility(View.GONE);
        addTextView.setVisibility(View.GONE);
        addPencil.setVisibility(View.GONE);
        addImageEmojiTextView.setVisibility(View.GONE);
        embed_link_tv.setVisibility(View.GONE);
        ivSend.setVisibility(View.GONE);
        ivSavePicture.setVisibility(View.GONE);
//        ivPostToDaily.setVisibility(View.GONE);
//        ivSaveToMemory.setVisibility(View.GONE);
    }

    private void showEdittingIcons() {
        ivClose.setVisibility(View.VISIBLE);
        addTextView.setVisibility(View.VISIBLE);
        addPencil.setVisibility(View.VISIBLE);
        addImageEmojiTextView.setVisibility(View.VISIBLE);
        embed_link_tv.setVisibility(View.VISIBLE);
        ivSend.setVisibility(View.VISIBLE);
        ivSavePicture.setVisibility(View.VISIBLE);
//        ivPostToDaily.setVisibility(View.VISIBLE);
//        ivSaveToMemory.setVisibility(View.VISIBLE);
    }


}
