package CustomView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.khaleeji.Adapter.MindMentionAdapter;
import com.app.khaleeji.Adapter.MindTagAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.AddCommentResponse;
import com.app.khaleeji.Response.Search.SearchByNameResponse;
import com.app.khaleeji.Response.Search.SearchFriendsModel;
import org.greenrobot.eventbus.EventBus;
import java.util.HashMap;
import java.util.Map;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Interfaces.OnDlgCloseListener;
import Utility.ApiClass;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomMindDlg extends Dialog {
    private TextView mTx;
    private OnDlgCloseListener mListener;
    private RecyclerView mRvContent;
    private CustomEditText etMind;
    private MindMentionAdapter mMindMentionAdapter;
    private MindTagAdapter mMindTagAdapter;
    private boolean mIsMemoryDescription;
    private Context mContext;
    private ImageView mImgMind;
    private ImageView mImgMemoryDesc;
    private ItemClickInterface mItemClickInterface;
    private CustomButtonView mBtNext;
    private RelativeLayout rlRow;
    private String mStrProfileStatus="";
    private String mStrTag="";
    private String mStrMention="";
    private SearchFriendsModel mSearchFriendsModel;
    private SearchByNameResponse mSearchByNameResponse;
    private CustomTextView txtNoData;
    private ProgressBar loading;

    public CustomMindDlg(Context paramContext, boolean isMemory, ItemClickInterface itemClickInterface) {
        super(paramContext, R.style.dialog);
        setContentView(R.layout.dialog_mind);

        mContext = paramContext;

        loading = findViewById(R.id.loading);

        txtNoData = findViewById(R.id.txtNoData);

        rlRow = findViewById(R.id.rlRow);
        rlRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mItemClickInterface = itemClickInterface;

        ImageView imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                disMissDialog();
            }
        });

        mBtNext = findViewById(R.id.btNext);
        mBtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               callUpdateStatus();
            }
        });

        mIsMemoryDescription = isMemory;

        CustomTextView txtTitle = findViewById(R.id.bar);
        mImgMemoryDesc = findViewById(R.id.imgMemoryDesc);
        mImgMind = findViewById(R.id.imgMind);
        etMind = findViewById(R.id.etMind);

        if(! mIsMemoryDescription){
            txtTitle.setText(mContext.getResources().getString(R.string.what_mind));
            etMind.setHint(mContext.getResources().getString(R.string.add_status_placeholder));
            mImgMind.setVisibility(View.VISIBLE);
            mImgMemoryDesc.setVisibility(View.GONE);
        }else{
            txtTitle.setText(mContext.getResources().getString(R.string.write_memory));
            etMind.setHint(mContext.getResources().getString(R.string.add_desc_placeholder));
            mImgMemoryDesc.setVisibility(View.VISIBLE);
            mImgMind.setVisibility(View.GONE);
        }

        mImgMind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                disMissDialog();
//                mItemClickInterface.onMindClick();
            }
        });

        mImgMemoryDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disMissDialog();
                mItemClickInterface.onMemoryDescClick();
            }
        });

        mRvContent = findViewById(R.id.rvContent);

        mMindTagAdapter= new MindTagAdapter(getContext(), new MindTagAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String tag) {
                hideSoftKeyboard();
                etMind.setText('#'+tag);
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRvContent.setLayoutManager(mLayoutManager);

        if(!mIsMemoryDescription)
            mRvContent.setAdapter(mMindMentionAdapter);
        else
            mRvContent.setAdapter(mMindTagAdapter);

        CustomTextView txtAddTag = findViewById(R.id.txtAddTag);
        txtAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMind.setText("#");
            }
        });

        CustomTextView txtAddMention = findViewById(R.id.txtAddMention);
        txtAddMention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMind.setText("@");
            }
        });

        mMindMentionAdapter = new MindMentionAdapter(getContext(), new MindMentionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String username) {
                hideSoftKeyboard();
                mStrMention = '@'+username;
                String str = etMind.getText().toString();
                etMind.setText(str.replace(str.substring( str.lastIndexOf("@")), mStrMention+" "));
                mMindMentionAdapter.setData(null);
            }
        });

        etMind.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int pos = s.toString().lastIndexOf('@');
                if(pos != -1  && ! s.toString().substring(pos).contains(" ") && s.toString().substring(pos).length() > 2){
                    if(pos == 0 || s.toString().substring(pos-1, pos).contains(" "))
                                callSearchForFriendsByname( s.toString().substring( s.toString().lastIndexOf("@")+1));
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void setOnCloseListener(OnDlgCloseListener listener){
        mListener = listener;
    }

    public void disMissDialog() {
        if (isShowing()) {
            dismiss();
            mListener.onClose();
        }
    }

    public void showDialog() {
        if (isShowing())
            dismiss();
        this.show();
    }

    public interface ItemClickInterface{
        void onMindClick();
        void onMemoryDescClick();
    }

    @Override
    public void onBackPressed() {

    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null && getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void callUpdateStatus(){
            if(etMind.getText().toString() == null || etMind.getText().toString().isEmpty()){
                return;
            }
            ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
            Map mparams = new HashMap();
            mparams.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(mContext).getUserdetail().getId().toString());
            mparams.put("profileStatus",etMind.getText().toString());
//            mparams.put("tag", mStrTag);
//            mparams.put("mention", mStrMention);

            Call<AddCommentResponse> call = mApiInterface.updateStatus(mparams);

            call.enqueue(new Callback<AddCommentResponse>() {
                @Override
                public void onResponse(Call<AddCommentResponse> call, Response<AddCommentResponse> response) {
                    if (response.isSuccessful())
                    {
                        AddCommentResponse addCommentResponse = response.body();
                        if(addCommentResponse!=null && addCommentResponse.getStatus().equalsIgnoreCase("true")){
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.PROFILE_REFRESH);
                            EventBus.getDefault().post(messageEvent);

                            MessageEvent messageEvent1 = new MessageEvent();
                            messageEvent1.setType(MessageEvent.MessageType.STATUS_REFRESH);
                            EventBus.getDefault().post(messageEvent1);
                        }
                        Toast.makeText(mContext, addCommentResponse.getMessage(),Toast.LENGTH_SHORT).show();
                        disMissDialog();
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
                @Override
                public void onFailure(Call<AddCommentResponse> call, Throwable t) {
                }
            });
    }

    public  void callSearchForFriendsByname(String searchStr){
        loading.setVisibility(View.VISIBLE);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mContext).getUserdetail().getId();
        Call<SearchByNameResponse> call = mApiInterface.seachForFriendsByname(userid, searchStr);
        call.enqueue(new Callback<SearchByNameResponse>() {
            @Override
            public void onResponse(Call<SearchByNameResponse> call, Response<SearchByNameResponse> response) {
                loading.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    mSearchByNameResponse = response.body();
                    if(mSearchByNameResponse!=null) {
                        if (mSearchByNameResponse.getStatus().equalsIgnoreCase("true")) {
                            if ( mSearchByNameResponse.getData() != null && mSearchByNameResponse.getData().size() > 0) {
                                mRvContent.setAdapter(mMindMentionAdapter);
                                mMindMentionAdapter.notifyDataSetChanged();
                                mMindMentionAdapter.setData(mSearchByNameResponse.getData());
                                mMindMentionAdapter.getFilter().filter(searchStr);
                                txtNoData.setVisibility(View.GONE);
                            } else {
                                txtNoData.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }


            @Override
            public void onFailure(Call<SearchByNameResponse> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }

    public void callSeachForUserByhastag(){
       /* ProgressDialog.showProgress(mContext);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mContext).getUserdetail().getId();
        String tag = etMind.getText().toString().substring(1);
        Call<SearchFriendsModel> call = mApiInterface.seachForUserByhastag(userid, tag);
        call.enqueue(new Callback<SearchFriendsModel>() {
            @Override
            public void onResponse(Call<SearchFriendsModel> call, Response<SearchFriendsModel> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    mSearchFriendsModel = response.body();

                    if(mSearchFriendsModel!=null) {
                        if (mSearchFriendsModel.getStatus().equalsIgnoreCase("true")) {
                            if (mSearchFriendsModel.getData() != null && mSearchFriendsModel.getData().getData().size() > 0) {
                                mRvContent.setAdapter(mMindTagAdapter);
                                mMindTagAdapter.notifyDataSetChanged();
                                mMindTagAdapter.setData(mSearchFriendsModel.getData().getData());
                                mMindTagAdapter.getFilter().filter(tag);
                                txtNoData.setVisibility(View.GONE);
                            } else {
                                txtNoData.setVisibility(View.VISIBLE);
                            }
                        } else {
                            AlertDialog.showAlert(mContext, mContext.getString(R.string.app_name), mSearchFriendsModel.getMessage(),
                                    mContext.getString(R.string.txt_Done), mContext.getString(R.string.cancel), false, null, null);
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }


            @Override
            public void onFailure(Call<SearchFriendsModel> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });*/
    }
}
