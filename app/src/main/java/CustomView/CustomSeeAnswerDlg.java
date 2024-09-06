package CustomView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.khaleeji.Adapter.SeeAnswerAdapter;
import com.app.khaleeji.R;

public class CustomSeeAnswerDlg extends Dialog {
    private TextView mTx;
    private OnDlgItemClickListener mListener;
    public CustomSeeAnswerDlg(Context paramContext, SeeAnswerAdapter listAdapter) {
        super(paramContext, R.style.dialog);
        setContentView(R.layout.dialog_see_answer);
        CustomTextView bar = findViewById(R.id.bar);

        RecyclerView rv = findViewById(R.id.rvQAs);

        ImageView imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disMissDialog();
            }
        });

        CustomTextView txtReject = findViewById(R.id.txtReject);
        txtReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                disMissDialog();
                mListener.onClick(1);
            }
        });

        CustomTextView txtDisLike = findViewById(R.id.txtDisLike);
        txtDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                disMissDialog();
                mListener.onClick(2);
            }
        });

        CustomTextView txtAccept = findViewById(R.id.txtAccept);
        txtAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                disMissDialog();
                mListener.onClick(3);
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
        void onClick(int type);
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
