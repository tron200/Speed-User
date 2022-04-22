package com.speed.user.activities;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.cardform.view.CardForm;
import com.speed.user.R;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.utills.MyButton;
import com.speed.user.utills.Utilities;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONObject;

import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class AddCard extends AppCompatActivity {

    static final Pattern CODE_PATTERN = Pattern
            .compile("([0-9]{0,4})|([0-9]{4}-)+|([0-9]{4}-[0-9]{0,4})+");
    Activity activity;
    Context context;
    ImageView backArrow, help_month_and_year, help_cvv;
    MyButton addCard;
    //EditText cardNumber, cvv, month_and_year;
    CardForm cardForm;
    String Card_Token = "";
    CustomDialog customDialog;
    Utilities utils = new Utilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setTheme(R.style.Mytheme);
        setContentView(R.layout.activity_add_card);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewByIdAndInitialize();

        backArrow.setOnClickListener(view -> onBackPressed());

        addCard.setOnClickListener(view -> {
            customDialog = new CustomDialog(AddCard.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            if (cardForm.getCardNumber() == null || cardForm.getExpirationMonth() == null || cardForm.getExpirationYear() == null || cardForm.getCvv() == null) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                displayMessage(getString(R.string.enter_card_details));
            } else {
                if (cardForm.getCardNumber().equals("") || cardForm.getExpirationMonth().equals("") || cardForm.getExpirationYear().equals("") || cardForm.getCvv().equals("")) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    displayMessage(getString(R.string.enter_card_details));
                } else {
                    String cardNumber = cardForm.getCardNumber();
                    int month = Integer.parseInt(cardForm.getExpirationMonth());
                    int year = Integer.parseInt(cardForm.getExpirationYear());
                    String cvv = cardForm.getCvv();
                    Utilities.print("MyTest", "CardDetails Number: " + cardNumber + "Month: " + month + " Year: " + year);


                    Card card = new Card(cardNumber, month, year, cvv);
                    Stripe stripe = new Stripe(AddCard.this,
                            URLHelper.STRIPE_TOKEN);
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    Utilities.print("CardToken:", " " + token.getId());
                                    Utilities.print("CardToken:", " " + token.getCard().getLast4());
                                    Card_Token = token.getId();

                                    addCardToAccount(Card_Token);
                                }

                                public void onError(Exception error) {
                                    // Show localized error message
                                    displayMessage(getString(R.string.enter_card_details));
                                    if ((customDialog != null) && (customDialog.isShowing()))
                                        customDialog.dismiss();
                                }
                            }
                    );
                }

            }
        });

    }


    public void findViewByIdAndInitialize() {
        backArrow = findViewById(R.id.backArrow);
        addCard = findViewById(R.id.addCard);
        context = AddCard.this;
        activity = AddCard.this;
        cardForm = findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel("Add CardDetails")
                .setup(AddCard.this);
    }

    public void addCardToAccount(final String cardToken) {
        JsonObject json = new JsonObject();
        json.addProperty("stripe_token", cardToken);

        Ion.with(this)
                .load(URLHelper.ADD_CARD_TO_ACCOUNT_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(AddCard.this, "token_type") + " " + SharedHelper.getKey(context, "access_token"))
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    Log.e("addcardexception", e + "");
                    Log.e("cardresponse", response + "");
                    // response contains both the headers and the string result
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();


                    if (e != null) {
                        if (e instanceof NetworkErrorException) {
                            displayMessage(getString(R.string.please_try_again));
                        }
                        if (e instanceof TimeoutException) {
                            addCardToAccount(cardToken);
                        }
                        return;
                    }

                    if (response != null) {
                        if (response.getHeaders().code() == 200) {
                            try {
                                Utilities.print("SendRequestResponse", response.toString());

                                JSONObject jsonObject = new JSONObject(response.getResult());
                                Toast.makeText(AddCard.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                                // onBackPressed();
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("isAdded", true);
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            customDialog.dismiss();
                        } else if (response.getHeaders().code() == 401) {
                            customDialog.dismiss();
                            GoToBeginActivity();
                        }
                    }
                });

    }


    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }


    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
