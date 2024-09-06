package Utility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImagePicker {

    public static final int REQUEST_CAMERA = 1;
    public static final int SELECT_FILE = 2;
    public static final int PICK_VIDEO = 3;
    private static final String TAKE_PHOTO = "Take Photo";
    private static final String CHOOSE_FROM_LIBRARY = "Choose Picture from Library";
    private static final String RECORD_VIDEO = "Pick Video";
    private static final String CANCEL = "Cancel";
    public static String userChoosenTask;

    public static void selectImage(final Activity mcontext) {

        final CharSequence[] items = {TAKE_PHOTO, CHOOSE_FROM_LIBRARY, RECORD_VIDEO, CANCEL};   //RECORD_VIDEO,

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mcontext);

        builder.setTitle("Choose File");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Permission.checkPermissionCamera(mcontext);

                if (items[item].equals(TAKE_PHOTO)) {
                    userChoosenTask = TAKE_PHOTO;
                    if (result)
                        cameraIntent(mcontext);
                } else if (items[item].equals(CHOOSE_FROM_LIBRARY)) {
                    userChoosenTask = CHOOSE_FROM_LIBRARY;
                    if (result)
                        galleryIntent(mcontext);
                } else if (items[item].equals(RECORD_VIDEO)) {
                    userChoosenTask = RECORD_VIDEO;
                    if (result) {
                        //videoPickerIntent(mcontext);

//                        new VideoPicker.Builder(mcontext)
//                                .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
//                                .directory(VideoPicker.Directory.DEFAULT)
//                                .extension(VideoPicker.Extension.MP4)
//                                .enableDebuggingMode(true)
//                                .build();
                    }

                } else if (items[item].equals(CANCEL)) {
                    dialog.dismiss();
                }

            }
        });

        builder.show();
    }

    public static void pickImage(final Activity mcontext) {

        final CharSequence[] items = {TAKE_PHOTO, CHOOSE_FROM_LIBRARY, CANCEL};   //RECORD_VIDEO,

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mcontext);

        builder.setTitle("Choose File");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Permission.checkPermissionCamera(mcontext);

                if (items[item].equals(TAKE_PHOTO)) {
                    userChoosenTask = TAKE_PHOTO;
                    if (result)
                        cameraIntent(mcontext);
                } else if (items[item].equals(CHOOSE_FROM_LIBRARY)) {
                    userChoosenTask = CHOOSE_FROM_LIBRARY;
                    if (result)
                        galleryIntent(mcontext);
                }
//                else if (items[item].equals(RECORD_VIDEO))
//                {
//                    userChoosenTask=RECORD_VIDEO;
//                    if(result)
//                    {
//                        //videoPickerIntent(mcontext);
//
//                        new VideoPicker.Builder(mcontext)
//                                .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
//                                .directory(VideoPicker.Directory.DEFAULT)
//                                .extension(VideoPicker.Extension.MP4)
//                                .enableDebuggingMode(true)
//                                .build();
//                    }
//
//                }
                else if (items[item].equals(CANCEL)) {
                    dialog.dismiss();
                }

            }
        });

        builder.show();
    }

    public static void cameraIntent(Activity mcontext) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mcontext.startActivityForResult(intent, REQUEST_CAMERA);
    }

    public static void galleryIntent(Activity mcontext) {
       /* Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        mcontext.startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
*/
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mcontext.startActivityForResult(i, SELECT_FILE);

    }

    public static void videoPickerIntent(Activity mcontext) {
//        Intent intent = new Intent();
//        intent.setType("video/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        mcontext.startActivityForResult(Intent.createChooser(intent,"Select Video"),PICK_VIDEO);


        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        mcontext.startActivityForResult(intent, PICK_VIDEO);
    }


    public static File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "khaleeji");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" +  System.currentTimeMillis() + ".jpg");

        return mediaFile;
    }

    public static File getJPEGOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "khaleeji");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" +  System.currentTimeMillis() + ".jpeg");
        try{
            if (!mediaFile.exists()) {
                if (! mediaFile.createNewFile()) {
                    Log.d("MyCameraApp", "failed to create file");
                    return null;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return mediaFile;
    }

    public static File getOutputVideoFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "khaleeji");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("khaleeji", "failed to create directory");
                return null;
            }
        }
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" +  System.currentTimeMillis() + ".mp4");
        return mediaFile;
    }

    public static File getThumbnailFile() {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "khaleeji");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("khaleeji", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "Thumb" +  System.currentTimeMillis() + ".png");

        return mediaFile;
    }

    public static File getCompressedVideoFile() {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "khaleeji");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("khaleeji", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "Compressed_VID_" +  System.currentTimeMillis() + ".mp4");

        return mediaFile;
    }

    public static String getRealPathFromURI(Uri contentURI, Context mcontext) {

        String result;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = mcontext.getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(filePathColumn[0]);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


}
