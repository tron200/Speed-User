package com.speed.user.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.utills.MyBoldTextView;
import com.speed.user.utills.MyTextView;
import com.speed.user.utills.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class RunningTrip extends AppCompatActivity {

    Utilities utils = new Utilities();
    private RecyclerView recyclerView;
    private RelativeLayout errorLayout;
    private ConnectionHelper helper;
    private boolean isInternet;
    private CustomDialog customDialog;
    private RunningTripAdapter runningTripAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setContentView(R.layout.activity_running_trip);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());

        findViewByIdAndInitialize();

        if (isInternet) {
            getRunningTripList();
        } else {
            displayMessage("No Internet Connection");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            //  onBackPressed();
            startActivity(new Intent(this, MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

    public void findViewByIdAndInitialize() {
        recyclerView = findViewById(R.id.recyclerView);
        errorLayout = findViewById(R.id.errorLayout);
        errorLayout.setVisibility(View.GONE);
        helper = new ConnectionHelper(RunningTrip.this);
        isInternet = helper.isConnectingToInternet();
    }

    private void getRunningTripList() {

        customDialog = new CustomDialog(RunningTrip.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JsonArrayRequest jsonArrayRequest = new
                JsonArrayRequest(URLHelper.CURRENT_TRIP,
                        response -> {
                            Utilities.print("getOnGoingTrip", response.toString());
                            if (response != null && response.length() > 0) {
                                runningTripAdapter = new RunningTripAdapter(response);
                                //  recyclerView.setHasFixedSize(true);
                                RecyclerView.LayoutManager mLayoutManager = new
                                        LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                if (runningTripAdapter != null && runningTripAdapter.getItemCount() > 0) {
                                    errorLayout.setVisibility(View.GONE);
                                    recyclerView.setAdapter(runningTripAdapter);
                                } else {
                                    errorLayout.setVisibility(View.VISIBLE);
                                }

                            } else {
                                recyclerView.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.VISIBLE);
                            }
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();

                        }, error -> {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                    displayMessage(getString(R.string.something_went_wrong));
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(RunningTrip.this,
                                "token_type") + " " + SharedHelper.getKey(RunningTrip.this,
                                "access_token"));
                        return headers;
                    }
                };

        ClassLuxApp.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(RunningTrip.this, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
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

    private class RunningTripAdapter extends RecyclerView.Adapter<RunningTripAdapter.MyViewHolder> {
        JSONArray jsonArray;

        public RunningTripAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        public void append(JSONArray array) {
            try {
                for (int i = 0; i < array.length(); i++) {
                    this.jsonArray.put(array.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public RunningTripAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_list_item, parent, false);
            return new RunningTripAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RunningTripAdapter.MyViewHolder holder, int position) {
            try {
                if (jsonArray.optJSONObject(position) != null) {
                    Picasso.get().load(jsonArray.optJSONObject(position).optString("static_map"))
                            .into(holder.tripImg);
                }

                if (jsonArray.optJSONObject(position).optJSONObject("payment") != null) {
                    holder.tripAmount.setText(SharedHelper.getKey(getApplicationContext(), "currency") + "" + jsonArray.optJSONObject(position).optJSONObject("payment").optString("total"));
                }
                if (jsonArray.optJSONObject(position).optString("booking_id") != null &&
                        !jsonArray.optJSONObject(position).optString("booking_id").equalsIgnoreCase("")) {
                    holder.booking_id.setText(getString(R.string.booking_id) + "" + jsonArray.optJSONObject(position).optString("booking_id"));
                }
                if (!jsonArray.optJSONObject(position).optString("assigned_at", "").isEmpty()) {
                    String form = jsonArray.optJSONObject(position).optString("assigned_at");
                    try {
                        holder.tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + " at " + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject serviceObj = jsonArray.getJSONObject(position).optJSONObject("service_type");
                if (serviceObj != null) {
                    holder.car_name.setText(serviceObj.optString("name"));
                    Picasso.get()
                            .load(URLHelper.BASE + serviceObj.optString("image"))
                            .into(holder.driver_image);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            MyTextView tripTime, car_name, booking_id;
            MyBoldTextView tripDate, tripAmount;
            ImageView tripImg, driver_image;

            public MyViewHolder(View itemView) {
                super(itemView);
                tripDate = itemView.findViewById(R.id.tripDate);
                tripTime = itemView.findViewById(R.id.tripTime);
                tripAmount = itemView.findViewById(R.id.tripAmount);
                tripImg = itemView.findViewById(R.id.tripImg);
                car_name = itemView.findViewById(R.id.car_name);
                booking_id = itemView.findViewById(R.id.booking_id);
                driver_image = itemView.findViewById(R.id.driver_image);

                itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(getApplicationContext(), TrackActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Log.e("Intent", "" + jsonArray.optJSONObject(getAdapterPosition()).toString());
                    intent.putExtra("post_value", jsonArray.optJSONObject(getAdapterPosition()).toString());
                    intent.putExtra("tag", "past_trips");
                    startActivity(intent);
                });

            }
        }
    }


}
