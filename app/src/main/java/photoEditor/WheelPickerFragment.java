package photoEditor;

import android.app.Activity;
import android.app.Dialog;
import androidx.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.aigestudio.wheelpicker.WheelPicker;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.DialogFragmentWheelPickerBinding;

import java.util.ArrayList;
import java.util.List;

import Constants.Bundle_Identifier;


public class WheelPickerFragment extends DialogFragment implements View.OnClickListener{

    private final String TAG = WheelPickerFragment.class.getName();

    //private String murl;
    private DialogFragmentWheelPickerBinding mbinding;

    List<String> timeApprncList = new ArrayList<>();
    int media_time = 3;

    SnapAprncTimeInterface snapAprncTimeInterface;

    public WheelPickerFragment(int  media_time, SnapAprncTimeInterface snapAprncTimeInterface) {
        // Required empty public constructor
        this.snapAprncTimeInterface = snapAprncTimeInterface;
        this.media_time = media_time;
    }

   /* public static WheelPickerFragment newInstance(int  media_time, SnapAprncTimeInterface snapAprncTimeInterface) {
        WheelPickerFragment fragment = new WheelPickerFragment(snapAprncTimeInterface);
        Bundle args = new Bundle();
        args.putInt(Bundle_Identifier.MEDIA_TIME, media_time);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            media_time = getArguments().getInt(Bundle_Identifier.MEDIA_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_wheel_picker, container, false);
        View view = mbinding.getRoot();
        initView();
        return view;
    }

    /*set Background button and text of tutorial*/
    private void initView() {

        mbinding.ivCloseWheelPicker.setOnClickListener(this);

        addSnapAprncScnds();

        mbinding.wheelPicker.setData(timeApprncList);

        int index;

        if (media_time == 0)
        {
            index = 9;
        }
        else
        {
            index = timeApprncList.indexOf(String.valueOf(media_time));
        }


        Log.e(TAG,"index : "+index);
        Log.e(TAG,"media_time : "+media_time);

        mbinding.wheelPicker.setSelectedItemPosition(index);

        mbinding.wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {

                Log.v(TAG,"data.toString() : "+data.toString());
                Log.v(TAG,"position : "+position);

                Log.e(TAG,"ivCloseWheelPicker");

                if (position == 9)
                {
                    media_time = 0;
                }
                else
                {
                    media_time = Integer.parseInt(data.toString());
                }

            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.ivCloseWheelPicker:

                Log.e(TAG,"ivCloseWheelPicker");

                if (snapAprncTimeInterface != null)
                {
                    snapAprncTimeInterface.snapAprncTime(media_time);
                    Log.d("hey", "onClick: " + media_time);
                }
                dismiss();

                break;
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_trans)));
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            if (dialog != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_trans)));
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        if (snapAprncTimeInterface != null)
        {
            snapAprncTimeInterface.snapAprncTime(media_time);
        }

    }

    private void addSnapAprncScnds()
    {
        timeApprncList.add("1");
        timeApprncList.add("2");
        timeApprncList.add("3");
        timeApprncList.add("4");
        timeApprncList.add("5");
        timeApprncList.add("6");
        timeApprncList.add("7");
        timeApprncList.add("8");
        timeApprncList.add("9");
        timeApprncList.add(getResources().getString(R.string.infinity));
    }

    public interface SnapAprncTimeInterface{

        void snapAprncTime(int mediaTime);

    }



}
