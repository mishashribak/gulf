package CustomView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.app.khaleeji.R;

public class CustomForgotPwdDlg extends Dialog {
    private TextView mTx;
    private OnDlgCloseListener mListener;

    public CustomForgotPwdDlg(Context paramContext, OnDlgCloseListener onDlgCloseListener) {
        super(paramContext);
        setContentView(R.layout.dialog_forgetpassword);
        mListener = onDlgCloseListener;
        TextView tvtitle = (TextView) findViewById(R.id.tvtitle);
        TextView tv_viaphone = (TextView) findViewById(R.id.tv_viaphone);
        TextView tvviaemail = (TextView) findViewById(R.id.tvviaemail);
        TextView tvcancel = (TextView) findViewById(R.id.tvcancel);
//        if (isForgetpassword)
            tvtitle.setText(paramContext.getString(R.string.choose_forgetpassword));
//        else
//            tvtitle.setText(getString(R.string.choose_forgetUsername));

        tv_viaphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disMissDialog();
                mListener.onClose(true);
            }
        });

        tvviaemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disMissDialog();
                mListener.onClose(false);
            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disMissDialog();
            }
        });

    }

    public interface OnDlgCloseListener {
        void onClose(boolean isPhone);
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
}
