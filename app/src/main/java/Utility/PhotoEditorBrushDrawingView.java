package Utility;

/**
 * Created by Dcube on 03-09-2018.
 */

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

import photoEditor.OnPhotoEditorSDKListener;
import photoEditor.ViewType;


/**
 * Created by Ahmed Adel on 5/8/17.
 */

public class PhotoEditorBrushDrawingView extends View {

    private final String TAG = PhotoEditorBrushDrawingView.class.getName();

    private float brushSize = 10;
    private float brushEraserSize = 100;

    private Path drawPath;
    private Paint drawPaint;
    private Paint canvasPaint;

    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private boolean brushDrawMode;

  //  private ArrayList<Path> paths = new ArrayList<Path>();

    List<Pair<Path, Integer>> path_color_list = new ArrayList<Pair<Path,Integer>>();

    private boolean isColorSlctdOnce = true;
    private OnPhotoEditorSDKListener onPhotoEditorSDKListener;

    public PhotoEditorBrushDrawingView(Context context) {
        this(context, null);
    }

    public PhotoEditorBrushDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupBrushDrawing();
    }

    public PhotoEditorBrushDrawingView(Context context, AttributeSet attrs, int defStyle) {
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
        Log.e(TAG,"setupBrushDrawing path_color_list.add");
        path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
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

        //Cmntd on 21
//        Log.e(TAG,"refreshBrushDrawing path_color_list.add");
//        path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));

    }

    //zcv

    void brushEraser() {
        drawPaint.setStrokeWidth(brushEraserSize);
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
       // invalidate();
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
        //refreshBrushDrawing();
        //Log.e(TAG,"setBrushColor path_color_list.add");
        //path_color_list.add( new Pair<Path, Integer>(drawPath,color));
        //drawPath = new Path();

        if (isColorSlctdOnce)
        {
            Log.e(TAG,"setBrushColor path_color_list.ad");
            path_color_list.add(new Pair<Path, Integer>(drawPath,drawPaint.getColor()));

            if (path_color_list.size() > 2)
            {
                path_color_list.remove(path_color_list.size() - 2);
            }

        }
        else
        {
            if (path_color_list.size() > 1)
            {
                int lastIndex = path_color_list.size()-1;
                Log.e(TAG,"setBrushColor lastIndex : "+lastIndex);
                path_color_list.set( lastIndex, new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
            }
            else
            {
                Log.e(TAG,"setBrushColor path_color_list.ad");
                path_color_list.add(new Pair<Path, Integer>(drawPath,drawPaint.getColor()));

//                if (path_color_list.size() > 2)
//                {
//                    path_color_list.remove(path_color_list.size() - 2);
//                }

            }
        }

        Log.e(TAG,"setBrushColor path_color_list.size() : "+path_color_list.size());
        isColorSlctdOnce = false;
    }

    void setBrushEraserSize(float brushEraserSize) {
        this.brushEraserSize = brushEraserSize;
    }

    void setBrushEraserColor(@ColorInt int color){
        drawPaint.setColor(color);
        //refreshBrushDrawing();
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
            invalidate();
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

        //canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        //Commented by rohit
        //canvas.drawPath(drawPath, drawPaint);

       // Log.v(TAG,"onDraw");

//        for (Path p : paths){
//            canvas.drawPath(p, drawPaint);
//        }
//        canvas.drawPath(drawPath, drawPaint);


        for (Pair<Path,Integer> path_clr : path_color_list )
        {
            drawPaint.setColor(path_clr.second);
            canvas.drawPath( path_clr.first, drawPaint);
        }

    }


    //Added

    public void undo()
    {
        Log.v(TAG,"undo");
        Log.v(TAG,"path_color_list.size() : "+path_color_list.size());

        if (path_color_list.size()>0)
        {
            path_color_list.remove(path_color_list.size()-1);
            invalidate();
        }
//        else
//        {
//            Log.v(TAG,"undo path_color_list.add");
//            path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
//        }

    }


    //*************

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (brushDrawMode) {
            float touchX = event.getX();
            float touchY = event.getY();

           // Log.v(TAG,"event.getAction() : "+event.getAction());

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    drawPath.moveTo(touchX, touchY);
                    if (onPhotoEditorSDKListener != null)
                        onPhotoEditorSDKListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);

                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);

                    break;
                case MotionEvent.ACTION_UP:

                    isColorSlctdOnce = true;

                    drawCanvas.drawPath(drawPath, drawPaint);

                    Log.e(TAG,"ACTION_UP path_color_list.add");
                    path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));

                    Log.e(TAG,"ACTION_UP path_color_list.size() : "+path_color_list.size());

//                    //Added
//                    //paths.add(drawPath);
//                    if (path_color_list.size() > 0)
//                    {
//                        Log.e(TAG,"ACTION_UP path_color_list.size() > 0 path_color_list.add");
//
//                        int lastIndex = path_color_list.size()-1;
//                        //path_color_list.set( lastIndex, new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
//                        path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
//                    }
//                    else
//                    {
//                        Log.e(TAG,"ACTION_UP path_color_list.add");
//                        path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
//                    }

                    drawPath = new Path();
                    //*********

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
}
