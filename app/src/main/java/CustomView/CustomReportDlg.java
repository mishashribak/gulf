package CustomView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;

import androidx.cardview.widget.CardView;

import com.app.khaleeji.R;


import Interfaces.OnDlgCloseListener;

public class CustomReportDlg extends Dialog {

    private OnDlgCloseListener mListener;
    private Context mContext;
    private ItemClickInterface mItemClickInterface;
    private CardView cardAbusiveMe;
    private CheckBox checkAbusiveMe;
    private CardView cardAbusiveOthers;
    private CheckBox checkAbuseOthers;
    private CardView cardPosting;
    private CheckBox checkPosting;
    private CardView cardUnsafe;
    private CheckBox checkUnsafe;
    private CardView cardOther;
    private CheckBox checkOther;
    private CustomTextView txtSubmit;

    public CustomReportDlg(Context paramContext, ItemClickInterface itemClickInterface) {
        super(paramContext, R.style.dialog);

        setContentView(R.layout.dialog_report_reason);

        mContext = paramContext;
        mItemClickInterface = itemClickInterface;

        txtSubmit = findViewById(R.id.txtSubmit);

        cardAbusiveMe = findViewById(R.id.cardAbusiveMe);
        checkAbusiveMe = findViewById(R.id.checkAbusiveMe);
        cardAbusiveMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCheckBox();
                checkAbusiveMe.setChecked(true);
            }
        });

        cardAbusiveOthers = findViewById(R.id.cardAbusiveOthers);
        checkAbuseOthers = findViewById(R.id.checkAbuseOthers);
        cardAbusiveOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCheckBox();
                checkAbuseOthers.setChecked(true);
            }
        });


        cardPosting = findViewById(R.id.cardPosting);
        checkPosting = findViewById(R.id.checkPosting);
        cardPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCheckBox();
                checkPosting.setChecked(true);
            }
        });


        cardUnsafe = findViewById(R.id.cardUnsafe);
        checkUnsafe = findViewById(R.id.checkUnsafe);
        cardUnsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCheckBox();
                checkUnsafe.setChecked(true);
            }
        });


        cardOther = findViewById(R.id.cardOther);
        checkOther = findViewById(R.id.checkOther);
        cardOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCheckBox();
                checkOther.setChecked(true);
            }
        });

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAbusiveMe.isChecked()){
                    mItemClickInterface.onClick(mContext.getString(R.string.reason_abuse_me));
                    disMissDialog();
                    return;
                }

                if(checkAbuseOthers.isChecked()){
                    mItemClickInterface.onClick(mContext.getString(R.string.reason_abuse_others));
                    disMissDialog();
                    return;
                }

                if( checkPosting.isChecked()){
                    mItemClickInterface.onClick(mContext.getString(R.string.reason_post));
                    disMissDialog();
                    return;
                }

                if(checkUnsafe.isChecked()){
                    mItemClickInterface.onClick(mContext.getString(R.string.reason_unsafe));
                    disMissDialog();
                    return;
                }

                if(checkOther.isChecked()){
                    mItemClickInterface.onClick(mContext.getString(R.string.reason_other));
                    disMissDialog();
                    return;
                }
            }
        });


//        ImageView imgClose = findViewById(R.id.imgClose);
//        imgClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Add your code in here!
//                disMissDialog();
//            }
//        });

    }

    private void initCheckBox(){
        checkAbusiveMe.setChecked(false);
        checkAbuseOthers.setChecked(false);
        checkPosting.setChecked(false);
        checkUnsafe.setChecked(false);
        checkOther.setChecked(false);
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
