package photoEditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.app.khaleeji.Activity.GulflinkApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utility.ImagePicker;

public class OriginalBrushDrawingView extends View {

    private final String TAG = OriginalBrushDrawingView.class.getName();

    private float brushSize = 10;
    private float brushEraserSize = 100;

    private Path drawPath;
    private Paint drawPaint;
    private Paint canvasPaint;

    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private Bitmap bitmapbackup;
    private boolean brushDrawMode;

    static List<Pair<Path, Paint>> path_color_list = new ArrayList<Pair<Path,Paint>>();

    GulflinkApplication gulflinkApplication;

    private OnPhotoEditorSDKListener onPhotoEditorSDKListener;

    Context context;

    public OriginalBrushDrawingView(Context context) {
        this(context, null);
        this.context = context;
        gulflinkApplication = (GulflinkApplication)context.getApplicationContext();
    }

    public OriginalBrushDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        gulflinkApplication = (GulflinkApplication)context.getApplicationContext();
        setupBrushDrawing();
    }

    public OriginalBrushDrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        gulflinkApplication = (GulflinkApplication)context.getApplicationContext();
        setupBrushDrawing();
    }

    void setupBrushDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);
        drawPaint.setColor(Color.BLACK);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        this.setVisibility(View.GONE);


        Log.e(TAG,"******************************");

    }

    private void refreshBrushDrawing() {
        brushDrawMode = true;
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
    }

    void brushEraser() {
        drawPaint.setStrokeWidth(brushEraserSize);
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    void setBrushDrawingMode(boolean brushDrawMode) {
        this.brushDrawMode = brushDrawMode;
        if (brushDrawMode) {
            this.setVisibility(View.VISIBLE);
            refreshBrushDrawing();

        }
    }

    void setBrushSize(float size) {
        brushSize = size;
        refreshBrushDrawing();
    }

    void setBrushColor(@ColorInt int color) {
        drawPaint.setColor(color);
        refreshBrushDrawing();
    }

    void setBrushEraserSize(float brushEraserSize) {
        this.brushEraserSize = brushEraserSize;
    }

    void setBrushEraserColor(@ColorInt int color){
        drawPaint.setColor(color);
        refreshBrushDrawing();
    }


    float getEraserSize() {
        return brushEraserSize;
    }

    float getBrushSize() {
        return brushSize;
    }

    int getBrushColor() {
        return drawPaint.getColor();
    }

    void clearAll() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setOnPhotoEditorSDKListener(OnPhotoEditorSDKListener onPhotoEditorSDKListener) {
        this.onPhotoEditorSDKListener = onPhotoEditorSDKListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);

        if(bitmapbackup != null) {
            canvas.drawBitmap(bitmapbackup, 0, 0, canvasPaint);
            canvas.drawPath(drawPath, drawPaint);
        }
    }

    public void undo()
    {

        //clearAll();
        drawCanvas = new Canvas(bitmapbackup);
        drawCanvas.drawPath(drawPath, drawPaint);
        invalidate();

    }
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (brushDrawMode) {
            float touchX = event.getX();
            float touchY = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    storeImage(canvasBitmap);

                    drawPath.moveTo(touchX, touchY);
                    if (onPhotoEditorSDKListener != null)
                        onPhotoEditorSDKListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    bitmapbackup = canvasBitmap;
                    drawCanvas.drawPath(drawPath, drawPaint);
                    path_color_list.add(new Pair<Path, Paint>(drawPath,drawPaint));
                    storeImage(canvasBitmap);
                    drawPath.reset();
                    if (onPhotoEditorSDKListener != null)
                        onPhotoEditorSDKListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);
                    break;
                default:
                    return false;
            }
            invalidate();
            return true;
        } else {
            return false;
        }
    }


    private void storeImage(Bitmap image) {

        File pictureFile = ImagePicker.getJPEGOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

    }


}
