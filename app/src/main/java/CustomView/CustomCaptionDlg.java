package CustomView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.khaleeji.R;


import Interfaces.OnDlgCloseListener;

public class CustomCaptionDlg extends Dialog {

    private OnDlgCloseListener mListener;
    private Context mContext;
    private ItemClickInterface mItemClickInterface;
    private CustomButtonView mBtNext;
    private RelativeLayout rlRow;
    private CustomEditText  etCaption;


    public CustomCaptionDlg(Context paramContext, ItemClickInterface itemClickInterface) {
        super(paramContext, R.style.dialog);

        setContentView(R.layout.dialog_caption);

        mContext = paramContext;
        mItemClickInterface = itemClickInterface;

        etCaption = findViewById(R.id.etCaption);

        ImageView imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                disMissDialog();
            }
        });

        rlRow = findViewById(R.id.rlRow);
        rlRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });

        mBtNext = findViewById(R.id.btNext);
        mBtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickInterface.onClick(etCaption.getText().toString());
            }
        });


    }


    public void disMissDialog() {
        if (isShowing()) {
            dismiss();
        }
    }

    public void showDialog() {
        if (isShowing())
            dismiss();
        this.show();
    }

    public interface ItemClickInterface{
        void onClick(String str);
    }

    @Override
    public void onBackPressed() {

    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null && getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

}
