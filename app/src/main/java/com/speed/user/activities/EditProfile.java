package com.speed.user.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.helper.AppHelper;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.helper.VolleyMultipartRequest;
import com.speed.user.utills.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private static final int SELECT_PHOTO = 100;
    public static int deviceHeight;
    public static int deviceWidth;
    private static String TAG = "EditProfile";
    public Context context = EditProfile.this;
    public Activity activity = EditProfile.this;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;

    ImageView backArrow;
    TextView email, first_name, mobile_no;
    ImageView profile_Image;
    Boolean isImageChanged = false;
    Uri uri;
    Utilities utils = new Utilities();
    TextView txtHeaderName, txtHeaderMob;
    RecyclerView locationRecycler;
    String device_token, device_UDID;

    TextView tvLocationHome, tvLocationHomeAddress, tvLocationWork, tvLocationWorkAddress;
    LinearLayout layoutName, layoutEmail, layoutMobile;
    ImageView ivEdit, ivEditWork;

    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        Log.e(TAG, "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight);
        Log.e(TAG, "getBitmapFromUri: width" + deviceWidth);
        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
            Log.e(TAG, "getBitmapFromUri: Width" + image.getWidth());
            Log.e(TAG, "getBitmapFromUri: Height" + image.getHeight());
            int inWidth = image.getWidth();
            int inHeight = image.getHeight();
            int outWidth;
            int outHeight;
            if (inWidth > inHeight) {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }
            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        } else {
            Toast.makeText(context, context.getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewByIdandInitialization();
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            backArrow.setImageDrawable(getDrawable(R.drawable.ic_forward));

        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }


        GetToken();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;

        backArrow.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        tvLocationHome = findViewById(R.id.tvLocationHome);
        tvLocationHomeAddress = findViewById(R.id.tvLocationHomeAddress);
        tvLocationWork = findViewById(R.id.tvLocationWork);
        tvLocationWorkAddress = findViewById(R.id.tvLocationWorkAddress);
        layoutName = findViewById(R.id.layoutName);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutMobile = findViewById(R.id.layoutMobile);
        ivEdit = findViewById(R.id.ivEdit);
        ivEditWork = findViewById(R.id.ivEditWork);
        mobile_no.setOnClickListener(this);

        layoutEmail.setOnClickListener(this);
        layoutName.setOnClickListener(this);
        ivEditWork.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        first_name.setOnClickListener(this);
        profile_Image.setOnClickListener(view -> {
            if (checkStoragePermission())
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            else
                goToImageIntent();
        });

        getProfile();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100)
            for (int grantResult : grantResults)
                if (grantResult == PackageManager.PERMISSION_GRANTED)
                    goToImageIntent();
    }

    public void goToImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    public void updateProfile() {
        if (isImageChanged) {
            updateProfileWithImage();
        } else {
            updateProfileWithoutImage();
        }
    }

    private void updateProfileWithImage() {
        isImageChanged = false;
        ProgressDialog progressDialog = new ProgressDialog(EditProfile.this);
        progressDialog.setTitle("Uploading");
        progressDialog.setCancelable(false);
        progressDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.UseProfileUpdate, response -> {
            progressDialog.dismiss();
            String res = new String(response.data);
            try {
                JSONObject jsonObject = new JSONObject(res);
                SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                    SharedHelper.putKey(context, "picture", "");
                } else {
                    if (jsonObject.optString("picture").startsWith("http"))
                        SharedHelper.putKey(context, "picture", jsonObject.optString("picture"));
                    else
                        SharedHelper.putKey(context, "picture", URLHelper.BASE + "storage/app/public/" + jsonObject.optString("picture"));
                }

                SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));

                SharedHelper.putKey(context, "card", jsonObject.optString("card"));
                SharedHelper.putKey(context, "paypal", jsonObject.optString("paypal"));
                SharedHelper.putKey(context, "cash", jsonObject.optString("cash"));
//                    GoToMainActivity();
                Toast.makeText(EditProfile.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                //displayMessage(getString(R.string.update_success));

            } catch (JSONException e) {
                e.printStackTrace();
                displayMessage(getString(R.string.something_went_wrong));
            }


        }, error -> {
            progressDialog.dismiss();
            displayMessage(getString(R.string.something_went_wrong));
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name.getText().toString());
                params.put("last_name", "");
                params.put("email", email.getText().toString());
                params.put("mobile", mobile_no.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                params.put("picture", new VolleyMultipartRequest.DataPart("userImage.jpg", AppHelper.getFileDataFromDrawable(profile_Image.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        ClassLuxApp.getInstance().addToRequestQueue(volleyMultipartRequest);

    }

    private void updateProfileWithoutImage() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new
                VolleyMultipartRequest(Request.Method.POST,
                        URLHelper.UseProfileUpdate,
                        response -> {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();

                            String res = new String(response.data);
                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                SharedHelper.putKey(context, "id", jsonObject.optString("id"));
                                SharedHelper.putKey(context, "first_name", jsonObject.optString("first_name"));
                                SharedHelper.putKey(context, "last_name", jsonObject.optString("last_name"));
                                SharedHelper.putKey(context, "email", jsonObject.optString("email"));
                                if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                                    SharedHelper.putKey(context, "picture", "");
                                } else {
                                    if (jsonObject.optString("picture").startsWith("http"))
                                        SharedHelper.putKey(context, "picture", jsonObject.optString("picture"));
                                    else
                                        SharedHelper.putKey(context, "picture", URLHelper.BASE + "storage/app/public/" + jsonObject.optString("picture"));
                                }

                                SharedHelper.putKey(context, "gender", jsonObject.optString("gender"));
                                SharedHelper.putKey(context, "mobile", jsonObject.optString("mobile"));
                                SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                                SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                                GoToMainActivity();
                                Toast.makeText(EditProfile.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                                //displayMessage(getString(R.string.update_success));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                displayMessage(getString(R.string.something_went_wrong));
                            }


                        }, error -> {
                            if ((customDialog != null) && customDialog.isShowing())
                                customDialog.dismiss();
                    displayMessage(getString(R.string.something_went_wrong));
                        }) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("first_name", first_name.getText().toString());
                        params.put("last_name", "");
                        params.put("email", email.getText().toString());
                        params.put("mobile", mobile_no.getText().toString());
                        params.put("picture", "");

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type")
                                + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };
        ClassLuxApp.getInstance().addToRequestQueue(volleyMultipartRequest);

    }

    public void findViewByIdandInitialization() {
        email = findViewById(R.id.email);
        first_name = findViewById(R.id.first_name);
        mobile_no = findViewById(R.id.mobile_no);

        backArrow = findViewById(R.id.backArrow);
        profile_Image = findViewById(R.id.img_profile);
        txtHeaderName = findViewById(R.id.txtHeaderName);
        txtHeaderMob = findViewById(R.id.txtHeaderMob);

        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("")
                && !SharedHelper.getKey(context, "picture").equalsIgnoreCase(null)
                && SharedHelper.getKey(context, "picture") != null) {
            Picasso.get()
                    .load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(profile_Image);
        } else {
            Picasso.get()
                    .load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(profile_Image);
        }
        email.setText(SharedHelper.getKey(context, "email"));
        first_name.setText(SharedHelper.getKey(context, "first_name"));
        txtHeaderName.setText(SharedHelper.getKey(context, "first_name"));
        if (SharedHelper.getKey(context, "mobile") != null
                && !SharedHelper.getKey(context, "mobile").equals("null")
                && !SharedHelper.getKey(context, "mobile").equals("")) {
            mobile_no.setText(SharedHelper.getKey(context, "mobile"));
            txtHeaderMob.setText(SharedHelper.getKey(context, "mobile"));
            mobile_no.setText(SharedHelper.getKey(context, "mobile"));
        } else {
            txtHeaderMob.setText(SharedHelper.getKey(context, ""));
            mobile_no.setText(SharedHelper.getKey(context, ""));
        }
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void refreshAccessToken(final String tag) {

        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                Utilities.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("UPDATE_PROFILE_WITH_IMAGE")) {
                    updateProfileWithImage();
                } else {
                    updateProfileWithoutImage();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    utils.GoToBeginActivity(EditProfile.this);
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        refreshAccessToken(tag);
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void getProfile() {
        Log.e("GetPostAPI", "" +
                URLHelper.UserProfile +
                "?device_type=android&device_id=" +
                device_UDID + "&device_token=" + device_token);
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.GET,
                        URLHelper.UserProfile + "?device_type=android&device_id=" + device_UDID + "&device_token=" + device_token, object,
                        response -> {
                            Log.e("GetProfile", response.toString());
                            try {
                                JSONArray locarray = response.getJSONArray("location");
                                for (int i = 0; i < locarray.length(); i++) {
                                    JSONObject object1 = locarray.getJSONObject(i);
                                    String locationType = object1.optString("location_type");
                                    if (locationType.equals("Home")) {
                                        String address = object1.optString("address");
                                        String lat = object1.optString("latitude");
                                        String lng = object1.optString("longitude");
                                        tvLocationHome.setText(getString(R.string.home_address));
                                        tvLocationHomeAddress.setText(address);

                                    }
                                    if (locationType.equals("Work")) {
                                        String address = object1.optString("address");
                                        String lat = object1.optString("latitude");
                                        String lng = object1.optString("longitude");
                                        tvLocationWork.setText(getString(R.string.work));
                                        tvLocationWorkAddress.setText(address);

                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                    displayMessage(getString(R.string.something_went_wrong));
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") +
                                " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") &&
                    SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.i(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "COULD NOT GET FCM TOKEN";
                Log.i(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            Log.i(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.layoutName || v.getId() == R.id.first_name) {
            Intent intent = new Intent(EditProfile.this, UpdateProfile.class);
            intent.putExtra("parameter", "first_name");
            intent.putExtra("value", first_name.getText().toString());
            startActivityForResult(intent, 1);
        }
        if (v.getId() == R.id.layoutEmail || v.getId() == R.id.etEmail) {
            Intent intent = new Intent(EditProfile.this, UpdateProfile.class);
            intent.putExtra("parameter", "email");
            intent.putExtra("value", email.getText().toString());
            startActivityForResult(intent, 1);
        }
        if (v.getId() == R.id.mobile_no) {
            Intent intent = new Intent(EditProfile.this, PhoneNoUpdateActivity.class);
            intent.putExtra("parameter", "mobile");
            intent.putExtra("value", mobile_no.getText().toString());
            startActivityForResult(intent, 1);
        }
        if (v.getId() == R.id.ivEditWork) {
            Intent intent = new Intent(EditProfile.this, FavouritePlaceSearch.class);
            intent.putExtra("type", "Work");
            startActivityForResult(intent, 2);
        }
        if (v.getId() == R.id.ivEdit) {
            Intent intent = new Intent(EditProfile.this, FavouritePlaceSearch.class);
            intent.putExtra("type", "Home");
            startActivityForResult(intent, 2);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Bitmap bitmap = null;

            try {
                isImageChanged = true;
                Bitmap resizeImg = getBitmapFromUri(this, uri);
                if (resizeImg != null) {
                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
                            AppHelper.getPath(this, uri));
                    profile_Image.setImageBitmap(reRotateImg);
                    updateProfileWithImage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                if (SharedHelper.getKey(EditProfile.this, "mobile") != null) {
                    mobile_no.setText(SharedHelper.getKey(EditProfile.this, "mobile"));
                    txtHeaderMob.setText(SharedHelper.getKey(EditProfile.this, "mobile"));
                }
                if (SharedHelper.getKey(EditProfile.this, "email") != null) {
                    email.setText(SharedHelper.getKey(EditProfile.this, "email"));
                }
                if (SharedHelper.getKey(EditProfile.this, "first_name") != null) {
                    first_name.setText(SharedHelper.getKey(EditProfile.this, "first_name"));
                    txtHeaderName.setText(SharedHelper.getKey(EditProfile.this, "first_name"));
                }
            }

        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                if (SharedHelper.getKey(EditProfile.this, "Home_address") != "" && SharedHelper.getKey(EditProfile.this, "Home_address") != null) {
                    tvLocationHome.setText(getString(R.string.home));
                    tvLocationHomeAddress.setText(SharedHelper.getKey(EditProfile.this, "Home_address"));

                }
                if (SharedHelper.getKey(EditProfile.this, "Work_address") != "" && SharedHelper.getKey(EditProfile.this, "Work_address") != null) {
                    tvLocationWork.setText(getString(R.string.work));
                    tvLocationWorkAddress.setText(SharedHelper.getKey(EditProfile.this, "Work_address"));
                }

            }

        }
    }

}
