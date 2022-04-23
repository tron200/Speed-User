package com.speed.user.activities;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.chat.UserChatActivity;
import com.speed.user.fragments.RoundCornerDrawable;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.DataParser;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.models.CardInfo;
import com.speed.user.models.ChangeDestRequest;
import com.speed.user.models.Driver;
import com.speed.user.models.PaymentRequest;
import com.speed.user.models.PaymentResponse;
import com.speed.user.models.RestInterface;
import com.speed.user.models.ServiceGenerator;
import com.speed.user.utills.MapAnimator;
import com.speed.user.utills.ResponseListener;
import com.speed.user.utills.Utilities;
import com.speed.user.utills.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.android.ui.IconGenerator;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;



public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResponseListener, GoogleMap.OnCameraMoveListener, PaymentResultListener {

    private static final String TAG = "TrackActivity";
    private static final int REQUEST_LOCATION = 1450;
    private static final int PAY_NOW_AUTO_REQUEST_CODE = 667;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    public static double current_lngSend, current_latSend;
    public String PreviousStatus = "";
    public String CurrentStatus = "";

    @BindView(R.id.layoutdriverstatus)
    LinearLayout layoutdriverstatus;
    @BindView(R.id.driveraccepted)
    LinearLayout driveraccepted;
    @BindView(R.id.txtdriveraccpted)
    TextView txtdriveraccpted;
    @BindView(R.id.driverArrived)
    LinearLayout driverArrived;
    @BindView(R.id.txtdriverArrived)
    TextView txtdriverArrived;
    @BindView(R.id.imgarrived)
    ImageView imgarrived;
    @BindView(R.id.driverPicked)
    LinearLayout driverPicked;
    @BindView(R.id.txtdriverpicked)
    TextView txtdriverpicked;
    @BindView(R.id.imgPicked)
    ImageView imgPicked;
    @BindView(R.id.driverCompleted)
    LinearLayout driverCompleted;
    @BindView(R.id.txtdrivercompleted)
    TextView txtdrivercompleted;
    @BindView(R.id.imgDropped)
    ImageView imgDropped;

    @BindView(R.id.lnrFlow)
    LinearLayout lnrFlow;

    @BindView(R.id.promoLayout)
    LinearLayout promoLayout;
    @BindView(R.id.lblDistancePrice)
    TextView lblDistancePrice;

    @BindView(R.id.txtDiscount)
    TextView txtDiscount;
    @BindView(R.id.txtPickUpNotes)
    TextView txtPickUpNotes;
    //    @BindView(R.id.lblPaymentChange)
//    TextView lblPaymentChange;
    TextView lblPaymentChange;
    String pickUpLocationName, dropLocationName;
//    @BindView(R.id.ImgConfrmCabType)
//    ImageView ImgConfrmCabType;
//    @BindView(R.id.lblCmfrmSourceAddress)
//    MyTextView lblCmfrmSourceAddress;
//    @BindView(R.id.lblCmfrmDestAddress)
//    MyTextView lblCmfrmDestAddress;

    /// source dest layout
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.imgSos)
    ImageView imgSos;
    @BindView(R.id.imgShareRide)
    ImageView imgShareRide;
    @BindView(R.id.mapfocus)
    ImageView mapfocus;
    @BindView(R.id.shadowBack)
    ImageView shadowBack;
    @BindView(R.id.lnrWaitingForProviders)
    RelativeLayout lnrWaitingForProviders;
    @BindView(R.id.lblNoMatch)
    TextView lblNoMatch;
    @BindView(R.id.btnCancelRide)
    Button btnCancelRide;
//    @BindView(R.id.content)
//    RippleBackground content;

    //// Waiting for provider layout
    @BindView(R.id.lnrProviderAccepted)
    LinearLayout lnrProviderAccepted;

    @BindView(R.id.AfterAcceptButtonLayout)
    LinearLayout AfterAcceptButtonLayout;
    @BindView(R.id.imgProvider)
    CircleImageView imgProvider;
    @BindView(R.id.imgServiceRequested)
    ImageView imgServiceRequested;

    /// Driver Accepted
    @BindView(R.id.lblProvider)
    TextView lblProvider;


    @BindView(R.id.lblSurgePrice)
    TextView lblSurgePrice;
    @BindView(R.id.lblServiceRequested)
    TextView lblServiceRequested;
    @BindView(R.id.lblModelNumber)
    TextView lblModelNumber;
    @BindView(R.id.ratingProvider)
    RatingBar ratingProvider;
    @BindView(R.id.btnCancelTrip)
    TextView btnCancelTrip;

    /// Invoice layout
    @BindView(R.id.lnrInvoice)
    LinearLayout lnrInvoice;
    @BindView(R.id.lblBasePrice)
    TextView lblBasePrice;
    @BindView(R.id.lblExtraPrice)
    TextView lblExtraPrice;
    @BindView(R.id.lblTaxPrice)
    TextView lblTaxPrice;
    @BindView(R.id.lblTotalPrice)
    TextView lblTotalPrice;
    @BindView(R.id.lblPaymentTypeInvoice)
    TextView lblPaymentTypeInvoice;
    @BindView(R.id.imgPaymentTypeInvoice)
    ImageView imgPaymentTypeInvoice;
    @BindView(R.id.btnPayNow)
    Button btnPayNow;

    //// Rate provider
    @BindView(R.id.lnrRateProvider)
    LinearLayout lnrRateProvider;
    @BindView(R.id.lblProviderName)
    TextView lblProviderName;
    @BindView(R.id.imgProviderRate)
    CircleImageView imgProviderRate;
    @BindView(R.id.txtComments)
    EditText txtComments;
    @BindView(R.id.ratingProviderRate)
    RatingBar ratingProviderRate;
    @BindView(R.id.btnSubmitReview)
    Button btnSubmitReview;

    @BindView(R.id.rtlStaticMarker)
    RelativeLayout rtlStaticMarker;
    @BindView(R.id.imgDestination)
    ImageView imgDestination;
    @BindView(R.id.btnDone)
    Button btnDone;
    @BindView(R.id.booking_id)
    TextView booking_id;
    @BindView(R.id.tvPaymentLabel)
    TextView tvPaymentLabel;

    @BindView(R.id.btnCall)
    ImageButton btnCall;
    @BindView(R.id.btnChat)
    ImageButton btnChat;
    String current_chat_requestID = "";
    String currentProviderID = "";
    String userID = "";
    String providerFirstName = "";
    String providerLastName = "";

    @BindView(R.id.imgEditDestination)
    ImageView imgEditDestination;

    int AUTOCOMPLETE_REQUEST_CODE = 1;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

    RestInterface restInterface;
    Call<PaymentResponse> paymentResponseCall;
    Activity activity;
    Context context;
    String ETA;
    String isPaid = "", paymentMode = "";
    Utilities utils = new Utilities();
    int flowValue = 0;
    String reqStatus = "";
    String feedBackRating;
    Handler handleCheckStatus;
    String strTag = "";
    boolean once = true;
    Driver driver;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    int value;
    //    Marker marker;
    Double latitude, longitude;
    String currentAddress;


    GoogleApiClient mGoogleApiClient;
    ImageView destinationBorderImg;

    String scheduledDate = "";
    String scheduledTime = "";
    String cancalReason = "";
    LocationRequest mLocationRequest;
    RippleBackground rippleBackground;

    CameraPosition cmPosition;
    String current_lat = "", current_lng = "", current_address = "", source_lat = "", source_lng = "", source_address = "",
            dest_lat = "", dest_lng = "", dest_address = "";
    //Internet
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    LatLng sourceLatLng;
    LatLng destLatLng;
    Marker sourceMarker;
    Marker destinationMarker;
    Marker providerMarker;
    AlertDialog alert;
    TextView lblPaymentType, txtpaiddriver;
    TextView lblDis, lblEta, lblApproxAmount, lblCmfrmSourceAddress, lblCmfrmDestAddress;
    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;

    boolean isRequestProviderScreen;
    String cancaltype = "";
    Dialog confirmDialog;
    String paymentType = "";
    String paymentId = "";
    Call<ResponseBody> changeLocationCall;
    String etaDur = "0";
    //    private ImageView imgBack;
    private boolean mIsShowing;
    private boolean mIsHiding;

    @butterknife.OnClick(R.id.btnCall)
    void callbtnCall() {
        Intent intentCall = new Intent(Intent.ACTION_DIAL);
        intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
        startActivity(intentCall);

    }

    @butterknife.OnClick(R.id.btnChat)
    void callbtnChat() {

        Intent intentChat = new Intent(context, UserChatActivity.class);
        intentChat.putExtra("requestId", current_chat_requestID);
        intentChat.putExtra("providerId", currentProviderID);
        intentChat.putExtra("userId", userID);
        intentChat.putExtra("userName", providerFirstName);
        context.startActivity(intentChat);
    }

    @OnClick(R.id.imgEditDestination)
    void changeDestination() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrackActivity.this);
        builder.setMessage("Do you want to change the destination")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Autocomplete.IntentBuilder(
                                AutocompleteActivityMode.FULLSCREEN, fields)
                                .build(TrackActivity.this);
                        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    @butterknife.OnClick(R.id.lnrWaitingForProviders)
    void lnrWaitingForProvidersClick() {

    }

    @butterknife.OnClick(R.id.lnrRateProvider)
    void lnrRateProviderClick() {

    }

    @butterknife.OnClick(R.id.lnrInvoice)
    void lnrInvoiceClick() {

    }

    @butterknife.OnClick(R.id.lnrProviderAccepted)
    void lnrProviderAcceptedClick() {

    }

    @butterknife.OnClick(R.id.imgShareRide)
    void imgShareRideClick() {
        String url = "http://maps.google.com/maps?q=loc:";
        navigateToShareScreen(url);
    }

    @butterknife.OnClick(R.id.imgSos)
    void imgSosClick() {
        showSosPopUp();
    }

    @butterknife.OnClick(R.id.imgProviderRate)
    void imgProviderRateClick() {
        Intent intent4 = new Intent(activity, ShowProfile.class);
        intent4.putExtra("driver", driver);
        startActivity(intent4);
    }

    @butterknife.OnClick(R.id.imgProvider)
    void imgProviderClick() {
        Intent intent1 = new Intent(activity, ShowProfile.class);
        intent1.putExtra("driver", driver);
        startActivity(intent1);
    }

    @butterknife.OnClick(R.id.mapfocus)
    void mapfocusClick() {
        Double crtLat, crtLng;
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            crtLat = Double.parseDouble(current_lat);
            crtLng = Double.parseDouble(current_lng);

            if (crtLat != null && crtLng != null) {
                LatLng loc = new LatLng(crtLat, crtLng);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mapfocus.setVisibility(View.INVISIBLE);
            }
        }
    }

    @butterknife.OnClick(R.id.btnDone)
    void btnDoneClick() {

    }

    @butterknife.OnClick(R.id.btnSubmitReview)
    void btnSubmitReviewClick() {
        submitReviewCall();
    }

    @butterknife.OnClick(R.id.btnPayNow)
    void btnPayNowClick() {
        if (lblPaymentTypeInvoice.getText().toString().equalsIgnoreCase("CARD")) {
            payNowCard();
        } else if (lblPaymentTypeInvoice.getText().toString().equalsIgnoreCase("PAYPAL")) {
            Log.d(TAG, "btnPayNowClick: " + lblTotalPrice.getText().toString());
            //   confirmFinalPayment(lblTotalPrice.getText().toString());
//       payNowCard();
            PayPalConfiguration config = new PayPalConfiguration()
                    // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                    // or live (ENVIRONMENT_PRODUCTION)
                    .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
                    .clientId("AfkUnyokJW7R1C5ylbjsrST_bw8-qkO8yQSb_bUXtWS6KFrTvPs3IOB4XX7DTJlBiY1InG2q6gz5bmle\n" +
                            "PAYPAL_SECRET=EAchM9cqDqo7iCiLZunNnMW2bgAFvAgAVaUdv_hGgoC9ShkIW07br0s8gf9hHjlFnvT-x3DSS7cfX56H\n" +
                            "PAYPAL_MODE=sandbox");

            PayPalPayment payment = new PayPalPayment(new BigDecimal(lblTotalPrice.getText().toString().replace("$", "")), "USD", " ",
                    PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(TrackActivity.this, PaymentActivity.class);
            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            startActivityForResult(intent, 0);


        } else {
            Checkout.preload(TrackActivity.this);
            startPayment();
        }

    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            String amount = lblTotalPrice.getText().toString().replace("$", "").trim();
            Log.v("totalamount", amount + " ");
            float finalAmount = (Float.parseFloat(amount)) * 100;
            Log.v("finalAmount", finalAmount + " ");
            JSONObject options = new JSONObject();
            options.put("name", "Razorpay Corp");
            options.put("description", "PinTaxi Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", finalAmount + "");

            JSONObject preFill = new JSONObject();
            preFill.put("email", SharedHelper.getKey(TrackActivity.this, "email"));
            preFill.put("contact", SharedHelper.getKey(TrackActivity.this, "mobile"));

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            paymentType = "RAZORPAY";
            paymentId = razorpayPaymentID;
            payNowCard();
            Toast.makeText(TrackActivity.this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.v(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(TrackActivity.this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.v(TAG, "Exception in onPaymentError", e);
        }
    }

    private void init() {

        context = TrackActivity.this;
        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        statusCheck();

        checkStatus();
        handleCheckStatus = new Handler();
        //check status every 3 sec
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.isConnectingToInternet()) {

                    checkStatus();
                    Utilities.print("Handler", "Called");
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                        alert = null;
                    }
                } else {
                    showDialog();
                }
                handleCheckStatus.postDelayed(this, 5000);
            }
        }, 5000);


        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("flowValue")) {
                flowValue = intent.getIntExtra("flowValue", 0);
            }
        } else {
            flowValue = 0;
        }
        layoutChanges();

        //Load animation
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slide_up_top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_top);
        slide_up_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_down);

        lblPaymentChange = findViewById(R.id.lblPaymentChanges);
        lblPaymentChange.setOnClickListener(v -> {
            Intent intent1 = new Intent(TrackActivity.this, CouponActivity.class);
            startActivity(intent1);
        });
    }

    @butterknife.OnClick(R.id.imgBack)
    void imgBackClick() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @butterknife.OnClick(R.id.btnCancelRide)
    void btnCancelRideClick() {
        showCancelRideDialog();
    }

    @butterknife.OnClick(R.id.btnCancelTrip)
    void btnCancelTripClick() {
        if (btnCancelTrip.getText().toString().equals(getResources()
                .getString(R.string.cancel_trip)))
            showCancelRideDialog();
        else {
            String shareUrl = URLHelper.REDIRECT_SHARE_URL;
            navigateToShareScreen(shareUrl);
        }
    }

    private void showChooser() {
        Intent intent = new Intent(TrackActivity.this, Payment.class);
        startActivityForResult(intent, 5555);
    }

    //    private TextView lblDistancePrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setContentView(R.layout.activity_track);
        context = TrackActivity.this;
        activity = TrackActivity.this;
        ButterKnife.bind(this);
        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_map_api));

// Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);
        lnrFlow.getBackground().setAlpha(127);
        restInterface = ServiceGenerator.createService(RestInterface.class);
        SharedHelper.putKey(context, "AUTO_CARETAKER_REQ", "");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
                //permission to access location
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Android M Permission check
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    initMap();
                    MapsInitializer.initialize(getApplicationContext());
                }
            }
        }, 500);
        reqStatus = SharedHelper.getKey(context, "req_status");
        lblPaymentType = findViewById(R.id.lblPaymentType);
        txtpaiddriver = findViewById(R.id.txtpaiddriver);
        lblDis = findViewById(R.id.lblDis);
        lblEta = findViewById(R.id.lblEta);
        lblApproxAmount = findViewById(R.id.lblApproxAmount);
        lblCmfrmSourceAddress = findViewById(R.id.lblCmfrmSourceAddress);
        lblCmfrmDestAddress = findViewById(R.id.lblCmfrmDestAddress);
        txtPickUpNotes = findViewById(R.id.txtPickUpNotes);

        lblPaymentType.setOnClickListener(v -> showChooser());
    }

    @OnClick(R.id.txtPickUpNotes)
    public void spcialiNotesClcik() {

        Intent intentPickuP = new Intent(TrackActivity.this, PickUpNotes.class);
        intentPickuP.putExtra("request_id", current_chat_requestID + "");
        startActivity(intentPickuP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handleCheckStatus.removeCallbacksAndMessages(null);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TrackActivity.this);
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    void initMap() {
        if (mMap == null) {
            FragmentManager fm = getSupportFragmentManager();
            mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
            mapFragment.getMapAsync(this);
        }

        if (mMap != null) {
            setupMap();
        }

    }

    @SuppressWarnings("MissingPermission")
    void setupMap() {
        if (mMap != null) {
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        }

    }

    public void statusCheck() {
        if (getApplicationContext() != null) {
            LocationManager manager = (LocationManager) TrackActivity.this.getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableLoc();
            }
        }
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(TrackActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d("Location error", "Connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(connectionResult -> Log.d("Location error", "Location error " + connectionResult.getErrorCode())).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();

            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(TrackActivity.this, REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;

                case LocationSettingsStatusCodes.CANCELED:
                    showDialogForGPSIntent();
                    break;
            }
        });
    }

    private void showDialogForGPSIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("GPS is disabled in your device. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        (dialog, id) -> {
                            Intent callGPSSettingIntent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(callGPSSettingIntent);
                        });
        builder.setNegativeButton("Cancel",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    private void mapClear() {
        mMap.clear();
        source_lat = "";
        source_lng = "";
        dest_lat = "";
        dest_lng = "";
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void reCreateMap() {
        if (mMap != null) {
            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
        }
    }

    @Override
    public void getJSONArrayResult(String strTag, JSONArray arrayResponse) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();

        if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {

            current_lat = "" + location.getLatitude();
            current_lng = "" + location.getLongitude();
            current_latSend = location.getLatitude();
            current_lngSend = location.getLongitude();
            if (source_lat.equalsIgnoreCase("") || source_lat.length() < 0) {
                source_lat = current_lat;
            }
            if (source_lng.equalsIgnoreCase("") || source_lng.length() < 0) {
                source_lng = current_lng;
            }

            if (value == 0) {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.setPadding(0, 0, 0, 0);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                currentAddress = utils.getCompleteAddressString(context, latitude, longitude);
                source_lat = "" + latitude;
                source_lng = "" + longitude;
                source_address = currentAddress;
                current_address = currentAddress;
                // frmSource.setText(currentAddress);
                value++;

            }
        }
    }

    @Override
    public void onCameraMove() {

        Utilities.print("Current marker", "Zoom Level " + mMap.getCameraPosition().zoom);
        cmPosition = mMap.getCameraPosition();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        String title = "";

        if (marker != null && marker.getTitle() != null) {
            title = marker.getTitle();

            if (sourceMarker != null && title.equalsIgnoreCase("Source")) {
                LatLng markerLocation = sourceMarker.getPosition();
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                source_lat = markerLocation.latitude + "";
                source_lng = markerLocation.longitude + "";

                try {
                    addresses = geocoder.getFromLocation(markerLocation.latitude, markerLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        SharedHelper.putKey(context, "source", "" + address + "," + city + "," + state);
                        source_address = "" + address + "," + city + "," + state;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (destinationMarker != null && title.equalsIgnoreCase("Destination")) {
                LatLng markerLocation = destinationMarker.getPosition();
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                dest_lat = "" + markerLocation.latitude;
                dest_lng = "" + markerLocation.longitude;

                try {
                    addresses = geocoder.getFromLocation(markerLocation.latitude, markerLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        SharedHelper.putKey(context, "destination", "" + address + "," + city + "," + state);
                        dest_address = "" + address + "," + city + "," + state;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            mMap.clear();
            setValuesForSourceAndDestination();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getApplicationContext(), R.raw.style_json));

            if (!success) {
                Utilities.print("Map:Style", "Style parsing failed.");
            } else {
                Utilities.print("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            Utilities.print("Map:Style", "Can't find style. Error: ");
        }

        mMap = googleMap;

        setupMap();

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
            } else {
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
        }

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(TrackActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(TrackActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    1);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(TrackActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String name = SharedHelper.getKey(context, "first_name");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "GoCab-" + "Mr/Mrs." + name + " would like to share a trip with you at " +
                    shareUrl + current_lat + "," + current_lng);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }
    }

    // layout changes
    void layoutChanges() {
        try {
            if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            } else if (lnrRateProvider.getVisibility() == View.VISIBLE) {
                lnrRateProvider.startAnimation(slide_down);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            }

            lnrWaitingForProviders.setVisibility(View.GONE);
            lnrProviderAccepted.setVisibility(View.GONE);
            lnrInvoice.setVisibility(View.GONE);
            lnrRateProvider.setVisibility(View.GONE);
            rtlStaticMarker.setVisibility(View.GONE);


            shadowBack.setVisibility(View.GONE);
            txtComments.setText("");
            if (flowValue == 0) {


                dest_address = "";
                dest_lat = "";
                dest_lng = "";
                source_lat = "" + current_lat;
                source_lng = "" + current_lng;
                source_address = "" + current_address;
            } else if (flowValue == 1) {

                destinationBorderImg.setVisibility(View.GONE);
                imgBack.setVisibility(View.VISIBLE);

                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(true);
                    destinationMarker.setDraggable(true);
                }

            } else if (flowValue == 2) {

                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }

            } else if (flowValue == 3) {

                lnrWaitingForProviders.setVisibility(View.VISIBLE);

            } else if (flowValue == 4) {

                lnrProviderAccepted.startAnimation(slide_up);


                lnrProviderAccepted.setVisibility(View.VISIBLE);
            } else if (flowValue == 5) {

                lnrInvoice.startAnimation(slide_up);
                lnrInvoice.setVisibility(View.VISIBLE);
            } else if (flowValue == 6) {

                lnrRateProvider.startAnimation(slide_up);
                lnrRateProvider.setVisibility(View.VISIBLE);
                LayerDrawable drawable = (LayerDrawable) ratingProviderRate.getProgressDrawable();
                drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
                ratingProviderRate.setRating(1.0f);
                feedBackRating = "1";
                ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                        if (rating < 1.0f) {
                            ratingProviderRate.setRating(1.0f);
                            feedBackRating = "1";
                        }
                        feedBackRating = String.valueOf((int) rating);
                    }
                });
            } else if (flowValue == 7) {

            } else if (flowValue == 8) {
                // clear all views
                shadowBack.setVisibility(View.GONE);
            } else if (flowValue == 9) {

                rtlStaticMarker.setVisibility(View.VISIBLE);
                shadowBack.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSosPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.emaergeny_call))
                .setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
            } else {
                Intent intentCall1 = new Intent(Intent.ACTION_DIAL);
                intentCall1.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                startActivity(intentCall1);
            }
        });
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void submitReviewCall() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("rating", feedBackRating);
            object.put("comment", "" + txtComments.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RATE_PROVIDER_API, object, response -> {
            Utilities.print("SubmitRequestResponse", response.toString());
            utils.hideKeypad(context, activity.getCurrentFocus());
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            // destination.setText("");
            // frmDest.setText("");
            SharedHelper.putKey(context, "service_type_Car_Ambulance", "");
            Intent goMain = new Intent(activity, MainActivity.class);
            goMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(goMain);
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            displayMessage(getString(R.string.places_try_again));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type")
                        + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void showCancelRideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.cancel_ride_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> showreasonDialog());
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showreasonDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
        Button submitBtn = view.findViewById(R.id.submit_btn);
        RadioGroup radioCancel = view.findViewById(R.id.radioCancel);
        radioCancel.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.driver) {
                reasonEtxt.setVisibility(View.VISIBLE);
                cancaltype = getResources().getString(R.string.plan_changed);
            }
            if (checkedId == R.id.vehicle) {
                reasonEtxt.setVisibility(View.VISIBLE);
                cancaltype = getResources().getString(R.string.booked_another_cab);
            }
            if (checkedId == R.id.app) {
                reasonEtxt.setVisibility(View.VISIBLE);
                cancaltype = getResources().getString(R.string.my_reason_is_not_listed);
            }
            if (checkedId == R.id.denied) {
                reasonEtxt.setVisibility(View.VISIBLE);
                cancaltype = getResources().getString(R.string.driver_denied_to_come);
            }
            if (checkedId == R.id.moving) {
                reasonEtxt.setVisibility(View.VISIBLE);
                cancaltype = getResources().getString(R.string.driver_is_not_moving);
            }
        });
        builder.setView(view)
                .setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        submitBtn.setOnClickListener(v -> {

            if (cancaltype.isEmpty()) {
                Toast.makeText(TrackActivity.this, getResources().getString(R.string.please_select_reason), Toast.LENGTH_SHORT).show();

            } else {
                cancalReason = reasonEtxt.getText().toString();
                if (cancalReason.isEmpty()) {
                    reasonEtxt.setError(getResources().getString(R.string.please_specify_reason));
                } else {
                    cancelRequest();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();

    }

    public void payNowBacked(String merchantReqId,
                             String checkOutReqId) {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("m_id", merchantReqId);
            object.put("c_id", checkOutReqId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.PAY_NOW_API,
                        object,
                        response -> {
                            Utilities.print("PayNowRequestResponse", response.toString());
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            SharedHelper.putKey(context, "total_amount", "");
                            flowValue = 6;
                            layoutChanges();
                        }, error -> {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                    displayMessage(getString(R.string.places_try_again));
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type")
                                + " " + SharedHelper.getKey(context, "access_token"));
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        return headers;
                    }
                };

        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    void payNow() {
        String timestamp = Utils.getTimestamp();
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAccountReference("test");
        paymentRequest.setAmount("32");
        paymentRequest.setBusinessShortCode("174379");
        paymentRequest.setCallBackURL("https://spurquoteapp.ga/pusher/pusher.php?title=stk_push&message=result&push_type=individual&regId=null");
        paymentRequest.setPartyA("254700000000");
        paymentRequest.setPartyB("174379");
        paymentRequest.setPassword("MTc0Mzc5YmZiMjc5ZjlhYTliZGJjZjE1OGU5N2RkNzFhNDY3Y2QyZTBjODkzMDU5YjEwZjc4ZTZiNzJhZGExZWQyYzkxOTIwMTkwMTAxMTkyODQz");
        paymentRequest.setPhoneNumber("254700000000");
        paymentRequest.setTimestamp("20190101192843");
        paymentRequest.setTransactionDesc("test");
        paymentRequest.setTransactionType("CustomerPayBillOnline");

        String auth = "Bearer " + SharedHelper.getKey(TrackActivity.this,
                "paymentAccessToken");
        String requestWith = "XMLHttpRequest";
        String CONTENT_TYPE = "application/json";

        paymentResponseCall = restInterface.createSocialLogin(requestWith, CONTENT_TYPE,
                auth, paymentRequest);
        paymentResponseCall.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, retrofit2.Response<PaymentResponse> response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                if (response.code() == 200) {
                    String cId = response.body().getMerchantRequestID();
                    String mId = response.body().getCheckoutRequestID();
                    payNowBacked(mId, cId);
                } else {
                    Toast.makeText(TrackActivity.this, "mPesa is not responding", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                Toast.makeText(TrackActivity.this, "mPesa Server error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmFinalPayment(String totalFee) {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("total_amount", lblTotalPrice.getText().toString());
            object.put("req_id", SharedHelper.getKey(getApplicationContext(), "request_id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.PAY_NOW_API, object, response -> {

            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            if (response != null) {
                try {
                    String status = response.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        //  paymentShowDialog(response.optString("message"));
                    } else {
                        //   paymentErrorShowDialog(response.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void cancelRequest() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("cancel_reason", cancalReason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, response -> {
            Utilities.print("CancelRequestResponse", response.toString());
            Toast.makeText(context, getResources().getString(R.string.request_cancel), Toast.LENGTH_SHORT).show();
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            SharedHelper.putKey(context, "request_id", "");
            SharedHelper.putKey(context, "service_type_Car_Ambulance", "");
            SharedHelper.putKey(context, "Insurance_id", "");
            SharedHelper.putKey(context, "insurance_price", "");
            SharedHelper.putKey(context, "insurance_name", "");

            PreviousStatus = "";
            Intent goMain = new Intent(activity, MainActivity.class);
            goMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(goMain);
            activity.finish();
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            displayMessage(getString(R.string.places_try_again));
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

    private void checkStatus() {
        try {

            Utilities.print("Handler", "Inside");
            if (isInternet) {

                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        URLHelper.REQUEST_STATUS_CHECK_API, null, response -> {
                            SharedHelper.putKey(context, "req_status", "");
                            try {
                                if (customDialog != null && customDialog.isShowing()) {
                                    customDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            reqStatus = "";
                            Utilities.print("Response", "" + response.toString());

                            if (response.optJSONArray("data") != null && response.optJSONArray("data").length() > 0) {
                                Utilities.print("response", "not null");
                                try {
                                    Log.i(TAG, "Check onResponse : " + response.toString(1));

                                    JSONArray requestStatusCheck = response.optJSONArray("data");
                                    JSONObject requestStatusCheckObject = requestStatusCheck.getJSONObject(0);
                                    //Driver Detail
                                    if (requestStatusCheckObject.optJSONObject("provider") != null) {
                                        current_chat_requestID = requestStatusCheckObject.optString("id");
                                        currentProviderID = requestStatusCheckObject.optJSONObject("provider").optString("id");
                                        userID = requestStatusCheckObject.optJSONObject("user").optString("id");
                                        driver = new Driver();
                                        providerFirstName = requestStatusCheckObject.optJSONObject("provider").optString("first_name");
                                        driver.setFname(requestStatusCheckObject.optJSONObject("provider").optString("first_name"));
                                        // driver.setLname(requestStatusCheckObject.optJSONObject("provider").optString("last_name"));
                                        driver.setEmail(requestStatusCheckObject.optJSONObject("provider").optString("email"));
                                        driver.setMobile(requestStatusCheckObject.optJSONObject("provider").optString("mobile"));
                                        driver.setImg(requestStatusCheckObject.optJSONObject("provider").optString("avatar"));
                                        driver.setRating(requestStatusCheckObject.optJSONObject("provider").optString("rating"));
                                    }
                                    String status = requestStatusCheckObject.optString("status");
                                    reqStatus = requestStatusCheckObject.optString("status");


                                    SharedHelper.putKey(context, "req_status", requestStatusCheckObject.optString("status"));
                                    String wallet = requestStatusCheckObject.optString("use_wallet");
                                    if (status.equalsIgnoreCase("STARTED")
                                            || status.equalsIgnoreCase("ARRIVED")) {
                                        source_lat = requestStatusCheckObject.optJSONObject("provider").optString("latitude");
                                        source_lng = requestStatusCheckObject.optJSONObject("provider").optString("longitude");
                                        dest_lat = requestStatusCheckObject.optString("s_latitude");
                                        dest_lng = requestStatusCheckObject.optString("s_longitude");
                                        setValuesForSourceAndDestination();

                                    } else if (status.equalsIgnoreCase("PICKEDUP")) {
                                        source_lat = requestStatusCheckObject.optJSONObject("provider").optString("latitude");
                                        source_lng = requestStatusCheckObject.optJSONObject("provider").optString("longitude");
                                        dest_lat = requestStatusCheckObject.optString("d_latitude");
                                        dest_lng = requestStatusCheckObject.optString("d_longitude");
                                        setValuesForSourceAndDestination();

                                    } else {
                                        source_lat = requestStatusCheckObject.optString("s_latitude");
                                        source_lng = requestStatusCheckObject.optString("s_longitude");
                                        dest_lat = requestStatusCheckObject.optString("d_latitude");
                                        dest_lng = requestStatusCheckObject.optString("d_longitude");
                                        setValuesForSourceAndDestination();

                                    }
                                    pickUpLocationName = requestStatusCheckObject.optString("s_address");
                                    dropLocationName = requestStatusCheckObject.optString("d_address");
                                    setValuesForSourceAndDestination();


                                    // surge price
                                    if (requestStatusCheckObject.optString("surge").equalsIgnoreCase("1")) {
                                        lblSurgePrice.setVisibility(View.VISIBLE);
                                    } else {
                                        lblSurgePrice.setVisibility(View.GONE);
                                    }

                                    Utilities.print("PreviousStatus", "" + PreviousStatus);

                                    if (!PreviousStatus.equals(status)) {
                                        mMap.clear();
                                        PreviousStatus = status;
                                        flowValue = 8;
                                        layoutChanges();
                                        SharedHelper.putKey(context, "request_id", "" + requestStatusCheckObject.optString("id"));
                                        reCreateMap();
                                        Utilities.print("ResponseStatus", "SavedCurrentStatus: " + CurrentStatus + " Status: " + status);
                                        switch (status) {
                                            case "SEARCHING":
                                                show(lnrWaitingForProviders);
                                                //rippleBackground.startRippleAnimation();
                                                strTag = "search_completed";
                                                if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                                                    LatLng myLocation1 = new LatLng(Double.parseDouble(source_lat),
                                                            Double.parseDouble(source_lng));
                                                    CameraPosition cameraPosition1 = new CameraPosition.Builder().target(myLocation1).zoom(16).build();
                                                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                                                }
                                                break;
                                            case "CANCELLED":
                                                strTag = "";
                                                imgSos.setVisibility(View.GONE);
                                                break;
                                            case "ACCEPTED":
                                                driveraccepted.setVisibility(View.VISIBLE);
                                                driverArrived.setVisibility(View.GONE);
                                                driverPicked.setVisibility(View.GONE);
                                                driverCompleted.setVisibility(View.GONE);
                                                txtdriveraccpted.setText(getString(R.string.arriving));
                                                imgarrived.setImageResource(R.drawable.arriveddisable);
                                                imgPicked.setImageResource(R.drawable.pickeddisable);
                                                imgDropped.setImageResource(R.drawable.complete);

                                                strTag = "ride_accepted";
                                                try {
                                                    JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                    JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                    JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                    SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                    lblProvider.setText(provider.optString("first_name"));


                                                    if (provider.optString("avatar").startsWith("http"))
                                                        Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    else
                                                        Picasso.get().load(URLHelper.BASE + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    lblServiceRequested.setText(service_type.optString("name"));
                                                    lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                    Picasso.get().load(service_type.optString("image"))
                                                            .placeholder(R.drawable.car_select).error(R.drawable.car_select)
                                                            .into(imgServiceRequested);
                                                    ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
    //                                                lblDis.setText(service_type.optString("distance")+" km");
    //                                                lblEta.setText(service_type.optString("minute")+" min");
                                                    lblApproxAmount.setText( service_type.optString("fixed") + SharedHelper.getKey(TrackActivity.this, "currency"));

                                                    lblCmfrmSourceAddress.setText(pickUpLocationName);
                                                    lblCmfrmDestAddress.setText(dropLocationName);
                                                    //lnrAfterAcceptedStatus.setVisibility(View.GONE);

                                                    AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                    show(lnrProviderAccepted);
                                                    flowValue = 9;
                                                    layoutChanges();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            case "STARTED":
                                                strTag = "ride_started";
                                                showStartedDialog(requestStatusCheckObject);

                                                break;

                                            case "ARRIVED":
                                                if ((confirmDialog != null) && (confirmDialog.isShowing()))
                                                    confirmDialog.dismiss();
                                                driveraccepted.setVisibility(View.GONE);
                                                driverArrived.setVisibility(View.VISIBLE);
                                                driverPicked.setVisibility(View.GONE);
                                                driverCompleted.setVisibility(View.GONE);
                                                txtdriverArrived.setText(getString(R.string.arrived));
                                                imgarrived.setImageResource(R.drawable.arriveddisable);
                                                imgPicked.setImageResource(R.drawable.pickeddisable);
                                                imgDropped.setImageResource(R.drawable.complete);

                                                once = true;
                                                strTag = "ride_arrived";
                                                Utilities.print("MyTest", "ARRIVED");
                                                try {
                                                    Utilities.print("MyTest", "ARRIVED TRY");
                                                    JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                    JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                    JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                    lblProvider.setText(provider.optString("first_name"));

                                                    if (provider.optString("avatar").startsWith("http"))
                                                        Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    else
                                                        Picasso.get().load(URLHelper.BASE + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    lblServiceRequested.setText(service_type.optString("name"));
                                                    lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                    Picasso.get().load(URLHelper.BASE + service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                    ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));

    //                                                lblDis.setText(service_type.optString("distance")+" km");
    //                                                lblEta.setText(service_type.optString("minute")+" min");
                                                    lblApproxAmount.setText( service_type.optString("fixed") + SharedHelper.getKey(TrackActivity.this, "currency"));

                                                    lblCmfrmSourceAddress.setText(pickUpLocationName);
                                                    lblCmfrmDestAddress.setText(dropLocationName);


                                                    AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 4;
                                                    layoutChanges();
                                                } catch (Exception e) {
                                                    Utilities.print("MyTest", "ARRIVED CATCH");
                                                    e.printStackTrace();
                                                }
                                                break;

                                            case "PICKEDUP":
                                                if ((confirmDialog != null) && (confirmDialog.isShowing()))
                                                    confirmDialog.dismiss();

                                                driveraccepted.setVisibility(View.GONE);
                                                driverArrived.setVisibility(View.GONE);
                                                driverPicked.setVisibility(View.VISIBLE);
                                                driverCompleted.setVisibility(View.GONE);
                                                txtdriverpicked.setText(getString(R.string.picked_up));
                                                imgarrived.setImageResource(R.drawable.arriveddisable);
                                                imgPicked.setImageResource(R.drawable.pickeddisable);
                                                imgDropped.setImageResource(R.drawable.complete);
                                                once = true;
                                                strTag = "ride_picked";
                                                try {
                                                    JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                    JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                    JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                    lblProvider.setText(provider.optString("first_name"));

                                                    if (provider.optString("avatar").startsWith("http"))
                                                        Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    else
                                                        Picasso.get().load(URLHelper.BASE + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                    lblServiceRequested.setText(service_type.optString("name"));
                                                    lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                    Picasso.get().load(URLHelper.BASE + service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                    ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));

    //                                                lblDis.setText(service_type.optString("distance")+" km");
    //                                                lblEta.setText(service_type.optString("minute")+" min");
                                                    lblApproxAmount.setText(  service_type.optString("fixed") + SharedHelper.getKey(TrackActivity.this, "currency"));
                                                    lblCmfrmSourceAddress.setText(pickUpLocationName);
                                                    lblCmfrmDestAddress.setText(dropLocationName);


                                                    imgSos.setVisibility(View.VISIBLE);
                                                    //imgShareRide.setVisibility(View.VISIBLE);

                                                    btnCancelTrip.setText(getString(R.string.share));
                                                    btnCancelTrip.setTextColor(getResources().getColor(R.color.quantum_googgreen));
                                                    AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                    flowValue = 4;
                                                    layoutChanges();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                break;

                                            case "DROPPED":
                                                driveraccepted.setVisibility(View.GONE);
                                                driverArrived.setVisibility(View.GONE);
                                                driverPicked.setVisibility(View.GONE);
                                                driverCompleted.setVisibility(View.VISIBLE);
                                                txtdrivercompleted.setText(getString(R.string.trip_completed));
                                                imgarrived.setImageResource(R.drawable.arriveddisable);
                                                imgPicked.setImageResource(R.drawable.pickeddisable);
                                                imgDropped.setImageResource(R.drawable.complete);
                                                once = true;
                                                strTag = "";
                                                imgSos.setVisibility(View.VISIBLE);
                                                lblPaymentType.setEnabled(false);
                                                //imgShareRide.setVisibility(View.VISIBLE);
                                                try {
                                                    JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                    if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                        JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                        isPaid = requestStatusCheckObject.optString("paid");
                                                        paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                        lblBasePrice.setText( payment.optString("fixed") + " "+ SharedHelper.getKey(context, "currency"));
                                                        lblTaxPrice.setText( payment.optString("tax") + "" + SharedHelper.getKey(context, "currency"));
                                                        lblDistancePrice.setText( payment.optString("distance") + " " + SharedHelper.getKey(context, "currency"));
                                                        if (payment.optString("discount") != null) {
                                                            promoLayout.setVisibility(View.VISIBLE);
                                                            txtDiscount.setText( payment.optString("discount") + "" + SharedHelper.getKey(context, "currency"));
                                                        }
                                                        //lblCommision.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("commision"));
                                                        lblTotalPrice.setText(payment.optString("total") + " " + SharedHelper.getKey(context, "currency"));
                                                        SharedHelper.putKey(TrackActivity.this, "total_price",
                                                                payment.optString("total"));
                                                    }
                                                    if (requestStatusCheckObject.optString("booking_id") != null &&
                                                            !requestStatusCheckObject.optString("booking_id").equalsIgnoreCase("")) {
                                                        booking_id.setText(requestStatusCheckObject.optString("booking_id"));
                                                        tvPaymentLabel.setText(SharedHelper.getKey(TrackActivity.this, "first_name").split(",")[0] + " owes");
                                                    } else {
                                                        booking_id.setVisibility(View.GONE);
                                                    }
                                                    if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        lblPaymentType.setEnabled(false);
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText(getString(R.string.cash));
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                            && wallet.equalsIgnoreCase("1")) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        lblPaymentType.setEnabled(false);
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText(getString(R.string.cash_wallet));
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")) {
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText(getString(R.string.card));
                                                        lblPaymentType.setEnabled(false);
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("PAYPAL")) {
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("PAYPAL");
                                                        lblPaymentType.setText("PAYPAL");
                                                        lblPaymentType.setEnabled(false);
                                                        txtpaiddriver.setVisibility(View.GONE);
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("RAZORPAY")) {
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText("RAZORPAY");
                                                        lblPaymentType.setText("RAZORPAY");
                                                        lblPaymentType.setEnabled(false);
                                                        txtpaiddriver.setVisibility(View.GONE);
                                                    } else if (isPaid.equalsIgnoreCase("1")) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        lblProviderName.setText(getString(R.string.rate_provider) + " " + provider.optString("first_name"));
                                                        if (provider.optString("avatar").startsWith("http"))
                                                            Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                        else
                                                            Picasso.get().load(URLHelper.BASE + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProvider);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                    }

                                                } catch (Exception e) {
                                                    Log.v("Dropped Error", e.getLocalizedMessage() + "  ");
                                                    e.printStackTrace();
                                                }
                                                if ((confirmDialog != null) && (confirmDialog.isShowing()))
                                                    confirmDialog.dismiss();
                                                break;

                                            case "COMPLETED":
                                                layoutdriverstatus.setVisibility(View.GONE);
    //                                            driveraccepted.setVisibility(View.GONE);
    //                                            driverArrived.setVisibility(View.GONE);
    //                                            driverPicked.setVisibility(View.GONE);
    //                                            driverCompleted.setVisibility(View.VISIBLE);
    //                                            txtdriveraccpted.setText(getString(R.string.arriving));
    //                                            imgarrived.setImageResource(R.drawable.arriveddisable);
    //                                            imgPicked.setImageResource(R.drawable.pickeddisable);
    //                                            imgDropped.setImageResource(R.drawable.complete);
                                                strTag = "";
                                                try {
                                                    if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                        JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                        lblBasePrice.setText( payment.optString("fixed") + "" + SharedHelper.getKey(context, "currency"));
                                                        lblTaxPrice.setText( payment.optString("tax") + "" + SharedHelper.getKey(context, "currency"));
                                                        lblDistancePrice.setText(payment.optString("distance")+ "" + SharedHelper.getKey(context, "currency"));
                                                        lblTotalPrice.setText(payment.optString("total") + " "+ SharedHelper.getKey(context, "currency"));
                                                    }
                                                    JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                    isPaid = requestStatusCheckObject.optString("paid");
                                                    paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                    imgSos.setVisibility(View.GONE);
                                                    //imgShareRide.setVisibility(View.GONE);
                                                    // lblCommision.setText(payment.optString("commision"));
                                                    if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")) {
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        btnPayNow.setVisibility(View.GONE);
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                        lblPaymentTypeInvoice.setText(getString(R.string.cash_wallet));
                                                    } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")) {
                                                        flowValue = 5;
                                                        layoutChanges();
                                                        imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        lblPaymentTypeInvoice.setText(getString(R.string.card));
                                                        btnPayNow.setVisibility(View.VISIBLE);
                                                    } else if (isPaid.equalsIgnoreCase("1")) {
                                                        btnPayNow.setVisibility(View.GONE);
                                                        lblProviderName.setText(getString(R.string.rate_provider) + " " + provider.optString("first_name"));
                                                        if (provider.optString("avatar").startsWith("http"))
                                                            Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                        else
                                                            Picasso.get().load(URLHelper.BASE + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.ic_dummy_user).into(imgProviderRate);
                                                        flowValue = 6;
                                                        layoutChanges();
                                                        //imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                        // lblPaymentTypeInvoice.setText("CARD");
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                if ((confirmDialog != null) && (confirmDialog.isShowing()))
                                                    confirmDialog.dismiss();
                                                break;
                                        }
                                    }

                                    if ("ACCEPTED".equals(status) || "STARTED".equals(status) ||
                                            "ARRIVED".equals(status) || "PICKEDUP".equals(status) || "DROPPED".equals(status)) {
    //                                    utils.print("Livenavigation", "" + status);
    //                                    utils.print("Destination Current Lat", "" + requestStatusCheckObject.getJSONObject("provider").optString("latitude"));
    //                                    utils.print("Destination Current Lng", "" + requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                                        liveNavigation(status, requestStatusCheckObject.getJSONObject("provider").optString("latitude"),
                                                requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (PreviousStatus.equalsIgnoreCase("SEARCHING")) {
                                SharedHelper.putKey(context, "current_status", "");
                                if (scheduledDate != null && scheduledTime != null && !scheduledDate.equalsIgnoreCase("")
                                        && !scheduledTime.equalsIgnoreCase("")) {
                                   /* Toast.makeText(context, getString(R.string.schdule_accept), Toast.LENGTH_SHORT).show();
                                    if (scheduleTrip){
                                        Intent intent = new Intent(activity,HistoryActivity.class);
                                        intent.putExtra("tag","upcoming");
                                        startActivity(intent);
                                    }*/
                                } else {
                                    Toast.makeText(context, getString(R.string.no_drivers_found), Toast.LENGTH_SHORT).show();
                                    strTag = "";
                                    PreviousStatus = "";
                                    flowValue = 0;
                                 /*
                                layoutChanges();
                                mMap.clear();
                                mapClear();*/
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    startActivity(intent);
                                }
                            } else if (PreviousStatus.equalsIgnoreCase("STARTED")) {
                                SharedHelper.putKey(context, "current_status", "");
                                Toast.makeText(context, getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                                strTag = "";
                                PreviousStatus = "";
                                Intent intent = new Intent(activity, MainActivity.class);
                                startActivity(intent);
                               /* flowValue = 0;
                                layoutChanges();
                                mMap.clear();
                                mapClear();*/
                            } else if (PreviousStatus.equalsIgnoreCase("ARRIVED")) {
                                SharedHelper.putKey(context, "current_status", "");
                                Toast.makeText(context, getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                                strTag = "";
                                PreviousStatus = "";
                               /* flowValue = 0;
                                layoutChanges();
                                mMap.clear();
                                mapClear();*/
                                Intent intent = new Intent(activity, MainActivity.class);
                                startActivity(intent);
                            } else if (PreviousStatus.equalsIgnoreCase("SEARCHING") && response.optJSONObject("data") != null
                                    && response.optJSONArray("data").length() > 0) {
                                Toast.makeText(context, getString(R.string.no_drivers_found), Toast.LENGTH_SHORT).show();
                                strTag = "";
                                PreviousStatus = "";
                                flowValue = 0;
                                 /*
                                layoutChanges();
                                mMap.clear();
                                mapClear();*/
                                Intent intent = new Intent(activity, MainActivity.class);
                                startActivity(intent);
                            }
                        }, error -> {
                            Utilities.print("Error", error.toString());
                            try {
                                if (customDialog != null && customDialog.isShowing()) {
                                    customDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            reqStatus = "";
                            SharedHelper.putKey(context, "req_status", "");
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        Utilities.print("Authorization", "" +
                                SharedHelper.getKey(context, "token_type")
                                + " " + SharedHelper.getKey(context, "access_token"));

                        headers.put("Authorization", "" +
                                SharedHelper.getKey(context, "token_type")
                                + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

                ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
                Log.i(TAG, "checkStatus api url : " + jsonObjectRequest.getUrl());

            } else {
                Log.e("nointerner", "nointernet");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.imgServiceRequested)
    public void serviceDetails() {
        if (lblModelNumber.getVisibility() == View.VISIBLE) {
            lblModelNumber.setVisibility(View.GONE);
        } else {
            lblModelNumber.setVisibility(View.VISIBLE);
        }
    }

    private void showStartedDialog(JSONObject requestStatusCheckObject) {

        confirmDialog = new Dialog(TrackActivity.this);
        confirmDialog.setContentView(R.layout.confirm_ride_dialog);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        confirmDialog.show();
        TextView tvCancel = confirmDialog.findViewById(R.id.tvCancel);
        TextView tvDone = confirmDialog.findViewById(R.id.tvDone);
        TextView tvDriverMsg = confirmDialog.findViewById(R.id.tvDriverMsg);
        tvDriverMsg.setText(getString(R.string.driver_will_pickup) + etaDur + " minutes.");

        tvCancel.setOnClickListener(v -> {
            confirmDialog.dismiss();
            cancelRequest();
        });

        tvDone.setOnClickListener(v -> {
            layoutdriverstatus.setVisibility(View.VISIBLE);
            driveraccepted.setVisibility(View.VISIBLE);
            driverArrived.setVisibility(View.GONE);
            driverPicked.setVisibility(View.GONE);
            driverCompleted.setVisibility(View.GONE);
            txtdriveraccpted.setText(getString(R.string.arriving));
            imgarrived.setImageResource(R.drawable.arriveddisable);
            imgPicked.setImageResource(R.drawable.pickeddisable);
            imgDropped.setImageResource(R.drawable.complete);
            try {
                JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                lblProvider.setText(provider.optString("first_name"));

                if (provider.optString("avatar").startsWith("http"))
                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                else
                    Picasso.get().load(URLHelper.BASE + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
                lblServiceRequested.setText(service_type.optString("name"));
                lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                Picasso.get().load(URLHelper.BASE + service_type.optString("image")).placeholder(R.drawable.car_select)
                        .error(R.drawable.car_select).into(imgServiceRequested);
                ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));

                lblApproxAmount.setText( service_type.optString("fixed") + SharedHelper.getKey(TrackActivity.this, "currency"));

                lblCmfrmSourceAddress.setText(pickUpLocationName);
                lblCmfrmDestAddress.setText(dropLocationName);

                AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                flowValue = 4;
                layoutChanges();
                if (!requestStatusCheckObject.optString("schedule_at").equalsIgnoreCase("null")) {
                    SharedHelper.putKey(context, "current_status", "");
                    Intent intent = new Intent(TrackActivity.this, HistoryActivity.class);
                    intent.putExtra("tag", "upcoming");
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            confirmDialog.dismiss();

        });
    }

    public void liveNavigation(String status, String lat, String lng) {
        Log.e("LiveNavigation", "ProLat" + lat + " ProLng" + lng);
        if (!lat.equalsIgnoreCase("") && !lng.equalsIgnoreCase("")) {
            Double proLat = Double.parseDouble(lat);
            Double proLng = Double.parseDouble(lng);

            Float rotation = 0.0f;

            MarkerOptions markerOptions = new MarkerOptions()
                    .anchor(0.5f, 0.75f)
                    .position(new LatLng(proLat, proLng))
                    .rotation(rotation)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider)
                    );

            if (providerMarker != null) {

                rotation = getBearing(providerMarker.getPosition(), markerOptions.getPosition());
                markerOptions.rotation(rotation * (180.0f / (float) Math.PI));
                providerMarker.remove();
                providerMarker = mMap.addMarker(markerOptions);
                String serviceRequsetName = SharedHelper.getKey(context, "service_type_Car_Ambulance");
                if (serviceRequsetName != "") {
                    if (serviceRequsetName.equalsIgnoreCase("CAR/TRANSFER") ||
                            serviceRequsetName.equalsIgnoreCase("AMBULANCE")) {
                        getDriverCarAmbETA();
                    } else {
                        getDriverETA();
                    }
                } else {
                    String serviceRequsetName1 = SharedHelper.getKey(context, "providerServiceType");
                    if (serviceRequsetName1 != "") {
                        if (serviceRequsetName1.equalsIgnoreCase("CAR") ||
                                serviceRequsetName1.equalsIgnoreCase("AMBULANCE")) {
                            getDriverCarAmbETA();
                        } else {
                            //   getDriverETA();
                        }
                    }
                }

            }


        }

    }

    // car and ambulance live driver ETA calculation
    public void getDriverCarAmbETA() {
        LatLng DriverLocation = providerMarker.getPosition();
        Double driver_lat = DriverLocation.latitude;
        Double driver_longi = DriverLocation.longitude;

        Log.e("TAG", "DRIVER LOCATION:" + driver_lat + "," + driver_longi);

        JSONObject object = new JSONObject();
        String constructedURL1 = URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                "?s_latitude=" + driver_lat
                + "&s_longitude=" + driver_longi
                + "&d_latitude=" + source_lat
                + "&d_longitude=" + source_lng
                + "&service_type=" + SharedHelper.getKey(context, "service_type");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, constructedURL1, object, response -> {
            //Log.e("TAG","Driver APi response"+response);
            if (response != null) {
                if (!response.optString("time").equalsIgnoreCase("")) {
                    ETA = response.optString("time");
                } else {
                    ETA = "--";
                }
                //Log.e(TAG,"ETA: "+ETA);


            }
        }, error -> displayMessage(getString(R.string.places_try_again))) {
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

    // caretaker driver live naivgation eta
    public void getDriverETA() {
        //Toast.makeText(getActivity(),providerMarker.getPosition().toString(),Toast.LENGTH_SHORT).show();
        LatLng DriverLocation = providerMarker.getPosition();
        Double driver_lat = DriverLocation.latitude;
        Double driver_longi = DriverLocation.longitude;

        Log.e("TAG", "DRIVER LOCATION:" + driver_lat + "," + driver_longi);

        JSONObject object = new JSONObject();
        String constructedURL1 = URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                "?s_latitude=" + driver_lat
                + "&s_longitude=" + driver_longi
                + "&d_latitude=" + source_lat
                + "&d_longitude=" + source_lng
                + "&service_type=" + SharedHelper.getKey(context, "service_type");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, constructedURL1, object, response -> {
            //Log.e("TAG","Driver APi response"+response);
            if (response != null) {
                if (!response.optString("time").equalsIgnoreCase("")) {
                    ETA = response.optString("time");
                } else {
                    ETA = "--";
                }

            }
        }, error -> {
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

    public float getBearing(LatLng oldPosition, LatLng newPosition) {
        double deltaLongitude = newPosition.longitude - oldPosition.longitude;
        double deltaLatitude = newPosition.latitude - oldPosition.latitude;
        double angle = (Math.PI * .5f) - Math.atan(deltaLatitude / deltaLongitude);

        if (deltaLongitude > 0) {
            return (float) angle;
        } else if (deltaLongitude < 0) {
            return (float) (angle + Math.PI);
        } else if (deltaLatitude < 0) {
            return (float) Math.PI;
        }

        return 0.0f;
    }

    public void setValuesForSourceAndDestination() {
        mapfocus.setVisibility(View.GONE);
        if (isInternet) {
            if (!source_lat.equalsIgnoreCase("")) {
                if (!source_address.equalsIgnoreCase("")) {
                    //  frmSource.setText(source_address);
                } else {
                    //  frmSource.setText(current_address);
                }
            } else {
                // frmSource.setText(current_address);
            }

            /***************************************CHANGES HERE TO HIDE SOURCE ADDRESS AND DESTINATION ADDRESS TEXTVIEW***********************************************/

            if (!dest_lat.equalsIgnoreCase("")) {
                // destination.setText(dest_address);
                // frmDestination.setVisibility(View.GONE);
                //  frmDest.setText(dest_address);

            }

            /***************************************CHANGES HERE TO HIDE SOURCE ADDRESS AND DESTINATION ADDRESS TEXTVIEW***********************************************/

            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("") && dest_lat != null && dest_lng != null) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }

            if (sourceLatLng != null && destLatLng != null) {
                Utilities.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
                // String url = getDirectionsUrl(sourceLatLng, destLatLng);
               /* DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);*/
                pickUpLocationName = source_address;

                String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.execute(url);
            }
        }
    }

    private void show(final View view) {
        mIsShowing = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .setDuration(500);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a show should hide the view
                mIsShowing = false;
                if (!mIsHiding) {
                    hide(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    private void hide(final View view) {
        mIsHiding = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Prevent drawing the View after it is gone
                mIsHiding = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a hide should show the view
                mIsHiding = false;
                if (!mIsShowing) {
                    show(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode + " result code " + resultCode + " ");
        // closed car and ambulance

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e("paymentExample", confirm.toJSONObject().toString(4));

                        // TODO: send 'confirm' to your server for verification.
                        // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                        // for more details.
                        paymentType = "PAYPAL";
                        paymentId = confirm.getProofOfPayment().getPaymentId();
                        payNowCard();
//                    JSONObject jsonObject=confirm.toJSONObject().getJSONObject("response");
//                            addPayment(jsonObject.getString("id"));
                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            }
        }
        if (requestCode == PAY_NOW_AUTO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                // Toast.makeText(activity, "card profile", Toast.LENGTH_SHORT).show();
                String result = data.getStringExtra("paymentSuccessful");

                // String serviceRequsetName1 = SharedHelper.getKey(context, "service_type_Car_Ambulance");

                //  else {

                //  sendRequest();
                //}
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
            if (requestCode == 5555) {
                if (resultCode == Activity.RESULT_OK) {
                    CardInfo cardInfo = data.getParcelableExtra("card_info");
                    getCardDetailsForPayment(cardInfo);
                }
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.v("PlaceResult", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress()
                        + ", " + place.getLatLng());

                changeLocation(String.valueOf(place.getAddress()), place.getLatLng().latitude, place.getLatLng().longitude);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


        if (requestCode == 5555) {
            if (resultCode == Activity.RESULT_OK) {
                CardInfo cardInfo = data.getParcelableExtra("card_info");
                getCardDetailsForPayment(cardInfo);
            }
        }
        if (requestCode == REQUEST_LOCATION) {

        } else {

        }
    }

    private void changeLocation(String address, double latitude, double longitude) {
        customDialog = new CustomDialog(TrackActivity.this);
        if (customDialog != null)
            customDialog.show();
        customDialog.setCancelable(false);
        String requestWith = "XMLHttpRequest";
        String auth = "Bearer " + SharedHelper.getKey(context, "access_token");
        ChangeDestRequest req = new ChangeDestRequest();
        req.setD_address(address);
        req.setD_latitude(latitude + "");
        req.setD_longitude(longitude + "");
        req.setStatus("CHANGE");
        req.setRequest_id(current_chat_requestID + "");

        changeLocationCall = restInterface.changeDestinationRequest(requestWith, auth, req);
        changeLocationCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                Log.v("change fare code", response.code() + " ");
                if (response.code() == 200) {
                    lblCmfrmDestAddress.setText(address);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                Log.v("change fare error", t.getLocalizedMessage() + " ");
            }
        });

    }

    private void getCardDetailsForPayment(CardInfo cardInfo) {

        ImageView imgPaymentType = findViewById(R.id.imgPaymentType);
        if (cardInfo.getLastFour().equals("CASH")) {
            SharedHelper.putKey(context, "payment_mode", "CASH");
            //  imgPaymentType.setImageResource(R.drawable.money1);
            lblPaymentType.setText(getString(R.string.cash));
            btnPayNow.setVisibility(View.GONE);
        } else {
//            SharedHelper.putKey(context, "card_id", cardInfo.getCardId());
//            SharedHelper.putKey(context, "payment_mode", "M-Pesa");
            SharedHelper.putKey(context, "payment_mode", "CARD");
            imgPaymentType.setImageResource(R.mipmap.ic_launcher);
            lblPaymentType.setText(cardInfo.getLastFour());
            btnPayNow.setVisibility(View.VISIBLE);
        }
    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

    private void trackPickToDest() throws Exception {

        GoogleDirection.withServerKey(getString(R.string.google_map_api))
                .from(new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng)))
                .to(new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng)))
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {

                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        try {
                            if (direction != null) {
                                if (direction.isOK()) {
                                    Log.v("rawBody", rawBody + "");
                                    Log.v("direction", direction + "");

                                    float totalDistance = 0;
                                    int totalDuration = 0;
                                    mMap.clear();
                                    Route route = direction.getRouteList().get(0);
                                    int legCount = route.getLegList().size();
                                    for (int index = 0; index < legCount; index++) {
                                        Leg leg = route.getLegList().get(index);
                                        try {
                                            totalDistance = totalDistance + Float.parseFloat(leg.getDistance().getText().replace("km", "").replace("m", "").trim());
                                        } catch (NumberFormatException ne) {
                                            ne.printStackTrace();
                                        }
//                                totalDistance =0;
                                        if (leg.getDuration().getText().contains("hour")) {
                                            Log.v("splithour", leg.getDuration().getText().split("hour")[0] + " ");
                                            totalDuration = totalDuration + 60 * Integer.parseInt(leg.getDuration().getText()
                                                    .split("hour")[0].trim());

                                        } else if (leg.getDuration().getText().contains("hours")) {
                                            totalDuration = totalDuration + 60 * Integer.parseInt(leg.getDuration().getText()
                                                    .split("hours")[0].trim().replace("m", ""));
                                        } else if (leg.getDuration().getText().contains("mins")) {
                                            totalDuration = totalDuration + Integer.parseInt(leg.getDuration().getText()
                                                    .replace("hour", "").replace("mins", "").replace("m", "").trim());
                                        } else {
                                            totalDuration = totalDuration + 0;
                                        }


                                        if (reqStatus.equals("PICKEDUP") || reqStatus.equals("DROPPED")) {
                                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.user_markers);
                                            Bitmap icon1 = BitmapFactory.decodeResource(getResources(), R.drawable.provider);
                                            mMap.addMarker(new MarkerOptions()
                                                    .icon(BitmapDescriptorFactory.fromBitmap(icon1))
                                                    .rotation(360)
                                                    .flat(true)
                                                    .anchor(0.5f, 0.5f)
                                                    .position(leg.getStartLocation().getCoordination()));
                                            if (index == legCount - 1) {
                                                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(leg.getEndLocation().getCoordination()));
                                            }
                                            List<Step> stepList = leg.getStepList();
                                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(TrackActivity.this, stepList, 3, Color.BLACK, 2, Color.GRAY);
                                            for (PolylineOptions polylineOption : polylineOptionList) {
                                                mMap.addPolyline(polylineOption);
                                            }
                                            if (pickUpLocationName != null) {
                                            }

                                            if (dest_address != null) {
                                                View marker_view2 = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(com.gsanthosh91.decoderoutekey.R.layout.custom_marker, null);
                                                TextView addressDes = marker_view2.findViewById(com.gsanthosh91.decoderoutekey.R.id.addressTxt);
                                                TextView etaTxt = marker_view2.findViewById(com.gsanthosh91.decoderoutekey.R.id.etaTxt);
                                                etaTxt.setVisibility(View.VISIBLE);
                                                addressDes.setText(dropLocationName);
                                                if (totalDuration > 60) {
                                                    etaTxt.setText(convertHours(totalDuration));
                                                } else {
                                                    etaTxt.setText(totalDuration + " mins");
                                                }
                                                etaDur = totalDuration + "";
                                                MarkerOptions marker_opt_des = new MarkerOptions().position(new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng)));
                                                marker_opt_des.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker_view2))).anchor(0.00f, 0.20f);
                                                destinationMarker = mMap.addMarker(marker_opt_des);
                                            }
                                        } else {
                                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.destination_marker);
                                            Bitmap icon1 = BitmapFactory.decodeResource(getResources(), R.drawable.user_markers);
                                            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon1)).position(leg.getStartLocation().getCoordination()));
                                            if (index == legCount - 1) {
                                                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(leg.getEndLocation().getCoordination()));
                                            }
                                            List<Step> stepList = leg.getStepList();
                                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(TrackActivity.this, stepList, 3, Color.BLACK, 2, Color.GRAY);
                                            for (PolylineOptions polylineOption : polylineOptionList) {
                                                mMap.addPolyline(polylineOption);
                                            }
                                            if (pickUpLocationName != null) {
                                            }

                                            if (dest_address != null) {
                                                View marker_view2 = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(com.gsanthosh91.decoderoutekey.R.layout.custom_marker, null);
                                                TextView addressDes = marker_view2.findViewById(com.gsanthosh91.decoderoutekey.R.id.addressTxt);
                                                TextView etaTxt = marker_view2.findViewById(com.gsanthosh91.decoderoutekey.R.id.etaTxt);
                                                etaTxt.setVisibility(View.VISIBLE);
                                                addressDes.setText(pickUpLocationName);
                                                if (totalDuration > 60) {
                                                    etaTxt.setText(convertHours(totalDuration));
                                                } else {
                                                    etaTxt.setText(totalDuration + " mins");
                                                }

                                                etaDur = totalDuration + "";
                                                MarkerOptions marker_opt_des = new MarkerOptions().position(new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng)));
                                                marker_opt_des.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker_view2))).anchor(0.00f, 0.20f);
                                                destinationMarker = mMap.addMarker(marker_opt_des);
                                            }
                                        }

                                    }

                                    mMap.setOnCameraIdleListener(() -> {
                                        if (sourceMarker != null) {
                                            String lat = String.valueOf(sourceLatLng.latitude);
                                            String lng = String.valueOf(sourceLatLng.longitude);
                                            if (((lat != null) && !lat.equals("") && !lat.isEmpty() && !lat.equalsIgnoreCase("0,0")) &&
                                                    ((lng != null) && !lng.equals("") && !lng.isEmpty() && !lng.equalsIgnoreCase("0,0"))) {
                                                Point PickupPoint = mMap.getProjection().toScreenLocation(new LatLng(sourceLatLng.latitude, sourceLatLng.longitude));
                                                sourceMarker.setAnchor(PickupPoint.x < dpToPx(context, 200) ? 0.00f : 1.00f, PickupPoint.y < dpToPx(context, 100) ? 0.20f : 1.20f);
                                            }

                                        }
                                        if (destinationMarker != null) {
                                            if (((dest_lat != null) && !dest_lat.equals("") && !dest_lat.isEmpty() && !dest_lat.equalsIgnoreCase("0,0")) &&
                                                    ((dest_lng != null) && !dest_lng.equals("") && !dest_lng.isEmpty() && !dest_lng.equalsIgnoreCase("0,0"))) {
                                                Point PickupPoint = mMap.getProjection().toScreenLocation(new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng)));
                                                destinationMarker.setAnchor(PickupPoint.x < dpToPx(context, 200) ? 0.00f : 1.00f, PickupPoint.y < dpToPx(context, 100) ? 0.20f : 1.20f);
                                            }
                                        }
                                    });
                                    lblCmfrmSourceAddress.setText(pickUpLocationName);
                                    lblDis.setText(totalDistance + " km");
                                    lblEta.setText(totalDuration + " min");
                                    setCameraWithCoordinationBounds(route);
                                }
                            }
                        } catch (NullPointerException ne) {
                            ne.printStackTrace();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                    }
                });

    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private int dpToPx(Context context, float dpValue) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dpValue * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public String convertHours(int runtime) {
        int hours = runtime / 60;
        int minutes = runtime % 60; // 5 in this case.
        return hours + "h " + minutes + " m";
    }

    private View getInfoWindow(String distance, String duration, boolean isMyLocation) {
        View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                this.findViewById(R.id.provider_map), false);
        TextView tv_desc = infoWindow.findViewById(R.id.tv_desc);
        LinearLayout info_window_time = infoWindow.findViewById(R.id.info_window_time);
        TextView tv_distance = infoWindow.findViewById(R.id.tv_distance);
        TextView tv_title = infoWindow.findViewById(R.id.tv_title);
        TextView tv_duration = infoWindow.findViewById(R.id.tv_duration);
        ImageView imgNavigate = infoWindow.findViewById(R.id.imgNavigate);


        if (isMyLocation) {
            info_window_time.setVisibility(View.VISIBLE);
            tv_title.setVisibility(View.VISIBLE);
            tv_duration.setText(duration);
            //   imageView.setImageResource(R.drawable.amu_bubble_mask);
            tv_distance.setText(distance);
            tv_title.setText(getString(R.string.my_location));
            tv_desc.setText(pickUpLocationName);
            tv_desc.setMaxLines(1);
        } else {
            tv_desc.setMaxLines(2);
            tv_desc.setText(dropLocationName);
            info_window_time.setVisibility(View.GONE);
            tv_title.setVisibility(View.GONE);
        }
        return infoWindow;
    }

    private void startAnim(ArrayList<LatLng> routeList) {
        if (mMap != null && routeList.size() > 1) {
            MapAnimator.getInstance().animateRoute(mMap, routeList);
        } else {
            Toast.makeText(context, "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    private void addIcon(View infoWindow, boolean isMyLocation,
                         LatLng pickUpCoordinates, LatLng dropCoordinates) {
        IconGenerator iconFactory = new IconGenerator(context);
        iconFactory.setContentView(infoWindow);
        iconFactory.setBackground(new RoundCornerDrawable());
        Bitmap icon = iconFactory.makeIcon();
        MarkerOptions markerOptions;
        boolean b = pickUpCoordinates.latitude > dropCoordinates.latitude &&
                pickUpCoordinates.longitude > dropCoordinates.longitude;
        if (isMyLocation) {
            markerOptions = new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(icon)).
                    position(pickUpCoordinates);
            Marker sourceMarker = mMap.addMarker(markerOptions);
            sourceMarker.setFlat(true);

            // sourceMarkerID = sourceMarker.getId();
            if (b) {
                sourceMarker.setAnchor(1.0f, 1.0f);
            } else {
                sourceMarker.setAnchor(0.0f, 0.0f);
            }
        } else {
            markerOptions = new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(icon)).
                    position(dropCoordinates);
            Marker destinationMarker = mMap.addMarker(markerOptions);
            destinationMarker.setFlat(true);
            // destinationMarkerID = destinationMarker.getId();
            if (b) {
                destinationMarker.setAnchor(0.0f, 0.0f);
            } else {
                destinationMarker.setAnchor(1.0f, 1.0f);
            }
        }

    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getUrl(double source_latitude, double source_longitude,
                          double dest_latitude, double dest_longitude) {

        // Origin of route
        String str_origin = "origin=" + source_latitude + "," + source_longitude;

        // Destination of route
        String str_dest = "destination=" + dest_latitude + "," + dest_longitude;


        // Sensor enabled
        String sensor = "sensor=false" + "&key=" + "AIzaSyCX6R-_OJ0vIApCQ-mFjVzd5Xn9h-xmlrI";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.e("url", url + "");
        return url;
    }

    public void payNowCard() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("total_payment", lblTotalPrice.getText().toString().replace("$", ""));
            if (paymentType.contains("PAYPAL") || paymentType.contains("RAZORPAY")) {
                object.put("payment_id", paymentId);
            }
//              object.put("payment_mode", SharedHelper.getKey(getApplicationContext(),"payment_mode"));
//              object.put("is_paid", isPaid);
            Log.d(TAG, "payNowCard: " + object.toString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAY_NOW_API, object, response -> {
            Log.d(TAG, "onResponse: " + response.toString());
            Utilities.print("PayNowRequestResponse", response.toString());
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            SharedHelper.putKey(context, "total_amount", "");
            flowValue = 6;
            layoutChanges();
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            displayMessage(getString(R.string.something_went_wrong));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };
        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);

            } catch (Exception e) {

            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {


        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                DataParser parser = new DataParser();


                // Starts parsing data
                routes = parser.parse(jObject);

            } catch (Exception e) {

                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            if (reqStatus != null && !reqStatus.equalsIgnoreCase("")) {
                try {
                    trackPickToDest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}




