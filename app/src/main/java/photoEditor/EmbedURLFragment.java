package photoEditor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import androidx.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.app.khaleeji.R;
import com.app.khaleeji.databinding.DialogFragmentEmbedUrlBinding;

import java.util.ArrayList;
import java.util.List;

import Constants.Bundle_Identifier;
import Utility.AlertDialog;
import Utility.Utils;
import Utility.WebViewClientImpl;

public class EmbedURLFragment extends DialogFragment implements View.OnClickListener{

    private final String TAG = EmbedURLFragment.class.getName();

    //private String murl;
    private DialogFragmentEmbedUrlBinding mbinding;

    List<String> timeApprncList = new ArrayList<>();
    int media_time = 3;

    EmbedURLToPhotoInterface embedURLToPhotoInterface;

    String url = "";

    Activity activity;

    public EmbedURLFragment(){

    }
    public EmbedURLFragment(EmbedURLToPhotoInterface embedURLToPhotoInterface) {
        // Required empty public constructor
        this.embedURLToPhotoInterface = embedURLToPhotoInterface;
    }

    public static EmbedURLFragment newInstance(int media_time, EmbedURLToPhotoInterface embedURLToPhotoInterface) {
        EmbedURLFragment fragment = new EmbedURLFragment(embedURLToPhotoInterface);
        Bundle args = new Bundle();
        args.putInt(Bundle_Identifier.MEDIA_TIME, media_time);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;

    }

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
        mbinding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_embed_url, container, false);
        View view = mbinding.getRoot();
        initView();
        return view;
    }

    /*set Background button and text of tutorial*/
    private void initView() {

        mbinding.ivCloseLinkView.setOnClickListener(this);
        mbinding.ivSearchLink.setOnClickListener(this);
        mbinding.btnAttachLink.setOnClickListener(this);


        WebSettings webSettings = mbinding.webview.getSettings();
        webSettings.setJavaScriptEnabled(true);


        mbinding.etSrchURL.setText("https://");
        Selection.setSelection(mbinding.etSrchURL.getText(), mbinding.etSrchURL.getText().length());


        mbinding.etSrchURL.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().startsWith("https://"))
                {
                    mbinding.etSrchURL.setText("https://");
                    Selection.setSelection(mbinding.etSrchURL.getText(), mbinding.etSrchURL.getText().length());
                }
            }
        });

        mbinding.etSrchURL.setMaxLines(1);

        mbinding.etSrchURL.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (!mbinding.etSrchURL.getText().toString().trim().isEmpty()  )
                    {
                        if (Utils.isURLValid(mbinding.etSrchURL.getText().toString().trim()))
                        {
                            Log.v(TAG,"etSrchURL.getText().toString().trim() : "+mbinding.etSrchURL.getText().toString().trim());
                            searchLinkDetail(mbinding.etSrchURL.getText().toString().trim());
                        }
                        else
                        {

                            AlertDialog.showAlert(activity,
                                    getString(R.string.app_name), getString(R.string.enterValidUrl),
                                    getString(R.string.txt_ok),
                                    getString(R.string.cancel),
                                    false, null, null);
                        }

                    }
                    return true;
                }
                return false;
            }
        });

    }


    @Override
    public void onClick(View v) {

        hideKeyboard(activity);

        switch (v.getId()){

            case R.id.ivCloseLinkView:

                Log.e(TAG,"ivCloseWheelPicker");

                if (embedURLToPhotoInterface != null)
                {
                    embedURLToPhotoInterface.embedUrl("");

//                    if (Utils.stringIsNotEmpty(url))
//                    {
//                        embedURLToPhotoInterface.embedUrl(url);
//                    }
//                    else
//                    {
//                        embedURLToPhotoInterface.embedUrl("");
//                    }
                }

                dismiss();

                break;


            case R.id.ivSearchLink:

                if (!mbinding.etSrchURL.getText().toString().trim().isEmpty())
                {
//                    Log.v(TAG,"etSrchURL.getText().toString().trim() : "+mbinding.etSrchURL.getText().toString().trim());
//                    searchLinkDetail(mbinding.etSrchURL.getText().toString().trim());

                    if (Utils.isURLValid(mbinding.etSrchURL.getText().toString().trim()))
                    {
                        Log.v(TAG,"etSrchURL.getText().toString().trim() : "+mbinding.etSrchURL.getText().toString().trim());
                        searchLinkDetail(mbinding.etSrchURL.getText().toString().trim());
                    }
                    else
                    {
                        AlertDialog.showAlert(activity,
                                getString(R.string.app_name), getString(R.string.enterValidUrl),
                                getString(R.string.txt_ok),
                                getString(R.string.cancel),
                                false, null, null);
                    }
                }

                break;

            case R.id.btnAttachLink:

                if (embedURLToPhotoInterface != null)
                {
                    if (Utils.stringIsNotEmpty(url))
                    {
                        embedURLToPhotoInterface.embedUrl(url);
                    }
                    else
                    {
                        embedURLToPhotoInterface.embedUrl("");
                    }
                }

                Log.e(TAG,"url : "+url);

                dismiss();

                break;

        }
    }

    public void hideKeyboard(Activity activity) {

        Log.e(TAG,"hideKeyboard");

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null && activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
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

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        Dialog dialog = new Dialog(mActivity){
//            @Override
//            public void onBackPressed() {
//                //super.onBackPressed();
//
//                if (mbinding.webview.canGoBack())
//                {
//                    mbinding.webview.goBack();
//                }
//                else
//                {
//                    dismiss();
//                }
//
//            }
//        };
//
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//
//        if (dialog != null) {
//            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_trans)));
//        }
//
//
//        return dialog;
//    }



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

//        if (embedURLToPhotoInterface != null)
//        {
//            embedURLToPhotoInterface.embedUrl("");
//        }

        mbinding.btnAttachLink.setVisibility(View.GONE);

    }


    private void searchLinkDetail(String url)
    {
        //hideSoftKeyboard(mActivity);

        this.url = url;

        WebViewClientImpl webViewClient = new WebViewClientImpl(getActivity());
        mbinding.webview.setWebViewClient(new WebViewClient());

        mbinding.webview.loadUrl(url);
        mbinding.btnAttachLink.setVisibility(View.VISIBLE);
    }


    public interface EmbedURLToPhotoInterface{

        public void embedUrl(String url);

    }

}
