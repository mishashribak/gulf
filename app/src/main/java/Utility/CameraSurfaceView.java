package Utility;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by Dcube on 23-10-2018.
 */

public class CameraSurfaceView extends SurfaceView {

    private List<Camera.Size> mSupportedPreviewSizes;
    public static int width,height;

    public CameraSurfaceView(Context context,Camera mCamera) {

        super(context);

        // TODO Auto-generated constructor stub
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
//        setMeasuredDimension(
//                MeasureSpec.getSize(widthMeasureSpec),
//                MeasureSpec.getSize(heightMeasureSpec));

        width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

//        if (mSupportedPreviewSizes != null)
//        {
//            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
//        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

}
