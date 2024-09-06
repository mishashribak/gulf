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

import videoEditor.OnPhotoEditorSDKListener;
import videoEditor.ViewType;


/**
 * Created by Ahmed Adel on 5/8/17.
 */

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

  //  private ArrayList<Path> paths = new ArrayList<Path>();

    List<Pair<Path, Integer>> path_color_list = new ArrayList<Pair<Path,Integer>>();


    private OnPhotoEditorSDKListener onPhotoEditorSDKListener;

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
        Log.e(TAG,"setupBrushDrawing");
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
        path_color_list.add( new Pair<Path, Integer>(drawPath,Color.BLACK));
    }

    private void refreshBrushDrawing() {
        Log.e(TAG,"refreshBrushDrawing");
        brushDrawMode = true;
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
        path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
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
        Log.e(TAG,"setBrushColor");
        drawPaint.setColor(color);
        //refreshBrushDrawing();
        path_color_list.add( new Pair<Path, Integer>(drawPath,color));
        //drawPath = new Path();
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

        Log.v(TAG,"onDraw");

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
//            path_color_list.add( new Pair<Path, Integer>(drawPath,Color.BLACK));
//        }

    }


    //*************

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (brushDrawMode) {
            float touchX = event.getX();
            float touchY = event.getY();


            Log.v(TAG,"event.getAction() : "+event.getAction());

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
                    drawCanvas.drawPath(drawPath, drawPaint);

                    //Added
                    //paths.add(drawPath);
                    if (path_color_list.size() > 0)
                    {
                        int lastIndex = path_color_list.size()-1;
                       // path_color_list.set( lastIndex,new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
                        path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
                    }
                    else
                    {
                        path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
                    }

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
