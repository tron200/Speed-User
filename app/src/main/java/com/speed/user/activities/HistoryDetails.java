package com.speed.user.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.models.Driver;
import com.speed.user.utills.MyBoldTextView;
import com.speed.user.utills.MyButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class HistoryDetails extends AppCompatActivity {

    public JSONObject jsonObject;
    Activity activity;
    Context context;
    Boolean isInternet;
    ConnectionHelper helper;
    CustomDialog customDialog;
    MyBoldTextView tripAmount;
    MyBoldTextView tripDate;
    MyBoldTextView paymentType;
    MyBoldTextView booking_id;
    MyBoldTextView tripComments;
    MyBoldTextView tripProviderName;
    MyBoldTextView tripSource;
    MyBoldTextView lblTotalPrice;
    MyBoldTextView lblBookingID;
    MyBoldTextView tripDestination;
    MyBoldTextView lblTitle;
    MyBoldTextView lblBasePrice;
    MyBoldTextView lblDistancePrice;
    MyBoldTextView lblTaxPrice;
    ImageView tripImg, tripProviderImg, paymentTypeImg;
    RatingBar tripProviderRating;
    LinearLayout sourceAndDestinationLayout, lnrComments, lnrUpcomingLayout;
    View viewLayout;
    ImageView backArrow;
    LinearLayout parentLayout;
    LinearLayout profileLayout;
    LinearLayout lnrInvoice, lnrInvoiceSub;
    String tag = "";
    MyButton btnCancelRide;
    Driver driver;
    String reason = "";

    Button btnViewInvoice, btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setContentView(R.layout.activity_history_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewByIdAndInitialize();
        try {
            Intent intent = getIntent();
            String post_details = intent.getStringExtra("post_value");
            tag = intent.getStringExtra("tag");
            jsonObject = new JSONObject(post_details);
        } catch (Exception e) {
            jsonObject = null;
        }

        if (jsonObject != null) {

            if (tag.equalsIgnoreCase("past_trips")) {
                btnCancelRide.setVisibility(View.GONE);
                lnrComments.setVisibility(View.VISIBLE);
                lnrUpcomingLayout.setVisibility(View.GONE);
                getRequestDetails();
                lblTitle.setText(getString(R.string.past_trips));
            } else {
                lnrUpcomingLayout.setVisibility(View.VISIBLE);
                btnViewInvoice.setVisibility(View.GONE);
                btnCancelRide.setVisibility(View.VISIBLE);
                lnrComments.setVisibility(View.GONE);
                getUpcomingDetails();
                lblTitle.setText(getString(R.string.upcomeng_rides));
            }
        }
        profileLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryDetails.this, ShowProfile.class);
            intent.putExtra("driver", driver);
            startActivity(intent);
        });

        backArrow.setOnClickListener(view -> onBackPressed());
    }

    public void findViewByIdAndInitialize() {
        activity = HistoryDetails.this;
        context = HistoryDetails.this;
        helper = new ConnectionHelper(activity);
        isInternet = helper.isConnectingToInternet();
        parentLayout = findViewById(R.id.parentLayout);
        profileLayout = findViewById(R.id.profile_detail_layout);
        lnrInvoice = findViewById(R.id.lnrInvoice);
        lnrInvoiceSub = findViewById(R.id.lnrInvoiceSub);
        parentLayout.setVisibility(View.GONE);
        backArrow = findViewById(R.id.backArrow);
        tripAmount = findViewById(R.id.tripAmount);
        tripDate = findViewById(R.id.tripDate);
        paymentType = findViewById(R.id.paymentType);
        booking_id = findViewById(R.id.booking_id);
        paymentTypeImg = findViewById(R.id.paymentTypeImg);
        tripProviderImg = findViewById(R.id.tripProviderImg);
        tripImg = findViewById(R.id.tripImg);
        tripComments = findViewById(R.id.tripComments);
        tripProviderName = findViewById(R.id.tripProviderName);
        tripProviderRating = findViewById(R.id.tripProviderRating);
        tripSource = findViewById(R.id.tripSource);
        tripDestination = findViewById(R.id.tripDestination);
        lblBookingID = findViewById(R.id.lblBookingID);
        lblBasePrice = findViewById(R.id.lblBasePrice);
        lblTaxPrice = findViewById(R.id.lblTaxPrice);
        lblDistancePrice = findViewById(R.id.lblDistancePrice);
        lblTotalPrice = findViewById(R.id.lblTotalPrice);
        lblTitle = findViewById(R.id.lblTitle);
        btnCancelRide = findViewById(R.id.btnCancelRide);
        sourceAndDestinationLayout = findViewById(R.id.sourceAndDestinationLayout);
        lnrComments = findViewById(R.id.lnrComments);
        // viewLayout = (View) findViewById(R.id.ViewLayout);

        lnrUpcomingLayout = findViewById(R.id.lnrUpcomingLayout);
        btnViewInvoice = findViewById(R.id.btnViewInvoice);
        btnCall = findViewById(R.id.btnCall);

        btnCancelRide.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.mipmap.ic_launcher_foreground)
                    .setTitle(R.string.app_name)
                    .setMessage(getString(R.string.cencel_request))
                    .setCancelable(false)
                    .setPositiveButton("YES", (dialog, id) -> {
                        dialog.dismiss();
                        showreasonDialog();
                    })
                    .setNegativeButton("NO", (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        });

        btnViewInvoice.setOnClickListener(v -> lnrInvoice.setVisibility(View.VISIBLE));

        lnrInvoice.setOnClickListener(v -> lnrInvoice.setVisibility(View.GONE));

        lnrInvoiceSub.setOnClickListener(v -> {
        });

        btnCall.setOnClickListener(v -> {
            if (driver.getMobile() != null && !driver.getMobile().equalsIgnoreCase("null") && driver.getMobile().length() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
                } else {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + driver.getMobile()));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intentCall);
                }
            } else {
                displayMessage(getString(R.string.user_no_mobile));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission Granted
            //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + driver.getMobile()));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        }
    }

    private void showreasonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
        Button submitBtn = view.findViewById(R.id.submit_btn);
        builder.setIcon(R.mipmap.ic_launcher_foreground)
                .setTitle(R.string.app_name)
                .setView(view)
                .setCancelable(true);
        submitBtn.setOnClickListener(v -> {
            reason = reasonEtxt.getText().toString();
            cancelRequest();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void getRequestDetails() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_HISTORY_DETAILS_API + "?request_id=" + jsonObject.optString("id"), response -> {

            Log.v("GetPaymentList", response.toString());
            if (response != null && response.length() > 0) {
                if (response.optJSONObject(0) != null) {
                    Picasso.get().load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);
                    Log.e("History Details", "onResponse: Currency" + SharedHelper.getKey(context, "currency"));
                    JSONObject providerObj = response.optJSONObject(0).optJSONObject("provider");
                    if (providerObj != null) {
                        driver = new Driver();
                        driver.setFname(providerObj.optString("first_name"));
                        driver.setLname(providerObj.optString("last_name"));
                        driver.setMobile(providerObj.optString("mobile"));
                        driver.setEmail(providerObj.optString("email"));
                        driver.setImg(providerObj.optString("avatar"));
                        driver.setRating(providerObj.optString("rating"));
                    }
                    if (response.optJSONObject(0).optString("booking_id") != null &&
                            !response.optJSONObject(0).optString("booking_id").equalsIgnoreCase("")) {
                        booking_id.setText(response.optJSONObject(0).optString("booking_id"));
                        lblBookingID.setText(response.optJSONObject(0).optString("booking_id"));
                    }
                    String form;
                    if (tag.equalsIgnoreCase("past_trips")) {
                        form = response.optJSONObject(0).optString("assigned_at");
                    } else {
                        form = response.optJSONObject(0).optString("schedule_at");
                    }
                    if (response.optJSONObject(0).optJSONObject("payment") != null && response.optJSONObject(0).optJSONObject("payment").optString("total") != null &&
                            !response.optJSONObject(0).optJSONObject("payment").optString("total").equalsIgnoreCase("")) {
                        tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + response.optJSONObject(0).optJSONObject("payment").optString("total"));
                        response.optJSONObject(0).optJSONObject("payment");
                        lblBasePrice.setText((SharedHelper.getKey(context, "currency") + ""
                                + response.optJSONObject(0).optJSONObject("payment").optString("fixed")));
                        lblDistancePrice.setText((SharedHelper.getKey(context, "currency") + ""
                                + response.optJSONObject(0).optJSONObject("payment").optString("distance")));
                        lblTaxPrice.setText((SharedHelper.getKey(context, "currency") + ""
                                + response.optJSONObject(0).optJSONObject("payment").optString("tax")));
                        lblTotalPrice.setText((SharedHelper.getKey(context, "currency") + ""
                                + response.optJSONObject(0).optJSONObject("payment").optString("total" +
                                "")));
                    } else {
                        tripAmount.setVisibility(View.GONE);
                    }
                    try {
                        tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + "\n" + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                    if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
                        paymentTypeImg.setImageResource(R.drawable.money_icon);
                    } else {
                        paymentTypeImg.setImageResource(R.drawable.visa);
                    }
                    Picasso.get().load(URLHelper.BASE + "storage/app/public/" + response.optJSONObject(0).optJSONObject("provider").optString("avatar"))
                            .placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(tripProviderImg);
                    if (response.optJSONObject(0).optJSONObject("rating") != null &&
                            !response.optJSONObject(0).optJSONObject("rating").optString("provider_comment").equalsIgnoreCase("")) {
                        tripComments.setText(response.optJSONObject(0).optJSONObject("rating").optString("provider_comment", ""));
                    } else {
                        tripComments.setText(getString(R.string.no_comments));
                    }
                    if (response.optJSONObject(0).optJSONObject("provider").optString("rating") != null
                            && !response.optJSONObject(0).optJSONObject("provider").optString("rating").equalsIgnoreCase("")) {
                        tripProviderRating.setRating(Float.parseFloat(response.optJSONObject(0).optJSONObject("provider").optString("rating")));
                    } else {
                        tripProviderRating.setRating(0);
                    }
                    tripProviderName.setText(response.optJSONObject(0).optJSONObject("provider").optString("first_name"));
                    if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                        sourceAndDestinationLayout.setVisibility(View.GONE);
                        //   viewLayout.setVisibility(View.GONE);
                    } else {
                        tripSource.setText(response.optJSONObject(0).optString("s_address"));
                        tripDestination.setText(response.optJSONObject(0).optString("d_address"));
                    }

                }
            }
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            parentLayout.setVisibility(View.VISIBLE);

        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            displayMessage(getString(R.string.something_went_wrong));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        ClassLuxApp.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    public void getUpcomingDetails() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.UPCOMING_TRIP_DETAILS + "?request_id=" + jsonObject.optString("id"), response -> {

            Log.v("GetPaymentList", response.toString());
            if (response != null && response.length() > 0) {
                if (response.optJSONObject(0) != null) {
                    Picasso.get().load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);
//                    tripDate.setText(response.optJSONObject(0).optString("assigned_at"));
                    paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                    String form = response.optJSONObject(0).optString("schedule_at");
                    JSONObject providerObj = response.optJSONObject(0).optJSONObject("provider");
                    if (response.optJSONObject(0).optString("booking_id") != null &&
                            !response.optJSONObject(0).optString("booking_id").equalsIgnoreCase("")) {
                        booking_id.setText(response.optJSONObject(0).optString("booking_id"));
                    }
                    if (providerObj != null) {
                        driver = new Driver();
                        driver.setFname(providerObj.optString("first_name"));
                        driver.setLname(providerObj.optString("last_name"));
                        driver.setMobile(providerObj.optString("mobile"));
                        driver.setEmail(providerObj.optString("email"));
                        driver.setImg(providerObj.optString("avatar"));
                        driver.setRating(providerObj.optString("rating"));
                    }
                    try {
                        tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + "\n" + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
                        paymentTypeImg.setImageResource(R.drawable.money_icon);
                    } else {
                        paymentTypeImg.setImageResource(R.drawable.visa);
                    }

                    if (response.optJSONObject(0).optJSONObject("provider") != null) {
                        if (response.optJSONObject(0).optJSONObject("provider").optString("avatar") != null)
                            Picasso.get().load(URLHelper.BASE + "storage/app/public/" + response.optJSONObject(0).optJSONObject("provider").optString("avatar"))
                                    .placeholder(R.drawable.car_select).error(R.drawable.car_select).into(tripProviderImg);
                        tripProviderRating.setRating(Float.parseFloat(response.optJSONObject(0).optJSONObject("provider").optString("rating")));
                        tripProviderName.setText(response.optJSONObject(0).optJSONObject("provider").optString("first_name"));
                    } else {
                        btnCall.setVisibility(View.GONE);
                        tripProviderName.setText("Not assigned yet");
                        tripProviderImg.setBackground(getDrawable(R.drawable.ic_dummy_user));
                    }
                    if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                        sourceAndDestinationLayout.setVisibility(View.GONE);
                        // viewLayout.setVisibility(View.GONE);
                    } else {
                        tripSource.setText(response.optJSONObject(0).optString("s_address"));
                        tripDestination.setText(response.optJSONObject(0).optString("d_address"));
                    }

                    try {
                        JSONObject serviceObj = response.optJSONObject(0).optJSONObject("service_type");
                        if (serviceObj != null) {
//                            holder.car_name.setText(serviceObj.optString("name"));
                            if (tag.equalsIgnoreCase("past_trips")) {
                                tripAmount.setText(SharedHelper.getKey(context, "currency") + serviceObj.optString("price"));
                            } else {
                                tripAmount.setVisibility(View.GONE);
                            }
                            Picasso.get().load(serviceObj.optString("image"))
                                    .placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user)
                                    .into(tripProviderImg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                parentLayout.setVisibility(View.VISIBLE);

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
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        ClassLuxApp.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onBackPressed() {
        if (lnrInvoice.getVisibility() == View.VISIBLE) {
            lnrInvoice.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public void cancelRequest() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", jsonObject.optString("id"));
            object.put("cancel_reason", reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, response -> {
            Log.v("CancelRequestResponse", response.toString());
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            finish();
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            displayMessage(getString(R.string.something_went_wrong));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    private String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

    private String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        return timeName;
    }
}
