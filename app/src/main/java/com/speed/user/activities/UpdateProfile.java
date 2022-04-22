package com.speed.user.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.helper.VolleyMultipartRequest;
import com.speed.user.utills.Utilities;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class UpdateProfile extends AppCompatActivity implements View.OnClickListener {
    ImageView backArrow;
    TextView toolName;
    String parameter, value;
    EditText editText;
    TextInputLayout text_input_layout;
    Button btnUpdate;
    Boolean isInternet;
    ConnectionHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setContentView(R.layout.activity_update_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolName = findViewById(R.id.toolName);
        backArrow = findViewById(R.id.backArrow);
        editText = findViewById(R.id.editText);
        btnUpdate = findViewById(R.id.btnUpdate);
        text_input_layout = findViewById(R.id.text_input_layout);
        backArrow.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        getIntentData();


    }

    private void getIntentData() {
        parameter = getIntent().getStringExtra("parameter");
        value = getIntent().getStringExtra("value");
        if (parameter.equalsIgnoreCase("first_name")) {

            toolName.setText(getString(R.string.update_name));
            text_input_layout.setHelperText("This name will be shown to the driver during ride pickup");
            editText.setHint(getString(R.string.name));
            text_input_layout.setHint(getString(R.string.enter_name));
            editText.setText(value);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        }

        if (parameter.equalsIgnoreCase("email")) {

            toolName.setText(getString(R.string.update_email));
            text_input_layout.setHelperText("It is updated to the your account");
            editText.setHint(getString(R.string.email));
            text_input_layout.setHint(getString(R.string.enter_email));
            editText.setText(value);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        if (parameter.equalsIgnoreCase("mobile")) {

            toolName.setText(getString(R.string.update_mobile));
            editText.setHint(getString(R.string.user_no_mobile));
            text_input_layout.setHint(getString(R.string.enter_mobile_no));
            editText.setText(value);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backArrow) {
            Utilities.hideKeyboard(UpdateProfile.this);
            onBackPressed();
            finish();
        }
        if (v.getId() == R.id.btnUpdate) {
            if (editText.getText().toString().equals("")) {
                text_input_layout.setError("This field is not empty");
            } else {
                if (isInternet) {
                    if (parameter.equals("first_name")) {

                        SharedHelper.putKey(getApplicationContext(), "first_name", editText.getText().toString());
//                        if (strList.size()==2)
//                        {
//                            SharedHelper.putKey(getApplicationContext(), "last_name",strList.get(1) );
//                        }

                        updateProfileWithoutImage();
                    } else {
                        SharedHelper.putKey(getApplicationContext(), parameter, editText.getText().toString());
                        updateProfileWithoutImage();
                    }


                }
            }
        }
    }


    private void updateProfileWithoutImage() {

        Dialog dialogCustom = new Dialog(UpdateProfile.this);
        dialogCustom.setContentView(R.layout.custom_dialog);
        dialogCustom.setCancelable(false);
        dialogCustom.show();

        VolleyMultipartRequest volleyMultipartRequest = new
                VolleyMultipartRequest(Request.Method.POST,
                        URLHelper.UseProfileUpdate,
                        response -> {

                            dialogCustom.dismiss();
                            String res = new String(response.data);
                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                SharedHelper.putKey(getApplicationContext(), "id", jsonObject.optString("id"));
                                SharedHelper.putKey(getApplicationContext(), "first_name", jsonObject.optString("first_name"));
                                SharedHelper.putKey(getApplicationContext(), "last_name", jsonObject.optString("last_name"));
                                SharedHelper.putKey(getApplicationContext(), "email", jsonObject.optString("email"));
                                if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                                    SharedHelper.putKey(getApplicationContext(), "picture", "");
                                } else {
                                    if (jsonObject.optString("picture").startsWith("http"))
                                        SharedHelper.putKey(getApplicationContext(), "picture", jsonObject.optString("picture"));
                                    else
                                        SharedHelper.putKey(getApplicationContext(), "picture", URLHelper.BASE + "storage/app/public/" + jsonObject.optString("picture"));
                                }

                                SharedHelper.putKey(getApplicationContext(), "gender", jsonObject.optString("gender"));
                                SharedHelper.putKey(getApplicationContext(), "mobile", jsonObject.optString("mobile"));
                                SharedHelper.putKey(getApplicationContext(), "wallet_balance", jsonObject.optString("wallet_balance"));
                                SharedHelper.putKey(getApplicationContext(), "payment_mode", jsonObject.optString("payment_mode"));
                                callSuccess();
//                                GoToMainActivity();
                                Toast.makeText(UpdateProfile.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                                //displayMessage(getString(R.string.update_success));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                displayMessage(getString(R.string.something_went_wrong));
                            }


                        }, error -> {
                            dialogCustom.dismiss();
                            displayMessage(getString(R.string.something_went_wrong));
                        }) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("first_name", SharedHelper.getKey(getApplicationContext(), "first_name"));
                        params.put("last_name", "");
                        params.put("email", SharedHelper.getKey(getApplicationContext(), "email"));
                        params.put("mobile", SharedHelper.getKey(getApplicationContext(), "mobile"));
                        params.put("picture", "");

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(getApplicationContext(), "token_type")
                                + " " + SharedHelper.getKey(getApplicationContext(), "access_token"));
                        return headers;
                    }
                };
        ClassLuxApp.getInstance().addToRequestQueue(volleyMultipartRequest);

    }

    private void callSuccess() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "result");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }
}
