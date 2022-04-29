package com.speed.user.activities;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.speed.user.R;
import com.speed.user.consts.CouponListAdapter;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.utills.Utilities;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import es.dmoral.toasty.Toasty;

public class CouponActivity extends AppCompatActivity {

    Context context;
    LinearLayout couponListCardView;
    ListView coupon_list_view;
    ArrayList<JSONObject> listItems;
    ListAdapter couponAdapter;
    CustomDialog customDialog;
    Utilities utils = new Utilities();
    private EditText coupon_et;
    private Button apply_button;
    private String session_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        getWindow().setBackgroundDrawableResource(R.drawable.coupon_bg);
        setContentView(R.layout.activity_coupon);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = CouponActivity.this;
        session_token = SharedHelper.getKey(this, "access_token");
        couponListCardView = findViewById(R.id.cardListViewLayout);
        coupon_list_view = findViewById(R.id.coupon_list_view);
        coupon_et = findViewById(R.id.coupon_et);
        apply_button = findViewById(R.id.apply_button);
        apply_button.setOnClickListener(view -> {
            if (coupon_et.getText().toString().isEmpty()) {
                Toast.makeText(CouponActivity.this, getString(R.string.enter_acoupon), Toast.LENGTH_SHORT).show();
            } else {
                sendToServer();
            }
        });

        getCoupon();
    }

    private void sendToServer() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JsonObject json = new JsonObject();
        json.addProperty("promocode", coupon_et.getText().toString());
        Ion.with(this)
                .load(URLHelper.ADD_COUPON_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(CouponActivity.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    try {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        // response contains both the headers and the string result
                        if (e != null) {
                            if (e instanceof NetworkErrorException) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            }
                            if (e instanceof TimeoutException) {
                                sendToServer();
                            }
                            return;
                        }
                        if (response.getHeaders().code() == 200) {
                            Utilities.print("AddCouponRes", "" + response.getResult());
                            try {
                                JSONObject jsonObject = new JSONObject(response.getResult());
                                if (jsonObject.optString("code").equals("promocode_applied")) {
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
//                                        finish();
                                    Toast.makeText(CouponActivity.this, getString(R.string.coupon_added), Toast.LENGTH_SHORT).show();
                                    couponListCardView.setVisibility(View.GONE);
                                    getCoupon();
                                } else if (jsonObject.optString("code").equals("promocode_expired")) {
                                    Toast.makeText(CouponActivity.this, getString(R.string.expired_coupon), Toast.LENGTH_SHORT).show();
                                } else if (jsonObject.optString("code").equals("promocode_already_in_use")) {
                                    Toast.makeText(CouponActivity.this, getString(R.string.already_in_use_coupon), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CouponActivity.this, getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            Utilities.print("AddCouponErr", "" + response.getResult());
                            if (response.getHeaders().code() == 401) {

                            } else
                                Toast.makeText(CouponActivity.this, getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                });
    }

    private void getCoupon() {
        couponListCardView.setVisibility(View.GONE);
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        Ion.with(this)
                .load(URLHelper.COUPON_LIST_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(CouponActivity.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    // response contains both the headers and the string result
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    if (e != null) {
                        if (e instanceof NetworkErrorException) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        }
                        if (e instanceof TimeoutException) {
                            getCoupon();
                        }
                    } else {
                        if (response != null) {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    if (jsonArray != null && jsonArray.length() > 0) {
                                        Utilities.print("CouponActivity", "" + jsonArray.toString());
                                        listItems = getArrayListFromJSONArray(jsonArray);
                                        couponAdapter = new CouponListAdapter(context, R.layout.coupon_list_item, listItems);
                                        coupon_list_view.setAdapter(couponAdapter);
                                        couponListCardView.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                if (response.getHeaders().code() == 401) {
                                }
                            }
                        } else {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                        }
                    }
                });
    }

    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray) {

        ArrayList<JSONObject> aList = new ArrayList<JSONObject>();

        try {
            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    aList.add(jsonArray.getJSONObject(i));

                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return aList;

    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
