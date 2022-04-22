package com.speed.user.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.speed.user.R;
import com.speed.user.helper.SharedHelper;

public class SosCallActivity extends AppCompatActivity {

    ImageView ivBack, ivCall;
    TextView tvName, tvNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setContentView(R.layout.activity_sos_call);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String sosNumber = SharedHelper.getKey(this, "sos");
        String sosFirstName = SharedHelper.getKey(this, "first_name");
        String sosLastNmae = SharedHelper.getKey(this, "last_name");
        ivBack = findViewById(R.id.ivBack);
        ivCall = findViewById(R.id.ivCall);
        tvName = findViewById(R.id.tvName);
        tvNumber = findViewById(R.id.tvNumber);

        tvName.setText(sosFirstName + " " + sosLastNmae);
        tvNumber.setText(sosNumber);
        ivBack.setOnClickListener(view -> onBackPressed());
        ivCall.setOnClickListener(view -> {
            if (tvNumber.getText().toString() != null) {
                sosCall();
            }
        });
    }

    private void sosCall() {
        String number = tvNumber.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
        } else {
            Intent intentCall = new Intent(Intent.ACTION_DIAL);
            intentCall.setData(Uri.parse("tel:" + number));
            startActivity(intentCall);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
