package Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;


public class Utils {

    private static final String TAG = Utils.class.getName();

    public static int convertPixelsToDP(float myPixels) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, myPixels, displaymetrics);

        return dp;
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    public static File createFileFromBitmap(Bitmap bitmap, Context context) {

        Long tsLong = System.currentTimeMillis() / 1000;
        String name = tsLong.toString();

        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(TAG, "Error writing bitmap", e);
        }

        return imageFile;
    }

    public static int sizeOfBitmap(Bitmap data) {
        return data.getByteCount();
    }

    public static long getFileSizeInKb(File file) {
        if (file != null) {
            long fileSizeInBytes = file.length();

            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            long fileSizeInKB = fileSizeInBytes / 1024;
            return fileSizeInKB;
        }

        return 0;
    }

    public static long convertSecondsToMilliSeconds(int seconds) {
        return seconds * 1000;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean stringIsNotEmpty(String string) {
        if (string != null && !string.equals("null")) {
            if (!string.trim().equals("")) {
                return true;
            }
        }
        return false;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

    }

    public static boolean isURLValid(String url) {
        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }

    public static Bitmap compressImage(String filePath, Context context, boolean isFrontCamera) {
        //  String filePath = getRealPathFromURI(imageUri,context);

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();

//            if (orientation == 6) {
//                matrix.postRotate(90);
//                Log.d("EXIF", "Exif: " + orientation);
//            } else if (orientation == 3) {
//                matrix.postRotate(180);
//                Log.d("EXIF", "Exif: " + orientation);
//            } else if (orientation == 8) {
//                matrix.postRotate(270);
//                Log.d("EXIF", "Exif: " + orientation);
//            }

            matrix.postRotate(degreeRotation(orientation, isFrontCamera));

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);

            if (needHorizontalFlip(orientation, isFrontCamera)) {
                scaledBitmap = horizontalFlip(scaledBitmap);
                //matrix.preScale(-1.0f, 1.0f);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;

        File compressedFile = ImagePicker.getJPEGOutputMediaFile();
        //String filename = getFilename();
        String filename = compressedFile.getAbsolutePath();

        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "Utils.getFileSizeInKb(compressedFile) : " + Utils.getFileSizeInKb(compressedFile));

        return scaledBitmap;
        //return filename;
    }

    private static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private static String getRealPathFromURI(String contentURI, Context context) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private static boolean needHorizontalFlip(int orientation, boolean isFrontCamera) {
        boolean horizontalFlip = false;

        switch (orientation) {

            case ExifInterface.ORIENTATION_NORMAL:

                if (isFrontCamera) {
                    horizontalFlip = true;
                }

                break;

            case ExifInterface.ORIENTATION_UNDEFINED:

                if (isFrontCamera) {
                    horizontalFlip = true;
                }
                break;
        }

        return horizontalFlip;

    }

    private static int degreeRotation(int orientation, boolean isFrontCamera) {
        int degree = 0;

        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:

                degree = 90;

                break;

            case ExifInterface.ORIENTATION_ROTATE_180:

                degree = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;

            case ExifInterface.ORIENTATION_NORMAL:

                if (isFrontCamera) {

                    degree = 270;

                } else {

                    degree = 90;
                }

                break;

            case ExifInterface.ORIENTATION_UNDEFINED:

                if (isFrontCamera) {
                    degree = 270;
                } else {
                    degree = 90;
                }

                break;

        }

        return degree;
    }

    private static Bitmap horizontalFlip(Bitmap bInput) {
        Bitmap bOutput;
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        bOutput = Bitmap.createBitmap(bInput, 0, 0, bInput.getWidth(), bInput.getHeight(), matrix, true);

        return bOutput;
    }

    public static void openURL(String url, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }

    public static StringBuilder convertIntegerListInCommaSeparated(List<Integer> slcrdFrnds) {
        //The string builder used to construct the string
        StringBuilder commaSepValueBuilder = new StringBuilder();

        //Looping through the list
        for (int i = 0; i < slcrdFrnds.size(); i++) {
            //append the value into the builder
            commaSepValueBuilder.append(slcrdFrnds.get(i));

            //if the value is not the last element of the list
            //then append the comma(,) as well
            if (i != slcrdFrnds.size() - 1) {
                commaSepValueBuilder.append(",");
            }
        }

        Log.e(TAG, "commaSepValueBuilder : " + commaSepValueBuilder);
        return commaSepValueBuilder;
    }

    private Bitmap exifValue(Bitmap bitmap, String filePath, boolean isFrontCamera)  //InputStream input,
    {
        Log.e(TAG, "exifValue filePath : " + filePath);

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;

        Log.e(TAG, "orientation : " + orientation);

        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotate(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotate(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotate(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:

                if (isFrontCamera) {
                    rotatedBitmap = rotate(bitmap, 270);

                    rotatedBitmap = horizontalFlip(rotatedBitmap);
                } else {
                    rotatedBitmap = rotate(bitmap, 90);
                }

                break;

            case ExifInterface.ORIENTATION_UNDEFINED:

                if (isFrontCamera) {
                    rotatedBitmap = rotate(bitmap, 270);

                    rotatedBitmap = horizontalFlip(rotatedBitmap);
                } else {
                    rotatedBitmap = rotate(bitmap, 90);
                }

                break;


            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

}
