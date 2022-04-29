package com.speed.user.activities;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.koushikdutta.ion.Ion;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.utills.Utilities;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;


public class Login extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQ_SIGN_IN_REQUIRED = 100;
    private static final int RC_SIGN_IN = 100;
    public static int APP_REQUEST_CODE = 99;
    TextView txtForget;
    Button btnLogin, txtSignUp;
    EditText etEmail, etPassword;
    //    Button btnFb,btnGoogle;
    ImageView btnFb, btnGoogle;
    CustomDialog customDialog;
    Button registerLayout;
    Boolean isInternet;
    ConnectionHelper helper;
    String device_token, device_UDID;
    String TAG = "FragmentLogin";
    Utilities utils = new Utilities();
    /*----------Facebook Login---------*/
    CallbackManager callbackManager;
    ImageView backArrow;
    AccessTokenTracker accessTokenTracker;
    String UserName, UserEmail, result, FBUserID, FBImageURLString;
    JSONObject json;

    /*----------Google Login---------------*/
    GoogleSignInClient mGoogleApiClient;

    JsonObject socialJson;
    String socialUrl, loginType;
    String mobile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FacebookSdk.sdkInitialize(getApplicationContext());

        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        txtSignUp = findViewById(R.id.txtSignUp);
        txtForget = findViewById(R.id.txtForget);
        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        registerLayout = findViewById(R.id.txtSignUp);
        registerLayout.setOnClickListener(this);
        btnFb = findViewById(R.id.btnFb);
        etPassword = findViewById(R.id.etPassword);
        btnGoogle = findViewById(R.id.btnGoogle);
        txtSignUp.setOnClickListener(this);
        txtForget.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnFb.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();
        getToken();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = GoogleSignIn.getClient(this, gso);
        try {

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String texto = bundle.getString("loginTypeSignUP");
                if (texto != null) {
                    if (texto.contains("fb")) {
                        facebookLogin();
                    }
                    if (texto.contains("google")) {
                        googleLogIn();
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtSignUp) {
            startActivity(new Intent(Login.this, SignUp.class));
        }
        if (v.getId() == R.id.txtForget) {
            startActivity(new Intent(Login.this, ForgotPassword.class));
        }
        if (v.getId() == R.id.txtSignUp) {
            startActivity(new Intent(Login.this, SignUp.class));
        }
        if (v.getId() == R.id.btnLogin) {
            Pattern ps = Pattern.compile(".*[0-9].*");
            if (etEmail.getText().toString().equals("") ||
                    etEmail.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                displayMessage(getString(R.string.email_validation));
            } else if (!Utilities.isValidEmail(etEmail.getText().toString().trim())) {
                displayMessage(getString(R.string.not_valid_email));
            } else if (etPassword.getText().toString().equals("") ||
                    etPassword.getText().toString()
                            .equalsIgnoreCase(getString(R.string.password_txt))) {
                displayMessage(getString(R.string.password_validation));
            } else if (etPassword.length() < 6) {
                displayMessage(getString(R.string.password_size));
            } else {
                SharedHelper.putKey(getApplicationContext(), "email", etEmail.getText().toString().trim());
                SharedHelper.putKey(getApplicationContext(), "password", etPassword.getText().toString());
                signIn();
            }
        }
        if (v.getId() == R.id.btnGoogle) {
            googleLogIn();
        }
        if (v.getId() == R.id.btnFb) {
            facebookLogin();
        }
    }

    private void googleLogIn() {
        Intent signInIntent = mGoogleApiClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signIn() {
        if (isInternet) {
            customDialog = new CustomDialog(Login.this);
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
                object.put("logged_in", "1");
                Utilities.print("InputToLoginAPI", "" + object);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.POST,
                            URLHelper.login,
                            object,
                            response -> {
                                if ((customDialog != null) && customDialog.isShowing())
                                    customDialog.dismiss();
                                Utilities.print("SignUpResponse", response.toString());
                                SharedHelper.putKey(getApplicationContext(),
                                        "access_token", response.optString("access_token"));
                                SharedHelper.putKey(getApplicationContext(),
                                        "refresh_token", response.optString("refresh_token"));
                                SharedHelper.putKey(getApplicationContext(),
                                        "token_type", response.optString("token_type"));
                                getProfile();
                            },
                            error -> {
                                if ((customDialog != null) && customDialog.isShowing())
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
            customDialog = new CustomDialog(Login.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.GET,
                            URLHelper.UserProfile + "?device_type=android&device_id="
                                    + device_UDID + "&device_token=" + device_token,
                            object,
                            response -> {
                                if ((customDialog != null) && customDialog.isShowing())
                                    customDialog.dismiss();
                                Utilities.print("GetProfile", response.toString());
                                SharedHelper.putKey(getApplicationContext(), "id",
                                        response.optString("id"));
                                SharedHelper.putKey(getApplicationContext(), "first_name",
                                        response.optString("first_name"));
                                SharedHelper.putKey(getApplicationContext(), "last_name",
                                        response.optString("last_name"));
                                SharedHelper.putKey(getApplicationContext(), "email",
                                        response.optString("email"));
                                if (response.optString("picture").startsWith("http"))
                                    SharedHelper.putKey(getApplicationContext(), "picture",
                                            response.optString("picture"));
                                else
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
                                if (!response.optString("currency").equalsIgnoreCase("") &&
                                        response.optString("currency") != null)
                                    SharedHelper.putKey(getApplicationContext(), "currency", response.optString("currency"));
                                else
                                    SharedHelper.putKey(getApplicationContext(), "currency", "AED");
                                SharedHelper.putKey(getApplicationContext(), "sos", response.optString("sos"));
                                SharedHelper.putKey(getApplicationContext(), "loggedIn", getString(R.string.True));
                                GoToMainActivity();

                            },
                            error -> {
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
                            Utilities.print("authoization", "" + SharedHelper.getKey(getApplicationContext(), "token_type") + " "
                                    + SharedHelper.getKey(getApplicationContext(), "access_token"));
                            return headers;
                        }
                    };

            ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
            Log.i(TAG, "getProfile: " + jsonObjectRequest.getUrl());

        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void getToken() {
        try {
            if (!SharedHelper.getKey(Login.this, "device_token").equals("") &&
                    SharedHelper.getKey(Login.this, "device_token") != null) {
                device_token = SharedHelper.getKey(Login.this, "device_token");
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
            device_UDID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Utilities.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Utilities.print(TAG, "Failed to complete device UDID");
        }
    }


    private void facebookLogin() {
        if (isInternet) {
            LoginManager.getInstance().logInWithReadPermissions(Login.this,
                    Arrays.asList("public_profile", "email"));


            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {


                        public void onSuccess(final LoginResult loginResult) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback() {

                                            @Override
                                            public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                                                try {
                                                    Log.e(TAG, "id" + user.optString("id"));
                                                    Log.e(TAG, "name" + user.optString("first_name"));

                                                    String profileUrl = "https://graph.facebook.com/v2.8/" + user.optString("id") + "/picture?width=1920";


                                                    final JsonObject json = new JsonObject();
                                                    json.addProperty("device_type", "android");
                                                    json.addProperty("device_token", device_token);
                                                    json.addProperty("accessToken", loginResult.getAccessToken().getToken());
                                                    json.addProperty("device_id", device_UDID);
                                                    json.addProperty("login_by", "facebook");
                                                    json.addProperty("first_name", user.optString("first_name"));
                                                    json.addProperty("last_name", user.optString("last_name"));
                                                    json.addProperty("id", user.optString("id"));
                                                    json.addProperty("email", user.optString("email"));
                                                    json.addProperty("avatar", profileUrl);
                                                    json.addProperty("mobile", mobile);

                                                    socialJson = json;
                                                    socialUrl = URLHelper.FACEBOOK_LOGIN;
                                                    loginType = "facebook";
                                                    openphonelogin();
//                                                    login(json, URLHelper.FACEBOOK_LOGIN, "facebook");

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Log.d("facebookExp", e.getMessage());
                                                }
                                            }
                                        });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,first_name,last_name,email");
                                request.setParameters(parameters);
                                request.executeAsync();
                                Log.e("getAccessToken", "" + loginResult.getAccessToken().getToken());
                                SharedHelper.putKey(Login.this, "accessToken", loginResult.getAccessToken().getToken());
//                                        login(loginResult.getAccessToken().getToken(), URLHelper.FACEBOOK_LOGIN, "facebook");
                            }

                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.e("exceptionfacebook", exception.toString());
                            // App code
                        }
                    });
        } else {
            //mProgressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setMessage(R.string.check_your_internet).setCancelable(false);
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getString(R.string.setting), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent NetworkAction = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(NetworkAction);

                }
            });
            builder.show();
        }
    }

    public void login(final JsonObject json, final String URL, final String Loginby) {
        customDialog = new CustomDialog(Login.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        Log.e("url", URL + "");
        Log.e(TAG, "login: Facebook" + json);
        Ion.with(Login.this)
                .load(URL)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback((e, result) -> {
                    Log.e("result_data", result + "");
                    // do stuff with the result or error
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    if (e != null) {
                        if (e instanceof NetworkErrorException) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (e instanceof TimeoutException) {
                            login(json, URL, Loginby);
                        }
                        return;
                    }
                    if (result != null) {
                        Log.e(Loginby + "_Response", result.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            String status = jsonObject.optString("status");
//                                if (status.equalsIgnoreCase("true")) {
                            SharedHelper.putKey(Login.this, "token_type", jsonObject.optString("token_type"));
                            SharedHelper.putKey(Login.this, "access_token", jsonObject.optString("access_token"));
                            if (Loginby.equalsIgnoreCase("facebook"))
                                SharedHelper.putKey(Login.this, "login_by", "facebook");
                            if (Loginby.equalsIgnoreCase("google"))
                                SharedHelper.putKey(Login.this, "login_by", "google");
                            getProfile();
//                                openphonelogin();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                    // onBackPressed();
                });
    }

    private void openphonelogin() {

        Dialog dialog = new Dialog(Login.this, R.style.AppTheme_NoActionBar);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.mobileverification);
        dialog.setCancelable(true);
        dialog.show();
        ImageView imgBack = dialog.findViewById(R.id.imgBack);
        CountryCodePicker ccp = dialog.findViewById(R.id.ccp);
        ImageButton nextIcon = dialog.findViewById(R.id.nextIcon);
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

//                String phone = ccp.getDefaultCountryCode() + mobile_no.getText().toString();
//                PhoneNumber phoneNumber = new PhoneNumber(ccp.getSelectedCountryCode(), mobile_no.getText().toString(), ccp.getSelectedCountryNameCode());
//                phoneLogin(phoneNumber);
                dialog.dismiss();
                String phone = ccp.getSelectedCountryCodeWithPlus() + mobile_no.getText().toString();
                mobile = phone;
                socialJson.addProperty("mobile", mobile);
                Intent intent = new Intent(Login.this, OtpVerification.class);
                intent.putExtra("phonenumber", phone);
                startActivityForResult(intent, APP_REQUEST_CODE);


            }
        });

    }


    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    public void displayMessage(String toastString) {
        Snackbar.make(findViewById(R.id.etEmail), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "Result: " + requestCode);
        if (data != null) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);

            }
            if (resultCode == Activity.RESULT_OK) {

                if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request

                    login(socialJson, socialUrl, loginType);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if ((customDialog != null) && customDialog.isShowing())
                    customDialog.dismiss();
            }
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            new RetrieveTokenTask().execute(account.getEmail());
        } catch (ApiException e) {
            Log.v(TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String accessToken) {
            super.onPostExecute(accessToken);
            Log.e("Token", accessToken);
            if ((customDialog != null) && customDialog.isShowing())
                customDialog.dismiss();
            final JsonObject json = new JsonObject();
            json.addProperty("device_type", "android");
            json.addProperty("device_token", device_token);
            json.addProperty("accessToken", accessToken);
            json.addProperty("device_id", device_UDID);
            json.addProperty("login_by", "google");
            json.addProperty("mobile", mobile);


            socialJson = json;
            socialUrl = URLHelper.GOOGLE_LOGIN;
            loginType = "google";
            openphonelogin();

        }
    }
}
