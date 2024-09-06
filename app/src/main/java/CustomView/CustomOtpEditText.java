package CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

import com.app.khaleeji.R;

/**
 * Created by user on 13/9/17.
 */

public class CustomOtpEditText extends androidx.appcompat.widget.AppCompatEditText {

    private static final String TAG = "TextView";
    public   keyEvnt keyEvnt;


    public CustomOtpEditText(Context context) {
        super(context);
    }

    public CustomOtpEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomOtpEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewCustom);
        String customFont = a.getString(R.styleable.TextViewCustom_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: " + e.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }


    /* @Override
     public boolean onKeyPreIme(int keyCode, KeyEvent event) {

         if (keyCode == KeyEvent.KEYCODE_BACK) {
             // User has pressed Back key. So hide the keyboard

             return  true;
             // TODO: Hide your view as you do it in your activity
         } else if (keyCode == KeyEvent.KEYCODE_MENU) {
             // Eat the event
             return true;
         }

         return false;
     }*/




    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_DEL){

            Log.e(TAG, "KEYCODE_test" +"KEYCODE_DEL");

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyEvnt != null) {
                    keyEvnt.eventclcik(this.getId());
                }



            }

            return   super.dispatchKeyEvent(event);
        }
        /*else if(event.getKeyCode()==KeyEvent.KEYCODE_0||event.getKeyCode()==KeyEvent.KEYCODE_1||event.getKeyCode()==KeyEvent.KEYCODE_2||event.getKeyCode()==KeyEvent.KEYCODE_3||event.getKeyCode()==KeyEvent.KEYCODE_4||event.getKeyCode()==KeyEvent.KEYCODE_5||event.getKeyCode()==KeyEvent.KEYCODE_6||event.getKeyCode()==KeyEvent.KEYCODE_7||event.getKeyCode()==KeyEvent.KEYCODE_8||event.getKeyCode()==KeyEvent.KEYCODE_9){

            Log.e(TAG, "KEYCODE_test" +event.getKeyCode());

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyEvnt != null) {
                    Log.e(TAG, "KEYCODE_" +event.getAction());

                    keyEvnt.eventclciknext(0);
                }
            }
            return super.dispatchKeyEvent(event);

        }*/

        return  super.dispatchKeyEvent(event);
    }



;


    public interface  keyEvnt{

        public void eventclcik(int id);

        public void eventclciknext(int id);

    }

    public void setOnKeyDelListener(keyEvnt mKeyEvent){
        this.keyEvnt=mKeyEvent;

    }

}
