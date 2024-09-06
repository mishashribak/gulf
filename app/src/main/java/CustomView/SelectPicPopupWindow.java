package CustomView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.app.khaleeji.R;

public class SelectPicPopupWindow extends PopupWindow
{
    private CustomButtonView cancelBtn;
    private CustomButtonView btUnBlock;
    private CustomButtonView btBlock;
    private CustomButtonView btReport;
    private CustomButtonView btGallery;
    private CustomButtonView btCamera;

    public SelectPicPopupWindow(View mMenuView, View.OnClickListener paramOnClickListener, int type)
    {
        super(mMenuView ,RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT,true);

        if(type == 0){
            //friend profile
            btReport = mMenuView.findViewById(R.id.btReport);
            btBlock = mMenuView.findViewById(R.id.btBlock);
            cancelBtn = mMenuView.findViewById(R.id.btCancel);
            btUnBlock = mMenuView.findViewById(R.id.btUnBlock);
            cancelBtn.setOnClickListener(paramOnClickListener);
            btUnBlock.setOnClickListener(paramOnClickListener);
            btBlock.setOnClickListener(paramOnClickListener);
            btReport.setOnClickListener(paramOnClickListener);
        }else if(type == 1){
            //my profile
            btGallery = mMenuView.findViewById(R.id.btGallery);
            btCamera = mMenuView.findViewById(R.id.btCamera);
            cancelBtn = mMenuView.findViewById(R.id.btCancel);
            cancelBtn.setOnClickListener(paramOnClickListener);
            btGallery.setOnClickListener(paramOnClickListener);
            btCamera.setOnClickListener(paramOnClickListener);
        }

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

    public void setUnblockUI(){
        btUnBlock.setVisibility(View.VISIBLE);
        btBlock.setVisibility(View.GONE);
    }

    public void setBlockUI(){
        btUnBlock.setVisibility(View.GONE);
        btBlock.setVisibility(View.VISIBLE);
    }
}
