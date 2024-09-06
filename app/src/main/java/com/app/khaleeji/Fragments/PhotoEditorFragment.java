package com.app.khaleeji.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.CameraActivity;
import com.app.khaleeji.Adapter.FontPickerAdapter;
import com.app.khaleeji.Adapter.PreviewSlidePagerAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.AddMemoryResponse;
import com.app.khaleeji.services.UploadService;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.rtugeek.android.colorseekbar.ColorSeekBar;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.PageIndicator;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Constants.SessionData;
import CustomView.CustomEditText;
import CustomView.CustomTextView;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.DebugLog;
import Utility.GlobalVariable;
import Utility.ProgressDialog;
import Utility.SavePref;
import Utility.Utils;
import photoEditor.BrushDrawingView;
import photoEditor.EmbedURLFragment;
import photoEditor.LocationFragment;
import photoEditor.OnPhotoEditorSDKListener;
import photoEditor.PhotoEditorSDK;
import photoEditor.ViewType;
import photoEditor.WheelPickerFragment;
import photoEditor.widget.SlidingUpPanelLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class  PhotoEditorFragment extends BaseFragment implements View.OnClickListener, OnPhotoEditorSDKListener {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
    private final String TAG = "PhotoEditorFragment";
    private final String CONTENT_TYPE_DAILY = "daily";
    private final String CONTENT_TYPE_MEMORY = "memory";
    private ImageView ivCloseLinkView, ivSearchLink, ivLinkLogo;
    private CustomTextView btnAttachLink;
    private TextView tvTimeAprnc;
    private ImageView ivTime, ivSavePicture, ivSend, ivPostToDaily, ivDoneDrawing, ivSaveToMemory, ivClose;
    private ViewPager image_emoji_view_pager;
    private TextCrawler textCrawler;
    private LinkPreviewCallback linkPreviewCallback;
    private ColorSeekBar colorSeekBar;
    private ImageView ivCloseWheelPicker, embed_link_tv, ivUndoDrawing;
    private ImageView photoEditImageView;   //ivClearAllViews,ivUndoView,
    private List<String> timeApprncList = new ArrayList<>();
    private Context context;
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private String edittedImagePath;
    private  String finalUrl;
    private int media_time;
    private String media_link = "";
    private PageIndicator indicator;
    private String slcnType;
    private Typeface fontType;
    private boolean isPicSaved;
    private String selectedImagePath;
    private ImageView addTextView, addPencil, addImageEmojiTextView;
    private WheelPickerFragment wheelPickerDialogFragment;
    private EmbedURLFragment embedURLFragment;
    private RelativeLayout relAprncLayout;
    private ExifInterface exif;
    private Dialog addCaptionDialog;
    private RelativeLayout parentImageRelativeLayout;
    private TextView eraseDrawingTextView, tvLinkName, tvLinkDesc;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private Typeface emojiFont;
    private View topShadow;
    private RelativeLayout topShadowRelativeLayout, relLinkDetailLayout;
    private View bottomShadow;
    private RelativeLayout bottomShadowRelativeLayout;
    private ArrayList<Integer> colorPickerColors;
    private int colorCodeTextView = Color.WHITE;
    private PhotoEditorSDK photoEditorSDK;
    private String[] fontArray;
    private int DEFAULT_TIME_APRNC = 3;
    private boolean isFromChat, isFromGroupChatCreate;
    private PreviewSlidePagerAdapter adapter;
    private DatabaseReference myRef1;
    private FrameLayout photoFrame;
    private ProgressBar mProgressBar;
    private View view;
    private Bundle bundle;
    private Context mContext;

    public PhotoEditorFragment(){

    }

    public PhotoEditorFragment(Context ctx) {
        // Required empty public constructor
        mContext = ctx;
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        mtx.setRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photo_editor, container, false);
        // Inflate the layout for this fragment
        initView();
        return view;
    }


    public Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initView() {
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        context = mActivity;
        bundle = getArguments();
        if (bundle == null) {
            return;
        }
        photoEditImageView = view.findViewById(R.id.photo_edit_iv);
        selectedImagePath  = bundle.getString("selectedImagePath");
        isFromChat         = bundle.getBoolean("isFromChat");
        isFromGroupChatCreate    = bundle.getBoolean("isFromGroupChatCreate");
        mProgressBar = view.findViewById(R.id.mProgress_bar);
        emojiFont = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Amaranth_Regular.ttf");
        BrushDrawingView brushDrawingView = view.findViewById(R.id.drawing_view);
        parentImageRelativeLayout = view.findViewById(R.id.parent_image_rl);
        addTextView = view.findViewById(R.id.add_text_tv);
        photoFrame = view.findViewById(R.id.photoContainer);
        addPencil = view.findViewById(R.id.add_pencil_tv);
        RelativeLayout deleteRelativeLayout = view.findViewById(R.id.delete_rl);
        addImageEmojiTextView = view.findViewById(R.id.add_image_emoji_tv);
        slidingUpPanelLayout = view.findViewById(R.id.sliding_layout);
        topShadow = view.findViewById(R.id.top_shadow);
        topShadowRelativeLayout = view.findViewById(R.id.top_parent_rl);
        bottomShadow = view.findViewById(R.id.bottom_shadow);
        bottomShadowRelativeLayout = view.findViewById(R.id.bottom_parent_rl);
        embed_link_tv = view.findViewById(R.id.embed_link_tv);
        ivTime = view.findViewById(R.id.ivTime);
        ivSavePicture = view.findViewById(R.id.ivSavePicture);
        ivSend = view.findViewById(R.id.ivSend);
        ivPostToDaily = view.findViewById(R.id.ivPostToDaily);
        ivSaveToMemory = view.findViewById(R.id.ivSaveToMemory);
        ivClose = view.findViewById(R.id.ivClose);
        ImageView ivCloseSticker = view.findViewById(R.id.imgCloseSticker);
        ivCloseSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slidingUpPanelLayout != null) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }
            }
        });
        colorSeekBar = view.findViewById(R.id.colorSeekBar);
        tvTimeAprnc = view.findViewById(R.id.tvTimeAprnc);
        relAprncLayout = view.findViewById(R.id.relAprncLayout);

//        if(!isFromChat){
//            if(GlobalVariable.isFloatingDaily){
//                ivPostToDaily.setVisibility(View.VISIBLE);
//            }else{
//                ivPostToDaily.setVisibility(View.INVISIBLE);
//            }
//        }

////////////////////////////////////////////////////////////////////////////////////
//        Bitmap bitmap = getBitmap(selectedImagePath);
//
//        ExifInterface exif = null;
//        try {
//            exif = new ExifInterface(selectedImagePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                ExifInterface.ORIENTATION_UNDEFINED);
//
//        Bitmap bmRotated = rotateBitmap(bitmap, orientation);

        Glide.with(context).load(selectedImagePath).fitCenter().into(photoEditImageView);

//        photoEditImageView.setImageBitmap(bmRotated);
 ////////////////////////////////////////////////////////////////////////////////////////

        ivDoneDrawing = view.findViewById(R.id.ivDoneDrawing);
        ivUndoDrawing = view.findViewById(R.id.ivUndoDrawing);
        slidingUpPanelLayout.setMinFlingVelocity(1000);
        image_emoji_view_pager = (ViewPager) view.findViewById(R.id.image_emoji_view_pager);
        indicator = (PageIndicator) view.findViewById(R.id.image_emoji_indicator);

        photoEditorSDK = new PhotoEditorSDK.PhotoEditorSDKBuilder(mActivity)
                .parentView(parentImageRelativeLayout) // add parent image view
                .childView(photoEditImageView) // add the desired image view
                .deleteView(deleteRelativeLayout) // add the deleted view that will appear during the movement of the views
                .brushDrawingView(brushDrawingView) // add the brush drawing view that is responsible for drawing on the image view
                .buildPhotoEditorSDK(); // build photo editor sdk
        photoEditorSDK.setOnPhotoEditorSDKListener(this);
        photoEditorSDK.setBrushColor(Color.BLACK);

        ivSend.setOnClickListener(this);
//        tvTimeAprnc.setOnClickListener(this);
        ivTime.setOnClickListener(this);
        relAprncLayout.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        addImageEmojiTextView.setOnClickListener(this);
        addTextView.setOnClickListener(this);
        addPencil.setOnClickListener(this);
        embed_link_tv.setOnClickListener(this);
        ivSaveToMemory.setOnClickListener(this);
        ivPostToDaily.setOnClickListener(this);
        ivDoneDrawing.setOnClickListener(this);
        ivUndoDrawing.setOnClickListener(this);
        ivSavePicture.setOnClickListener(this);
        addColorsToList();
        addSnapAprncScnds();
        media_time = DEFAULT_TIME_APRNC;
        tvTimeAprnc.setText(String.valueOf(DEFAULT_TIME_APRNC));
        textCrawler = new TextCrawler();

        linkPreviewCallback = new LinkPreviewCallback() {
            @Override
            public void onPre() {
                ProgressDialog.showProgress(context);
            }

            @Override
            public void onPos(SourceContent sourceContent, boolean isNull) {
                ProgressDialog.hideprogressbar();
                if (isNull || sourceContent.getFinalUrl().equals("")) {
                    tvLinkName.setText("Invalid URL");
                    tvLinkDesc.setVisibility(View.GONE);
                    ivLinkLogo.setVisibility(View.INVISIBLE);
                    btnAttachLink.setVisibility(View.GONE);
                } else {
                    tvLinkDesc.setVisibility(View.VISIBLE);
                    ivLinkLogo.setVisibility(View.VISIBLE);
                    btnAttachLink.setVisibility(View.VISIBLE);
                    media_link = sourceContent.getUrl();
                    Picasso.with(mActivity)
                            .load(sourceContent.getImages().get(0))
                            .into(ivLinkLogo);
                    tvLinkName.setText(sourceContent.getTitle());
                    if (sourceContent.getTitle().equalsIgnoreCase("google")) {
                        tvLinkDesc.setText(" © 2018 - Privacy - Terms");
                    } else {
                        tvLinkDesc.setText(sourceContent.getDescription());
                    }
                    relLinkDetailLayout.setVisibility(View.VISIBLE);
                }
            }
        };


        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                photoEditorSDK.setBrushColor(color);
            }
        });

        fontArray = getResources().getStringArray(R.array.font_english_array);

        credentialsProvider();
        setTransferUtility();

        adapter = new PreviewSlidePagerAdapter(getFragmentManager()
                , SessionData.I().localData.getZipCategoryDataList(), mActivity,
                new PreviewSlidePagerAdapter.OnPagerItemClickListener() {
                    @Override
                    public void onPagerItemClick(int resId, String type) {
                        if (type.equals(AppConstants.TYPE_LOCATION)) {
                            callLocationPage();
                            if (slidingUpPanelLayout != null) {
                                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                            }
                        } else {
                            addImage(resId, false);
                        }
                    }
                });
    }

    private void setPagerAdapter() {
        Log.d("PhotoEditorSDK", "setPagerAdapter: " + SessionData.I().localData.getZipCategoryDataList().size());
        adapter = new PreviewSlidePagerAdapter(getFragmentManager()
                , SessionData.I().localData.getZipCategoryDataList(), mActivity,
                new PreviewSlidePagerAdapter.OnPagerItemClickListener() {
                    @Override
                    public void onPagerItemClick(int resId, String type) {
                        if (type.equals(AppConstants.TYPE_LOCATION)) {
                            callLocationPage();
                            if (slidingUpPanelLayout != null) {
                                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                            }
                        } else {
                            addImage(resId, false);
                        }
                    }
                });

        image_emoji_view_pager.setAdapter(adapter);
        indicator.setViewPager(image_emoji_view_pager);
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


    private Fragment getCurrentChildFragment() {
        int index = mActivity.getSupportFragmentManager().getBackStackEntryCount() - 1;
        FragmentManager.BackStackEntry backEntry = mActivity.getSupportFragmentManager().getBackStackEntryAt(index);
        String tag = backEntry.getName();
        Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
        return fragment;
    }


    public void transferObserverListener(TransferObserver transferObserver, final String caption, final String contentType) {
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    callAddMemories(caption, contentType);
                } else if (TransferState.FAILED == state) {
                    ProgressDialog.hideprogressbar();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.e("zzz", "onProgressChanged : " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, "transferObserverListener onError : " + ex.getMessage());
            }

        });
    }

    private void callAddMemories(String caption, String contentType) {

        //ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        Map mparams = new HashMap();

        //contentType = "daily";

        Log.e(TAG, "callAddMemories USER_ID : " + SavePref.getInstance(mActivity)
                .getUserdetail().getId().toString());
        Log.e(TAG, "callAddMemories url : " + finalUrl);
        Log.e(TAG, "callAddMemories contentType : " + contentType);
        Log.e(TAG, "callAddMemories media_type : " + "image");
        Log.e(TAG, "callAddMemories caption : " + caption);
        Log.e(TAG, "callAddMemories media_link : " + media_link);
        Log.e(TAG, "callAddMemories media_time : " + media_time);
        //  Log.e(TAG, "callAddMemories thumbUrl : " + thumbUrl);

        mparams.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(mActivity)
                .getUserdetail().getId().toString());
        mparams.put(ApiClass.getmApiClass().url, finalUrl);
        mparams.put(ApiClass.getmApiClass().content_type, contentType);
        mparams.put(ApiClass.getmApiClass().media_type, "image");
        mparams.put(ApiClass.getmApiClass().caption, caption);
        mparams.put(ApiClass.getmApiClass().media_link, media_link);
        mparams.put(ApiClass.getmApiClass().media_time, media_time);


        //  mparams.put(ApiClass.getmApiClass().thumbnail, thumbUrl);


        Call<AddMemoryResponse> call = mApiInterface.addMemoryService(mparams);

        call.enqueue(new Callback<AddMemoryResponse>() {
            @Override
            public void onResponse(Call<AddMemoryResponse> call, Response<AddMemoryResponse> response) {
                ProgressDialog.hideprogressbar();

                if (response.isSuccessful()) {
                    AddMemoryResponse mOtpResponse = response.body();

                    if (mOtpResponse != null) {
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {
                            String msg = "";
                            switch (slcnType) {
                                case CONTENT_TYPE_DAILY:
                                    msg = "Picture Posted to Daily";
                                    break;

                                case CONTENT_TYPE_MEMORY:

                                    msg = "Picture Posted to Memory";

                                    break;
                            }
                            Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
//                            AlertDialog.showAlert(mActivity,
//                                    getString(R.string.app_name), msg,
//                                    getString(R.string.txt_ok),
//                                    getString(R.string.cancel),
//                                    false, null, null);
                        } else {
                            Toast.makeText(context,mOtpResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name)
//                                    , mOtpResponse.getMessage(), getString(R.string.txt_Done)
//                                    , getString(R.string.cancel), false, null, null);
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<AddMemoryResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();

                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }

    public void callLocationPage() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFromVideoEditorActivity", false);
        FragmentManager fragmentManager = getFragmentManager();
        LocationFragment locationFragment = new LocationFragment(new LocationFragment.OnStrickerClickListener() {
            @Override
            public void onStickerClick(Bitmap bitmap, String type) {
                mActivity.onBackPressed();
                addImage(bitmap, true);
            }
        });

        locationFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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
        relAprncLayout.setVisibility(View.GONE);
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
        relAprncLayout.setVisibility(View.VISIBLE);
        ivSend.setVisibility(View.VISIBLE);
        ivSavePicture.setVisibility(View.VISIBLE);
//        ivPostToDaily.setVisibility(View.VISIBLE);
//        ivSaveToMemory.setVisibility(View.VISIBLE);
    }

    private void addCaptionDialog() {
        Log.e(TAG, "addCaptionDialog");
        addCaptionDialog = new Dialog(context);
        // Include dialog.xml file
        addCaptionDialog.setContentView(R.layout.dialog_add_caption);
        addCaptionDialog.setTitle("GulfLink");
        addCaptionDialog.show();
        // Set dialog title
        final CustomEditText etComnt = (CustomEditText) addCaptionDialog.findViewById(R.id.etComnt);
        CustomTextView tvCancel = addCaptionDialog.findViewById(R.id.tvCancel);
        CustomTextView tvUpload = addCaptionDialog.findViewById(R.id.tvUpload);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCaptionDialog.dismiss();
            }
        });

        tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etComnt.getText().toString().isEmpty()) {
                    Toast.makeText(context,getString(R.string.addComment), Toast.LENGTH_SHORT).show();
//                    AlertDialog.showAlert(context, getString(R.string.app_name), getString(R.string.addComment), getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                } else {
                    if (CheckConnection.isNetworkAvailable(context)) {
                        String caption = etComnt.getText().toString();

                        slcnType = CONTENT_TYPE_MEMORY;
                        saveImage(CONTENT_TYPE_MEMORY, caption);
                        addCaptionDialog.dismiss();
                        //reportService(etComnt.getText().toString().trim());
                    } else {
                        Toast.makeText(context,getString(R.string.network_down), Toast.LENGTH_SHORT).show();
//                        AlertDialog.showAlert(context, getString(R.string.app_name), getString(R.string.network_down), getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                    }
                }
            }
        });
    }

    public void credentialsProvider() {
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
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
        timeApprncList.add(getResources().getString(R.string.infinity));
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
        if (slidingUpPanelLayout != null)
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    public void addImage(Bitmap image, boolean isLocationLabel) {

        photoEditorSDK.addImage(image, isLocationLabel);
        if (slidingUpPanelLayout != null)
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    public void addImage(int  resId, boolean isLocationLabel) {

        photoEditorSDK.addImage(resId, isLocationLabel);
        if (slidingUpPanelLayout != null)
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    private void addText(String text, int colorCodeTextView, Typeface fontType) {
        photoEditorSDK.addText(text, colorCodeTextView, fontType);
    }

    private void undoDrawing() {
        photoEditorSDK.undoBrushDrawing();
    }

    private void openAddTextPopupWindow(String text, int colorCode) {
        colorCodeTextView = colorCode;
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addTextPopupWindowRootView = inflater.inflate(R.layout.add_text_popup_window, null);
        final EditText addTextEditText = (EditText) addTextPopupWindowRootView.findViewById(R.id.add_text_edit_text);
        ImageView ivAddTextDone = (ImageView) addTextPopupWindowRootView.findViewById(R.id.ivAddTextDone);
        RecyclerView addTextFontPickerRecyclerView = (RecyclerView) addTextPopupWindowRootView.findViewById(R.id.add_text_font_picker_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        addTextFontPickerRecyclerView.setLayoutManager(layoutManager);
        FontPickerAdapter fontPickerAdapter = new FontPickerAdapter(mActivity, fontArray);
        fontPickerAdapter.setOnFontPickerClickListener(new FontPickerAdapter.OnFontPickerClickListener() {
            @Override
            public void onFontPickerClickListener(int position) {
                Typeface newFont = Typeface.createFromAsset(mActivity.getAssets(), fontArray[position]);
                addTextEditText.setTypeface(newFont);
                fontType = newFont;
            }
        });

        addTextFontPickerRecyclerView.setAdapter(fontPickerAdapter);
        ColorSeekBar colorSeekBar = (ColorSeekBar) addTextPopupWindowRootView.findViewById(R.id.colorSeekBar);

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

        colorSeekBar.setColorBarPosition(7);

        if (fontType != null) {
            addTextEditText.setTypeface(fontType);
        } else {
            Typeface newFont = Typeface.createFromAsset(mActivity.getAssets(), fontArray[0]);
            fontType = newFont;
            addTextEditText.setTypeface(fontType);
        }

        final PopupWindow pop = new PopupWindow(mActivity);
        pop.setContentView(addTextPopupWindowRootView);
        pop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        pop.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        pop.setFocusable(true);
        pop.setBackgroundDrawable(null);
        pop.showAtLocation(addTextPopupWindowRootView, Gravity.TOP, 0, 0);
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        ivAddTextDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addTextEditText.getText().toString().isEmpty()) {
                    addText(addTextEditText.getText().toString(), colorCodeTextView, fontType);
                }
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        ivSend.setVisibility(visibility);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleImageUploadJob( Context context,String slctnType, String videoPath) {
        PersistableBundle pb = new PersistableBundle();
        pb.putString("slectionType", slctnType);
        pb.putString("videoPath", videoPath);
//        pb.putInt("media_time", media_time);
//        pb.putString("media_link", media_link);
//        pb.putString("caption", caption);
//        pb.putBoolean("isFromGroupChat", isFromGroupChat);
//        pb.putBoolean("isDailiesSel", isDailiesSel);
//        pb.putBoolean("isMemoriesSel", isMemoriesSel);
//        pb.putBoolean("isHotMemoriesSel", isHotMemoriesSel);
//        pb.putBoolean("isHotDailiesSel", isHotDailiesSel);
//        pb.putInt("hotSpotId", hotSpotId);


        ComponentName serviceComponent = new ComponentName(context, UploadService.class);
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

    public void saveImage(final String slctnType, final String caption) {
        ProgressDialog.showProgress(context);
        new CountDownTimer(1000, 500) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                String imageName = "IMG_" + System.currentTimeMillis() + ".jpg";
                edittedImagePath = photoEditorSDK.saveImage("not required", imageName,
                        mActivity, false);
                ProgressDialog.hideprogressbar();
                switch (slctnType) {
                    case "save":
                        photoEditorSDK.saveImage("not required", imageName,
                                mActivity, true);
                        break;
                    case CONTENT_TYPE_DAILY:
                        ProgressDialog.hideprogressbar();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mActivity,getString(R.string.post_media_alert),Toast.LENGTH_SHORT).show();
                            }
                        });
                        scheduleImageUploadJob( mActivity, "daily", edittedImagePath);
                        break;
                    case "send":
                        ProgressDialog.hideprogressbar();

                        Bundle bundle = new Bundle();
                        bundle.putString("originalPath", edittedImagePath);
                        bundle.putString("destPath", edittedImagePath);
                        bundle.putString("media_link", media_link);
                        bundle.putInt("media_time", media_time);
                        ((CameraActivity)mActivity).openSendToFragment(bundle);

                        break;

//                    case CONTENT_TYPE_DAILY:
//                        if (edittedImagePath.isEmpty() || edittedImagePath != null) {
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.post_media_alert), getString(R.string.txt_ok), "", false, null, null);
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    setFileToUpload(edittedImagePath);
//                                }
//                            }, 200);
//                            setFileToUpload(edittedImagePath, "", CONTENT_TYPE_DAILY);
//                        }
//                        break;
//
//                    case CONTENT_TYPE_MEMORY:
//                        if (edittedImagePath.isEmpty() || edittedImagePath != null) {
//                            setFileToUpload(edittedImagePath, caption, CONTENT_TYPE_MEMORY);
//                        }
//                        break;
                }
            }
        }.start();
    }

    private void showAlertDialog() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder =
                new androidx.appcompat.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle("Saved!");
        alertDialogBuilder.setMessage("Your image has been saved to your photos.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });

        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void callBackPress() {
        if (isPicSaved) {
            openCameraKitFragment();
        } else {
            AlertDialog.showAlert(mActivity,
                    getString(R.string.app_name),
                    getString(R.string.discardPicture),
                    getString(R.string.txt_yes),
                    getString(R.string.cancel),
                    true, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!selectedImagePath.isEmpty()) {
                                File pictureFile = new File(selectedImagePath);
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
        CameraKitFragment fragment = new CameraKitFragment(mContext, isFromChat, isFromGroupChatCreate);
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_main, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivClose) {
            callBackPress();
        } else if (v.getId() == R.id.add_image_emoji_tv) {
            Log.d("hello", "onClick: " + "Sticker clicked");
            setPagerAdapter();
            adapter.notifyDataSetChanged();
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        } else if (v.getId() == R.id.add_text_tv) {
            openAddTextPopupWindow("", colorCodeTextView);
        } else if (v.getId() == R.id.add_pencil_tv) {
            updateBrushDrawingView(true);
        } else if (v.getId() == R.id.ivDoneDrawing) {
            updateBrushDrawingView(false);
        } else if (v.getId() == R.id.ivSavePicture) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage(getString(R.string.save_confirm_dlg));
            builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    isPicSaved = true;

                    slcnType = "save";
                    saveImage("save", "");

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

            slcnType = "send";
            saveImage("send", "");

        } else if (v.getId() == R.id.ivUndoDrawing) {
            undoDrawing();
        } else if (v.getId() == R.id.embed_link_tv) {
            showEmbedLink();
        } else if (v.getId() == R.id.ivCloseLinkView) {
            hideEmbedLink();
        } else if (v.getId() == R.id.ivSearchLink) {

        } else if ((v.getId() == R.id.relAprncLayout) || (v.getId() == R.id.ivTime) || (v.getId() == R.id.tvTimeAprnc)) {
            showWheelPicker();
        } else if (v.getId() == R.id.ivCloseWheelPicker) {
            hideWheelPicker();
        } else if (v.getId() == R.id.btnAttachLink) {
            hideEmbedLink();
        } else if (v.getId() == R.id.ivPostToDaily) {
            saveImage(CONTENT_TYPE_DAILY, "");

        } else if (v.getId() == R.id.ivSaveToMemory) {
//            addCaptionDialog();
        }

    }

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

    private void hideWheelPicker() {
        wheelPickerDialogFragment.dismiss();
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

                Log.e(TAG, "media_link : " + media_link);

                Log.e(TAG, "url : " + url);

                showEdittingIcons();
            }
        });
        embedURLFragment.setStyle(androidx.fragment.app.DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        embedURLFragment.show(getFragmentManager(), "DialogFragment");
    }

    private void hideEmbedLink() {
        embed_link_tv.setImageResource(R.mipmap.link_bg);
        if (slidingUpPanelLayout != null) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
        clearLinkData();
        image_emoji_view_pager.setVisibility(View.VISIBLE);
    }

    private void clearLinkData() {
        relLinkDetailLayout.setVisibility(View.INVISIBLE);
    }

}
