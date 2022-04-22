package com.speed.user.activities.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.speed.user.R;
import com.speed.user.helper.CustomDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.speed.user.helper.SharedHelper;

import java.util.Arrays;

public class FaceBookLoginActivity extends AppCompatActivity {

    CustomDialog customDialog;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setContentView(R.layout.activity_face_book_login);
        customDialog = new CustomDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFaceBookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                onBackPressed();
            }

            @Override
            public void onError(FacebookException error) {
                onBackPressed();
            }
        });

    }

    private void handleFaceBookAccessToken(AccessToken accessToken) {
        customDialog.show();
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userName = user.getDisplayName();
                        String userMail = user.getEmail();
                        String userId = user.getUid();
                        String userToken = accessToken.getToken();
                        Intent intent = new Intent();
                        intent.putExtra("userName", userName);
                        intent.putExtra("userMail", userMail);
                        intent.putExtra("userId", userId);
                        intent.putExtra("userToken", userToken);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        onBackPressed();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
