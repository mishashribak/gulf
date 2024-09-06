package CustomView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.khaleeji.Adapter.NearByUserOnMapAdapter;
import com.app.khaleeji.R;

import Interfaces.OnDlgCloseListener;

public class CustomUserGroupLocatonDlg extends Dialog {

    private OnDlgCloseListener mListener;
    private CustomTextView tvNoData;

    public CustomUserGroupLocatonDlg(Context paramContext, boolean isNoData, NearByUserOnMapAdapter listAdapter) {
        super(paramContext, R.style.dialog);
        setContentView(R.layout.dialog_group_user_location);
        RecyclerView rv = findViewById(R.id.rvUsers);
        ImageView imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                disMissDialog();
                mListener.onClose();
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(listAdapter);

        tvNoData = findViewById(R.id.txtNoData);
        if( ! isNoData)
            tvNoData.setVisibility(View.GONE);
        else
            tvNoData.setVisibility(View.VISIBLE);
    }

    public void setOnCloseListener(OnDlgCloseListener listener){
        mListener = listener;
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
