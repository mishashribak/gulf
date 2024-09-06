package photoEditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.ColorInt;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.khaleeji.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import CustomView.SingleMediaScanner;
import Utility.Utils;


public class PhotoEditorSDK implements MultiTouchListener.OnMultiTouchListener {

    private final String TAG = "PhotoEditorSDK";

    private Context context;
    private RelativeLayout parentView;
    private ImageView imageView;
    private View deleteView;
    private BrushDrawingView brushDrawingView;
    private List<View> addedViews;
    private List<Integer> locationLabelPos;
    private OnPhotoEditorSDKListener onPhotoEditorSDKListener;
    private View addTextRootView;

    private PhotoEditorSDK(PhotoEditorSDKBuilder photoEditorSDKBuilder) {
        this.context = photoEditorSDKBuilder.context;
        this.parentView = photoEditorSDKBuilder.parentView;
        this.imageView = photoEditorSDKBuilder.imageView;
        this.deleteView = photoEditorSDKBuilder.deleteView;
        this.brushDrawingView = photoEditorSDKBuilder.brushDrawingView;
        addedViews = new ArrayList<>();
        locationLabelPos = new ArrayList<>();
    }

    public void addImage(Bitmap desiredImage, boolean isLocationLabel) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View imageRootView = inflater.inflate(com.ahmedadeltito.photoeditorsdk.R.layout.photo_editor_sdk_image_item_list, null);
        ImageView imageView = (ImageView) imageRootView.findViewById(com.ahmedadeltito.photoeditorsdk.R.id.photo_editor_sdk_image_iv);
        File f = Utils.createFileFromBitmap(desiredImage, context);
        Glide.with(context).load(f.getPath()).into(imageView);

        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        MultiTouchListener multiTouchListener = new MultiTouchListener(deleteView,
                parentView, this.imageView, onPhotoEditorSDKListener);
        multiTouchListener.setOnMultiTouchListener(this);
        imageRootView.setOnTouchListener(multiTouchListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(imageRootView, params);
        addedViews.add(imageRootView);

        if (isLocationLabel) {
            locationLabelPos.add(addedViews.size() - 1);
            Log.e(TAG, "locationLabelPos.size() : " + locationLabelPos.size());
        }

        if (onPhotoEditorSDKListener != null)
            onPhotoEditorSDKListener.onAddViewListener(ViewType.IMAGE, addedViews.size());
    }

    public void addImage(int resId, boolean isLocationLabel) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View imageRootView = inflater.inflate(com.ahmedadeltito.photoeditorsdk.R.layout.photo_editor_sdk_image_item_list, null);
        ImageView imageView = (ImageView) imageRootView.findViewById(com.ahmedadeltito.photoeditorsdk.R.id.photo_editor_sdk_image_iv);
        Glide.with(context).load(resId).into(imageView);

        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        MultiTouchListener multiTouchListener = new MultiTouchListener(deleteView,
                parentView, this.imageView, onPhotoEditorSDKListener);
        multiTouchListener.setOnMultiTouchListener(this);
        imageRootView.setOnTouchListener(multiTouchListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(imageRootView, params);
        addedViews.add(imageRootView);

        if (isLocationLabel) {
            locationLabelPos.add(addedViews.size() - 1);
            Log.e(TAG, "locationLabelPos.size() : " + locationLabelPos.size());
        }

        if (onPhotoEditorSDKListener != null)
            onPhotoEditorSDKListener.onAddViewListener(ViewType.IMAGE, addedViews.size());
    }

    public void addText(String text, int colorCodeTextView) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addTextRootView = inflater.inflate(com.ahmedadeltito.photoeditorsdk.R.layout.photo_editor_sdk_text_item_list, null);
        TextView addTextView = (TextView) addTextRootView.findViewById(com.ahmedadeltito.photoeditorsdk.R.id.photo_editor_sdk_text_tv);
        addTextView.setGravity(Gravity.CENTER);
        addTextView.setText(text);
        if (colorCodeTextView != -1)
            addTextView.setTextColor(colorCodeTextView);
        MultiTouchListener multiTouchListener = new MultiTouchListener(deleteView,
                parentView, this.imageView, onPhotoEditorSDKListener);
        multiTouchListener.setOnMultiTouchListener(this);
        addTextRootView.setOnTouchListener(multiTouchListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);  //WRAP_CONTENT
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addTextRootView.setBackgroundColor(context.getResources().getColor(R.color.black));
        //   addTextRootView.setAlpha(0.4f);
        parentView.addView(addTextRootView, params);
        addedViews.add(addTextRootView);
        if (onPhotoEditorSDKListener != null)
            onPhotoEditorSDKListener.onAddViewListener(ViewType.TEXT, addedViews.size());
    }

    public void addText(String text, int colorCodeTextView, Typeface fontType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addTextRootView = inflater.inflate(com.ahmedadeltito.photoeditorsdk.R.layout.photo_editor_sdk_text_item_list, null);
        TextView addTextView = (TextView) addTextRootView.findViewById(com.ahmedadeltito.photoeditorsdk.R.id.photo_editor_sdk_text_tv);
        addTextView.setGravity(Gravity.CENTER);
        addTextView.setText(text);
        addTextView.setTypeface(fontType);
        if (colorCodeTextView != -1)
            addTextView.setTextColor(colorCodeTextView);
        MultiTouchListener multiTouchListener = new MultiTouchListener(deleteView,
                parentView, this.imageView, onPhotoEditorSDKListener);
        multiTouchListener.setOnMultiTouchListener(this);
        addTextRootView.setOnTouchListener(multiTouchListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);  //WRAP_CONTENT
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        // addTextRootView.setBackgroundColor(context.getResources().getColor(R.color.black));
        //   addTextRootView.setAlpha(0.4f);
        parentView.addView(addTextRootView, params);
        addedViews.add(addTextRootView);
        if (onPhotoEditorSDKListener != null)
            onPhotoEditorSDKListener.onAddViewListener(ViewType.TEXT, addedViews.size());
    }

    public void addEmoji(String emojiName, Typeface emojiFont) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emojiRootView = inflater.inflate(com.ahmedadeltito.photoeditorsdk.R.layout.photo_editor_sdk_text_item_list, null);
        TextView emojiTextView = (TextView) emojiRootView.findViewById(com.ahmedadeltito.photoeditorsdk.R.id.photo_editor_sdk_text_tv);
        emojiTextView.setTypeface(emojiFont);
        emojiTextView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        emojiTextView.setText(convertEmoji(emojiName));
        MultiTouchListener multiTouchListener = new MultiTouchListener(deleteView,
                parentView, this.imageView, onPhotoEditorSDKListener);
        multiTouchListener.setOnMultiTouchListener(this);
        emojiRootView.setOnTouchListener(multiTouchListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(emojiRootView, params);
        addedViews.add(emojiRootView);
        if (onPhotoEditorSDKListener != null)
            onPhotoEditorSDKListener.onAddViewListener(ViewType.EMOJI, addedViews.size());
    }

    public void setBrushDrawingMode(boolean brushDrawingMode) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushDrawingMode(brushDrawingMode);
    }

    public void setBrushEraserSize(float brushEraserSize) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushEraserSize(brushEraserSize);
    }

    public void setBrushEraserColor(@ColorInt int color) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushEraserColor(color);
    }

    public float getEraserSize() {
        if (brushDrawingView != null)
            return brushDrawingView.getEraserSize();
        return 0;
    }

    public float getBrushSize() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushSize();
        return 0;
    }

    public void setBrushSize(float size) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushSize(size);
    }

    public int getBrushColor() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushColor();
        return 0;
    }

    public void setBrushColor(@ColorInt int color) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushColor(color);
    }

    public void brushEraser() {
        if (brushDrawingView != null)
            brushDrawingView.brushEraser();
    }

    public void undoBrushDrawing() {

        if (brushDrawingView != null)
            brushDrawingView.onClickUndo();

    }

    public void viewUndo() {
        if (addedViews.size() > 0) {

            //Added by roohit
            removeLocationLabel(-1);

            parentView.removeView(addedViews.remove(addedViews.size() - 1));
            if (onPhotoEditorSDKListener != null)
                onPhotoEditorSDKListener.onRemoveViewListener(addedViews.size());
        }
    }

    private void viewUndo(View removedView) {
        if (addedViews.size() > 0) {
            if (addedViews.contains(removedView)) {
                parentView.removeView(removedView);

                //Added by roohit
                int pos = addedViews.indexOf(removedView);
                removeLocationLabel(pos);

                addedViews.remove(removedView);
                if (onPhotoEditorSDKListener != null)
                    onPhotoEditorSDKListener.onRemoveViewListener(addedViews.size());
            }
        }
    }

    public void clearBrushAllViews() {
        if (brushDrawingView != null)
            brushDrawingView.clearAll();
    }

    public void clearAllViews() {
        for (int i = 0; i < addedViews.size(); i++) {
            parentView.removeView(addedViews.get(i));
        }
        if (brushDrawingView != null)
            brushDrawingView.clearAll();
    }

    public String saveImage(String folderName, String imageName, Activity activity) {

        String selectedOutputPath = "";

        if (isSDCARDMounted()) {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "khaleeji");

            // Create a storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("PhotoEditorSDK", "Failed to create directory");
                }
            }
            // Create a media file name
            //selectedOutputPath = mediaStorageDir.getPath() + File.separator + imageName;
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            selectedOutputPath = mediaStorageDir.getPath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";
            Log.d("PhotoEditorSDK", "selected camera path " + selectedOutputPath);
            File file = new File(selectedOutputPath);

            try {
                FileOutputStream out = new FileOutputStream(file);
                if (parentView != null) {
                    parentView.setDrawingCacheEnabled(true);
                    parentView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 80, out);
                }

                activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return selectedOutputPath;
    }

    public String saveImage(String folderName, String imageName, Activity activity, boolean saveImage) {

        String selectedOutputPath = "";

        if (isSDCARDMounted()) {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "khaleeji");

            // Create a storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("PhotoEditorSDK", "Failed to create directory");
                }
            }
            // Create a media file name
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            selectedOutputPath = mediaStorageDir.getPath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";
            Log.d("PhotoEditorSDK", "selected camera path " + selectedOutputPath);
            File file = new File(selectedOutputPath);

            try {
                FileOutputStream out = new FileOutputStream(file);
                if (parentView != null) {
                    parentView.setDrawingCacheEnabled(true);
                    parentView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 91, out);
                }
                out.flush();
                out.close();

                if(saveImage){
                    copyFileToDCIM(file);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return selectedOutputPath;
    }

    public String copyFileToDCIM(File inputFile) {

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
            String outputPath = storageDir.getPath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";


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

            new SingleMediaScanner(context, new File(outputPath));

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, context.getString(R.string.save), Toast.LENGTH_SHORT).show();
                }
            });
            return outputPath;


        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        return "";
    }

    private boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    private String convertEmoji(String emoji) {
        String returnedEmoji = "";
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public void setOnPhotoEditorSDKListener(OnPhotoEditorSDKListener onPhotoEditorSDKListener) {
        this.onPhotoEditorSDKListener = onPhotoEditorSDKListener;
        brushDrawingView.setOnPhotoEditorSDKListener(onPhotoEditorSDKListener);
    }

    @Override
    public void onEditTextClickListener(String text, int colorCode) {
        if (addTextRootView != null) {
            parentView.removeView(addTextRootView);
            addedViews.remove(addTextRootView);
        }
    }

    @Override
    public void onRemoveViewListener(View removedView) {
        viewUndo(removedView);
    }

    private void removeLocationLabel(int pos) {
        if (pos == -1) {
            if (locationLabelPos.size() >= 0) {
                int locationLabel = addedViews.size() - 1;

                if (locationLabelPos.contains(locationLabel)) {
                    int index = locationLabelPos.indexOf(locationLabel);

                    locationLabelPos.remove(index);
                }
            }
        } else {
            if (locationLabelPos.size() >= 0) {
                //int locationLabel = addedViews.size() - 1;

                if (locationLabelPos.contains(pos)) {
                    int index = locationLabelPos.indexOf(pos);

                    locationLabelPos.remove(index);
                }
            }

            // locationLabelPos.remove(pos);
        }
    }

    public static class PhotoEditorSDKBuilder {

        private Context context;
        private RelativeLayout parentView;
        private ImageView imageView;
        private View deleteView;
        private BrushDrawingView brushDrawingView;

        public PhotoEditorSDKBuilder(Context context) {
            this.context = context;
        }

        public PhotoEditorSDKBuilder parentView(RelativeLayout parentView) {
            this.parentView = parentView;
            return this;
        }

        public PhotoEditorSDKBuilder childView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public PhotoEditorSDKBuilder deleteView(View deleteView) {
            this.deleteView = deleteView;
            return this;
        }

        public PhotoEditorSDKBuilder brushDrawingView(BrushDrawingView brushDrawingView) {
            this.brushDrawingView = brushDrawingView;
            return this;
        }

        public PhotoEditorSDK buildPhotoEditorSDK() {
            return new PhotoEditorSDK(this);
        }
    }
}
