package com.speed.user.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.speed.user.R;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.models.RestInterface;
import com.speed.user.models.ServiceGenerator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class PickUpNotes extends AppCompatActivity {

    Context context = PickUpNotes.this;

    ImageView back_icon;
    EditText etMsg;
    CardView btnSend;
    Button btnNotest;
    Call<ResponseBody> pickupNotesCall;
    RestInterface restInterface;
    String requestWith = "XMLHttpRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }


        setContentView(R.layout.activity_pick_up_notes);
        restInterface = ServiceGenerator.createService(RestInterface.class);
        back_icon = findViewById(R.id.imgBack);
        etMsg = findViewById(R.id.etMsg);
        btnSend = findViewById(R.id.btnSend);
        btnNotest = findViewById(R.id.btnNotest);
        btnNotest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotes(etMsg.getText().toString());
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotes(etMsg.getText().toString());
            }
        });


        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    private void sendNotes(String message) {
        CustomDialog customDialog = new CustomDialog(PickUpNotes.this);
        customDialog.show();
        String auth = "Bearer " + SharedHelper.getKey(context, "access_token");
        String requestId = getIntent().getStringExtra("request_id");

        pickupNotesCall = restInterface.addPickUpNotes(requestWith, auth, message, requestId);
        pickupNotesCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                customDialog.dismiss();
                if (response.code() == 200) {
                    finish();
                    Toast.makeText(PickUpNotes.this, "Pickup Notes sent to driver", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                customDialog.dismiss();
            }
        });

    }

}
