package com.speed.user.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.activities.login.IntroActivity;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class SplashScreen extends AppCompatActivity {

    String TAG = "SplashActivity";
    ConnectionHelper helper;
    Boolean isInternet;
    String device_token, device_UDID;
    Handler handleCheckStatus;
    AlertDialog alert;
    FirebaseAnalytics firebaseAnalytics;

    String keys = "ojBHda1ppwq9Fdc8lTJ507dNQkfBWAG1" + ":" + "Ixy9QVRAnoDrmT1I";

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            String packageName = context.getApplicationContext().getPackageName();
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setContentView(R.layout.activity_splash);
        printKeyHash(this);
        GetToken();
        firebaseAnalytics = FirebaseAnalytics.getInstance(SplashScreen.this);
        //Crashlytics.getInstance();

        helper = new ConnectionHelper(this);
        isInternet = helper.isConnectingToInternet();
        String base64Key = Base64.encodeToString(keys.getBytes(), Base64.NO_WRAP);
        handleCheckStatus = new Handler();
        //check status every 3 sec
        SharedHelper.putKey(SplashScreen.this, "base64Key", base64Key);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        showPermissionDialog();
    }

    public void getProfile() {
        if (isInternet) {
            Log.v("GetPostAPI", "" +
                    URLHelper.UserProfile +
                    "?device_type=android&device_id=" +
                    device_UDID + "&device_token=" + device_token);
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.GET,
                            URLHelper.UserProfile + "?device_type=android&device_id="
                                    + device_UDID + "&device_token=" + device_token, object,
                            response -> {
                                Log.v("GetProfile", response.toString());
                                SharedHelper.putKey(this, "id", response.optString("id"));
                                SharedHelper.putKey(this, "first_name", response.optString("first_name"));
                                SharedHelper.putKey(this, "last_name", response.optString("last_name"));
                                SharedHelper.putKey(this, "email", response.optString("email"));

                                if (response.optString("picture").startsWith("http"))
                                    SharedHelper.putKey(this, "picture", response.optString("picture"));
                                else
                                    SharedHelper.putKey(this, "picture", URLHelper.BASE + "storage/app/public/" + response.optString("picture"));
                                SharedHelper.putKey(this, "gender", response.optString("gender"));
                                SharedHelper.putKey(this, "mobile", response.optString("mobile"));
                                SharedHelper.putKey(this, "wallet_balance", response.optString("wallet_balance"));
                                SharedHelper.putKey(this, "payment_mode", response.optString("payment_mode"));
                                if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                                    SharedHelper.putKey(this, "currency", response.optString("currency"));
                                else
                                    SharedHelper.putKey(this, "currency", "$");
                                SharedHelper.putKey(this, "sos", response.optString("sos"));
                                Log.e(TAG, "onResponse: Sos Call" + response.optString("sos"));
                                SharedHelper.putKey(this, "loggedIn", getString(R.string.True));

                                SharedHelper.putKey(this, "card", response.optString("card"));
                                SharedHelper.putKey(this, "paypal", response.optString("paypal"));
                                SharedHelper.putKey(this, "cash", response.optString("cash"));
                                GoToMainActivity();
                            }, error -> {
                        Log.v("splaherror", error.toString() + "");
                        GoToBeginActivity();
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Requested-With", "XMLHttpRequest");
                            headers.put("Authorization", "" + SharedHelper.getKey(SplashScreen.this, "token_type")
                                    + " " +
                                    SharedHelper.getKey(SplashScreen.this, "access_token"));
                            return headers;
                        }
                    };

            ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            //mProgressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
            builder.setMessage(getString(R.string.check_your_internet)).setCancelable(false);
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            builder.setPositiveButton(getString(R.string.setting), (dialog, which) -> {

                Intent NetworkAction = new Intent(Settings.ACTION_SETTINGS);
                startActivity(NetworkAction);

            });
            builder.show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(this, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

    @SuppressLint("HardwareIds")
    public void GetToken() {
        try {
            if (!SharedHelper.getKey(this, "device_token").equals("") &&
                    SharedHelper.getKey(this, "device_token") != null) {
                device_token = SharedHelper.getKey(this, "device_token");
                Log.i(TAG, "GCM Registration Token: " + device_token);
            } else {
                FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(new OnCompleteListener<InstallationTokenResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<InstallationTokenResult> task) {
                        String newToken = task.getResult().getToken();
                        Log.e("newToken", newToken);
                        SharedHelper.putKey(getApplicationContext(), "device_token", "" + newToken);
                        device_token = newToken;
                    }
                });
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }


        try {
            device_UDID = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.i(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

    void showPermissionDialog() {
        Dexter.withActivity(SplashScreen.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (SharedHelper.getKey(getApplicationContext(), "selectedlanguage") != null &&
                                    !SharedHelper.getKey(getApplicationContext(), "selectedlanguage").isEmpty()) {
                                setLocale(SharedHelper.getKey(getApplicationContext(), "selectedlanguage"));
                                handleCheckStatus.postDelayed(() -> {
                                    if (SharedHelper.getKey(SplashScreen.this, "loggedIn").equalsIgnoreCase(getString(R.string.True))) {
                                        getProfile();
                                    } else {
                                        GoToBeginActivity();
                                    }
                                }, 3000);
                            } else {
                                startActivity(new Intent(SplashScreen.this, IntroActivity.class));
                                finish();
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
        builder.setTitle(getString(R.string.need_permission));
        builder.setMessage(getString(R.string.need_location_perm));
        builder.setPositiveButton(getString(R.string.go_to_setting), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    boolean localeHasChanged = false;
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        localeHasChanged = true;
    }

}
