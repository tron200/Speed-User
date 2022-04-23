package com.speed.user.activities;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.speed.user.ClassLuxApp;
import com.speed.user.R;
import com.speed.user.consts.NewPaymentListAdapter;
import com.speed.user.helper.ConnectionHelper;
import com.speed.user.helper.CustomDialog;
import com.speed.user.helper.SharedHelper;
import com.speed.user.helper.URLHelper;
import com.speed.user.models.CardDetails;
import com.speed.user.models.CardInfo;
import com.speed.user.utills.Utilities;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import es.dmoral.toasty.Toasty;


public class Payment extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private final int ADD_CARD_CODE = 435;
    Activity activity;
    Context context;
    CustomDialog customDialog;
    ImageView backArrow;
    Button addCard;
    ListView payment_list_view;
    ArrayList<JSONObject> listItems;
    NewPaymentListAdapter paymentAdapter;
    TextView empty_text;
    Utilities utils = new Utilities();
    JSONObject deleteCard = new JSONObject();
    RadioButton chkPayPal, chkRazorPay;
    LinearLayout cashLayout, payPal_layout, wallet_layout, razor_layout;
    LinearLayout layoutStripe;
    //Internet
    ConnectionHelper helper;
    Boolean isInternet;
    TextView tvWalletAmt;
    TextView tvaddAmt;
    float walletamt = 0;
    private ArrayList<CardDetails> cardArrayList;

    public Payment() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }


        setContentView(R.layout.activity_payment);
        context = Payment.this;
        activity = Payment.this;
        findViewByIdAndInitialize();
        getCardList();
        backArrow.setOnClickListener(view -> finish());
        addCard.setOnClickListener(view -> GoToAddCard());
        cashLayout.setOnClickListener(view -> {
            SharedHelper.putKey(Payment.this, "selectedPaymentMode", "CASH");
            CardInfo cardInfo = new CardInfo();
            cardInfo.setLastFour("CASH");
            Intent intent = new Intent();
            intent.putExtra("card_info", cardInfo);
            setResult(RESULT_OK, intent);
            finish();
        });

        payment_list_view.setOnItemClickListener((parent, view, position, id) -> {
            try {
                JSONObject object = new JSONObject(listItems.get(position).toString());
                SharedHelper.putKey(Payment.this, "selectedPaymentMode", "STRIPE");
                CardInfo cardInfo = new CardInfo();
                cardInfo.setLastFour(object.optString("last_four"));
                cardInfo.setCardId(object.optString("card_id"));
                cardInfo.setCardType(object.optString("brand"));
                Intent intent = new Intent();
                intent.putExtra("card_info", cardInfo);
                setResult(RESULT_OK, intent);
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        payment_list_view.setOnItemLongClickListener((adapterView, view, i, l) -> {
            try {
                JSONObject object = new JSONObject(paymentAdapter.getItem(i).toString());
                Utilities.print("MyTest", "" + paymentAdapter.getItem(i));
                DeleteCardDailog(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public void callRadio(int pos) {
        try {


            JSONObject object = new JSONObject(listItems.get(pos).toString());

            SharedHelper.putKey(Payment.this, "selectedPaymentMode", "STRIPE");
            CardInfo cardInfo = new CardInfo();
            cardInfo.setLastFour(object.optString("last_four"));
            cardInfo.setCardId(object.optString("card_id"));
            cardInfo.setCardType(object.optString("brand"));
            Intent intent = new Intent();
            intent.putExtra("card_info", cardInfo);
            setResult(RESULT_OK, intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void DeleteCardDailog(final JSONObject object) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.are_you_sure))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    deleteCard = object;
                    deleteCard();
                })
                .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void deleteCard() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("card_id", deleteCard.optString("card_id"));
            object.put("_method", "DELETE");

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.DELETE_CARD_FROM_ACCOUNT_API, object, response -> {
            Utilities.print("SendRequestResponse", response.toString());
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            getCardList();
            deleteCard = new JSONObject();
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            displayMessage(getString(R.string.something_went_wrong));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void getCardList() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.CARD_PAYMENT_LIST, response -> {

            Utilities.print("GetPaymentList", response.toString());
            if (response != null && response.length() > 0) {
                listItems = getArrayListFromJSONArray(response);
                if (listItems.isEmpty()) {
                    //empty_text.setVisibility(View.VISIBLE);
                    payment_list_view.setVisibility(View.GONE);
                    layoutStripe.setVisibility(View.GONE);
                } else {
                    //empty_text.setVisibility(View.GONE);
                    payment_list_view.setVisibility(View.VISIBLE);
                    layoutStripe.setVisibility(View.VISIBLE);
                }
                cardArrayList = new ArrayList<>();
                for (JSONObject jsonObject : listItems) {
                    Gson gson = new Gson();
                    CardDetails card = gson.fromJson(jsonObject.toString(), CardDetails.class);
                    card.setSelected("false");
                    card.setSelected("true");

                    cardArrayList.add(card);
                }

                Log.v("cardArrayList", cardArrayList + "onResponse: " + cardArrayList.size() + "");

                paymentAdapter = new NewPaymentListAdapter(context, cardArrayList, activity);
                payment_list_view.setAdapter(paymentAdapter);

            } else {
                //empty_text.setVisibility(View.VISIBLE);
                layoutStripe.setVisibility(View.GONE);
                payment_list_view.setVisibility(View.GONE);
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
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        ClassLuxApp.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray) {

        ArrayList<JSONObject> aList = new ArrayList<JSONObject>();

        try {
            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    aList.add(jsonArray.getJSONObject(i));

                }

            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return aList;

    }

    public void findViewByIdAndInitialize() {
        tvWalletAmt = findViewById(R.id.tvWalletAmt);
        tvaddAmt = findViewById(R.id.tvaddAmt);
        layoutStripe = findViewById(R.id.layoutStripe);
        chkPayPal = findViewById(R.id.chkPayPal);
        chkRazorPay = findViewById(R.id.chkRazorPay);
        backArrow = findViewById(R.id.backArrow);
        addCard = findViewById(R.id.addCard);
        payment_list_view = findViewById(R.id.payment_list_view);
//        empty_text =  findViewById(R.id.empty_text);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        cashLayout = findViewById(R.id.cash_layout);
        payPal_layout = findViewById(R.id.payPal_layout);
        wallet_layout = findViewById(R.id.wallet_layout);
        razor_layout = findViewById(R.id.razor_layout);


        wallet_layout.setOnClickListener(v -> verifyWallet());
        payPal_layout.setOnClickListener(v -> {
            SharedHelper.putKey(Payment.this, "selectedPaymentMode", "PAYPAL");
            CardInfo cardInfo = new CardInfo();
            cardInfo.setLastFour("PAYPAL");
            Intent intent = new Intent();
            intent.putExtra("card_info", cardInfo);
            setResult(RESULT_OK, intent);
            finish();
        });

        razor_layout.setOnClickListener(v -> {
            SharedHelper.putKey(Payment.this, "selectedPaymentMode", "RAZORPAY");
            CardInfo cardInfo = new CardInfo();
            cardInfo.setLastFour("RAZORPAY");
            Intent intent = new Intent();
            intent.putExtra("card_info", cardInfo);
            setResult(RESULT_OK, intent);
            finish();
        });

        if (SharedHelper.getKey(Payment.this, "selectedPaymentMode")
                .equalsIgnoreCase("STRIPE")) {

        } else if (SharedHelper.getKey(Payment.this, "selectedPaymentMode")
                .equalsIgnoreCase("PAYPAL")) {


            chkPayPal.setChecked(true);
        } else if (SharedHelper.getKey(Payment.this, "selectedPaymentMode")
                .equalsIgnoreCase("RAZORPAY")) {


            chkRazorPay.setChecked(true);
        } else {

        }
        chkPayPal.setOnCheckedChangeListener(this);
        chkRazorPay.setOnCheckedChangeListener(this);
        getBalance();
        tvaddAmt.setOnClickListener(v -> startActivity(new Intent(Payment.this, ActivityWallet.class)));

    }

    private void verifyWallet() {
        if (walletamt > 0) {
            SharedHelper.putKey(Payment.this, "selectedPaymentMode", "Wallet");
            CardInfo cardInfo = new CardInfo();
            cardInfo.setLastFour("WALLET");
            Intent intent = new Intent();
            intent.putExtra("card_info", cardInfo);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBalance();
    }

    private void getBalance() {
        if ((customDialog != null))
            customDialog.show();
        Ion.with(this)
                .load(URLHelper.getUserProfileUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(Payment.this, "token_type") + " " + SharedHelper.getKey(this, "access_token"))
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
                                String currency = jsonObject.optString("currency");
                                walletamt = Float.parseFloat(jsonObject.optString("wallet_balance"));
                                tvWalletAmt.setText(getString(R.string.wallet_balance) + " : " +  jsonObject.optString("wallet_balance") + jsonObject.optString("currency"));
                                SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            if ((customDialog != null) && customDialog.isShowing())
                                customDialog.dismiss();
                            if (response.getHeaders().code() == 401) {
                               GoToBeginActivity();
                            }
                        }
                    } else {

                    }
                });
    }

    public void displayMessage(String toastString) {
        Toasty.info(this, toastString, Toasty.LENGTH_SHORT, true).show();
    }

    public void GoToBeginActivity() {
//        Intent mainIntent = new Intent(activity, LoginActivity.class);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(mainIntent);
//        activity.finish();
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToAddCard() {
        Intent mainIntent = new Intent(activity, AddCard.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCardList();
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.chkPayPal) {
            SharedHelper.putKey(Payment.this, "selectedPaymentMode", "PAYPAL");
            CardInfo cardInfo = new CardInfo();
            cardInfo.setLastFour("PAYPAL");
            Intent intent = new Intent();
            intent.putExtra("card_info", cardInfo);
            setResult(RESULT_OK, intent);
            finish();
        }
        if (buttonView.getId() == R.id.chkRazorPay) {
            SharedHelper.putKey(Payment.this, "selectedPaymentMode", "RAZORPAY");
            CardInfo cardInfo = new CardInfo();
            cardInfo.setLastFour("RAZORPAY");
            Intent intent = new Intent();
            intent.putExtra("card_info", cardInfo);
            setResult(RESULT_OK, intent);
            finish();
        }

    }
//    private final int ADD_CARD_CODE = 435;
//    Activity activity;
//    Context context;
//    CustomDialog customDialog;
//    ImageView backArrow;
//    ImageView addCard;
//    ListView payment_list_view;
//    ArrayList<JSONObject> listItems;
//    NewPaymentListAdapter paymentAdapter;
//    TextView empty_text;
//    Utilities utils = new Utilities();
//    JSONObject deleteCard = new JSONObject();
//    RadioButton chkcash,chkPayPal;
//    LinearLayout cashLayout,payPal_layout;
//    LinearLayout layoutcard;
//    //Internet
//    ConnectionHelper helper;
//    Boolean isInternet;
//
//    private ArrayList<CardDetails> cardArrayList;
//
//    public Payment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_payment);
//        context = Payment.this;
//        activity = Payment.this;
//        findViewByIdAndInitialize();
//        getCardList();
//        backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//        addCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GoToAddCard();
//            }
//        });
//        cashLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedHelper.putKey(Payment.this,"selectedPaymentMode","CASH");
//                CardInfo cardInfo=new CardInfo();
//                cardInfo.setLastFour("CASH");
//                Intent intent=new Intent();
//                intent.putExtra("card_info",cardInfo);
//                setResult(RESULT_OK,intent);
//                finish();
//            }
//        });
//
//        payment_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                try {
//                    JSONObject object = new JSONObject(listItems.get(position).toString());
//                    SharedHelper.putKey(Payment.this,"selectedPaymentMode","STRIPE");
//                    CardInfo cardInfo=new CardInfo();
//                    cardInfo.setLastFour(object.optString("last_four"));
//                    cardInfo.setCardId(object.optString("card_id"));
//                    cardInfo.setCardType(object.optString("brand"));
//                    Intent intent=new Intent();
//                    intent.putExtra("card_info",cardInfo);
//                    setResult(RESULT_OK,intent);
//                    finish();
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        payment_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                try {
//                    JSONObject object = new JSONObject(paymentAdapter.getItem(i).toString());
//                    utils.print("MyTest", "" + paymentAdapter.getItem(i));
//                    DeleteCardDailog(object);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return false;
//            }
//        });
//    }
//    public void callRadio(int pos){
//        try {
//
//
//            JSONObject object = new JSONObject(listItems.get(pos).toString());
//
//        SharedHelper.putKey(Payment.this,"selectedPaymentMode","STRIPE");
//        CardInfo cardInfo=new CardInfo();
//        cardInfo.setLastFour(object.optString("last_four"));
//        cardInfo.setCardId(object.optString("card_id"));
//        cardInfo.setCardType(object.optString("brand"));
//        Intent intent=new Intent();
//        intent.putExtra("card_info",cardInfo);
//        setResult(RESULT_OK,intent);
//        finish();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void DeleteCardDailog(final JSONObject object) {
//        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
//        builder.setMessage(getString(R.string.are_you_sure))
//                .setCancelable(false)
//                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        deleteCard = object;
//                        deleteCard();
//                    }
//                })
//                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    public void deleteCard() {
//        customDialog = new CustomDialog(context);
//        customDialog.setCancelable(false);
//        if (customDialog != null)
//            customDialog.show();
//        JSONObject object = new JSONObject();
//        try {
//            object.put("card_id", deleteCard.optString("card_id"));
//            object.put("_method", "DELETE");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.DELETE_CARD_FROM_ACCOUNT_API, object, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                utils.print("SendRequestResponse", response.toString());
//                if ((customDialog != null) && (customDialog.isShowing()))
//                    customDialog.dismiss();
//                getCardList();
//                deleteCard = new JSONObject();
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if ((customDialog != null) && (customDialog.isShowing()))
//                    customDialog.dismiss();
//                String json = null;
//                String Message;
//                NetworkResponse response = error.networkResponse;
//                if (response != null && response.data != null) {
//
//                    try {
//                        JSONObject errorObj = new JSONObject(new String(response.data));
//
//                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
//                            try {
//                                displayMessage(errorObj.getString("message"));
//                            } catch (Exception e) {
//                                displayMessage(errorObj.optString("error"));
//                                //displayMessage(getString(R.string.something_went_wrong));
//                            }
//                            utils.print("MyTest", "" + errorObj.toString());
//                        } else if (response.statusCode == 401) {
//                            refreshAccessToken("DELETE_CARD");
//                        } else if (response.statusCode == 422) {
//
//                            json = trimMessage(new String(response.data));
//                            if (json != "" && json != null) {
//                                displayMessage(json);
//                            } else {
//                                displayMessage(getString(R.string.please_try_again));
//                            }
//                        } else if (response.statusCode == 503) {
//                            displayMessage(getString(R.string.server_down));
//                        } else {
//                            displayMessage(getString(R.string.please_try_again));
//                        }
//
//                    } catch (Exception e) {
//                        displayMessage(getString(R.string.something_went_wrong));
//                    }
//
//                } else {
//                    if (error instanceof NoConnectionError) {
//                        displayMessage(getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof NetworkError) {
//                        displayMessage(getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof TimeoutError) {
//                        deleteCard();
//                    }
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
//                return headers;
//            }
//        };
//
//        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
//    }
//
//    public void getCardList() {
//
//        customDialog = new CustomDialog(context);
//        customDialog.setCancelable(false);
//        if (customDialog != null)
//            customDialog.show();
//
//        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.CARD_PAYMENT_LIST, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//
//                utils.print("GetPaymentList", response.toString());
//                if (response != null && response.length() > 0) {
//                    listItems = getArrayListFromJSONArray(response);
//                    if (listItems.isEmpty()) {
//                        //empty_text.setVisibility(View.VISIBLE);
//                        payment_list_view.setVisibility(View.GONE);
//                    } else {
//                        //empty_text.setVisibility(View.GONE);
//                        payment_list_view.setVisibility(View.VISIBLE);
//                    }
//
//                    for (JSONObject jsonObject : listItems) {
//                        Gson gson = new Gson();
//                        CardDetails card = gson.fromJson(jsonObject.toString(), CardDetails.class);
//                        card.setSelected("false");
//                        card.setSelected("true");
//                        cardArrayList = new ArrayList<>();
//                        cardArrayList.add(card);
//                    }
//
//                    Log.e("", "onResponse: " + cardArrayList.size()+"");
//                    paymentAdapter = new NewPaymentListAdapter(context, cardArrayList, activity);
//                    payment_list_view.setAdapter(paymentAdapter);
//                } else {
//                    //empty_text.setVisibility(View.VISIBLE);
//                    payment_list_view.setVisibility(View.GONE);
//                }
//                if ((customDialog != null) && (customDialog.isShowing()))
//                    customDialog.dismiss();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if ((customDialog != null) && (customDialog.isShowing()))
//                    customDialog.dismiss();
//                String json = null;
//                String Message;
//                NetworkResponse response = error.networkResponse;
//                if (response != null && response.data != null) {
//
//                    try {
//                        JSONObject errorObj = new JSONObject(new String(response.data));
//
//                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
//                            try {
//                                displayMessage(errorObj.optString("message"));
//                            } catch (Exception e) {
//                                displayMessage(getString(R.string.something_went_wrong));
//                            }
//
//                        } else if (response.statusCode == 401) {
//                            refreshAccessToken("PAYMENT_LIST");
//                        } else if (response.statusCode == 422) {
//
//                            json = trimMessage(new String(response.data));
//                            if (json != "" && json != null) {
//                                displayMessage(json);
//                            } else {
//                                displayMessage(getString(R.string.please_try_again));
//                            }
//                        } else if (response.statusCode == 503) {
//                            displayMessage(getString(R.string.server_down));
//
//                        } else {
//                            displayMessage(getString(R.string.please_try_again));
//                        }
//                    } catch (Exception e) {
//                        displayMessage(getString(R.string.something_went_wrong));
//                    }
//                } else {
//                    if (error instanceof NoConnectionError) {
//                        displayMessage(getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof NetworkError) {
//                        displayMessage(getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof TimeoutError) {
//                        getCardList();
//                    }
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
//                return headers;
//            }
//        };
//
//        ClassLuxApp.getInstance().addToRequestQueue(jsonArrayRequest);
//    }
//
//
//    private void refreshAccessToken(final String tag) {
//
//
//        JSONObject object = new JSONObject();
//        try {
//
//            object.put("grant_type", "refresh_token");
//            object.put("client_id", URLHelper.client_id);
//            object.put("client_secret", URLHelper.client_secret);
//            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
//            object.put("scope", "");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                utils.print("SignUpResponse", response.toString());
//                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
//                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
//                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
//                if (tag.equalsIgnoreCase("PAYMENT_LIST")) {
//                    getCardList();
//                } else {
//                    deleteCard();
//                }
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                String json = null;
//                String Message;
//                NetworkResponse response = error.networkResponse;
//
//                if (response != null && response.data != null) {
//                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
//                    GoToBeginActivity();
//                } else {
//                    if (error instanceof NoConnectionError) {
//                        displayMessage(getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof NetworkError) {
//                        displayMessage(getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof TimeoutError) {
//                        refreshAccessToken(tag);
//                    }
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                return headers;
//            }
//        };
//
//        ClassLuxApp.getInstance().addToRequestQueue(jsonObjectRequest);
//    }
//
//
//    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray) {
//
//        ArrayList<JSONObject> aList = new ArrayList<JSONObject>();
//
//        try {
//            if (jsonArray != null) {
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//
//                    aList.add(jsonArray.getJSONObject(i));
//
//                }
//
//            }
//        } catch (JSONException je) {
//            je.printStackTrace();
//        }
//
//        return aList;
//
//    }
//
//    public void findViewByIdAndInitialize() {
//        chkcash =findViewById(R.id.chkcash);
//        chkPayPal = findViewById(R.id.chkPayPal);
//        backArrow = (ImageView) findViewById(R.id.backArrow);
//        addCard =  findViewById(R.id.addCard);
//        payment_list_view = (ListView) findViewById(R.id.payment_list_view);
//        empty_text =  findViewById(R.id.empty_text);
//        helper = new ConnectionHelper(context);
//        isInternet = helper.isConnectingToInternet();
//        cashLayout = (LinearLayout) findViewById(R.id.cash_layout);
//        payPal_layout = findViewById(R.id.payPal_layout);
//        layoutcard =findViewById(R.id.layoutcard);
//
//        if (SharedHelper.getKey(Payment.this,"paypal").equalsIgnoreCase("0"))
//        {
//            payPal_layout.setVisibility(View.GONE);
//        }
//        else {
//            payPal_layout.setVisibility(View.VISIBLE);
//        }
//
//        if (SharedHelper.getKey(Payment.this,"cash").equalsIgnoreCase("0"))
//        {
//            cashLayout.setVisibility(View.GONE);
//        }
//        else {
//            cashLayout.setVisibility(View.VISIBLE);
//        }
//
//        if (SharedHelper.getKey(Payment.this,"card").equalsIgnoreCase("0"))
//        {
//            layoutcard.setVisibility(View.GONE);
//        }
//        else {
//            layoutcard.setVisibility(View.VISIBLE);
//        }
//
//        payPal_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedHelper.putKey(Payment.this,"selectedPaymentMode","PAYPAL");
//                CardInfo cardInfo=new CardInfo();
//                cardInfo.setLastFour("PAYPAL");
//                Intent intent=new Intent();
//                intent.putExtra("card_info",cardInfo);
//                setResult(RESULT_OK,intent);
//                finish();
//            }
//        });
//        if (SharedHelper.getKey(Payment.this,"selectedPaymentMode")
//                .equalsIgnoreCase("STRIPE"))
//        {
//            chkcash.setChecked(false);
//        }
//        else if (SharedHelper.getKey(Payment.this,"selectedPaymentMode")
//                .equalsIgnoreCase("PAYPAL"))
//        {
//
//            chkcash.setChecked(false);
//            chkPayPal.setChecked(true);
//        }
//        else {
//            chkcash.setChecked(true);
//        }
//
//
//        chkcash.setOnCheckedChangeListener(this);
//        chkPayPal.setOnCheckedChangeListener(this);
//
//    }
//
//    public void displayMessage(String toastString) {
//        try {
//            if (getCurrentFocus() != null)
//                Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void GoToBeginActivity() {
////        Intent mainIntent = new Intent(activity, LoginActivity.class);
////        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        startActivity(mainIntent);
////        activity.finish();
//    }
//
//    public void GoToMainActivity() {
//        Intent mainIntent = new Intent(activity, MainActivity.class);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(mainIntent);
//        activity.finish();
//    }
//
//    public void GoToAddCard() {
//        Intent mainIntent = new Intent(activity, AddCard.class);
//        startActivityForResult(mainIntent, ADD_CARD_CODE);
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ADD_CARD_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                boolean result = data.getBooleanExtra("isAdded", false);
//                if (result) {
//                    getCardList();
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//     if (buttonView.getId()==R.id.chkcash)
//        {
//            SharedHelper.putKey(Payment.this,"selectedPaymentMode","CASH");
//            CardInfo cardInfo=new CardInfo();
//            cardInfo.setLastFour("CASH");
//            Intent intent=new Intent();
//            intent.putExtra("card_info",cardInfo);
//            setResult(RESULT_OK,intent);
//            finish();
//        }
//        else if (buttonView.getId()==R.id.chkPayPal)
//        {
//            SharedHelper.putKey(Payment.this,"selectedPaymentMode","PAYPAL");
//            CardInfo cardInfo=new CardInfo();
//            cardInfo.setLastFour("PAYPAL");
//            Intent intent=new Intent();
//            intent.putExtra("card_info",cardInfo);
//            setResult(RESULT_OK,intent);
//            finish();
//        }
//
//    }
}
