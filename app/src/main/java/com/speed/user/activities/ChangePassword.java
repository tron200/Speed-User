package com.speed.user.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ChangePassword extends AppCompatActivity {
    public Context context = ChangePassword.this;
    public Activity activity = ChangePassword.this;
    String TAG = "ChangePasswordActivity";
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Button changePasswordBtn;
    ImageView backArrow;
    EditText current_password, new_password, confirm_new_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewByIdandInitialization();
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            backArrow.setImageDrawable(getDrawable(R.drawable.ic_forward));

        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }



        backArrow.setOnClickListener(view -> onBackPressed());

        changePasswordBtn.setOnClickListener(view -> {
            String current_password_value = current_password.getText().toString();
            String new_password_value = new_password.getText().toString();
            String confirm_password_value = confirm_new_password.getText().toString();
            if (current_password_value == null || current_password_value.equalsIgnoreCase("")) {
                displayMessage(getString(R.string.please_enter_current_pass));
            } else if (new_password_value == null || new_password_value.equalsIgnoreCase("")) {
                displayMessage(getString(R.string.please_enter_new_pass));
            } else if (confirm_password_value == null || confirm_password_value.equalsIgnoreCase("")) {
                displayMessage(getString(R.string.please_enter_confirm_pass));
            } else if (!new_password_value.equals(confirm_password_value)) {
                displayMessage(getString(R.string.different_passwords));
            } else {
                changePassword(current_password_value, new_password_value, confirm_password_value);
            }
        });

    }

    public void findViewByIdandInitialization() {
        current_password = findViewById(R.id.current_password);
        new_password = findViewById(R.id.new_password);
        confirm_new_password = findViewById(R.id.confirm_password);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        backArrow = findViewById(R.id.backArrow);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
    }


    private void changePassword(final String current_pass, final String new_pass, final String confirm_new_pass) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("password", new_pass);
            object.put("password_confirmation", confirm_new_pass);
            object.put("old_password", current_pass);
            Log.e("ChangePasswordAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CHANGE_PASSWORD_API, object, response -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            Log.v("SignInResponse", response.toString());
            displayMessage(response.optString("message"));
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            displayMessage(getString(R.string.something_went_wrong));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }
}
