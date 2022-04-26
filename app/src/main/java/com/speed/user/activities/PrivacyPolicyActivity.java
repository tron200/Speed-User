package com.speed.user.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.speed.user.R;
import com.speed.user.helper.SharedHelper;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        backArrow = findViewById(R.id.backArrow);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            backArrow.setImageDrawable(getDrawable(R.drawable.ic_forward));

        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }


        backArrow.setOnClickListener(view -> {
            //SharedHelper.putKey(getApplicationContext(), "password", "");
            Intent mainIntent = new Intent(getApplicationContext(), Login.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            // activity.finish();
        });

    }
}
