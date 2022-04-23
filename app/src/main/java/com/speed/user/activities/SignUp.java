package com.speed.user.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.utills.Utilities;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    public static int APP_REQUEST_CODE = 99;
    Button txtSignIn;
    EditText etName, etEmail, etPassword;
    Button btnSignUp;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Utilities utils = new Utilities();
    String device_token, device_UDID;
    String result;
    JSONObject json;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setContentView(R.layout.activity_sign_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        txtSignIn = findViewById(R.id.txtSignIn);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        txtSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        getToken();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtSignIn) {
            startActivity(new Intent(SignUp.this, Login.class));
            finish();
        }
        if (v.getId() == R.id.btnSignUp) {

            Pattern ps = Pattern.compile(".*[0-9].*");
            Matcher firstName = ps.matcher(etName.getText().toString());


            if (etName.getText().toString().equals("") ||
                    etName.getText().toString().equalsIgnoreCase(getString(R.string.first_name))) {
                displayMessage(getString(R.string.first_name_empty));
            } else if (firstName.matches()) {
                displayMessage(getString(R.string.first_name_no_number));
            } else if (etEmail.getText().toString().equals("") ||
                    etEmail.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                displayMessage(getString(R.string.email_validation));
            } else if (!Utilities.isValidEmail(etEmail.getText().toString())) {
                displayMessage(getString(R.string.not_valid_email));
            } else if (etPassword.getText().toString().equals("") ||
                    etPassword.getText().toString().equalsIgnoreCase(getString(R.string.password_txt))) {
                displayMessage(getString(R.string.password_validation));
            } else if (etPassword.length() < 6) {
                displayMessage(getString(R.string.password_size));
            } else {
                if (isInternet) {
                    openphonelogin();
                } else {
                    displayMessage(getString(R.string.something_went_wrong_net));
                }
            }
        }
    }

    private void openphonelogin() {

        dialog = new Dialog(SignUp.this, R.style.AppTheme_NoActionBar);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.mobileverification);
        dialog.setCancelable(true);
        dialog.show();
        CountryCodePicker ccp = dialog.findViewById(R.id.ccp);
        ImageButton nextIcon = dialog.findViewById(R.id.nextIcon);
        ImageView imgBack = dialog.findViewById(R.id.imgBack);
        EditText mobile_no = dialog.findViewById(R.id.mobile_no);
        final String countryCode = ccp.getDefaultCountryCode();
        final String countryIso = ccp.getSelectedCountryNameCode();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = ccp.getSelectedCountryCodeWithPlus() + mobile_no.getText().toString();
                SharedHelper.putKey(getApplicationContext(), "mobile_number", phone);
                Log.v("Phonecode", phone + " ");
                Intent intent = new Intent(SignUp.this, OtpVerification.class);
                intent.putExtra("phonenumber", phone);
                startActivityForResult(intent, APP_REQUEST_CODE);
                dialog.dismiss();
            }
        });

    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == APP_REQUEST_CODE) {
                dialog.dismiss();
                registerAPI();
            }
        }
    }


    private void registerAPI() {
        customDialog = new CustomDialog(SignUp.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {

            object.put("device_type", "android");
            object.put("device_id", device_UDID);
            object.put("device_token", "" + device_token);
            object.put("login_by", "manual");
            object.put("first_name", etName.getText().toString());
            object.put("last_name", etName.getText().toString());
            object.put("email", etEmail.getText().toString());
            object.put("password", etPassword.getText().toString());
            object.put("mobile", SharedHelper.getKey(getApplicationContext(), "mobile_number"));
            object.put("picture", "");
            object.put("social_unique_id", "");

            Utilities.print("InputToRegisterAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.register,
                        object,
                        response -> {
                            dialog.dismiss();
                            try {
                                displayMessage(response.getString("msg"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            Utilities.print("SignInResponse", response.toString());
                            SharedHelper.putKey(getApplicationContext(), "email", etEmail.getText().toString());
                            SharedHelper.putKey(getApplicationContext(), "password", etPassword.getText().toString());
                            signIn();
                        },
                        error -> {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            displayMessage(getString(R.string.something_went_wrong));
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

    public void signIn() {
        if (isInternet) {
            customDialog = new CustomDialog(SignUp.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            try {
                object.put("grant_type", "password");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("username", SharedHelper.getKey(getApplicationContext(), "email"));
                object.put("password", SharedHelper.getKey(getApplicationContext(), "password"));
                object.put("scope", "");
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                Utilities.print("InputToLoginAPI", "" + object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.POST,
                            URLHelper.login,
                            object,
                            response -> {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                Utilities.print("SignUpResponse", response.toString());
                                SharedHelper.putKey(getApplicationContext(), "access_token",
                                        response.optString("access_token"));
                                SharedHelper.putKey(getApplicationContext(), "refresh_token",
                                        response.optString("refresh_token"));
                                SharedHelper.putKey(getApplicationContext(), "token_type",
                                        response.optString("token_type"));
                                getProfile();
                            },
                            error -> {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                displayMessage(getString(R.string.something_went_wrong));
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Requested-With", "XMLHttpRequest");
                            return headers;
                        }
                    };

            ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void getProfile() {
        if (isInternet) {
            customDialog = new CustomDialog(SignUp.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.GET,
                            URLHelper.UserProfile,
                            object,
                            response -> {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    //customDialog.dismiss();
                                    Utilities.print("GetProfile", response.toString());
                                SharedHelper.putKey(getApplicationContext(), "id",
                                        response.optString("id"));
                                SharedHelper.putKey(getApplicationContext(), "first_name",
                                        response.optString("first_name"));
                                SharedHelper.putKey(getApplicationContext(), "last_name",
                                        response.optString("last_name"));
                                SharedHelper.putKey(getApplicationContext(), "email",
                                        response.optString("email"));
                                SharedHelper.putKey(getApplicationContext(), "picture",
                                        URLHelper.BASE + "storage/app/public/" +
                                                response.optString("picture"));
                                SharedHelper.putKey(getApplicationContext(), "gender",
                                        response.optString("gender"));
                                SharedHelper.putKey(getApplicationContext(), "mobile",
                                        response.optString("mobile"));
                                SharedHelper.putKey(getApplicationContext(), "wallet_balance",
                                        response.optString("wallet_balance"));
                                SharedHelper.putKey(getApplicationContext(), "payment_mode",
                                        response.optString("payment_mode"));
                                if (!response.optString("currency")
                                        .equalsIgnoreCase("") &&
                                        response.optString("currency") != null)
                                    SharedHelper.putKey(getApplicationContext(), "currency",
                                            response.optString("currency"));
                                else
                                    SharedHelper.putKey(getApplicationContext(), "currency", "AED");
                                SharedHelper.putKey(getApplicationContext(), "sos",
                                        response.optString("sos"));
                                SharedHelper.putKey(getApplicationContext(), "loggedIn",
                                        getString(R.string.True));
                                GoToMainActivity();
                            },
                            error -> {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                displayMessage(getString(R.string.something_went_wrong));
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Requested-With", "XMLHttpRequest");
                            headers.put("Authorization", "" + SharedHelper.getKey(getApplicationContext(), "token_type")
                                    + " " + SharedHelper.getKey(getApplicationContext(), "access_token"));
                            return headers;
                        }
                    };

            ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }

    @SuppressLint("HardwareIds")
    public void getToken() {
        try {
            if (!SharedHelper.getKey(SignUp.this, "device_token").equals("") &&
                    SharedHelper.getKey(SignUp.this, "device_token") != null) {
                device_token = SharedHelper.getKey(SignUp.this, "device_token");
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
        }

        try {
            device_UDID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GoToMainActivity() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

}
