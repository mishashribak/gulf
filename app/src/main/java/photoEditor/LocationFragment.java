package photoEditor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import com.app.khaleeji.Adapter.NearByLocationAdapter;
import com.app.khaleeji.Adapter.SearchLocationAdapter;
import com.app.khaleeji.Fragments.BaseFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.nearByLocation.NearByLocationResponse;
import com.app.khaleeji.Response.nearByLocation.NearByLocationResult;
import com.app.khaleeji.Response.searchLocationPackage.SearchLocationPrediction;
import com.app.khaleeji.Response.searchLocationPackage.SearchLocationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Model.AreaModel;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.DebugLog;
import Utility.ProgressDialog;
import Utility.SavePref;
import Utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LocationFragment extends BaseFragment implements LocationAdapter.OnImageClickListener, AdapterView.OnItemClickListener,
        NearByLocationAdapter.OnChoosingLocationListener, SearchLocationAdapter.OnChoosingSearchLocationListener {

    public static final String STATUS = "status";
    public static final String OK = "OK";
    public static final String ZERO_RESULTS = "ZERO_RESULTS";
    public static final String GEOMETRY = "geometry";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String SUPERMARKET_ID = "id";
    public static final String NAME = "name";
    public static final String PLACE_ID = "place_id";
    public static final String REFERENCE = "reference";
    public static final String VICINITY = "vicinity";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/textsearch";
    private static final String OUT_JSON = "/json";
    private final String TAG = LocationFragment.class.getName();
    private OnStrickerClickListener onStrickerClickListener;
    public RecyclerView imageRecyclerView, rvNearByLocation;
    AutoCompleteTextView et_currentlocation;
    double lat, lng;
    NearByLocationAdapter nearByLocationAdapter;
    SearchLocationAdapter searchLocationAdapter;
    List<NearByLocationResult> nearByLocations;
    List<SearchLocationPrediction> searchLocationPredictions;
    LinearLayout linParentLayout;
    private LocationAdapter adapter;

    @SuppressLint("ValidFragment")
    public LocationFragment(OnStrickerClickListener onStrickerClickListener) {
        this.onStrickerClickListener = onStrickerClickListener;
    }

    public LocationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TypedArray images = getResources().obtainTypedArray(R.array.photo_editor_photos);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);
        et_currentlocation = rootView.findViewById(R.id.et_currentlocation);
        imageRecyclerView = rootView.findViewById(R.id.fragment_main_photo_edit_image_rv);
        rvNearByLocation = rootView.findViewById(R.id.rvNearByLocation);
        linParentLayout = rootView.findViewById(R.id.linParentLayout);
        linParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        lat = SavePref.getInstance(getActivity()).getLatitude();
        lng = SavePref.getInstance(getActivity()).getLongitude();
        et_currentlocation.setOnItemClickListener(this);
        et_currentlocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    rvNearByLocation.setVisibility(View.VISIBLE);
                    imageRecyclerView.setVisibility(View.GONE);
                    linParentLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
                    if (CheckConnection.isNetworkAvailable(mActivity)) {
                        fetchSearchedLocations(s.toString());
                    } else {
                        AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.network_down), getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                    }
                } else {
                    if (CheckConnection.isNetworkAvailable(mActivity)) {
                        fetchNearByLocations();
                    } else {
                        AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.network_down), getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                    }
                }
            }
        });

        if (CheckConnection.isNetworkAvailable(mActivity)) {
            fetchNearByLocations();
        } else {
            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.network_down), getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
        }

        return rootView;
    }

    public ArrayList autocomplete(final String input) {

        final ArrayList<AreaModel> resultList = new ArrayList();
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        StringBuilder sb = null;
        try {
            sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + ApiClass.GooglePlaceKey);
            sb.append("&query=" + input.replace(" ", "%20"));
            Log.d(TAG, "urlL" + sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            DebugLog.log(1, "result_name ", jsonResults.toString());

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            resultList.clear();
            resultList.addAll(parseLocationResult(jsonObj));

        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //  mGooglePlacesAutocompleteAdapter.setList(resultList);
            }
        });
        return resultList;
    }

    private ArrayList parseLocationResult(JSONObject result) {
        ArrayList<AreaModel> resultList = null;
        String id, place_id, placeName = null, reference, icon, vicinity = null;
        double latitude, longitude;
        try {
            final JSONArray jsonArray = result.getJSONArray("results");
            resultList = new ArrayList(jsonArray.length());
            if (result.getString(STATUS).equalsIgnoreCase(OK)) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);
                    AreaModel mAreaModel = new AreaModel();
                    id = place.getString(SUPERMARKET_ID);
                    place_id = place.getString(PLACE_ID);
                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LATITUDE);
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LONGITUDE);
                    if (!place.isNull(NAME)) {
                        placeName = place.getString(NAME);
                        DebugLog.log(1, "reuslt_name ", placeName);
                        mAreaModel.setName(placeName);
                        mAreaModel.setLat(latitude);
                        mAreaModel.setLng(longitude);
                        resultList.add(mAreaModel);
                    }
                    if (!place.isNull(VICINITY)) {
                        vicinity = place.getString(VICINITY);
                   }
                    reference = place.getString(REFERENCE);
                }
            } else if (result.getString(STATUS).equalsIgnoreCase(ZERO_RESULTS)) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "No Area found in  radius!!!");
                        //Toast.makeText(getActivity(), "No Area found in  radius!!!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }
       return resultList;
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public void onImageClickListener(Bitmap image) {
        onStrickerClickListener.onStickerClick(image,AppConstants.TYPE_LOCATION);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void hideSoftKeyboard(Activity activity) {

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fetchNearByLocations() {
        Log.e(TAG, "fetchNearByLocations ");
        String startingURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        String endingURL = "&radius=500&key=";
//        String API_KEY = "AIzaSyD-CncdJE9r8Jurb0Q6P-j_L-SunV9Dz9E";
        String latLongs = lat + "," + lng;
        String finalURL = startingURL + latLongs + endingURL + ApiClass.GooglePlaceKey;
        Log.e(TAG, "finalURL : " + finalURL);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<NearByLocationResponse> call = mApiInterface.fetchNearByLocations(finalURL);
        call.enqueue(new Callback<NearByLocationResponse>() {
            @Override
            public void onResponse(Call<NearByLocationResponse> call, Response<NearByLocationResponse> response) {
                Log.e(TAG, "hitOTPApi respnse received");
                if (response.isSuccessful()) {
                    NearByLocationResponse nearByLocationResponse = response.body();
                    Log.e(TAG, "hitOTPApi respnse Successful");
                    if (nearByLocationResponse != null && isAdded()) {
                        Log.e(TAG, "hitOTPApi otpResponseTest!=null");

                        if (nearByLocationResponse.getResults().size() > 0) {
                            nearByLocations = nearByLocationResponse.getResults();

                            setNearByLocationAdapter(nearByLocations);
                        }

                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<NearByLocationResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });


    }

    private void fetchSearchedLocations(String locationName) {
        String startingURL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
        String endingURL = "&key=";
        String API_KEY = "AIzaSyD-CncdJE9r8Jurb0Q6P-j_L-SunV9Dz9E";
        String finalURL = startingURL + locationName + endingURL + ApiClass.GooglePlaceKey;
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<SearchLocationResponse> call = mApiInterface.fetchSearchedLocations(finalURL);
        call.enqueue(new Callback<SearchLocationResponse>() {
            @Override
            public void onResponse(Call<SearchLocationResponse> call, Response<SearchLocationResponse> response) {
                if (response.isSuccessful()) {
                    SearchLocationResponse searchLocationResponse = response.body();
                    if (searchLocationResponse != null && isAdded()) {
                        if (searchLocationResponse.getPredictions().size() > 0) {
                            searchLocationPredictions = searchLocationResponse.getPredictions();
                            setSearchedLocationAdapter(searchLocationResponse.getPredictions());
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<SearchLocationResponse> call, Throwable t) {
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });


    }

    private void setNearByLocationAdapter(List<NearByLocationResult> nearByLocations) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvNearByLocation.setLayoutManager(linearLayoutManager);
        nearByLocationAdapter = new NearByLocationAdapter(getActivity(), nearByLocations);
        nearByLocationAdapter.setOnImageClickListener(LocationFragment.this);
        rvNearByLocation.setAdapter(nearByLocationAdapter);
    }

    private void setSearchedLocationAdapter(List<SearchLocationPrediction> nearByLocations) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvNearByLocation.setLayoutManager(linearLayoutManager);
        searchLocationAdapter = new SearchLocationAdapter(getActivity(), nearByLocations);
        searchLocationAdapter.setOnImageClickListener(LocationFragment.this);
        rvNearByLocation.setAdapter(searchLocationAdapter);
    }

    @Override
    public void onChoosingLocationListener(int pos) {
        hideSoftKeyboard(mActivity);

        if (Utils.stringIsNotEmpty(nearByLocations.get(pos).getName())) {
            setLocationView(nearByLocations.get(pos).getName());
        }
    }

    @Override
    public void onChoosingSearchLocationListener(int pos) {
        hideSoftKeyboard(mActivity);
        if (Utils.stringIsNotEmpty(searchLocationPredictions.get(pos).getStructuredFormatting().getMainText())) {
            setLocationView(searchLocationPredictions.get(pos).getStructuredFormatting().getMainText());
        }
    }

    private void setLocationView(String locationName) {
        rvNearByLocation.setVisibility(View.GONE);
        imageRecyclerView.setVisibility(View.VISIBLE);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new LocationAdapter(mActivity, locationName);
        adapter.setOnImageClickListener(this);
        imageRecyclerView.setAdapter(adapter);
    }

    public interface OnStrickerClickListener {
        void onStickerClick(Bitmap bitmap, String type);
    }
}

