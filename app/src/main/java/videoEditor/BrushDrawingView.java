package videoEditor;

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

import java.util.ArrayList;
import java.util.List;

public class BrushDrawingView extends View {

    private final String TAG = BrushDrawingView.class.getName();

    private float brushSize = 10;
    private float brushEraserSize = 100;

    private Path drawPath;
    private Paint drawPaint;
    private Paint canvasPaint;

    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private boolean brushDrawMode;

    private OnPhotoEditorSDKListener onPhotoEditorSDKListener;

    private boolean isColorSlctdOnce = true;

    List<Pair<Path, Integer>> path_color_list = new ArrayList<Pair<Path,Integer>>();

    //  private ArrayList<PathPoints> paths = new ArrayList<PathPoints>();

    public BrushDrawingView(Context context) {
        this(context, null);
    }

    public BrushDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupBrushDrawing();

    }

    public BrushDrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

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

    }

    private void refreshBrushDrawing() {
        //  Log.e(TAG,"refreshBrushDrawing");
        brushDrawMode = true;
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));

    }

    //zcv

    void brushEraser() {
        drawPaint.setStrokeWidth(brushEraserSize);
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

    }

    void setBrushDrawingMode(boolean brushDrawMode) {
        this.brushDrawMode = brushDrawMode;
        if (brushDrawMode) {
            this.setVisibility(View.VISIBLE);
            try {
                refreshBrushDrawing();
            }
            catch (Exception e)
            {
                Log.e(TAG,"setBrushDrawingMode e.getMessage() : "+e.getMessage());
            }
        }
    }

    void setBrushSize(float size) {
        brushSize = size;
        refreshBrushDrawing();
    }


    void setBrushColor(@ColorInt int color) {

        drawPaint.setColor(color);

        Log.e(TAG,"setBrushColor path_color_list.ad"+color);

        if (isColorSlctdOnce)
        {
            Log.e(TAG,"setBrushColor path_color_list.ad");
            // path_color_list.add(new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
        }

        refreshBrushDrawing();

        Log.e(TAG,"setBrushColor path_color_list.size() : "+path_color_list.size());
        isColorSlctdOnce = false;
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

        if (drawCanvas!= null)
        {
            drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            //  invalidate();
        }

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

        Log.e(TAG,"onDraw");
        // canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
        for (Pair<Path,Integer> path_clr : path_color_list )
        {
            drawPaint.setColor(path_clr.second);
            canvas.drawPath( path_clr.first, drawPaint);
        }

    }

    public void onClickUndo() {

        if (path_color_list.size() > 0) {
            path_color_list.remove(path_color_list.size() - 1);
            invalidate();
        }
    }



    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (brushDrawMode) {
            float touchX = event.getX();
            float touchY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    drawPath.moveTo(touchX, touchY);
                    path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));

                    if (onPhotoEditorSDKListener != null)
                        onPhotoEditorSDKListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);

                    invalidate();

                    break;

                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);

                    invalidate();

                    break;

                case MotionEvent.ACTION_UP:

                    isColorSlctdOnce = true;
                    //  drawPath.lineTo(mX, mY);
                    drawCanvas.drawPath(drawPath, drawPaint);

                    Log.e(TAG,"ACTION_UP path_color_list.size() : "+path_color_list.size());

                    drawPath = new Path();

                    //*********

                    drawPath.reset();

                    if (onPhotoEditorSDKListener != null)
                        onPhotoEditorSDKListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);

                    invalidate();

                    break;

                default:
                    return false;
            }
            //  invalidate();
            return true;
        } else {
            return false;
        }
    }



}
