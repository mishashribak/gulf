package com.app.khaleeji.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;

import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentImagefullBinding;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import Constants.Bundle_Identifier;
import Utility.AlertDialog;
import Utility.ImagePicker;

/**
 * intro
 */
public class ImageFullScreenFragment extends DialogFragment implements View.OnClickListener{

    private final String TAG = ImageFullScreenFragment.class.getName();
    private String murl;
    private FragmentImagefullBinding mbinding;

    public ImageFullScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IntroFragment.
     */
    public static ImageFullScreenFragment newInstance(String url) {
        ImageFullScreenFragment fragment = new ImageFullScreenFragment();
        Bundle args = new Bundle();
        args.putString(Bundle_Identifier.IMAGEURL, url);
        Log.e("ImageFullScreenFragment","url : " + url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            murl = getArguments().getString(Bundle_Identifier.IMAGEURL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_imagefull, container, false);
        View view = mbinding.getRoot();
        initView();
        return view;
    }

    /*set Background button and text of tutorial*/
    private void initView() {
        mbinding.ivCross.setOnClickListener(this);
        mbinding.ivSave.setOnClickListener(this);

        if (murl != null && !murl.equalsIgnoreCase("")) {
            if(new File(murl).exists()){
                Picasso.with(getActivity()).load(new File(murl)).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).into(mbinding.ivShowimage);

            }
            else
            Picasso.with(getActivity()).load(murl).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).into(mbinding.ivShowimage);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_save:
                if (murl != null && !murl.equalsIgnoreCase("")) {
                    if(new File(murl).exists()) {
                        AlertDialog.showAlert(getActivity(), getString(R.string.app_name), getString(R.string.imageSaved) + " " + murl, getString(R.string.txt_Done), getString(R.string.cancel), false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ImageFullScreenFragment.this.dismiss();

                            }

                        }, null);
                    }
                    else
                     Picasso.with(getActivity()).load(murl).into(picassoImageTarget(getActivity()));
                 }
                break;
            case R.id.iv_cross:
               dismiss();
                break;
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_trans)));
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            if (dialog != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_trans)));
            }
        }
    }

    private Target picassoImageTarget(Context context) {
        Log.d("picassoImageTarget", " picassoImageTarget");
        ContextWrapper cw = new ContextWrapper(context);
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = ImagePicker.getOutputMediaFile(); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
//                                ProgressDialog.hideprogressbar();

                                AlertDialog.showAlert(getActivity(), getString(R.string.app_name), getString(R.string.imageSaved) + " " + myImageFile.getAbsolutePath(), getString(R.string.txt_Done), getString(R.string.cancel), false, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        ImageFullScreenFragment.this.dismiss();

                                    }

                                }, null);
                                Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
//                ProgressDialog.hideprogressbar();

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {
//                    ProgressDialog.showProgress(getActivity());
                }
            }
        };
    }
}