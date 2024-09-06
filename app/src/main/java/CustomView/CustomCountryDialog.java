package CustomView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.khaleeji.R;

public class CustomCountryDialog extends Dialog {
    private TextView mTx;
    private OnItemClickListener mItemClickListener;

    public CustomCountryDialog(Context paramContext, OnItemClickListener itemClickListener) {
        super(paramContext, R.style.dialog);
        setContentView(R.layout.dialog_country);
        mItemClickListener = itemClickListener;

        ImageView imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                disMissDialog();
            }
        });

        LinearLayout llBahrain = findViewById(R.id.llBahrain);
        LinearLayout llSaudi = findViewById(R.id.llSaudi);
        LinearLayout llOman = findViewById(R.id.llOman);
        LinearLayout llYemen = findViewById(R.id.llYemen);
        LinearLayout llQatar = findViewById(R.id.llQatar);
        LinearLayout llkuwait = findViewById(R.id.llKuwait);
        LinearLayout llIraq = findViewById(R.id.llIraq);
        LinearLayout llUA = findViewById(R.id.llUA);

        llBahrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(0);
                disMissDialog();
            }
        });

        llIraq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(1);
                disMissDialog();
            }
        });

        llkuwait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(2);
                disMissDialog();
            }
        });

        llOman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(3);
                disMissDialog();
            }
        });

        llQatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(4);
                disMissDialog();
            }
        });

        llSaudi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(5);
                disMissDialog();
            }
        });

        llUA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(6);
                disMissDialog();
            }
        });

        llYemen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(7);
                disMissDialog();
            }
        });
    }

    public interface OnItemClickListener{
        void onItemClick(int type);
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
