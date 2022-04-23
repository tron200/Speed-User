package com.speed.user.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.fragments.UserMapFragment;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.utills.CustomTypefaceSpan;
import com.speed.user.utills.ResponseListener;
import com.speed.user.utills.Utilities;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity implements
        UserMapFragment.HomeFragmentListener,
        ResponseListener {


    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final int REQUEST_LOCATION = 1450;
    public Context context = MainActivity.this;
    public Activity activity = MainActivity.this;
    // index to identify current nav menu item
    public int navItemIndex = 0;
    public String CURRENT_TAG = TAG_HOME;
    CustomDialog customDialog;
    Utilities careUtilities = Utilities.getUtilityInstance();
    GoogleApiClient mGoogleApiClient;
    String keys = "ojBHda1ppwq9Fdc8lTJ507dNQkfBWAG1" + ":" + "Ixy9QVRAnoDrmT1I";
    FirebaseAnalytics firebaseAnalytics;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtWebsite;
    private TextView txtName;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private Handler mHandler;
    private String notificationMsg;
    private TextView legal_id;
    private TextView footer_item_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        if (SharedHelper.getKey(context, "login_by").equals("facebook"))
            FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String base64Key = Base64.encodeToString(keys.getBytes(), Base64.NO_WRAP);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null)
            notificationMsg = intent.getExtras().getString("Notification");

        firebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);

        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        legal_id = findViewById(R.id.legal_id);
        footer_item_version = findViewById(R.id.footer_item_version);
        footer_item_version.setText(careUtilities.getAppVersion(context));

        legal_id.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LegalActivity.class)));

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.usernameTxt);
        txtWebsite = navHeader.findViewById(R.id.status_txt);
        imgProfile = navHeader.findViewById(R.id.img_profile);

        navHeader.setOnClickListener(view -> startActivity(new Intent(activity, Profile.class)));

        getPaymetGetWayToken(base64Key);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }


    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        txtName.setText(SharedHelper.getKey(context, "first_name"));
        txtWebsite.setText("5");
        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("")
                && !SharedHelper.getKey(context, "picture")
                .equalsIgnoreCase(null) && SharedHelper.getKey(context, "picture") != null) {
            Picasso.get().load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(imgProfile);
        } else {
            Picasso.get().load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(imgProfile);
        }

    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        SharedHelper.putKey(context, "current_status", "");
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            return;
        }
        Runnable mPendingRunnable = () -> {
            Fragment fragment = getHomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();

    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                UserMapFragment userMapFragment = UserMapFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("Notification", notificationMsg);
                userMapFragment.setArguments(bundle);
                return userMapFragment;
            default:
                return new UserMapFragment();
        }

    }

    private void setUpNavigationView() {
        // This method will trigger on item Click of navigation menu
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                //Replacing the main content with ContentFragment Which is our Inbox View;
                //case R.id.nav_home:
                //navItemIndex = 0;
                //CURRENT_TAG = TAG_HOME;
                //break;
                case R.id.nav_payment:
                    drawer.closeDrawers();
                    startActivity(new Intent(MainActivity.this, Payment.class));
//                    finish();

                    break;
                case R.id.nav_home:
                    drawer.closeDrawers();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();

                    break;

                case R.id.nav_track:
                    drawer.closeDrawers();
                    //navigateToShareScreen(URLHelper.APP_URL);
                    startActivity(new Intent(MainActivity.this, RunningTrip.class));
                    break;
                case R.id.nav_profile:
                    drawer.closeDrawers();
                    startActivity(new Intent(MainActivity.this, EditProfile.class));

                    break;
                case R.id.nav_notification:
                    drawer.closeDrawers();
                    startActivity(new Intent(MainActivity.this, NotificationTab.class));

                    break;

                case R.id.nav_yourtrips:
                    drawer.closeDrawers();
                    SharedHelper.putKey(context, "current_status", "");
                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                    intent.putExtra("tag", "past");
                    startActivity(intent);
                    return true;
                // break;
                case R.id.nav_coupon:
                    drawer.closeDrawers();
                    SharedHelper.putKey(context, "current_status", "");
                    startActivity(new Intent(MainActivity.this, CouponActivity.class));
                    return true;
                case R.id.nav_wallet:
                    drawer.closeDrawers();
                    SharedHelper.putKey(context, "current_status", "");
                    startActivity(new Intent(MainActivity.this, ActivityWallet.class));
                    return true;
                case R.id.nav_help:
                    drawer.closeDrawers();
                    SharedHelper.putKey(context, "current_status", "");
                    startActivity(new Intent(MainActivity.this, ActivityHelp.class));
//                        finish();
                    break;
                case R.id.nav_sos:
                    drawer.closeDrawers();
                    startActivity(new Intent(MainActivity.this, SosCallActivity.class));
//                        finish();
                    break;
                case R.id.nav_share:
                    // launch new intent instead of loading fragment
                    //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                    navigateToShareScreen(URLHelper.APP_URL);
                    drawer.closeDrawers();
                    return true;
                case R.id.nav_logout:
                    drawer.closeDrawers();
                    // launch new intent instead of loading fragment
                    //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                    showLogoutDialog();
                    return true;


                case R.id.nav_media:
                    drawer.closeDrawers();
                    startActivity(new Intent(MainActivity.this, MediaHome.class));
                    break;
                default:
                    navItemIndex = 0;
            }
            loadHomeFragment();

            return true;
        });

        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem menuItem = m.getItem(i);
            applyFontToMenuItem(menuItem);

        }

        ActionBarDrawerToggle actionBarDrawerToggle = new
                ActionBarDrawerToggle(this, drawer, toolbar,
                        R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                    }
                };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                        if (status.isSuccess()) {
                            Log.d("MainAct", "Google User Logged out");
                           /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();*/
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("MAin", "Google API Client Connection Suspended");
            }
        });
    }

    public void logout() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(this, "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("MainActivity", "logout: " + object);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, response -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();


            if (SharedHelper.getKey(context, "login_by").equals("facebook"))
                LoginManager.getInstance().logOut();
            if (SharedHelper.getKey(context, "login_by").equals("google"))
                signOut();
            if (!SharedHelper.getKey(MainActivity.this, "account_kit_token").equalsIgnoreCase("")) {
                Log.e("MainActivity", "Account kit logout: " + SharedHelper.getKey(MainActivity.this, "account_kit_token"));

                SharedHelper.putKey(MainActivity.this, "account_kit_token", "");
            }
            SharedHelper.clearSharedPreferences(context);
            Intent goToLogin = new Intent(activity, Login.class);
            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goToLogin);
            finishAffinity();
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            displayMessage(getString(R.string.something_went_wrong));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                Log.e("headers: Token", headers + " ");

                return headers;
            }
        };
        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finishAffinity();
    }

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setTitle(context.getString(R.string.app_name))
                    .setIcon(R.drawable.ic_launcher_round)
                    .setMessage(getString(R.string.logout_alert));
            builder.setPositiveButton(R.string.yes, (dialog, which) -> logout());
            builder.setNegativeButton(R.string.no, (dialog, which) -> {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(arg -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            });
            dialog.show();
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/The-Sans-Plain.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {
//        finishAffinity();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notification, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " -via " + getString(R.string.app_name));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void getJSONArrayResult(String strTag, JSONArray arrayResponse) {

    }

    private void getPaymetGetWayToken(String baseKey) {
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                URLHelper.PAYMENT_TOKEN,
                jsonObject,
                response -> {
                    SharedHelper.putKey(MainActivity.this,
                            "paymentAccessToken",
                            response.optString("access_token"));
                    Log.e("paymentAccessToken", response.optString("access_token"));
                },
                error -> Log.e("errorPaymentToken", error + " ")) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                // headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Basic" + " " + baseKey);
                return headers;
            }
        };
        ClassLuxApp.getInstance().addToRequestQueue(objectRequest);
    }


}