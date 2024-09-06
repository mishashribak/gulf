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

//        canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
//
//        for (PathPoints p : paths) {
//            drawPaint.setColor(p.getColor());
//            Log.v(TAG, "Color code : " + p.getColor());
//            canvas.drawPath(p.getPath(), drawPaint);
//        }

    }

    protected void onDrawAgain(Canvas canvas) {

        for (Pair<Path,Integer> path_clr : path_color_list )
        {
            drawPaint.setColor(path_clr.second);
            canvas.drawPath( path_clr.first, drawPaint);
        }

    }

    public void undo()
    {
        Log.v(TAG,"undo");
       // Log.v(TAG,"path_color_list.size() : "+path_color_list.size());

        if (path_color_list.size()>2)
        {

            Log.v(TAG, "path_color_list.size() : " + path_color_list.get(path_color_list.size()-1).second);
             Log.v(TAG, "path_color_list.size() : " + path_color_list.get(path_color_list.size()-3).second);

            if(path_color_list.get(path_color_list.size()-1).second.equals(path_color_list.get(path_color_list.size()-3).second))
            {
                Log.v(TAG, "color match : ");

                path_color_list.remove(path_color_list.size() - 1);
                Log.v(TAG, "path_color_list.size() : " + path_color_list.size());
            }
            else {

                Log.v(TAG, "color not match : ");

                path_color_list.remove(path_color_list.size() - 1);
                path_color_list.remove(path_color_list.size() - 1);
                Log.v(TAG, "path_color_list.size() : " + path_color_list.size());

            }

        }
        else
        {
            if (path_color_list.size()>1)
            {
                if(path_color_list.get(path_color_list.size()-1).second.equals(path_color_list.get(path_color_list.size()-2).second))
                {
                    path_color_list.remove(path_color_list.size() - 1);
                    path_color_list.remove(path_color_list.size() - 1);
                }
            }
            else
            {
                if (path_color_list.size()>0)
                {
                    path_color_list.remove(path_color_list.size() - 1);
                    Log.v(TAG, "path_color_list.size() : " + path_color_list.size());
                }
            }
        }


        invalidate();
    }

    public void onClickUndo() {

        if (path_color_list.size() > 0) {
            path_color_list.remove(path_color_list.size() - 1);
            invalidate();
        }
    }


    //*************


    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 0;

    private void touch_start(float x, float y) {
        drawPath.reset();
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            drawPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (brushDrawMode) {
            float touchX = event.getX();
            float touchY = event.getY();

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:

                   // touch_start(touchX,touchY);
                   // paths.add(new PathPoints(drawPath, drawPaint.getColor(), false));
                    drawPath.moveTo(touchX, touchY);
                    path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));

                    if (onPhotoEditorSDKListener != null)
                        onPhotoEditorSDKListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);

                    invalidate();

                    break;

                case MotionEvent.ACTION_MOVE:

                   // touch_move(touchX,touchY);

                    drawPath.lineTo(touchX, touchY);

                    invalidate();

                    break;

                case MotionEvent.ACTION_UP:

                    isColorSlctdOnce = true;
                  //  drawPath.lineTo(mX, mY);
                    drawCanvas.drawPath(drawPath, drawPaint);

                    Log.e(TAG,"ACTION_UP path_color_list.add");
                   // path_color_list.add( new Pair<Path, Integer>(drawPath,drawPaint.getColor()));
                   // paths.add(new PathPoints(drawPath, drawPaint.getColor(), false));

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





    class PathPoints {
        private Path path;
        // private Paint mPaint;
        private int color;
        private String textToDraw;
        private boolean isTextToDraw;
        private int x, y;

        public PathPoints(Path path, int color, boolean isTextToDraw) {
            this.path = path;
            this.color = color;
            this.isTextToDraw = isTextToDraw;
        }

        public PathPoints(int color, String textToDraw, boolean isTextToDraw, int x, int y) {
            this.color = color;
            this.textToDraw = textToDraw;
            this.isTextToDraw = isTextToDraw;
            this.x = x;
            this.y = y;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

		/*
		 * private Paint getPaint() { mPaint = new Paint();
		 * mPaint.setAntiAlias(true); mPaint.setColor(color);
		 * mPaint.setStyle(Paint.Style.STROKE);
		 * mPaint.setStrokeJoin(Paint.Join.ROUND);
		 * mPaint.setStrokeCap(Paint.Cap.ROUND); mPaint.setStrokeWidth(6);
		 * return mPaint; }
		 */

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getTextToDraw() {
            return textToDraw;
        }

        public void setTextToDraw(String textToDraw) {
            this.textToDraw = textToDraw;
        }

        public boolean isTextToDraw() {
            return isTextToDraw;
        }

        public void setTextToDraw(boolean isTextToDraw) {
            this.isTextToDraw = isTextToDraw;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

    }


}
