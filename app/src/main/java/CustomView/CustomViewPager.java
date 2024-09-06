package CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {


    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    float mStartDragX;
    float mStartDragY;
    OnSwipeOutListener mListener;


    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mListener = listener;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartDragX = x;
                mStartDragY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(mStartDragX - x) > 10 && Math.abs(mStartDragY - y) < 20){
                    if (mStartDragX < x ) {
                        if(mListener != null)
                            mListener.onSwipePrev();
                    } else if (mStartDragX > x ) {
                        if(mListener != null)
                            mListener.onSwipeNext();
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnSwipeOutListener {
        public void onSwipePrev();
        public void onSwipeNext();
    }
}
