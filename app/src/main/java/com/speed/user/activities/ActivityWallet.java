package com.speed.user.activities;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.speed.user.R;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.models.CardInfo;
import com.speed.user.utills.MyBoldTextView;
import com.speed.user.utills.Utilities;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import es.dmoral.toasty.Toasty;

public class ActivityWallet extends AppCompatActivity implements View.OnClickListener {

    private final int ADD_CARD_CODE = 435;
    Utilities utils = new Utilities();
    boolean loading;
    private Button add_fund_button;
    private ProgressDialog loadingDialog;
    private CardView wallet_card, add_money_card;
    private Button add_money_button;
    private EditText money_et;
    private MyBoldTextView balance_tv;
    private String session_token;
    private Button one, two, three;
    private double update_amount = 0;
    private ArrayList<CardInfo> cardInfoArrayList;
    private String currency = "";
    private CustomDialog customDialog;
    private Context context;
    private TextView currencySymbol;
    private ImageView backArrow;
    private CardInfo cardInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        backArrow = findViewById(R.id.backArrow);

        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            backArrow.setImageDrawable(getDrawable(R.drawable.ic_forward));

        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardInfoArrayList = new ArrayList<>();
        add_fund_button = findViewById(R.id.add_fund_button);
        wallet_card = findViewById(R.id.wallet_card);
        add_money_card = findViewById(R.id.add_money_card);
        balance_tv = findViewById(R.id.balance_tv);
        currencySymbol = findViewById(R.id.currencySymbol);
        context = this;
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);

        currencySymbol.setText(SharedHelper.getKey(context, "currency"));
        money_et = findViewById(R.id.money_et);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        one.setText( "199" + SharedHelper.getKey(context, "currency"));
        two.setText("599" + SharedHelper.getKey(context, "currency"));
        three.setText("1099" + SharedHelper.getKey(context, "currency") );
        backArrow.setOnClickListener(v -> onBackPressed());
        money_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.toString().length() == 0)
                    add_fund_button.setVisibility(View.GONE);
                else add_fund_button.setVisibility(View.VISIBLE);
                if (count == 1 || count == 0) {
                    one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                    two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                    three.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        add_fund_button.setOnClickListener(this);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Please wait...");

        session_token = SharedHelper.getKey(this, "access_token");

        wallet_card.setVisibility(View.VISIBLE);
        add_money_card.setVisibility(View.VISIBLE);

        getBalance();
        getCards(false);
    }

    private void getBalance() {
        if ((customDialog != null))
            customDialog.show();
        Ion.with(this)
                .load(URLHelper.getUserProfileUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    // response contains both the headers and the string result
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    if (e != null) {
                        if (e instanceof TimeoutException) {
                            displayMessage(getString(R.string.please_try_again));
                        }
                        if (e instanceof NetworkErrorException) {
                            getBalance();
                        }
                        return;
                    }
                    if (response != null) {
                        if (response.getHeaders().code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.getResult());
                                currency = jsonObject.optString("currency");
                                balance_tv.setText( jsonObject.optString("wallet_balance") + jsonObject.optString("currency"));
                                SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            if ((customDialog != null) && customDialog.isShowing())
                                customDialog.dismiss();
                            if (response.getHeaders().code() == 401) {

                            }
                        }
                    } else {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void getCards(final boolean showLoading) {
        loading = showLoading;
        if (loading) {
            if (customDialog != null)
                customDialog.show();
        }
        Ion.with(this)
                .load(URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    // response contains both the headers and the string result
                    if (response != null) {
                        if (showLoading) {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                        }
                        if (e != null) {
                            if (e instanceof TimeoutException) {
                                displayMessage(getString(R.string.please_try_again));
                            }
                            if (e instanceof NetworkErrorException) {
                                getCards(showLoading);
                            }
                            return;
                        }
                        if (response.getHeaders().code() == 200) {
                            try {
                                JSONArray jsonArray = new JSONArray(response.getResult());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject cardObj = jsonArray.getJSONObject(i);
                                    CardInfo cardInfo = new CardInfo();
                                    cardInfo.setCardId(cardObj.optString("card_id"));
                                    cardInfo.setCardType(cardObj.optString("brand"));
                                    cardInfo.setLastFour(cardObj.optString("last_four"));
                                    cardInfoArrayList.add(cardInfo);
                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            if (response.getHeaders().code() == 401) {
                            }
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fund_button:
                if (money_et.getText().toString().isEmpty()) {
                    update_amount = 0;
                    Toast.makeText(this, "Enter an amount greater than 0", Toast.LENGTH_SHORT).show();
                } else {
                    update_amount = Double.parseDouble(money_et.getText().toString());
                    //  payByPayPal(update_amount);
                    if (cardInfoArrayList.size() > 0) {
                        showChooser();
                    } else {
                        gotoAddCard();
                    }
                }
                break;

            case R.id.one:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                money_et.setText("199");
                break;
            case R.id.two:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                money_et.setText("599");
                break;
            case R.id.three:
                one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                money_et.setText("1099");
                break;
        }
    }

    private void gotoAddCard() {
        Intent mainIntent = new Intent(this, AddCard.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards(true);
                }
            }
        }
    }

    private void showChooser() {

        final String[] cardsList = new String[cardInfoArrayList.size()];

        for (int i = 0; i < cardInfoArrayList.size(); i++) {
            cardsList[i] = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(i).getLastFour();
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Add money using");
        builderSingle.setSingleChoiceItems(cardsList, 0, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.custom_tv);

        for (int j = 0; j < cardInfoArrayList.size(); j++) {
            String card = "";
            card = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(j).getLastFour();
            arrayAdapter.add(card);
        }

        builderSingle.setPositiveButton("Ok", (dialog, which) -> {
            dialog.dismiss();
            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
            Log.e("Items clicked===>", "" + selectedPosition);
            cardInfo = cardInfoArrayList.get(selectedPosition);
            addMoney(cardInfoArrayList.get(selectedPosition));
        });
        builderSingle.setNegativeButton(
                "cancel",
                (dialog, which) -> dialog.dismiss());
        builderSingle.show();
    }

    private void addMoney(final CardInfo cardInfo) {
        if (customDialog != null)
            customDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("card_id", cardInfo.getCardId());
        json.addProperty("amount", money_et.getText().toString());

        Ion.with(this)
                .load(URLHelper.addCardUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    // response contains both the headers and the string result
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    if (e != null) {
                        if (e instanceof TimeoutException) {
                            displayMessage(getString(R.string.please_try_again));
                        }
                        if (e instanceof NetworkErrorException) {
                            addMoney(cardInfo);
                        }
                        return;
                    }

                    if (response.getHeaders().code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.getResult());
                            Toast.makeText(ActivityWallet.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                            JSONObject userObj = jsonObject.getJSONObject("user");
                            balance_tv.setText(userObj.optString("wallet_balance") + currency );
                            SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                            money_et.setText("");
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        try {
                            if (response != null && response.getHeaders() != null) {
                                if (response.getHeaders().code() == 401) {
                                }
                            }
                        } catch (Exception exception) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

}
