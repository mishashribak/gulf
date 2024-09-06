package Utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.app.khaleeji.R;

public class ProgressDialog {

    private static Dialog dialog;

    public static void showProgress(Context mcontext) {
       try{

           if(dialog != null && dialog.isShowing())
               return;

            dialog = new Dialog(mcontext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_progressbar);
            dialog.show();

       }catch (Exception e){
           e.printStackTrace();
       }
    }


    public static void hideprogressbar() {
        try{
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog=null;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
