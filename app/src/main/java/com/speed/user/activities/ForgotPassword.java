package com.speed.user.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.utills.Utilities;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    public static int APP_REQUEST_CODE = 99;
    public Context context = ForgotPassword.this;
    Dialog dialog;
    String TAG = "ForgetPassword";
    ImageView backArrow;
    Button nextIcon;
    TextInputLayout newPasswordLayout, confirmPasswordLayout, OtpLay;
    LinearLayout ll_resend;
    EditText newPassowrd, confirmPassword, OTP;
    EditText email;
    EditText mobile_no;
    CustomDialog customDialog;
    String validation = "",
            str_newPassword,
            str_confirmPassword,
            id,
            str_email = "",
            str_otp,
            server_opt,
            getemail,
            getmobile,
            str_number;
    ConnectionHelper helper;
    Boolean isInternet;
    TextView note_txt;
    Boolean fromActivity = false;
    TextView resend;
    Utilities utils = new Utilities();
    EditText number;
    String phoneNumberString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setContentView(R.layout.activity_forgot_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewById();

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


    }

    private void findViewById() {

        mobile_no = findViewById(R.id.mobile_no);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        backArrow = findViewById(R.id.backArrow);
        nextIcon = findViewById(R.id.nextIcon);

        note_txt = findViewById(R.id.note);
        newPassowrd = findViewById(R.id.new_password);
        OTP = findViewById(R.id.otp);
        confirmPassword = findViewById(R.id.confirm_password);
        confirmPasswordLayout = findViewById(R.id.confirm_password_lay);
        OtpLay = findViewById(R.id.otp_lay);
        newPasswordLayout = findViewById(R.id.new_password_lay);
        resend = findViewById(R.id.resend);
        ll_resend = findViewById(R.id.ll_resend);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        str_email = SharedHelper.getKey(ForgotPassword.this, "email");
        email.setText(str_email);
        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        nextIcon.setOnClickListener(this);
        backArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextIcon:

                str_email = email.getText().toString();
                str_number = number.getText().toString();
                if (validation.equalsIgnoreCase("")) {
                    if (email.getText().toString().equals("")) {
                        displayMessage(getString(R.string.email_validation));
                    } else if (!Utilities.isValidEmail(email.getText().toString())) {
                        displayMessage(getString(R.string.not_valid_email));
                    } else {
                        if (isInternet) {
                            forgetPassword();
                        } else {
                            displayMessage(getString(R.string.something_went_wrong_net));
                        }

                    }
                } else {
                    str_newPassword = newPassowrd.getText().toString();
                    str_confirmPassword = confirmPassword.getText().toString();
                    str_otp = OTP.getText().toString();
                    if (str_newPassword.equals("") || str_newPassword.equalsIgnoreCase(getString(R.string.new_password))) {
                        displayMessage(getString(R.string.password_validation));
                    } else if (str_newPassword.length() < 6) {
                        displayMessage(getString(R.string.password_size));
                    } else if (str_confirmPassword.equals("") || str_confirmPassword.equalsIgnoreCase(getString(R.string.confirm_password)) || !str_newPassword.equalsIgnoreCase(str_confirmPassword)) {
                        displayMessage(getString(R.string.confirm_password_validation));
                    } else if (str_confirmPassword.length() < 6) {
                        displayMessage(getString(R.string.password_size));
                    } else {
                        if (isInternet) {
                            resetpassword();
                        } else {
                            displayMessage(getString(R.string.something_went_wrong_net));
                        }
                    }
                }

                break;

            case R.id.backArrow:

                onBackPressed();
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utilities.hideKeyboard(ForgotPassword.this);

    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }


    private void forgetPassword() {
        str_number = number.getText().toString();
        customDialog = new CustomDialog(ForgotPassword.this);
        customDialog.setCancelable(false);
        customDialog.show();

        StringRequest jsonObjectRequest = new
                StringRequest(Request.Method.POST,
                        URLHelper.FORGET_PASSWORD,
                        response -> {
                            customDialog.dismiss();
                            Log.e("ForgetPasswordResponse", response);
                            try {
                                JSONObject obj = new JSONObject(response);

                                JSONObject userObject = obj.getJSONObject("user");
                                if (userObject.getString("mobile") != null) {
                                    id = userObject.getString("id");
                                    getemail = userObject.getString("email");
                                    getmobile = userObject.getString("mobile");
//                                        if (getmobile == str_number) {
                                    Log.e("getmobile", getmobile + "");
                                    openphonelogin();

//                                        } else {
//                                            displayMessage("You have entered different mobile number");
//                                        }
                                } else {
                                    displayMessage("Mobile no is not exist with this email_id");
                                }


                            } catch (JSONException e) {
                                displayMessage("Mobile no is not exist with this email_id");
                                e.printStackTrace();
                            }

                        },
                        error -> {
                            Log.e("volleyerror", error.toString() + "");
                            customDialog.dismiss();
                            displayMessage(getString(R.string.something_went_wrong));
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        return headers;
                    }

                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", str_email);
                        Log.e(TAG, "params: " + params.toString());
                        return params;
                    }
                };

        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void resetpassword() {
        customDialog = new CustomDialog(ForgotPassword.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("password", str_newPassword);
            object.put("password_confirmation", str_confirmPassword);
            Log.e("ResetPassword", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.RESET_PASSWORD,
                        object,
                        response -> {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            Log.v("ResetPasswordResponse", response.toString());
                            try {
                                JSONObject object1 = new JSONObject(response.toString());
                                Toast.makeText(context, object1.optString("message"),
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPassword.this,
                                        Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
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

    private void openphonelogin() {

        dialog = new Dialog(ForgotPassword.this, R.style.AppTheme_NoActionBar);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.mobileverification);
        dialog.setCancelable(false);
        dialog.show();
        CountryCodePicker ccp = dialog.findViewById(R.id.ccp);
        ImageButton nextIcon = dialog.findViewById(R.id.nextIcon);
        EditText mobile_no = dialog.findViewById(R.id.mobile_no);
        final String countryCode = ccp.getDefaultCountryCode();
        final String countryIso = ccp.getSelectedCountryNameCode();
        nextIcon.setOnClickListener(v -> {

            phoneNumberString = ccp.getSelectedCountryCodeWithPlus() + mobile_no.getText().toString();

            SharedHelper.putKey(getApplicationContext(), "mobile_number", phoneNumberString);
            Log.v("Phonecode", phoneNumberString + " ");
            Intent intent = new Intent(ForgotPassword.this, OtpVerification.class);
            intent.putExtra("phonenumber", phoneNumberString);
            startActivityForResult(intent, APP_REQUEST_CODE);
            dialog.dismiss();


        });

    }


    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
        if (data != null) {
            if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request


                if (getmobile != null) {
                    String[] separated = phoneNumberString.split("\\+");
                    String phoneSplit = separated[1];
                    if (getmobile.contains(phoneSplit)) {
                        email.setFocusable(false);
                        email.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                        email.setClickable(false);

                        validation = "reset";
//                                    titleText.setText(R.string.reset_password);
                        newPasswordLayout.setVisibility(View.VISIBLE);
                        confirmPasswordLayout.setVisibility(View.VISIBLE);
                        OtpLay.setVisibility(View.GONE);
                        note_txt.setVisibility(View.GONE);
                        //OTP.performClick();
                        ll_resend.setVisibility(View.GONE);
                    } else {
                        displayMessage("Mobile no is not match with register emailid");
                    }
                }

                SharedHelper.putKey(ForgotPassword.this, "mobile", phoneNumberString);

            }
        }
    }
}
