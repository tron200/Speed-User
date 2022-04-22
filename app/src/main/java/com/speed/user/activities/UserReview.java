package com.speed.user.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import es.dmoral.toasty.Toasty;


public class UserReview extends AppCompatActivity implements View.OnClickListener {
    ImageView backArrow;
    RecyclerView recReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setContentView(R.layout.activity_user_review);

        recReview = findViewById(R.id.recReview);

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(this);
        getReview();
    }

    private void getReview() {
        CustomDialog customDialog = new CustomDialog(UserReview.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("provider_id", SharedHelper.getKey(UserReview.this, "id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.GET_USERREVIEW, object, response -> {
            if (response != null) {
                PostAdapter postAdapter = null;
                try {
                    postAdapter = new PostAdapter(response.getJSONArray("Data"));
                    recReview.setLayoutManager(new LinearLayoutManager(UserReview.this) {
                        @Override
                        public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                            return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                        }
                    });
                    recReview.setAdapter(postAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            customDialog.dismiss();
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            displayMessage(getString(R.string.something_went_wrong));
        }) {
            @Override

            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(UserReview.this, "access_token"));
                return headers;
            }
        };
        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("dd MMM yyyy").format(cal.getTime());
        return yearName;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backArrow) {
            onBackPressed();
            finish();
        }
    }

    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
        JSONArray jsonArray;

        public PostAdapter(JSONArray array) {
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
        public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_item, parent, false);
            return new PostAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PostAdapter.MyViewHolder holder, int position) {
            try {

                if (!jsonArray.optJSONObject(position).optString("provider_comment").isEmpty()) {
                    holder.txtComment.setText(jsonArray.optJSONObject(position).optString("provider_comment"));
                } else {
                    holder.txtComment.setText(getString(R.string.no_comment));
                }
                holder.userRating.setRating(Float.parseFloat(jsonArray.optJSONObject(position).optString("user_rating")));

                if (!jsonArray.optJSONObject(position).optString("created_at", "").isEmpty()) {
                    String form = jsonArray.optJSONObject(position).optString("created_at");
                    try {
                        holder.txtDateTime.setText(getYear(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtComment, txtDateTime;
            RatingBar userRating;

            public MyViewHolder(View itemView) {
                super(itemView);

                txtComment = itemView.findViewById(R.id.txtComment);
                userRating = itemView.findViewById(R.id.userRating);
                txtDateTime = itemView.findViewById(R.id.txtDateTime);


            }
        }
    }
}
