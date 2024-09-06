package CustomView;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.khaleeji.Adapter.LocationListAdapter;
import com.app.khaleeji.R;

import Constants.AppConstants;
import Interfaces.OnDlgCloseListener;
import Utility.ApiClass;

public class CustomLocationDlg extends Dialog {
    private TextView mTx;
    private OnDlgCloseListener mListener;
    private LocationImgItemClick mLocationImgItemClick;
    private ImageView imgSleep;
    private ImageView imgDrink;
    private ImageView imgFlare;
    private ImageView imgFood;
    private ImageView imgShop;
    private ImageView imgTree;
    private Context mContext;
    private CustomTextView txtNoData;
    private CustomEditText etSearch;

    public CustomLocationDlg(Context paramContext, LocationListAdapter listAdapter, LocationImgItemClick locationImgItemClick) {
        super(paramContext, R.style.dialog);
        setContentView(R.layout.dialog_location_layout);

        mContext = paramContext;
        mLocationImgItemClick = locationImgItemClick;

        etSearch = findViewById(R.id.search_edit);
        etSearch.addTextChangedListener(new TextWatcher() {


            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(listAdapter!=null) {
                    listAdapter.getFilter().filter(s);
                }
            }
        });

        txtNoData = findViewById(R.id.txtNoData);
        RecyclerView rv = findViewById(R.id.rvHotspot);
      /*  ImageView imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                disMissDialog();
                mListener.onClose();
            }
        });*/

        ImageView imgDown = findViewById(R.id.imgDown);
        imgDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your code in here!
                disMissDialog();
                mListener.onClose();
            }
        });

        imgSleep = findViewById(R.id.imgSleep);
        imgSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationImgItemClick.onLocationItemClick(AppConstants.HOTEL, "lodging");
                setDefaultImageSize();
                imgSleep.getLayoutParams().height = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgSleep.getLayoutParams().width = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgSleep.requestLayout();
            }
        });

        imgDrink = findViewById(R.id.imgDrink);
        imgDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationImgItemClick.onLocationItemClick(AppConstants.CAFE, "cafe");
                setDefaultImageSize();
                imgDrink.getLayoutParams().height = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgDrink.getLayoutParams().width = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgDrink.requestLayout();
            }
        });

        imgFlare = findViewById(R.id.imgFlare);
        imgFlare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationImgItemClick.onLocationItemClick(AppConstants.FLARE, "flare");
                setDefaultImageSize();
                imgFlare.getLayoutParams().height = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgFlare.getLayoutParams().width = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgFlare.requestLayout();

            }
        });

        imgFood = findViewById(R.id.imgFood);
        imgFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationImgItemClick.onLocationItemClick( AppConstants.RESTAURANT,"restaurant");
                setDefaultImageSize();
                imgFood.getLayoutParams().height = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgFood.getLayoutParams().width = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgFood.requestLayout();
            }
        });

        imgShop = findViewById(R.id.imgShop);
        imgShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationImgItemClick.onLocationItemClick(AppConstants.SHOPPING, "shopping_mall");
                setDefaultImageSize();
                imgShop.getLayoutParams().height = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgShop.getLayoutParams().width = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgShop.requestLayout();
            }
        });

        imgTree = findViewById(R.id.imgTree);
        imgTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationImgItemClick.onLocationItemClick(AppConstants.OUTDOOR,"park");
                setDefaultImageSize();
                imgTree.getLayoutParams().height = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgTree.getLayoutParams().width = (int)paramContext.getResources().getDimension(R.dimen.size_40);
                imgTree.requestLayout();
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(listAdapter);

    }

    public void setOnCloseListener(OnDlgCloseListener listener){
        mListener = listener;
    }

    public void showNoData(){
        txtNoData.setVisibility(View.VISIBLE);
    }

    public void hideNoData(){
        txtNoData.setVisibility(View.GONE);
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

    public interface LocationImgItemClick{
        void onLocationItemClick(int index, String category);
    }

    public void setDefaultImageSize(){
        imgSleep.getLayoutParams().height = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgSleep.getLayoutParams().width = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgSleep.requestLayout();

        imgDrink.getLayoutParams().height = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgDrink.getLayoutParams().width = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgDrink.requestLayout();

        imgFlare.getLayoutParams().height = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgFlare.getLayoutParams().width = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgFlare.requestLayout();

        imgFood.getLayoutParams().height = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgFood.getLayoutParams().width = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgFood.requestLayout();

        imgShop.getLayoutParams().height = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgShop.getLayoutParams().width = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgShop.requestLayout();

        imgTree.getLayoutParams().height = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgTree.getLayoutParams().width = (int)mContext.getResources().getDimension(R.dimen.size_30);
        imgTree.requestLayout();
    }

    @Override
    public void onBackPressed() {

    }
}
