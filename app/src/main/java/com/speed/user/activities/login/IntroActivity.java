package com.speed.user.activities.login;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.speed.user.R;
import com.speed.user.activities.SplashScreen;
import com.speed.user.helper.SharedHelper;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    Button btnSubmit;
    boolean localeHasChanged = false;

    @BindView(R.id.rdEng)
    RadioButton rdEng;
    @BindView(R.id.rdArb)
    RadioButton rdArb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
        btnSubmit = findViewById(R.id.btnSubmit);
        radioGroup = findViewById(R.id.radioGroup);


        btnSubmit.setOnClickListener(v -> {
            if (SharedHelper.getKey(IntroActivity.this, "selectedlanguage") != null) {
                Intent refresh = new Intent(IntroActivity.this, SplashScreen.class);
                startActivity(refresh);
                finish();
            } else {
                Toast.makeText(IntroActivity.this, getString(R.string.choose_language), Toast.LENGTH_SHORT).show();
            }
        });
        setLanguage();
    }

    private void setLanguage() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdEng) {
                SharedHelper.putKey(IntroActivity.this, "selectedlanguage", "en");
                setLocale("en");
            }
            if (checkedId == R.id.rdArb) {
                SharedHelper.putKey(IntroActivity.this, "selectedlanguage", "ar");
                setLocale("ar");
            }
        });
    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        localeHasChanged = true;
        btnSubmit.setText(getString(R.string.submit));
        rdEng.setText(getString(R.string.english));
        rdArb.setText(R.string.arbic);
    }

    public void onResume() {
        super.onResume();
    }
}
