package com.speed.user.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.consts.AutoCompleteAdapter;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.models.PlacePredictions;
import com.speed.user.utills.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class FavouritePlaceSearch extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    ImageView backArrow, imgSourceClose;
    String type;
    TextView addressType, txtaddressSource;
    double latitude;
    double longitude;
    Boolean isInternet;
    ConnectionHelper helper;
    PlacesClient placesClient;
    String TAG = "FavouritePlaceSearch";
    private ListView mAutoCompleteList;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private String GETPLACESHIT = "places_hit";
    private PlacePredictions predictions = new PlacePredictions();
    private Handler handler;
    private GoogleApiClient mGoogleApiClient;
    private PlacePredictions placePredictions = new PlacePredictions();
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setContentView(R.layout.activity_favourite_place_search);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_map_api));

// Create a new Places client instance
        placesClient = Places.createClient(this);
        backArrow = findViewById(R.id.backArrow);
        addressType = findViewById(R.id.addressType);
        txtaddressSource = findViewById(R.id.txtaddressSource);
        imgSourceClose = findViewById(R.id.imgSourceClose);

        mAutoCompleteList = findViewById(R.id.searchResultLV);
        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        backArrow.setOnClickListener(this);

        txtaddressSource.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                imgSourceClose.setVisibility(View.VISIBLE);
            } else {
                imgSourceClose.setVisibility(View.GONE);
            }
        });
        imgSourceClose.setOnClickListener(v -> {
            txtaddressSource.setText("");
            mAutoCompleteList.setVisibility(View.GONE);
            imgSourceClose.setVisibility(View.GONE);
            txtaddressSource.requestFocus();
        });


        txtaddressSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgSourceClose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // optimised way is to start searching for laction after user has typed minimum 3 chars

                if (txtaddressSource.getText().length() > 0) {
                    imgSourceClose.setVisibility(View.VISIBLE);

                    if (mAutoCompleteAdapter == null)
                        mAutoCompleteList.setVisibility(View.VISIBLE);
                    Runnable run = () -> {
                        // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                        ClassLuxApp.getInstance().cancelRequestInQueue(GETPLACESHIT);

                        JSONObject object = new JSONObject();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(txtaddressSource.getText().toString()),
                                object, response -> {
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Gson gson = new Gson();
                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                                    if (mAutoCompleteAdapter == null) {
                                        mAutoCompleteAdapter = new AutoCompleteAdapter(FavouritePlaceSearch.this, predictions.getPlaces(), FavouritePlaceSearch.this);
                                        mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
                                    } else {
                                        mAutoCompleteList.setVisibility(View.VISIBLE);
                                        mAutoCompleteAdapter.clear();
                                        mAutoCompleteAdapter.addAll(predictions.getPlaces());
                                        mAutoCompleteAdapter.notifyDataSetChanged();
                                        mAutoCompleteList.invalidate();
                                    }
                                }, error -> Log.v("PayNowRequestResponse", error.toString()));
                        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);

                    };

                    // only canceling the network calls will not help, you need to remove all callbacks as well
                    // otherwise the pending callbacks and messages will again invoke the handler and will send the request
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 1000);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgSourceClose.setVisibility(View.VISIBLE);
            }

        });

        mAutoCompleteList.setOnItemClickListener((parent, view, position, id) -> setGoogleAddress(position));
        getDataType();
    }

    private void setGoogleAddress(int position) {
//        buildGoogleApiClient();
//
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }
//        if (mGoogleApiClient != null) {

        // Define a Place ID.
        String placeId = predictions.getPlaces().get(position).getPlaceID();

// Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME
                , Place.Field.ADDRESS, Place.Field.LAT_LNG);

// Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i(TAG, "Place found: " + place.getName());

            LatLng queriedLocation = place.getLatLng();
            Log.v("Latitude is", "" + queriedLocation.latitude);
            Log.v("Longitude is", "" + queriedLocation.longitude);

            placePredictions.strSourceAddress = txtaddressSource.getText().toString() + "";
            placePredictions.strSourceLatLng = place.getLatLng().toString() + "";
            placePredictions.strSourceLatitude = place.getLatLng().latitude + "";
            placePredictions.strSourceLongitude = place.getLatLng().longitude + "";
            txtaddressSource.setText(placePredictions.strSourceAddress);
            saveLocationAddress(type, place.getLatLng().latitude + "", place.getLatLng().longitude + "",
                    place.getAddress());
            mAutoCompleteList.setVisibility(View.GONE);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });


    }

    public String getPlaceAutoCompleteUrl(String input) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/autocomplete/json");
        urlString.append("?input=");
        try {
            urlString.append(URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&location=");
        urlString.append(latitude + "," + longitude); // append lat long of current location to show nearby results.
        urlString.append("&radius=500&language=en");
        urlString.append("&key=" + getResources().getString(R.string.google_map_api));

        Log.d("FINAL URL:::   ", urlString.toString());
        return urlString.toString();
    }

    private void getDataType() {
        type = getIntent().getStringExtra("type");
        addressType.setText(type + " " + "Address");
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backArrow) {
            Utilities.hideKeyboard(FavouritePlaceSearch.this);
            onBackPressed();
            finish();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void saveLocationAddress(String locationType, String lat, String lng, String address) {
        String addressLat = lat;
        String addressLng = lng;
        String addressLocation = address;
        String type = locationType;
        String id = SharedHelper.getKey(getApplicationContext(), "id");

        if (isInternet) {
            Dialog customDialog = new Dialog(FavouritePlaceSearch.this);
            customDialog.setContentView(R.layout.custom_dialog);
            customDialog.show();


            JSONObject object = new JSONObject();
            try {
                object.put("location_type", type);
                object.put("user_id", id);
                object.put("address", addressLocation);
                object.put("longitude", addressLat);
                object.put("latitude", addressLng);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                    URLHelper.SAVE_LOCATION,
                    object,
                    response -> {
                        if ((customDialog != null) && customDialog.isShowing())
                            customDialog.dismiss();
                        SharedHelper.putKey(FavouritePlaceSearch.this, type + "_address", addressLocation);
                        callSuccess();
                    }, error -> {
                        if ((customDialog != null) && customDialog.isShowing())
                            customDialog.dismiss();
                        displayMessage(getString(R.string.something_went_wrong));
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(getApplicationContext(), "token_type") + " "
                            + SharedHelper.getKey(getApplicationContext(), "access_token"));
                    return headers;
                }
            };
            ClassLuxApp.getInstance().addToRequestQueue(objectRequest);
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utilities.hideKeyboard(FavouritePlaceSearch.this);
    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

    private void callSuccess() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "result");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
