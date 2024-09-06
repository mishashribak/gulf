package CustomView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.khaleeji.Adapter.SendAnswerAdapter;
import com.app.khaleeji.R;

public class CustomSenderAnswerDlg extends Dialog {

    private OnDlgItemClickListener mListener;

    public CustomSenderAnswerDlg(Context paramContext, String name, SendAnswerAdapter listAdapter) {
        super(paramContext, R.style.dialog);
        setContentView(R.layout.dialog_send_answer);

        CustomTextView bar = findViewById(R.id.bar);
        bar.setText(name + "'s Questions");

        RecyclerView rv = findViewById(R.id.rvQAs);

        ImageView imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disMissDialog();
            }
        });

        CustomTextView txtSendRequest = findViewById(R.id.txtSendRequest);
        txtSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                disMissDialog();
                mListener.onClick();
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(listAdapter);

    }

    public void setOnItemClickListener(OnDlgItemClickListener listener){
        mListener = listener;
    }

    public interface OnDlgItemClickListener{
        void onClick();
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

    @Override
    public void onBackPressed() {

    }
}
