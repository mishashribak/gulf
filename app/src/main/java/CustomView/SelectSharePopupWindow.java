package CustomView;

import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.app.khaleeji.R;

public class SelectSharePopupWindow extends PopupWindow
{
    private CustomButtonView cancelBtn;
    private CustomButtonView btUnBlock;
    private CustomButtonView btBlock;
    private CustomButtonView btReport;
    private CustomButtonView btProfile;
    private CustomButtonView btQr;

    public SelectSharePopupWindow(View mMenuView, View.OnClickListener paramOnClickListener)
    {
        super(mMenuView ,RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT,true);


        btProfile = mMenuView.findViewById(R.id.btProfile);
        btQr = mMenuView.findViewById(R.id.btQr);
        cancelBtn = mMenuView.findViewById(R.id.btCancel);
        cancelBtn.setOnClickListener(paramOnClickListener);
        btProfile.setOnClickListener(paramOnClickListener);
        btQr.setOnClickListener(paramOnClickListener);
        
        setContentView(mMenuView);
        setFocusable(true);
        setAnimationStyle(R.style.PopupAnimation);
        mMenuView.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
            {
                int i = mMenuView.findViewById(R.id.pop_layout).getTop();
                int j = (int)paramMotionEvent.getY();
                if ((paramMotionEvent.getAction() == 1) && (j < i))
                    dismiss();
                return true;
            }
        });
    }
}
