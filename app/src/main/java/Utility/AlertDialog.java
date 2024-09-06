package Utility;

import android.content.Context;
import android.content.DialogInterface;

import com.app.khaleeji.R;


public class AlertDialog {

    public static void showAlert(Context mcontext, String title, String message, String oktag,
                                 String CancelTag, boolean showCancel, final DialogInterface.OnClickListener onOkClick,
                                 final DialogInterface.OnClickListener onCancelClick)
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mcontext, R.style.MyAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(oktag, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onOkClick != null)
                    onOkClick.onClick(dialog, which);
                dialog.dismiss();
            }
        });
        if (showCancel)
        {
            builder.setNegativeButton(CancelTag, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (onCancelClick != null)
                        onCancelClick.onClick(dialog, which);

                    dialog.dismiss();
                }
            });
        }
        builder.show();
    }
}
