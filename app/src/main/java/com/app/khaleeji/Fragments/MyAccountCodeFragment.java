package com.app.khaleeji.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Activity.SimpleScannerActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.GulfProfiledata;
import com.app.khaleeji.databinding.FragmentAccountCodeBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import CustomView.SelectSharePopupWindow;
import CustomView.SingleMediaScanner;
import Utility.SavePref;

public class MyAccountCodeFragment extends BaseFragment {
    private static final String TAG = MyAccountCodeFragment.class.getSimpleName();
    private FragmentAccountCodeBinding mbinding;
    private  Bitmap bitmap = null;
    private Uri bmpUri;
    private GulfProfiledata gulfProfiledata;
    private SelectSharePopupWindow selectPicPopupWindow;
    private View rootView;

    public MyAccountCodeFragment(){

    }

    public MyAccountCodeFragment(GulfProfiledata gulfProfiledata){
        this.gulfProfiledata = gulfProfiledata;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_code, container, false);
        rootView = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private void initView() {

        ((MainActivity)mActivity).hide();
        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).back();
            }
        });
        mbinding.imgSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openSearchTabFragment();
            }
        });
        mbinding.imgMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMessageFragment();
            }
        });

        mbinding.llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });

        mbinding.llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        mbinding.llScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermissions()){
                    openScanner();
                }
            }
        });

        View viewMenu = ((LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_layout_share, null);
        selectPicPopupWindow = new SelectSharePopupWindow(viewMenu, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(bmpUri != null){
                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        switch (v.getId()){
                            case R.id.btProfile:
                                selectPicPopupWindow.dismiss();
                                if(getProfileShareLink() != null){
                                    share.setType("text/plain");
                                    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.add_on_me) +" "+ getProfileShareLink());
                                    startActivity(Intent.createChooser(share, getResources().getString(R.string.share)));
                                }
                                break;
                            case R.id.btQr:
                                selectPicPopupWindow.dismiss();
                                share.setType("image/*");
                                share.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                startActivity(Intent.createChooser(share, getResources().getString(R.string.share)));
                                break;
                            case R.id.btCancel:
                                selectPicPopupWindow.dismiss();
                                break;
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        StrictMode.VmPolicy.Builder builder =  new  StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mbinding.txtUsername.setText("@"+SavePref.getInstance(mActivity).getUserdetail().getUsername());
        generateQRCode();
    }

    private void share() {
        selectPicPopupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private String getProfileShareLink() {
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority("khaleejiapp.com")
                .path("/app.html")
                .appendQueryParameter("user", String.valueOf(SavePref.getInstance(mActivity).getUserdetail().getUsername()));

        final Uri appLink = builder.build();
//        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(deepLink)
//                .setDomainUriPrefix("https://khaleeji.page.link")
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
//                .buildDynamicLink();
        try{
            URL dynamicLinkUrl = new URL(URLDecoder.decode(appLink.toString(), "UTF-8"));
            return dynamicLinkUrl.toString();
        }catch (UnsupportedEncodingException | MalformedURLException ex){
            ex.printStackTrace();
            return null;
        }
    }

    private void generateQRCode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", SavePref.getInstance(mActivity).getUserdetail().getId()+"");
            jsonObject.put("username", SavePref.getInstance(mActivity).getUserdetail().getUsername());
            if(gulfProfiledata != null){
                jsonObject.put("question", gulfProfiledata.getQuestion() == null ? "" : gulfProfiledata.getQuestion());
            }else{
                jsonObject.put("question","");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(jsonObject.toString(), BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);

            Bitmap logoBitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.app_icon);
//            int nh = (int) (logoBitmap.getHeight() * (100 / logoBitmap.getWidth()));
//            Bitmap scaledLogoBitmap = Bitmap.createScaledBitmap(logoBitmap, 100, nh, true);

            bitmap = overlay(bitmap, logoBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(bitmap != null){
                    Glide.with(mActivity).load(saveToInternalStorage())
                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                            .into(mbinding.imgQrCode);
                }
            }
        }, 200);
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Canvas canvas = new Canvas(bmp1);
        float left = (float) (bmp1.getWidth() - bmp2.getWidth())/2;
        float top = (float) (bmp1.getHeight() - bmp2.getHeight())/2;
        canvas.drawBitmap(bmp2, left, top, null);
        return bmp1;
    }

    public String saveToInternalStorage(){
        String filename = mActivity.getDir("filesdir", Context.MODE_PRIVATE) + "/IMG_QR_PROFILE.png";
        if(bitmap != null){
            File file = new File(filename);

            if (file.exists())
            {
                file.delete();
            }

            try (FileOutputStream out = new FileOutputStream(filename)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (Exception e) {
                e.printStackTrace();
            }
            File f =  new File(filename);
            bmpUri = Uri.fromFile(f);
            return filename;
        }
        return null;
    }

    public void saveImage() {
        if(bitmap != null && isSDCARDMounted()){
            File storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM
                    ), "Camera"
            );
            if(!storageDir.exists())
                storageDir.mkdir();

            String filename = storageDir.getPath() + File.separator + "IMG_QR_PROFILE.jpg";
            try (FileOutputStream out = new FileOutputStream(filename)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (Exception e) {
                e.printStackTrace();
            }
            File f =  new File(filename);
            new SingleMediaScanner(mActivity, f);
            Toast.makeText(mActivity, getString(R.string.txtSave), Toast.LENGTH_SHORT).show();
            bmpUri = Uri.fromFile(f);
        }
    }

    private boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    private void openScanner(){
        startActivity(new Intent(mActivity, SimpleScannerActivity.class));
    }

    private  boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.CAMERA);
        int permissionReadStorage = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            this.requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openScanner();
                }
            }
        }
    }
}
