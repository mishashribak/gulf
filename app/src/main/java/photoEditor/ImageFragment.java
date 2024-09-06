package photoEditor;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.khaleeji.Fragments.BaseFragment;
import com.app.khaleeji.R;

import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.List;

import Model.ZipCategoryData;

public class ImageFragment extends BaseFragment implements ImageAdapter.OnImageClickListener {

    public RecyclerView imageRecyclerView;
    private List<Integer> stickerDrawableList;
    private TextView txtTitle;
    private String categoryName = "";
    private int index;
    private boolean isVideoEditorActivity = true;
    private ImageAdapter adapter;
    private OnStrickerClickListener onStrickerClickListener;

    @SuppressLint("ValidFragment")
    public ImageFragment(OnStrickerClickListener onStrickerClickListener, String categoryName) {
        this.onStrickerClickListener = onStrickerClickListener;
        this.categoryName = categoryName;
    }

    public ImageFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_photo_edit_image, container, false);
        imageRecyclerView = rootView.findViewById(R.id.fragment_main_photo_edit_image_rv);
//        imageRecyclerView.setHasFixedSize(true);
//        imageRecyclerView.setItemViewCacheSize(20);
//        imageRecyclerView.setDrawingCacheEnabled(true);
//        imageRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        txtTitle = rootView.findViewById(R.id.txt_title);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        adapter = new ImageAdapter(mActivity, stickerDrawableList, index, isVideoEditorActivity);
        adapter.setOnImageClickListener(this);
        imageRecyclerView.setAdapter(adapter);
        txtTitle.setText(categoryName);
        return rootView;
    }

    public Bitmap decodeSampledBitmapFromResource(String resId,
                                                  int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onImageClickListener(int image, String type) {
//        onStrickerClickListener.onStickerClick(image, type);
//        onStrickerClickListener.onStickerClick(BitmapFactory.decodeResource(getResources(), image), type);
        onStrickerClickListener.onStickerClick(image, type);

    }

    public void setData(ZipCategoryData zipCategoryData, int position) {
//        stickerDrawableList.clear();
        index = position;
//        isVideoEditorActivity = isFromVideoEditorActivity;
//        categoryName = zipCategoryData.getCategory();
//        for (int i = 0; i < zipCategoryData.stickerPathList.size(); i++) {
//            if(!zipCategoryData.stickerPathList.get(i).contains("__MACOSX"))
//                stickerDrawableList.add(zipCategoryData.stickerPathList.get(i));
//        }
        stickerDrawableList = zipCategoryData.getStickerDrawableList();
    }

    public interface OnStrickerClickListener {
        void onStickerClick(int resId, String type);
    }
}
