package photoEditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import androidx.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;


import java.util.ArrayList;

import android.util.Log;
import android.view.MotionEvent;


public class DrawingPanel extends View implements View.OnTouchListener {

    private final String TAG = DrawingPanel.class.getName();

    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint, mBitmapPaint;
    private ArrayList<PathPoints> paths = new ArrayList<PathPoints>();
    private ArrayList<PathPoints> undonePaths = new ArrayList<PathPoints>();
    private Bitmap mBitmap;
    private int color;
    private int x, y;

    private float brushSize = 10;
    private float brushEraserSize = 100;

    private OnPhotoEditorSDKListener onPhotoEditorSDKListener;

    private boolean brushDrawMode;

    public DrawingPanel(Context context) {
        this(context, null);
    }

    public DrawingPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupBrushDrawing();

    }

    public DrawingPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setupBrushDrawing();
    }

    public DrawingPanel(Context context, int color) {
        super(context);
       setupBrushDrawing();
    }

    private void setupBrushDrawing()
    {
        this.color = color;
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(brushSize);
        mPaint.setTextSize(30);

        mPath = new Path();
        paths.add(new PathPoints(mPath, color, false));
        mCanvas = new Canvas();
    }

    void setBrushEraserColor(@ColorInt int color){
        mPaint.setColor(color);
        refreshBrushDrawing();
    }
    float getEraserSize() {
        return brushEraserSize;
    }

    float getBrushSize() {
        return brushSize;
    }

    int getBrushColor() {
        return mPaint.getColor();
    }

    void setBrushSize(float size) {
        brushSize = size;
        refreshBrushDrawing();
    }

    void setBrushColor(@ColorInt int color) {
        this.color = color;
        mPaint.setColor(color);
        refreshBrushDrawing();
    }

    void brushEraser() {
        mPaint.setStrokeWidth(brushEraserSize);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setOnPhotoEditorSDKListener(OnPhotoEditorSDKListener onPhotoEditorSDKListener) {
        this.onPhotoEditorSDKListener = onPhotoEditorSDKListener;
    }

    public void colorChanged(int color) {
        this.color = color;
        mPaint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        /*
        // mBitmap = AddReportItemActivity.mPhoto;
        mBitmap = getIntent().getExtras().getParcelable(ChooseActivity.BITMAP);
        float xscale = (float) w / (float) mBitmap.getWidth();
        float yscale = (float) h / (float) mBitmap.getHeight();
        if (xscale > yscale) // make sure both dimensions fit (use the
            // smaller scale)
            xscale = yscale;
        float newx = (float) w * xscale;
        float newy = (float) h * xscale; // use the same scale for both
        // dimensions
        // if you want it centered on the display (black borders)
        mBitmap = Bitmap.createScaledBitmap(mBitmap, this.getWidth(),
                this.getHeight(), true);
        // mCanvas = new Canvas(mBitmap);
        */

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //drawCanvas = new Canvas(canvasBitmap);
    }

    private void refreshBrushDrawing() {
        brushDrawMode = true;
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(brushSize);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
    }

    void setBrushEraserSize(float brushEraserSize) {
        this.brushEraserSize = brushEraserSize;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        for (PathPoints p : paths) {
            mPaint.setColor(p.getColor());
            Log.v(TAG, "Color code : " + p.getColor());
            if (p.isTextToDraw()) {
                canvas.drawText(p.textToDraw, p.x, p.y, mPaint);
            } else {
                canvas.drawPath(p.getPath(), mPaint);
            }
        }
    }

    void setBrushDrawingMode(boolean brushDrawMode) {
        this.brushDrawMode = brushDrawMode;
        if (brushDrawMode) {
            this.setVisibility(View.VISIBLE);
            refreshBrushDrawing();
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 0;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        if (onPhotoEditorSDKListener != null)
            onPhotoEditorSDKListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath = new Path();
        paths.add(new PathPoints(mPath, color, false));
        if (onPhotoEditorSDKListener != null)
            onPhotoEditorSDKListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);
    }

//    private void drawText(int x, int y) {
//        Log.v(TAG, "Here");
//        Log.v(TAG, "X " + x + " Y " + y);
//        this.x = x;
//        this.y = y;
//        paths.add(new PathPoints(color, textToDraw, true, x, y));
//        // mCanvas.drawText(textToDraw, x, y, mPaint);
//    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                if (!isTextModeOn) {
//                    touch_start(x, y);
//                    invalidate();
//                }
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
//                if (!isTextModeOn) {
//                    touch_move(x, y);
//                    invalidate();
//                }
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
//                if (isTextModeOn) {
//                    drawText((int) x, (int) y);
//                    invalidate();
//                } else {
//                    touch_up();
//                    invalidate();
//                }

                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void onClickUndo() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        } else {

        }
        // toast the user
    }

    public void onClickRedo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        } else {

        }
        // toast the user
    }


    void clearAll() {
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
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
