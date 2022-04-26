package com.speed.user.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import es.dmoral.toasty.Toasty;


public class NotificationTab extends AppCompatActivity {
    ImageView backArrow;
    RecyclerView recReview;
    LinearLayout layoutNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_tab);

        backArrow = findViewById(R.id.backArrow);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            backArrow.setImageDrawable(getDrawable(R.drawable.ic_forward));

        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }


        recReview = findViewById(R.id.recReview);
        layoutNotification = findViewById(R.id.layoutNotification);
        backArrow.setOnClickListener(v -> onBackPressed());
        getNotifications();

        recReview.setRecyclerListener(holder -> {
        });
    }

    private void getNotifications() {

        CustomDialog customDialog = new CustomDialog(NotificationTab.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.NOTIFICATION_URL, object, response -> {
            if (response != null) {
                Log.v("responseNoti", response + " ");
                PostAdapter postAdapter = null;
                try {
                    if (response.getJSONArray("Data") != null) {

                        postAdapter = new PostAdapter(response.getJSONArray("Data"));
                        //recReview.setHasFixedSize(true);
                        recReview.setLayoutManager(new LinearLayoutManager(NotificationTab.this) {
                            @Override
                            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                            }
                        });
                        if (postAdapter != null && postAdapter.getItemCount() > 0) {
                            layoutNotification.setVisibility(View.GONE);
                            recReview.setAdapter(postAdapter);
                        } else {
                            layoutNotification.setVisibility(View.VISIBLE);
                        }

                    }
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
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(NotificationTab.this, "access_token"));
                return headers;
            }
        };

        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        startActivity(new Intent(NotificationTab.this, MainActivity.class));
        finishAffinity();
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
                    .inflate(R.layout.notification_item, parent, false);
            return new PostAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PostAdapter.MyViewHolder holder, int position) {
            try {

                if (!jsonArray.optJSONObject(position).optString("notification_text").isEmpty()) {
                    holder.txtNotification.setText(jsonArray.optJSONObject(position).optString("notification_text"));
                } else {
                    holder.txtNotification.setText(getString(R.string.no_comment));
                }

                holder.txtDateTime.setText(jsonArray.optJSONObject(position).optString("expiration_date"));

                if (!jsonArray.optJSONObject(position).optString("expiration_date", "").isEmpty()) {
                    String form = jsonArray.optJSONObject(position).optString("expiration_date");

                }
                if (jsonArray.optJSONObject(position).optString("image").contains(".mp4")) {
                    holder.imgNoti.setVisibility(View.GONE);
                    holder.imgPdf.setVisibility(View.GONE);
                    holder.niceVideoPlayer.setVisibility(View.VISIBLE);
                    String videoUrl = URLHelper.BASE + "public/user/profile/" + jsonArray.optJSONObject(position).optString("image");
                    holder.niceVideoPlayer.setUp(
                            videoUrl,
                            jsonArray.optJSONObject(position).optString("title"), Jzvd.SCREEN_NORMAL);
                    Glide.with(NotificationTab.this)
                            .load(R.drawable.img_default)
                            .into(holder.niceVideoPlayer.thumbImageView);

                } else if (jsonArray.optJSONObject(position).optString("image").contains(".pdf")) {
                    holder.niceVideoPlayer.setVisibility(View.GONE);
                    holder.imgPdf.setVisibility(View.VISIBLE);
                    holder.imgNoti.setVisibility(View.GONE);
                    holder.imgPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String pdfLink = URLHelper.BASE + "public/user/profile/" + jsonArray.optJSONObject(position).optString("image");
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(pdfLink), "text/html");
                            startActivity(intent);
                        }
                    });

                } else if (!jsonArray.optJSONObject(position).isNull("image")
                        && jsonArray.optJSONObject(position).optString("image") != "") {
                    holder.imgNoti.setVisibility(View.VISIBLE);
                    holder.imgPdf.setVisibility(View.GONE);
                    holder.niceVideoPlayer.setVisibility(View.GONE);
                    Picasso.get()
                            .load(URLHelper.BASE + "public/user/profile/" + jsonArray.optJSONObject(position).optString("image"))
                            .into(holder.imgNoti);

                    holder.imgNoti.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(NotificationTab.this, FullImage.class);
                            intent.putExtra("title", jsonArray.optJSONObject(position).optString("title"));
                            intent.putExtra("url", URLHelper.BASE + "public/user/profile/" + jsonArray.optJSONObject(position).optString("image"));
                            startActivity(intent);
                        }
                    });
                } else {
                    holder.imgPdf.setVisibility(View.GONE);
                    holder.niceVideoPlayer.setVisibility(View.GONE);
                    holder.imgNoti.setVisibility(View.GONE);
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

            TextView txtNotification, txtDateTime;
            ImageView imgNoti, imgPdf;
            JzvdStd niceVideoPlayer;
//            ExoVideoView videoView;
//            SimpleExoPlayerView videoView;


            public MyViewHolder(View itemView) {
                super(itemView);
                niceVideoPlayer = itemView.findViewById(R.id.nice_video_player);
                txtNotification = itemView.findViewById(R.id.txtNotification);
                txtDateTime = itemView.findViewById(R.id.txtDateTime);
                imgNoti = itemView.findViewById(R.id.imgNoti);
                imgPdf = itemView.findViewById(R.id.imgPdf);

//                videoView = itemView.findViewById(R.id.videoView);

            }
        }
    }

}
